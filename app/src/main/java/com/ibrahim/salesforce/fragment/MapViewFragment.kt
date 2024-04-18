package com.ibrahim.salesforce.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.ibrahim.salesforce.R
import com.ibrahim.salesforce.activity.DashboardActivity


class MapViewFragment : androidx.fragment.app.Fragment(), OnMapReadyCallback {

    private lateinit var v: View
    private var googleMap: GoogleMap? = null

    companion object {
        fun newInstance(): MapViewFragment {
            return MapViewFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_map_view, container, false)
        val mapFragment = childFragmentManager?.findFragmentById(R.id.map1) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        return v
    }

    override fun onMapReady(p0: GoogleMap) {
//        TODO("Not yet implemented")
        googleMap = p0
        val latLng = LatLng(DashboardActivity.mLocationService.latitude, DashboardActivity.mLocationService.longitude)
        val markerOptions: MarkerOptions = MarkerOptions().position(latLng).title("You are Here!")
        val zoomLevel = 17.0f
        googleMap?.addMarker(markerOptions)
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel))

    }

}