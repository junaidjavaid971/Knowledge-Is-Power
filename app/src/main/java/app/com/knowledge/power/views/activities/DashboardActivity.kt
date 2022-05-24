package app.com.knowledge.power.views.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import app.com.knowledge.power.R
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.com.knowledge.power.adapters.GroupAdapter
import app.com.knowledge.power.adapters.MembersAdapter
import app.com.knowledge.power.databinding.ActivityDashboardBinding
import app.com.knowledge.power.models.Category
import app.com.knowledge.power.models.Group
import app.com.knowledge.power.models.MessageLocation
import app.com.knowledge.power.models.User
import app.com.knowledge.power.utils.Commons
import app.com.knowledge.power.utils.SharePrefData
import app.com.knowledge.power.views.BaseActivity
import app.com.knowledge.power.views.CategoriesBottomSheet
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*


class DashboardActivity : BaseActivity(), GroupAdapter.GroupCallback,
    GoogleMap.OnMarkerClickListener, MembersAdapter.MemberCallback {

    lateinit var binding: ActivityDashboardBinding
    var userGroupsList: ArrayList<Group> = ArrayList()
    var membersList: ArrayList<User> = ArrayList()
    var defaultGroupId = ""

    var alertDialog: AlertDialog? = null
    var currentlyOpenedGroup: Group = Group()

    var fusedLocationClient: FusedLocationProviderClient? = null
    var locationRequest: LocationRequest? = null
    var mMap: GoogleMap? = null

    var currentLoc: LatLng? = null

    var myMarker: Marker? = null
    val markersList: ArrayList<Marker> = ArrayList()
    val categoriesList = ArrayList<Category>()
    var zoomCompleted = false

    var isNormalMap = true

    var isAdmin = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        defaultGroupId =
            SharePrefData.getInstance().getPrefString(this@DashboardActivity, "defaultGroupId")


        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?

        if (mapFragment != null) {
            mapFragment.getMapAsync(callback)
        }
        requestPermission()
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this@DashboardActivity)
        createLocationRequest()

        getUserGroups()
        manageClicks()
        getCategories()
    }

    private fun manageClicks() {
        binding.ivGroups.setOnClickListener {
            val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
            val inflater = this.layoutInflater
            val dialogView: View = inflater.inflate(R.layout.group_dialog_row, null)
            dialogBuilder.setView(dialogView)

            val rvGroups: RecyclerView = dialogView.findViewById(R.id.rvGroups)
            val tvGroupName: TextView = dialogView.findViewById(R.id.groupName)
            val btnJoinGroup: TextView = dialogView.findViewById(R.id.btnJoinGroup)
            val btnCreateGroup: TextView = dialogView.findViewById(R.id.btnCreateGroup)

            btnJoinGroup.setOnClickListener {
                startActivity(Intent(this@DashboardActivity, JoinGroupActivity::class.java))
            }
            btnCreateGroup.setOnClickListener {
                startActivity(Intent(this@DashboardActivity, CreateGroupActivity::class.java))
            }
            val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            val memberAdapter = GroupAdapter(userGroupsList, this, this)
            rvGroups.layoutManager = linearLayoutManager
            rvGroups.adapter = memberAdapter
            tvGroupName.text = currentlyOpenedGroup.groupName

            alertDialog = dialogBuilder.create()
            alertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            alertDialog?.show()
        }

        binding.bottomSheet.layoutCreateCategories.setOnClickListener {
            startActivity(
                Intent(
                    this@DashboardActivity,
                    CategoriesActivity::class.java
                ).putExtra("groupId", defaultGroupId)
            )
        }

        binding.ivTarget.setOnClickListener {
            if (currentLoc != null) {
                mMap?.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(currentLoc!!, 15f)
                )
            }
        }

        binding.ivLayers.setOnClickListener {
            if (isNormalMap) {
                isNormalMap = false
                mMap?.mapType = GoogleMap.MAP_TYPE_SATELLITE
            } else {
                isNormalMap = true
                mMap?.mapType = GoogleMap.MAP_TYPE_NORMAL
            }
        }

        binding.tvSearchLocation.setOnClickListener {
            if (!Places.isInitialized()) {
                Places.initialize(applicationContext, getString(R.string.map_key), Locale.US);
            }

            val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this)
            autoCompleteLauncher.launch(intent)
        }

        binding.bottomSheet.layoutAddMember.setOnClickListener {
            startActivity(
                Intent(
                    this@DashboardActivity,
                    InvitationActivity::class.java
                ).putExtra("invitationCode", currentlyOpenedGroup.groupCode)
            )
        }

        binding.bottomSheet.ivEdit.setOnClickListener {
            changeGroupName()
        }

        binding.ivSettings.setOnClickListener {
            showLogoutDialog()
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        binding.bottomSheet.tvLeaveGroup.setOnClickListener {
            leaveGroup(userId)
        }
    }

    private fun leaveGroup(userId: String?) {
        Log.d("JDJD", "$defaultGroupId - $userId")
        for (group in userGroupsList) {
            if (group.id.equals(defaultGroupId) && group.adminId.equals(userId)) {
                Commons.showAlertDialog(
                    "Error",
                    getString(R.string.cannotLeaveGrp),
                    this@DashboardActivity,
                    null
                )
                return
            }
        }
        showGroupLeaveDialog()
    }

    private fun changeGroupName() {
        val dialogBuilder = android.app.AlertDialog.Builder(this@DashboardActivity)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_change_group_name, null)
        dialogBuilder.setView(dialogView)
        val edGroupName = dialogView?.findViewById<EditText>(R.id.edGroupName)
        val btnUpdateName = dialogView?.findViewById<TextView>(R.id.btnOk)
        val btnUpdateCancel = dialogView?.findViewById<TextView>(R.id.btnCancel)

        val alertDialog = dialogBuilder.create()

        btnUpdateCancel?.setOnClickListener {
            alertDialog.dismiss()
        }

        btnUpdateName?.setOnClickListener {
            if (edGroupName?.text.toString().isEmpty()) {
                Commons.showToast(this@DashboardActivity, "Group name cannot be empty")
                return@setOnClickListener
            }
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            val ref =
                FirebaseDatabase.getInstance().getReference("Groups").child(defaultGroupId)
                    .child("groupName")
            ref.setValue(edGroupName?.text.toString())

            val ugRef =
                FirebaseDatabase.getInstance().getReference("UserGroups").child(userId.toString())
                    .child(defaultGroupId)
                    .child("groupName")
            ugRef.setValue(edGroupName?.text.toString())
            alertDialog.dismiss()
        }
        dialogBuilder.setNegativeButton("Cancel") { dialog, which -> dialog.dismiss() }
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
    }

    private fun bindGroupMembersRv(membersList: ArrayList<User>, adminId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        Log.d("UserID", userId.toString())
        if (userId?.equals(adminId)!!) {
            isAdmin = true
            binding.bottomSheet.ivEdit.visibility = View.VISIBLE
            binding.bottomSheet.layoutCreateCategories.visibility = View.VISIBLE
        } else {
            isAdmin = false
            binding.bottomSheet.ivEdit.visibility = View.GONE
            binding.bottomSheet.layoutCreateCategories.visibility = View.GONE
        }
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val memberAdapter = MembersAdapter(membersList, this, this, adminId)
        binding.bottomSheet.rvGroupMembers.layoutManager = linearLayoutManager
        binding.bottomSheet.rvGroupMembers.adapter = memberAdapter
    }

    private fun getGroupMembers() {
        val membersRef = FirebaseDatabase.getInstance().getReference("Groups").child(defaultGroupId)

        membersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                membersList.clear()
                currentlyOpenedGroup = snapshot.getValue(Group::class.java)!!
                binding.bottomSheet.tvGroupName.text = currentlyOpenedGroup.groupName
                for (postSnapshot in snapshot.child("members").children) {
                    val member = postSnapshot.getValue(User::class.java)
                    membersList.add(member!!)
                }
                bindGroupMembersRv(membersList, currentlyOpenedGroup.adminId)

                getMessagesLocations()
            }

            override fun onCancelled(error: DatabaseError) {
                Commons.hideProgress()
                Commons.addLog(error.message)
            }

        })
    }

    private fun getMessagesLocations() {
        val reference =
            FirebaseDatabase.getInstance().getReference("messageLocations").child(defaultGroupId)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messageLocations = ArrayList<MessageLocation>()
                for (postSnapshot in snapshot.children) {
                    val latLng = LatLng(
                        postSnapshot.child("latitude").value.toString().toDouble(),
                        postSnapshot.child("longitude").value.toString().toDouble()
                    )
                    val colorCode = postSnapshot.child("colorCode").value.toString().toInt()
                    val messageLocation = MessageLocation(latLng, postSnapshot.key, colorCode)
                    messageLocations.add(messageLocation)
                    Commons.addLog("messageLocations: $latLng")
                }

                addMessageMarkers(messageLocations)
                Commons.hideProgress()
            }

            override fun onCancelled(error: DatabaseError) {
                Commons.addLog(error.message)
                Commons.hideProgress()
            }

        })
    }

    private val autoCompleteLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode.equals(RESULT_OK)) {
                if (it.data != null) {
                    val place = Autocomplete.getPlaceFromIntent(it.data!!)
                    val latLng = LatLng(place.latLng!!.latitude, place.latLng!!.longitude)
                    mMap?.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(latLng, 15f)
                    )
                }
            }
        }

    /*private fun setFragment() {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.flMap, mapsFragment)
        ft.commit()
    }*/

    private fun getUserGroups() {
        Commons.showProgress(this@DashboardActivity)
        val mAuth = FirebaseAuth.getInstance()
        val userRef = FirebaseDatabase.getInstance().getReference("UserGroups")
            .child(mAuth.currentUser?.uid.toString())

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userGroupsList.clear()
                for (dataSnapshot in snapshot.children) {
                    val group = dataSnapshot.getValue(Group::class.java)
                    userGroupsList.add(group!!)
                }

                if (userGroupsList.isNotEmpty()) {
                    if (defaultGroupId.isEmpty()) {
                        setGroupId(userGroupsList[0].id)
                    } else {
                        getGroupMembers()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Commons.hideProgress()
                Commons.addLog(error.message)
            }

        })
    }

    private fun setGroupId(groupId: String) {
        defaultGroupId = groupId
        SharePrefData.getInstance().setPrefString(this@DashboardActivity, "defaultGroupId", groupId)
        getGroupMembers()
    }

    override fun onGroupItemClicked(group: Group) {
        if (alertDialog != null) {
            if (alertDialog!!.isShowing) {
                alertDialog?.dismiss()
            }
        }
        setGroupId(group.id)
    }

    private fun showLogoutDialog() {
        val dialogBuilder = android.app.AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView =
            inflater.inflate(R.layout.logout_alert_dialog, null)
        dialogBuilder.setView(dialogView)
        val btnNo = dialogView.findViewById<TextView>(R.id.btnNo)
        val btnYes = dialogView.findViewById<TextView>(R.id.btn_yes)
        val alertDialog = dialogBuilder.create()
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
        btnNo.setOnClickListener { view: View? -> alertDialog.cancel() }
        btnYes.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(
                Intent(this@DashboardActivity, LoginSignupActivity::class.java).addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
            alertDialog.cancel()
        }
    }

    private fun showGroupLeaveDialog() {
        val dialogBuilder = android.app.AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView =
            inflater.inflate(R.layout.logout_alert_dialog, null)
        dialogBuilder.setView(dialogView)
        val btnNo = dialogView.findViewById<TextView>(R.id.btnNo)
        val btnYes = dialogView.findViewById<TextView>(R.id.btn_yes)
        val title = dialogView.findViewById<TextView>(R.id.tvDisclaimer)
        val desc = dialogView.findViewById<TextView>(R.id.tvDisclaimerDetails)
        val alertDialog = dialogBuilder.create()
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

        title.text = getString(R.string.leave_this_group)
        desc.text = getString(R.string.leaveGroupDesc)
        btnNo.setOnClickListener { view: View? -> alertDialog.cancel() }
        btnYes.setOnClickListener {
            val auth = FirebaseAuth.getInstance()

            val reference = FirebaseDatabase.getInstance().getReference("UserGroups")
                .child(auth.currentUser?.uid.toString()).child(defaultGroupId)
            reference.removeValue()

            val groupMemberRef =
                FirebaseDatabase.getInstance().getReference("Groups").child(defaultGroupId)
                    .child("members").child(auth.currentUser?.uid.toString())
            groupMemberRef.removeValue()

            SharePrefData.getInstance().deletePrefData(this, "defaultGroupId")
            defaultGroupId = ""

            alertDialog.cancel()

            startActivity(
                Intent(this@DashboardActivity, DashboardActivity::class.java).addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
        }
    }

    private val callback = OnMapReadyCallback { googleMap: GoogleMap ->
        mMap = googleMap
        googleMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                this@DashboardActivity,
                R.raw.mapstyle
            )
        )
        mMap?.setOnMapClickListener {
            CategoriesBottomSheet(
                "",
                it.latitude,
                it.longitude,
                categoriesList,
                isAdmin
            ).show(supportFragmentManager, "")
        }
        mMap!!.setOnMarkerClickListener(this)
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            for (location in p0.locations) {
                if (location == null) return

                setCurrentLocationMarker(location.latitude, location.longitude)
            }
        }
    }


    fun setCurrentLocationMarker(latitude: Double, longitude: Double) {
        currentLoc = LatLng(latitude, longitude)

        val icon = Commons.drawableToBitmap(
            ContextCompat.getDrawable(
                this@DashboardActivity,
                R.drawable.ic_dot
            )!!
        )
        val markerOptions = MarkerOptions().position(currentLoc!!).icon(
            BitmapDescriptorFactory.fromBitmap(icon!!)
        )

        if (myMarker != null) {
            myMarker?.remove()
        }
        myMarker = mMap?.addMarker(markerOptions)!!
        myMarker?.tag = "myMarker"

        if (!zoomCompleted) {
            mMap?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    currentLoc!!,
                    15f
                )
            )
            zoomCompleted = true
        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissionRequestLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            )
        } else {
            permissionRequestLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private val permissionRequestLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            it.entries.forEach {
                Log.e("PermissionsResult", "${it.key} = ${it.value}")
            }
        }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient?.removeLocationUpdates(locationCallback)
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this@DashboardActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this@DashboardActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        locationRequest?.let {
            fusedLocationClient?.requestLocationUpdates(
                it,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        if (marker.tag == null || marker.tag.toString().isEmpty()) {
            CategoriesBottomSheet(
                "",
                marker.position.latitude,
                marker.position.longitude,
                categoriesList,
                isAdmin
            ).show(supportFragmentManager, "")
        } else if (marker.tag.toString() == "myMarker") {
            return false
        } else {
            startActivity(
                Intent(this@DashboardActivity, CommentsActivity::class.java).putExtra(
                    "locationId",
                    marker.tag.toString(),
                ).putExtra("latitude", marker.position.latitude)
                    .putExtra("longitude", marker.position.longitude)
                    .putExtra("isAdmin", isAdmin)
                    .putExtra("colorCode", marker.snippet?.toInt())
            )

        }
        return true
    }

    fun addMessageMarkers(locations: ArrayList<MessageLocation>) {
        if (markersList.isNotEmpty()) {
            for (marker in markersList) {
                marker.remove()
            }
        }
        if (locations.isEmpty()) {
            return
        }
        for (location in locations) {
            Commons.addLog("pinColor: " + location.colorCode)

            val unwrappedDrawable =
                AppCompatResources.getDrawable(this@DashboardActivity, R.drawable.ic_pin)

            val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable!!)
            DrawableCompat.setTint(wrappedDrawable, location.colorCode)

            val markerOptions = MarkerOptions().position(location.latLng).icon(
                BitmapDescriptorFactory.fromBitmap(
                    Commons.drawableToBitmap(wrappedDrawable)!!
                )
            )

            val locationMarker = mMap?.addMarker(markerOptions)
            locationMarker?.tag = location.locationId
            locationMarker?.snippet = location.colorCode.toString()

            markersList.add(locationMarker!!)
        }
    }

    override fun onMemberClicked(user: User) {
        val options = arrayOf("Remove Member", "Make Admin")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select an Option")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> removeMemberFromGroup(user)
                1 -> makeAdmin(user)
            }
        }
        builder.show()
    }

    private fun removeMemberFromGroup(user: User) {
        Commons.showConfirmationDialog(
            "Remove this member?",
            "Are you sure you want to remove this member?",
            this@DashboardActivity,
            object : Commons.DialogButtonsCallback {
                override fun onDialogPositiveClick() {
                    val ref =
                        FirebaseDatabase.getInstance().getReference("Groups").child(defaultGroupId)
                            .child("members").child(user.id)

                    ref.removeValue()

                    val ugRef =
                        FirebaseDatabase.getInstance().getReference("UserGroups")
                            .child(user.id).child(defaultGroupId)

                    ugRef.removeValue()
                }
            })
    }

    private fun makeAdmin(user: User) {
        Commons.showConfirmationDialog(
            "Change Admin?",
            "Are you sure you want to make this member as admin? Doing so will remove you from admin.",
            this@DashboardActivity,
            object : Commons.DialogButtonsCallback {
                override fun onDialogPositiveClick() {
                    val ref =
                        FirebaseDatabase.getInstance().getReference("Groups").child(defaultGroupId)
                            .child("adminId")

                    ref.setValue(user.id)

                    val ugRef =
                        FirebaseDatabase.getInstance().getReference("UserGroups")
                            .child(defaultGroupId)
                            .child("adminId")

                    ugRef.setValue(user.id)
                }
            })
    }


    private fun getCategories() {
        val ref = FirebaseDatabase.getInstance().getReference("Categories").child(defaultGroupId)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categoriesList.clear()
                for (dataSnapshot in snapshot.children) {
                    val category = dataSnapshot.getValue(Category::class.java)
                    if (category != null) {
                        categoriesList.add(category)
                    }
                }
                val linearLayoutManager = LinearLayoutManager(
                    this@DashboardActivity,
                    LinearLayoutManager.VERTICAL,
                    false
                )
            }

            override fun onCancelled(error: DatabaseError) {
                Commons.hideProgress()
                Commons.addLog(error.message)
            }

        })
    }
}