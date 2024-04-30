package com.app.salesforce.model;

import static com.app.salesforce.base.SFApplication.context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.salesforce.R;
import com.app.salesforce.network.AppWebServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GenreDataFactory {

    public static List<Genre> makeGenres(int SOID) {
        return Arrays.asList(
                Percent(SOID),
                Abcent__(SOID),
                makeClassicGenre(SOID),
                AddNewSchoolSVisitToday(SOID)
               ,
                this_month_visits(SOID), makeBluegrassGenre(SOID));
    }


    public static Genre makeClassicGenre(int SOID) {
        return new Genre("Vendor Visits Today ", makeClassicArtists(SOID), R.drawable.ic_banjo);
    }


    public static List<Artist> makeClassicArtists(int SOID) {
        String url = AppWebServices.BASE_URL + AppWebServices.API_GET_NEW_SCHOOLS_VISITS + "?SaleOfficerID=" + SOID;
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        final List<Artist> artists = new ArrayList<>();


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray jsonArray = response.getJSONArray("Schools");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String name = jsonObject.getString("SaleOfficerName");
                                int visits = jsonObject.getInt("VisitCount");
                                Artist schoolsVisitingClass = new Artist(name, true, String.valueOf(visits), "");
                                artists.add(schoolsVisitingClass);


                            }
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
        return artists;

    }


    public static Genre makeBluegrassGenre(int SOID) {
        Object context;
        return new Genre("Visits Last Month", makeBluegrassArtists(SOID), R.drawable.ic_banjo);
    }


    public static List<Artist> makeBluegrassArtists(int SOID) {
        String url = AppWebServices.BASE_URL + AppWebServices.API_Visits_Last_Month + "?SaleOfficerID=" + SOID;
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        final List<Artist> lstArtist = new ArrayList<>();


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray jsonArray = response.getJSONArray("Schools");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                Artist schoolsVisitingClass = new Artist(obj.getString("SaleOfficerName"), true, String.valueOf(obj.getInt("VisitCount")), "");
                                lstArtist.add(schoolsVisitingClass);
                            }
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
        return lstArtist;

    }

    public static Genre AddNewSchoolSVisitToday(int SOID) {
        Object context;
        return new Genre("New Schools Today ", AddSchools_VisitToday(SOID), R.drawable.ic_banjo);
    }


    public static List<Artist> AddSchools_VisitToday(int SOID) {
        String url = AppWebServices.BASE_URL + AppWebServices.API_GET_NEW_SCHOOLS_TODAY + "?SaleOfficerID=" + SOID;
        RequestQueue Queue = Volley.newRequestQueue(context);
        final List<Artist> lstArtists = new ArrayList<>();


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray jsonArray = response.getJSONArray("NewSchools");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                Artist schoolsVisitingClass = new Artist(obj.getString("SaleOfficerName"), true, String.valueOf(obj.getInt("NewSchoolsCount")), "");
                                lstArtists.add(schoolsVisitingClass);
                            }

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
        Queue   .add(jsonObjectRequest);
        return lstArtists;

    }
    public static Genre makeNewSchoolsvistits() {
        Object context;
        return new Genre("NEW School Visits", New_Schools_Visits(), R.drawable.ic_banjo);
    }




    public static List<Artist> New_Schools_Visits() {

        String url = AppWebServices.BASE_URL+ AppWebServices.API_GET_NEW_SCHOOLS_TODAY;
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        final List<Artist> artists = new ArrayList<>();


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray jsonArray = response.getJSONArray("NewSchoolsToday");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String name = jsonObject.getString("SaleofficerName");
                                String visits = jsonObject.getString("Visits");
                                Artist schoolsVisitingClass = new Artist(name, true, visits, "");
                                artists.add(schoolsVisitingClass);


                            }
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
        return artists;

    }

    public static Genre this_month_visits(int SOID) {
        Object context;
        return new Genre("Visits This Month", this_month_visits_(SOID), R.drawable.ic_banjo);
    }


    public static List<Artist> this_month_visits_(int SOID) {
        String url = AppWebServices.BASE_URL + AppWebServices.API_Visits_This_Month + "?SaleOfficerID=" + SOID;
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        final List<Artist> artists = new ArrayList<>();


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("Schools");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                Artist schoolsVisitingClass = new Artist(obj.getString("SaleOfficerName"), true, String.valueOf(obj.getInt("VisitCount")), "");
                                artists.add(schoolsVisitingClass);
                            }
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
        return artists;

    }

    public static Genre Abcent__(int SOID) {
        Object context;
        return new Genre("SO Absent Today ", Abcent_(SOID), R.drawable.ic_banjo);
    }


    public static List<Artist> Abcent_(int SOID) {
        String url = AppWebServices.BASE_URL + AppWebServices.Api_abcent_ + "?SaleOfficerID=" + SOID;
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        final List<Artist> artists = new ArrayList<>();


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("AbsentSO");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String name = jsonObject.getString("SaleOfficerName");
                                Artist schoolsVisitingClass = new Artist(name, true, "", "");
                                artists.add(schoolsVisitingClass);


                            }
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
        return artists;

    }

    public static Genre Percent(int SOID) {
        Object context;
        return new Genre("SO Present Today ", Parent_office_(SOID), R.drawable.ic_banjo);
    }


    public static List<Artist> Parent_office_(int SOID) {
        String url = AppWebServices.BASE_URL + AppWebServices.APi_Parent + "?SaleOfficerID=" + SOID;
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        final List<Artist> artists = new ArrayList<>();


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null,new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("SaleOfficers");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String name = jsonObject.getString("SaleOfficerName");
                                String visits = jsonObject.getString("DayInTime");
                                String areaName = jsonObject.getString("AreaName");
                                Artist schoolsVisitingClass = new Artist(name, true, visits, areaName);
                                artists.add(schoolsVisitingClass);
                            }
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
        return artists;

    }

}



