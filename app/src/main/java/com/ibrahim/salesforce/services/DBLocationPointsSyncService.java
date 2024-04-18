package com.ibrahim.salesforce.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

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
import com.ibrahim.salesforce.utilities.Constants;
import com.ibrahim.salesforce.utilities.Utility;

import java.util.ArrayList;

import retrofit2.Call;


/**
 * Created by Yasir on 5/15/2016.
 */
public class DBLocationPointsSyncService extends IntentService implements ServerConnectListenerObject {

    private ApiService mService;


    public DBLocationPointsSyncService() {
        super(DBLocationPointsSyncService.class.getName());
        setIntentRedelivery(true);
    }

    public DBLocationPointsSyncService(String name) {
        super(DBLocationPointsSyncService.class.getName());
        setIntentRedelivery(true);
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mService = RestClient.getInstance(DBLocationPointsSyncService.this);
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        startForeGround();
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d("Service", "DBLocationPointsSyncService Started");
        if (Utility.isNetworkAvailable(getApplicationContext())) {

            ArrayList<LocationDetails> images= DatabaseManager.getInstance(getApplicationContext()).getUserTrackerPoints();

            if(images!=null && images.size()>0)
            {
                Log.d("Service", "total saved location points = " +images.size());
                for (int i=0;i<images.size();i++)
                {
                    uploadImage(images.get(i));
                }
            }
        }

        Log.d("Service", "DBLocationPointsSyncService End");
    }


    private void uploadImage(LocationDetails locationDetails)
    {
        Call<GetLiveLocationResponse> userObject = mService.saveLocation(locationDetails);
        RestCallbackObject callbackObject = new RestCallbackObject(DBLocationPointsSyncService.this, this, RequestCode.POST_SAVE_LOCATION).showProgress(false, 0).dontHideProgress(false);
        userObject.enqueue(callbackObject);

    }


    private void startForeGround()
    {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, Constants.Notification_Location_Uploading_Channel_ID);

        mBuilder.setSmallIcon(R.drawable.ic_map_view);
        mBuilder.setContentText("Location uploading");
        mBuilder.setColor( ContextCompat.getColor(this, R.color.blue));

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

        startForeground(1212,notification);
    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = Constants.Notification_Location_Uploading_Channel_Name;
            String description = Constants.Notification_Location_Uploading_Channel_Desc;
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(Constants.Notification_Location_Uploading_Channel_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onFailure(String error, RequestCode requestCode) {

    }

    @Override
    public void onSuccess(Object object, RequestCode requestCode) {
        Log.d("onSuccess", "DBLocationPointsSyncService onSuccess: location uploaded requestCode " + requestCode.toString());
        GetLiveLocationResponse serverResponse = (GetLiveLocationResponse) object;

        DatabaseManager.getInstance(getApplicationContext()).deleteUserTrackerPoint(serverResponse.getData());

    }
}
