package com.ibrahim.salesforce.activity

import android.Manifest
import android.app.ActivityManager
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ibrahim.salesforce.R
import com.ibrahim.salesforce.base.SFApplication
import com.ibrahim.salesforce.fragment.ChangePasswordFragment
import com.ibrahim.salesforce.fragment.DailyActivityFragment
import com.ibrahim.salesforce.fragment.GetNextWeekVisitPlanFragment
import com.ibrahim.salesforce.fragment.LoginFragment
import com.ibrahim.salesforce.fragment.MapViewFragment
import com.ibrahim.salesforce.fragment.MyVisitsFragment
import com.ibrahim.salesforce.fragment.Reminders
import com.ibrahim.salesforce.fragment.SchoolRegFragment
import com.ibrahim.salesforce.fragment.SessionWiseSchools
import com.ibrahim.salesforce.fragment.SetNextWeekVisitPlanFragment
import com.ibrahim.salesforce.fragment.ShopRegFragment
import com.ibrahim.salesforce.services.LocationUpdateService
import com.ibrahim.salesforce.utilities.AppKeys

class FragmentShownActivity : AppCompatActivity(){

    private val CAMERA_INTENT = 30

    private var fragmentType: Int = 0
    private lateinit var calledFragment: androidx.fragment.app.Fragment
    private val fragmentTagName = arrayOf(SFApplication.getAppResources().getString(R.string.login),
        SFApplication.getAppResources().getString(R.string.school_form),
            SFApplication.getAppResources().getString(R.string.shop_form),
        SFApplication.getAppResources().getString(R.string.my_visits), "Your Current Location",
        SFApplication.getAppResources().getString(R.string.reminders),
        SFApplication.getAppResources().getString(R.string.test_fragment),
        SFApplication.getAppResources().getString(R.string.change_passowrd_view), "Session Wise Schools",
        SFApplication.getAppResources().getString(R.string.next_week_visit_plan), "Next Week Visit Plan",
        SFApplication.getAppResources().getString(R.string.get_next_week_visit_plan), "Get Next Week Visit Plan"
    )
    private val fragmentsArray = arrayOf<androidx.fragment.app.Fragment>(
        LoginFragment.newInstance(),
        SchoolRegFragment.newInstance(),
        ShopRegFragment.newInstance(),
        MyVisitsFragment.newInstance(),
        MapViewFragment.newInstance(),
        Reminders.newInstance(),
        DailyActivityFragment.newInstance(),
        ChangePasswordFragment.newInstance(),
        SessionWiseSchools.newInstance(),
        SetNextWeekVisitPlanFragment.newInstance(),
        GetNextWeekVisitPlanFragment.newInstance()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_shown)
        parseBundle()
        Log.d("class_name", this.javaClass.simpleName)
    }
    private fun parseBundle() {
        if (intent != null) {
            fragmentType = intent.getIntExtra(AppKeys.FRAGMENT_TYPE, 0)
        }
        val actionbar = supportActionBar
        actionbar!!.title = fragmentTagName[fragmentType]

        if(fragmentType!=0) {
            actionbar.setDisplayHomeAsUpEnabled(true)
                                  actionbar.setDisplayHomeAsUpEnabled(true)
        }
//        32

        title = fragmentTagName[fragmentType]
        showFragment(fragmentsArray[fragmentType])
    }
    private fun showFragment(frag: androidx.fragment.app.Fragment) {
        supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment, frag, fragmentTagName[fragmentType])
                .addToBackStack(null)
                .commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode > 1000)
            return
        for (fragment in supportFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun checkAndRequestPermissions(): Boolean {
        val cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val permissionLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)


        val listPermissionsNeeded = ArrayList<String>()

        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toTypedArray(), REQUEST_CAMERA_PERMISSIONS)
            return false
        }
        return true
    }
    public fun stopLocationServiceIfRunning() {
        if (isMyServiceRunning(LocationUpdateService::class.java)) {
            Log.d("FragmentMainActivity", "onCreate: isMyServiceRunning = true")
            stopService(Intent(this@FragmentShownActivity, LocationUpdateService::class.java))
        } else {
            Log.d("FragmentMainActivity", "onCreate: isMyServiceRunning = false")
        }
    }

    fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CAMERA_PERMISSIONS -> {
                val perms = HashMap<String, Int>()
                perms[Manifest.permission.CAMERA] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.ACCESS_FINE_LOCATION] = PackageManager.PERMISSION_GRANTED
                for (i in permissions.indices)
                    perms[permissions[i]] = grantResults[i]
                if (perms[Manifest.permission.CAMERA] == PackageManager.PERMISSION_GRANTED) {
                    //&& perms[Manifest.permission.ACCESS_FINE_LOCATION] == PackageManager.PERMISSION_GRANTED
                    if (calledFragment is SchoolRegFragment)
                        (calledFragment as SchoolRegFragment).openCamera()
                    else if (calledFragment is ShopRegFragment)
                        (calledFragment as ShopRegFragment).openCamera()
                    else if (calledFragment is DailyActivityFragment) (calledFragment as DailyActivityFragment).openCamera()
                }
                else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)
                            || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        showDialogOK("Camera Permissions is required for this app",
                                DialogInterface.OnClickListener { dialog, which ->
                                    when (which) {
                                        DialogInterface.BUTTON_POSITIVE -> checkAndRequestPermissions()
                                        DialogInterface.BUTTON_NEGATIVE -> finish()
                                    }
                                })
                    } else {
                        explain("You need to give some mandatory permissions to continue. Do you want to go to app settings?")
                    }//permission is denied (and never ask again is  checked)
                    //shouldShowRequestPermissionRationale will return false
                }
            }
        }

    }

    private fun showDialogOK(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show()
    }

    private fun explain(msg: String) {
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
        dialog.setMessage(msg)
                .setPositiveButton("Yes") { paramDialogInterface, paramInt ->
                    //  permissionsclass.requestPermission(type,code);
                    startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:com.hp.salesforce")))
                }
                .setNegativeButton("Cancel") { paramDialogInterface, paramInt -> finish() }
        dialog.show()
    }

    companion object {
        val REQUEST_CAMERA_PERMISSIONS = 1
    }

    fun openCamera(fragment: androidx.fragment.app.Fragment) {
        calledFragment = fragment
        if (checkAndRequestPermissions()) {
            if (calledFragment is SchoolRegFragment)
                (calledFragment as SchoolRegFragment).showPictureDialog()
            else if (calledFragment is ShopRegFragment)
                (calledFragment as ShopRegFragment).showPictureDialog()
             if (calledFragment is DailyActivityFragment)
                (calledFragment as DailyActivityFragment).showPictureDialog()
        }

    }

}