package com.ibrahim.salesforce.fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.ibrahim.salesforce.R
import com.ibrahim.salesforce.adapters.MyVisitPlansAdapter
import com.ibrahim.salesforce.adapters.VisitDetailsAdapter
import com.ibrahim.salesforce.databinding.FragmentGetNextWeekVisitPlanBinding
import com.ibrahim.salesforce.network.RequestCode
import com.ibrahim.salesforce.network.RestService
import com.ibrahim.salesforce.response.*
import com.ibrahim.salesforce.utilities.AppKeys
import io.paperdb.Paper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*


class GetNextWeekVisitPlanFragment : androidx.fragment.app.Fragment(), View.OnClickListener {

    private lateinit var v: FragmentGetNextWeekVisitPlanBinding
    private var mContext: Context? = null
    private lateinit var mServerResponse: GetServerResponse
    private lateinit var mDialog: ProgressDialog
    private lateinit var mArrEmployee: List<SalesOfficer>
    private lateinit var disposable: Disposable
    private var empPosition: Int = 0
    private var date: String = ""
    private lateinit var employeeAdapter: ArrayAdapter<SalesOfficer>
    private var mArrVisits: MutableList<MyVisits> = mutableListOf()
    private var visitDetailAdapter: VisitDetailsAdapter? = null
    var dateFrom: TextView? = null
    var dateTo: TextView? = null
    var select_date: String? = null
    var myCalendar: Calendar? = null
    // get toDate and store it mtd.
    var mtd : String? = null
    private val datee: DatePickerDialog.OnDateSetListener? = null
    val trueList: ArrayList<GVisitPlans> = ArrayList()
    val updateVisitPlan: ArrayList<NextVisitPlanData> = ArrayList()
    var uVisitPlans: ArrayList<UVisitPlans> = ArrayList()

    private lateinit var getCustomerRespone: GetServerResponse
    private lateinit var mArrVisitPlans: ArrayList<GVisitPlans>
    private var mVisitPlansAdapter: MyVisitPlansAdapter? = null
    private var mInitialToDate: String? = null

    private val restService by lazy { RestService.create() }

    companion object {
        fun newInstance(): GetNextWeekVisitPlanFragment {
            return GetNextWeekVisitPlanFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = FragmentGetNextWeekVisitPlanBinding.inflate(inflater, container, false)
        mContext = activity
        Paper.init(activity)
        initGui()
        return v.root
    }

    @SuppressLint("SetTextI18n")
    private fun initGui() {
        mServerResponse = Paper.book().read<GetServerResponse>(AppKeys.KEY_LOGIN_RESPONSE)
        mDialog = ProgressDialog(activity)
        mDialog.setCancelable(false)
        setEmployeeSpinner()
        //  yyyy-MM-dd
        //  E-dd-MM
        var dateN = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        Log.e("dateN", "dateN $dateN")

        var currentDate: Date = Calendar.getInstance().time
        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        v.tvFromVisitDateGetvisitplan.text = sdf.format(currentDate.time)

//        v.tvFromVisitDateGetvisitplan.text = dateN
//        v.tvToVisitDateGetvisitplan.text = dateN

        v.tvToVisitDateGetvisitplan.text = getCalculatedDate(myFormat, 7)

        v.tvFromVisitDateGetvisitplan!!.setOnClickListener {
            val newCalendar = Calendar.getInstance()
            val startDatePickerDialog = DatePickerDialog(
                mContext!!, object : DatePickerDialog.OnDateSetListener {
                    @SuppressLint("SimpleDateFormat")
                    override fun onDateSet(
                        view: DatePicker,
                        year: Int,
                        monthOfYear: Int,
                        dayOfMonth: Int
                    ) {
                        var attendanceDateFormater = SimpleDateFormat("dd/MM/yyyy")
                        val newDate = Calendar.getInstance()
                        newDate[year, monthOfYear] = dayOfMonth
                        v.tvFromVisitDateGetvisitplan.text = attendanceDateFormater.format(newDate.time)
                    }
                }, newCalendar[Calendar.YEAR], newCalendar[Calendar.MONTH],
                newCalendar[Calendar.DAY_OF_MONTH]
            )
            startDatePickerDialog.show()
        }

        v.tvToVisitDateGetvisitplan!!.setOnClickListener {
            val newCalendar = Calendar.getInstance()
            val startDatePickerDialog = DatePickerDialog(
                mContext!!, object : DatePickerDialog.OnDateSetListener {
                    @SuppressLint("SimpleDateFormat")
                    override fun onDateSet(
                        view: DatePicker,
                        year: Int,
                        monthOfYear: Int,
                        dayOfMonth: Int
                    ) {
                        var attendanceDateFormater = SimpleDateFormat("dd/MM/yyyy")
                        val newDate = Calendar.getInstance()
                        newDate[year, monthOfYear] = dayOfMonth
                        v.tvToVisitDateGetvisitplan.text =
                            attendanceDateFormater.format(newDate.time)

                        Toast.makeText(mContext, "Getting Data", Toast.LENGTH_SHORT).show()

                        mArrVisitPlans.clear()
//                mArrVisits.clear()
                        Log.e(
                            "emppos",
                            "onClick: Employee Position " + mArrEmployee[empPosition].ID
                        )
                        mtd = v.tvToVisitDateGetvisitplan.text.toString()

                        getVisitsFromServers(
                            mArrEmployee[empPosition].ID,
                            v.tvFromVisitDateGetvisitplan.text.toString(),
                            v.tvToVisitDateGetvisitplan.text.toString()
                        )
                    }
                }, newCalendar[Calendar.YEAR], newCalendar[Calendar.MONTH],
                newCalendar[Calendar.DAY_OF_MONTH]
            )
            startDatePickerDialog.show()
        }

//        v.tvToVisitDateGetvisitplan.text = attendanceDateFormater.format(newDate.time)


        mArrVisitPlans = ArrayList()
        v.btnUpdateFrgGetNextWeekVisit.setOnClickListener(this)
    }

    private fun setEmployeeSpinner() {
        mArrEmployee = mServerResponse.Data.SalesOfficer
        if (mArrEmployee.isEmpty()) {
            Log.e("ABC", mArrEmployee.size.toString())
        } else {
            Log.e("ABCD", mArrEmployee.size.toString())
            callLocationService(mArrEmployee[empPosition].ID, date)
            employeeAdapter = ArrayAdapter<SalesOfficer>(
                requireActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                mArrEmployee
            )
            v.spnEmployee.adapter = employeeAdapter
        }
        if (mArrEmployee.size == 1)
            v.spnEmployee.isEnabled = false
        v.spnEmployee.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                empPosition = position
                callLocationService(mArrEmployee[position].ID, date)
                getVisitsFromServers(
                    mArrEmployee[position].ID,
                    v.tvFromVisitDateGetvisitplan.text.toString(),
                    v.tvToVisitDateGetvisitplan.text.toString()
                )
                if (!mArrVisits.isNullOrEmpty()) {
                    mArrVisits.clear()
                    visitDetailAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun callLocationService(SOID: Int, Date: String?) {
        mDialog.show()
        mDialog.setMessage("please wait...")
        disposable = restService.getVisitDetails(SOID.toString(), Date)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    showResult(
                        result,
                        RequestCode.API_GET_SCHOOL_INFO_FOR_EDIT_REQUEST_CODE
                    )
                },
                { error -> showError(error.message) }
            )
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnUpdateFrgGetNextWeekVisit -> {
                if (updateVisitPlan.isEmpty()) {
                    Toast.makeText(mContext, "No Plan Selected", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("size", "SIZE ${updateVisitPlan.size}")
                    updateVisitPlans(updateVisitPlan)
                }
            }
        }
    }

    private fun updateVisitPlans(updateVPlan: ArrayList<NextVisitPlanData>) {
        mDialog.show()
        mDialog.setMessage("please wait...")

        Log.e("mplans", "size : " + updateVPlan.size)
        for (i in updateVPlan) {
            Log.e("mplans", "chek: " + i.IsChecked)
            Log.e("mplans", "date: " + i.PlanDate)
            Log.e("mplans", "dtl : " + i.PlanDetails)
        }

        var uVisitPlans = UVisitPlans()
        uVisitPlans.SOID = mArrEmployee[empPosition].ID
        uVisitPlans.uNextVisitPlanData = updateVPlan
        Log.e("MYDATA", "Mdata : " + uVisitPlans.SOID)
        Log.e("MYDATA", "Mdata : " + uVisitPlans.uNextVisitPlanData)



        disposable = restService.updateWeeklyVisitPlan(uVisitPlans)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> showResult(result, RequestCode.API_UPDATE_WEEKLY_VISIT_PLAN) },
                { error -> showError(error.message) }
            )
    }

    private fun getVisitsFromServers(SOID: Int, fromDate: String, toDate: String) {
        mDialog.show()
        mDialog.setMessage("Fetching  please wait...")
        disposable = restService.getVisitPlans(SOID, fromDate, toDate)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> showResult(result, RequestCode.API_GET_WEEKLY_VISIT_PLAN) },
                { error -> showError(error.message) }
            )
    }

    private fun showResult(response: GetServerResponse, requestCode: RequestCode) {
        if (requestCode == RequestCode.API_GET_WEEKLY_VISIT_PLAN) {
            getCustomerRespone = response
            mArrVisitPlans = getCustomerRespone.GVisitPlans!!
            Log.e("TAG", "ALL LIST: ${getCustomerRespone.GVisitPlans}")
            Log.e("TAG", "ALL LIST: ${mArrVisitPlans.size}")

            if (mArrVisitPlans.isNotEmpty()) {
                v.recyclerViewGetVisitPlan.layoutManager =
                    androidx.recyclerview.widget.LinearLayoutManager(activity)

                mVisitPlansAdapter = MyVisitPlansAdapter(mArrVisitPlans, v.tvToVisitDateGetvisitplan.text.toString())

                mVisitPlansAdapter!!.setOnClickMenuListener(mOnClickRowConsignmentListener)
                mVisitPlansAdapter!!.onClickRowClass

                v.recyclerViewGetVisitPlan.adapter = mVisitPlansAdapter
                mVisitPlansAdapter!!.notifyDataSetChanged()
            } else {
                Toast.makeText(mContext, "No Data", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(mContext, response.Message, Toast.LENGTH_SHORT).show()
        }

        if (requestCode == RequestCode.API_UPDATE_WEEKLY_VISIT_PLAN) {
            if (mDialog.isShowing)
                mDialog.dismiss()
            Toast.makeText(mContext, "Form Updated Successfully.", Toast.LENGTH_LONG).show()
            updateVisitPlan.clear()
            requireActivity().onBackPressed()
        } else {
            mDialog.dismiss()
            Toast.makeText(activity, response.Message, Toast.LENGTH_LONG).show()
            requireActivity().onBackPressed()
        }

        if (mDialog.isShowing)
            mDialog.dismiss()

        mDialog.hide()
    }

    private fun showError(message: String?) {
        mDialog.hide()
    }

    private val mOnClickRowConsignmentListener: MyVisitPlansAdapter.OnClickRowSchoolListener =
        object : MyVisitPlansAdapter.OnClickRowSchoolListener {

            override fun onClickCheckBox(
                position: Int,
                vps: ArrayList<GVisitPlans>,
                updteVplan: ArrayList<NextVisitPlanData>?
            ) {

                trueList.clear()
                updateVisitPlan.clear()
                Log.e("abc", "vps size : " + vps!!.size)
                Log.e("abc", "vps size : " + updteVplan!!.size)
                trueList.addAll(vps)
                updateVisitPlan.addAll(updteVplan!!)
                Log.e("abc", "truelist Size : " + trueList.size)
                Log.e("abc", "truelist Size : " + updteVplan.size)
            }
        }

    private fun getCalculatedDate(dateFormat: String?, days: Int): String? {
        val cal = Calendar.getInstance()
        val s = SimpleDateFormat(dateFormat)
        cal.add(Calendar.DAY_OF_YEAR, days)
        return s.format(Date(cal.timeInMillis))
    }

}