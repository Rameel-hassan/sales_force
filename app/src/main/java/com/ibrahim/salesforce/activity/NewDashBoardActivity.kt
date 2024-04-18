package com.ibrahim.salesforce.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.ibrahim.salesforce.adapters.DashboardAdapter
import com.ibrahim.salesforce.databinding.ActivityNewDashBoardBinding
import com.ibrahim.salesforce.network.RequestCode
import com.ibrahim.salesforce.network.RestService
import com.ibrahim.salesforce.response.GetServerResponse
import com.ibrahim.salesforce.utilities.AppKeys
import io.paperdb.Paper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class NewDashBoardActivity : AppCompatActivity() {
    private lateinit var disposable: Disposable
    private val restService by lazy { RestService.create() }
    //region flags to check if data is already loaded
    private var soPresentSet = false
    private var soAbsentSet = false
    private var schoolVisitsTodaySet = false
    private var newSchoolsTodaySet = false
    private var visitsThisMonthSet = false
    private lateinit var mLoginResponse: GetServerResponse
    private var visitsLastMonthSet = false
    private var leaveSet = false
    private var noVisitTodaySet = false
    private var allLeave = false
    private var allNoVisitToday = false
    private var allCombinedVisits = false
    private var allEventVisits = false
    private var allDealerVisits = false
    private var DealerVisitsSet = false
    private var EventsSet = false
    private var CombinedVisitSet = false
    lateinit var binding:ActivityNewDashBoardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewDashBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mLoginResponse = Paper.book().read<GetServerResponse>(AppKeys.KEY_LOGIN_RESPONSE)
        Log.d("class_name", this.javaClass.simpleName)
    }

    fun getSOPresentToday(view: View) {

        enableDisableButtons()

        if (!soPresentSet) {
            binding.progressBarCyclic.visibility = View.VISIBLE

            disposable = restService.getPresentSO(mLoginResponse.Data.SOID) //SOID
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { result -> showResult(result, RequestCode.GET_PRESENT_SO) },
                            { error -> showError(error.message) }
                    )
        } else {
            if (binding.presentSoList.visibility == View.GONE) {
                binding.presentSoList.visibility = View.VISIBLE
            } else if (binding.presentSoList.visibility == View.VISIBLE) {
                binding.presentSoList.visibility = View.GONE
            }
            enableDisableButtons()
        }
    }

    fun getSOAbsentToday(view: View) {

        enableDisableButtons()

        if (!soAbsentSet) {
            binding.progressBarCyclic.visibility = View.VISIBLE

            disposable = restService.getAbsentSO(mLoginResponse.Data.SOID) // SOID
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { result -> showResult(result, RequestCode.GET_ABSENT_SO) },
                            { error -> showError(error.message) }
                    )
        } else {
            if (binding.absentSoList.visibility == View.GONE) {
                binding.absentSoList.visibility = View.VISIBLE
            } else if (binding.absentSoList.visibility == View.VISIBLE) {
                binding.absentSoList.visibility = View.GONE
            }
            enableDisableButtons()
        }
    }

    fun getSchoolVisitsToday(view: View) {

        enableDisableButtons()

        if (!schoolVisitsTodaySet) {
            binding.progressBarCyclic.visibility = View.VISIBLE

            disposable = restService.getSchoolVisitsToday(mLoginResponse.Data.SOID) // SOID
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { result -> showResult(result, RequestCode.GET_SCHOOL_VISITS_TODAY) },
                            { error -> showError(error.message) }
                    )
        } else {
            if (binding.schoolVistsTodayList.visibility == View.GONE) {
                binding.schoolVistsTodayList.visibility = View.VISIBLE
            } else if (binding.schoolVistsTodayList.visibility == View.VISIBLE) {
                binding.schoolVistsTodayList.visibility = View.GONE
            }
            enableDisableButtons()
        }
    }

    fun getNewSchoolsToday(view: View) {

        enableDisableButtons()

        if (!newSchoolsTodaySet) {
            binding.progressBarCyclic.visibility = View.VISIBLE

            disposable = restService.getNewSchoolsToday(mLoginResponse.Data.SOID) // SOID
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { result -> showResult(result, RequestCode.GET_NEW_SCHOOLS_TODAY) },
                            { error -> showError(error.message) }
                    )
        } else {
            if (binding.newSchoolsTodayList.visibility == View.GONE) {
                binding.newSchoolsTodayList.visibility = View.VISIBLE
            } else if (binding.newSchoolsTodayList.visibility == View.VISIBLE) {
                binding.newSchoolsTodayList.visibility = View.GONE
            }
            enableDisableButtons()
        }
    }

    fun getVisitsThisMonth(view: View) {

        enableDisableButtons()

        if (!visitsThisMonthSet) {
            binding.progressBarCyclic.visibility = View.VISIBLE

            disposable = restService.getVisitsThisMonth(mLoginResponse.Data.SOID) // SOID
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { result -> showResult(result, RequestCode.GET_VISITS_THIS_MONTH) },
                            { error -> showError(error.message) }
                    )
        } else {
            if (binding.visitsThisMonthList.visibility == View.GONE) {
                binding.visitsThisMonthList.visibility = View.VISIBLE
            } else if (binding.visitsThisMonthList.visibility == View.VISIBLE) {
                binding.visitsThisMonthList.visibility = View.GONE
            }

            enableDisableButtons()
        }
    }


    fun getVisitsLastMonth(view: View) {

        enableDisableButtons()

        if (!visitsLastMonthSet) {
            binding.progressBarCyclic.visibility = View.VISIBLE

            disposable = restService.getVisitsLastMonth(Paper.book().read<GetServerResponse>(AppKeys.KEY_LOGIN_RESPONSE).Data.SOID) // SOID
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { result -> showResult(result, RequestCode.GET_VISITS_LAST_MONTH) },
                            { error -> showError(error.message) }
                    )
        } else {
            if (binding.visitsLastMonthList.visibility == View.GONE) {
                binding.visitsLastMonthList.visibility = View.VISIBLE
            } else if (binding.visitsLastMonthList.visibility == View.VISIBLE) {
                binding.visitsLastMonthList.visibility = View.GONE
            }
            enableDisableButtons()
        }
    }

    fun getLeave(view: View) {

        enableDisableButtons()

        if (!leaveSet) {
            binding.progressBarCyclic.visibility = View.VISIBLE

            disposable = restService.getLeave(Paper.book().read<GetServerResponse>(AppKeys.KEY_LOGIN_RESPONSE).Data.SOID) // SOID
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { result -> showResult(result, RequestCode.GET_LEAVE) },
                            { error -> showError(error.message) }
                    )
        } else {
            if (binding.leaveList.visibility == View.GONE) {
                binding.leaveList.visibility = View.VISIBLE
            } else if (binding.leaveList.visibility == View.VISIBLE) {
                binding.leaveList.visibility = View.GONE
            }
            enableDisableButtons()
        }
        if (allLeave) {
            Snackbar.make(binding.mainLayout, "No One on Leave Today", Snackbar.LENGTH_SHORT).show()
        }
    }

    fun getNoVisitsToday(view: View) {

        enableDisableButtons()

        if (!noVisitTodaySet) {
            binding.progressBarCyclic.visibility = View.VISIBLE

            disposable = restService.getNoVisitsToday(Paper.book().read<GetServerResponse>(AppKeys.KEY_LOGIN_RESPONSE).Data.SOID) // SOID
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { result -> showResult(result, RequestCode.GET_NO_VISITS_TODAY) },
                            { error -> showError(error.message) }
                    )
        } else {
            if (binding.noVisitTodayList.visibility == View.GONE) {
                binding.noVisitTodayList.visibility = View.VISIBLE
            } else if (binding.noVisitTodayList.visibility == View.VISIBLE) {
                binding.noVisitTodayList.visibility = View.GONE
            }
            enableDisableButtons()
        }
        if (allNoVisitToday) {
            Snackbar.make(binding.mainLayout, "No, No Visits Today", Snackbar.LENGTH_SHORT).show()
        }
    }

    fun getEventsVisits(view: View) {

        enableDisableButtons()

        if (!EventsSet) {
            binding.progressBarCyclic.visibility = View.VISIBLE

            disposable = restService.getEvents(Paper.book().read<GetServerResponse>(AppKeys.KEY_LOGIN_RESPONSE).Data.SOID) // SOID
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { result -> showResult(result, RequestCode.GET_EVENTS) },
                            { error -> showError(error.message) }
                    )
        } else {
            if (binding.eventsList.visibility == View.GONE) {
                binding.eventsList.visibility = View.VISIBLE
            } else if (binding.eventsList.visibility == View.VISIBLE) {
                binding.eventsList.visibility = View.GONE
            }
            enableDisableButtons()
        }
        if (allEventVisits) {
            Snackbar.make(binding.mainLayout, "No Events Today", Snackbar.LENGTH_SHORT).show()
        }
    }

    fun getCombinedVisits(view: View) {
        enableDisableButtons()

        if (!CombinedVisitSet) {
            binding.progressBarCyclic.visibility = View.VISIBLE

            disposable = restService.getCombinedVisits(Paper.book().read<GetServerResponse>(AppKeys.KEY_LOGIN_RESPONSE).Data.SOID) // SOID
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { result -> showResult(result, RequestCode.GET_COMBINED_VISITS) },
                            { error -> showError(error.message) }
                    )
        } else {
            if (binding.combinedVisitsList.visibility == View.GONE) {
                binding.combinedVisitsList.visibility = View.VISIBLE
            } else if (binding.combinedVisitsList.visibility == View.VISIBLE) {
                binding.combinedVisitsList.visibility = View.GONE
            }
            enableDisableButtons()
        }
        if (allCombinedVisits) {
            Snackbar.make(binding.mainLayout, "No Combined Visits Today", Snackbar.LENGTH_SHORT).show()
        }
    }

    fun getDealerVisits(view: View) {
        enableDisableButtons()

        if (!DealerVisitsSet) {
            binding.progressBarCyclic.visibility = View.VISIBLE

            disposable = restService.getDealerVisits(Paper.book().read<GetServerResponse>(AppKeys.KEY_LOGIN_RESPONSE).Data.SOID) // SOID
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { result -> showResult(result, RequestCode.GET_DEALER_VISITS) },
                            { error -> showError(error.message) }
                    )
        } else {
            if (binding.dealerVisitList.visibility == View.GONE) {
                binding.dealerVisitList.visibility = View.VISIBLE
            } else if (binding.dealerVisitList.visibility == View.VISIBLE) {
                binding.dealerVisitList.visibility = View.GONE
            }
            enableDisableButtons()
        }
        if (allDealerVisits) {
            Snackbar.make(binding.mainLayout, "No Dealer Visits Today", Snackbar.LENGTH_SHORT).show()
        }

    }

    private fun showResult(result: GetServerResponse, requestCode: RequestCode) {
        val text1: MutableList<String>? = mutableListOf()
        val text2: MutableList<String>? = mutableListOf()
        val text3: MutableList<String>? = mutableListOf()
        val text4: MutableList<String>? = mutableListOf()
        // SO Present Today
        if (requestCode == RequestCode.GET_PRESENT_SO) {
            for ((i, value) in result.PresentSo.withIndex()) {
                text1?.add(i, value.soName)
                text2?.add(i, value.dateTime.toString())
                text3?.add(i, value.area)
                text4?.add("")
            }
            setSOPresentToday(text1, text2, text3, text4)
            soPresentSet = true
        }
        // So Absent Today
        if (requestCode == RequestCode.GET_ABSENT_SO) {
            for ((i, value) in result.AbsentSo.withIndex()) {
                text1?.add(i, value.soName)
                text2?.add(i, "")
                text3?.add(i, "")
                text4?.add("")
            }
            setSOAbsentToday(text1, text2, text3, text4)
            soAbsentSet = true
        }
        // School Visits Today
        if (requestCode == RequestCode.GET_SCHOOL_VISITS_TODAY) {
            for ((i, value) in result.Schools.withIndex()) {
                text1?.add(i, "" + value.soName)
                text2?.add(i, "No of visits: " + value.noOfVisits.toString())
                text3?.add(i, "Online visits: " + value.onlineVisits.toString())
                text4?.add(i, "Offline visits: " + value.offlineVisits.toString())
            }
            setSchoolVisitsToday(text1, text2, text3, text4)
            schoolVisitsTodaySet = true
        }
        // New Schools Today
        if (requestCode == RequestCode.GET_NEW_SCHOOLS_TODAY) {
            for ((i, value) in result.NewSchoolsToday.withIndex()) {
                text1?.add(i, value.soName)
                text2?.add(i, value.newSchoolsCount.toString())
                text3?.add("")
                text4?.add("")

            }
            setNewSchoolsToday(text1, text2, text3, text4)
            newSchoolsTodaySet = true
        }
        // Visits This Month
        if (requestCode == RequestCode.GET_VISITS_THIS_MONTH) {
            for ((i, value) in result.Schools.withIndex()) {
                text1?.add(i, value.soName)
                text2?.add(i, "Number of visits: " + value.noOfVisits.toString())
                text3?.add(i, "Online visits: " + value.onlineVisits.toString())
                text4?.add(i, "Offline visits: " + value.offlineVisits.toString())
            }
            setVisitsThisMonth(text1, text2, text3, text4)
            visitsThisMonthSet = true
        }
        // VisitsLastMonth
        if (requestCode == RequestCode.GET_VISITS_LAST_MONTH) {
            for ((i, value) in result.Schools.withIndex()) {
                text1?.add(i, value.soName)
                text2?.add(i, "Number of visits: " + value.noOfVisits.toString())
                text3?.add(i, "Online visits: " + value.onlineVisits.toString())
                text4?.add(i, "Offline visits: " + value.offlineVisits.toString())
            }
            setVisitsLastMonth(text1, text2, text3, text4)
            visitsLastMonthSet = true
        }
        if (requestCode == RequestCode.GET_LEAVE) {
            leaveSet = true
            if (!result.PresentSo.isNullOrEmpty()) {
                for ((i, value) in result.PresentSo.withIndex()) {
                    text1?.add(i, value.soName)
                    text2?.add(i, value.reason)
                    text3?.add(i, value.dateTime)
                    text4?.add("")

                }
                setLeave(text1, text2, text3, text4)

            } else {
                allLeave = true
                Snackbar.make(binding.mainLayout, "No One on Leave Today", Snackbar.LENGTH_SHORT).show()
            }

        }
        if (requestCode == RequestCode.GET_NO_VISITS_TODAY) {
            noVisitTodaySet = true
            if (!result.NoVisitsToday.isNullOrEmpty()) {
                for ((i, value) in result.NoVisitsToday.withIndex()) {
                    text1?.add(i, value.soName)
                    text2?.add("")
                    text3?.add("")
                    text4?.add("")
                }
                setNoVisitToday(text1, text2, text3, text4)
            } else {
                allNoVisitToday = true
                Snackbar.make(binding.mainLayout, "No No Visits Today", Snackbar.LENGTH_SHORT).show()
            }

        }
        if (requestCode == RequestCode.GET_DEALER_VISITS) {
            DealerVisitsSet = true
            if (!result.PresentSo.isNullOrEmpty()) {
                for ((i, value) in result.PresentSo.withIndex()) {
                    text1?.add(i, value.soName)
                    text2?.add(i, value.noOfVisits.toString())
                    text3?.add("")
                    text4?.add("")
                }
                setDealerVisitsToday(text1, text2, text3, text4)
            } else {
                allDealerVisits = true
                Snackbar.make(binding.mainLayout, "No Dealer Visits Today", Snackbar.LENGTH_SHORT).show()
            }

        }
        if (requestCode == RequestCode.GET_EVENTS) {
            EventsSet = true
            if (!result.PresentSo.isNullOrEmpty()) {
                for ((i, value) in result.PresentSo.withIndex()) {
                    text1?.add(i, value.soName)
                    text2?.add(i, value.schoolName)
                    text3?.add(i, value.visitPurpose)
                    text4?.add("")
                }
                setEventVisitsToday(text1, text2, text3, text4)
            } else {
                allEventVisits = true
                Snackbar.make(binding.mainLayout, "No Events Today", Snackbar.LENGTH_SHORT).show()
            }

        }
        if (requestCode == RequestCode.GET_COMBINED_VISITS) {
            CombinedVisitSet = true
            if (!result.PresentSo.isNullOrEmpty()) {
                for ((i, value) in result.PresentSo.withIndex()) {
                    if (value.noOfSchoolsVisited != 0 && value.noOfSchoolsVisited != null) {
                        text1?.add(i, value.soName + "," + " " + value.noOfSchoolsVisited + " " + "Vendors" + " " + "visited with" + " " + value.secondSO)
                        text2?.add("")
                        text3?.add("")
                        text4?.add("")
                    } else {
                        text1?.add(i, value.soName + " " + value.visitPurpose + " " + "with" + " " + value.secondSO)
                        text2?.add("")
                        text3?.add("")
                        text4?.add("")
                    }
                }
                setCombinedVisitsToday(text1, text2, text3, text4)
            } else {
                allCombinedVisits = true;
                Snackbar.make(binding.mainLayout, "No Combined Visits Today", Snackbar.LENGTH_SHORT).show()
            }

        }
        binding.progressBarCyclic.visibility = View.GONE
        enableDisableButtons()
    }


    // Present SO Recycle View
    private fun setSOPresentToday(text1: MutableList<String>?, text2: MutableList<String>?, text3: MutableList<String>?, text4: MutableList<String>?) {
        binding.presentSoList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        binding.presentSoList.adapter = DashboardAdapter(text1, text2, text3, text4, this)
        binding.presentSoList.visibility = View.VISIBLE
    }

    // Absent SO Recycle View
    private fun setSOAbsentToday(text1: MutableList<String>?, text2: MutableList<String>?, text3: MutableList<String>?, text4: MutableList<String>?) {
        binding.absentSoList.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(this)
        binding.absentSoList.adapter = DashboardAdapter(text1, text2, text3, text4, this)
        binding.absentSoList.visibility = View.VISIBLE
    }

    // School Visits Today  RecyclerView
    private fun setSchoolVisitsToday(text1: MutableList<String>?, text2: MutableList<String>?, text3: MutableList<String>?, text4: MutableList<String>?) {
        binding.schoolVistsTodayList.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(this)
        binding.schoolVistsTodayList.adapter = DashboardAdapter(text1, text2, text3, text4, this)
        binding.schoolVistsTodayList.visibility = View.VISIBLE
    }

    // New Schools Today Recycle View
    private fun setNewSchoolsToday(text1: MutableList<String>?, text2: MutableList<String>?, text3: MutableList<String>?, text4: MutableList<String>?) {
        binding.newSchoolsTodayList.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(this)
        binding.newSchoolsTodayList.adapter = DashboardAdapter(text1, text2, text3, text4, this)
        binding.newSchoolsTodayList.visibility = View.VISIBLE
    }

    // Visits This Month Recycle View
    private fun setVisitsThisMonth(text1: MutableList<String>?, text2: MutableList<String>?, text3: MutableList<String>?, text4: MutableList<String>?) {
        binding.visitsThisMonthList.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(this)
        binding.visitsThisMonthList.adapter = DashboardAdapter(text1, text2, text3, text4, this)
        binding.visitsThisMonthList.visibility = View.VISIBLE
    }

    // Visits Last Month Recycler View
    private fun setVisitsLastMonth(text1: MutableList<String>?, text2: MutableList<String>?, text3: MutableList<String>?, text4: MutableList<String>?) {
        binding.visitsLastMonthList.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(this)
        binding.visitsLastMonthList.adapter = DashboardAdapter(text1, text2, text3, text4, this)
        binding.visitsLastMonthList.visibility = View.VISIBLE
    }

    //Leave RecyclerView
    private fun setLeave(text1: MutableList<String>?, text2: MutableList<String>?, text3: MutableList<String>?, text4: MutableList<String>?) {
        binding.leaveList.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(this)
        binding.leaveList.adapter = DashboardAdapter(text1, text2, text3, text4, this)
        binding.leaveList.visibility = View.VISIBLE
    }

    //No visitToday RecyclerView

    private fun setNoVisitToday(text1: MutableList<String>?, text2: MutableList<String>?, text3: MutableList<String>?, text4: MutableList<String>?) {
        binding.noVisitTodayList.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(this)
        binding.noVisitTodayList.adapter = DashboardAdapter(text1, text2, text3, text4, this)
        binding.noVisitTodayList.visibility = View.VISIBLE
    }

    private fun setDealerVisitsToday(text1: MutableList<String>?, text2: MutableList<String>?, text3: MutableList<String>?, text4: MutableList<String>?) {
        binding.dealerVisitList.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(this)
        binding.dealerVisitList.adapter = DashboardAdapter(text1, text2, text3, text4, this)
        binding.dealerVisitList.visibility = View.VISIBLE
    }

    private fun setEventVisitsToday(text1: MutableList<String>?, text2: MutableList<String>?, text3: MutableList<String>?, text4: MutableList<String>?) {
        binding.eventsList.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(this)
        binding.eventsList.adapter = DashboardAdapter(text1, text2, text3, text4, this)
        binding.eventsList.visibility = View.VISIBLE
    }

    private fun setCombinedVisitsToday(text1: MutableList<String>?, text2: MutableList<String>?, text3: MutableList<String>?, text4: MutableList<String>?) {
        binding.combinedVisitsList.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(this)
        binding.combinedVisitsList.adapter = DashboardAdapter(text1, text2, text3, text4, this)
        binding.combinedVisitsList.visibility = View.VISIBLE
    }


    private fun showError(message: String?) {
        if (message == null) {
            Toast.makeText(this, "Unknown Error: null", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
        }
        binding.progressBarCyclic.visibility = View.GONE
        enableDisableButtons()
    }

    private fun enableDisableButtons() {

        if (binding.soPresentTodayLayout.isEnabled) {
            binding.soPresentTodayLayout.isEnabled = false
            binding.soAbsentTodayLayout.isEnabled = false
            binding.schoolVistsTodayList.isEnabled = false
            binding.newSchoolsTodayLayout.isEnabled = false
            binding.visitsThisMonthLayout.isEnabled = false
            binding.visitsLastMonthLayout.isEnabled = false
            binding.leaveLayout.isEnabled = false
            binding.noVisitTodayLayout.isEnabled = false
            binding.dealerVisitList.isEnabled = false
            binding.eventsLayout.isEnabled = false
            binding.combinedVisitLayout.isEnabled = false

        } else {
            binding.soPresentTodayLayout.isEnabled = true
            binding.soAbsentTodayLayout.isEnabled = true
            binding.schoolVistsTodayList.isEnabled = true
            binding.newSchoolsTodayLayout.isEnabled = true
            binding.visitsThisMonthLayout.isEnabled = true
            binding.visitsLastMonthLayout.isEnabled = true
            binding.leaveLayout.isEnabled = true
            binding.noVisitTodayLayout.isEnabled = true
            binding.dealerVisitList.isEnabled = true
            binding.eventsLayout.isEnabled = true
            binding.combinedVisitLayout.isEnabled = true
        }
    }

}
