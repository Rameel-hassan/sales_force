package com.app.salesforce.network;

public interface ServerConnectListener {
    public void onFailure(String error, RequestCode requestCode);

    public void onSuccess(String response, RequestCode requestCode);
}
