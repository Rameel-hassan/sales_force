package com.app.salesforce.network;

public interface ServerConnectListenerObject {
    public void onFailure(String error, RequestCode requestCode);

    public void onSuccess(Object object, RequestCode requestCode);
}
