package com.ibrahim.salesforce.adapters

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.ibrahim.salesforce.databinding.RowVisitPlansListItemBinding
import com.ibrahim.salesforce.response.GVisitPlans
import com.ibrahim.salesforce.response.NextVisitPlanData
import com.ibrahim.salesforce.response.VisitPlans
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class MyVisitPlansAdapter(val list: ArrayList<GVisitPlans>, val toDate: String) :
    androidx.recyclerview.widget.RecyclerView.Adapter<MyVisitPlansAdapter.ViewHolder>() {

    var trueList: ArrayList<GVisitPlans> = ArrayList()
    var mUList: ArrayList<NextVisitPlanData> = ArrayList()
    var uVisitPlans: ArrayList<VisitPlans> = ArrayList()
    var myToDate: String? = toDate
    lateinit var onClickRowClass: OnClickRowSchoolListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(

                RowVisitPlansListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
        val outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyy", Locale.ENGLISH)
        val date = LocalDate.parse("2018-04-10T04:00:00", inputFormatter)
        val datee = LocalDate.parse(list[position].PlanDate, inputFormatter)
        val formattedDate = outputFormatter.format(datee)
        Log.e("date", formattedDate) // prints 10-04-2018

        holder.IsChecked.isChecked = false
        holder.planDate.text = formattedDate.toString()
        holder.planDetails.setText(list[position].PlanDetails)

        // Update and Insert from same form work
/*
        // Get the last date from server.
        val s = LocalDate.parse(list[itemCount-1].PlanDate, inputFormatter)

//        var s = list[itemCount-1].PlanDate
        var l =s!!.toString().replace("\\D+".toRegex(), "")
        Log.e("lastDate1", ": ${itemCount-1}")
        Log.e("lastDate1", ":: $s")
        Log.e("lastDate1", "::: $l")

        val fd = l[6]
        val fdd = l[7]
        val fddd = "$fd$fdd"
        Log.e("lastDatetdd", "fromDate $fddd")

        var m = toDate.replace("\\D+".toRegex(), "")
        var td = m[0]
        var tdd = m[1]
        var tddd = "$td$tdd"
        Log.e("lastDatetdd", "toDate $tddd")

        var fDate = fddd.toInt()
        var tDate = tddd.toInt()

        if (list.last().equals(itemCount-1)){
            if (fddd.toInt() < tddd.toInt()){
                Log.e("lastIndex", "Index at end")
            }
        }
        */
        // End Update and Insert from same form work

        holder.IsChecked.setOnCheckedChangeListener { compoundButton, b ->

            var muvp: NextVisitPlanData

            if (holder.planDetails.text.toString() == list[position].PlanDetails) {
                holder.IsChecked.isChecked = false
                Log.e("aaa", "true")
                Toast.makeText(holder.IsChecked.context, "Update Plan First", Toast.LENGTH_SHORT).show()
            } else {

                if (b) {
                    list[position].IsChecked = b
                    list[position].PlanDate = holder.planDate.text.toString()
                    list[position].PlanDetails = holder.planDetails.text.toString()
                    Log.e("list", "list-pos true " + list[position])
                    trueList.add(list[position])

                    muvp = NextVisitPlanData(
                        b,
                        holder.planDate.text.toString(),
                        holder.planDetails.text.toString()
                    )
                    Log.e("list", "list-pos true $muvp")
                    mUList.add(muvp)

//                onClickRowClass.onClickCheckBox(position, trueList, holder.planDate.text.toString(), holder.planDetails.text.toString())
                } else {
                    Log.e("mchkbox", "false $b")
                    list[position].IsChecked = b
                    list[position].PlanDate = holder.planDate.text.toString()
                    list[position].PlanDetails = holder.planDetails.text.toString()
                    Log.e("list", "list-pos false " + list[position])
                    trueList.remove(list[position])
//
                    muvp = NextVisitPlanData(
                        b,
                        holder.planDate.text.toString(),
                        holder.planDetails.text.toString()
                    )
                    Log.e("list", "list-pos false $muvp")
                    mUList.remove(muvp)
                }

                Log.e("list", "list-pos 1" + list[position])
                onClickRowClass.onClickCheckBox(
                    position,
                    trueList,
                    mUList
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: RowVisitPlansListItemBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view.root) {
        var IsChecked: CheckBox = view.checkBoxUpdate!!
        val planDate = view.tvPlanDate!!
        val planDetails: EditText = view.tvPlanDetails!!
    }

    fun setOnClickMenuListener(onClickRowSchoolListener: OnClickRowSchoolListener) {
        this.onClickRowClass = onClickRowSchoolListener
    }

    interface OnClickRowSchoolListener {
        fun onClickCheckBox(
            position: Int,
            visitPlans: ArrayList<GVisitPlans>,
            uVisitPlans: ArrayList<NextVisitPlanData>?
        )
    }
}