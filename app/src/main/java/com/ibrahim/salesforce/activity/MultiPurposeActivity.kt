package com.ibrahim.salesforce.activity

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ibrahim.salesforce.databinding.ActivityMultiPurposeLayoutBinding
import com.ibrahim.salesforce.network.RequestCode
import com.ibrahim.salesforce.network.RestService
import com.ibrahim.salesforce.response.ActivityPurpose
import com.ibrahim.salesforce.response.CustomersRelatedtoSO
import com.ibrahim.salesforce.response.GetServerResponse
import com.ibrahim.salesforce.response.SalesOfficer
import com.ibrahim.salesforce.utilities.AppBundles
import com.ibrahim.salesforce.utilities.AppKeys
import io.paperdb.Paper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class MultiPurposeActivity : AppCompatActivity() {

    private var mCustomerObj: CustomersRelatedtoSO? = null
    private lateinit var mLoginResponse: GetServerResponse
    private var shopName: String? = null
    private lateinit var mDialog: ProgressDialog
    private lateinit var adapter: ArrayAdapter<SalesOfficer>
    private lateinit var visitAdapter: ArrayAdapter<ActivityPurpose>
    private lateinit var mArrEmployee: List<SalesOfficer>
    private lateinit var mArrVisitPurpose: List<ActivityPurpose>
    private var mArrIDsToSend: MutableList<Int> = arrayListOf()
    private lateinit var mArrDealers: List<CustomersRelatedtoSO>
    private var soPosition: Int = 0
    private var visitPosition: Int = 0
    private var SOID: Int = 0
    private var noOfSchools: Int = 0
    private lateinit var disposable: Disposable
    private lateinit var visitType: String
    private var dealerID: Int? = null
    private var retailerID: Int? = null
    private var multiVisitPurpose: String = ""
    private val restService by lazy { RestService.create() }
    lateinit var binding:ActivityMultiPurposeLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMultiPurposeLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("class_name", this.javaClass.simpleName)
        parseBundle()
        setSaleOfficerSpinner()
        setVisitPurposeSpinner()

    }

    private fun parseBundle() {
        Paper.init(this)
        mArrVisitPurpose = ArrayList()
        mArrEmployee = ArrayList()
        mArrDealers = ArrayList()
        mDialog = ProgressDialog(this)
        mDialog.setCancelable(false)
        mLoginResponse = Paper.book().read<GetServerResponse>(AppKeys.KEY_LOGIN_RESPONSE)
        SOID = mLoginResponse.Data.SOID
        mArrEmployee = mLoginResponse.Data.SalesOfficer


        if (intent != null) {
            val args = intent.getBundleExtra("BUNDLE")
            visitType = args!!.getString(AppBundles.BUNDLE_VISIT_TYPE)!!
            mCustomerObj = args.getSerializable(AppBundles.BUNDLE_CUSTOMER_OBJ) as CustomersRelatedtoSO?
            if (visitType == "Dealer Visits") {
                binding.spnVisitPurpose.visibility = View.GONE
                mArrDealers = args.getSerializable("ARRAYLIST") as ArrayList<CustomersRelatedtoSO>
            } else {
                if (visitType == "Events") {
                    mArrVisitPurpose = mLoginResponse.Data.Events
                } else {
                    mArrVisitPurpose = mLoginResponse.Data.CombinedVisits
                }
                if (mCustomerObj != null) {
                    retailerID = mCustomerObj!!.ID
                    shopName = mCustomerObj!!.ShopName
                }
            }
        }
    }

    private fun setSaleOfficerSpinner() {
        adapter = ArrayAdapter<SalesOfficer>(this, android.R.layout.simple_spinner_item, mArrEmployee)
        binding.spnSaleOfficer.adapter = adapter
        if (mArrEmployee.size == 1)
            binding.spnSaleOfficer.isEnabled = false
        binding.spnSaleOfficer.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                soPosition = position
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setVisitPurposeSpinner() {
        visitAdapter = ArrayAdapter<ActivityPurpose>(this, android.R.layout.simple_spinner_item, mArrVisitPurpose)
        binding.spnVisitPurpose.adapter = visitAdapter
        if (mArrVisitPurpose.size == 1)
            binding.spnVisitPurpose.isEnabled = false
        binding.spnVisitPurpose.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                binding.etNoOfSchools.visibility = View.GONE
                visitPosition = position
                if (mArrVisitPurpose.get(visitPosition).ID == 9) {
                    binding.etNoOfSchools.visibility = View.VISIBLE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun showResult(response: GetServerResponse, requestCode: RequestCode) {

        mDialog.dismiss()
        Toast.makeText(this, response.Message, Toast.LENGTH_LONG).show()
        this.onBackPressed()
        mDialog.dismiss()
    }

    private fun showError(message: String?) {
        mDialog.dismiss()
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        this.onBackPressed()

    }

    fun postData(view: View) {
        if (visitType != "Dealer Visits") {
            multiVisitPurpose = mArrVisitPurpose[visitPosition].Name
        }
        if (!binding.etNoOfSchools.text.toString().isNullOrEmpty()) {
            noOfSchools = binding.etNoOfSchools.text.toString().toInt()
        }
        if (visitType == "Combined Visit") {
            if (mArrVisitPurpose.get(visitPosition).ID == 9) {
                if (noOfSchools == 0) {
                    Toast.makeText(this, "Enter Number of Schools First", Toast.LENGTH_SHORT).show()
                } else {
                    CallService()
                }

            } else {
                CallService()
            }
        } else {
            CallService()
        }
    }

    fun CallService() {
        var remarks: String = binding.etRemarks.text.toString()
        if (visitType == "Dealer Visits") {
            for (item in mArrDealers) {
                mArrIDsToSend.add(item.ID)
            }
        }

        mDialog.show()
        mDialog.setMessage("please wait...")
        disposable = restService.CombinedVisits(retailerID, SOID, mArrEmployee[soPosition].ID, visitType, multiVisitPurpose, remarks, noOfSchools, mArrIDsToSend)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result -> showResult(result, RequestCode.POST_MULTI_PURPOSE_VISIT) },
                        { error -> showError(error.message) }
                )

    }
}
