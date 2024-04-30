package com.app.salesforce.fragment

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.app.salesforce.adapters.VisitDetailsAdapter
import com.app.salesforce.databinding.FragmentMyVisitsBinding
import com.app.salesforce.network.RestService
import com.app.salesforce.response.GetServerResponse
import com.app.salesforce.response.MyVisits
import com.app.salesforce.response.SalesOfficer
import com.app.salesforce.utilities.AppKeys
import io.paperdb.Paper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MyVisitsFragment : androidx.fragment.app.Fragment() {

    private lateinit var v: FragmentMyVisitsBinding
    private var visitDetailAdapter: VisitDetailsAdapter? = null
    private lateinit var mDialog: ProgressDialog
    private lateinit var disposable: Disposable
    private lateinit var mServerResponse: GetServerResponse
    private var mArrVisits: MutableList<MyVisits> = mutableListOf()
    private lateinit var mArrEmployee: List<SalesOfficer>
    private var date: String = ""
    private var empPosition: Int = 0
    private lateinit var employeeAdapter: ArrayAdapter<SalesOfficer>
    private val restService by lazy { RestService.create() }


    companion object {
        fun newInstance(): MyVisitsFragment {
            return MyVisitsFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = FragmentMyVisitsBinding.inflate(inflater, container, false)
        Paper.init(activity)
        initGui()
        setDate()
        return v.root
    }

    private fun initGui() {
        mServerResponse = Paper.book().read<GetServerResponse>(AppKeys.KEY_LOGIN_RESPONSE)
        mDialog = ProgressDialog(activity)
        mDialog.setCancelable(false)
        mDialog.setCancelable(false)
        setEmployeeSpinner()
    }

    private fun setDate() {

        var cal = Calendar.getInstance()

        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val myFormat = "yyyy-MM-dd" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            v.txtDateSelect.text = sdf.format(cal.time)
            date = v.txtDateSelect.text.toString()
            callLocationService(mArrEmployee[empPosition].ID, date!!)
            if (!mArrVisits.isNullOrEmpty()) {
                mArrVisits.clear()
                visitDetailAdapter!!.notifyDataSetChanged()
                v.llVisitsData.visibility = View.GONE
            }
        }

        v.txtDateSelect.setOnClickListener {
            DatePickerDialog(
                requireActivity(), dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun setEmployeeSpinner() {
        mArrEmployee = mServerResponse.Data.SalesOfficer
        if (mArrEmployee.isEmpty()){
            Log.e("ABC", mArrEmployee.size.toString())
        } else {
            Log.e("ABCD", mArrEmployee.size.toString())
            callLocationService(mArrEmployee[empPosition].ID, date)
            employeeAdapter = ArrayAdapter<SalesOfficer>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, mArrEmployee)
            v.spnEmployee.adapter = employeeAdapter
        }
        if (mArrEmployee.size == 1)
            v.spnEmployee.isEnabled = false
        v.spnEmployee.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                empPosition = position
                callLocationService(mArrEmployee[position].ID, date)
                if (!mArrVisits.isNullOrEmpty()) {
                    mArrVisits.clear()
                    visitDetailAdapter!!.notifyDataSetChanged()
                    v.llVisitsData.visibility = View.GONE
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
                        { result -> showResult(result) },
                        { error -> showError(error.message) }
                )
    }

    private fun showResult(result: GetServerResponse) {
        v.tvError.visibility = View.GONE
        mArrVisits = result.MyVisits.toMutableList()
        if (mArrVisits.isNotEmpty()) {
            v.rvItems.layoutManager =
                androidx.recyclerview.widget.LinearLayoutManager(activity)
            visitDetailAdapter = VisitDetailsAdapter(mArrVisits)
            v.rvItems.adapter = visitDetailAdapter
            v.llVisitsData.visibility = View.VISIBLE
        } else
            v.tvError.visibility = View.VISIBLE
        mDialog.hide()
        Log.e("ALL VISITS", result.toString())
    }

    private fun showError(message: String?) {
        mDialog.hide()
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
        v.tvError.visibility = View.VISIBLE
    }
}