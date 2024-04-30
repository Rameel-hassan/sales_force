package com.app.salesforce.network;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.app.salesforce.BuildConfig;
import com.app.salesforce.base.SFApplication;
import com.app.salesforce.utilities.AppKeys;
import com.app.salesforce.utilities.AppPreference;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {
    private static ApiService service;

    public static ApiService getInstance(Context context) {
        if (service != null) {
            service = null;
        }
       /* Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .excludeFieldsWithModifiers(Modifier.TRANSIENT)
                .create();*/
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.readTimeout(50, TimeUnit.SECONDS);
        builder.connectTimeout(50, TimeUnit.SECONDS);
        builder.writeTimeout(50, TimeUnit.SECONDS);
        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(@NonNull Chain chain)  {

                try{
                    Request request = chain.request().newBuilder().build();
                    return chain.proceed(request);
                }catch (Exception e){
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // Display the error to the user using a Toast or other UI element
                            Toast.makeText(context.getApplicationContext(), "An error occurred: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
                return null;
            }
        });
        if (BuildConfig.DEBUG) {
            try {
                HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                interceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                builder.addNetworkInterceptor(interceptor);
            }catch (Exception e){
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // Display the error to the user using a Toast or other UI element
                        Toast.makeText(context.getApplicationContext(), "An error occurred: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
//        int cacheSize = 2 * 1024 * 1024; // 2 MiB
//        Cache cache = new Cache(context.getCacheDir(), cacheSize);
//        builder.cache(cache);
        String baseURL;
        if (AppPreference.getValue(SFApplication.getAppContext(), AppKeys.KEY_BASE_URL) == null)
            baseURL = AppWebServices.BASE_URL;
        else
            baseURL = AppPreference.getValue(SFApplication.getAppContext(), AppKeys.KEY_BASE_URL);

        Retrofit retrofit = new Retrofit
                .Builder()
                .client(builder.build())
                .addConverterFactory(new NullOnEmptyConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseURL).build();
        service = retrofit.create(ApiService.class);

        return service;
    }
}
