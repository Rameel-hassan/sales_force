package com.app.salesforce.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Checkable
import com.app.salesforce.databinding.RowPublishersListItemBinding
import com.app.salesforce.response.Competitor
import com.app.salesforce.response.Sery


class PublishersListAdapter(val list: List<Competitor>, val context: Context,val addRemovePublishrs: AddRemoveItem.AddRemovePublishers,val selectedSery:Sery): androidx.recyclerview.widget.RecyclerView.Adapter<PublishersListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RowPublishersListItemBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.publisherName?.text = list[position].toString()
        holder.cbxPublisher.setOnClickListener(View.OnClickListener {
            if(holder.cbxPublisher.isChecked){
                (holder.cbxPublisher as Checkable).isChecked = true
               addRemovePublishrs.AddRemovePublishers(Competitor(list.get(position).CompetitorName,list.get(position).ID,selectedSery),true);
            }
            else if(!holder.cbxPublisher.isChecked){
                if(list.size>0){
                   addRemovePublishrs.AddRemovePublishers(Competitor(list.get(position).CompetitorName,list.get(position).ID,selectedSery),false);
                }
                else{

                }
            }
        })

    }

    class ViewHolder(view: RowPublishersListItemBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view.root) {
        val publisherName = view.publisherNames
        val cbxPublisher=view.cbItemPublisher
    }
}