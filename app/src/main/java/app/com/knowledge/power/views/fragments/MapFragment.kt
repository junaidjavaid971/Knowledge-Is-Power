package app.com.knowledge.power.views.fragments

import android.Manifest
import android.content.pm.PackageManager
import com.google.android.gms.maps.OnMapReadyCallback
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat.getCurrentLocation
import androidx.fragment.app.Fragment
import app.com.knowledge.power.R
import app.com.knowledge.power.utils.Commons
import com.google.android.gms.common.internal.service.Common
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

open class MapFragment : Fragment(), OnMapReadyCallback {
    var fusedLocationProviderClient: FusedLocationProviderClient? = null
    var mapFragment: SupportMapFragment? = null
    var mMap: GoogleMap? = null
    lateinit var currentLoc: LatLng
    var myMarker: Marker? = null
    var zoomCompleted = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?

        if (mapFragment != null) {
            mapFragment!!.getMapAsync(callback)
        }
        requestPermission()
    }

    private val callback = OnMapReadyCallback { googleMap: GoogleMap ->
        mMap = googleMap
        googleMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                requireActivity(),
                R.raw.mapstyle
            )
        )
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
        getCurrentLocation()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 44) {
            getCurrentLocation()
        }
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission()
            return
        }
        val mLocationRequest = LocationRequest()
        mLocationRequest.interval = 1000

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                p0 ?: return
                for (location in p0.locations) {
                    if (location == null) {
                        return
                    }
                    val currentLatLng = LatLng(location.latitude, location.longitude)

                    Log.d(
                        "onLocationChanged",
                        currentLatLng.latitude.toString() + " - " + currentLatLng.longitude
                    )
                    currentLoc = currentLatLng
                    mMap?.uiSettings?.isMyLocationButtonEnabled = true
                    mMap?.uiSettings?.isZoomControlsEnabled = true
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
                                currentLatLng,
                                15f
                            )
                        )
                        zoomCompleted = true
                    }
                }
            }
        }

        LocationServices.getFusedLocationProviderClient(requireActivity()).requestLocationUpdates(
            mLocationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    override fun onMapReady(map: GoogleMap) {
        this.mMap = map
        getCurrentLocation()
    }
}