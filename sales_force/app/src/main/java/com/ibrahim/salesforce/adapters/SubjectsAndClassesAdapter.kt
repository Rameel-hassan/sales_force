package com.app.salesforce.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import com.app.salesforce.databinding.RowSubjAndClassesBinding
import com.app.salesforce.model.SubjectAndClassesModel

class SubjectsAndClassesAdapter( val model:ArrayList<SubjectAndClassesModel>,val context: Context,val callbacks:AddRemoveItem.AddSubjClass) :RecyclerView.Adapter<SubjectsAndClassesAdapter.ViewHolder>() {



   public class ViewHolder(val view: RowSubjAndClassesBinding):RecyclerView.ViewHolder(view.root) {
        var tvSubjName: TextView = view.tvSubjetcName
         var cbSelectAll: CheckBox=view.cbSelectAll
         var rvClass:RecyclerView=view.rvClasses
        var ivNext:ImageView=view.ivNext


    }

    private var viewPool = RecycledViewPool()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectsAndClassesAdapter.ViewHolder {
       return ViewHolder(RowSubjAndClassesBinding.inflate(LayoutInflater.from(parent.context), parent, false));
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       with(holder){
           tvSubjName.text=model[position].subject.SubjectName
           cbSelectAll.setOnCheckedChangeListener(null)
           cbSelectAll.isChecked=model[position].isSelected
           cbSelectAll.setOnCheckedChangeListener { buttonView, isChecked ->
               //subject selected
               cbSelectAll.setOnCheckedChangeListener(null)
               model[absoluteAdapterPosition].isSelected=isChecked
               if(isChecked){
                   for(cls in model[position].classes){
                       cls.isSelected=true
                       callbacks.addRemoveSelectedItem(model[absoluteAdapterPosition].subject,cls,true,absoluteAdapterPosition)
                   }
               }else{
                   for(cls in model[position].classes){
                       cls.isSelected=false
                       callbacks.addRemoveSelectedItem(model[absoluteAdapterPosition].subject,cls,false,absoluteAdapterPosition)
                   }
               }

                this@SubjectsAndClassesAdapter.notifyItemChanged(absoluteAdapterPosition)
               //sendClassback
           }



           rvClass.layoutManager=LinearLayoutManager(rvClass.context,LinearLayoutManager.HORIZONTAL, false)
           var adapter:ClassRangeAdapter?=null
           adapter=ClassRangeAdapter(model[position].classes,
            { clas, isChecked,_pos ->

                    callbacks.addRemoveSelectedItem(model[absoluteAdapterPosition].subject,clas,isChecked,absoluteAdapterPosition)
                    adapter?.notifyItemChanged(_pos)

           },context)
           rvClass.adapter=adapter
           rvClass.setRecycledViewPool(viewPool)

           ivNext.setOnClickListener{
               if((rvClass.layoutManager as LinearLayoutManager).findLastVisibleItemPosition() <model.size-3){
                  rvClass.smoothScrollToPosition((rvClass.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()+2)
               }
           }

       }
    }

    override fun getItemCount()=model.size
}