package com.ibrahim.salesforce.activity

import android.R
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ibrahim.salesforce.adapters.OnItemClick
import com.ibrahim.salesforce.adapters.VisitHistoryAdapter
import com.ibrahim.salesforce.databinding.ActivityVisitHistoryBinding
import com.ibrahim.salesforce.dialoge.DialogHelper
import com.ibrahim.salesforce.model.SampleSearchModel
import com.ibrahim.salesforce.network.ApiService
import com.ibrahim.salesforce.network.RequestCode
import com.ibrahim.salesforce.network.RestCallbackObject
import com.ibrahim.salesforce.network.RestClient
import com.ibrahim.salesforce.network.ServerConnectListenerObject
import com.ibrahim.salesforce.response.GetServerResponse
import com.ibrahim.salesforce.response.GetVisitHistoryResponse
import com.ibrahim.salesforce.response.GetVisitHistoryResponse.VisitHistory
import com.ibrahim.salesforce.response.SalesOfficer
import com.ibrahim.salesforce.utilities.AppKeys
import com.ibrahim.salesforce.utilities.DateTimeUtilites
import io.paperdb.Paper
import retrofit2.Call
import retrofit2.Callback
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class VisitHistoryActivity : AppCompatActivity(), ServerConnectListenerObject {


    private val startDateCalendar = Calendar.getInstance()
    private var mArrEmployee: List<SalesOfficer>? = null
    private var mLoginResponse: GetServerResponse? = null

    private var otherSOID = -1

    private var visitHistoryList: ArrayList<VisitHistory>? = null


    private var mDialog: ProgressDialog? = null
    private var mService: ApiService? = null

    private var newSampleSearchModels: ArrayList<SampleSearchModel>? = null


    private lateinit var binding: ActivityVisitHistoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVisitHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)


        newSampleSearchModels = ArrayList()
        parseBundle()
        mArrEmployee = mLoginResponse?.Data?.SalesOfficer
        mDialog = ProgressDialog(this@VisitHistoryActivity);
        initSpinners()

        binding.btnSearch.setOnClickListener {
            if (validDates()) {
                callService()
            }
        }


        binding.tvEndDate.setOnClickListener {
            if (binding.tvStartDate.text.equals("Start Date")) {
                Toast.makeText(
                    this@VisitHistoryActivity,
                    "Start Date not selected",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            val myCalendar = Calendar.getInstance()
            DatePickerDialog(
                this@VisitHistoryActivity,
                { view, year, month, dayOfMonth ->


                    myCalendar[Calendar.YEAR] = year
                    myCalendar[Calendar.MONTH] = month
                    myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                    if (myCalendar.timeInMillis < startDateCalendar.timeInMillis) {
                        Toast.makeText(
                            this@VisitHistoryActivity,
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
                this@VisitHistoryActivity,
                { view, year, month, dayOfMonth ->


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
        if (binding.tvStartDate.text.equals("Start Date") || binding.tvEndDate.text.equals("End Date")) {
            Toast.makeText(this@VisitHistoryActivity, "Dates are not selected.", Toast.LENGTH_SHORT)
                .show()
            return false
        } else if (otherSOID == -1) {
            Toast.makeText(
                this@VisitHistoryActivity,
                "Sale Officer not selected.",
                Toast.LENGTH_SHORT
            ).show()
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
                    //callService()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }


    private fun callService() {
        mService = RestClient.getInstance(this@VisitHistoryActivity)

        mDialog!!.show()
        mDialog!!.setMessage("Fetching Visit History please wait...")
        mService = RestClient.getInstance(this)
        val userObject: Call<GetVisitHistoryResponse>? = mService?.getVisitHistoryResponse(
            binding.tvEndDate.text.toString(),
            binding.tvStartDate.text.toString(), otherSOID
        )
        val callbackObject =
            RestCallbackObject(this, this, RequestCode.GET_VISIT_HISTORY_RESPONSE).showProgress(
                true,
                0
            ).dontHideProgress(false)
        userObject?.enqueue(callbackObject as Callback<GetVisitHistoryResponse>)

    }

    override fun onFailure(error: String?, requestCode: RequestCode?) {

        Toast.makeText(
            this@VisitHistoryActivity,
            "API Failed--$requestCode$error",
            Toast.LENGTH_SHORT
        ).show()

        mDialog?.hide()
    }

    override fun onSuccess(`object`: Any?, requestCode: RequestCode?) {
        if (requestCode == RequestCode.GET_VISIT_HISTORY_RESPONSE) {
            visitHistoryList =
                (`object` as GetVisitHistoryResponse).visitHistoryList as ArrayList<VisitHistory>?
            mDialog!!.hide()

            if (visitHistoryList != null && visitHistoryList?.isNotEmpty() == true) {
                for (i in 0 until visitHistoryList!!.size) {
                    visitHistoryList!![i].srNo = i + 1
                    visitHistoryList!![i].visitDate =
                        DateTimeUtilites.getDatetime(visitHistoryList!![i].visitDate);

//                        visitHistoryList!![i].visitDate.substring(0,10)
                }



                binding.tvNoVisits.visibility = View.GONE
                binding.rv.visibility = View.VISIBLE
                binding.tableHeading.visibility = View.VISIBLE

                initVisitHistoryAdapter()

            } else {

                binding.tvNoVisits.visibility = View.VISIBLE
                binding.rv.visibility = View.GONE
                binding.tableHeading.visibility = View.GONE
            }

        }
    }

    private fun initVisitHistoryAdapter() {
        binding.rv.layoutManager = LinearLayoutManager(this@VisitHistoryActivity)
        val onItemClickImplementation = object : OnItemClick {
            override fun infoClicked(position: Int) {
                showDetailsDilog(position);
                Log.d("TAG", "infoClicked: line " + position)
            }
        }

        binding.rv.adapter =
            visitHistoryList?.let { VisitHistoryAdapter(it, onItemClickImplementation) }
    }

    private fun showDetailsDilog(position: Int) {

        var msg = "Address : " + visitHistoryList?.get(position)?.address.toString()
        msg += "\n" + "Comments : " +visitHistoryList?.get(position)?.comments.toString()
//        msg += "\n" + "Address : " +visitHistoryList?.get(position)?.srNo.toString()
//        msg += "\n" +"Address : " +visitHistoryList?.get(position)?.srNo.toString()


        DialogHelper.showAlert(this@VisitHistoryActivity,
            visitHistoryList?.get(position)?.srNo.toString(),
            msg)
    }
}


