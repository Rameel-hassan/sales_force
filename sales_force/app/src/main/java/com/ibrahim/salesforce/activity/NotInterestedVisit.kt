package com.app.salesforce.activity

import android.R
import android.app.Activity
import android.app.ProgressDialog
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.salesforce.databinding.ActivityNotInterestedVisitBinding
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
import com.app.salesforce.response.GetServerResponse
import com.app.salesforce.response.Region
import com.app.salesforce.response.ServerResponse
import com.app.salesforce.utilities.AppKeys
import com.app.salesforce.utilities.Utility
import io.paperdb.Paper
import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat
import ir.mirrajabi.searchdialog.core.SearchResultListener
import retrofit2.Call
import retrofit2.Callback

class NotInterestedVisit : AppCompatActivity(), ServerConnectListenerObject {


    private var mArrSchools: List<CustomersRelatedtoSO>? = null
    private var mLoginResponse: GetServerResponse? = null
    private var schoolID = -1
    private var regionID = -1
    private var cityID = -1
    private var areaID = -1
    private var adapter: ArrayAdapter<*>? = null
    private var monthsList: ArrayList<String>? = null
    private var selectedMonthPosition = 0
    private var mArrRegion: List<Region>? = null
    private var citiesList: List<Cities>? = null
    private var mDialog: ProgressDialog? = null
    private var mService: ApiService? = null
    private var arrCitiesRelatedToRegion: ArrayList<GetCitiesResponse.City>? = null
    private var newSampleSearchModels: ArrayList<SampleSearchModel>? = null
    private var mArrAreas: ArrayList<GetAreasResponse.Area>? = null
    private var isOther = false
    private var commentType: CommentType = CommentType.Late



    lateinit var binding: ActivityNotInterestedVisitBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotInterestedVisitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        newSampleSearchModels = ArrayList()
        parseBundle()
        mDialog = ProgressDialog(this@NotInterestedVisit);
        initSpinners()

        binding.btnSearch.setOnClickListener {
            if (validForm()) {
                callService(7)
            }
        }


        binding.rbLate.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.rlSpnMonth.visibility = View.VISIBLE

                commentType = CommentType.Late
                setMonthSpinner()
            }
        }
        binding.rbOther.setOnCheckedChangeListener { _, isChecked ->

            if (isChecked) {
                commentType = CommentType.Other
                binding.rlSpnMonth.visibility = View.GONE
                isOther = true
            }
        }


    }

    private fun setMonthSpinner() {
        monthsList = ArrayList<String>()
        monthsList?.add("Select Month*")
        monthsList?.add("January")
        monthsList?.add("February")
        monthsList?.add("march")
        monthsList?.add("april")
        monthsList?.add("May")
        monthsList?.add("June")
        monthsList?.add("July")
        monthsList?.add("August")
        monthsList?.add("September")
        monthsList?.add("October")
        monthsList?.add("November")
        monthsList?.add("December")
        adapter = ArrayAdapter<String>(
            this@NotInterestedVisit,
            R.layout.simple_spinner_dropdown_item,
            monthsList!!
        )
        binding.spnMonth.adapter = adapter
        selectedMonthPosition = 0
        binding.spnMonth.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedMonthPosition = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
    }

    private fun validForm(): Boolean {
        if (binding.etComment.text.toString().trim().isEmpty()) {
            Toast.makeText(
                this@NotInterestedVisit,
                "Comment shouldn't be empty.",
                Toast.LENGTH_SHORT
            ).show()
            return false
        } else if (cityID == -1 || regionID == -1 || areaID == -1 || schoolID == -1) {
            Toast.makeText(
                this@NotInterestedVisit,
                "Spinners are not selected Correctly.",
                Toast.LENGTH_SHORT
            ).show()
            return false
        } else if (!isOther && selectedMonthPosition == 0) {
            Toast.makeText(this@NotInterestedVisit, "Month not selected", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun parseBundle() {
        Paper.init(this)
        mLoginResponse = Paper.book().read<GetServerResponse>(AppKeys.KEY_LOGIN_RESPONSE)
    }

    private fun initSpinners() {
        setRegionSpinner()
        setMonthSpinner()
        setVendorSpinner()

    }

    private fun setSchoolSpinner() {
        val schoolAdapter =
            ArrayAdapter<CustomersRelatedtoSO>(
                this,
                R.layout.simple_spinner_dropdown_item,
                mArrSchools!!
            )
        binding.spnSchool.setAdapter(schoolAdapter)
        //        if (mArrEmployee.size() == 1) {
        // mSpnSalesOfficer.setEnabled(false);
        schoolID = mArrSchools?.get(0)!!.ID
        //   }
        binding.spnSchool.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    schoolID = mArrSchools?.get(position)!!.ID
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
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
                if (Utility.isNetworkAvailable(this@NotInterestedVisit)) {
                    regionID = mArrRegion?.get(position)!!.ID
                    callService(3)
                } else
                    Toast.makeText(
                        this@NotInterestedVisit,
                        "Internet not available",
                        Toast.LENGTH_SHORT
                    ).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun loadCities(position: Int) {
        Thread {
            val something = AppDataBase.getInstance(this@NotInterestedVisit)
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


    private fun callService(type: Int) {
        mService = RestClient.getInstance(this@NotInterestedVisit)
        if (type == 3) {
            mDialog?.show()
            mDialog?.setMessage("Loading Cities,please wait...")
            val userObject: Call<GetCitiesResponse> =
                mService!!.getCities(regionID, mLoginResponse!!.Data.SOID)
            val callbackObject: RestCallbackObject = RestCallbackObject(
                this@NotInterestedVisit as Activity?,
                this,
                RequestCode.GET_CITIES_REQUEST_CODE
            ).showProgress(true, 0).dontHideProgress(false)
            userObject.enqueue(callbackObject as Callback<GetCitiesResponse>)
        } else if (type == 5) {
            mDialog?.show()
            mDialog?.setMessage("Fetching areas please wait...")
            mService = RestClient.getInstance(this@NotInterestedVisit)
            val userObject: Call<GetAreasResponse> = mService!!.getAreas(
                cityID.toString(),
                mLoginResponse!!.Data.SOID
            )
            val callbackObject = RestCallbackObject(
                this@NotInterestedVisit as Activity?,
                this,
                RequestCode.GET_AREAS_FOR_CITY
            )
            callbackObject.showProgress(true, 0)
            callbackObject.dontHideProgress(false)
            userObject.enqueue(callbackObject as Callback<GetAreasResponse>)
        } else if (type == 7) {
            mDialog!!.show()
            mDialog!!.setMessage("Submitting Comment please wait...")
            mService = RestClient.getInstance(this)
            val userObject: Call<ServerResponse>? = mService?.submitComment(
                mLoginResponse!!.Data.SOID, schoolID, commentType.name,
                monthsList!![selectedMonthPosition], binding.etComment.text.toString().trim()
            )
            val callbackObject =
                RestCallbackObject(this, this, RequestCode.SUBMIT_COMMENT).showProgress(
                    true,
                    0
                ).dontHideProgress(false)
            userObject?.enqueue(callbackObject as Callback<ServerResponse>)
        } else if (type == 8) {
            mDialog!!.show()
            mDialog!!.setMessage("Loading your customers,please wait...")
            val userObject = mService?.getCustomerRelatedToSo(
                cityID, areaID, binding.spVendorType.selectedItem.toString()
            )
            val callbackObject = RestCallbackObject(
                this@NotInterestedVisit,
                this,
                RequestCode.GET_CUSTOMERS_RELATED_TO_AREA
            ).showProgress(true, 0).dontHideProgress(false)
            userObject?.enqueue(callbackObject as Callback<GetServerResponse>)
        }
    }

    override fun onFailure(error: String?, requestCode: RequestCode?) {
        if (requestCode == RequestCode.GET_AREAS_FOR_CITY) {
            Toast.makeText(this@NotInterestedVisit, "No area Associated", Toast.LENGTH_SHORT)
                .show()
            binding.rlSpnArea.visibility = View.GONE
            binding.rlSpnSchool.visibility = View.GONE
        } else if (requestCode == RequestCode.GET_CUSTOMERS_RELATED_TO_AREA) {
            binding.rlSpnSchool.visibility = View.GONE
            Toast.makeText(
                this@NotInterestedVisit,
                "Failed to get Schools for selected Area.",
                Toast.LENGTH_SHORT
            ).show()
        } else if (requestCode == RequestCode.SUBMIT_COMMENT) {
            Toast.makeText(
                this@NotInterestedVisit,
                "Failed Submit Comment.",
                Toast.LENGTH_SHORT
            ).show()
        } else if (requestCode == RequestCode.GET_CITIES_REQUEST_CODE) {
            binding.rlSpnCity.visibility = View.GONE
            binding.rlSpnSchool.visibility = View.GONE
            binding.rlSpnArea.visibility = View.GONE
            Toast.makeText(
                this@NotInterestedVisit,
                "Failed to get Cities for selected Region.",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                this@NotInterestedVisit,
                "API Failed--$requestCode$error",
                Toast.LENGTH_SHORT
            ).show()
        }
        mDialog?.hide()
    }

    override fun onSuccess(`object`: Any?, requestCode: RequestCode?) {
        if (requestCode == RequestCode.GET_CITIES_REQUEST_CODE) {
            if ((`object` as GetCitiesResponse).cities == null || `object`.cities.isEmpty()) {
                binding.rlSpnCity.visibility = View.GONE
                binding.rlSpnArea.visibility = View.GONE
                binding.rlSpnSchool.visibility = View.GONE
                Toast.makeText(
                    this@NotInterestedVisit,
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
                binding.rlSpnArea.visibility = View.GONE
                binding.rlSpnSchool.visibility = View.GONE
                Toast.makeText(
                    this@NotInterestedVisit,
                    "There are no areas associated",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                binding.rlSpnArea.visibility = View.VISIBLE
                setAreaSpinner(mNewAreaResponse)
            }
        } else if (requestCode == RequestCode.GET_CUSTOMERS_RELATED_TO_AREA) {
            val customersRelatedtoSO = (`object` as GetServerResponse).CustomersRelatedtoSO
            if (customersRelatedtoSO != null && customersRelatedtoSO.isNotEmpty()) {
                binding.rlSpnSchool.visibility = View.VISIBLE
                mLoginResponse!!.Data.CustomersRelatedtoSO = customersRelatedtoSO
                mArrSchools = customersRelatedtoSO
                Paper.book().write(AppKeys.KEY_LOGIN_RESPONSE, mLoginResponse)
                setSchoolSpinner()
                //hassan Usman Test 2
            } else {
                binding.rlSpnSchool.visibility = View.GONE
                Toast.makeText(
                    this@NotInterestedVisit,
                    "No Schools associated to the selected Area.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else if (requestCode == RequestCode.SUBMIT_COMMENT) {
            var res = (`object` as ServerResponse)
            if (res.resultType == 1) {
                Toast.makeText(
                    this@NotInterestedVisit,
                    "Comment Submitted Successfully.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            } else {
                Toast.makeText(this@NotInterestedVisit, res.message, Toast.LENGTH_SHORT).show()

            }
        }
        mDialog?.hide()
    }

    private fun setCitySpinner() {
        var cityAdapter =
            ArrayAdapter(this, R.layout.simple_spinner_dropdown_item, arrCitiesRelatedToRegion!!)
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
                Toast.makeText(
                    this@NotInterestedVisit,
                    "Please Wait for data to populate",
                    Toast.LENGTH_LONG
                )
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
        binding.spnCity.setOnTouchListener(View.OnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                arrCitiesRelatedToRegion?.clear()
                val temp: SimpleSearchDialogCompat<SampleSearchModel> =
                    SimpleSearchDialogCompat<SampleSearchModel>(this@NotInterestedVisit,
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
                                this@NotInterestedVisit,
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
            true
        })
    }

    private fun setAreaSpinner(mAreaResponse: GetAreasResponse) {
        mArrAreas = mAreaResponse.areas as ArrayList<GetAreasResponse.Area>?
        val adapter: ArrayAdapter<GetAreasResponse.Area> = ArrayAdapter<GetAreasResponse.Area>(
            this@NotInterestedVisit,
            R.layout.simple_spinner_dropdown_item,
            mArrAreas!!
        )
        binding.spnArea.adapter = adapter
        binding.rlSpnArea.visibility = View.VISIBLE
        areaID = mArrAreas!![0].id
      /*  Handler(Looper.getMainLooper()).postDelayed({
            try {
                callService(8)
            } catch (ex: Exception) {
                Toast.makeText(
                    this@NotInterestedVisit,
                    "Please Wait for data to populate",
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        }, 600)*/
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
                callService(8)

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setVendorSpinner() {
        val vendors = ArrayList<String>()
        vendors.add("School")
        vendors.add("Shop")
        // Create an ArrayAdapter using the string array and a default spinner layout
        val adapter = ArrayAdapter<String>(this, R.layout.simple_spinner_item, vendors
        )

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)

        // Set the ArrayAdapter to the Spinner
        binding.spVendorType.setAdapter(adapter)

        // Set the default selected item to "School"
        binding.spVendorType.setSelection(0)

    }
    override fun onBackPressed() {
        super.onBackPressed()
        if (mDialog?.isShowing == true) {
            mDialog?.hide()
        }
    }

    enum class CommentType(name: String) {
        Late("Late"), Other("Other")
    }


}