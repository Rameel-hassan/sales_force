package com.app.salesforce.activity

import android.R
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.salesforce.adapters.SampleDetailsAdapter
import com.app.salesforce.adapters.SamplesHistoryAdapter
import com.app.salesforce.databinding.ActivitySampleHistoryBinding
import com.app.salesforce.databinding.DbSampleDetailsBinding
import com.app.salesforce.model.SampleSearchModel
import com.app.salesforce.network.ApiService
import com.app.salesforce.network.RequestCode
import com.app.salesforce.network.RestCallbackObject
import com.app.salesforce.network.RestClient
import com.app.salesforce.network.ServerConnectListenerObject
import com.app.salesforce.offline.AppDataBase
import com.app.salesforce.offline.Cities
import com.app.salesforce.response.CustomersRelatedtoSO
import com.app.salesforce.response.GetAreasResponse
import com.app.salesforce.response.GetCitiesResponse
import com.app.salesforce.response.GetSamplesHistoryResponse
import com.app.salesforce.response.GetSamplesHistoryResponse.SampledDetails
import com.app.salesforce.response.GetServerResponse
import com.app.salesforce.response.Region
import com.app.salesforce.response.SalesOfficer
import com.app.salesforce.utilities.AppKeys
import com.app.salesforce.utilities.Utility
import io.paperdb.Paper
import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat
import ir.mirrajabi.searchdialog.core.SearchResultListener
import retrofit2.Call
import retrofit2.Callback
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class SampleHistoryActivity : AppCompatActivity(), ServerConnectListenerObject {

    private lateinit var binding: ActivitySampleHistoryBinding
    private val startDateCalendar = Calendar.getInstance()
    private var mArrEmployee: List<SalesOfficer>? = null
    private var mLoginResponse: GetServerResponse? = null
    private var mCustomerObj: CustomersRelatedtoSO? = null
    private var otherSOID = -1
    private var regionID = -1
    private var cityID = -1
    private var areaID = -1
    private var selectedSampleDetails:SampledDetails?=null
    private var dialogSamples:Dialog?=null

    private var selectedSampleDate=""
    private var selectedSampleID=0;

    private var mArrRegion: List<Region>? = null
    private var citiesList: List<Cities>? = null
    private var mDialog: ProgressDialog? = null
    private var mService: ApiService? = null
    private var arrCitiesRelatedToRegion: ArrayList<GetCitiesResponse.City>? = null
    private var newSampleSearchModels: ArrayList<SampleSearchModel>?=null
    private var mArrAreas: ArrayList<GetAreasResponse.Area>? = null
    private var samples: ArrayList<GetSamplesHistoryResponse.Sample>? = null
    private var samplesDetails: ArrayList<GetSamplesHistoryResponse.SampledDetails>? = null

    private var samplesAdapter:SamplesHistoryAdapter?=null
    private var sampleDetailsAdapter:SampleDetailsAdapter?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySampleHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        newSampleSearchModels= ArrayList()
        parseBundle()
        mArrEmployee = mLoginResponse?.Data?.SalesOfficer
        mDialog= ProgressDialog(this@SampleHistoryActivity);
        initSpinners()

        binding.btnSearch.setOnClickListener {
            if(validDates()){
                callService(7)
            }
        }


        binding.tvEndDate.setOnClickListener {
            if (binding.tvStartDate.text.equals("Start Date")) {
                Toast.makeText(
                    this@SampleHistoryActivity,
                    "Start Date not selected",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            val myCalendar = Calendar.getInstance()
            DatePickerDialog(
                this@SampleHistoryActivity,
                { view, year, month, dayOfMonth ->


                    myCalendar[Calendar.YEAR] = year
                    myCalendar[Calendar.MONTH] = month
                    myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                    if (myCalendar.timeInMillis < startDateCalendar.timeInMillis) {
                        Toast.makeText(
                            this@SampleHistoryActivity,
                            "End date should be greater than Start Date",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@DatePickerDialog
                    }
                    val myFormat = "MM-dd-yyyy" //In which you need put here

                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    binding.tvEndDate.text = sdf.format(myCalendar.time)

                }, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.tvStartDate.setOnClickListener {

            DatePickerDialog(
                this@SampleHistoryActivity,
                { view, year, month, dayOfMonth ->

                    // TODO Auto-generated method stub
                    startDateCalendar[Calendar.YEAR] = year
                    startDateCalendar[Calendar.MONTH] = month
                    startDateCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                    val myFormat = "MM-dd-yyyy" //In which you need put here

                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    binding.tvStartDate.text = sdf.format(startDateCalendar.time)

                }, startDateCalendar
                    .get(Calendar.YEAR), startDateCalendar.get(Calendar.MONTH),
                startDateCalendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

    }

    private fun validDates(): Boolean {
        if(binding.tvStartDate.text.equals("Start Date")||binding.tvEndDate.text.equals("End Date")){
            Toast.makeText(this@SampleHistoryActivity,"Dates are not selected.",Toast.LENGTH_SHORT).show()
            return false
        }else if(cityID==-1||regionID==-1||areaID==-1||otherSOID==-1){
            Toast.makeText(this@SampleHistoryActivity,"Spinners are not selected Correctly.",Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun parseBundle() {
        Paper.init(this)
        mLoginResponse = Paper.book().read<GetServerResponse>(AppKeys.KEY_LOGIN_RESPONSE)
    }

    private fun initSpinners() {
        setSalesOfficerSpinner();

    }

    private fun setSalesOfficerSpinner() {
        val salesOficerAdapter =
            ArrayAdapter<SalesOfficer>(this, R.layout.simple_spinner_dropdown_item, mArrEmployee!!)
        binding.spnSchool.setAdapter(salesOficerAdapter)
        //        if (mArrEmployee.size() == 1) {
        // mSpnSalesOfficer.setEnabled(false);
        otherSOID = mArrEmployee?.get(0)!!.ID
        //   }
        binding.spnSchool.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    otherSOID = mArrEmployee?.get(position)!!.ID
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        setRegionSpinner()
    }

    private fun setRegionSpinner() {
        mArrRegion = mLoginResponse!!.Data.Region

        val adapter: ArrayAdapter<Region> =
            ArrayAdapter<Region>(this, R.layout.simple_spinner_dropdown_item, mArrRegion!!)
        binding.spnRegion.adapter = adapter
        regionID = mArrRegion!![0].ID
        binding.spnRegion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                (view as TextView).setTextColor(resources.getColorStateList(R.color.black))
                loadCities(position)
                if (Utility.isNetworkAvailable(this@SampleHistoryActivity)) {
                    regionID = mArrRegion?.get(position)!!.ID
                    callService(3)
                } else
                    Toast.makeText(
                        this@SampleHistoryActivity,
                        "Internet not available",
                        Toast.LENGTH_SHORT
                    ).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun loadCities(position: Int) {
        Thread {
            val something = AppDataBase.getInstance(this@SampleHistoryActivity)
                .cityDao()
                .getCities(mArrRegion!![position].ID)
            citiesList = something
//            for (i in citiesList!!.indices) {
////                    citiesList.add(something.get(i));
//                Log.d(
//                    "ehtie",
//                    "cities: " + something[i].cityName + "\t RegionID: " + something[i].regionId
//                )
//            }
        }.start()
    }



    private fun callService(type: Int, jobItemID: Int=-1) {
        mService = RestClient.getInstance(this@SampleHistoryActivity)
        if (type == 3) {
            mDialog?.show()
            mDialog?.setMessage("Loading Cities,please wait...")
            val userObject: Call<GetCitiesResponse> =
                mService!!.getCities(regionID, mLoginResponse!!.Data.SOID)
            val callbackObject: RestCallbackObject = RestCallbackObject(
                this@SampleHistoryActivity as Activity?,
                this,
                RequestCode.GET_CITIES_REQUEST_CODE
            ).showProgress(true, 0).dontHideProgress(false)
            userObject.enqueue(callbackObject as Callback<GetCitiesResponse>)
        } else if (type == 5) {
            mDialog?.show()
            mDialog?.setMessage("Fetching areas please wait...")
            mService = RestClient.getInstance(this@SampleHistoryActivity)
            val userObject: Call<GetAreasResponse> = mService!!.getAreas(
                cityID.toString(),
                mLoginResponse!!.Data.SOID
            )
            val callbackObject = RestCallbackObject(
                this@SampleHistoryActivity as Activity?,
                this,
                RequestCode.GET_AREAS_FOR_CITY
            )
            callbackObject.showProgress(true, 0)
            callbackObject.dontHideProgress(false)
            userObject.enqueue(callbackObject as Callback<GetAreasResponse>)
        }else if(type==7){
            mDialog!!.show()
            mDialog!!.setMessage("Fetching Samples please wait...")
            mService = RestClient.getInstance(this)
            val userObject:Call<GetSamplesHistoryResponse>? = mService?.getSamplesHistory(binding.tvEndDate.text.toString(),
                binding.tvStartDate.text.toString(),otherSOID,
                cityID, areaID)
            val callbackObject =
                RestCallbackObject(this, this, RequestCode.GET_SAMPLES_HISTORY).showProgress(
                    true,
                    0
                ).dontHideProgress(false)
            userObject?.enqueue(callbackObject as Callback<GetSamplesHistoryResponse>)
        }else if(type==8){
            mDialog!!.show()
            mDialog!!.setMessage("Fetching Sample Details please wait...")
            mService = RestClient.getInstance(this@SampleHistoryActivity)
            val userObject:Call<GetSamplesHistoryResponse>? = mService?.getSampleDetails(selectedSampleID)
            val callbackObject =
                RestCallbackObject(this@SampleHistoryActivity, this, RequestCode.GET_SAMPLES_HISTORY_DETAILS).showProgress(
                    true,
                    0
                ).dontHideProgress(false)
            userObject?.enqueue(callbackObject as Callback<GetSamplesHistoryResponse>)
        }else if(type==9){
            mDialog!!.show()
            mDialog!!.setMessage("Removing Sample please wait...")
            mService = RestClient.getInstance(this@SampleHistoryActivity)
            val userObject:Call<GetSamplesHistoryResponse>? = mService?.removeSampleDemand(selectedSampleDetails!!.jobItemID)
            val callbackObject =
                RestCallbackObject(this@SampleHistoryActivity, this, RequestCode.REMOVE_SAMPLE_DEMAND).showProgress(
                    true,
                    0
                ).dontHideProgress(false)
            userObject?.enqueue(callbackObject as Callback<GetSamplesHistoryResponse>)
        }
    }

    override fun onFailure(error: String?, requestCode: RequestCode?) {
        if (requestCode == RequestCode.GET_AREAS_FOR_CITY) {
            Toast.makeText(this@SampleHistoryActivity, "No area Associated", Toast.LENGTH_SHORT)
                .show()
            binding.spnArea.visibility = View.GONE
        } else if(requestCode==RequestCode.GET_SAMPLES_HISTORY) {
            Toast.makeText(
                this@SampleHistoryActivity,
                "No Samples found.",
                Toast.LENGTH_SHORT
            ).show()

        }else if(requestCode==RequestCode.GET_SAMPLES_HISTORY_DETAILS){
            Toast.makeText(
                this@SampleHistoryActivity,
                "No Samples Found",
                Toast.LENGTH_SHORT
            ).show()
        }else if(requestCode==RequestCode.REMOVE_SAMPLE_DEMAND){
            Toast.makeText(
                this@SampleHistoryActivity,
                "Failed to Remove Sample.",
                Toast.LENGTH_SHORT
            ).show()
        }else{
            Toast.makeText(
                this@SampleHistoryActivity,
                "API Failed--$requestCode$error",
                Toast.LENGTH_SHORT
            ).show()
        }
        mDialog?.hide()
    }

    override fun onSuccess(`object`: Any?, requestCode: RequestCode?) {
        if (requestCode == RequestCode.GET_CITIES_REQUEST_CODE) {
            if ((`object` as GetCitiesResponse).cities == null || `object`.cities.isEmpty()) {
                binding.spnCity.visibility = View.GONE
                binding.spnArea.visibility = View.GONE
                Toast.makeText(
                    this@SampleHistoryActivity,
                    "There are no cities in this ",
                    Toast.LENGTH_SHORT
                ).show()
                mDialog!!.dismiss()
            } else {
                binding.rlSpnCity.visibility = View.VISIBLE
                arrCitiesRelatedToRegion = `object`.cities as ArrayList<GetCitiesResponse.City>?
                setCitySpinner()
            }
        } else if (requestCode == RequestCode.GET_AREAS_FOR_CITY) {
            if (mDialog!!.isShowing) {
                mDialog?.hide()
            }
            val mNewAreaResponse = `object` as GetAreasResponse
            if (mNewAreaResponse.areas == null) {
                binding.spnArea.visibility = View.GONE
                Toast.makeText(
                    this@SampleHistoryActivity,
                    "There are no areas associated",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                binding.rlSpnArea.visibility = View.VISIBLE
                setAreaSpinner(mNewAreaResponse)
            }
        }else if (requestCode==RequestCode.GET_SAMPLES_HISTORY){
            samples= (`object` as GetSamplesHistoryResponse).samplesHistory as ArrayList<GetSamplesHistoryResponse.Sample>?
            mDialog!!.hide()

            if(samples!=null&&samples?.isNotEmpty()==true){
                for(i in 0 until samples!!.size){
                    samples!![i].srNo=i+1
                    samples!![i].jobDate=samples!![i].jobDate.substring(0,10)
                }
                initSamplesAdapter()
                binding.tvNoSamples.visibility=View.GONE
                binding.rv.visibility=View.VISIBLE
                binding.tableHeading.visibility=View.VISIBLE
            }else{

                binding.tvNoSamples.visibility=View.VISIBLE
                binding.rv.visibility=View.GONE
                binding.tableHeading.visibility=View.GONE
            }




        }else if(requestCode==RequestCode.GET_SAMPLES_HISTORY_DETAILS){
            samplesDetails= (`object` as GetSamplesHistoryResponse).sampledDetails as ArrayList<GetSamplesHistoryResponse.SampledDetails>?
            mDialog!!.dismiss()

            if(samplesDetails!=null&& samplesDetails?.isNotEmpty()==true){
                for(i in 0 until samplesDetails!!.size){
                    samplesDetails!![i].srNo=i+1
                   // samplesDetails!![i].jobDate=date
                }
                initSampleDetails()

            }else{
                Toast.makeText(this@SampleHistoryActivity,"No Details found for this Sample",Toast.LENGTH_SHORT).show()
            }


        }else if(requestCode==RequestCode.REMOVE_SAMPLE_DEMAND){
            val result=(`object` as GetSamplesHistoryResponse).result as String?
            if(result!=null&&result.isNotEmpty()){
                Toast.makeText(this@SampleHistoryActivity,"Deleted Successfully",Toast.LENGTH_SHORT).show()
                var index=samplesDetails?.indexOf(selectedSampleDetails)

                if (index != null) {
                    sampleDetailsAdapter?.notifyItemRemoved(index)
                    samplesDetails?.remove(selectedSampleDetails)
                }
                selectedSampleDetails=null

                if(samplesDetails?.isEmpty() == true&& dialogSamples?.isShowing==true){
                    dialogSamples?.dismiss()
                }
            }
        }
    }

    private fun initSamplesAdapter() {
        with(binding){
            rv.layoutManager=LinearLayoutManager(this@SampleHistoryActivity,LinearLayoutManager.VERTICAL,false)
            samplesAdapter=SamplesHistoryAdapter(samples!!){ isClicked: Boolean, sample: GetSamplesHistoryResponse.Sample ->
                if(isClicked){
                    selectedSampleID=sample.jobID
                    selectedSampleDate=sample.jobDate

                    callService(8)


                }
            }
            rv.adapter=samplesAdapter
        }
    }

    private fun initSampleDetails() {
        if(dialogSamples!=null&&dialogSamples?.isShowing == true){
            dialogSamples?.dismiss()
            dialogSamples=null
        }
        dialogSamples = Dialog(this@SampleHistoryActivity)
        dialogSamples?.setCancelable(false)
        val dbBinding=DbSampleDetailsBinding.inflate(layoutInflater)
        dialogSamples?.setContentView(dbBinding.root)
        var window: Window? = dialogSamples?.window
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setGravity(17)

        window?.setLayout(MATCH_PARENT, WRAP_CONTENT)
        dbBinding.rv.layoutManager=LinearLayoutManager(this@SampleHistoryActivity,LinearLayoutManager.VERTICAL, false)
        sampleDetailsAdapter=SampleDetailsAdapter(samplesDetails!!) {
            selectedSampleDetails=it
            callService(9,jobItemID=it.jobItemID)
        }
        dbBinding.rv.adapter=sampleDetailsAdapter
        dbBinding.ivClose.setOnClickListener {
            callService(7)
            dialogSamples?.dismiss()
        }
        dialogSamples?.show()

    }

    private fun setCitySpinner() {
        var cityAdapter = ArrayAdapter(this, R.layout.simple_spinner_dropdown_item, arrCitiesRelatedToRegion!!)
        binding.spnCity.adapter = cityAdapter
        newSampleSearchModels?.clear()
        for (i in arrCitiesRelatedToRegion!!.indices) {
            val city = arrCitiesRelatedToRegion!![i]
            val name = city.name
            val id = city.id
            newSampleSearchModels?.add(SampleSearchModel(name, id))
        }
        cityID = arrCitiesRelatedToRegion!![0].id
        Handler(Looper.getMainLooper()).postDelayed({
            try {
                callService(5)
            } catch (ex: Exception) {
                Toast.makeText(this@SampleHistoryActivity, "Please Wait for data to populate", Toast.LENGTH_LONG)
                    .show()
            }
        }, 600)
        binding.spnCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                (view as TextView).setTextColor(resources.getColorStateList(R.color.black))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        mDialog!!.dismiss()
        binding.spnCity.setOnTouchListener(OnTouchListener(fun(
            v: View,
            event: MotionEvent
        ): Boolean {
            if (event.action == MotionEvent.ACTION_UP) {
                arrCitiesRelatedToRegion?.clear()
                val temp: SimpleSearchDialogCompat<SampleSearchModel> =
                    SimpleSearchDialogCompat<SampleSearchModel>(this@SampleHistoryActivity,
                        "Search...",
                        "What are you looking for...?",
                        null,
                        newSampleSearchModels,
                        SearchResultListener<SampleSearchModel> { dialog, item, position ->
                            arrCitiesRelatedToRegion?.add(
                                GetCitiesResponse().City(
                                    item.id,
                                    item.title
                                )
                            )
                            cityAdapter = ArrayAdapter(
                                this@SampleHistoryActivity,
                                R.layout.simple_spinner_dropdown_item,
                                arrCitiesRelatedToRegion!!
                            )
                            binding.spnCity.adapter = cityAdapter
                            binding.rlSpnCity.visibility = View.VISIBLE
                            binding.spnCity.setOnItemSelectedListener(object :
                                AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    parent: AdapterView<*>,
                                    view: View,
                                    position: Int,
                                    id: Long
                                ) {
                                    (view as TextView).setTextColor(resources.getColorStateList(R.color.black))
                                    val selectedText = parent.getChildAt(0) as TextView
                                    if (selectedText != null) {
                                        selectedText.setTextColor(Color.BLACK)
                                    } else {
                                        selectedText.setTextColor(Color.BLACK)
                                    }
                                    cityID = arrCitiesRelatedToRegion!!.get(position).id
                                    callService(5)
                                }

                                override fun onNothingSelected(parent: AdapterView<*>?) {}
                            })
                            dialog.dismiss()
                        })
                temp.show()
                temp.titleTextView.setTextColor(Color.BLACK)
                temp.searchBox.setTextColor(resources.getColorStateList(com.app.salesforce.R.color.innercolor))

            }
            return true
        }))
    }

    private fun setAreaSpinner(mAreaResponse: GetAreasResponse) {
        mArrAreas = mAreaResponse.areas as ArrayList<GetAreasResponse.Area>?
        val adapter: ArrayAdapter<GetAreasResponse.Area> = ArrayAdapter<GetAreasResponse.Area>(
            this@SampleHistoryActivity,
            R.layout.simple_spinner_dropdown_item,
            mArrAreas!!
        )
        binding.spnArea.adapter = adapter
        binding.rlSpnArea.visibility = View.VISIBLE
        areaID = mArrAreas!![0].id
        binding.spnArea.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                (view as TextView).setTextColor(
                    resources.getColorStateList(R.color.black)
                )
                areaID = mArrAreas!![position].id
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(mDialog?.isShowing == true){
            mDialog?.hide()
        }
    }


}