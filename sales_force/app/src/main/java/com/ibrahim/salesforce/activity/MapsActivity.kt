package com.app.salesforce.activity

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.app.salesforce.R
import com.app.salesforce.databinding.ActivityMapsBinding
import com.app.salesforce.network.RestService
import com.app.salesforce.response.GetServerResponse
import com.app.salesforce.response.MyVisitsMapView
import com.app.salesforce.response.SalesOfficer
import com.app.salesforce.utilities.AppKeys
import io.paperdb.Paper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var spnEmployee : Spinner
    private lateinit var mMap: GoogleMap
    private lateinit var mArrLocations: MutableList<MyVisitsMapView>
    private lateinit var mArrEmployee: List<SalesOfficer>
    private lateinit var adapter: ArrayAdapter<SalesOfficer>
    private lateinit var mLoginResponse: GetServerResponse
    private lateinit var mDialog: ProgressDialog
    private lateinit var disposable: Disposable
    lateinit var mContext: Context
    private var date: String = ""
    private var empPosition: Int = 0
    private var mapFragment: SupportMapFragment?=null
    private val restService by lazy { RestService.create() }
    lateinit var binding: ActivityMapsBinding;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Daily Map Activity"
        Paper.init(this)
        mArrLocations = ArrayList<MyVisitsMapView>()
        spnEmployee = findViewById(R.id.spnEmployee)
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
        disposable = restService.getVisitsLocation(SOID.toString(), Date)
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
        if (mArrLocations.isNotEmpty()) {
            googleMap.clear()
        }
        Log.e("all", "" + mArrLocations.toString())
        /*mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))*/

        for (location in mArrLocations) {
            val marker = googleMap.addMarker(MarkerOptions().position(LatLng(location.Latitude, location.Longitude)).title(location.School).snippet(location.Area).visible(true))
            marker?.showInfoWindow()
        }
        if(mArrLocations.isNotEmpty())
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(mArrLocations[0].Latitude, mArrLocations[0].Longitude)))
        else
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(33.738045,73.084488)))
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(14f), 2000, null)
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
        mArrLocations = result.MyVisitsMapView.toMutableList()
        Log.e("ALL VISITS", result.MyVisitsMapView.toString())
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


