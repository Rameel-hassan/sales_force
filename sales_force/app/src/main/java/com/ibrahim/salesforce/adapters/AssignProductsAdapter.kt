package com.app.salesforce.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.salesforce.activity.AddTargetedProduct
import com.app.salesforce.databinding.RowAssignProductsBinding

class AssignProductsAdapter(
    val selectedProducts: ArrayList<AddTargetedProduct.ProductToAssign>,
    val callBack: (Boolean, AddTargetedProduct.ProductToAssign) -> Unit
) : RecyclerView.Adapter<AssignProductsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RowAssignProductsBinding.inflate(LayoutInflater.from(parent.context), parent,false));
    }

    override fun getItemCount()=selectedProducts.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding){
            tvClassName.text=selectedProducts[position].ClassName
            tvSeriesName.text=selectedProducts[position].SelectedSeries.SeriesName
            tvSubjectName.text=selectedProducts[position].SubjectName

            ibRemoveItem.setOnClickListener {
                callBack(true, selectedProducts[holder.absoluteAdapterPosition])
            }

        }

    }

    class ViewHolder(val binding: RowAssignProductsBinding) : RecyclerView.ViewHolder(binding.root){

    }

}