package com.app.salesforce.activity;

import static com.app.salesforce.model.GenreDataFactory.makeGenres;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.salesforce.R;
import com.app.salesforce.adapters.GenreAdapter;
import com.app.salesforce.model.SchoolsVisitingClass;
import com.app.salesforce.network.AppWebServices;
import com.app.salesforce.response.GetServerResponse;
import com.app.salesforce.utilities.AppKeys;
import com.razerdp.widget.animatedpieview.AnimatedPieView;
import com.razerdp.widget.animatedpieview.AnimatedPieViewConfig;
import com.razerdp.widget.animatedpieview.data.SimplePieInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.paperdb.Paper;

public class DashBoard_Activity extends AppCompatActivity {
    private Random random = new Random();
   /* private MaterialSpinner materialSpinner;
    private RequestQueue mRequestQueue;
    private List<SchoolsVisitingClass> schoolsVisitingClasses;
    private SchoolVisitingAdapter schoolVisitingAdapter;
    RecyclerView recyclerView;
    private  String vistit_this_month_url;
    private  String  new_schoolstoday;
    private  String   schools_new_visits;
    private  String   school_visit_today;*/

    ayalma.ir.expandablerecyclerview.ExpandableRecyclerView recyclerView;
    private static GetServerResponse mLoginResponse;
    RequestQueue requestQueue, queue;
    AnimatedPieView pieChart, this_month_pie_chart, new_schools_visits_today, add_new_schools;
    public GenreAdapter adapter;
    String url, this_month_api;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board_);
        mLoginResponse = Paper.book().read(AppKeys.KEY_LOGIN_RESPONSE);
        Log.d("class_name", this.getClass().getSimpleName());
        pieChart = (AnimatedPieView) findViewById(R.id.pie_char_school);
        this_month_pie_chart = (AnimatedPieView) findViewById(R.id.pie_char_school_);
        new_schools_visits_today = (AnimatedPieView) findViewById(R.id.pie_char_school_visits_today);
        add_new_schools = (AnimatedPieView) findViewById(R.id.pie_char_school_add_new_schools_today);
        requestQueue = Volley.newRequestQueue(this);
        queue = Volley.newRequestQueue(this);
        url = AppWebServices.BASE_URL + AppWebServices.API_Visits_Last_Month;
        this_month_api = AppWebServices.BASE_URL + AppWebServices.API_Visits_This_Month;
        String url_new_schools_today = AppWebServices.BASE_URL + AppWebServices.API_GET_SCHOOL_VISITS_TODAY;
        String url_new_schools_visits_today = AppWebServices.BASE_URL + AppWebServices.API_GET_NEW_SCHOOLS_TODAY;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("DashBoard");
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        getRequest(url);
        get_this_month_schools_data(this_month_api);
        get_add_visits_new_schools_toay(url_new_schools_visits_today);
        get_add_new_schools_today(url_new_schools_today);
        // RecyclerView has some built in animations to it, using the DefaultItemAnimator.
        // Specifically when you call notifyItemChanged() it does a fade animation for the changing
        // of the data in the ViewHolder. If you would like to disable this you can use the following:
        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        if (animator instanceof DefaultItemAnimator) {
            ((DefaultItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        adapter = new GenreAdapter(makeGenres(mLoginResponse.getData().getSOID()));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);


    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        adapter.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        adapter.onRestoreInstanceState(savedInstanceState);

    }


    public void getRequest(String url) {
        final List<SimplePieInfo> simplePieInfos = new ArrayList<>();
        final AnimatedPieViewConfig config = new AnimatedPieViewConfig();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url + "?SaleOfficerID=" + mLoginResponse.getData().getSOID(), null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("Schools");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                SimplePieInfo simplePieInfo = new SimplePieInfo();
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                SchoolsVisitingClass schoolsVisitingClass = new SchoolsVisitingClass();
                                String name = jsonObject.getString("SaleOfficerName");
                                String visits = jsonObject.getString("VisitCount");
                                simplePieInfo.setValue(Float.valueOf(visits));
                                simplePieInfo.setDesc(name + "=" + visits);
                                simplePieInfo.setColor(randomColor());
                                config.addData(simplePieInfo).drawText(true)
                                        .duration(1200).textSize(25)// Text description size
                                        .textMargin(8)// Margin between text and guide line
                                        .autoSize(true)// Auto fit chart radius
                                        .pieRadius(100)// Set chart radius
                                        .pieRadiusRatio(0.8f)// Chart's radius ratio for parent ViewGroup
                                        .guidePointRadius(2)// Chart's radius
                                        .guideLineWidth(3)// Text guide line stroke width
                                        .guideLineMarginStart(8)
                                        .textGravity(AnimatedPieViewConfig.ALIGN);
                                this_month_pie_chart.applyConfig(config);
                                this_month_pie_chart.start();
                            }
// The following two sentences can be replace directly 'mAnimatedPieView.start (config); '
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                    }
                });
        requestQueue.add(jsonObjectRequest);


    }

    public void get_this_month_schools_data(String url) {
        final AnimatedPieViewConfig config = new AnimatedPieViewConfig();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url + "?SaleOfficerID=" + mLoginResponse.getData().getSOID(), null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("Schools");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                SimplePieInfo simplePieInfo = new SimplePieInfo();
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                SchoolsVisitingClass schoolsVisitingClass = new SchoolsVisitingClass();
                                String name = jsonObject.getString("SaleOfficerName");
                                String visits = jsonObject.getString("VisitCount");
                                simplePieInfo.setValue(Float.valueOf(visits));
                                simplePieInfo.setDesc(name + "=" + visits);
                                simplePieInfo.setColor(randomColor());
                                config.addData(simplePieInfo).drawText(true)
                                        .duration(1200).duration(1200).textSize(25)// Text description size
                                        .textMargin(8)// Margin between text and guide line
                                        .autoSize(true)// Auto fit chart radius
                                        .pieRadius(120)// Set chart radius
                                        .pieRadiusRatio(0.8f)// Chart's radius ratio for parent ViewGroup
                                        .guidePointRadius(2)// Chart's radius
                                        .guideLineWidth(3)// Text guide line stroke width
                                        .guideLineMarginStart(8)
                                        .textGravity(AnimatedPieViewConfig.ALIGN);
                                pieChart.applyConfig(config);
                                pieChart.start();
                            }
// The following two sentences can be replace directly 'mAnimatedPieView.start (config); '
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                    }
                });
        queue.add(jsonObjectRequest);

    }

    public void get_add_visits_new_schools_toay(String url) {
        final AnimatedPieViewConfig config = new AnimatedPieViewConfig();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url + "?SaleOfficerID=" + mLoginResponse.getData().getSOID(), null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("NewSchools");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                SimplePieInfo simplePieInfo = new SimplePieInfo();
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                SchoolsVisitingClass schoolsVisitingClass = new SchoolsVisitingClass();
                                String name = jsonObject.getString("SaleOfficerName");
                                String visits = jsonObject.getString("NewSchoolsCount");
                                simplePieInfo.setValue(Float.valueOf(visits));
                                simplePieInfo.setDesc(name + "=" + visits);
                                simplePieInfo.setColor(randomColor());
                                config.addData(simplePieInfo).drawText(true)
                                        .duration(1200).duration(1200).textSize(25)// Text description size
                                        .textMargin(8)// Margin between text and guide line
                                        .autoSize(true)// Auto fit chart radius
                                        .pieRadius(120)// Set chart radius
                                        .pieRadiusRatio(0.8f)// Chart's radius ratio for parent ViewGroup
                                        .guidePointRadius(2)// Chart's radius
                                        .guideLineWidth(3)// Text guide line stroke width
                                        .guideLineMarginStart(8)
                                        .textGravity(AnimatedPieViewConfig.ALIGN);
                                new_schools_visits_today.applyConfig(config);
                                new_schools_visits_today.start();

                            }
// The following two sentences can be replace directly 'mAnimatedPieView.start (config); '
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                    }
                });
        queue.add(jsonObjectRequest);

    }

    public void get_add_new_schools_today(String url) {
        final AnimatedPieViewConfig config = new AnimatedPieViewConfig();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url + "?SaleOfficerID=" + mLoginResponse.getData().getSOID(), null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("Schools");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                SimplePieInfo simplePieInfo = new SimplePieInfo();
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                SchoolsVisitingClass schoolsVisitingClass = new SchoolsVisitingClass();
                                String name = jsonObject.getString("SaleOfficerName");
                                String visits = jsonObject.getString("VisitCount");
                                simplePieInfo.setValue(Float.valueOf(visits));
                                simplePieInfo.setDesc(name + "=" + visits);
                                simplePieInfo.setColor(randomColor());
                                config.addData(simplePieInfo).drawText(true)
                                        .duration(1200).duration(1200)// Text description size
                                        .textMargin(8)// Margin between text and guide line
                                        .autoSize(true)// Auto fit chart radius
                                        .pieRadius(120)// Set chart radius
                                        .pieRadiusRatio(0.8f)// Chart's radius ratio for parent ViewGroup
                                        .guidePointRadius(2)// Chart's radius
                                        .guideLineWidth(3)// Text guide line stroke width
                                        .guideLineMarginStart(8)
                                        .textSize(25)
                                        .textGravity(AnimatedPieViewConfig.ALIGN);
                                add_new_schools.applyConfig(config);
                                add_new_schools.start();

                            }
// The following two sentences can be replace directly 'mAnimatedPieView.start (config); '
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                    }
                });
        queue.add(jsonObjectRequest);

    }

    private int randomColor() {
        int red = random.nextInt(255);
        int green = random.nextInt(255);
        int blue = random.nextInt(255);
        return Color.argb(255, red, green, blue);
    }


}