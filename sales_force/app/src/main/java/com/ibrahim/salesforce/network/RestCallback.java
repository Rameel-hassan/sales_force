package com.app.salesforce.network;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;

import com.app.salesforce.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RestCallback implements Callback<String> {
    private int msgResId = R.string.progressbar_default_msg;
    private ProgressDialog progressDialog = null;
    private ServerConnectListener callback;
    private boolean isCanceled = false;
    private boolean dontHideProgress = false;
    private Activity context;
    private RequestCode requestCode;

    public RestCallback(Activity context, ServerConnectListener callback, RequestCode requestCode) {
        this.context = context;
        this.callback = callback;
        this.requestCode = requestCode;
    }

    public RestCallback showProgress(boolean showProgress, int mmsgResId) {
        if (mmsgResId != 0)
            this.msgResId = mmsgResId;
        if (showProgress) {
            showProgress(msgResId);
        }
        return this;
    }

    public RestCallback dontHideProgress(boolean dontHideProgress) {//true in case don't want to hide progress
        this.dontHideProgress = dontHideProgress;
        return this;
    }

    public void cancel() {
        isCanceled = true;
    }

    public boolean isCancelled() {
        return isCanceled;
    }

    public void showProgress(int mmsgResId) {
//        progressDialog = DialogFactory.createProgressDialog(context, mmsgResId);
    }

    public void stopProgress() {
        if (progressDialog != null && progressDialog.isShowing() && !dontHideProgress) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onResponse(Call<String> call, Response<String> response) {
        if (isCancelled()) {
            return;
        }
        if (call != null && !call.isCanceled()) {
            Log.d("call", " != null");
            call.cancel();
        }
        stopProgress();
        Log.e("onSuccess", " onResponse");
        if (response.isSuccessful()) {
            try {
                String responseStr = response.body().replaceAll("(\r\n|\n)", "");
                if (responseStr == null || responseStr.trim().equals("")) {
                    callback.onFailure(context.getString(R.string.global_network_no_data), requestCode);
                    return;
                }
                callback.onSuccess(responseStr, requestCode);
            } catch (Throwable t) {
                //Exception caught from response.body() method
                callback.onFailure(NetworkErrorHandler.get(context, t), requestCode);
            }
        } else {
            Log.e(",", "handle request errors yourself");
            int statusCode = response.code();
            // handle request errors yourself
            try {
                String errorDetail = response.errorBody().string();
                callback.onFailure(errorDetail, requestCode);

            } catch (Throwable t) {
                //Exception caught from response.errorBody().string() method
                callback.onFailure(NetworkErrorHandler.get(context, t), requestCode);
            }

        }

    }

    @Override
    public void onFailure(Call<String> call, Throwable t) {
        if (call != null && !call.isCanceled()) {
            call.cancel();
        }
        t.printStackTrace();
        callback.onFailure(NetworkErrorHandler.get(context, t), requestCode);
        stopProgress();
    }


}