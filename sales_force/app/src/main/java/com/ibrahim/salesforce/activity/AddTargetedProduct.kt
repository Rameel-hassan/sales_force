package com.app.salesforce.activity

import android.R
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.salesforce.adapters.AdapterShowProducts
import com.app.salesforce.adapters.AssignProductsAdapter
import com.app.salesforce.adapters.SampleDetailsAdapter
import com.app.salesforce.adapters.SubjectsAndClassesAdapter
import com.app.salesforce.adapters.TargetedSchoolsAdapter
import com.app.salesforce.databinding.ActivityAddTargetedSchoolBinding
import com.app.salesforce.databinding.DbAssignProductBinding
import com.app.salesforce.databinding.DbShowProductsBinding
import com.app.salesforce.model.SampleSearchModel
import com.app.salesforce.model.SubjectAndClassesModel
import com.app.salesforce.network.ApiService
import com.app.salesforce.network.RequestCode
import com.app.salesforce.network.RestCallbackObject
import com.app.salesforce.network.RestClient
import com.app.salesforce.network.ServerCodes
import com.app.salesforce.network.ServerConnectListenerObject
import com.app.salesforce.offline.AppDataBase
import com.app.salesforce.offline.Cities
import com.app.salesforce.request.AssignTargatedProduct
import com.app.salesforce.response.CustomersRelatedtoSO
import com.app.salesforce.response.GetAreasResponse
import com.app.salesforce.response.GetCitiesResponse
import com.app.salesforce.response.GetSamplesHistoryResponse
import com.app.salesforce.response.GetServerResponse
import com.app.salesforce.response.Region
import com.app.salesforce.response.SalesOfficer
import com.app.salesforce.response.ServerResponse
import com.app.salesforce.response.Sery
import com.app.salesforce.response.TargatedProducts
import com.app.salesforce.response.TargatedSchoolList
import com.app.salesforce.utilities.AppBundles
import com.app.salesforce.utilities.AppKeys
import com.app.salesforce.utilities.Utility
import io.paperdb.Paper
import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat
import ir.mirrajabi.searchdialog.core.SearchResultListener
import retrofit2.Call
import retrofit2.Callback
import java.util.Calendar

class AddTargetedProduct : AppCompatActivity(), ServerConnectListenerObject {


    private val startDateCalendar = Calendar.getInstance()
    private var mArrEmployee: List<SalesOfficer>? = null

    private var selectedSeries: Sery? = null
    private var regionID = -1
    private var cityID = -1
    private var areaID = -1
    private var selectedSampleDetails: GetSamplesHistoryResponse.SampledDetails? = null
    private var dialogAssignProduct: Dialog? = null
    private var dbBinding: DbAssignProductBinding? = null
    private var selectedSampleDate = ""
    private var selectedSampleID = 0;
    private var areaSearch: Int = 0
    private var mArrRegion: List<Region>? = null
    private var citiesList: List<Cities>? = null
    private var mDialog: ProgressDialog? = null
    private var mService: ApiService? = null
    private var arrCitiesRelatedToRegion: ArrayList<GetCitiesResponse.City>? = null
    private var newSampleSearchModels: ArrayList<SampleSearchModel>? = null
    private var mArrAreas: ArrayList<GetAreasResponse.Area>? = null
    private var samples: ArrayList<TargatedSchoolList.TargetedSchool>? = null
    private var samplesDetails: ArrayList<GetSamplesHistoryResponse.SampledDetails>? = null

    private var samplesAdapter: TargetedSchoolsAdapter? = null
    private var sampleDetailsAdapter: SampleDetailsAdapter? = null

    lateinit var binding: ActivityAddTargetedSchoolBinding
    private var mLoginResponse: GetServerResponse? = null
    private var mCustomerObj: CustomersRelatedtoSO? = null
    private var shopName: String? = null
    private var activityType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTargetedSchoolBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        newSampleSearchModels = ArrayList()
        parseBundle()
        mArrEmployee = mLoginResponse?.Data?.SalesOfficer
        mDialog = ProgressDialog(this@AddTargetedProduct);
        initSpinners()
        binding.btnSearch.setOnClickListener {

            callService(7)

        }

    }

    private fun parseBundle() {
        Paper.init(this)
        mLoginResponse = Paper.book().read<GetServerResponse>(AppKeys.KEY_LOGIN_RESPONSE)
        if (intent != null) {

            activityType = intent.getStringExtra(AppBundles.BUNDLE_ORDER)
//            regionID = intent.getIntExtra("region", 0);
//            cityID = intent.getIntExtra("city", 0);
//            areaID = intent.getIntExtra("area", 0);
//            areaSearch = intent.getIntExtra("areaId", 0)


        }
    }


    private fun initSpinners() {

        setRegionSpinner()
        setWorkingPrioritySpinner()
    }

    private var workingPriority = "Low"
    private fun setWorkingPrioritySpinner() {
        val adapter = ArrayAdapter.createFromResource(
            this@AddTargetedProduct,
            com.app.salesforce.R.array.working_priority, R.layout.simple_spinner_dropdown_item
        )
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spnPriority.adapter = adapter
        binding.spnPriority.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                workingPriority = parent?.getItemAtPosition(position)?.toString().toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
    }

    private fun setRegionSpinner() {
        mArrRegion = mLoginResponse!!.Data.Region

        val adapter: ArrayAdapter<Region> =
            ArrayAdapter<Region>(this, R.layout.simple_spinner_dropdown_item, mArrRegion!!)
        binding.spnRegion.adapter = adapter
        // binding.spnRegion.setSelection(regionID)
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
                if (Utility.isNetworkAvailable(this@AddTargetedProduct)) {
                    regionID = mArrRegion?.get(position)!!.ID
                    callService(3)
                } else
                    Toast.makeText(
                        this@AddTargetedProduct,
                        "Internet not available",
                        Toast.LENGTH_SHORT
                    ).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun loadCities(position: Int) {
        Thread {
            val something = AppDataBase.getInstance(this@AddTargetedProduct)
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


    private fun callService(type: Int, jobItemID: Int = -1) {
        mService = RestClient.getInstance(this@AddTargetedProduct)
        if (type == 3) {
            mDialog?.show()
            mDialog?.setMessage("Loading Cities,please wait...")
            val userObject: Call<GetCitiesResponse> =
                mService!!.getCities(regionID, mLoginResponse!!.Data.SOID)
            val callbackObject: RestCallbackObject = RestCallbackObject(
                this@AddTargetedProduct as Activity?,
                this,
                RequestCode.GET_CITIES_REQUEST_CODE
            ).showProgress(true, 0).dontHideProgress(false)
            userObject.enqueue(callbackObject as Callback<GetCitiesResponse>)
        } else if (type == 5) {
            mDialog?.show()
            mDialog?.setMessage("Fetching areas please wait...")
            mService = RestClient.getInstance(this@AddTargetedProduct)
            val userObject: Call<GetAreasResponse> = mService!!.getAreas(
                cityID.toString(),
                mLoginResponse!!.Data.SOID
            )
            val callbackObject = RestCallbackObject(
                this@AddTargetedProduct as Activity?,
                this,
                RequestCode.GET_AREAS_FOR_CITY
            )
            callbackObject.showProgress(true, 0)
            callbackObject.dontHideProgress(false)
            userObject.enqueue(callbackObject as Callback<GetAreasResponse>)
        } else if (type == 7) {
            mDialog!!.show()
            mDialog!!.setMessage("Fetching Schools please wait...")
            mService = RestClient.getInstance(this)

            val userObject: Call<TargatedSchoolList>? = mService?.getTargetedSchoolList(
                areaSearch, workingPriority
            )
            val callbackObject =
                RestCallbackObject(this, this, RequestCode.GET_TARGETED_SCHOOLS).showProgress(
                    true,
                    0
                ).dontHideProgress(false)
            userObject?.enqueue(callbackObject as Callback<TargatedSchoolList>)
        } else if (type == 8) {
            mDialog!!.show()
            mDialog!!.setMessage("Fetching School Details please wait...")
            mService = RestClient.getInstance(this@AddTargetedProduct)
            val userObject: Call<GetSamplesHistoryResponse>? =
                mService?.getSampleDetails(selectedSampleID)
            val callbackObject =
                RestCallbackObject(
                    this@AddTargetedProduct,
                    this,
                    RequestCode.GET_SAMPLES_HISTORY_DETAILS
                ).showProgress(
                    true,
                    0
                ).dontHideProgress(false)
            userObject?.enqueue(callbackObject as Callback<GetSamplesHistoryResponse>)
        } else if (type == 9) {
            mDialog!!.show()
            mDialog!!.setMessage("Removing Sample please wait...")
            mService = RestClient.getInstance(this@AddTargetedProduct)
            val userObject: Call<GetSamplesHistoryResponse>? =
                mService?.removeSampleDemand(selectedSampleDetails!!.jobItemID)
            val callbackObject =
                RestCallbackObject(
                    this@AddTargetedProduct,
                    this,
                    RequestCode.REMOVE_SAMPLE_DEMAND
                ).showProgress(
                    true,
                    0
                ).dontHideProgress(false)
            userObject?.enqueue(callbackObject as Callback<GetSamplesHistoryResponse>)
        }else if(type==92){
            mDialog!!.show()
            mDialog!!.setMessage("Loading Targeted Products...")
            mService = RestClient.getInstance(this@AddTargetedProduct)
            val userObject: Call<TargatedProducts>? =
                mService?.getTargetedProducts(targetedSchool.ID)
            val callbackObject =
                RestCallbackObject(
                    this@AddTargetedProduct,
                    this,
                    RequestCode.GET_TARGETED_PRODUCTS
                ).showProgress(
                    true,
                    0
                ).dontHideProgress(false)
            userObject?.enqueue(callbackObject as Callback<TargatedProducts>)
        }
    }

    override fun onFailure(error: String?, requestCode: RequestCode?) {
        if (requestCode == RequestCode.GET_AREAS_FOR_CITY) {
            Toast.makeText(this@AddTargetedProduct, "No area Associated", Toast.LENGTH_SHORT)
                .show()
            binding.spnArea.visibility = View.GONE
        } else if (requestCode == RequestCode.GET_SAMPLES_HISTORY) {
            Toast.makeText(
                this@AddTargetedProduct,
                "No Samples found.",
                Toast.LENGTH_SHORT
            ).show()

        } else if (requestCode == RequestCode.GET_SAMPLES_HISTORY_DETAILS) {
            Toast.makeText(
                this@AddTargetedProduct,
                "No Samples Found",
                Toast.LENGTH_SHORT
            ).show()
        } else if (requestCode == RequestCode.REMOVE_SAMPLE_DEMAND) {
            Toast.makeText(
                this@AddTargetedProduct,
                "Failed to Remove Sample.",
                Toast.LENGTH_SHORT
            ).show()
        } else if (requestCode == RequestCode.GET_TARGETED_SCHOOLS) {
            Toast.makeText(
                this@AddTargetedProduct,
                "Failed to Get Schools.${error}",
                Toast.LENGTH_SHORT
            ).show()
        } else if (requestCode == RequestCode.GET_SUBJECTS_SERIES_WISE) {
            if (mDialog!!.isShowing) {
                mDialog!!.dismiss()
            }
            dbBinding?.tvNoSubjects?.visibility = View.VISIBLE
            dbBinding?.llTable?.visibility = View.INVISIBLE
            dbBinding?.llTableHead?.visibility = View.INVISIBLE
            Toast.makeText(
                this@AddTargetedProduct,
                "Failed to Get Subjects.",
                Toast.LENGTH_SHORT
            ).show()
            Log.d("Response: ", error!!)
        } else if(requestCode==RequestCode.ASSIGN_PRODUCTS){
            if (mDialog!!.isShowing) {
                mDialog!!.dismiss()
            }
            dbBinding?.btnAssignProducts?.setEnabled(true)
            Log.d("Response: ", error!!)
            Toast.makeText(this, "Failed-- $error", Toast.LENGTH_SHORT).show()

        }else if(requestCode== RequestCode.GET_TARGETED_PRODUCTS){
            if (mDialog!!.isShowing) {
                mDialog!!.dismiss()
            }
            dbBinding?.btnAssignProducts?.setEnabled(true)
            Log.d("Response: ", error!!)
            Toast.makeText(this, "Failed-- $error", Toast.LENGTH_SHORT).show()

        }else{
            Toast.makeText(
                this@AddTargetedProduct,
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
                    this@AddTargetedProduct,
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
                    this@AddTargetedProduct,
                    "There are no areas associated",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                binding.spnArea.visibility = View.VISIBLE
                binding.rlSpnArea.visibility = View.VISIBLE
                setAreaSpinner(mNewAreaResponse)
            }
        } else if (requestCode == RequestCode.GET_SUBJECTS_SERIES_WISE) {
            val SubjectsRelatedToSeries = (`object` as GetServerResponse).SubjectsRelatedToSeries
            if (SubjectsRelatedToSeries != null) {
                //make table visible
                subjectsAndClassesToSelect.clear()
                for (subject in SubjectsRelatedToSeries) {
                    subjectsAndClassesToSelect.add(SubjectAndClassesModel(subject))
                }
                if (SubjectsRelatedToSeries.isEmpty()) {
                    subjectsAndClassesToSelect.clear()
                }

                populateTable()
            } else {
                //hide table
                Toast.makeText(
                    applicationContext,
                    "There are no Books related with this Series.",
                    Toast.LENGTH_LONG
                ).show()

                dbBinding?.tvNoSubjects?.visibility = View.VISIBLE
                dbBinding?.llTableHead?.visibility = View.INVISIBLE
                dbBinding?.llTable?.visibility = View.INVISIBLE
            }
            if (mDialog!!.isShowing) {
                mDialog!!.hide()
            }
        } else if (requestCode == RequestCode.GET_TARGETED_SCHOOLS) {
            samples =
                (`object` as TargatedSchoolList).targetedSchoolsList as ArrayList<TargatedSchoolList.TargetedSchool>?
            mDialog!!.dismiss()

            var viewVisibility: Int
            var assignProductVisibility: Int
            if(mLoginResponse?.Data?.IsRegionalHead==true){
                viewVisibility=View.VISIBLE
                assignProductVisibility=View.VISIBLE
            }else{
                viewVisibility=View.VISIBLE
                assignProductVisibility=View.GONE
            }
//            viewVisibility = View.VISIBLE
//            assignProductVisibility = View.VISIBLE

            if (samples != null && samples?.isNotEmpty() == true) {
                for (i in 0 until samples!!.size) {
                    samples!![i].srNo = i + 1
                    samples!![i].viewVisibility = viewVisibility
                    samples!![i].assignProductVisibility = assignProductVisibility
                }
                initSamplesAdapter()
                binding.tvNoSamples.visibility = View.GONE
                binding.rv.visibility = View.VISIBLE
                binding.tableHeading.visibility = View.VISIBLE
            } else {

                binding.tvNoSamples.visibility = View.VISIBLE
                binding.rv.visibility = View.GONE
                binding.tableHeading.visibility = View.GONE
            }
        }else if(requestCode==RequestCode.ASSIGN_PRODUCTS){
            val serverResponse = `object` as ServerResponse
            mDialog!!.dismiss()
            if (serverResponse.resultType == ServerCodes.ServerResultType.SERVER_RESULT_SUCCESS) {
                Toast.makeText(
                    this,
                    serverResponse.message,
                    Toast.LENGTH_LONG
                ).show()
                val index=samples!!.indexOf(targetedSchool)
                samples!![index].isProductsAssigned=true
                dialogAssignProduct?.cancel()
                samplesAdapter?.notifyDataSetChanged()
            }
            else
                Toast.makeText(this, serverResponse.message, Toast.LENGTH_LONG).show()


        }else if(requestCode==RequestCode.GET_TARGETED_PRODUCTS){
            val targetedProducts = `object` as TargatedProducts
            mDialog!!.dismiss()
            if(targetedProducts.targatedProductsList?.isEmpty()==true){
                Toast.makeText(this@AddTargetedProduct,"No Products found",
                Toast.LENGTH_SHORT).show()
            }else{
                popupToShowTargetedProducts(targetedProducts.targatedProductsList)
            }
        }

        if(mDialog!!.isShowing){
            mDialog!!.dismiss()
        }
    }

    private fun popupToShowTargetedProducts(targatedProductsList: List<TargatedProducts.TargetedProducts>?) {
        if (dialogAssignProduct != null && dialogAssignProduct?.isShowing == true) {
            dialogAssignProduct?.dismiss()
            dialogAssignProduct = null
        }
        dialogAssignProduct = Dialog(this@AddTargetedProduct)
        dialogAssignProduct?.setCancelable(true)
        val bind = DbShowProductsBinding.inflate(layoutInflater)
        dialogAssignProduct?.setContentView(bind.root)
        var window: Window? = dialogAssignProduct?.window
        window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        window?.setGravity(17)

        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)


        bind.title.text="View Products Assigned to "+targetedSchool.Name

        bind.rvSelected.layoutManager=LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL,false)
        val adapter=AdapterShowProducts(targatedProductsList as ArrayList<TargatedProducts.TargetedProducts>)
        bind.rvSelected.adapter=adapter
        bind.btnOk.setOnClickListener {
            dialogAssignProduct?.dismiss()
        }
        dialogAssignProduct?.show()
    }

    private val subjectsAndClassesToSelect = java.util.ArrayList<SubjectAndClassesModel>()
    private lateinit var subjectsAndClassesAdapter: SubjectsAndClassesAdapter
    private fun populateTable() {

        dbBinding?.rvSubjClass?.setLayoutManager(
            LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false
            )
        )
        subjectsAndClassesAdapter = SubjectsAndClassesAdapter(
            subjectsAndClassesToSelect, this
        ) { (ID, SubjectName), (ClassName, _, ClassID), isChecked, absoluteAdapterPosition ->
            addRemoveAssignProduct(
                ProductToAssign
                    (
                    ClassID,
                    ClassName,
                    ID,
                    SubjectName,
                    selectedSeries!!,
                ), isChecked
            )
        }

        dbBinding?.rvSubjClass?.setAdapter(subjectsAndClassesAdapter)

        dbBinding?.tvNoSubjects?.visibility = View.GONE
        dbBinding?.llTable?.visibility = View.VISIBLE
        dbBinding?.llTableHead?.visibility = View.VISIBLE
        dbBinding?.tvSelectedProductsTitle?.visibility = View.VISIBLE
        dbBinding?.rvSelected?.visibility = View.VISIBLE
        dbBinding?.llSelectedProducts?.visibility = View.VISIBLE
    }

    private var selectedProductsAdapter: AssignProductsAdapter? = null
    private var selectedProducts: ArrayList<ProductToAssign>? = null

    private fun addRemoveAssignProduct(
        item: ProductToAssign,
        checked: Boolean,
    ) {

        if (selectedProductsAdapter == null) {
            selectedProducts = ArrayList()
            selectedProducts?.add(
                item
            )
            dbBinding?.rvSelected?.layoutManager = LinearLayoutManager(this)

            selectedProductsAdapter = AssignProductsAdapter(
                selectedProducts!!
            ) { isChecked: Boolean, toRemove: ProductToAssign ->
                var index = selectedProducts?.indexOf(toRemove)
                selectedProducts?.remove(toRemove)
                selectedProductsAdapter?.notifyItemRemoved(index!!)

                if (selectedSeries?.SeriesName == toRemove.SelectedSeries.SeriesName) {
                    for (subj in subjectsAndClassesToSelect) {
                        if (subj.subject.SubjectName == toRemove.SubjectName) {
                            for (clas in subj.classes) {
                                if (clas.ClassID == toRemove.ClassID &&
                                    clas.ClassName == toRemove.ClassName
                                ) {
                                    clas.isSelected = false
                                    subjectsAndClassesAdapter.notifyItemChanged(
                                        subjectsAndClassesToSelect.indexOf(subj)
                                    )
                                }
                            }
                        }
                    }
                }

                if (selectedProducts?.isEmpty() == true) {
                    dbBinding?.tvSelectedProductsTitle?.visibility = View.GONE
                    dbBinding?.rvSelected?.visibility = View.GONE
                    dbBinding?.llSelectedProducts?.visibility = View.GONE
                    Toast.makeText(
                        this@AddTargetedProduct,
                        "No Products are selected.",
                        Toast.LENGTH_SHORT
                    ).show()
                }


            }
            dbBinding?.rvSelected?.setAdapter(selectedProductsAdapter)
        } else if (checked) {
            if (selectedProducts!!.size > 0) {
                var isPresent = false

                if (item in selectedProducts!!) {
                    Toast.makeText(
                        applicationContext,
                        "Class, Subject and Series Already Exist",
                        Toast.LENGTH_LONG
                    ).show()
                    isPresent = true
                }

                if (!isPresent) {
                    selectedProducts?.add(
                        item
                    )
                    selectedProductsAdapter?.notifyItemInserted(selectedProducts!!.size)
                }
            } else {
                selectedProducts?.add(
                    item
                )
                selectedProductsAdapter?.notifyItemInserted(selectedProducts!!.size)
            }
        } else {
            if (item in selectedProducts!!) {
                var index = selectedProducts?.indexOf(item)
                selectedProducts?.remove(item)
                selectedProductsAdapter?.notifyItemRemoved(index!!)
            }
        }

        if (selectedProducts?.isEmpty() == true) {
            dbBinding?.tvSelectedProductsTitle?.visibility = View.GONE
            dbBinding?.rvSelected?.visibility = View.GONE
            dbBinding?.llSelectedProducts?.visibility = View.GONE
            Toast.makeText(this@AddTargetedProduct, "No Products are selected.", Toast.LENGTH_SHORT)
                .show()
        } else {
            dbBinding?.tvSelectedProductsTitle?.visibility = View.VISIBLE
            dbBinding?.rvSelected?.visibility = View.VISIBLE
            dbBinding?.llSelectedProducts?.visibility = View.VISIBLE
        }
    }

    data class ProductToAssign(
        val ClassID: Int, val ClassName: String,
        val subjectId: Int, val SubjectName: String, val SelectedSeries: Sery
    )

    private lateinit var targetedSchool: TargatedSchoolList.TargetedSchool
    private fun initSamplesAdapter() {

        binding.rv.layoutManager =
            LinearLayoutManager(this@AddTargetedProduct, LinearLayoutManager.VERTICAL, false)
        samplesAdapter =
            TargetedSchoolsAdapter(samples!!) { isToAssign: Boolean, sample: TargatedSchoolList.TargetedSchool ->
                targetedSchool = sample
                if (isToAssign) {
                    //TODO Assign Popup

                    popUpToAssignProduct()
                } else {
                    //TODO view
                    callService(92)
                }
            }
        binding.rv.adapter = samplesAdapter

    }

    private fun popUpToAssignProduct() {

        subjectsAndClassesToSelect.clear()
        selectedProducts?.clear()
        selectedProductsAdapter=null



        if (dialogAssignProduct != null && dialogAssignProduct?.isShowing == true) {
            dialogAssignProduct?.dismiss()
            dialogAssignProduct = null
        }
        dialogAssignProduct = Dialog(this@AddTargetedProduct)
        dialogAssignProduct?.setCancelable(true)
        dbBinding = DbAssignProductBinding.inflate(layoutInflater)
        dialogAssignProduct?.setContentView(dbBinding!!.root)
        var window: Window? = dialogAssignProduct?.window
        window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        window?.setGravity(17)

        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val arrSeriesList = ArrayList(mLoginResponse!!.Data.Series )
        arrSeriesList.add(0, Sery(0, "Select Series"))
        var seriesAdapter =
            ArrayAdapter<Sery>(this, R.layout.simple_spinner_dropdown_item, arrSeriesList)
        dbBinding?.subject?.adapter = seriesAdapter
        val seriesSampleSearchModels = ArrayList<SampleSearchModel>()
        seriesSampleSearchModels.clear()
        for (i in 1 until arrSeriesList.size) {
            val (id, name) = arrSeriesList[i]
            seriesSampleSearchModels.add(SampleSearchModel(name, id))
        }
        mDialog!!.dismiss()


        dbBinding?.subject?.setOnTouchListener(OnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                arrSeriesList.clear()
                SimpleSearchDialogCompat(this@AddTargetedProduct, "Search...",
                    "What are you looking for...?", null, seriesSampleSearchModels,
                    SearchResultListener<SampleSearchModel> { dialog, item, position ->
                        arrSeriesList.add(Sery(item.id, item.title))
                        seriesAdapter = ArrayAdapter<Sery>(
                            this@AddTargetedProduct,
                            R.layout.simple_spinner_dropdown_item,
                            arrSeriesList
                        )
                        dbBinding?.subject?.setAdapter(seriesAdapter)
                        dbBinding?.subject?.setVisibility(View.VISIBLE)
                        dbBinding?.subject?.setOnItemSelectedListener(object :
                            AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View,
                                position: Int,
                                id: Long
                            ) {
                                if (arrSeriesList[0].ID != 0) {
                                    selectedSeries = arrSeriesList[position]

                                    //listOfClasses.setLayoutManager(mLayoutManager);
                                    //  classRangeAdapter = new ClassRangeAdapter(arrClassList, view.getContext(), ReportsActivity.this, selectedSeries, arrSelectedPublishers);
                                    //listOfClasses.setAdapter(classRangeAdapter);
                                    loadBooks(selectedSeries!!.ID)
                                    //classRangeAdapter.notifyDataSetChanged();
                                } else {
                                    //currentPubItem.setVisibility(View.GONE);
                                    //classRangeItem.setVisibility(View.GONE);
                                    //interestedBooksItem.setVisibility(View.GONE);
                                }
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {}
                        })
                        dialog.dismiss()
                    }).show()
            }
            true
        })

        dbBinding?.btnAssignProducts?.setOnClickListener {
            mDialog!!.show()
            mDialog!!.setMessage("Submitting your Selected Products, please wait...")
            dbBinding?.btnAssignProducts?.setEnabled(false)
            mService = RestClient.getInstance(this)

            //list of items to send
            val listOfTargetedProducts: AssignTargatedProduct = AssignTargatedProduct()
            val ret: AssignTargatedProduct.RetailerProducts =
                AssignTargatedProduct.RetailerProducts()
            val listOfProducts = ArrayList<AssignTargatedProduct.Products>()
            for (products in selectedProducts!!) {
                val product = AssignTargatedProduct.Products()
                product?.ClassId = products.ClassID
                product?.ProductId = products.subjectId
                product?.SeriesId = products.SelectedSeries.ID
                listOfProducts.add(
                    product
                )
            }
            ret.ProductList=listOfProducts
            ret.RetailerID=targetedSchool.ID
            //listOfTargetedProducts.assignProducts= listOf(ret)
            val userObject = mService?.assignTargatedProducts(listOf(ret))
            val callbackObject = RestCallbackObject(this, this, RequestCode.ASSIGN_PRODUCTS)
                .showProgress(true, 0).dontHideProgress(false)
            userObject?.enqueue(callbackObject as Callback<ServerResponse>)
        }

        dialogAssignProduct?.show()

    }

    private fun loadBooks(id: Int) {
        mDialog!!.show()
        mDialog!!.setMessage("Getting Books, please wait...")
        mService = RestClient.getInstance(this)
        val userObject = mService?.getSubjectsSeriesWise(id)
        val callbackObject =
            RestCallbackObject(this, this, RequestCode.GET_SUBJECTS_SERIES_WISE)
                .showProgress(true, 0)
                .dontHideProgress(false)
        userObject?.enqueue(callbackObject as Callback<GetServerResponse>)
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
        // binding.spnCity.setSelection(cityID)
        binding.rlSpnCity.visibility=View.VISIBLE
        binding.spnCity.visibility=View.VISIBLE
        cityID = arrCitiesRelatedToRegion!![0].id
        Handler(Looper.getMainLooper()).postDelayed({
            try {
                callService(5)
            } catch (ex: Exception) {
                Toast.makeText(
                    this@AddTargetedProduct,
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
        binding.spnCity.setOnTouchListener(View.OnTouchListener(fun(
            v: View,
            event: MotionEvent
        ): Boolean {
            if (event.action == MotionEvent.ACTION_UP) {
                arrCitiesRelatedToRegion?.clear()
                val temp: SimpleSearchDialogCompat<SampleSearchModel> =
                    SimpleSearchDialogCompat<SampleSearchModel>(this@AddTargetedProduct,
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
                                this@AddTargetedProduct,
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
            this@AddTargetedProduct,
            R.layout.simple_spinner_dropdown_item,
            mArrAreas!!
        )
        binding.spnArea.visibility = View.VISIBLE
        binding.spnArea.adapter = adapter
        binding.rlSpnArea.visibility = View.VISIBLE
        // binding.spnArea.setSelection(areaID)
        areaID = mArrAreas!![0].id
        //callService(7)
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
                areaSearch = mArrAreas!![position].id
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (mDialog?.isShowing == true) {
            mDialog?.hide()
        }
    }




}
