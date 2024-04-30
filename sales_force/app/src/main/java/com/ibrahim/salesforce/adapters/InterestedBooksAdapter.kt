package com.app.salesforce.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Checkable
import com.app.salesforce.databinding.RowInterestedBooksItemBinding
import com.app.salesforce.response.Sery
import com.app.salesforce.response.Subject


class InterestedBooksAdapter(val list: List<Sery>, val context: Context,   val selectedSubject:Subject) : androidx.recyclerview.widget.RecyclerView.Adapter<InterestedBooksAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RowInterestedBooksItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bookNames?.text = list[position].toString()
        holder.cbxBooks.setOnClickListener(View.OnClickListener {
            if(holder.cbxBooks.isChecked){
                (holder.cbxBooks as Checkable).isChecked = true
             //  addRemoveItem.AddRemoveInterestedBook(Sery(list.get(position).ID,list.get(position).SeriesName,selectedSubject.SubjectName),true);
            }
            else if(!holder.cbxBooks.isChecked){
                if(list.size>0){
                    //addRemoveItem.AddRemoveInterestedBook(Sery(list.get(position).ID,list.get(position).SeriesName,selectedSubject.SubjectName),false);
                }
                else{

                }
            }
        })
    }

    class ViewHolder(view: RowInterestedBooksItemBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view.root) {
        val cbxBooks=view.cbItemInterestedBooks
        val bookNames = view.bookNames
    }
}