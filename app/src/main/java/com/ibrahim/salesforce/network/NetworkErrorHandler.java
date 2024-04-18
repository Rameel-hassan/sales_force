package com.ibrahim.salesforce.network;

import android.content.Context;

import com.ibrahim.salesforce.R;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.HttpException;

public final class NetworkErrorHandler {


    private NetworkErrorHandler() {

    }

    public static String get(Context context, Throwable throwable) {
        int resId = R.string.global_network_error;

        if (throwable instanceof HttpException) {
            HttpException exception = (HttpException) throwable;
            if (exception.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                //throw new UnauthorizedException();
                resId = R.string.global_network_fail;
            } else if (exception.code() == HttpURLConnection.HTTP_NOT_FOUND) {
                resId = R.string.global_network_not_found;
            } else {
                resId = R.string.global_network_fail;
            }
        }
        if (throwable instanceof UnknownHostException) {
            resId = R.string.global_network_unknown_host;
        } else if (throwable instanceof SocketTimeoutException) {
            resId = R.string.global_network_timeout;
        } else if (throwable instanceof InterruptedIOException) {
            resId = R.string.global_network_timeout;
        } else if (throwable instanceof IOException) {
            resId = R.string.global_network_timeout;
        } else {
            resId = R.string.global_network_no_data;
        }

        return context.getString(resId);
    }

}
