package com.ibrahim.salesforce.utilities;

import androidx.annotation.NonNull;

/**
 * Created by Hassan Usman on 14/10/2018.
 */
public interface PermissionResultCallBack {
    void isPermissionGranted(boolean result, int resultFor, String message);

    void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantResults);
}
