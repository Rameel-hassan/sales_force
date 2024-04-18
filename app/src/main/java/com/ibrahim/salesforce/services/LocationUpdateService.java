package com.ibrahim.salesforce.services;

import static com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.ibrahim.salesforce.R;
import com.ibrahim.salesforce.activity.DashboardActivity;
import com.ibrahim.salesforce.database.DatabaseManager;
import com.ibrahim.salesforce.network.ApiService;
import com.ibrahim.salesforce.network.LocationDetails;
import com.ibrahim.salesforce.network.RequestCode;
import com.ibrahim.salesforce.network.RestCallbackObject;
import com.ibrahim.salesforce.network.RestClient;
import com.ibrahim.salesforce.network.ServerConnectListenerObject;
import com.ibrahim.salesforce.response.GetLiveLocationResponse;
import com.ibrahim.salesforce.response.GetServerResponse;
import com.ibrahim.salesforce.utilities.AppKeys;
import com.ibrahim.salesforce.utilities.Constants;
import com.ibrahim.salesforce.utilities.Utility;

import java.util.Calendar;

import io.paperdb.Paper;
import retrofit2.Call;


/**
 * Created by Rameel on 6/17/2022.
 * muhamadrameelhassan@gmail.com
 */

public class LocationUpdateService extends Service implements ServerConnectListenerObject
{
    private static final String LOG = "LocationUpdateService";


    private static final long INTERVAL = 5 * 1000;
    private static final long FASTEST_INTERVAL = 1 * 1000;
    private static final long SMALLEST_DISP = 50;

    private LocationRequest mLocationRequest;

    private ApiService mService;

    private PowerManager.WakeLock cpuWakeLock;
    private WifiManager.WifiLock wifiWackeLock;
    FusedLocationProviderClient fusedLocationClient;
    private long currentMillis;

    private GetServerResponse mServerResponse;
    public static int userId = -1;
    public static double lastKnownLat=0.0;
    public static double lastKnownLong=0.0;

    LocationDetails locationDetails;

    LocationCallback locationCallback =new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            if (locationResult == null){
                return;
            }
            for (Location location:locationResult.getLocations()){
                onLocationChanged(location);
                Log.d(LOG,location.toString());
            }
        }
    };

    private void parseStoredData() {
        Paper.init(this);
        mServerResponse = Paper.book().read(AppKeys.KEY_LOGIN_RESPONSE);
        if (mServerResponse != null)
            if (Paper.book().contains(AppKeys.KEY_KEEP_LOGIN)) {
                userId = mServerResponse.getData().getSOID();
            }
    }
    private void initLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setSmallestDisplacement(SMALLEST_DISP);
        mLocationRequest.setPriority(PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setWaitForAccurateLocation(true);


    }

    private void sendLocationToServer() {



        Call<GetLiveLocationResponse> userObject = mService.saveLocation(locationDetails);
        RestCallbackObject callbackObject = new RestCallbackObject(LocationUpdateService.this, this, RequestCode.POST_SAVE_LOCATION).showProgress(false, 0).dontHideProgress(false);
        userObject.enqueue(callbackObject);
    }
    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();

        currentMillis = System.currentTimeMillis();

        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        cpuWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOG+":CPU");
        wifiWackeLock =((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL,LOG);

        mService = RestClient.getInstance(LocationUpdateService.this);

        parseStoredData();
        initLocationRequest();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentMillis);

//        int mYear = calendar.get(Calendar.YEAR);
//        int mMonth = calendar.get(Calendar.MONTH) + 1;
//        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        locationDetails = new LocationDetails();
        locationDetails.setUserID(userId);
        locationDetails.setAdminCompanyID(0);
//        locationDetails.setCreatedDate(""+mYear+"-"+mMonth+"-"+mDay);
        locationDetails.setCreatedDate(currentMillis/1000);


        Log.d(LOG,"onCreate");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        checkSettingAndStartLocationUpdate();

    }

    private void checkSettingAndStartLocationUpdate() {
        LocationSettingsRequest request = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest).build();
        SettingsClient client = LocationServices.getSettingsClient(getApplicationContext());
        Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(request);
        locationSettingsResponseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                startLocationUpdate();
            }
        });
        locationSettingsResponseTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException){
                    ResolvableApiException apiException = (ResolvableApiException) e ;
                    try {
//                        apiException.startResolutionForResult(10001);
                    }
                    catch (Exception ex){
                        ex.printStackTrace();
                    }

                }
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG,"onStartCommand");


        startForeGround();

//        LoginResponse loginInfo = AppUtils.getLoginInfo(this);



        return START_STICKY;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdate();

        Log.d(LOG,"onDestroy");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onLocationChanged(Location location) {
        if(location == null)
            return;

        try {

            lastKnownLat = location.getLatitude();
            lastKnownLong = location.getLongitude();

            Log.d(LOG, "loc " + location.getLatitude() + " , " + location.getLongitude());

            Toast.makeText(getApplicationContext(),"lat : "+location.getLatitude()+" lng : "+location.getLongitude(),Toast.LENGTH_SHORT).show();



            cpuWakeLock.acquire();
            wifiWackeLock.acquire();

            locationDetails.setLat(lastKnownLat);
            locationDetails.setLng(lastKnownLong);
            locationDetails.setCreatedDate(Calendar.getInstance().getTimeInMillis()/1000);

            if (location.getAccuracy()>100) {
                return;
            }
            if (Utility.isNetworkAvailable(getApplicationContext())) {
                //todo put location sharing code here
                Log.d("LocationUpdateService", "onLocationChanged: sending live location");
                sendLocationToServer();
            }else
            {
                Log.d("LocationUpdateService", "onLocationChanged: saving location to db");
                addPointsToDb(locationDetails);
            }
            cpuWakeLock.release();
            wifiWackeLock.release();

        }catch (Exception ex){ex.printStackTrace();}
    }


    private boolean checkLocationPermission()
    {
        int permissionLocation = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        return (permissionLocation == PackageManager.PERMISSION_GRANTED);
    }

    private void startLocationUpdate() {

        if (!checkLocationPermission()) {
            stopSelf();
            return;
        }

        fusedLocationClient.requestLocationUpdates(mLocationRequest,locationCallback, Looper.getMainLooper());

    }

    private void stopLocationUpdate() {
        try {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }catch (Exception e){}

    }


    private void startForeGround()
    {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, Constants.Notification_Location_Channel_ID);

        mBuilder.setSmallIcon(R.drawable.ic_general_schools);
        mBuilder.setContentText("Location Sharing");
        mBuilder.setColor( ContextCompat.getColor(this, R.color.colorPrimary));

        mBuilder.setPriority(NotificationCompat.PRIORITY_LOW);


        Intent pintent = new Intent(this, DashboardActivity.class);

        PendingIntent pendingIntent;
        //todo uncomment this to adding support for android 12
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(this, 1, pintent, PendingIntent.FLAG_MUTABLE|PendingIntent.FLAG_UPDATE_CURRENT);
        }
        else
        {
        pendingIntent = PendingIntent.getActivity(this, 1, pintent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        mBuilder.setContentIntent(pendingIntent);
        Notification notification = mBuilder.build();

        startForeground(1112,notification);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = Constants.Notification_Location_Channel_Name;
            String description = Constants.Notification_Location_Channel_Desc;
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(Constants.Notification_Location_Channel_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onFailure(String error, RequestCode requestCode) {

        Log.d("LocationUpdateService", "onFailure: location not uploaded");
    }

    private void addPointsToDb(final LocationDetails locationDetails)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DatabaseManager.getInstance(getApplicationContext()).addUserTrackerPoints(locationDetails);
            }
        }).start();
    }
    @Override
    public void onSuccess(Object object, RequestCode requestCode) {
        Log.d("LocationUpdateService", "onSuccess: location uploaded requestCode " + requestCode.toString());

    }

}
