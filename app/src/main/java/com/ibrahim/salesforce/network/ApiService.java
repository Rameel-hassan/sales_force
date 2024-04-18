package com.ibrahim.salesforce.network;


import com.ibrahim.salesforce.model.GetTodayTargetedSchools;
import com.ibrahim.salesforce.request.AssignTargatedProduct;
import com.ibrahim.salesforce.request.RouteDetail;
import com.ibrahim.salesforce.request.SchoolRegRequest;
import com.ibrahim.salesforce.request.ShopRegRequest;
import com.ibrahim.salesforce.request.VisitReportRequest;
import com.ibrahim.salesforce.response.GetAreasResponse;
import com.ibrahim.salesforce.response.GetBookSellerResponse;
import com.ibrahim.salesforce.response.GetBooksCollectionResponse;
import com.ibrahim.salesforce.response.GetCitiesResponse;
import com.ibrahim.salesforce.response.GetClassResponse;
import com.ibrahim.salesforce.response.GetItemsResponse;
import com.ibrahim.salesforce.response.GetLiveLocationResponse;
import com.ibrahim.salesforce.response.GetRetailerSampling;
import com.ibrahim.salesforce.response.GetSamplesHistoryResponse;
import com.ibrahim.salesforce.response.GetSchoolInfo;
import com.ibrahim.salesforce.response.GetServerResponse;
import com.ibrahim.salesforce.response.GetSubAreasResponse;
import com.ibrahim.salesforce.response.GetSubCatResponse;
import com.ibrahim.salesforce.response.GetSubCategoryAResponse;
import com.ibrahim.salesforce.response.GetVisitDetailsResponse;
import com.ibrahim.salesforce.response.GetVisitHistoryResponse;
import com.ibrahim.salesforce.response.GetVisitsLocationResponse;
import com.ibrahim.salesforce.response.GetZonesResponse;
import com.ibrahim.salesforce.response.MapsRouteModel;
import com.ibrahim.salesforce.response.ServerResponse;
import com.ibrahim.salesforce.response.TargatedProducts;
import com.ibrahim.salesforce.response.TargatedSchoolList;
import com.ibrahim.salesforce.response.UserCordinates;
import com.ibrahim.salesforce.response.VisitPlans;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    // (1)
    @FormUrlEncoded
    @POST(AppWebServices.API_USER_LOGIN)
    Call<GetServerResponse> login(@Field("UserName") String userName,
                                  @Field("Password") String password);


    @GET(AppWebServices.GET_SUBJECTS_SERIES_WISE)
    Call<GetServerResponse> getSubjectsSeriesWise(
            @Query("SeriesID") int seriesID
            );

    @GET(AppWebServices.API_GET_TODAY_REMINDERS)
    Call<GetServerResponse> getTodayReminders(
            @Query("SOID") int soid,
            @Query("date") String date
    );
    // (2)
    @POST(AppWebServices.API_SCHOOL_REGISTRATION)
    Call<ServerResponse> regSchool(@Body SchoolRegRequest obj);

    // (3)
    @POST(AppWebServices.API_SHOP_REGISTRATION)
    Call<ServerResponse> regShop(@Body ShopRegRequest obj);

    @POST(AppWebServices.AP_SCHOOL_EDIT_INFO)
    Call<ServerResponse> GetSchoolSchoolEditInfo(@Body SchoolRegRequest obj);

    // (4)
    @FormUrlEncoded
    @POST(AppWebServices.API_VALIDATE_LATLONG)
    Call<ServerResponse> validateLatLong(@Field("RetailerId") int retailerId,
                                         @Field("Latitude") double latitude,
                                         @Field("Longitude") double longitude);

    @POST(AppWebServices.API_START_ROUTE_DETAILS)
    Call<ServerResponse> saveStartingLatLng(@Body RouteDetail obj);

    @POST(AppWebServices.API_END_ROUTE_DETAILS)
    Call<ServerResponse> saveEndingLatLng(@Body RouteDetail obj);


    // (5)
    @POST(AppWebServices.API_DAILY_ACTIVITY)
    Call<ServerResponse> regActivity(@Body VisitReportRequest obj);

    @POST(AppWebServices.API_ASSIGN_TARGATED_PRODUCT)
    Call<ServerResponse> assignTargatedProducts(@Body
                                                List<AssignTargatedProduct.RetailerProducts> obj);

    // (6)
    @GET(AppWebServices.API_GET_ITEMS)
    Call<GetItemsResponse> getItems(@Query("MainCatID") String mainCatID);

    // (7)
    @GET(AppWebServices.API_GET_SUBCAT)
    Call<GetSubCatResponse> getSubCat(@Query("MainCatID") String mainCatID);

    // (8)
    @FormUrlEncoded
    @POST(AppWebServices.API_RESET_LATLONG)
    Call<ServerResponse> resetLatLong(@Field("RetailerId") int retailerId,
                                      @Field("Latitude") double latitude,
                                      @Field("Longitude") double longitude);

    // (9)
    @GET(AppWebServices.API_GET_CITIES)
    Call<GetCitiesResponse> getCities(@Query("RegionID") int regionId, @Query("SoID") int soID);

    @GET(AppWebServices.API_GET_SUB_AREAS)
    Call<GetSubAreasResponse> geSubAreas(@Query("AreaID") int areaID);
    // (10)
    @GET(AppWebServices.API_GET_AREAS)
    Call<GetAreasResponse> getAreas(@Query("CityID") String cityId, @Query("SOID") int soID);

    @GET(AppWebServices.API_GET_AREAS)
    Call<GetAreasResponse> getAreas(@Query("CityID") String cityId, @Query("SOID") int soID,@Query("ZoneID") int zoneID);

    @GET(AppWebServices.API_GET_ZONES)
    Call<GetZonesResponse> getZones(@Query("CityID") String cityId,@Query("SOID") int SOID);

    // (11)
    @GET(AppWebServices.API_GET_SUBCAT_A)
    Call<GetSubCategoryAResponse> getSubCatA(@Query("MainCatID") String mainCatID, @Query("SubCatID") String subCatId);

    // (12)
    @GET(AppWebServices.API_Visit_Map_View)
    Call<GetVisitsLocationResponse> getVisitsLocation(@Query("SOID") String soid);

    // (13)
    @GET(AppWebServices.API_Visit_DETAIL)
    Call<GetVisitDetailsResponse> getVisitDetails(@Query("SOID") String soid);

    // (14)
    @GET(AppWebServices.API_GET_CUSTOMER_RELATED_TO_SO)
    Call<GetServerResponse> getCustomerRelatedToSo(@Query("CityID") int cityID, @Query("AreaID") int areaID,@Query("VendorType") String s);

    @GET(AppWebServices.API_GET_CUSTOMER_RELATED_TO_SUBAREA)
    Call<GetServerResponse> getCustomerSubAreaWise( @Query("SubAreaID") int subAreaID,@Query("VendorType") String s);

    @GET(AppWebServices.GET_SAMPLES_HISTORY)
    Call<GetSamplesHistoryResponse> getSamplesHistory(@Query("DateTo") String dateTo, @Query("DateFrom") String dateFrom,
                                                      @Query("SOID") int SOID, @Query("CityID") int cityID,
                                                      @Query("AreaID") int areaID/*, @Query("RetailerID") int retailerID*/);

    @GET(AppWebServices.GET_TARGETED_SCHOOL_LIST)
    Call<TargatedSchoolList> getTargetedSchoolList(@Query("AreaID") int AreaID, @Query("PLevel") String PLevel );

    // (15)
    @GET(AppWebServices.GET_SAMPLE_DETAILS)
    Call<GetSamplesHistoryResponse> getSampleDetails(@Query("JobID") int jobID);

    @GET(AppWebServices.REMOVE_SAMPLE_DEMAND)
    Call<GetSamplesHistoryResponse> removeSampleDemand(@Query("JobItemID") int removeSampleDemand);


    @GET(AppWebServices.VISIT_HISTORY)
    Call<GetVisitHistoryResponse> getVisitHistoryResponse(@Query("DateTo") String dateTo, @Query("DateFrom") String dateFrom,
                                                          @Query("SOID") int SOID);
    @FormUrlEncoded
    @POST(AppWebServices.SUBMITTING_COMMENT)
    Call<ServerResponse> submitComment(  @Field("SOID") int SOID, @Field("RetailerID") int retailerID,
                                                            @Field("Type") String type, @Field("AppropriateMonth") String appropriateMonth
                                                            ,@Field("Comment") String comment);


    // (15)
    @GET(AppWebServices.API_GET_RETAILERS_AREA_WISE)
    Call<GetServerResponse> getCustomersAreaWise(@Query("SoID") int soID, @Query("CityID") int cityID);

    //(16)
    @GET(AppWebServices.API_GET_SCHOOL_INFO_FOR_EDIT)
    Call<GetServerResponse> GetSchoolinfoForEdit(@Query("ID") String regionID);

    //17
    @GET(AppWebServices.API_GET_SELLERS)
    Call<GetBookSellerResponse> getSellers(@Query("CityID") String cityId, @Query("AreaID") String areaId);

    //18
    @GET(AppWebServices.API_GET_RETAILER_SAMPLE)
    Call<GetRetailerSampling> GetSamplingItems(@Query("RetailerID") int retailerID);

    //19
    @GET(AppWebServices.API_GET_CLASSES_RELATED_TO_SUBJECT)
    Call<GetClassResponse> getClassesForSubjects(@Query("SubjectID") int subjectID, @Query("SeriesID") int seriesID);

    //20
    //POST with the query usually dont work, but here it is working
    @POST(AppWebServices.API_DELETE_RETAILER)
    Call<ServerResponse> deleteRetailer(@Query("RetailerID") int retailerId);

    //22
    @FormUrlEncoded
    @POST(AppWebServices.API_ORDER_FOR_EXCEL)
    Call<GetServerResponse> getOrderExcel(
            @Field("DateFrom") String DateFrom,
            @Field("DateTo") String DateTo,
            @Field("SaleOfficerID") int SaleOfficerID);

    //23
    @GET(AppWebServices.API_GET_SERIES_SUBJECT_WISE)
    Call<GetServerResponse> getSeriesForSubject(@Query("SubjectID") int subjectID);

    //24
    @GET(AppWebServices.API_GET_ACTIVITY_PURPOSE_RETAILER_WISE)
    Call<GetServerResponse> getActivityPurpose(@Query("RetailerID") int retailerID);

    //25
    @GET(AppWebServices.API_GET_DISTRICTS_PROVINCE_WISE)
    Call<GetServerResponse> getDistricts(@Query("ProvinceID") int provinceID);

    //26
    @GET(AppWebServices.API_GET_TEHSIL_DISTRICT_WISE)
    Call<GetServerResponse> getTehsils(@Query("DistrictID") int districtID);

    //27
    @GET(AppWebServices.API_GET_DEALER_CITY_WISE)
    Call<GetServerResponse> getDealers(@Query("CityID") int cityID);

    //28
    @GET(AppWebServices.API_GET_ALL_CITIES)
    Call<GetCitiesResponse> getAllCities(@Query("RegionID") int regionID);

    //29
    @GET(AppWebServices.API_CITIES_RELATED_TO_REGIONAL_HEAD)
    Call<GetCitiesResponse> getRegionalHeadCities(@Query("RegionID") int regionId, @Query("SoID") int soID, @Query("IsRegionalHead") Boolean isRegionalHead);

    //30
    @GET(AppWebServices.API_AREAS_RELATEDTO_REGIONALHEAD)
    Call<GetAreasResponse> getRegionalHeadAreas(@Query("CityID") int cityID, @Query("SoID") int soID, @Query("IsRegionalHead") Boolean isRegionalHead);

    //31
    @GET(AppWebServices.API_GET_RETAILERS_LOCATIONS)
    Call<GetServerResponse> getRetailerSchoolsLocation(@Query("SoID") int SoID, @Query("AreaID") int AreaID);

    //32
    @GET(AppWebServices.API_GET_SCHOOL_INFO)
    Call<GetSchoolInfo> getSchoolInfo(@Query("RetailerID") int retailerID);

    // (33)
    @POST(AppWebServices.API_WEEKLY_VISIT_PLAN)
    Call<GetServerResponse> regWeeklyVisitPlan(@Body VisitPlans obj);



    @POST(AppWebServices.API_SAVE_MAPS_ENDS)
    Call<GetServerResponse> saveUserMapsEndPoints(@Body MapsRouteModel obj);

//    @GET(AppWebServices.API_GET_LOCATIONS_END_POINT)
//    Call<UserCordinates> getUserAllPointCoOridnates(@Query("UserID") int UserID,
//                                                    @Query("date") String date);

        @GET(AppWebServices.API_GET_LOCATIONS_N_END_POINT)
    Call<UserCordinates> getUserEndCoOrdinates(@Query("UserID") int UserID,
                                                    @Query("date") String date);
    //34
    @Headers({"Content-Type: application/json"})
    @POST(AppWebServices.API_GET_LIVE_LOCATION)
    Call<GetLiveLocationResponse> saveLocation(@Body LocationDetails locationDetails);

    //35
    @Headers({"Content-Type: application/json"})
    @GET(AppWebServices.API_GET_DASHBOARD_ENTRIES)
    Call<GetServerResponse> getDashBoardData(@Query("SoID") int SoID);

    @GET(AppWebServices.API_GET_TODAY_TARGETED_SCHOOLS)
    Call<GetTodayTargetedSchools> getTodayTargetedSchools(@Query("SOID") int SoID);

    @GET(AppWebServices.API_GET_TODAY_TARGETED_SCHOOLS)
    Call<GetTodayTargetedSchools> getTodayTargetedSchools(@Query("SOID") int SoID,@Query("selectedDate") String date);
    //36
    @Headers({"Content-Type: application/json"})
    @GET(AppWebServices.API_GET_USER_ASSIGNED_SAMPLE)
    Call<GetBooksCollectionResponse> getUserAssignedSample(@Query("UserID") int UserID);

    //37
    @Headers({"Content-Type: application/json"})
    @GET(AppWebServices.API_GET_USER_ASSIGNED_SAMPLE_DETAILS)
    Call<GetBooksCollectionResponse> getUserAssignedSampleDetail(@Query("UserID") int UserID,@Query("SubjectID") int SubjectID);


    @GET(AppWebServices.GET_TARGATED_PRODUCTS)
    Call<TargatedProducts> getTargetedProducts(@Query("RetailerID") int id);
}
