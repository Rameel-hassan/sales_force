package com.ibrahim.salesforce.utilities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.ibrahim.salesforce.R;
import com.ibrahim.salesforce.base.SFApplication;
import com.ibrahim.salesforce.permissions.RuntimePermissionHandler;
import com.ibrahim.salesforce.permissions.RuntimePermissionUtils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Hassan Usman on 1/11/2018.
 */
public class GPSTrackerService extends Service implements LocationListener {
    private Context mContext = null;

    private final int REQ_CODE_LOCATION_PERMISSION = 1002;

    private final IBinder mBinder = new LocalBinder();

    //flag for GPS Status
    public boolean isGPSEnabled = false;

    public boolean isShowingSettingDialog = false;


    //flag for network status
    boolean isNetworkEnabled = false;

    boolean canGetLocation = false;

    Location location;
    double latitude;
    double longitude;

    public class LocalBinder extends Binder {
        public GPSTrackerService getService(Context context) {
            // Return this instance of LocalService so clients can call public methods
            mContext = context;
            return GPSTrackerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    //The minimum distance to change updates in metters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 2; //10 metters

    //The minimum time beetwen updates in milliseconds

    private static final long MIN_TIME_BW_UPDATES = 500; // 1 minute

    protected LocationManager locationManager;

    public GPSTrackerService() { }


    public boolean isGSPOn() {


        if (mContext == null) {
            mContext = getApplicationContext();
        }

        if (locationManager == null)
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);

        //getting GPS status
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        //getting network status
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        //activitySavingError

        if (!isGPSEnabled) {
            return false;
        } else

            return true;

    }

    public void getcurrentLocationStart() {
        RuntimePermissionHandler.requestPermission(REQ_CODE_LOCATION_PERMISSION, (Activity) mContext, mPermissionListener, RuntimePermissionUtils.LocationPermission);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        RuntimePermissionHandler.onRequestPermissionsResult((Activity) mContext, requestCode, permissions, grantResults);
    }

    private RuntimePermissionHandler.PermissionListener mPermissionListener = new RuntimePermissionHandler.PermissionListener() {
        @Override
        public void onRationale(final @NonNull RuntimePermissionHandler.PermissionRequest permissionRequest, final Activity target, final int requestCode, @NonNull final String[] permissions) {
            switch (requestCode) {
                case REQ_CODE_LOCATION_PERMISSION:
                    new androidx.appcompat.app.AlertDialog.Builder(target)
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
            }
        }

        @Override
        public void onAllowed(int requestCode, @NonNull String[] permissions) {
            switch (requestCode) {
                case REQ_CODE_LOCATION_PERMISSION:
                    getLocation();
                    break;
            }
        }

        @Override
        public void onDenied(final @NonNull RuntimePermissionHandler.PermissionRequest permissionRequest, Activity target, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, RuntimePermissionHandler.DENIED_REASON deniedReason) {
            if (deniedReason == RuntimePermissionHandler.DENIED_REASON.USER) {
                switch (requestCode) {
                    case REQ_CODE_LOCATION_PERMISSION:
                        Toast.makeText(target, R.string.location_permission_denied, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }

        @Override
        public void onNeverAsk(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            switch (requestCode) {
                case REQ_CODE_LOCATION_PERMISSION:
                    new androidx.appcompat.app.AlertDialog.Builder((Activity) mContext)
                            .setPositiveButton(R.string.allow, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(@NonNull DialogInterface dialog, int which) {
                                    RuntimePermissionUtils.openAppSettings((Activity) mContext);
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
            }
        }
    };

    @SuppressLint("MissingPermission")
    public Location getLocation() {
        try {

            if (mContext == null) {
                mContext = SFApplication.getAppContext();
            }
            if (locationManager == null)
                locationManager = (LocationManager) SFApplication.getAppContext().getSystemService(LOCATION_SERVICE);

            //getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            //getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                showSettingsAlert();
            } else {
                this.canGetLocation = true;

                //First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    Log.d("Network", "Network");

                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        updateGPSCoordinates();
                    }
                }

                //if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        if (ActivityCompat.checkSelfPermission(SFApplication.getAppContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return null;
                        }


                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        Log.d("GPS Enabled", "GPS Enabled");

                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            updateGPSCoordinates();
                        }
                    }
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
            Log.e("Error : Location", "Impossible to connect to LocationManager", e);
        }

        return location;
    }


    public void updateGPSCoordinates() {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     */

    public void stopUsingGPS() {
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(SFApplication.getAppContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.removeUpdates(GPSTrackerService.this);
        }
    }

    /**
     * Function to get latitude
     */
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        return latitude;
    }

    /**
     * Function to get longitude
     */
    public double getLongitude() {
//        if (location != null)
//        {
//            longitude = location.getLongitude();
//        }

        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog
     */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        //Setting Dialog Title
        alertDialog.setTitle("Alert");

        //Setting Dialog Message
        alertDialog.setMessage("Please turn on your GPS");

        isShowingSettingDialog = true;

        //On Pressing Setting button
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
                //isGPSEnabled = true;

            }
        });

        //On pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    /**
     * Get list of address by latitude and longitude
     *
     * @return null or List<Address>
     */
    public List<Address> getGeocoderAddress(Context context) {
        if (location != null) {
            Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                return addresses;
            } catch (IOException e) {
                //e.printStackTrace();
                Log.e("Error : Geocoder", "Impossible to connect to Geocoder", e);
            }
        }

        return null;
    }

    /**
     * Try to get AddressLine
     *
     * @return null or addressLine
     */
    public String getAddressLine(Context context) {
        List<Address> addresses = getGeocoderAddress(context);
        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            String addressLine = address.getAddressLine(0);

            return addressLine;
        } else {
            return null;
        }
    }

    /**
     * Try to get Locality
     *
     * @return null or locality
     */
    public String getLocality(Context context) {
        List<Address> addresses = getGeocoderAddress(context);
        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            String locality = address.getLocality();

            return locality;
        } else {
            return null;
        }
    }

    /**
     * Try to get Postal Code
     *
     * @return null or postalCode
     */
    public String getPostalCode(Context context) {
        List<Address> addresses = getGeocoderAddress(context);
        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            String postalCode = address.getPostalCode();

            return postalCode;
        } else {
            return null;
        }
    }

    /**
     * Try to get CountryName
     *
     * @return null or postalCode
     */
    public String getCountryName(Context context) {
        List<Address> addresses = getGeocoderAddress(context);
        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            String countryName = address.getCountryName();

            return countryName;
        } else {
            return null;
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        latitude = location.getLatitude();
        longitude = location.getLongitude();

        this.location = location;
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }


}
