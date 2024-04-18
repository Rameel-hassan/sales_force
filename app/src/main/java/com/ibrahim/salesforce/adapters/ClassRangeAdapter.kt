package com.ibrahim.salesforce.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ibrahim.salesforce.databinding.RowClassListItemBinding
import com.ibrahim.salesforce.response.Clas


class ClassRangeAdapter(val list: List<Clas>, val classCallBacks:AddRemoveItem.AddRemoveClass, val context: Context) : RecyclerView.Adapter<ClassRangeAdapter.ViewHolder>() {

    //var selectedSeries:Sery ?= null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RowClassListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount()=list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.classList.text = list[position].ClassName
        holder.cb_ClassItem.setOnCheckedChangeListener(null)
        holder.cb_ClassItem.isChecked=list[position].isSelected
        val adapterPosition=position
        holder.cb_ClassItem?.setOnCheckedChangeListener { _, isChecked ->
            holder.cb_ClassItem.setOnCheckedChangeListener(null)
            list[adapterPosition].isSelected = isChecked
            classCallBacks.addRemoveItem(list[adapterPosition], holder.cb_ClassItem.isChecked,position);
            notifyItemChanged(adapterPosition)
        }

    }
    class ViewHolder(val view: RowClassListItemBinding) : RecyclerView.ViewHolder(view.root) {
        val classList = view.classList
        val cb_ClassItem=view.cbItem
    }

}