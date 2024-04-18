package com.ibrahim.salesforce.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ibrahim.salesforce.databinding.RowSamplesHistoryBinding
import com.ibrahim.salesforce.response.GetSamplesHistoryResponse.Sample

class SamplesHistoryAdapter(val samples: ArrayList<Sample>,val callBack: (Boolean,Sample) -> Unit): RecyclerView.Adapter<SamplesHistoryAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RowSamplesHistoryBinding.inflate(LayoutInflater.from(parent.context), parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding){
            tvSrNo.text= samples[position].srNo.toString().trim()
            tvContactPerson.text=samples[position].contactNo.trim()
            tvVisitDate.text=samples[position].jobDate.trim()
            tvRetailerName.text= samples[position].shopName.trim()
            tvAddress.text=samples[position].shopAddress.trim()
            root.setOnClickListener {
                callBack(true,samples[position])
            }
        }
    }

    override fun getItemCount()=samples.size

     open class ViewHolder(val binding:RowSamplesHistoryBinding):RecyclerView.ViewHolder(binding.root) {

    }
}