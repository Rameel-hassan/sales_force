package com.app.salesforce.fragment

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.app.salesforce.R
import com.app.salesforce.adapters.RemindersAdapter
import com.app.salesforce.callbacks.ReminderAdapterCallBacks
import com.app.salesforce.databinding.FragmentRejectedVisitBinding
import com.app.salesforce.network.RequestCode
import com.app.salesforce.network.RestService
import com.app.salesforce.response.GetServerResponse
import com.app.salesforce.response.Reminder
import com.app.salesforce.response.SalesOfficer
import com.app.salesforce.utilities.AppKeys
import com.app.salesforce.utilities.RecyclerSectionItemDecoration
import io.paperdb.Paper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers



public class RejectedVisitFragment : androidx.fragment.app.Fragment(), ReminderAdapterCallBacks {

    private lateinit var v: FragmentRejectedVisitBinding
    private lateinit var mDialog: ProgressDialog
    private lateinit var remindersAdapter: RemindersAdapter
    private lateinit var disposable: Disposable
    private lateinit var mServerResponse: GetServerResponse
    private var arrTotalReminders: MutableList<Reminder> = mutableListOf()
    private lateinit var arrEmployee: List<SalesOfficer>
    private var arrActiveReminders: MutableList<Reminder> = ArrayList()
    private var arrPendingReminders: MutableList<Reminder> = ArrayList()
    private lateinit var employeeAdapter: ArrayAdapter<SalesOfficer>
    private var SoId: Int = 0
    private var deleteReminderPosition: Int = 0
    private var ReschduleReminderPosition: Int = 0
    private val restService by lazy { RestService.create() }

    private var adapter: ArrayAdapter<String>? = null

    var mSpnSessionDate: Spinner? = null ;
    private var mArrSessionDate: MutableList<String> = ArrayList()


    companion object {
        fun newInstance(): RejectedVisitFragment {
            return RejectedVisitFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = FragmentRejectedVisitBinding.inflate(inflater, container, false)

        mSpnSessionDate = v.spnEditSessStartMonth

        Paper.init(activity)
        initGui()
        return v.root
    }

    private fun initGui() {
        mServerResponse = Paper.book().read<GetServerResponse>(AppKeys.KEY_LOGIN_RESPONSE)
        mDialog = ProgressDialog(activity)
        mDialog.setCancelable(false)
        setEmployeeSpinner()
        setSessionStartMonth("January")
    }
    private fun setSessionStartMonth(Current: String?) {
        mArrSessionDate.add("Select Session Start Month*")
        mArrSessionDate.add("January")
        mArrSessionDate.add("February")
        mArrSessionDate.add("march")
        mArrSessionDate.add("april")
        mArrSessionDate.add("May")
        mArrSessionDate.add("June")
        mArrSessionDate.add("July")
        mArrSessionDate.add("August")
        mArrSessionDate.add("September")
        mArrSessionDate.add("October")
        mArrSessionDate.add("November")
        mArrSessionDate.add("December")
        adapter = context?.let {
            ArrayAdapter<String>(
                it,
                android.R.layout.simple_spinner_dropdown_item,
                mArrSessionDate
            )
        }
        mSpnSessionDate!!.adapter = adapter
        if (Current != null) {
            mSpnSessionDate!!.setSelection(mArrSessionDate.indexOf(Current))
        }
    }

    private fun setEmployeeSpinner() {
        arrEmployee = mServerResponse.Data.SalesOfficer
        //callReminderService(arrEmployee[0].ID,1)
        employeeAdapter = ArrayAdapter<SalesOfficer>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, arrEmployee)
        v.spnEmployee.adapter = employeeAdapter
        if (arrEmployee.size == 1)
            v.spnEmployee.isEnabled = false
        v.spnEmployee.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                 callReminderService(arrEmployee[position].ID, 1)
                if (!arrTotalReminders.isNullOrEmpty()) {
                    arrTotalReminders.clear()
                    remindersAdapter.notifyDataSetChanged()
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun getSectionCallback(arrReminders: List<Reminder>): RecyclerSectionItemDecoration.SectionCallback {
        return object : RecyclerSectionItemDecoration.SectionCallback {
            override fun isSection(position: Int): Boolean {
                if (position == 0)
                    return true
                else if (position == arrActiveReminders.size) {
                    if (arrActiveReminders.isNotEmpty())
                        return true
                }
                return false
            }

            override fun getSectionHeader(position: Int): CharSequence {
                val title: String
                if (position == 0) {
                    if (arrActiveReminders.isNotEmpty())
                        title = "Active Reminders"
                    else
                        title = "Pending Reminders"
                } else {
                    title = "Pending Reminders"
                }
                return title
            }
        }
    }

    private fun callReminderService(SOID: Int, callType: Int) {
        if (callType == 2) {
            disposable = restService.getPendingReminders(SOID.toString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { result -> showResult(result, RequestCode.GET_REMINDERS_PENDING_REQUEST_CODE) },
                            { error -> showError(error.message) }
                    )
        } else {
            SoId = SOID
            mDialog.show()
            mDialog.setMessage("please wait...")
            disposable = restService.getActiveReminders(SOID.toString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { result -> showResult(result, RequestCode.GET_REMINDERS_ACTIVE_REQUEST_CODE) },
                            { error -> showError(error.message) }
                    )
        }

    }
    private fun callReminderDeleteAndRescheduleService(obj: Reminder, callType: Int ,Remarks: String) {
        if (callType == 1) {
            mDialog.show()
            mDialog.setMessage("please wait...")
            disposable = restService.deleteReminder(obj.ReminderID, Remarks, mServerResponse.Data.Token)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { result -> showResult(result, RequestCode.GET_REMINDERS_DELETE_REQUEST_CODE) },
                            { error -> showError(error.message) }
                    )
        }
    }

    private fun callReminderRescheduleService(obj: Reminder, callType: Int,RemaiderDate:String) {
        if (callType == 1) {
            mDialog.show()
            mDialog.setMessage("please wait...")
            disposable = restService.rescheduleReminder(obj.ReminderID,RemaiderDate, mServerResponse.Data.Token)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { result -> showResult(result, RequestCode.GET_RESCHDULE_REMINDER) },
                            { error -> showError(error.message) }
                    )

        }
    }

    private fun showResult(result: GetServerResponse, requestCode: RequestCode) {
        v.tvError2.visibility = View.GONE
        if (requestCode == RequestCode.GET_REMINDERS_ACTIVE_REQUEST_CODE) {
            arrActiveReminders = result.ActiveReminders
            callReminderService(SoId, 2)
        } else if (requestCode == RequestCode.GET_REMINDERS_PENDING_REQUEST_CODE) {
            arrPendingReminders = result.PendingReminders
            arrTotalReminders.addAll(arrActiveReminders)
            arrTotalReminders.addAll(arrPendingReminders)
            if (arrActiveReminders.isNotEmpty() || arrPendingReminders.isNotEmpty()) {
                v.rvReminders.layoutManager =
                    androidx.recyclerview.widget.LinearLayoutManager(activity)
                remindersAdapter = RemindersAdapter(arrTotalReminders, com.app.salesforce.R.layout.row_reminder, this,activity)
                val sectionItemDecoration = RecyclerSectionItemDecoration(getResources().getDimensionPixelSize(R.dimen.recycler_section_header_height), true, getSectionCallback(arrTotalReminders))
                v.rvReminders.addItemDecoration(sectionItemDecoration)
                v.rvReminders.adapter = remindersAdapter
            } else
                v.tvError2.visibility = View.VISIBLE
        } else if (requestCode == RequestCode.GET_REMINDERS_DELETE_REQUEST_CODE) {
            if (result.ResultType == 1) {
                arrTotalReminders.removeAt(deleteReminderPosition)
                remindersAdapter.notifyDataSetChanged()
            }

        }
       if(requestCode==RequestCode.GET_RESCHDULE_REMINDER){
           if(result.ResultType==1)
               Toast.makeText(activity,result.Message,Toast.LENGTH_LONG).show()
               remindersAdapter.notifyDataSetChanged()
        }
        mDialog.hide()
    }
    private fun showError(message: String?) {
        mDialog.hide()
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
        v.tvError2.visibility = View.VISIBLE
    }
    override fun deleteReminder(item: Reminder, position: Int ,Remarks : String) {
        callReminderDeleteAndRescheduleService(item, 1,Remarks)
        deleteReminderPosition = position
    }
    override fun rescheduleReminder(item: Reminder, position: Int,RemainderDate:String) {
            callReminderRescheduleService(item,1,RemainderDate)
            deleteReminderPosition = position
    }

    override fun onPause() {
        super.onPause()
        disposable.dispose()

    }

    override fun onResume() {
        super.onResume()
        initGui()

    }
}