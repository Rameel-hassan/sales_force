package com.app.salesforce.activity;

import static android.graphics.Color.TRANSPARENT;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.app.salesforce.R;
import com.app.salesforce.adapters.GetTodayTargetedSchoolsAdapter;
import com.app.salesforce.adapters.TodayRemindersAdapter;
import com.app.salesforce.dialoge.DetailsDialogFragment;
import com.app.salesforce.dialoge.NearBySchoolsFragment;
import com.app.salesforce.dialoge.ResetLatLongDialog;
import com.app.salesforce.model.GetTodayTargetedSchools;
import com.app.salesforce.model.MainOption;
import com.app.salesforce.network.ApiService;
import com.app.salesforce.network.RequestCode;
import com.app.salesforce.network.RestCallbackObject;
import com.app.salesforce.network.RestClient;
import com.app.salesforce.network.ServerConnectListenerObject;
import com.app.salesforce.permissions.RuntimePermissionHandler;
import com.app.salesforce.permissions.RuntimePermissionUtils;
import com.app.salesforce.request.RouteDetail;
import com.app.salesforce.response.GetServerResponse;
import com.app.salesforce.response.MainDashboardEntry;
import com.app.salesforce.response.ServerResponse;
import com.app.salesforce.response.TodayActiveReminders;
import com.app.salesforce.utilities.AppKeys;
import com.app.salesforce.utilities.BadgeDrawable;
import com.app.salesforce.utilities.DateTimeUtilites;
import com.app.salesforce.utilities.GPSTrackerService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;

public class DashboardActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener, ServerConnectListenerObject {
    private RecyclerView rcOptions;
    private TextView appVersion;
    private Context mContext;
    private Dialog detailsDialog;
    private String versionName;
    private ApiService mService;

    private GetServerResponse mLoginResponse;
    SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView rv_reminders;


    private List<MainOption> lstallOptions;

    GetTodayTargetedSchools todayTargetedSchools;

    private final int REQ_CODE_LOCATION_PERMISSION = 1002;
    private final int REQ_CODE_STORAGE_PERMISSION = 1003;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    DrawerLayout Drawer;
    ActionBar actionBar;
    BottomNavigationView bottomNavigationView;
    LinearLayout totalSampleVisit;
    LinearLayout totalSampeledelevered;
    LinearLayout totalsamplereturn;
    LinearLayout totalSelectedbooks;
    LinearLayout ll_today_reminders;
    TextView mTotalVisit;
    TextView mTotaldelevered;
    TextView mTotalreturn;
    TextView mTotalbooks;
    TextView mTotalTarget;
    Button mStartStopRouteBtn;

    LocationManager locationManager;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 0;//1000 * 60 * 1; // 1 minute

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activitydashboard);
        mContext = this;
        ll_today_reminders = findViewById(R.id.ll_today_reminders);
        navigationView = findViewById(R.id.nav_view);
        bottomNavigationView = findViewById(R.id.botton_navigation_id);
        totalSampleVisit = findViewById(R.id.total_sample_visits);
        totalSampeledelevered = findViewById(R.id.total_sample_delivered);
        totalsamplereturn = findViewById(R.id.total_sample_return);
        totalSelectedbooks = findViewById(R.id.total_selected_books);

        mStartStopRouteBtn = findViewById(R.id.btn_route_start_stop);
        rv_reminders = findViewById(R.id.rv_reminders);

        mTotalVisit = findViewById(R.id.textview_total_visit);
        mTotaldelevered = findViewById(R.id.textview_total_delivery);
        mTotalreturn = findViewById(R.id.textview_total_return);
        mTotalbooks = findViewById(R.id.textview_total_selected_books);
        mTotalTarget = findViewById(R.id.textview_total_target_value);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        navigationView.setItemIconTintList(null);
        navigationView.setItemTextColor(ColorStateList.valueOf(Color.WHITE));


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        Drawer = findViewById(R.id.drawerlayout);
        navigationView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) this);
        navigationView.bringToFront();
        toggle = new ActionBarDrawerToggle(this, Drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(true);

        Drawer.addDrawerListener(toggle);

        toggle.syncState();
        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        mStartStopRouteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogStartStopRoute();
            }
        });
        totalSampleVisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIntent(3);

            }
        });
        totalSampeledelevered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        totalsamplereturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        totalSelectedbooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.school_registration_:
                        startIntent(1);
                        break;

                    case R.id.online_visits_:
                        showEditDialog(7);
                        break;

                    case R.id.mark_attendance_:
                        startIntent(6);

                        break;


                }

                return false;
            }
        });

//        appVersion.setText("V: " + versionName);



//        initGui(SFApplication.getAppResources().getString(R.string.dashboard));
        Log.d("class_name", this.getClass().getSimpleName());
        Paper.init(this);
        mLoginResponse = Paper.book().read(AppKeys.KEY_LOGIN_RESPONSE);

        if(mLoginResponse.getData().getIsRouteStarted()){
            mStartStopRouteBtn.setText(getResources().getString(R.string.stop));
        }else {
            mStartStopRouteBtn.setText(getResources().getString(R.string.start));
        }
        if (mLoginResponse.getData().getIsMarked() )
            mStartStopRouteBtn.setClickable(true);
        else
            mStartStopRouteBtn.setClickable(false);

        boolean isRm = mLoginResponse.getData().getIsRm();
        navigationView.getMenu().findItem(R.id.routeHostory).setVisible(isRm);
        navigationView.getMenu().findItem(R.id.get_visit).setVisible(isRm);

        initTodayReminders();


        hideItem();
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);
    }

    private void showDialogStartStopRoute() {
        String title = "";
        if (mLoginResponse.getData().getIsRouteStarted())
            title = "Stop Route";
        else
            title = "Start Route";

        // show dialog to ask user action
        new AlertDialog.Builder(DashboardActivity.this).setTitle(title)
                .setMessage(
                        "Are you sure \n you want to " + title)
                .setPositiveButton("ALLOW", (dialogInterface, i) -> {
                    startStopRoute(! mLoginResponse.getData().getIsRouteStarted());

                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();

    }



    public void startStopRoute(boolean isStarted) {
        RouteDetail routeDetail = new RouteDetail();


        mService = RestClient.getInstance(this);
        Call<ServerResponse> userObject;
        RestCallbackObject callbackObject;

        mDialog = new ProgressDialog(this);
        mDialog.setCancelable(false);
        // Toast.makeText(this,getTodayDate(),Toast.LENGTH_SHORT).show();
        mDialog.setMessage("Progressing...");
        mDialog.show();

        routeDetail.setUserID(mLoginResponse.getData().getSOID());

        if (!isStarted) {


            routeDetail.setStartingTime(DateTimeUtilites.getCurrentDateTime());
            routeDetail.setStartingLat(GPSTrackerService.latitude);
            routeDetail.setStartingLong(GPSTrackerService.longitude);
            routeDetail.setStartingPhone(Build.MODEL);



            userObject = mService.saveStartingLatLng(routeDetail);
            callbackObject = new RestCallbackObject(this, DashboardActivity.this, RequestCode.API_START_ROUTE_DETAILS);
        }else {


            routeDetail.setEndingTime(DateTimeUtilites.getCurrentDateTime());
            routeDetail.setEndingLat(GPSTrackerService.latitude);
            routeDetail.setEndingLong(GPSTrackerService.longitude);
            routeDetail.setEndingPhone(Build.MODEL);

            userObject = mService.saveEndingLatLng(routeDetail);
            callbackObject = new RestCallbackObject(this, DashboardActivity.this, RequestCode.API_END_ROUTE_DETAILS);

        }

        userObject.enqueue(callbackObject);
    }



    private ProgressDialog mDialog;

    private void loadTodayReminders() {
        mDialog = new ProgressDialog(this);
        mDialog.setCancelable(false);
        // Toast.makeText(this,getTodayDate(),Toast.LENGTH_SHORT).show();
        mDialog.setMessage("Getting Today Reminders, please wait...");
        mDialog.show();
        mService = RestClient.getInstance(this);
        Call<GetServerResponse> userObject = mService.getTodayReminders(mLoginResponse.getData().getSOID(), getTodayDate());
        RestCallbackObject callbackObject = new RestCallbackObject((Activity)
                mContext, this, RequestCode.GET_TODAY_REMINDERS)
                .showProgress(true, 0)
                .dontHideProgress(false);
        userObject.enqueue(callbackObject);
    }

    private String getTodayDate() {
        return new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).format(new Date());
    }

    private void hideItem() {
        if (!mLoginResponse.getData().getIsRegionalHead()) {
            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.routeHostory).setVisible(false);
        }
        if (mLoginResponse.getData().getIsAssignSampleMenu()) {
            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.manage_books_samples_info).setVisible(true);
        }
        if (mLoginResponse.getData().getIsRm()) {
            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.get_visit).setVisible(true);
        }

    }

    private RuntimePermissionHandler.PermissionListener mPermissionListener = new RuntimePermissionHandler.PermissionListener() {
        @Override
        public void onRationale(final @NonNull RuntimePermissionHandler.PermissionRequest permissionRequest, final Activity target, final int requestCode, @NonNull final String[] permissions) {
            switch (requestCode) {
                case REQ_CODE_LOCATION_PERMISSION:
                    new AlertDialog.Builder(target)
                            .setPositiveButton(R.string.allow, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(@NonNull DialogInterface dialog, int which) {
                                    permissionRequest.proceed(target, requestCode, permissions);
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton(R.string.deny, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(@NonNull DialogInterface dialog, int which) {
                                    permissionRequest.cancel(target, requestCode, permissions);
                                    dialog.dismiss();
                                }
                            })
                            .setCancelable(false)
                            .setMessage(R.string.location_permission_rational)
                            .show();
                    break;
                case REQ_CODE_STORAGE_PERMISSION:
                    new AlertDialog.Builder(target)
                            .setPositiveButton(R.string.allow, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(@NonNull DialogInterface dialog, int which) {
                                    permissionRequest.proceed(target, requestCode, permissions);
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton(R.string.deny, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(@NonNull DialogInterface dialog, int which) {
                                    permissionRequest.cancel(target, requestCode, permissions);
                                    dialog.dismiss();
                                }
                            })
                            .setCancelable(false)
                            .setMessage("Storage permission is needed to save images")
                            .show();
                    break;

            }
        }

        @Override
        public void onAllowed(int requestCode, @NonNull String[] permissions) {
            switch (requestCode) {
                case REQ_CODE_LOCATION_PERMISSION:
                    mLocationService.getLocation();
                    //getLocation();
                    break;
            }
        }

        @Override
        public void onDenied(final @NonNull RuntimePermissionHandler.PermissionRequest permissionRequest, Activity target, int requestCode, @NonNull String[] permissions,
                             @NonNull int[] grantResults, RuntimePermissionHandler.DENIED_REASON deniedReason) {
            if (deniedReason == RuntimePermissionHandler.DENIED_REASON.USER) {
                switch (requestCode) {
                    case REQ_CODE_LOCATION_PERMISSION:
                        Toast.makeText(target, R.string.location_permission_denied, Toast.LENGTH_SHORT).show();
                        break;
                    case REQ_CODE_STORAGE_PERMISSION:
                        Toast.makeText(target, "Storage permission denied by user", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }

        @Override
        public void onNeverAsk(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            switch (requestCode) {
                case REQ_CODE_LOCATION_PERMISSION:
                    new AlertDialog.Builder(DashboardActivity.this)
                            .setPositiveButton(R.string.allow, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(@NonNull DialogInterface dialog, int which) {
                                    RuntimePermissionUtils.openAppSettings(DashboardActivity.this);
                                }
                            })
                            .setNegativeButton(R.string.deny, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(@NonNull DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setCancelable(false)
                            .setMessage(R.string.location_permission_denied)
                            .show();
                    break;
                case REQ_CODE_STORAGE_PERMISSION:
                    new AlertDialog.Builder(DashboardActivity.this)
                            .setPositiveButton(R.string.allow, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(@NonNull DialogInterface dialog, int which) {
                                    RuntimePermissionUtils.openAppSettings(DashboardActivity.this);
                                }
                            })
                            .setNegativeButton(R.string.deny, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(@NonNull DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setCancelable(false)
                            .setMessage("Storage permission denied by user")
                            .show();
                    break;
            }
        }
    };


    private void showEditDialogFrg(int type) {
        FragmentManager fm = getSupportFragmentManager();
        NearBySchoolsFragment editNearBySchoolsActivity = NearBySchoolsFragment.newInstance("Information", this, type);
        editNearBySchoolsActivity.show(fm, "fragment_edit_name");
    }

    private void showEditDialog(int type) {
        FragmentManager fm = getSupportFragmentManager();
        DetailsDialogFragment editNameDialogFragment = DetailsDialogFragment.newInstance("Information", this, type);
        editNameDialogFragment.show(fm, "fragment_edit_name");
    }

    private void showResetLatLongDialog() {
        FragmentManager fm = getSupportFragmentManager();
        ResetLatLongDialog rsll = ResetLatLongDialog.newInstance("Information", this);
        rsll.show(fm, "fragment_edit_name");
    }

    public void getcurrentLocationStart() {
        RuntimePermissionHandler.requestPermission(REQ_CODE_LOCATION_PERMISSION, this, mPermissionListener, RuntimePermissionUtils.LocationPermission);
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else {
            return true;
        }
    }

    public void getStoragePermission() {
        RuntimePermissionHandler.requestPermission(REQ_CODE_STORAGE_PERMISSION, this, mPermissionListener, RuntimePermissionUtils.StoragePermission);

    }

    @Override
    public void onStart() {
        super.onStart();
        //TO CHECK STORAGE PERMISSION
        Intent intent = new Intent(this, GPSTrackerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            getcurrentLocationStart();

        // loadTodayReminders();
        //else
        //  mLocationService.getLocation();
    }


    public void initTodayReminders() {

        if (mLoginResponse.getData().getTodayReminders() != null && !mLoginResponse.getData().getTodayReminders().isEmpty()) {
            ll_today_reminders.setVisibility(View.VISIBLE);
            ArrayList<TodayActiveReminders> reminders = mLoginResponse.getData().getTodayReminders();

            TodayRemindersAdapter adapter = new TodayRemindersAdapter(reminders);

            rv_reminders.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

            rv_reminders.setAdapter(adapter);

        } else {
            ll_today_reminders.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "There are no reminders for today.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            GPSTrackerService.latitude = location.getLatitude();
            GPSTrackerService.longitude = location.getLongitude();

            /*if(geoLocation.getWebID()==null)
             getPlaceId(lat, lon);*/
//            locationManager.removeUpdates(locationListener);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };


    private void requestLocationUpdate() {
        try {
            boolean gps_enabled = false, network_enabled = false;
            try {
                gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception ex) {
            }
            try {
                network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception ex) {
            }
            Location location = null;
            if (network_enabled) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                if (locationManager != null)
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            } else if (gps_enabled) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                if (locationManager != null)
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }

            if (location != null) {
                GPSTrackerService.latitude = location.getLatitude();
                GPSTrackerService.longitude = location.getLongitude();
//                String lat = Double.toString(location.getLatitude());
//                String lon = Double.toString(location.getLongitude());
//                geoLocation.setLatitude(lat);
//                geoLocation.setLogitude(lon);
                // getPlaceId(lat, lon);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }



    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this, GPSTrackerService.class);
        unbindService(mConnection);
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestLocationUpdate();

        if (mLocationService != null && mLocationService.isShowingSettingDialog) {
            mLocationService.isShowingSettingDialog = false;
            mLocationService.getLocation();
        }
        getTodayTargetedSchools(mLoginResponse.getData().getSOID());
    }

    public static GPSTrackerService mLocationService = new GPSTrackerService();

    boolean mBound = false;
    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            GPSTrackerService.LocalBinder binder = (GPSTrackerService.LocalBinder) service;
            mLocationService = binder.getService(mContext);
            //if (mLocationService.isGSPOn()) {
            mLocationService.getLocation();
            //}
            mBound = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };


    private void startIntent(Context context, Class cls) {
        Intent intent = new Intent(context, cls);
        startActivity(intent);
    }

    private void startIntent(int type) {
        Intent intent = new Intent(mContext, FragmentShownActivity.class);
        intent.putExtra(AppKeys.FRAGMENT_TYPE, type);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_favorite) {
            startIntent(7);
            return true;
        }else if(id==R.id.notification){
            if(todayTargetedSchools!=null&&todayTargetedSchools.getTodayTargatedSchools().size()> 0){
                getNotifications(todayTargetedSchools.getTodayTargatedSchools());
            }else{
                Toast.makeText(this, "No Targeted Schools found!", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getNotifications(List<GetTodayTargetedSchools.TodayTargetedSchool> targetedSchools) {
        // Create a custom dialog
        Dialog customDialog = new Dialog(this);
        customDialog.setContentView(R.layout.db_targeted_schools);


// Get the window of the dialog
        Window window = customDialog.getWindow();

        if (window != null) {
            // Set the background color
            window.setBackgroundDrawable(new ColorDrawable(TRANSPARENT));
            // Set the width and height of the    dialog
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());

            // Set the desired width and height here (in pixels)
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;/* your width in pixels */;
            layoutParams.height =WindowManager.LayoutParams.WRAP_CONTENT; /* your height in pixels */;

            // Apply the layout parameters to the window
            window.setAttributes(layoutParams);
        }
        // Initialize RecyclerView
        RecyclerView rv = customDialog.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));

        GetTodayTargetedSchoolsAdapter adapter = new GetTodayTargetedSchoolsAdapter(targetedSchools);
        rv.setAdapter(adapter);

        // Set dialog title
        customDialog.setTitle("Today's Target Schools");

        // Initialize and set an OK button
        Button okButton = customDialog.findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform an action when the OK button is clicked
                customDialog.dismiss();
            }
        });

        // Show the custom dialog
        customDialog.show();
    }
    LayerDrawable notificationCount;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Find the menu item for the bell icon
        MenuItem bellMenuItem = menu.findItem(R.id.notification);
        notificationCount = (LayerDrawable) bellMenuItem.getIcon();
        setBadgeCount(this, notificationCount, "0");
        return super.onCreateOptionsMenu(menu);
    }
    public  void setBadgeCount(Context context, LayerDrawable icon, String count) {

        BadgeDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.dashbaord:
                startIntent(getApplicationContext(), NewDashBoardActivity.class);
                break;
            case R.id.online_attendance:
                startIntent(6);
                break;
            case R.id.school_registration:
                startIntent(1);
                break;
            case R.id.update_school:
                startIntent(getApplicationContext(), EditSchoolInformation.class);
                break;
            case R.id.online_schools:
                showEditDialog(7);
                break;
            case R.id.offline_school:
                showEditDialog(8);
                break;
            case R.id.targeted_school:
                Intent i = new Intent(this, TargetedSchoolsActivity.class);
                startActivity(i);
                break;
            case R.id.logout:
                finish();
                Intent ii = new Intent(this, FragmentShownActivity.class);
                ii.putExtra("type",0);
                startActivity(ii);
                break;
            case R.id.add_targeted_school:
                // showEditDialog(18);
                Intent intent = new Intent(mContext, AddTargetedProduct.class);
                startActivity(intent);
                break;
            case R.id.mi_not_interested_visit:
                startIntent(getApplicationContext(), NotInterestedVisit.class);
                break;
            case R.id.my_visits:
                startIntent(3);
                break;
            case R.id.visit_map_view:
                startIntent(getApplicationContext(), MapsActivity.class);
                break;
            case R.id.mi_visit_history:
                startIntent(getApplicationContext(), VisitHistoryActivity.class);
                break;
            case R.id.routeHostory:
                startIntent(getApplicationContext(), MapsRoutesActivity.class);
                break;
            case R.id.manage_books_samples_info:
                startIntent(getApplicationContext(), BooksCollectionActivity.class);
                break;
            case R.id.session_wise_school:
                startIntent(8);
                break;
            case R.id.multi_purpose_visit:
                startIntent(getApplicationContext(), MultiPurposeActivityMenu.class);
                break;
            case R.id.reset_location:
                showResetLatLongDialog();
                break;
            case R.id.expense_sheets:
                startIntent(getApplicationContext(), DailyExpenseReport.class);
                break;

            case R.id.near_by_school:
                showEditDialogFrg(15);
                startIntent(getApplicationContext(), NearBySchoolsActivity.class);
                break;
            case R.id.mi_sale_history:
                startIntent(getApplicationContext(), SampleHistoryActivity.class);
                break;
            case R.id.reminders:
                startIntent(5);
                break;

            case R.id.current_location:
                startIntent(4);
                break;

            case R.id.set_visit:
                startIntent(9);
                break;
            case R.id.get_visit:
                startIntent(10);
                break;
            case R.id.shop_registration:
                startIntent(2);
                break;

//        rcOptions.addOnItemTouchListener(
//                new RecyclerItemClickListener(this, rcOptions, new RecyclerItemClickListener.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(View view, int position) {
//                        MainOption option = lstallOptions.get(position);
//                        if (option.getOption_name() == "Dashboard") {
//                            startIntent(getApplicationContext(), NewDashBoardActivity.class);
//                        } else if (option.getOption_name() == "School Registration") {
//                            startIntent(1);
//                        } else if (option.getOption_name() == "Shop Registration") {
//                            startIntent(2);
//                        } else if (option.getOption_name() == "My Visits") {
//                            startIntent(3);
//                        } else if (option.getOption_name() == "My Current Location") {
//                            startIntent(4);
//                        } else if (option.getOption_name() == "Reminders") {
//                            startIntent(5);
//                        } else if (option.getOption_name() == "Online Attendance") {
//                            startIntent(6);
//                        } else if (option.getOption_name() == "Visit Details") {
//                            showEditDialog(7);
//                        } else if (option.getOption_name() == "Session Wise Schools") {
//                            startIntent(8);
//                        } else if (option.getOption_name() == "Set Visit Plan") {
//                            startIntent(9);
//                        } else if (option.getOption_name() == "Visit Plan Details") {
//                            startIntent(10);
//                        } else if (option.getOption_name() == "Reset Location") {
//                            showResetLatLongDialog();
//                        } else if (option.getOption_name() == "Activity MapView") {
//                            startIntent(getApplicationContext(), MapsActivity.class);
//                        }else if (option.getOption_name() == "Edit School Info") {
//                            startIntent(getApplicationContext(), EditSchoolInformation.class);
//                        } else if (option.getOption_name() == "Offline Visits") {
//                            showEditDialog(8);
//                        } else if (option.getOption_name() == "Delete School") {
//                            showEditDialog(10);
//                        } else if (option.getOption_name() == "Daily Expense Report") {
//                            startIntent(getApplicationContext(), DailyExpenseReport.class);
//                        } else if (option.getOption_name() == "MultiPurpose Visit") {
//                            startIntent(getApplicationContext(), MultiPurposeActivityMenu.class);
//                        } else if (option.getOption_name() == "Download Data") {
//                            startIntent(getApplicationContext(), SyncDataActivity.class);
//                        } else if(option.getOption_name() == "Near By Schools") {
//                            showEditDialogFrg(15);
////                            startIntent(getApplicationContext(), NearBySchoolsActivity.class);
//                        }
//                    }
//
//                    @Override
//                    public void onLongItemClick(View view, int position) {
//                        // do whatever
//                    }
//                })
//        );
//    }

        }

        Drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onRefresh() {
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }

        getDashBoardEntries(mLoginResponse.getData().getSOID());
        Log.d("DashboardActivity", "onRefresh: called");
//            syncMasters();
    }

    @Override
    public void onFailure(String error, RequestCode requestCode) {
        if (requestCode == RequestCode.API_GET_DASHBOARD_ENTRIES) {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
            Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
        } else if (requestCode == RequestCode.GET_TODAY_REMINDERS) {
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }
            ll_today_reminders.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
        }else if(requestCode == RequestCode.API_GET_TODAY_TARGETED_SCHOOLS){
            if (mDialog.isShowing()) {
                mDialog.hide();
            }
            setBadgeCount(mContext,notificationCount, String.valueOf(0));
            Toast.makeText(getApplicationContext(), "No Target Schools found.", Toast.LENGTH_SHORT).show();
        }else if(requestCode == RequestCode.API_START_ROUTE_DETAILS){
            if (mDialog.isShowing()) {
                mDialog.hide();
            }
        } else if(requestCode == RequestCode.API_END_ROUTE_DETAILS){
                if (mDialog.isShowing()) {
                    mDialog.hide();
                }
            }
        else {

            Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onSuccess(Object object, RequestCode requestCode) {
        if (requestCode == RequestCode.API_GET_DASHBOARD_ENTRIES) {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
            MainDashboardEntry dashboardEntry = ((GetServerResponse) object).getMainDashboard();
            setValueInDashBoard(dashboardEntry);
            Log.d("DashboardActivity", "onSuccess: dashboard entries");
        } else if (requestCode == RequestCode.GET_TODAY_REMINDERS) {

            if (mDialog.isShowing()) {
                mDialog.hide();
            }
        }else if(requestCode == RequestCode.API_GET_TODAY_TARGETED_SCHOOLS){
            if (mDialog.isShowing()) {
                mDialog.hide();
            }
        } else if(requestCode == RequestCode.API_START_ROUTE_DETAILS){
            mLoginResponse.getData().setIsRouteStarted(true);
            mStartStopRouteBtn.setText(getResources().getText(R.string.stop));
            if (mDialog.isShowing()) {
                    mDialog.hide();
                }
        } else if(requestCode == RequestCode.API_END_ROUTE_DETAILS){

                mLoginResponse.getData().setIsRouteStarted(false);
                mStartStopRouteBtn.setText(getResources().getText(R.string.start));
                if (mDialog.isShowing()) {
                  mDialog.hide();
                }
            }

            todayTargetedSchools=((GetTodayTargetedSchools) object);

            if(todayTargetedSchools.getTodayTargatedSchools().size()>0){
                // Update the notification count
                int count=todayTargetedSchools.getTodayTargatedSchools().size();
                setBadgeCount(mContext,notificationCount, String.valueOf(count));
            }else{
                setBadgeCount(mContext,notificationCount, String.valueOf(0));
            }
        }


    private void setValueInDashBoard(MainDashboardEntry dashboardEntry) {
        mTotalVisit.setText("Visits : " + dashboardEntry.getTotalVisits());
        mTotaldelevered.setText("Sample deliver : " + dashboardEntry.getTotalSampleDeliver());
        mTotalreturn.setText("Sample Return : " + dashboardEntry.getTotalSampleReturn());
        mTotalbooks.setText("Selected Books : " + dashboardEntry.getTotalSelectedBooks());
        mTotalTarget.setText("Target Value : " + dashboardEntry.getTargetValue());
    }

    private void getDashBoardEntries(int soid) {
        mService = RestClient.getInstance(this);
        Call<GetServerResponse> userObject = mService.getDashBoardData(soid);
        RestCallbackObject callbackObject = new RestCallbackObject(this, this, RequestCode.API_GET_DASHBOARD_ENTRIES);
        userObject.enqueue(callbackObject);
    }

    private void getTodayTargetedSchools(int soid) {

        if(mDialog!=null){
            mDialog.show();
        }else{
            mDialog=new ProgressDialog(this);
            mDialog.setMessage("Loading Data...");
        }

        mService = RestClient.getInstance(this);
        Call<GetTodayTargetedSchools> userObject = mService.getTodayTargetedSchools(soid);
        RestCallbackObject callbackObject = new RestCallbackObject(this, this, RequestCode.API_GET_TODAY_TARGETED_SCHOOLS);
        userObject.enqueue(callbackObject);
    }

}
