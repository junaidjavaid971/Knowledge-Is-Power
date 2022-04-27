package app.com.knowledge.power.views.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import app.com.knowledge.power.R
import app.com.knowledge.power.databinding.FragmentMapBinding
import app.com.knowledge.power.utils.Commons
import app.com.knowledge.power.views.activities.CommentsActivity
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*


class MapFragment : Fragment(), GoogleMap.OnMapClickListener,
    GoogleMap.OnMarkerClickListener {
    var fusedLocationClient: FusedLocationProviderClient? = null
    var locationRequest: LocationRequest? = null
    var mapFragment: SupportMapFragment? = null
    var mMap: GoogleMap? = null
    lateinit var currentLoc: LatLng
    var myMarker: Marker? = null
    var zoomCompleted = false

    lateinit var binding: FragmentMapBinding

    private val locationLiveData: MutableLiveData<LatLng> = MutableLiveData()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?

        if (mapFragment != null) {
            mapFragment!!.getMapAsync(callback)
        }
        requestPermission()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        createLocationRequest()
    }

    private val callback = OnMapReadyCallback { googleMap: GoogleMap ->
        mMap = googleMap
        googleMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                requireActivity(),
                R.raw.mapstyle
            )
        )
        mMap?.setOnMapClickListener {
            startActivity(Intent(requireActivity(), CommentsActivity::class.java))
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
        locationLiveData.postValue(currentLoc)

        val icon = Commons.drawableToBitmap(
            ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.ic_dot
            )
        )
        val markerOptions = MarkerOptions().position(currentLoc).icon(
            BitmapDescriptorFactory.fromBitmap(icon)
        )

        if (myMarker != null) {
            myMarker?.remove()
        }
        myMarker = mMap?.addMarker(markerOptions)!!

        if (!zoomCompleted) {
            mMap?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    currentLoc,
                    15f
                )
            )
            zoomCompleted = true
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ),
            44
        )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 44) {
            Log.v("Permissions", "Permissions Granted")
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
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
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

    override fun onMapClick(p0: LatLng) {
        Commons.showToast(requireActivity(), p0.latitude.toString())
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        startActivity(Intent(requireActivity(), CommentsActivity::class.java))
        return true
    }
}