package com.ibrahim.salesforce.fragment


import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.ibrahim.salesforce.adapters.SessionWiseSchoolsAdapter
import com.ibrahim.salesforce.databinding.FragmentSessionWiseSchoolsBinding
import com.ibrahim.salesforce.network.RequestCode
import com.ibrahim.salesforce.network.RestService
import com.ibrahim.salesforce.response.GetServerResponse
import com.ibrahim.salesforce.response.SalesOfficer
import com.ibrahim.salesforce.response.School
import com.ibrahim.salesforce.utilities.AppKeys
import io.paperdb.Paper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class SessionWiseSchools : androidx.fragment.app.Fragment() {
    private lateinit var mLoginResponse: GetServerResponse
    private lateinit var mView: FragmentSessionWiseSchoolsBinding
    private lateinit var mArrMonths: MutableList<String>
    private lateinit var mArrEmployee: List<SalesOfficer>
    private lateinit var mArrSchools: List<School>
    private lateinit var getCustomerRespone: GetServerResponse
    private lateinit var disposable: Disposable
    private lateinit var mDialog: ProgressDialog
    private var employeePosition: Int=0
    private var monthPosition: Int=0
    private var schoolAdapter: SessionWiseSchoolsAdapter? = null
    private val restService by lazy { RestService.create() }
    companion object {
        fun newInstance(): SessionWiseSchools {
            return SessionWiseSchools()
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mView = FragmentSessionWiseSchoolsBinding.inflate(inflater, container, false)
        Paper.init(activity)
        init()
        return mView.root
    }
    private fun init(){
        mDialog = ProgressDialog(activity)
        mDialog.setCancelable(false)
        mLoginResponse = Paper.book().read<GetServerResponse>(AppKeys.KEY_LOGIN_RESPONSE)
        mArrMonths=ArrayList()
        mArrSchools=ArrayList();
        setRtailerSpinner()
        setMonthsSpinner()

    }
    private fun setRtailerSpinner() {
        mArrEmployee=mLoginResponse.Data.SalesOfficer
        val adapter = ArrayAdapter<SalesOfficer>(requireActivity(), android.R.layout.simple_spinner_dropdown_item,mArrEmployee)
        mView.spnNewSchools.adapter = adapter
        mView.spnNewSchools.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
              employeePosition=position;
                if(monthPosition!=0){
                    callGetSchoolsService(mArrEmployee.get(position).ID,mArrMonths.get(monthPosition))
                }
                if(!mArrSchools.isNullOrEmpty()){
                   schoolAdapter!!.notifyDataSetChanged()
                    mView.tvEror.visibility=View.GONE
                }
            }

        }
    }
    private fun setMonthsSpinner() {
mArrMonths.add(0,"Select Month")
       mArrMonths.add("January")
        mArrMonths.add("February")
        mArrMonths.add("march")
        mArrMonths.add("april")
        mArrMonths.add("May")
        mArrMonths.add("June")
        mArrMonths.add("July")
        mArrMonths.add("August")
        mArrMonths.add("September")
        mArrMonths.add("October")
        mArrMonths.add("November")
        mArrMonths.add("December")
        val adapter = ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_dropdown_item,mArrMonths)
        mView.spnNewMonth.adapter = adapter
        mView.spnNewMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position != 0){
                    monthPosition=position
callGetSchoolsService(mArrEmployee.get(employeePosition).ID,mArrMonths.get(position))
            }}
    }

}

private fun callGetSchoolsService(soId:Int,sessionMonth:String){

        mDialog.show()
        mDialog.setMessage("Fetching  please wait...")
        disposable = restService.getRetailerSession(soId,sessionMonth)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result -> showResult(result, RequestCode.GET_RETAILER_SESSION_WISE) },
                        { error -> showError(error.message) }
                )

}

    private fun showResult(response: GetServerResponse, requestCode: RequestCode) {
        if (requestCode == RequestCode.GET_RETAILER_SESSION_WISE) {
            mView.tvEror.visibility=View.GONE
            getCustomerRespone=response
            mArrSchools=getCustomerRespone.RetailerSessionWise
            Log.e("ALL LIST",getCustomerRespone.RetailerSessionWise.toString());
            if(mArrSchools.isNotEmpty()) {
                mView.rcSchoolsSessionWise.visibility=View.VISIBLE;
                mView.tvEror.visibility=View.GONE
                mView.rcSchoolsSessionWise.layoutManager =
                    androidx.recyclerview.widget.LinearLayoutManager(activity)
               schoolAdapter = SessionWiseSchoolsAdapter(mArrSchools)
               mView.rcSchoolsSessionWise.adapter = schoolAdapter
                mView.rcSchoolsSessionWise.visibility = View.VISIBLE
            }else
            { mView.tvEror.visibility = View.VISIBLE
            mView.rcSchoolsSessionWise.visibility=View.GONE
            }
            mDialog.hide()
        }
       /* else{
            mDialog.hide()
            Toast.makeText(activity,"C", Toast.LENGTH_LONG).show()
            requireActivity().onBackPressed()
        }*/
        mDialog.hide()
    }

    private fun showError(message: String?) {
        mDialog.hide()
        Toast.makeText(activity,"No Schools Associated", Toast.LENGTH_LONG).show()
       // requireActivity().onBackPressed()

    }

}
