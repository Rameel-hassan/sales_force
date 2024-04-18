package com.ibrahim.salesforce.fragment

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ibrahim.salesforce.BuildConfig
import com.ibrahim.salesforce.activity.DashboardActivity
import com.ibrahim.salesforce.databinding.FragmentLoginBinding
import com.ibrahim.salesforce.dialoge.DevConfirmationDialog
import com.ibrahim.salesforce.network.RestService
import com.ibrahim.salesforce.network.ServerCodes
import com.ibrahim.salesforce.response.GetServerResponse
import com.ibrahim.salesforce.utilities.AppKeys
import com.ibrahim.salesforce.utilities.Utility
import io.paperdb.Paper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class LoginFragment : Fragment() {

    private var mServerResponse: GetServerResponse? = null
    private lateinit var mDialog: ProgressDialog
    private lateinit var view: FragmentLoginBinding
    private var logoClicked: Int = 0
    private val restService by lazy { RestService.create() }
    private var isKeepLogin: Boolean = false
    private lateinit var versionName: String

    companion object {
        fun newInstance(): LoginFragment {
            return LoginFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        view = FragmentLoginBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity).supportActionBar!!.hide()
        Paper.init(activity)
        parseStoredData()
        initGui()
        return view.root

    }


    private fun parseStoredData() {
        mServerResponse = Paper.book().read<GetServerResponse>(AppKeys.KEY_LOGIN_RESPONSE)
        if (mServerResponse !== null)
            if (Paper.book().contains(AppKeys.KEY_KEEP_LOGIN)) {
                isKeepLogin = Paper.book().read<Boolean>(AppKeys.KEY_KEEP_LOGIN)
            }
    }

    private fun initGui() {
        view.cbLogin.setOnCheckedChangeListener { buttonView, isChecked -> Paper.book().write(AppKeys.KEY_KEEP_LOGIN, isChecked) }
        mDialog = ProgressDialog(activity)
        mDialog.setCancelable(false)
        try {
            versionName =  BuildConfig.VERSION_NAME;

//            versionName = requireActivity().packageManager.getPackageInfo(requireActivity().packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        view.tvVersion.text = "V: $versionName"
        view.btnLogin.setOnClickListener { startLoginProcess() }
        view.ivLogo.setOnClickListener {
            logoClicked++
            if (logoClicked == 7) {
                val newFragment = DevConfirmationDialog.newInstance()
                newFragment.isCancelable = false
                fragmentManager?.let { it1 -> newFragment.show(it1, AppKeys.DIALOG_DEV_CONFIRMATION) }
                logoClicked = 0
            }
        }

        if (isKeepLogin) {
            view.etUserName.setText(mServerResponse!!.Data.Name)
            if (!view.etUserName.text.isNotEmpty())
                view.etUserName.setSelection(view.etUserName.text.length)
            view.etPassword.setText(mServerResponse!!.Data.Password)
            if (!view.etPassword.text.isNotEmpty())
                view.etPassword.setSelection(view.etPassword.text.length)
            view.cbLogin.setChecked(true)
        }
    }

    private fun callLoginService() {
        mDialog.show()
        mDialog.setMessage("Logging in, please wait...")
//        val mService = RestClient.getInstance(activity)
//        val userObject = mService.login(view!!.et_user_name.text.toString(), view!!.et_user_name.text.toString())
//        val callbackObject = RestCallbackObject(activity, this, RequestCode.LOGIN_REQUEST_CODE).showProgress(true, 0).dontHideProgress(false)
        //userObject.enqueue(callbackObject)
        val disposable: Disposable = restService.login(view?.etUserName?.text.toString(), view?.etPassword?.text.toString())
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
        Log.e("msg", message.toString())
    }

    private fun showResult(response: GetServerResponse) {

        if (mDialog.isShowing) {
            mDialog.dismiss()
        }
        Log.e("Response", response.toString())
        Log.e("ALL DATA", response.Data.toString())
//        Log.d("ehtesham", response.Data.Class.toString())

        // Remove this
//        for (i in response.Data.Class) {
//            Log.d("ehtesham", "" + i.ClassName)
//        }

        if (response.ResultType == ServerCodes.ServerResultType.SERVER_RESULT_SUCCESS) {
            response.Data.Name = view.etUserName.text.toString()
            response.Data.Password = view.etPassword.text.toString()
            Paper.book().write(AppKeys.KEY_LOGIN_RESPONSE, response)
            Log.e("TAG", "showResult: "+response )
            if (view.cbLogin.isChecked)
                Paper.book().write(AppKeys.KEY_KEEP_LOGIN, true)
            startActivity(Intent(activity, DashboardActivity::class.java))
            requireActivity().finish()
        } else {
            Toast.makeText(activity, response.Message, Toast.LENGTH_SHORT).show()
            Log.e("TAG", "showResult: "+response.Message)
        }
    }

    private fun startLoginProcess() {
        if (Utility.isNetworkAvailable(activity)) {
            if (!view.etUserName.text.toString().trim { it <= ' ' }.equals("", ignoreCase = true)) {
                if (!view.etPassword.text.toString().trim { it <= ' ' }.equals("", ignoreCase = true)) {
                    callLoginService()
                } else {
                    Toast.makeText(activity, "Please enter password", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(activity, "Please enter username", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(activity, getString(com.ibrahim.salesforce.R.string.str_no_internet), Toast.LENGTH_SHORT).show()
        }
    }
}

