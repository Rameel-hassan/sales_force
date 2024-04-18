package com.ibrahim.salesforce.activity

/**
 * @author Rameel Hassan
 * Created 20/07/2022 at 8:43 pm
 */

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.ibrahim.salesforce.R
import com.ibrahim.salesforce.databinding.ActivityRoutesMapsBinding
import com.ibrahim.salesforce.network.RestService
import com.ibrahim.salesforce.response.GetServerResponse
import com.ibrahim.salesforce.response.LocPointsData
import com.ibrahim.salesforce.response.RouteDetails
import com.ibrahim.salesforce.response.SalesOfficer
import com.ibrahim.salesforce.utilities.AppKeys
import io.paperdb.Paper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class MapsRoutesActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var spnEmployee : Spinner
    private lateinit var totalKmTextView : TextView

    private lateinit var routeDetails: RouteDetails
    private lateinit var mArrEmployee: List<SalesOfficer>
    private lateinit var adapter: ArrayAdapter<SalesOfficer>
    private lateinit var mLoginResponse: GetServerResponse
    private lateinit var mDialog: ProgressDialog
    private lateinit var disposable: Disposable
    lateinit var mContext: Context
    private var date: String = ""
    private var empPosition: Int = 0
    private var totalKm: String = ""
    private val restService by lazy { RestService.create() }
    lateinit var binding:ActivityRoutesMapsBinding;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoutesMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = getString(R.string.map_routes)
        Paper.init(this)
//        routeDetails = RouteDetails();
        spnEmployee = binding.spnEmployee
        totalKmTextView = binding.totalKmTv
        Paper.init(this)
        mLoginResponse = Paper.book().read<GetServerResponse>(AppKeys.KEY_LOGIN_RESPONSE)
        // the SupportMapFragment and get notified when the map is ready to be used.

        mDialog = ProgressDialog(this)
        setEmployeeSpinner()
        setDate()
    }

    private fun setDate() {

        var cal = Calendar.getInstance()

        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val myFormat = "yyyy-MM-dd" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            binding.txtDateSelectMap.text = sdf.format(cal.time)
            date = binding.txtDateSelectMap.text.toString()
            callLocationService(mArrEmployee[empPosition].ID, date)
        }

        binding.txtDateSelectMap.setOnClickListener {
            DatePickerDialog(this, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun callLocationService(SOID: Int, Date: String?) {
        mDialog.show()
        mDialog.setMessage("please wait...")
        disposable = restService.getRouteDetails(SOID.toString(), Date)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> showResult(result) },
                { error -> showError(error.message) }
            )
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.clear()



        if (routeDetails == null) {
            return
        }

//        Log.e("all", "" + routeDetails.toString())
        /*mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))*/


        var oldLocation:LocPointsData? = null;
        for (location in routeDetails.locPointsData) {
//            val marker = googleMap.addMarker(MarkerOptions().position(LatLng(location.startLat, location.startLong)).visible(true))
//            marker.showInfoWindow()
            if (oldLocation == null){
                oldLocation = location
                continue
            }
            googleMap.addPolyline(PolylineOptions()
                .add(LatLng(oldLocation.startLat, oldLocation.startLong), LatLng(location.startLat, location.startLong))
                .width(5f)
                .color(Color.RED))
            oldLocation = location

        }
        if(routeDetails.locPointsData!= null)
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(routeDetails.locPointsData[0].startLat, routeDetails.locPointsData[0].startLong)))
        else
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(33.738045, 73.084488)))
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(12f), 2000, null)
    }

    private fun setEmployeeSpinner() {
        mArrEmployee = mLoginResponse.Data.SalesOfficer
        if (mArrEmployee.isEmpty()){
            Log.e("ABC", mArrEmployee.size.toString())
        } else{
            Log.e("ABCD", mArrEmployee.size.toString())
            callLocationService(mArrEmployee[empPosition].ID, date)
            adapter = ArrayAdapter<SalesOfficer>(this, android.R.layout.simple_spinner_dropdown_item, mArrEmployee)
            spnEmployee.adapter = adapter
        }
        if(mArrEmployee.size==1)
            spnEmployee.isEnabled = false
        spnEmployee.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                //if (position != 0) {
                empPosition = position
                callLocationService(mArrEmployee[empPosition].ID, date)
                //}
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun showResult(result: GetServerResponse) {
        routeDetails = result.locPoints;

        val df = DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH))
        df.maximumFractionDigits = 2
        totalKm = df.format(routeDetails.totalKM)
        totalKmTextView.setText(totalKm + " km")

//        Log.e("ALL VISITS", result.locPoints.toString())
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        mDialog.dismiss()

    }

    private fun showError(message: String?) {
        mDialog.dismiss()
        //Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

}


