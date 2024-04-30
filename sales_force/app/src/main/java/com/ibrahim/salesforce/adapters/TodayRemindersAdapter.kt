package com.app.salesforce.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.salesforce.databinding.RowTodayRemindersBinding
import com.app.salesforce.response.TodayActiveReminders

class TodayRemindersAdapter(val reminders:ArrayList<TodayActiveReminders>) : RecyclerView.Adapter<TodayRemindersAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RowTodayRemindersBinding.inflate(LayoutInflater.from(parent.context), parent, false));
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(reminders[position]){
                tvAddress.text=address
                tvAreaName.text=areaName
                tvStudntsStrength.text=studentStrength
                tvSchoolName.text=schoolName
                tvLastVistDate.text=lastVisitDate.substring(0,16)
                tvLastVisitPurpose.text=lastVisitPurpose
                tvComent.text=lastVisitComment
            }
        }
    }


    override fun getItemCount()=reminders.size
    class ViewHolder(val view: RowTodayRemindersBinding):RecyclerView.ViewHolder(view.root) {

        val tvSchoolName=view.tvSchoolName
        val tvAddress=view.tvAddress
        val tvAreaName=view.tvArea
        val tvStudntsStrength=view.tvStdStrength
        val tvLastVistDate=view.tvDate
        val tvLastVisitPurpose=view.tvLastVisitPurpose
        val tvComent=view.tvCmnt

    }
}