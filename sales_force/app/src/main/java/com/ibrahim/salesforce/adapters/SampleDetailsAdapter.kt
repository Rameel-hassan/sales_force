package com.app.salesforce.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.salesforce.databinding.RowSampleDetailsBinding
import com.app.salesforce.response.GetSamplesHistoryResponse.SampledDetails

class SampleDetailsAdapter(val samples:ArrayList<SampledDetails>,val callBack:(SampledDetails)->Unit) :RecyclerView.Adapter<SampleDetailsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RowSampleDetailsBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding){
            tvClass.text = samples[position].className
            tvVisitDate.text = samples[position].jobDate
            tvClass.text = samples[position].className
            tvSeries.text=samples[position].seriesName
            tvSubject.text=samples[position].subjectName
            tvSrNo.text = samples[position].srNo.toString()
            btnDelete.isEnabled=!samples[position].isDelivered
            btnDelete.setOnClickListener {
                callBack(samples[position])
            }


        }
    }

    override fun getItemCount()=samples.size
    class ViewHolder(val binding:RowSampleDetailsBinding):RecyclerView.ViewHolder(binding.root)

}