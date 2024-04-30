package com.app.salesforce.fragment

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.app.salesforce.R
import com.app.salesforce.activity.FragmentShownActivity
import com.app.salesforce.databinding.FragmentPasswordChangeBinding
import com.app.salesforce.network.RestService
import com.app.salesforce.response.GetServerResponse
import com.app.salesforce.utilities.AppKeys
import com.app.salesforce.utilities.Utility
import io.paperdb.Paper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class ChangePasswordFragment : androidx.fragment.app.Fragment() {
    private lateinit var v: FragmentPasswordChangeBinding
    private lateinit var disposable: Disposable
    private lateinit var mDialog: ProgressDialog
    private lateinit var mLoginResponse: GetServerResponse
    private lateinit var versionName: String
    public var SOID:Int = 0
    public var Token:String= ""
    private val restService by lazy { RestService.create() }

    companion object {
        fun newInstance(): ChangePasswordFragment {
//            val result = RegistrationFragment()
//            val args = Bundle()
//            args.putInt(AppKeys.ARG_CALL_FROM, callFrom)
//            result.arguments = args
            return ChangePasswordFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = FragmentPasswordChangeBinding.inflate(inflater, container, false)
        initGui()
        return v.root
    }

    public fun initGui() {
        mDialog = ProgressDialog(activity)
        mLoginResponse = Paper.book().read<GetServerResponse>(AppKeys.KEY_LOGIN_RESPONSE)
        val osname = mLoginResponse.Data.Name;
        SOID = mLoginResponse.Data.SOID;
        Token= mLoginResponse.Data.Token;
        v.TvuserName.text = osname
                v.btnnLogin.setOnClickListener {
                    startLoginProcess()
                }
       /* callChangePasswordServices(osid,token,v.ett_password.text.toString());*/
        v.ettPassword.text
        try {
            versionName = requireActivity().packageManager.getPackageInfo(requireActivity().packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        v.apppVersion.text = "V: $versionName"
    }

    fun callChangePasswordServices(SOID:Int,token:String,password:String){
       mDialog.show()
        mDialog.setMessage("Logging in, please wait...")
        disposable = restService.changePassword(SOID,token,password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result -> showResult(result) },
                        { error -> showError(error.message) }
                )
    }

    private fun showError(message: String?) {
        if (mDialog.isShowing) {
            mDialog.dismiss()
        }
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    private fun showResult(result: GetServerResponse?) {
        if (result != null) {
            if(result.ResultType==1)
                Toast.makeText(activity, result.Message, Toast.LENGTH_SHORT).show()
                Paper.book().delete(AppKeys.KEY_LOGIN_RESPONSE)
                startIntent(0)
               v.etOldPassword.visibility= View.GONE
                v.ettPassword.visibility   = View.GONE
                v.btnnLogin.text= "Back To Login"
               if (  v.btnnLogin.text=="Back To Login"){
                   v.btnnLogin.setOnClickListener {
                       Paper.book().delete(AppKeys.KEY_LOGIN_RESPONSE)
                       startIntent(0)
                   }
               }

              /*  Paper.book().delete(AppKeys.KEY_LOGIN_RESPONSE)
                 startIntent(0)*/
                TODO("not implemented")
        } //To change body of created functions use File | Settings | File Templates.
    }

    private fun startLoginProcess() {
        if (Utility.isNetworkAvailable(activity)) {
            if (!v.etOldPassword.text.toString().trim { it <= ' ' }.equals("", ignoreCase = true)) {
                if (!v.ettPassword.text.toString().trim { it <= ' ' }.equals("", ignoreCase = true)) {
                    callChangePasswordServices(SOID,Token,v.ettPassword.text.toString());
                } else {
                    Toast.makeText(activity, "Please enter New password", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(activity, "Please enter Old Passoword", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(activity, getString(R.string.str_no_internet), Toast.LENGTH_SHORT).show()
        }
    }

    private fun startIntent(type: Int) {
        val intent = Intent(activity, FragmentShownActivity::class.java)
        intent.putExtra(AppKeys.FRAGMENT_TYPE, type)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        requireActivity().finishAffinity()
    }

}


