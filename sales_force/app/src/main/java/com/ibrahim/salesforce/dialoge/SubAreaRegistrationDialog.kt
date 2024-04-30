package com.app.salesforce.dialoge


import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.AllCaps
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.app.salesforce.R
import com.app.salesforce.fragment.AreaDialog
import com.app.salesforce.model.SampleSearchModel
import com.app.salesforce.network.ApiService
import com.app.salesforce.network.RequestCode
import com.app.salesforce.network.RestCallbackObject
import com.app.salesforce.network.RestClient
import com.app.salesforce.network.RestService
import com.app.salesforce.network.ServerCodes
import com.app.salesforce.network.ServerConnectListenerObject
import com.app.salesforce.offline.AppDataBase
import com.app.salesforce.offline.Cities
import com.app.salesforce.response.GetAreasResponse
import com.app.salesforce.response.GetCitiesResponse
import com.app.salesforce.response.GetCitiesResponse.City
import com.app.salesforce.response.GetServerResponse
import com.app.salesforce.response.GetZonesResponse
import com.app.salesforce.response.Region
import com.app.salesforce.utilities.AppKeys
import com.app.salesforce.utilities.Utility
import io.paperdb.Paper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Callback

class SubAreaRegistrationDialog() : DialogFragment(), ServerConnectListenerObject{
    private var zoneID: Int=-1;
    private var mSpnRegion: Spinner? = null
    private  var mSpnCity:Spinner? = null
    private var mBtnCheckin: Button? = null
    private val restService by lazy { RestService.create() }
    private var mLoginResponse: GetServerResponse? = null

    private var mContext: Context? = null
    private var mArrAreas: ArrayList<GetAreasResponse.Area>? = null
    private var mDialog: ProgressDialog? = null
    private var mService: ApiService? = null
    private var etArea:EditText?=null
    private var regionID=-1;
    private var cityID=-1;



    //DetailsDialogFragment dialogFragment;
    var mArrRegion: List<Region?>? = null

    var arrCitiesRelatedToRegion: List<City> ?=null
    var citiesList: List<Cities>? = null
    var sampleSearchModels: ArrayList<SampleSearchModel>? = null
    private var cityAdapter: ArrayAdapter<City>? = null
    private var areaToAdd="";
    private var viewZone:RelativeLayout?=null
    private var viewArea:RelativeLayout?=null
    private var spZone:Spinner? = null
    private var spArea:Spinner? = null

    private var mZoneResponse:GetZonesResponse?=null
    private var mAreaResponse:GetAreasResponse?=null



    companion object{
        private var areaAddedCallback:AreaDialog?=null

        fun newInstance(title: String?, mContext: Context?,areaAddedCallback:AreaDialog): SubAreaRegistrationDialog? {
            Paper.init(mContext)
            this.areaAddedCallback=areaAddedCallback;
            val frag = SubAreaRegistrationDialog()
            frag.mContext = mContext
            val args = Bundle()
            args.putString("title", title)
            frag.arguments = args
            return frag
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.dialog_add_new_sub_area, container, false)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.bg_round_corners)
        initComponents(view)
        Log.d("class_name", this.javaClass.simpleName)
        return view
    }

    private fun initComponents(view: View) {
        mSpnRegion = view.findViewById(R.id.spn_region)
        mBtnCheckin = view.findViewById(R.id.btnCheckIn)
        etArea=view.findViewById(R.id.et_sub_area)

        mLoginResponse = Paper.book().read(AppKeys.KEY_LOGIN_RESPONSE)
        mDialog = ProgressDialog(mContext)

        viewZone    = view.findViewById(R.id.view_zone)
        viewArea=view.findViewById(R.id.view_sub_area)
        spZone=view.findViewById(R.id.sp_zone)
        spArea=view.findViewById(R.id.sp_sub_area)

//all caps
        //mEtEmail.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        etArea?.filters = arrayOf<InputFilter>(AllCaps())


        mDialog?.setCancelable(false)
        mSpnCity = view.findViewById(R.id.spn_city)
        setRegionSpinner()
        sampleSearchModels = java.util.ArrayList()

        mArrAreas = java.util.ArrayList()
        mBtnCheckin?.setOnClickListener(View.OnClickListener {
            if (Utility.isNetworkAvailable(mContext)) {
                //store data to the database
                areaToAdd= etArea?.text.toString()
                if(areaToAdd.isNotEmpty()&&areaToAdd.length>3) {
                    callService(55)

                }else {
                    etArea?.error = "Please enter correct Sub-Area"
                }
            }
        })
    }
    private fun setRegionSpinner() {
        mArrRegion = mLoginResponse?.Data?.Region

        regionID= mArrRegion?.get(0)?.ID!!;
        val adapter = ArrayAdapter(
            mContext!!, android.R.layout.simple_spinner_dropdown_item,
            mArrRegion!!
        )
        mSpnRegion!!.adapter = adapter
        mSpnRegion!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                (view as TextView).setTextColor(getResources().getColorStateList(R.color.black))
                loadCities(position)

                mSpnCity?.visibility=View.GONE
                viewArea?.visibility=View.GONE
                viewZone?.visibility=View.GONE

                if (Utility.isNetworkAvailable(mContext)) {
                    regionID = mArrRegion?.get(position)?.ID!!;
                    //                    loadCities(position);
                    callService(3)
                } else Toast.makeText(
                    mContext,
                    getString(R.string.str_no_internet),
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setCitySpinner() {
        cityAdapter = ArrayAdapter(
            mContext!!,
            android.R.layout.simple_spinner_dropdown_item,
            arrCitiesRelatedToRegion as List<GetCitiesResponse.City>
        )
        mSpnCity!!.adapter = cityAdapter

        cityID = arrCitiesRelatedToRegion!![0].id

        mSpnCity!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                (view as TextView).setTextColor(getResources().getColorStateList(R.color.black))
                cityID = arrCitiesRelatedToRegion!![position].id;
                callService(4)//get related zones
                viewZone?.visibility=View.GONE
                viewArea?.visibility=View.GONE
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        mDialog!!.dismiss()


    }




    override fun onFailure(error: String, requestCode: RequestCode) {
        if(requestCode==RequestCode.GET_CITIES_REQUEST_CODE){
            viewZone?.visibility=View.GONE
            viewArea?.visibility=View.GONE
        }else if(requestCode==RequestCode.GET_ZONES_REQUEST_CODE){
            if (mDialog!!.isShowing) {
                mDialog!!.dismiss()
            }
            viewZone?.visibility = View.GONE
            viewArea?.visibility = View.GONE

            Log.e("Result", "Failed")
            Toast.makeText(mContext, "Failed-- $error", Toast.LENGTH_SHORT).show()
        }
        Toast.makeText(mContext, "API Failed--$requestCode$error", Toast.LENGTH_SHORT).show()
        mDialog!!.dismiss()
        // this.dismiss();
    }

    override fun onSuccess(response: Any?, requestCode: RequestCode?) {
        if (requestCode == RequestCode.GET_CITIES_REQUEST_CODE) {
            if ((response as GetCitiesResponse).cities == null || response.cities.isEmpty()) {
                viewZone?.visibility=View.GONE
                viewArea?.visibility=View.GONE
                mSpnCity?.visibility=View.GONE
                Toast.makeText(mContext, "There are no cities in this ", Toast.LENGTH_SHORT).show()
                mDialog!!.dismiss()
            } else {
                mSpnCity?.visibility = View.VISIBLE
                arrCitiesRelatedToRegion = response.cities
                setCitySpinner()
            }
        }else if(requestCode==RequestCode.GET_ZONES_REQUEST_CODE){
            mZoneResponse = response as GetZonesResponse
            if (mDialog!!.isShowing) {
                mDialog!!.hide()
            }
            //testing
            //testing
            if (mZoneResponse?.zones!!.size <= 0) {
                Toast.makeText(mContext, "There are no Zones associated", Toast.LENGTH_SHORT).show()
                viewZone?.setVisibility(View.GONE)
                viewArea?.setVisibility(View.GONE)

            } else {
                viewZone?.visibility = View.VISIBLE
                setZoneSpinner(mZoneResponse)
            }
        }else if(requestCode==RequestCode.GET_AREA_REQUEST_CODE){
            mAreaResponse = response as GetAreasResponse
            if (mDialog!!.isShowing) {
                mDialog!!.hide()
            }
            //testing
            //testing
            if (mAreaResponse?.areas!!.size <= 0) {
                Toast.makeText(mContext, "There are no Areas associated", Toast.LENGTH_SHORT).show()
                viewArea?.setVisibility(View.GONE)

            } else {
                viewArea?.visibility = View.VISIBLE
                setAreaSpinner(mAreaResponse)
            }
        }
        mDialog!!.hide()
    }


    private var areaID=-1;
    private  var areaAdapter:ArrayAdapter<GetAreasResponse.Area>?=null;

    private fun setAreaSpinner(mAreaResponse: GetAreasResponse?) {
        areaID = -1
        mArrAreas = mAreaResponse?.areas as ArrayList<GetAreasResponse.Area>
        areaAdapter = ArrayAdapter<GetAreasResponse.Area>(
            mContext!!,
            android.R.layout.simple_spinner_dropdown_item,
            mArrAreas!!
        )
        spArea?.setAdapter(areaAdapter)
        spArea?.setVisibility(View.VISIBLE)
        viewArea?.setVisibility(View.VISIBLE)
        val areaSearchList = java.util.ArrayList<SampleSearchModel?>()
        for (i in mArrAreas!!.indices) {
            val area = mArrAreas!!.get(i)
            val name = area.name
            val id = area.id
            areaSearchList.add(SampleSearchModel(name, id))
        }


             spArea?.onItemSelectedListener=object : OnItemSelectedListener {
                 override fun onItemSelected(
                     parent: AdapterView<*>?,
                     view: View?,
                     position: Int,
                     id: Long
                 ) {
                     areaID= mArrAreas?.get(position)?.id!!;
                     etArea?.visibility=View.VISIBLE
                 }

                 override fun onNothingSelected(parent: AdapterView<*>?) {
                 }

             }
    }

    private var mArrZones:ArrayList<GetZonesResponse.Zone>?=null
    private var zoneAdapter:ArrayAdapter<GetZonesResponse.Zone>?=null
    private fun setZoneSpinner(mZonesResponse: GetZonesResponse?) {
        zoneID = -1
        mArrZones = mZonesResponse?.zones as ArrayList<GetZonesResponse.Zone>

        zoneAdapter = ArrayAdapter<GetZonesResponse.Zone>(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            mArrZones!!
        )
        spZone?.setAdapter(zoneAdapter)

        spZone?.onItemSelectedListener=object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                zoneID=mArrZones?.get(position)!!.id;
                loadAreas();
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

    }

    private fun loadAreas() {
        mDialog!!.show()
        mDialog!!.setMessage("Fetching areas please wait...")
        mService = RestClient.getInstance(mContext)
        val userObject = mService?.getAreas(cityID.toString(), mLoginResponse!!.Data.SOID, zoneID)
        val callbackObject =
            RestCallbackObject(activity, this, RequestCode.GET_AREA_REQUEST_CODE).showProgress(
                true,
                0
            ).dontHideProgress(false)
        userObject?.enqueue(callbackObject as Callback<GetAreasResponse>)
    }


    private fun loadCities(position: Int) {
        Thread {
            val something = AppDataBase.getInstance(getContext())
                .cityDao()
                .getCities(mArrRegion!![position]!!.ID)
            citiesList = something
            for (i in citiesList!!.indices) {
//                    citiesList.add(something.get(i));
                Log.d(
                    "ehtie",
                    "cities: " + something[i].cityName + "\t RegionID: " + something[i].regionId
                )
            }
        }.start()
    }
    private fun callService(type: Int) {
        mService = RestClient.getInstance(mContext)
        if (type == 3) {
            mDialog!!.show()
            mDialog!!.setMessage("Loading Cities,please wait...")
            val userObject = mService!!.getCities(regionID, mLoginResponse!!.Data.SOID)
            val callbackObject = RestCallbackObject(
                mContext as Activity?,
                this ,
                RequestCode.GET_CITIES_REQUEST_CODE
            ).showProgress(true, 0).dontHideProgress(false)
            userObject.enqueue(callbackObject as Callback<GetCitiesResponse>)
        }else if(type==4){
            mDialog!!.show()
            mDialog!!.setMessage("Fetching Zones please wait...")
            mService = RestClient.getInstance(mContext)
            val userObject = mService?.getZones(cityID.toString(), mLoginResponse!!.Data.SOID)
            val callbackObject =
                RestCallbackObject(activity, this, RequestCode.GET_ZONES_REQUEST_CODE).showProgress(
                    true,
                    0
                ).dontHideProgress(false)
            userObject?.enqueue(callbackObject as Callback<GetZonesResponse>)

        } else if(type==55){
            mDialog!!.show()
            mDialog!!.setMessage("Adding new SUb-Area,please wait...")

            val disposable: Disposable = restService.addSubArea(regionID,cityID,zoneID,areaID, areaToAdd,mLoginResponse?.Data?.SOID.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result -> showResult(result) },
                    { error -> showError(error.message) }
                )
            // send data to database
        }
    }

    private fun showError(message: String?) {
        if (mDialog?.isShowing == true) {
            mDialog?.dismiss()
        }
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
        Log.e("msg", message.toString())
    }

    private fun showResult(response: GetServerResponse) {


        Log.e("Response", response.toString())
        //  Log.e("ALL DATA", response.Data.toString())
//        Log.d("ehtesham", response.Data.Class.toString())

        // Remove this
//        for (i in response.Data.Class) {
//            Log.d("ehtesham", "" + i.ClassName)
//        }
        if (mDialog?.isShowing == true) {
            mDialog?.dismiss()
        }
        if (response.ResultType == ServerCodes.ServerResultType.SERVER_RESULT_SUCCESS) {

            this.dismiss()
            areaAddedCallback?.subAreaAdded();
            Toast.makeText(activity, response.Message, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(activity, response.Message, Toast.LENGTH_SHORT).show()
            Log.e("TAG", "showResult: "+response.Message)
        }
    }


}