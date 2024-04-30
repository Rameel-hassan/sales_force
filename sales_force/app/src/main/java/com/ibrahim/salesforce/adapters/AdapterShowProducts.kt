package com.app.salesforce.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.salesforce.databinding.RowShowProductsBinding
import com.app.salesforce.response.TargatedProducts

class AdapterShowProducts (
    val selectedProducts: ArrayList<TargatedProducts.TargetedProducts>,
) : RecyclerView.Adapter<AdapterShowProducts.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RowShowProductsBinding.inflate(LayoutInflater.from(parent.context), parent,false));
    }

    override fun getItemCount()=selectedProducts.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding){
            tvClassName.text=selectedProducts[position].className
            tvSeriesName.text=selectedProducts[position].seriesName
            tvSubjectName.text=selectedProducts[position].subjectName
        }

    }

    class ViewHolder(val binding: RowShowProductsBinding) : RecyclerView.ViewHolder(binding.root){

    }

}