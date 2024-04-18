package com.ibrahim.salesforce.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ibrahim.salesforce.databinding.RowVisitHistoryBinding
import com.ibrahim.salesforce.response.GetVisitHistoryResponse.VisitHistory

interface OnItemClick {
    fun infoClicked(position: Int)
}
class VisitHistoryAdapter(var list:ArrayList<VisitHistory>, var listener : OnItemClick):
    RecyclerView.Adapter<VisitHistoryAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RowVisitHistoryBinding.inflate(LayoutInflater.from(parent.context), parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding){
//            tvAddress.text=list[position].address.trim()?:""
//            tvComment.text=list[position].comments.trim()?:""
            if ( list[position].visitType != null)
                tvVisitType.text = list[position].visitType.trim()?:"";
            else
                tvVisitType.text = "online"
            tvPurposeOfVisit.text=list[position].purposeOfVisit.trim()?:""
            tvVisitDate.text=list[position].visitDate.trim()?:""
//            tvContactPerson.text=list[position].contactPerson.trim()?:""
            tvRetailerName.text=list[position].retailerName.trim()?:""
            tvSrNo.text= list[position].srNo.toString().trim()?:""

            tvFullDetails.setOnClickListener(View.OnClickListener {
                listener.infoClicked(position);
            })

        }
    }

    override fun getItemCount()=list.size

    class ViewHolder(val binding:RowVisitHistoryBinding):RecyclerView.ViewHolder(binding.root) {

    }

}