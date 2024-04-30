package com.app.salesforce.dialoge


import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
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
import com.app.salesforce.response.Region
import com.app.salesforce.utilities.AppKeys
import com.app.salesforce.utilities.Utility
import io.paperdb.Paper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Callback

class AreaRegistrationDialogFrag() : DialogFragment(), ServerConnectListenerObject{
    private var mSpnRegion: Spinner? = null
    private  var mSpnCustomersRelatedToSo:Spinner? = null
    private  var mSpnCity:Spinner? = null
    private  var mSpnArea:Spinner? = null
    private var mBtnCheckin: Button? = null
    private val restService by lazy { RestService.create() }
    private var mLoginResponse: GetServerResponse? = null

    private var mContext: Context? = null
    private var mArrAreas: List<GetAreasResponse.Area>? = null
    private var mDialog: ProgressDialog? = null
    private var mService: ApiService? = null
    private var etArea:EditText?=null
    private var regionPosition = 0
    private var regionID=-1;
    private var cityID=-1;
    private  var cityPosition:Int = 0
    private var mRlRegionCustomers: RelativeLayout? = null

    //DetailsDialogFragment dialogFragment;
    var mArrRegion: List<Region?>? = null

    var arrCitiesRelatedToRegion: List<City> ?=null
    var citiesList: List<Cities>? = null
    var sampleSearchModels: ArrayList<SampleSearchModel>? = null
    var newsampleSearchModels: ArrayList<SampleSearchModel>? = null
    private var cityAdapter: ArrayAdapter<City>? = null
    private var areaToAdd="";



    companion object{
        private var areaAddedCallback:AreaDialog?=null

        fun newInstance(title: String?, mContext: Context?,areaAddedCallback:AreaDialog): AreaRegistrationDialogFrag? {
            Paper.init(mContext)
            this.areaAddedCallback=areaAddedCallback;
            val frag = AreaRegistrationDialogFrag()
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
        val view: View = inflater.inflate(R.layout.dialog_add_new_area, container, false)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.bg_round_corners)
        initComponents(view)
        Log.d("class_name", this.javaClass.simpleName)
        return view
    }

    private fun initComponents(view: View) {
        mSpnRegion = view.findViewById(R.id.spn_region)
        mSpnCustomersRelatedToSo = view.findViewById(R.id.spn_customers)
        mBtnCheckin = view.findViewById(R.id.btnCheckIn)
        etArea=view.findViewById(R.id.et_sub_area)
        mRlRegionCustomers = view.findViewById(R.id.rl_region_customers)
        mSpnArea = view.findViewById(R.id.spn_pop_area)
        mLoginResponse = Paper.book().read(AppKeys.KEY_LOGIN_RESPONSE)
        mDialog = ProgressDialog(mContext)

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
                    etArea?.error = "Please enter correct area"
                }
            }
        })
    }
    private fun setRegionSpinner() {
        mArrRegion = mLoginResponse?.Data?.Region
        //        if (mArrRegion.size() > 1) {
//            Region region = new Region(0, "Select Region");
//            mArrRegion.add(0, region);
//        } else


//        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    callService(3);
//                } catch (Exception ex) {
//                    Toast.makeText(mContext, "Please Wait for data to populate", Toast.LENGTH_LONG).show();
//                }
//            }
//        }, 600);

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
                if (Utility.isNetworkAvailable(mContext)) {
                    regionID = mArrRegion?.get(position)?.ID!!;
                    regionPosition=position
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

        cityID=arrCitiesRelatedToRegion!![0].id

        mSpnCity!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                (view as TextView).setTextColor(getResources().getColorStateList(R.color.black))
                cityPosition = position
                cityID=arrCitiesRelatedToRegion!![position].id;
                etArea?.visibility = View.VISIBLE
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        mDialog!!.dismiss()


//        //code for searching
//        mSpnCity!!.setOnTouchListener{ v: View?, event: MotionEvent? ->
//            if (event?.action == MotionEvent.ACTION_UP) {
//                arrCitiesRelatedToRegion?.clear()
//                val temp: SimpleSearchDialogCompat<*> =
//                    SimpleSearchDialogCompat(getActivity(), "Search...",
//                        "What are you looking for...?", null, newsampleSearchModels,
//                        SearchResultListener<SampleSearchModel> { dialog, item, position ->
//                            arrCitiesRelatedToRegion?.add(
//                                GetCitiesResponse().City(
//                                    item.id,
//                                    item.title
//                                )
//                            )
//                            cityAdapter = ArrayAdapter(
//                                mContext!!,
//                                android.R.layout.simple_spinner_dropdown_item,
//                                arrCitiesRelatedToRegion as MutableList<GetCitiesResponse.City>
//                            )
//                            mSpnCity!!.adapter = cityAdapter
//                            mSpnCity!!.visibility = View.VISIBLE
//                            mSpnCity!!.onItemSelectedListener =
//                                object : AdapterView.OnItemSelectedListener {
//                                    override fun onItemSelected(
//                                        parent: AdapterView<*>,
//                                        view: View,
//                                        position: Int,
//                                        id: Long
//                                    ) {
//
//                                        val selectedText = parent.getChildAt(0) as TextView
//                                        if (selectedText != null) {
//                                            selectedText.setTextColor(Color.BLACK)
//                                        } else {
//                                            selectedText.setTextColor(Color.BLACK)
//                                        }
//
//                                    }
//
//                                    override fun onNothingSelected(parent: AdapterView<*>?) {}
//                                }
//                            dialog.dismiss()
//                        })
//                temp.show()
//                temp.titleTextView.setTextColor(Color.BLACK)
//                temp.searchBox.setTextColor(getResources().getColorStateList(R.color.innercolor))
//            }
//            true
//        }
    }





    override fun onFailure(error: String, requestCode: RequestCode) {
        Toast.makeText(mContext, "API Failed--$requestCode$error", Toast.LENGTH_SHORT).show()
        mDialog!!.dismiss()
        // this.dismiss();
    }

    override fun onSuccess(response: Any?, requestCode: RequestCode?) {
        if (requestCode == RequestCode.GET_CITIES_REQUEST_CODE) {
            if ((response as GetCitiesResponse).cities == null || response.cities.isEmpty()) {
                mSpnArea!!.visibility = View.GONE
                mSpnCustomersRelatedToSo!!.visibility = View.GONE
                Toast.makeText(mContext, "There are no cities in this ", Toast.LENGTH_SHORT).show()
                mDialog!!.dismiss()
            } else {
                mSpnCity!!.visibility = View.VISIBLE
                arrCitiesRelatedToRegion = response.cities
                setCitySpinner()
            }
        }
        mDialog!!.hide()
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
             val userObject = mService!!.getCities(mArrRegion?.get(regionPosition)!!.ID, mLoginResponse!!.Data.SOID)
             val callbackObject = RestCallbackObject(
                 mContext as Activity?,
                 this ,
                 RequestCode.GET_CITIES_REQUEST_CODE
             ).showProgress(true, 0).dontHideProgress(false)
             userObject.enqueue(callbackObject as Callback<GetCitiesResponse>)
        } else if(type==55){
            mDialog!!.show()
            mDialog!!.setMessage("Adding new Area,please wait...")

             val disposable: Disposable = restService.addArea(mLoginResponse?.Data?.SOID.toString(),
                 regionID,cityID, areaToAdd)
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
            areaAddedCallback?.areaAdded();
            Toast.makeText(activity, response.Message, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(activity, response.Message, Toast.LENGTH_SHORT).show()
            Log.e("TAG", "showResult: "+response.Message)
        }
    }


}