package com.app.salesforce.dialoge

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.app.salesforce.R
import com.app.salesforce.activity.UtilityActivity
import com.app.salesforce.databinding.DialogDevConfirmationLayoutBinding


/**
 * Created by Hassan Usman on 12/1/2019.
 */
class DevConfirmationDialog : androidx.fragment.app.DialogFragment() {

    private lateinit var mView: DialogDevConfirmationLayoutBinding
    private var content: String? = null

    companion object {
        fun newInstance(): DevConfirmationDialog {
            return DevConfirmationDialog()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        content = arguments?.getString("content")

        val style = androidx.fragment.app.DialogFragment.STYLE_NO_FRAME
        val theme = R.style.DialogTheme
        //setStyle(style, theme)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = DialogDevConfirmationLayoutBinding.inflate(inflater, container, false)
        initGui()
        return mView.root
    }

    private fun initGui() {
        mView.btnOk.setOnClickListener{
            if(mView.etDevCode.text.trim().toString()==("787898")) {
                startActivity(Intent(activity, UtilityActivity::class.java))
                dismiss()
                requireActivity().finish()
            }else {
                Toast.makeText(activity, "Wrong Code. Try again!", Toast.LENGTH_LONG).show()
                //dismiss()
            }
        }
        mView.btnCancel.setOnClickListener{dismiss()}
    }
}