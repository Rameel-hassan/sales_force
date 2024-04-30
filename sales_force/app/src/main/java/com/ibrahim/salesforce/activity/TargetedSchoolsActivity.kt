package com.app.salesforce.activity

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.salesforce.adapters.TargetSchoolsAdapter
import com.app.salesforce.databinding.ActivityTargetedSchoolsBinding
import com.app.salesforce.model.GetTodayTargetedSchools
import com.app.salesforce.network.RestService
import com.app.salesforce.response.GetServerResponse
import com.app.salesforce.response.SalesOfficer
import com.app.salesforce.utilities.AppKeys
import io.paperdb.Paper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TargetedSchoolsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTargetedSchoolsBinding
    private var targetSchoolsAdapter: TargetSchoolsAdapter? = null
    private lateinit var mDialog: ProgressDialog
    private lateinit var disposable: Disposable
    private lateinit var mServerResponse: GetServerResponse
    private var targetedSchools: ArrayList<GetTodayTargetedSchools.TodayTargetedSchool?>? =
        ArrayList()
    private lateinit var mArrEmployee: List<SalesOfficer>
    private var date: String = ""
    private var empPosition: Int = 0
    private lateinit var employeeAdapter: ArrayAdapter<SalesOfficer>
    private val restService by lazy { RestService.create() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTargetedSchoolsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setDate()
        initGui()

    }

    private fun setDate() {

        var cal = Calendar.getInstance()

        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "yyyy-MM-dd" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                binding.txtDateSelect.text = sdf.format(cal.time)
                date = binding.txtDateSelect.text.toString()
                getTargetedSchools(mArrEmployee[empPosition].ID, date!!)
                if (!targetedSchools.isNullOrEmpty()) {
                    targetedSchools?.clear()
                    targetSchoolsAdapter!!.notifyDataSetChanged()
                    binding.llVisitsData.visibility = View.GONE
                }
            }

        val myFormat = "yyyy-MM-dd" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        date=sdf.format(cal.time)

        binding.txtDateSelect.setOnClickListener {
            DatePickerDialog(
                this, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }


    private fun setEmployeeSpinner() {
        mArrEmployee = mServerResponse.Data.SalesOfficer
        if (mArrEmployee.isEmpty()) {
            Log.e("ABC", mArrEmployee.size.toString())
        } else {
            Log.e("ABCD", mArrEmployee.size.toString())
            getTargetedSchools(mArrEmployee[empPosition].ID, date)
            employeeAdapter = ArrayAdapter<SalesOfficer>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                mArrEmployee
            )
            binding.spnEmployee.adapter = employeeAdapter
        }
        if (mArrEmployee.size == 1)
            binding.spnEmployee.isEnabled = false
        binding.spnEmployee.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                empPosition = position
                getTargetedSchools(mArrEmployee[position].ID, date)
                if (!targetedSchools.isNullOrEmpty()) {
                    targetedSchools?.clear()
                    targetSchoolsAdapter!!.notifyDataSetChanged()
                    binding.llVisitsData.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun initGui() {
        mServerResponse = Paper.book().read<GetServerResponse>(AppKeys.KEY_LOGIN_RESPONSE)
        mDialog = ProgressDialog(this)
        mDialog.setCancelable(false)
        mDialog.setCancelable(false)
        setEmployeeSpinner()
    }

    private fun getTargetedSchools(SOID: Int, Date: String?) {
        mDialog.show()
        mDialog.setMessage("please wait...")
        disposable = restService.getDateWiseTargetSchools(SOID.toString(), Date)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> showResult(result) },
                { error -> showError(error.message) }
            )
    }

    private fun showResult(result: GetTodayTargetedSchools) {
        binding.tvError.visibility = View.GONE
        targetedSchools = result.todayTargatedSchools as ArrayList<GetTodayTargetedSchools.TodayTargetedSchool?>?
        if (targetedSchools?.isNotEmpty() == true) {
            binding.rvItems.layoutManager =
                androidx.recyclerview.widget.LinearLayoutManager(this)
            targetSchoolsAdapter = TargetSchoolsAdapter(targetedSchools)
            binding.rvItems.adapter = targetSchoolsAdapter
            binding.llVisitsData.visibility = View.VISIBLE
        } else
            binding.tvError.visibility = View.VISIBLE
        mDialog.hide()
        Log.e("ALL VISITS", result.toString())
    }

    private fun showError(message: String?) {
        mDialog.hide()
        Toast.makeText(this, "No Target Schools Found", Toast.LENGTH_SHORT).show()
        binding.tvError.visibility = View.VISIBLE
    }

}