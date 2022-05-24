package app.com.knowledge.power.views.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import app.com.knowledge.power.R
import app.com.knowledge.power.databinding.FragmentProfilePictureBinding
import app.com.knowledge.power.interfaces.NextFragmentCallback
import app.com.knowledge.power.models.User
import app.com.knowledge.power.utils.Commons
import app.com.knowledge.power.views.activities.CreateJoinGroupActivity
import com.google.android.gms.common.internal.service.Common
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


class ProfilePictureFragment(
    var callback: NextFragmentCallback,
    var user: User,
    var password: String
) :
    Fragment() {

    lateinit var binding: FragmentProfilePictureBinding
    var selectedImage: Bitmap? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_profile_picture, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivProfilePic.setOnClickListener {
            showOptionDialog()
        }

        binding.btnNext.setOnClickListener {
            if (selectedImage == null) {
                Commons.showAlertDialog(
                    "Error", "Please select a profile picture", activity as AppCompatActivity, null
                )
            } else {
                Commons.showProgress(activity as AppCompatActivity)
                val auth = FirebaseAuth.getInstance()
                auth.createUserWithEmailAndPassword(user.email, password)
                    .addOnCompleteListener(requireActivity()) {
                        if (it.isSuccessful) {
                            user.id = auth.currentUser?.uid.toString()
                            val ref = FirebaseStorage.getInstance().getReference("profilePictures")
                                .child(user.id)

                            val uploadTask = ref.putFile(
                                Commons.getFile(
                                    requireActivity(),
                                    user.id,
                                    selectedImage!!
                                )
                            )

                            uploadTask.continueWithTask { task ->
                                if (!task.isSuccessful) {
                                    Commons.hideProgress()
                                    task.exception?.let {
                                        Commons.showAlertDialog(
                                            "Error",
                                            it.localizedMessage?.toString(),
                                            activity as AppCompatActivity,
                                            null
                                        )
                                    }
                                }
                                ref.downloadUrl
                            }.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val downloadUri = task.result
                                    user.profilePicUrl = downloadUri.toString()

                                    val reference =
                                        FirebaseDatabase.getInstance().getReference("Users")
                                            .child(user.id)
                                    reference.setValue(user).addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            Commons.hideProgress()
                                            startActivity(
                                                Intent(
                                                    requireActivity(),
                                                    CreateJoinGroupActivity::class.java
                                                )
                                            )
                                        } else {
                                            Commons.hideProgress()
                                            Commons.showAlertDialog(
                                                "Error",
                                                it.exception?.localizedMessage.toString(),
                                                activity as AppCompatActivity,
                                                null
                                            )
                                        }
                                    }
                                } else {
                                    Commons.hideProgress()
                                    Commons.showAlertDialog(
                                        "Error",
                                        task.exception?.localizedMessage.toString(),
                                        activity as AppCompatActivity,
                                        null
                                    )
                                }
                            }
                        } else {
                            Commons.hideProgress()
                            Commons.showAlertDialog(
                                "Error", it.exception?.localizedMessage,
                                activity as AppCompatActivity?, null
                            )
                        }
                    }
            }
        }
    }

    private fun showOptionDialog() {
        val options = arrayOf("Camera", "Gallery")

        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Upload your picture")
        builder.setItems(options) { dialog, which ->
            when (options[which]) {
                "Camera" -> {
                    openCamera()
                }
                "Gallery" -> {
                    pickPictureFromGallery()
                }
            }
        }
        builder.show()
    }

    private fun openCamera() {
        if (checkSelfPermission(requireActivity(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraResultLauncher.launch(cameraIntent)
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun pickPictureFromGallery() {
        if (checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            galleryResultLauncher.launch(intent)
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

    private var storagePermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            result.entries.forEach {
                Log.d("StoragePerm", it.key + " = " + it.value)
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

    private var galleryResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data: Intent? = it.data

                val imageStream: InputStream? =
                    activity?.contentResolver?.openInputStream(data?.data as Uri)
                selectedImage = BitmapFactory.decodeStream(imageStream)

                binding.ivProfilePic.setImageBitmap(selectedImage)
            } else {
                Commons.showAlertDialog(
                    "Error",
                    "Unexpected Error Occurred!",
                    activity as AppCompatActivity,
                    null
                )
            }
        }
    private var cameraResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result != null) {
                    val data: Intent? = result.data

                    selectedImage = data?.extras?.get("data") as Bitmap?
                    binding.ivProfilePic.setImageBitmap(selectedImage)

                }
            } else {
                Commons.showAlertDialog(
                    "Error",
                    "Unexpected Error Occurred!",
                    activity as AppCompatActivity,
                    null
                )
            }
        }
}