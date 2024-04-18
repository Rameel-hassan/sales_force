package com.ibrahim.salesforce.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ibrahim.salesforce.R
import com.ibrahim.salesforce.dialoge.MultiPurposeDialougeFragment

class MultiPurposeActivityMenu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multi_purpose)
        Log.d("class_name", this.javaClass.simpleName)
    }


    fun eventCombinedVisits(view: View) {
        showEditDialog(1)
    }

    fun eventEvents(view: View) {
        showEditDialog(2)
    }

    fun EventDealerVisit(view: View) {
        showEditDialog(3)
    }

    private fun showEditDialog(type: Int) {
        val fm = supportFragmentManager
        val editNameDialogFragment = MultiPurposeDialougeFragment.newInstance("Information", this, type)
        editNameDialogFragment.show(fm, "fragment_edit_name")
    }


}
