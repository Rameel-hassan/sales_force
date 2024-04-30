package com.app.salesforce.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.salesforce.databinding.RowTargetedSchoolsBinding
import com.app.salesforce.response.TargatedSchoolList

class TargetedSchoolsAdapter (val samples: ArrayList<TargatedSchoolList.TargetedSchool>, val callBack: (Boolean, TargatedSchoolList.TargetedSchool) -> Unit): RecyclerView.Adapter<TargetedSchoolsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RowTargetedSchoolsBinding.inflate(LayoutInflater.from(parent.context), parent,false))
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding){
            tvSrNo.text= samples[position].srNo.toString().trim()
            tvContactPerson.text=samples[position].Phone1
            tvSchoolName.text= samples[position].Name.trim()
            tvAddress.text = samples[position].ShopName?.trim() ?:""
            tvAssignProduct.visibility=samples[position].assignProductVisibility
            tvView.visibility=samples[position].viewVisibility
            checkBox.isChecked=samples[position].isProductsAssigned
            tvAssignProduct.setOnClickListener {
                callBack(true,samples[position])
            }
            tvView.setOnClickListener {
                callBack(false,samples[position])
            }
        }
    }

    override fun getItemCount()=samples.size

    open class ViewHolder(val binding: RowTargetedSchoolsBinding): RecyclerView.ViewHolder(binding.root) {

    }
}