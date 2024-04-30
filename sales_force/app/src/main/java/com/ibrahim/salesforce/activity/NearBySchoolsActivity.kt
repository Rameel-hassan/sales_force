package com.app.salesforce.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.app.salesforce.R
import com.app.salesforce.dialoge.NearBySchoolsFragment
import com.app.salesforce.network.RestService
import com.app.salesforce.offline.AppDataBase
import com.app.salesforce.offline.SchLocations
import com.app.salesforce.response.GetServerResponse
import com.app.salesforce.response.MyVisitsMapView
import com.app.salesforce.response.SLocations
import com.app.salesforce.response.SalesOfficer
import com.app.salesforce.utilities.AppKeys
import io.paperdb.Paper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class NearBySchoolsActivity : AppCompatActivity(), OnMapReadyCallback {

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
    private var mapFragment: SupportMapFragment? = null

    private lateinit var sLocations: List<SLocations>
    private lateinit var schLocations: List<SchLocations>

    lateinit var db: AppDataBase
    lateinit var schols: List<SchLocations>

    private val restService by lazy { RestService.create() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_near_by_schools)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Daily Map Activity"

        Paper.init(this)
        mArrLocations = ArrayList<MyVisitsMapView>()
        sLocations = ArrayList<SLocations>()


        var i:Intent = intent
        try {
            sLocations = (i.getSerializableExtra("myloc") as List<SLocations>)
        } catch (e: Exception) {
            Log.e("TAG", "error.${e.toString()}" )
        }
        Log.e("slocation", "onCreate: " + sLocations.size )

        for (i in sLocations) {

            Log.e("slocation", "onCreate: " + i.schoolName )
        }

//        val bundle = intent.extras
//        sLocations =  bundle.getParcelable<Parcelable>("myloc") as List(SLocations)

//        sLocations = list


//        btnChangeArea.setOnClickListener {
//            Log.e("TEST", "Done")
//            showEditDialog(15)
//        }

        Paper.init(this)
        mLoginResponse = Paper.book().read<GetServerResponse>(AppKeys.KEY_LOGIN_RESPONSE)
        // the SupportMapFragment and get notified when the map is ready to be used.

        mDialog = ProgressDialog(this)


//        db = AppDataBase.getInstance(applicationContext)
//        getTasks()

//        val mapFragment = supportFragmentManager
//            .findFragmentById(R.id.map) as SupportMapFragment
//        mapFragment.getMapAsync(this)

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
        if (sLocations.isNotEmpty()) {
            googleMap.clear()
        }
        Log.e("all", "" + sLocations.toString())

        for (location in sLocations) {
            val marker = googleMap.addMarker(MarkerOptions().position(LatLng(location.latitude,location.longitude)).title(location.schoolName).snippet(location.schoolName).visible(true))
            marker?.showInfoWindow()
        }
        if(sLocations.isNotEmpty())
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(sLocations[0].latitude, sLocations[0].longitude)))
        else
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(33.738045,73.084488)))
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(14f), 2000, null)
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

    private fun showEditDialog(type: Int) {
        val fm = supportFragmentManager
        val editNearBySchoolsActivity = NearBySchoolsFragment.newInstance("Information", this, type)
        editNearBySchoolsActivity.show(fm, "fragment_edit_name")
    }

    /*private fun getTasks() {
        class GetTasks :
            AsyncTask<Void?, Void?, List<SchLocations?>?>() {
            override fun doInBackground(vararg voids: Void?): List<SchLocations> {

                schols = db.schLocationsDao().allSchLocations
                if (schols.isNotEmpty()){
                    Log.e("usersss", " " + schols[0].schoolName)

                    for (i in schols) {
                        Log.e("usersss", " " + i.schoolName)
                    }
                }

                return schols
            }

            override fun onPostExecute(tasks: List<SchLocations?>?) {
                super.onPostExecute(tasks)

                Log.e("testttt", "onPostExecute: jhjk")
                val mapFragment = supportFragmentManager
                    .findFragmentById(R.id.map) as SupportMapFragment
                mapFragment.getMapAsync(this@NearBySchoolsActivity)
            }
        }
//        val gt = GetTasks()
//        gt.execute()
    }*/

    override fun onResume() {

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        super.onResume()
    }

}


