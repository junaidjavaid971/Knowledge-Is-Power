package app.com.knowledge.power.views.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import app.com.knowledge.power.MessageTypes
import app.com.knowledge.power.R
import app.com.knowledge.power.adapters.MessagesAdapter
import app.com.knowledge.power.databinding.ActivityCommentsBinding
import app.com.knowledge.power.models.Message
import app.com.knowledge.power.models.User
import app.com.knowledge.power.models.UserLocation
import app.com.knowledge.power.utils.Commons
import app.com.knowledge.power.utils.SharePrefData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.*


class CommentsActivity : AppCompatActivity(), MessagesAdapter.LongPressCallback {

    lateinit var binding: ActivityCommentsBinding
    lateinit var adapter: MessagesAdapter

    var locationId = ""
    var groupId = ""
    var locality = ""
    var isAdmin = false

    var user: User? = null
    var latlng: UserLocation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_comments)

        locationId = intent?.getStringExtra("locationId").toString()

        if (locationId.isEmpty()) {
            locationId = Commons.getRandomNumberString(10)
        }

        locality = intent?.getStringExtra("locality").toString()
        groupId = SharePrefData.getInstance().getPrefString(this@CommentsActivity, "defaultGroupId")

        val latitude = intent?.getDoubleExtra("latitude", 0.0)
        val longitude = intent?.getDoubleExtra("longitude", 0.0)
        isAdmin = intent?.getBooleanExtra("isAdmin", false)!!
        val colorCode = intent?.getIntExtra("colorCode", 0)

        latlng = UserLocation(latitude!!, longitude!!, colorCode!!)

        binding.tvPlaceName.text = locality

        getUser()
        manageClicks()
        getMessages()
    }

    private fun bindMessagesRV(messagesList: ArrayList<Message>) {
        adapter = MessagesAdapter(messagesList, this, this, isAdmin)

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        layoutManager.stackFromEnd = true

        binding.recyclerview.layoutManager = layoutManager
        binding.recyclerview.setHasFixedSize(true)
        binding.recyclerview.adapter = adapter
        binding.recyclerview.smoothScrollToPosition(messagesList.size)
    }

    private fun manageClicks() {
        binding.ivAttachment.setOnClickListener {
            if (binding.layoutAttachment.visibility == View.GONE) {
                binding.layoutAttachment.visibility = View.VISIBLE
            } else {
                binding.layoutAttachment.visibility = View.GONE
            }
        }

        binding.mesagesLayout.setOnClickListener {
            if (binding.layoutAttachment.visibility == View.VISIBLE) {
                binding.layoutAttachment.visibility = View.GONE
            }
        }

        binding.sendMessage.setOnClickListener {
            if (binding.message.text.isEmpty()) {
                return@setOnClickListener
            } else {
                sendMessage(MessageTypes.TEXT, binding.message.text.toString(), "")
            }
        }

        binding.layoutGallery.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this@CommentsActivity,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    this@CommentsActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                galleryResultLauncher.launch(intent)

                binding.layoutAttachment.visibility = View.GONE
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    storagePermissionsLauncher.launch(
                        arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    )
                } else {
                    storagePermissionsLauncher.launch(
                        arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    )
                }
            }
        }

        binding.layoutCamera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this@CommentsActivity, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
            ) {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                cameraResultLauncher.launch(cameraIntent)

                binding.layoutAttachment.visibility = View.GONE
            } else {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

        binding.headerLayout.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    private var storagePermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            result.entries.forEach {
                Log.d("StoragePerm", it.key + " = " + it.value)
            }
        }

    private fun sendMessage(messageType: MessageTypes, msg: String, imgUrl: String) {
        if (user == null) {
            Commons.showToast(
                this@CommentsActivity,
                "Cannot send message now, please try again later!"
            )
            return
        }
        val auth = FirebaseAuth.getInstance()
        val messageId = Commons.getRandomNumberString(10)
        val message = Message(
            user,
            messageId,
            msg,
            Commons.formattedDate,
            groupId,
            messageType,
            imgUrl,
            locationId,
            latlng
        )

        val reference =
            FirebaseDatabase.getInstance().getReference("Messages").child(groupId).child(locationId)
                .child(messageId)
        reference.setValue(message)

        val locationRef =
            FirebaseDatabase.getInstance().getReference("messageLocations").child(groupId)
                .child(locationId)
        locationRef.setValue(latlng)

        binding.message.text = null
    }

    private fun getMessages() {
        val reference =
            FirebaseDatabase.getInstance().getReference("Messages").child(groupId).child(locationId)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messagesList = ArrayList<Message>()
                for (postSnapshot in snapshot.children) {
                    val message = postSnapshot.getValue(Message::class.java)
                    messagesList.add(message!!)
                }

                messagesList.sortWith { o1, o2 -> o1.datetime.compareTo(o2.datetime) }

                bindMessagesRV(messagesList)
            }

            override fun onCancelled(error: DatabaseError) {
                Commons.addLog(error.message)
            }
        })
    }

    override fun onBackPressed() {
        if (binding.layoutAttachment.visibility == View.VISIBLE) {
            binding.layoutAttachment.visibility = View.GONE
        } else {
            super.onBackPressed()
        }
    }

    private var galleryResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data: Intent? = it.data

                val imageStream: InputStream? =
                    contentResolver?.openInputStream(data?.data as Uri)
                val selectedImage: Bitmap? = BitmapFactory.decodeStream(imageStream)

                val uri = getImageUri(this@CommentsActivity, selectedImage!!)
                val intent = Intent(
                    this@CommentsActivity,
                    ImageCaptionActivity::class.java
                ).putExtra("image", uri.toString())

                imageReceiveLauncher.launch(intent)
            }
        }
    private var cameraResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result != null) {
                    val data: Intent? = result.data

                    val selectedImage: Bitmap? = data?.extras?.get("data") as Bitmap?

                    val uri = getImageUri(this@CommentsActivity, selectedImage!!)
                    val intent = Intent(
                        this@CommentsActivity,
                        ImageCaptionActivity::class.java
                    ).putExtra("image", uri.toString())

                    imageReceiveLauncher.launch(intent)
                }
            }
        }

    private var cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result) {
                Log.d("CamerPerm", "Granted")
            } else {
                Log.d("CamerPerm", "Denied")
            }
        }

    private var imageReceiveLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                if (it.data != null) {
                    val data = it.data
                    val uri = data?.getStringExtra("uri")
                    val caption = data?.getStringExtra("caption")
                    val imageId = Commons.getRandomNumberString(10)

                    val ref = FirebaseStorage.getInstance().getReference("chatPictures")
                        .child(imageId)

                    val imageUri: Uri? = uri?.toUri()
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)

                    val uploadTask =
                        ref.putFile(Commons.getFile(this@CommentsActivity, imageId, bitmap!!))

                    Commons.showProgress(this@CommentsActivity)
                    uploadTask.continueWithTask { task ->
                        if (!task.isSuccessful) {
                            Commons.hideProgress()
                            task.exception?.let {
                                Commons.addLog(
                                    it.localizedMessage?.toString()
                                )
                            }
                        }
                        ref.downloadUrl
                    }.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Commons.hideProgress()
                            val downloadUri = task.result
                            val downloadUrl = downloadUri.toString()

                            sendMessage(MessageTypes.IMAGE, caption.toString(), downloadUrl)
                        } else {
                            Commons.hideProgress()
                        }
                    }
                }
            }
        }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.getContentResolver(),
            inImage,
            "Title",
            null
        )
        return Uri.parse(path)
    }

    private fun getUser() {
        Commons.showProgress(this@CommentsActivity)
        val reference = FirebaseDatabase.getInstance().getReference("Users")
            .child(FirebaseAuth.getInstance().currentUser?.uid.toString())

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Commons.hideProgress()
                user = snapshot.getValue(User::class.java)
            }

            override fun onCancelled(error: DatabaseError) {
                Commons.hideProgress()
                Commons.addLog(error.message)
            }

        })
    }

    override fun onEditClicked(message: Message?) {
        val dialogBuilder = AlertDialog.Builder(this@CommentsActivity)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_update_message, null)
        dialogBuilder.setView(dialogView)
        val edGroupName = dialogView?.findViewById<EditText>(R.id.edMessage)
        val btnUpdateName = dialogView?.findViewById<TextView>(R.id.btnOk)
        val btnUpdateCancel = dialogView?.findViewById<TextView>(R.id.btnCancel)

        edGroupName?.setText(message?.message)
        val alertDialog = dialogBuilder.create()

        btnUpdateCancel?.setOnClickListener {
            alertDialog.dismiss()
        }

        btnUpdateName?.setOnClickListener {
            if (edGroupName?.text.toString().isEmpty()) {
                Commons.showToast(this@CommentsActivity, "Cannot update empty message")
                return@setOnClickListener
            }

            val reference =
                FirebaseDatabase.getInstance().getReference("Messages").child(groupId)
                    .child(locationId)
                    .child(message?.messageId.toString()).child("message")
            reference.setValue(edGroupName?.text.toString())

            alertDialog.dismiss()
        }
        dialogBuilder.setNegativeButton("Cancel") { dialog, which -> dialog.dismiss() }
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
    }


    override fun onDeleteClicked(message: Message?) {
        Commons.showConfirmationDialog(
            "Delete Comment",
            "Are you sure you want to delete this comment?",
            this@CommentsActivity,
            object : Commons.DialogButtonsCallback {
                override fun onDialogPositiveClick() {
                    val reference =
                        FirebaseDatabase.getInstance().getReference("Messages").child(groupId)
                            .child(locationId)
                            .child(message?.messageId.toString())
                    reference.removeValue()

                }
            })
    }
}