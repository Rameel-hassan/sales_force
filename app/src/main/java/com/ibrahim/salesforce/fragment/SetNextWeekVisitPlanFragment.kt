package com.ibrahim.salesforce.fragment

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.ibrahim.salesforce.R
import com.ibrahim.salesforce.adapters.VisitDetailsAdapter
import com.ibrahim.salesforce.databinding.FragmentSetNextWeekVisitPlanBinding
import com.ibrahim.salesforce.network.ApiService
import com.ibrahim.salesforce.network.RequestCode
import com.ibrahim.salesforce.network.RestClient
import com.ibrahim.salesforce.network.RestService
import com.ibrahim.salesforce.response.GVisitPlans
import com.ibrahim.salesforce.response.GetServerResponse
import com.ibrahim.salesforce.response.MyVisits
import com.ibrahim.salesforce.response.NextVisitPlanData
import com.ibrahim.salesforce.response.SalesOfficer
import com.ibrahim.salesforce.response.VisitPlans
import com.ibrahim.salesforce.utilities.AppKeys
import com.ibrahim.salesforce.utilities.Utility
import io.paperdb.Paper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class SetNextWeekVisitPlanFragment : androidx.fragment.app.Fragment(), View.OnClickListener {

    private lateinit var v: FragmentSetNextWeekVisitPlanBinding
    private var mContext: Context? = null
    private var visitDetailAdapter: VisitDetailsAdapter? = null
    private lateinit var mDialog: ProgressDialog
    private lateinit var disposable: Disposable
    private lateinit var mServerResponse: GetServerResponse
    private var mArrVisits: MutableList<MyVisits> = mutableListOf()
    private lateinit var mArrEmployee: List<SalesOfficer>
    private var date: String = ""
    private var empPosition: Int = 0
    private lateinit var employeeAdapter: ArrayAdapter<SalesOfficer>

    private lateinit var mArrWeeklyVisitPlan: ArrayList<NextVisitPlanData>
    var visitPlans: VisitPlans = VisitPlans()

    private var mNextVisitPlanData: NextVisitPlanData? = null
    private lateinit var mService: ApiService

    private lateinit var getCustomerRespone: GetServerResponse
    private lateinit var mArrVisitPlans: ArrayList<GVisitPlans>

    private val restService by lazy { RestService.create() }

    companion object {
        fun newInstance(): SetNextWeekVisitPlanFragment {
            return SetNextWeekVisitPlanFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = FragmentSetNextWeekVisitPlanBinding.inflate(inflater, container, false)
        mContext = activity
        Paper.init(activity)
        initGui()

        return v.root
    }

    private fun initGui() {
        mServerResponse = Paper.book().read<GetServerResponse>(AppKeys.KEY_LOGIN_RESPONSE)
        mDialog = ProgressDialog(activity)
        mDialog.setCancelable(false)
        mDialog.setCancelable(false)

        var currentDate: Date = Calendar.getInstance().time
        Log.e("TAG", "initGui: $currentDate")
//        val myFormat = "E-yyyy-MM-dd"
//        val myFormat = "E-dd-MM"
        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        v.tvDayOneSetNextWeekPlan.text = sdf.format(currentDate.time)

        // format type 6
//        calendar = Calendar.getInstance()
//        simpleDateFormat = SimpleDateFormat("E.LLLL.yyyy HH:mm:ss aaa z")
//        dateTime = simpleDateFormat.format(calendar.time).toString()
//        format6.text = dateTime

        Log.e("TAG", "initGui: ${sdf.format(currentDate.time)}")
        Log.e("TAG", "initGui: $currentDate")

        v.tvDayTwoSetNextWeekPlan.text = getCalculatedDate(myFormat, 1)
        v.tvDayThreeSetNextWeekPlan.text = getCalculatedDate(myFormat, 2)
        v.tvDayFourSetNextWeekPlan.text = getCalculatedDate(myFormat, 3)
        v.tvDayFiveSetNextWeekPlan.text = getCalculatedDate(myFormat, 4)
        v.tvDaySixSetNextWeekPlan.text = getCalculatedDate(myFormat, 5)
        v.tvDaySevenSetNextWeekPlan.text = getCalculatedDate(myFormat, 6)

        setEmployeeSpinner()
        v.btnSubmitFrgNextWeekVisit.setOnClickListener(this)

        getVisitsFromServers(
            mArrEmployee[empPosition].ID,
            v.tvDayOneSetNextWeekPlan.text.toString(),
            v.tvDaySevenSetNextWeekPlan.text.toString()
        )

        Log.e("vpserver", "called")
    }

    private fun getCalculatedDate(dateFormat: String?, days: Int): String? {
        val cal = Calendar.getInstance()
        val s = SimpleDateFormat(dateFormat)
        cal.add(Calendar.DAY_OF_YEAR, days)
        return s.format(Date(cal.timeInMillis))
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
                if (!mArrVisits.isNullOrEmpty()) {
                    mArrVisits.clear()
                    visitDetailAdapter!!.notifyDataSetChanged()
                }

                getVisitsFromServers(
                    mArrEmployee[empPosition].ID,
                    v.tvDayOneSetNextWeekPlan.text.toString(),
                    v.tvDaySevenSetNextWeekPlan.text.toString()
                )
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    override fun onClick(view: View?) {

        when (view?.id) {
            R.id.btnSubmitFrgNextWeekVisit -> {

                var obj1 = NextVisitPlanData()
                var obj2 = NextVisitPlanData()
                var obj3 = NextVisitPlanData()
                var obj4 = NextVisitPlanData()
                var obj5 = NextVisitPlanData()
                var obj6 = NextVisitPlanData()
                var obj7 = NextVisitPlanData()
//                setWeeklyVisit(mNextVisitPlanData!!)

                mArrWeeklyVisitPlan = ArrayList()

                obj1.PlanDate = v.tvDayOneSetNextWeekPlan.text.toString()
                obj1.PlanDetails = v.etOneNextWeekPlan.text.toString()
                obj2.PlanDate = v.tvDayTwoSetNextWeekPlan.text.toString()
                obj2.PlanDetails = v.etTwoNextWeekPlan.text.toString()
                obj3.PlanDate = v.tvDayThreeSetNextWeekPlan.text.toString()
                obj3.PlanDetails = v.etThreeNextWeekPlan.text.toString()
                obj4.PlanDate = v.tvDayFourSetNextWeekPlan.text.toString()
                obj4.PlanDetails = v.etFourNextWeekPlan.text.toString()
                obj5.PlanDate = v.tvDayFiveSetNextWeekPlan.text.toString()
                obj5.PlanDetails = v.etFiveNextWeekPlan.text.toString()
                obj6.PlanDate = v.tvDaySixSetNextWeekPlan.text.toString()
                obj6.PlanDetails = v.etSixNextWeekPlan.text.toString()
                obj7.PlanDate = v.tvDaySevenSetNextWeekPlan.text.toString()
                obj7.PlanDetails = v.etSevenNextWeekPlan.text.toString()

                mArrWeeklyVisitPlan.add(obj1)
                mArrWeeklyVisitPlan.add(obj2)
                mArrWeeklyVisitPlan.add(obj3)
                mArrWeeklyVisitPlan.add(obj4)
                mArrWeeklyVisitPlan.add(obj5)
                mArrWeeklyVisitPlan.add(obj6)
                mArrWeeklyVisitPlan.add(obj7)

                visitPlans.SOID = mArrEmployee[empPosition].ID
                visitPlans.nextVisitPlanData = mArrWeeklyVisitPlan

                Log.e("data", "visit plans: " + visitPlans.SOID)
                for (e in mArrWeeklyVisitPlan) {
                    Log.e("\ndata", "date " + e.PlanDate)
                    Log.e("data", "details " + e.PlanDetails)
                }

                if (Utility.isNetworkAvailable(mContext)) {
                    if (validateForm()) {
                        savePlan(visitPlans)
                    }
                } else {
                    Toast.makeText(
                        mContext,
                        getString(R.string.str_no_internet),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun callLocationService(SOID: Int, Date: String?) {
        mDialog.show()
        mDialog.setMessage("please wait...")
        disposable =
            restService.getVisitDetails(SOID.toString(), Date)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result -> showResult(result, RequestCode.API_GET_SCHOOL_INFO) },
                    { error -> showError(error.message) }
                )
    }

    private fun showResult(result: GetServerResponse, requestCode: RequestCode) {

        if (requestCode == RequestCode.API_GET_WEEKLY_VISIT_PLAN) {
            if (requestCode == RequestCode.API_GET_WEEKLY_VISIT_PLAN) {
                getCustomerRespone = result
                mArrVisitPlans = getCustomerRespone.GVisitPlans!!
                Log.e("TAG", "ALL LIST: ${getCustomerRespone.GVisitPlans}")
                Log.e("TAG", "ALL LIST: ${mArrVisitPlans.size}")

                if (mArrVisitPlans.isNotEmpty()) {

                    if(mArrVisitPlans[0].bPrivate == true){
                        v.etOneNextWeekPlan.isEnabled = false
                    }
                    v.etOneNextWeekPlan.setText(mArrVisitPlans[0].PlanDetails)

                    if(mArrVisitPlans[1].bPrivate == true){
                        v.etTwoNextWeekPlan.isEnabled = false
                    }
                    v.etTwoNextWeekPlan.setText(mArrVisitPlans[1].PlanDetails)

                    if(mArrVisitPlans[2].bPrivate == true){
                        v.etThreeNextWeekPlan.isEnabled = false
                    }
                    v.etThreeNextWeekPlan.setText(mArrVisitPlans[2].PlanDetails)

                    if(mArrVisitPlans[3].bPrivate == true){
                        v.etFourNextWeekPlan.isEnabled = false
                    }
                    v.etFourNextWeekPlan.setText(mArrVisitPlans[3].PlanDetails)

                    if(mArrVisitPlans[4].bPrivate == true){
                        v.etFiveNextWeekPlan.isEnabled = false
                    }
                    v.etFiveNextWeekPlan.setText(mArrVisitPlans[4].PlanDetails)

                    if(mArrVisitPlans[5].bPrivate == true){
                        v.etSixNextWeekPlan.isEnabled = false
                    }
                    v.etSixNextWeekPlan.setText(mArrVisitPlans[5].PlanDetails)

                    if(mArrVisitPlans[6].bPrivate == true){
                        v.etSevenNextWeekPlan.isEnabled = false
                    }
                    v.etSevenNextWeekPlan.setText(mArrVisitPlans[6].PlanDetails)

                } else {
                    Toast.makeText(mContext, "No Data", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(mContext, result.Message, Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == RequestCode.API_WEEKLY_VISIT_PLAN) {
            if (requestCode == RequestCode.API_WEEKLY_VISIT_PLAN) {
                if (mDialog.isShowing)
                    mDialog.dismiss()
                Toast.makeText(mContext, "Form Successfully submitted.", Toast.LENGTH_LONG).show()
            } else {
                mDialog.dismiss()
                Toast.makeText(activity, result.Message, Toast.LENGTH_LONG).show()
                requireActivity().onBackPressed()
            }
        }

        if (mDialog.isShowing)
            mDialog.dismiss()
    }

    private fun showError(message: String?) {
        mDialog.hide()
    }

    private fun savePlan(visitPlans: VisitPlans) {
        mDialog.show()
        mDialog.setMessage("please wait...")
        disposable = restService.regWeeklyVisitPlan(visitPlans)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> showResult(result, RequestCode.API_WEEKLY_VISIT_PLAN) },
                { error -> showError(error.message) }
            )

    }

    private fun callRegService(visitPlans: VisitPlans) {
        mDialog.show()
        mDialog.setMessage("Submitting form, please wait...")
        mService = RestClient.getInstance(mContext)
        var userObject: Call<GetServerResponse> = mService.regWeeklyVisitPlan(visitPlans)

        Log.e("TAG", "callRegService: $visitPlans")
/*

        var callbackObject =
            RestCallbackObject(activity, this, RequestCode.API_WEEKLY_VISIT_PLAN).showProgress(
                true,
                0
            ).dontHideProgress(false)
        userObject.enqueue(callbackObject)
*/

        /*
        userObject.enqueue(object : Callback<GetServerResponse?> {

            override fun onResponse(
                p0: Call<GetServerResponse?>,
                p1: Response<GetServerResponse?>
            ) {
                if (p1.isSuccessful) {
                    if (mDialog.isShowing) mDialog.dismiss()
                    Log.e("response", "onResponse: $p1")
                    Log.e("response", "onResponse: body " + p1.body())
                }
            }

            override fun onFailure(p0: Call<GetServerResponse?>, p1: Throwable) {
                if (mDialog.isShowing) {
                    mDialog.dismiss()
                }
                Log.e("error", "onResponse: $p1")
            }
        })
        */
    }

    private fun setWeeklyVisit(obj: NextVisitPlanData) {

        /*
        var a : List<NextVisitPlanData> = ArrayList()
        a[0].planDate = tvDayOneSetNextWeekPlan.text.toString()
        a[0].planDetails = etOne_NextWeekPlan.text.toString()

        a[1].planDate = v.tvDayTwoSetNextWeekPlan.text.toString()
        a[1].planDetails = etTwoNextWeekPlan.text.toString()

        a[2].planDate = tvDayThreeSetNextWeekPlan.text.toString()
        a[2].planDetails = etThreeNextWeekPlan.text.toString()

        a[3].planDate = tvDayFourSetNextWeekPlan.text.toString()
        a[3].planDetails = etFourNextWeekPlan.text.toString()

        a[4].planDate = tvDayFiveSetNextWeekPlan.text.toString()
        a[4].planDetails = etFiveNextWeekPlan.text.toString()

        a[5].planDate = tvDaySixSetNextWeekPlan.text.toString()
        a[5].planDetails = etSixNextWeekPlan.text.toString()

        a[7].planDate = tvDaySevenSetNextWeekPlan.text.toString()
        a[7].planDetails = etSevenNextWeekPlan.text.toString()
        */
//        obj.planDate = tvDayOneSetNextWeekPlan.text.toString()
//        obj.planDetails = etOne_NextWeekPlan.text.toString()
/*

        mArrWeeklyVisitPlan = ArrayList()

        obj.planDate = tvDayOneSetNextWeekPlan.text.toString()
        obj.planDetails = etOne_NextWeekPlan.text.toString()
        mArrWeeklyVisitPlan.add(obj)

        obj.planDate = v.tvDayTwoSetNextWeekPlan.text.toString()
        obj.planDetails = etTwoNextWeekPlan.text.toString()
        mArrWeeklyVisitPlan.add(obj)

        obj.planDate = tvDayThreeSetNextWeekPlan.text.toString()
        obj.planDetails = etThreeNextWeekPlan.text.toString()
        mArrWeeklyVisitPlan.add(obj)

        obj.planDate = tvDayFourSetNextWeekPlan.text.toString()
        obj.planDetails = etFourNextWeekPlan.text.toString()
        mArrWeeklyVisitPlan.add(obj)

        obj.planDate = tvDayFiveSetNextWeekPlan.text.toString()
        obj.planDetails = etFiveNextWeekPlan.text.toString()
        mArrWeeklyVisitPlan.add(obj)

        obj.planDate = tvDaySixSetNextWeekPlan.text.toString()
        obj.planDetails = etSixNextWeekPlan.text.toString()
        mArrWeeklyVisitPlan.add(obj)

        obj.planDate = tvDaySevenSetNextWeekPlan.text.toString()
        obj.planDetails = etSevenNextWeekPlan.text.toString()
        mArrWeeklyVisitPlan.add(obj)


        visitPlans.setNextVisitPlanData(mArrWeeklyVisitPlan)
*/

        /*for( i in visitPlans) {

        }*/

        /*
        obj.planDateOne = tvDayOneSetNextWeekPlan.text.toString()
        obj.planDateTwo = v.tvDayTwoSetNextWeekPlan.text.toString()
        obj.planDateThree = tvDayThreeSetNextWeekPlan.text.toString()
        obj.planDateFour = tvDayFourSetNextWeekPlan.text.toString()
        obj.planDateFive = tvDayFiveSetNextWeekPlan.text.toString()
        obj.planDateSix = tvDaySixSetNextWeekPlan.text.toString()
        obj.planDateSeven = tvDaySevenSetNextWeekPlan.text.toString()

        obj.planDetailsOne = etOne_NextWeekPlan.text.toString()
        obj.planDetailsTwo = etTwoNextWeekPlan.text.toString()
        obj.planDetailsThree = etThreeNextWeekPlan.text.toString()
        obj.planDetailsFour = etFourNextWeekPlan.text.toString()
        obj.planDetailsFive = etFiveNextWeekPlan.text.toString()
        obj.planDetailsSix = etSixNextWeekPlan.text.toString()
        obj.planDetailsSeven = etSevenNextWeekPlan.text.toString()
        */
    }

    private fun validateForm(): Boolean {
        /*
        if (mNextVisitPlanData!!.planDetailsOne.trim().isEmpty()) {
            showToast("Plan Details One")
            etOne_NextWeekPlan.error = "This Field Cannot be Blank"
            return false
        } else if (mNextVisitPlanData!!.planDetailsTwo.trim().isEmpty()) {
            showToast("Plan Details Two")
            etTwoNextWeekPlan.error = "This Field Cannot be Blank"
            return false
        } else if (mNextVisitPlanData!!.planDetailsThree.trim().isEmpty()) {
            showToast("Plan Details Two")
            etThreeNextWeekPlan.error = "This Field Cannot be Blank"
            return false
        } else if (mNextVisitPlanData!!.planDetailsFour.trim().isEmpty()) {
            showToast("Plan Details Two")
            etFourNextWeekPlan.error = "This Field Cannot be Blank"
            return false
        } else if (mNextVisitPlanData!!.planDetailsFive.trim().isEmpty()) {
            showToast("Plan Details Two")
            etFiveNextWeekPlan.error = "This Field Cannot be Blank"
            return false
        } else if (mNextVisitPlanData!!.planDetailsSix.trim().isEmpty()) {
            showToast("Plan Details Two")
            etSixNextWeekPlan.error = "This Field Cannot be Blank"
            return false
        } else if (mNextVisitPlanData!!.planDetailsSeven.trim().isEmpty()) {
            showToast("Plan Details Two")
            etSevenNextWeekPlan.error = "This Field Cannot be Blank"
            return false
        }
        */
        return true
    }

    private fun showToast(msg: String) {
        Toast.makeText(mContext, "$msg is required", Toast.LENGTH_SHORT).show()
    }

/*

    override fun onFailure(error: String?, requestCode: RequestCode?) {
        if (requestCode == RequestCode.API_WEEKLY_VISIT_PLAN) {
            if (mDialog.isShowing) {
                mDialog.dismiss()
            }
            Log.e("Result", "Failed")
            Toast.makeText(mContext, "Failed-- $error", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSuccess(`object`: Any?, requestCode: RequestCode?) {
        if (requestCode == RequestCode.API_WEEKLY_VISIT_PLAN) {
            val serverResponse = `object` as ServerResponse
            if (mDialog.isShowing) mDialog.dismiss()
            Log.e("Result", "Success")
            if (serverResponse.resultType == ServerCodes.ServerResultType.SERVER_RESULT_SUCCESS) {
                Toast.makeText(mContext, "Form Successfully submitted.", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(mContext, serverResponse.message, Toast.LENGTH_LONG).show()
                requireActivity().finish()
            }
        }

    }
*/

    override fun onResume() {
        super.onResume()

        getVisitsFromServers(
            mArrEmployee[empPosition].ID,
            v.tvDayOneSetNextWeekPlan.text.toString(),
            v.tvDaySevenSetNextWeekPlan.text.toString()
        )

        Log.e("onresume", "onresume called")
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

}