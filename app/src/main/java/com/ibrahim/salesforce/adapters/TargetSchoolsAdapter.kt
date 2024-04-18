package com.ibrahim.salesforce.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ibrahim.salesforce.databinding.RowTargetSchoolsBinding
import com.ibrahim.salesforce.model.GetTodayTargetedSchools

class TargetSchoolsAdapter(private val targetedSchools: ArrayList<GetTodayTargetedSchools.TodayTargetedSchool?>?) :
    RecyclerView.Adapter<TargetSchoolsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowTargetSchoolsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return targetedSchools!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val targetedSchool = targetedSchools?.get(position)
        if (targetedSchool != null) {
            holder.bind(targetedSchool)
        }
    }

    class ViewHolder(private val row: RowTargetSchoolsBinding) : RecyclerView.ViewHolder(row.root) {
        fun bind(targetedSchool: GetTodayTargetedSchools.TodayTargetedSchool) {
            // Bind your data to the views in the layout here
            row.textViewVendor.text = targetedSchool.vendorName
            row.textViewContactPerson.text = targetedSchool.principalName
            row.textViewContactNumber.text = targetedSchool.phoneNumber
            row.textViewArea.text = targetedSchool.areaName
            // Set the status based on "visited" field
            val statusText = if (targetedSchool.visited) {
                "Visited"
            } else {
                "Pending"
            }
            row.textViewStatus.text = statusText
        }
    }
}
