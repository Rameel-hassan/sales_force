package com.ibrahim.salesforce.fragment


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.ibrahim.salesforce.activity.FragmentShownActivity
import com.ibrahim.salesforce.base.SFApplication.TAG
import com.ibrahim.salesforce.databinding.FragmentTestBinding
import com.ibrahim.salesforce.model.SampleSearchModel
import com.ibrahim.salesforce.network.RequestCode
import com.ibrahim.salesforce.network.RestService
import com.ibrahim.salesforce.response.*
import com.ibrahim.salesforce.services.DBLocationPointsSyncService
import com.ibrahim.salesforce.services.LocationUpdateService
import com.ibrahim.salesforce.utilities.AppKeys
import com.ibrahim.salesforce.utilities.Constants
import com.ibrahim.salesforce.utilities.Utility.BatteryOptimization
import io.paperdb.Paper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat
import ir.mirrajabi.searchdialog.core.SearchResultListener
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class DailyActivityFragment : androidx.fragment.app.Fragment() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationEnabled: Boolean = false;

    lateinit var mServiceIntent: Intent

    //to get location permissions.
    private val LOCATION_REQUEST_CODE = 23
    private var mMap: GoogleMap? = null
    var distance: Double? = 0.0

    //current and destination location objects
    var myLocation: Location? = null
    var destinationLocation: Location? = null
    protected var start: LatLng? = null
    protected var end: LatLng? = null
    protected var last: LatLng? = null
    private lateinit var arrdayinOutDropdown: MutableList<DayinOutDropdown>
    private lateinit var mView: FragmentTestBinding
    private val CAMERA_INTENT = 30
    private val METER_OUT_INTENT = 130
    private val TRANSPORT_INTENT = 131
    private val NIGHT_STAY_INTENT = 132
    private val FOOD_INTENT = 133
    private val OTHER_INTENT = 134


    private val gralleryIntent = 31
    private var bitmap: Bitmap? = null
    private var transportBitmap: Bitmap? = null
    private var foodBitmap: Bitmap? = null
    private var meterOutBitmap: Bitmap? = null
    private var nightstayBitmap: Bitmap? = null
    private var otherBitmap: Bitmap? = null
    private var uploadbtn: Button? = null
    private lateinit var mDialog: ProgressDialog
    private lateinit var disposable: Disposable
    private lateinit var mLoginResponse: GetServerResponse
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var mArrRegions: MutableList<Region>
    private lateinit var mArrCities: MutableList<IdName>
    private var cityPosition: Int = 0
    private lateinit var regionId: String
    private lateinit var mArrAreas: MutableList<IdName>
    private lateinit var endingReasons: MutableList<IdName>
    private lateinit var visitPurposes: MutableList<IdName>
    private val restService by lazy { RestService.create() }
    private var mArrShopType: java.util.ArrayList<String>? = null
    private var sampleSearchModels: java.util.ArrayList<SampleSearchModel>? = null
    private var regionID: Int = 0
    private var cityID: Int = 0
    private var areaID: Int = 0
    private var optionID: Int = 0
    private var meterOutValue: String = "0"
    private var totalMeterKms: String = "0"
    private var totalSystemKms: String = "0"
    private var totalFare: String = "0"
    private var transport: String = "0"
    private var nightStay: String = "0"
    private var food: String = "0"
    private var other: String = ""
    private var visitPurpose1: String = ""
    private var value: String = ""
    private var remarks: String = ""
    private lateinit var imageUri: Uri
    private var soId: Int = 0
    private var REQUEST_CODE: Int = 0
    private var position_: Int = 0
    private var token: String? = null
    var latitudeget: Double? = null
    var longitudeget: Double? = null

    private var isMarked: Boolean = false
    private var isCheckOut: Boolean = false
    private var totalLeaves: Int = 0
    private var achievedLeaves: Int = 0


    private var isResultFromStartAttendence = false

    companion object {
        private const val MY_FINE_LOCATION_REQUEST = 99
        private const val MY_BACKGROUND_LOCATION_REQUEST = 100
        private val IMAGE_DIRECTORY = "/GOHAR ATTENDANCE IMAGES"
        fun newInstance(): DailyActivityFragment {
            return DailyActivityFragment()
        }
    }

    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = FragmentTestBinding.inflate(inflater, container, false)
        Paper.init(activity)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
//        distance  = Paper.book().read<String>("TotalDistance")?.let{
//             it.toDouble()
//         }?:0.0
       // Log.e(TAG, "distance: " + distance)
        init()
       // mView.etTotalSystemKms.setText(distance.toString())

        mView.etMeterOutValue.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No changes needed here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val meterOutValue = mView.etMeterOutValue.text.toString()
                try {
                    if (meterOutValue.isNotEmpty()) {
                        try{
                            val input = meterOutValue.toFloat()
                            if(distance!=null){
                                if (input >= distance!!) {
                                    mView.etTotalMeterKms.setText("${input - distance!!}")
                                    //mView.etTotalSystemKms.text = mView.etTotalMeterKms.text
                                }
                            }else{
                                mView.etTotalMeterKms.setText("0.0 Kms Traveled")
                                // mView.etTotalSystemKms.setText("0.0")
                            }
                        }catch(e: NumberFormatException){
                            mView.etTotalMeterKms.setText("0.0 Kms Traveled")
                            mView.etMeterOutValue.error="Invalid input value"
                        }
                    }else{
                        mView.etTotalMeterKms.setText("0.0 Kms Traveled")
                       // mView.etTotalSystemKms.setText("0.0")

                    }
                } catch (e: NumberFormatException) {
                    Toast.makeText(context, "Invalid input, please enter a valid number", Toast.LENGTH_SHORT).show()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // No changes needed here
            }
        })

        if ((activity as FragmentShownActivity?)!!.isMyServiceRunning(LocationUpdateService::class.java)) {
            locationEnabled = true
            Log.d("FragmentMainActivity", "onCreate: isMyServiceRunning = true")
        } else {
            locationEnabled = false;
            Log.d("FragmentMainActivity", "onCreate: isMyServiceRunning = false")
        }

        return mView.root
    }

    override fun onStart() {
        super.onStart()
        setupPermissions()
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                val builder = AlertDialog.Builder(requireActivity())
                builder.setMessage("Permission to access storage is required")
                    .setTitle("Permission required")

                builder.setPositiveButton(
                    "OK"
                ) { dialog, id ->
                    makeRequest()
                }

                val dialog = builder.create()
                dialog.show()
            } else {
                makeRequest()

            }
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i(TAG, "Permission has been denied by user")
                } else {
                    Log.i(TAG, "Permission has been granted by user")
                }
            }

            MY_FINE_LOCATION_REQUEST -> {

                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        requestBackgroundLocationPermission()
                    }

                } else {
                    Toast.makeText(
                        requireContext(),
                        "ACCESS_FINE_LOCATION permission denied",
                        Toast.LENGTH_LONG
                    ).show()
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(
                            requireActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    ) {
                        startActivity(
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", requireContext().packageName, null)
                            ),
                        )
                    }
                }
                return
            }

            MY_BACKGROUND_LOCATION_REQUEST -> {

                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        Toast.makeText(
                            requireContext(),
                            "Background location Permission Granted",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Background location permission denied",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
                return
            }

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BatteryOptimization(context)

    }


    private fun init() {
        mLoginResponse = Paper.book().read<GetServerResponse>(AppKeys.KEY_LOGIN_RESPONSE)
        soId = mLoginResponse.Data.SOID
        token = mLoginResponse.Data.Token
        isCheckOut = mLoginResponse.Data.IsCheckOut
        isMarked = mLoginResponse.Data.IsMarked
        totalLeaves = mLoginResponse.Data.TotalLeave
        achievedLeaves = mLoginResponse.Data.AchievedLeave
        mDialog = ProgressDialog(activity)
        mDialog.setCancelable(false)
        arrdayinOutDropdown = ArrayList();
        sampleSearchModels = ArrayList()
        visitPurposes = ArrayList();
        mView.btnupload.setOnClickListener { (activity as FragmentShownActivity).openCamera(this) }

        mView.ivFood.setOnClickListener {
            openCameraWithPermissionCheck(FOOD_INTENT)
        }
        mView.ivMeterOut.setOnClickListener {
            openCameraWithPermissionCheck(METER_OUT_INTENT)
        }
        mView.ivOther.setOnClickListener {
            openCameraWithPermissionCheck(OTHER_INTENT)
        }

        mView.ivTransport.setOnClickListener {
            openCameraWithPermissionCheck(TRANSPORT_INTENT)
        }

        mView.ivNigthStay.setOnClickListener {
            openCameraWithPermissionCheck(NIGHT_STAY_INTENT)
        }

        setdailyInOutDropDown();
//        setVisitPurposeDropDown()
        mView.spndayInOut.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                if (position == 1) {   //mark attendence

                    if (ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                        == PackageManager.PERMISSION_GRANTED
                    ) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                            if (ActivityCompat.checkSelfPermission(
                                    requireContext(),
                                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                                )
                                != PackageManager.PERMISSION_GRANTED
                            ) {

                                AlertDialog.Builder(requireContext()).apply {
                                    setTitle("Background permission")
                                    setMessage(com.ibrahim.salesforce.R.string.background_location_permission_message)
                                    setPositiveButton("Start service anyway",
                                        DialogInterface.OnClickListener { dialog, id ->
                                            fusedLocationClient.lastLocation
                                                .addOnSuccessListener { location: Location? ->
                                                    // Got last known location. In some rare situations this can be null.
                                                    latitudeget = location!!.latitude
                                                    longitudeget = location!!.longitude
                                                    val context = HashMap<String, Double>()
                                                    context.put("startLatitude", latitudeget!!)
                                                    context.put("startLongitude", longitudeget!!)
                                                    val current = LocalDateTime.now()
                                                    val formatter =
                                                        DateTimeFormatter.ofPattern("dd-MM-yyyy")
                                                    val formatted = current.format(formatter)
                                                    val userName = mLoginResponse.Data.Name

                                                }
                                        })
                                    setNegativeButton("Grant background Permission",
                                        DialogInterface.OnClickListener { dialog, id ->
                                            requestBackgroundLocationPermission()
                                        })
                                }.create().show()

                            }
                        }
                    } else if (ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                requireActivity(),
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                        ) {
                            AlertDialog.Builder(requireContext())
                                .setTitle("ACCESS_FINE_LOCATION")
                                .setMessage("Location permission required")
                                .setPositiveButton(
                                    "OK"
                                ) { _, _ ->
                                    requestFineLocationPermission()
                                }
                                .create()
                                .show()
                        } else {
                            requestFineLocationPermission()
                        }
                    }

                    mView.llOpStartingTime.visibility = View.VISIBLE
                    mView.llOpLeavingTime.visibility = View.GONE
                    mView.llLeaveOptions.visibility = View.GONE
                    mView.uploadimage.visibility = View.VISIBLE
                    mView.btnupload.visibility = View.VISIBLE
                    setRegionSpinner()
                }
                if (position == 2) { //close attendence
                    mView.llOpStartingTime.visibility = View.GONE
                    mView.llOpLeavingTime.visibility = View.VISIBLE
                    mView.llLeaveOptions.visibility = View.GONE
                    mView.uploadimage.visibility = View.GONE
                    mView.btnupload.visibility = View.GONE

                    if (isMarked && !isCheckOut)
                        getEndingKmAndFareService(soId);
                    else
                        Toast.makeText(context, "no mark attendence found", Toast.LENGTH_SHORT)
                            .show();
                }
                if (position == 3) { //leave
                    mView.llOpStartingTime.visibility = View.GONE
                    mView.llOpLeavingTime.visibility = View.GONE
                    mView.llLeaveOptions.visibility = View.VISIBLE
                    fillLeaveValues();


                }
                if (position != 0)
                    position_ = arrdayinOutDropdown[position].ID
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        mView.btnsbmt.setOnClickListener {

            if (mView.spndayInOut.selectedItemPosition == 0)
                Toast.makeText(activity, "Please Select DailyActivity", Toast.LENGTH_LONG).show()
            /*else if (mView.llOpStartingTime.visibility == View.VISIBLE && mView.spnVisitPurpose.selectedItemPosition <= 0) {
                Toast.makeText(activity, "Select Some Visit Purpose", Toast.LENGTH_LONG).show()
            }*/ else if (mView.llLeaveOptions.visibility != View.GONE && mView.etReason.text.toString()
                    .isNullOrEmpty()
            ) {
                Toast.makeText(activity, "Enter some valid Reason", Toast.LENGTH_LONG).show()
                mView.etReason.requestFocus()
            } else {

                totalSystemKms = mView.etTotalSystemKms.text.toString();
                totalFare = mView.etTotalFare.text.toString();
                totalMeterKms = mView.etTotalMeterKms.text.toString();
                meterOutValue = mView.etMeterOutValue.text.toString();
                transport = mView.etTransport.text.toString();
                other = mView.etOther.text.toString();
                food = mView.etFood.text.toString();
                nightStay = mView.etNigthStay.text.toString();


                var f=0;
                var mo=0;
                var t=0;


                if (mView.spndayInOut.selectedItemPosition == 2) {
                     if (meterOutBitmap == null) {
//                        Toast.makeText(context, "Select Meter Image First.", Toast.LENGTH_SHORT).show();
//                        return@setOnClickListener
                    }
                    if(mView.etMeterOutValue.text.isNullOrEmpty()){
                         Toast.makeText(context, "Enter Meter value First.", Toast.LENGTH_SHORT)
                             .show();
                         return@setOnClickListener
                     }
                }
                if (meterOutValue.isEmpty()) {
                    meterOutValue = "0";
                }

                try{
                    mo=Integer.parseInt(meterOutValue);
                }catch(e:NumberFormatException){
                    mView.etMeterOutValue.error="Invalid Input value"
                    return@setOnClickListener
                }


                if (food.isEmpty()) {
                    food = "0";
                }

                try{
                    f=Integer.parseInt(food);
                }catch(e:NumberFormatException){
                    mView.etFood.error="Invalid Input value"
                    return@setOnClickListener
                }

                if (transport.isEmpty()) {
                    transport = "0";
                }

                try{
                    t=Integer.parseInt(transport);
                }catch(e:NumberFormatException){
                    mView.etTransport.error="Invalid Input value"
                    return@setOnClickListener
                }


//                visitPurpose1 = visitPurposes.get(mView.spnVisitPurpose.selectedItemPosition).Name
                visitPurpose1 = "First Visit"
                remarks = mView.etReason.text.toString()
                Log.e("Image", getStringImage(bitmap));
                if (bitmap != null) {
                    SaveImages(bitmap!!)
                }
                Log.e("BITMAP", bitmap.toString())
                var meterInValue: Int = 0
                if (mView.spndayInOut.selectedItemPosition == 1) {
                    isResultFromStartAttendence = true;
                    if (mView.etMeterValue.text.isNullOrEmpty()) {
                        Toast.makeText(context, "Please Enter Meter Value", Toast.LENGTH_SHORT)
                            .show();
                        return@setOnClickListener
                    }
                    if(bitmap == null){
//                        Toast.makeText(context, "Please upload Meter Image", Toast.LENGTH_SHORT).show();
//                        return@setOnClickListener
                    }
                    try{
                        meterInValue = Integer.parseInt(mView.etMeterValue.text.toString())
                    }catch (ex:NumberFormatException){
                        mView.etMeterValue.error="Invalid Input value"
                        if(mView.etMeterValue.text.toString().isNotEmpty()){
                            mView.etMeterValue.text!!.clear()
                        }
                        return@setOnClickListener
                    }
                }





                callLocationService(
                    soId,
                    token!!,
                    position_,
                    if (mView.spndayInOut.selectedItemPosition == 1)
                        getStringImage(bitmap)
                    else
                        getStringImage(meterOutBitmap),
                    regionID,
                    cityID,
                    areaID,
                    optionID,
                    remarks,
                   // totalMeterKms,
                    totalSystemKms,
                    totalFare,
                    f,
                    nightStay,
                    t,
                    other, visitPurpose1, meterInValue, mo,
                    getStringImage(foodBitmap),
                    getStringImage(transportBitmap),
                    getStringImage(nightstayBitmap),
                    getStringImage(otherBitmap)
                )
                if (position_ == 2) {
//                    startActivity(Intent(requireContext(),RouteActivity::class.java))
                }
            }
        }
    }

    private fun fillLeaveValues() {
        mView.tvTotalLeaves.setText("" + totalLeaves)
        mView.tvAcqLeaves.setText("" + achievedLeaves)

    }

    override fun onResume() {
        super.onResume()

        val serviceIntent = Intent(activity, DBLocationPointsSyncService::class.java)
        activity?.let { ContextCompat.startForegroundService(it, serviceIntent) }
    }

    private fun RequestPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        val permissionCamera = ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val listPermissionsNeeded: MutableList<String> = java.util.ArrayList()
        if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (!listPermissionsNeeded.isEmpty()) {
            requestPermissions(
                listPermissionsNeeded.toTypedArray(),
                Constants.LOCATION_REQUEST_PERMISSION
            )
            return false
        }
        return true
    }

    private fun startService() {
        if (RequestPermission()) {
            Log.d(TAG, "start service")
            locationEnabled = true
            ContextCompat.startForegroundService(
                this.requireActivity(),
                Intent(this.requireContext(), LocationUpdateService::class.java)
            )
        }
    }

    private fun stopService() {

        if ((activity as FragmentShownActivity?)!!.isMyServiceRunning(LocationUpdateService::class.java)) {
            Log.d("FragmentMainActivity", "onCreate: isMyServiceRunning = true")
            (activity as FragmentShownActivity?)!!.stopLocationServiceIfRunning()
            locationEnabled = false;
        }

        //todo confirm if head also make attendence

    }

    private fun SaveImages(finalBitmap: Bitmap) {

//        requestImagePermissione()

        val root = Environment.getExternalStorageDirectory().toString()
        val myDir = File("$root/Gohar Attendance")
        if (!myDir.exists()) {
            myDir.mkdirs()
        }
        val generator = Random()
        var n = 10000
        n = generator.nextInt(n)
        val fname = "Image-$n.jpg"
        val file = File(myDir, fname)
        if (file.exists())
            file.delete()
        try {
            val out = FileOutputStream(file)
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 90, out)
            out.flush()
            out.close()
            MediaScannerConnection.scanFile(
                activity,
                arrayOf(file.getPath()),
                arrayOf("image/*"), null
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun requestImagePermissione() {

    }

    //SAVE IMAGES ALTERNATIVE METHODS:

    /*  private fun saveImageToExternalStorage(finalBitmap: Bitmap) {
          val root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
          val myDir = File("$root/GOHAR")
          myDir.mkdirs()
          val generator = Random()
          var n = 10000
          n = generator.nextInt(n)
          val fname = "Image-$n.jpg"
          val file = File(myDir, fname)
          if (file.exists())
              file.delete()
          try {
              val out = FileOutputStream(file)
              finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
              Toast.makeText(activity, "Piture Saved!", Toast.LENGTH_SHORT).show()
              out.flush()
              out.close()
          } catch (e: Exception) {
              e.printStackTrace()
          }


          // Tell the media scanner about the new file so that it is
          // immediately available to the user.
          MediaScannerConnection.scanFile(activity, arrayOf(file.toString()), null,
                  MediaScannerConnection.OnScanCompletedListener { path, uri ->
                      Log.i("ExternalStorage", "Scanned $path:")
                      Log.i("ExternalStorage", "-> uri=$uri")
                  })


      }

      private fun saveImageToExternalStorage1(bitmap: Bitmap) {
          val root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
          val myDir = File("$root/"+getString(com.ibrahim.salesforce.R.string.app_name))
          if (!myDir.exists()){
              myDir.mkdirs()
          }
          val generator = Random()
          var n = 10000
          n = generator.nextInt(n)
          val fname = "Image-$n.jpg"
          val file = File(myDir, fname)
          if (file.exists())
              file.delete()
          try {
              val out = FileOutputStream(file)
              bitmap.compress(Bitmap.CompressFormat.JPEG, 1000, out)
              out.flush()
              out.close()

          } catch (e: Exception) {
              e.printStackTrace()

          }}




      fun saveImage(myBitmap: Bitmap): String {
          val bytes = ByteArrayOutputStream()
          myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
          val wallpaperDirectory = File(
                  (Environment.getExternalStorageDirectory()).toString() + IMAGE_DIRECTORY)
          // have the object build the directory structure, if needed.
          Log.d("fee", wallpaperDirectory.toString())
          if (!wallpaperDirectory.exists()) {
              wallpaperDirectory.mkdirs()
          }

          try {
              Log.d("heel", wallpaperDirectory.toString())
              val f = File(wallpaperDirectory, ((Calendar.getInstance()
                      .getTimeInMillis()).toString() + ".jpg"))
              f.createNewFile()
              val fo = FileOutputStream(f)
              fo.write(bytes.toByteArray())
              MediaScannerConnection.scanFile(activity,
                      arrayOf(f.getPath()),
                      arrayOf("image/jpeg"), null)
              fo.close()
              Log.d("TAG", "File Saved::--->" + f.getAbsolutePath())

              return f.getAbsolutePath()
          } catch (e1: IOException) {
              e1.printStackTrace()
          }
          return ""

      }*/


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.LOCATION_REQUEST_PERMISSION) {
            if (resultCode != PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Location Permission has been denied by user")
            } else {
                Log.i(TAG, "Location Permission has been granted by user")
            }
        } else
            if (requestCode == CAMERA_INTENT) {
                if (data != null && data.extras != null && data.extras!!.get("data") != null) {

                    bitmap =
                        Bitmap.createScaledBitmap(
                            data.extras!!.get("data") as Bitmap,
                            512,
                            512,
                            true
                        )
                    mView.uploadimage.setImageBitmap(bitmap);
                    mView.uploadimage.visibility = View.VISIBLE
                }
            } else if (requestCode == TRANSPORT_INTENT) {
//                data?.let { data_ ->
//                    getBitmap(data_)?.let { bitmap ->
//                        transportBitmap = bitmap
//                        mView.ivTransport.setImageBitmap(transportBitmap);
//                    }
//                }

                if (data != null && data.extras != null && data.extras!!.get("data") != null) {

                    transportBitmap =
                        Bitmap.createScaledBitmap(
                            data.extras!!.get("data") as Bitmap,
                            512,
                            512,
                            true
                        )
                    mView.ivTransport.setImageBitmap(transportBitmap);

                }

            } else if (requestCode == FOOD_INTENT) {

                // for gallery code
//                data?.let { data_ ->
//                    getBitmap(data_)?.let { bitmap ->
//                        foodBitmap = bitmap
//                        mView.ivFood.setImageBitmap(foodBitmap);
//                    }
//                }


                // for camera code
                if (data != null && data.extras != null && data.extras!!.get("data") != null) {

                    foodBitmap =
                        Bitmap.createScaledBitmap(
                            data.extras!!.get("data") as Bitmap,
                            512,
                            512,
                            true
                        )
                    mView.ivFood.setImageBitmap(foodBitmap);
                }

            } else if (requestCode == NIGHT_STAY_INTENT) {
//                data?.let { data_ ->
//                    getBitmap(data_)?.let { bitmap ->
//                        nightstayBitmap = bitmap
//                        mView.ivNigthStay.setImageBitmap(nightstayBitmap);
//                    }
//                }

                if (data != null && data.extras != null && data.extras!!.get("data") != null) {

                    nightstayBitmap =
                        Bitmap.createScaledBitmap(
                            data.extras!!.get("data") as Bitmap,
                            512,
                            512,
                            true
                        )
                    mView.ivNigthStay.setImageBitmap(nightstayBitmap);

                }

            } else if (requestCode == OTHER_INTENT) {
//                data?.let { data_ ->
//                    getBitmap(data_)?.let { bitmap ->
//                        otherBitmap = bitmap
//                        mView.ivOther.setImageBitmap(otherBitmap);
//                    }
//                }

                if (data != null && data.extras != null && data.extras!!.get("data") != null) {

                    otherBitmap =
                        Bitmap.createScaledBitmap(
                            data.extras!!.get("data") as Bitmap,
                            512,
                            512,
                            true
                        )
                    mView.ivOther.setImageBitmap(otherBitmap);

                }

            } else if (requestCode == METER_OUT_INTENT) {
                if ((data != null && data.extras != null && data.extras!!.get("data") != null)) {
                    meterOutBitmap = Bitmap.createScaledBitmap(
                        data.extras!!.get("data") as Bitmap,
                        512,
                        512,
                        true
                    )
                    mView.ivMeterOut.setImageBitmap(meterOutBitmap);
                    bitmap = meterOutBitmap
                }
            } else if (requestCode == gralleryIntent) {
                if (data != null) {
                    val contentURI = data!!.data
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(
                            requireActivity().applicationContext.contentResolver,
                            contentURI
                        )
                        Toast.makeText(activity, "Image Saved!", Toast.LENGTH_SHORT).show()
                        mView.uploadimage.setImageBitmap(bitmap)
                        mView.uploadimage.visibility = View.VISIBLE

                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(activity, "Failed!", Toast.LENGTH_SHORT).show()
                    }

                }
            } else {
                Toast.makeText(activity, "no data is found", Toast.LENGTH_LONG).show()
            }
    }

    fun getBitmap(data: Intent): Bitmap? {
        val contentURI = data!!.data
        try {
            return MediaStore.Images.Media.getBitmap(
                requireActivity().applicationContext.contentResolver,
                contentURI
            )

        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(activity, "Failed!", Toast.LENGTH_SHORT).show()
        }
        return null
    }

    fun openGrallery() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(galleryIntent, gralleryIntent)
    }

    private fun openGrallery(code: Int) {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(galleryIntent, code)
    }

    fun openCamera() {
        val i = Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(i, CAMERA_INTENT)
    }

    fun openCameraWithPermissionCheck(code:Int) {
        // Check if the CAMERA permission is granted
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // Permission is already granted, so open the camera
            openCamera(requireActivity(), code)
        } else {
            // Permission is not granted, request it from the user
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), 927)
        }
    }



    fun openCamera(activity: Activity, code: Int) {
        val i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        activity.startActivityForResult(i, code)
    }


    fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(activity)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems =
            arrayOf(/*"Select photo from gallery", */"Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems) { dialog, which ->
            when (which) {
//                0 -> openGrallery()
                0 -> openCamera()
            }
        }
        pictureDialog.show()
    }

    private fun setdailyInOutDropDown() {
        arrdayinOutDropdown = mLoginResponse.Data.DayinOutDropdown
        if (arrdayinOutDropdown.get(0).ID != 0) {
            val hintElement = DayinOutDropdown(0, "Select Daily Activity")
            arrdayinOutDropdown.add(0, hintElement)
        }
        val adapter = ArrayAdapter<DayinOutDropdown>(
            requireActivity(),
            android.R.layout.simple_spinner_dropdown_item,
            arrdayinOutDropdown
        )
        mView.spndayInOut.adapter = adapter
        mView.spndayInOut.visibility = View.VISIBLE

    }

    private fun setVisitPurposeDropDown() {
        visitPurposes = mLoginResponse.Data.VisitPurposes!!.toMutableList()
        val hintElement = IdName(0, "Select Visit Purpose")
        visitPurposes.add(0, hintElement)
        val adapter =
            ArrayAdapter<IdName>(
                requireActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                visitPurposes
            )
//        mView.spnVisitPurpose.adapter = adapter
//        mView.spnVisitPurpose.visibility = View.VISIBLE

    }

    private fun setRegionSpinner() {
        mArrRegions = mLoginResponse.Data.Region.toMutableList()
        val emptyRegion = Region(0, "Select Zone")
        mArrRegions.add(0, emptyRegion)
        val adapter = ArrayAdapter<Region>(
            requireActivity(),
            android.R.layout.simple_spinner_dropdown_item,
            mArrRegions
        )
        mView.spnRegion.adapter = adapter
        mView.spnRegion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                if (position != 0) {
                    if (!mLoginResponse.Data.IsRegionalHead) {
                        callGetCityService(mArrRegions[position].ID.toString())
                    } else {
                        callGetCityServiceForRegionalHead(mArrRegions[position].ID.toString())
                    }
                    regionID = mArrRegions[position].ID
                }
            }
        }
    }

    private fun setCitySpinner(citiesResponse: GetServerResponse) {
        mArrCities = ArrayList();

        sampleSearchModels?.removeAll(sampleSearchModels!!.asIterable())
        mArrCities = citiesResponse.Cities


        if (::mArrCities.isInitialized) {
            for (i in mArrCities.indices) {
                val city = mArrCities.get(i)
                val name = city.Name
                val id = city.ID
                sampleSearchModels!!.add(SampleSearchModel(name, id))

            }
            val adapter = ArrayAdapter<IdName>(
                requireActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                mArrCities
            )
            mView.spnCity.adapter = adapter
            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    if (!mLoginResponse.Data.IsRegionalHead) {
                        callGetAreaService(mArrCities[cityPosition].ID.toString())
                    } else {
                        callGetAreaServiceForRegionalHead(mArrCities[cityPosition].ID.toString())
                    }
                    cityID = mArrCities[cityPosition].ID
                } catch (ex: Exception) {
                    Toast.makeText(
                        activity,
                        "Please Wait for Cities to populate",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }, 600)

            mDialog.show()
            mView.spnCity.setOnTouchListener(View.OnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    mArrCities.clear()
                    SimpleSearchDialogCompat(activity, "Search...",
                        "What are you looking for...?", null, sampleSearchModels,
                        SearchResultListener<SampleSearchModel> { dialog, item, position ->
                            mArrCities.add(IdName(item.id, item.title))
                            val cityAdapter = ArrayAdapter<IdName>(
                                requireActivity(),
                                android.R.layout.simple_spinner_dropdown_item,
                                mArrCities
                            )
                            mView.spnCity.adapter = cityAdapter
                            mView.spnCity.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onNothingSelected(parent: AdapterView<*>?) {}

                                    override fun onItemSelected(
                                        parent: AdapterView<*>?,
                                        view: View?,
                                        position: Int,
                                        id: Long
                                    ) {
                                        if (position != 0 || position == 0) {

                                            cityPosition = position;
                                            if (!mLoginResponse.Data.IsRegionalHead) {
                                                callGetAreaService(mArrCities[position].ID.toString())
                                            } else {
                                                callGetAreaServiceForRegionalHead(mArrCities[position].ID.toString())
                                            }
                                            cityID = mArrCities[position].ID
                                        }
                                    }
                                }
                            dialog.dismiss()
                        }).show()
                }
                true
            })
            adapter.notifyDataSetChanged()
        } else {
            Toast.makeText(activity, "No Cities Associated", Toast.LENGTH_LONG).show()
        }
    }

    private fun setAreaSpinner(response: GetServerResponse) {
        mArrAreas = response.Areas
        val hintElement = IdName(0, "Select Area")
        mArrAreas.add(0, hintElement)
        val adapter =
            ArrayAdapter<IdName>(
                requireActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                mArrAreas
            )
        mView.spnArea.adapter = adapter
        mView.spnArea.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    areaID = mArrAreas[position].ID
                }
            }
        }

    }

    private fun callGetCityService(s: String) {
        regionId = s
        mDialog.show()
        mDialog.setMessage("Fetching cities please wait...")
        disposable = restService.getCities(regionId, mLoginResponse.Data.SOID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> showResult(result, RequestCode.GET_CITIES_REQUEST_CODE) },
                { error -> showError(error.message) }
            )
    }

    private fun callGetCityServiceForRegionalHead(s: String) {
        regionId = s
        mDialog.show()
        mDialog.setMessage("Fetching cities please wait...")
        disposable = restService.getCitiesRelatedToRegionalHeaf(
            regionId,
            mLoginResponse.Data.SOID,
            mLoginResponse.Data.IsRegionalHead
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> showResult(result, RequestCode.GET_CITIES_REQUEST_CODE) },
                { error -> showError(error.message) }
            )
    }

    private fun callGetAreaService(cityId: String) {
        mDialog.show()
        mDialog.setMessage("Fetching areas please wait...")
        disposable = restService.getAreas(cityId, mLoginResponse.Data.SOID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> showResult(result, RequestCode.GET_AREA_REQUEST_CODE) },
                { error -> showError(error.message) }
            )
    }

    private fun callGetAreaServiceForRegionalHead(cityId: String) {
        mDialog.show()
        mDialog.setMessage("Fetching areas please wait...")
        disposable = restService.getAreasRelatedToRegionalHeas(
            cityId,
            mLoginResponse.Data.SOID,
            mLoginResponse.Data.IsRegionalHead
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> showResult(result, RequestCode.GET_AREA_REQUEST_CODE) },
                { error -> showError(error.message) }
            )
    }

    private fun callLocationService(
        SOID: Int,
        Token: String,
        DropdownId: Int,
        picture: String,
        regionID: Int,
        cityId: Int,
        AreaID: Int,
        EndingTimeReasonID: Int,
        Remarks: String,
        totalMeterKms: String,
        //totalSystemKms: String,
        totalFare: String,
        food: Int,
        nightStay: String,
        transport: Int,
        other: String,
        visitPurpose1: String,
        meterIntValue: Int,
        meterOutValue: Int,
        foodImage: String,
        transportImage: String,
        nightImage: String,
        otherImage: String
    ) {
        mDialog.show()
        mDialog.setMessage("please wait...")

        disposable = restService.dayIndayOut(
            SOID,
            Token,
            DropdownId,
            picture,
            regionID,
            cityId,
            AreaID,
            EndingTimeReasonID,
            Remarks,
            //totalSystemKms,
            totalMeterKms,
            nightStay,
            totalFare,
            other,
            visitPurpose1,
            meterIntValue,
            meterOutValue,
            transport,
            food,
            foodImage,
            transportImage,
            nightImage,
            otherImage
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> showResult(result, RequestCode.API_DAY_IN_DAY_OUT) },
                { error -> showError(error.message) }
            )

    }

    private fun getEndingKmAndFareService(
        SOID: Int
    ) {
        mDialog.show()
        mDialog.setMessage("please wait...")
        Log.e("noob", "$SOID")
        disposable = restService.getUserEndingKmAndFare(
            SOID
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> showResult(result, RequestCode.API_GET_USER_ENDING_KM_FARE) },
                { error -> showError(error.message) }
            )

    }

    private fun showResult(response: GetServerResponse, requestCode: RequestCode) {
        if (requestCode == RequestCode.GET_CITIES_REQUEST_CODE) {
            setCitySpinner(response)
        } else if (requestCode == RequestCode.GET_AREA_REQUEST_CODE) {
            setAreaSpinner(response)
        } else if (requestCode == RequestCode.API_CALL_LOCATION_SERVICE) {
            Toast.makeText(activity, response.Message, Toast.LENGTH_SHORT).show()
        } else if (requestCode == RequestCode.API_DAY_IN_DAY_OUT) {
            if (isResultFromStartAttendence) {
                startService()
                isResultFromStartAttendence = false
                mLoginResponse.Data.IsMarked = true

            } else {
                stopService()
                mLoginResponse.Data.IsCheckOut = true
            }

            Paper.book().delete(AppKeys.KEY_LOGIN_RESPONSE)
            Paper.book().write(AppKeys.KEY_LOGIN_RESPONSE, mLoginResponse)

            Toast.makeText(activity, response.Message, Toast.LENGTH_SHORT).show()

        } else if (requestCode == RequestCode.API_GET_USER_ENDING_KM_FARE) {
            setEndingKmAndFare(response)
        } else {
            mDialog.dismiss()
            Toast.makeText(activity, response.Message, Toast.LENGTH_LONG).show()
            Log.e("resp", "response: $response")
            //todo later
            requireActivity().onBackPressed()
        }
        mDialog.dismiss()
    }

    private fun setEndingKmAndFare(response: GetServerResponse) {
        var routeInfo: RouteDetails? = response.locPoints

        distance=routeInfo?.DayInMeter
        mView.etTotalSystemKms.setText("" + routeInfo?.totalKM)
        mView.etTotalFare.setText("" + routeInfo?.totalFare)

    }

    private fun showError(message: String?) {
        mDialog.dismiss()
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
        Log.e("imageerr", "showError: " + message)
        requireActivity().onBackPressed()
    }

    fun getStringImage(bmp: Bitmap?): String {

        if (bmp == null) {
            return ""
        }
        val baos = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 40, baos)
        val imageBytes = baos.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss()
        }
    }

    private fun requestBackgroundLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
            MY_BACKGROUND_LOCATION_REQUEST
        )
    }

    private fun requestFineLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            MY_FINE_LOCATION_REQUEST
        )
    }
}
