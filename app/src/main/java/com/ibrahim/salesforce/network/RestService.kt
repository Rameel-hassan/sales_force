package com.ibrahim.salesforce.network

import com.ibrahim.salesforce.BuildConfig
import com.ibrahim.salesforce.base.SFApplication
import com.ibrahim.salesforce.model.GetTodayTargetedSchools
import com.ibrahim.salesforce.request.AssignTargatedProduct.RetailerProducts
import com.ibrahim.salesforce.request.RouteDetail
import com.ibrahim.salesforce.request.SchoolRegRequest
import com.ibrahim.salesforce.request.ShopRegRequest
import com.ibrahim.salesforce.request.VisitReportRequest
import com.ibrahim.salesforce.response.GetAreasResponse
import com.ibrahim.salesforce.response.GetBookSellerResponse
import com.ibrahim.salesforce.response.GetBooksCollectionResponse
import com.ibrahim.salesforce.response.GetCitiesResponse
import com.ibrahim.salesforce.response.GetClassResponse
import com.ibrahim.salesforce.response.GetItemsResponse
import com.ibrahim.salesforce.response.GetLiveLocationResponse
import com.ibrahim.salesforce.response.GetRetailerSampling
import com.ibrahim.salesforce.response.GetSamplesHistoryResponse
import com.ibrahim.salesforce.response.GetSchoolInfo
import com.ibrahim.salesforce.response.GetServerResponse
import com.ibrahim.salesforce.response.GetSubAreasResponse
import com.ibrahim.salesforce.response.GetSubCatResponse
import com.ibrahim.salesforce.response.GetSubCategoryAResponse
import com.ibrahim.salesforce.response.GetVisitDetailsResponse
import com.ibrahim.salesforce.response.GetVisitHistoryResponse
import com.ibrahim.salesforce.response.GetVisitsLocationResponse
import com.ibrahim.salesforce.response.GetZonesResponse
import com.ibrahim.salesforce.response.MapsRouteModel
import com.ibrahim.salesforce.response.ServerResponse
import com.ibrahim.salesforce.response.TargatedProducts
import com.ibrahim.salesforce.response.TargatedSchoolList
import com.ibrahim.salesforce.response.UVisitPlans
import com.ibrahim.salesforce.response.UserCordinates
import com.ibrahim.salesforce.response.VisitPlans
import com.ibrahim.salesforce.utilities.AppKeys
import com.ibrahim.salesforce.utilities.AppPreference
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


interface RestService {

    @POST(AppWebServices.API_USER_LOGIN)
    @FormUrlEncoded
    fun login(
        @Field("UserName") userName: String,
        @Field("Password") password: String
    ): Observable<GetServerResponse>


    @POST(AppWebServices.API_ADD_AREA)
    @FormUrlEncoded
    fun addArea(

        @Field("SOID") soid: String,
        @Field("RegionID") RegionID: Int,
        @Field("CityID") CityID: Int,
        @Field("AreaName") areaName: String

    ):Observable<GetServerResponse>

    @POST(AppWebServices.API_ADD_SUB_AREA)
    @FormUrlEncoded
    fun addSubArea(
        @Field("RegionID") RegionID: Int,
        @Field("CityID") CityID: Int,
        @Field("ZoneID") ZoneID: Int,
        @Field("AreaID") AreaID: Int,
        @Field("SubAreaName") subAreaName: String,
        @Field("SOID") soid: String,

    ):Observable<GetServerResponse>


    @GET(AppWebServices.API_Visit_DETAIL)
    fun getVisitDetails(
        @Query("SOID") soid: String,
        @Query("Date") date: String?
    ): Observable<GetServerResponse>
    @GET(AppWebServices.API_DATE_WISE_TARGET_SCHOOLS)
    fun getDateWiseTargetSchools(
        @Query("SOID") soid: String,
        @Query("selectedDate") date: String?
    ): Observable<GetTodayTargetedSchools>


    @GET(AppWebServices.API_GET_ACTIVE_REMINDERS)
    fun getActiveReminders(@Query("SOID") soid: String): Observable<GetServerResponse>

    @POST(AppWebServices.API_DAY_IN_DAY_OUT)
    @FormUrlEncoded
    fun dayIndayOut(
        @Field("SalesOfficerID") SalesOfficerID: Int,
        @Field("Token") Token: String,
        @Field("DropDownID") DropDownID: Int,
        @Field("Picture1") Picture1: String,
        @Field("RegionID") RegionID: Int,
        @Field("CityID") CityID: Int,
        @Field("AreaID") AreaID: Int,
        @Field("EndingTimeReasonID") EndingTimeReasonID: Int,
        @Field("Remarks") Remarks: String,
       // @Field("Exstation") exStation: String,
        @Field("Millage") Milage: String,
        @Field("Nightstay") nightStay: String,
        @Field("Taxifare") taxiFare: String,
        @Field("Others") others: String,
        @Field("VisitPurpose") visitPurpose: String,
        @Field("DayInMeter") dayInMeter: Int,
        @Field("DayOutMeter") dayOutMeter: Int,
        @Field("Transport") transport: Int,
        @Field("Food") food: Int,
        @Field("FoodImage") foodImage: String,
        @Field("TransportImage") transportImage: String,
        @Field("NightStayImage") nightStayImage: String,
        @Field("OtherImage") otherImage: String

    ): Observable<GetServerResponse>


    @POST(AppWebServices.API_POST_MULTI_PURPOSE_VISIT)
    @FormUrlEncoded
    fun CombinedVisits(
        @Field("RetailerID") RetailerID: Int?,
        @Field("SaleOfficerID") SalesOfficerID: Int,
        @Field("SecondSaleOfficerID") SecSaleOfficerID: Int,
        @Field("ActivityType") activityType: String,
        @Field("MultiVisitPurpose") multiPurposeVisit: String?,
        @Field("Remarks") remakrs: String,
        @Field("NoOfSchools") noOfSchoools: Int,
        @Field("Dealers") Dealers: MutableList<Int>?

    ): Observable<GetServerResponse>

    @GET(AppWebServices.API_GET_PENDING_REMINDERS)
    fun getPendingReminders(@Query("SOID") soid: String): Observable<GetServerResponse>

    @GET(AppWebServices.API_Visit_Map_View)
    fun getVisitsLocation(
        @Query("SOID") soid: String,
        @Query("Date") date: String?
    ): Observable<GetServerResponse>
    @GET(AppWebServices.API_GET_USER_ROUTE_POINTS)
    fun getRouteDetails(
        @Query("UserID") soid: String,
        @Query("Date") date: String?
    ): Observable<GetServerResponse>

    @GET(AppWebServices.API_GET_USER_ENDING_KM_FARE)
    fun getUserEndingKmAndFare(
        @Query("UserID") soid: Int
    ): Observable<GetServerResponse>

    @GET(AppWebServices.API_GET_CITIES)
    fun getCities(
        @Query("RegionID") regionId: String,
        @Query("SoID") SoID: Int
    ): Observable<GetServerResponse>

    @GET(AppWebServices.API_CITIES_RELATED_TO_REGIONAL_HEAD)
    fun getCitiesRelatedToRegionalHeaf(
        @Query("RegionID") regionId: String,
        @Query("SoID") SoID: Int,
        @Query("IsRegionalHead") isRegionalHead: Boolean
    ): Observable<GetServerResponse>


    @GET(AppWebServices.API_GET_AREAS)
    fun getAreas(
        @Query("CityID") cityId: String,
        @Query("SOID") soID: Int
    ): Observable<GetServerResponse>


    @GET(AppWebServices.API_AREAS_RELATEDTO_REGIONALHEAD)
    fun getAreasRelatedToRegionalHeas(
        @Query("CityID") cityId: String,
        @Query("SOID") soID: Int,
        @Query("IsRegionalHead") isRegionalHead: Boolean
    ): Observable<GetServerResponse>


    @GET(AppWebServices.API_GET_RETAILER_SESSION)
    fun getRetailerSession(
        @Query("SoID") soID: Int,
        @Query("SessionMonth") sessionMonth: String
    ): Observable<GetServerResponse>

    @POST(AppWebServices.API_DELETE__REMINDER)
    @FormUrlEncoded
    fun deleteReminder(
        @Field("ReminderID") ReminderID: Int,
        @Field("Remarks") Remarks: String,
        @Field("Token") Token: String
    ): Observable<GetServerResponse>

    @POST(AppWebServices.API_RESCHEDULE_REMINDER)
    @FormUrlEncoded
    fun rescheduleReminder(
        @Field("ReminderID") ReminderID: Int,
        @Field("ReminderDate") ReminderDate: String,
        @Field("Token") Token: String
    ): Observable<GetServerResponse>

    @POST(AppWebServices.API_CHANGE_PASSWORD)
    @FormUrlEncoded
    fun changePassword(
        @Field("SOID") SOID: Int,
        @Field("Token") Token: String,
        @Field("Password") Password: String
    ): Observable<GetServerResponse>

    @GET(AppWebServices.APi_Parent)
    fun getPresentSO(@Query("SaleOfficerID") soID: Int): Observable<GetServerResponse>

    @GET(AppWebServices.Api_abcent_)
    fun getAbsentSO(@Query("SaleOfficerID") soID: Int): Observable<GetServerResponse>

    @GET(AppWebServices.API_GET_NEW_SCHOOLS_VISITS)
    fun getSchoolVisitsToday(@Query("SaleOfficerID") soID: Int): Observable<GetServerResponse>

    @GET(AppWebServices.API_GET_NEW_SCHOOLS_TODAY)
    fun getNewSchoolsToday(@Query("SaleOfficerID") soID: Int): Observable<GetServerResponse>

    @GET(AppWebServices.API_GET_CUSTOMER_RELATED_TO_SO)
    fun getSchools(
        @Query("CityID") cityID: Int,
        @Query("AreaID") areaID: Int
    ): Observable<GetServerResponse>

   /* @GET(AppWebServices.API_GET_TODAY_REMINDERS)
    fun getTodayReminders(
        @Query("SOID") soid: Int,
        @Query("date") date: Int
    ): Observable<GetServerResponse>*/

    @GET(AppWebServices.API_Visits_This_Month)
    fun getVisitsThisMonth(@Query("SaleOfficerID") soID: Int): Observable<GetServerResponse>

    @GET(AppWebServices.API_Visits_Last_Month)
    fun getVisitsLastMonth(@Query("SaleOfficerID") soID: Int): Observable<GetServerResponse>

    @GET(AppWebServices.API_GET_LEAVE)
    fun getLeave(@Query("SaleOfficerID") soID: Int): Observable<GetServerResponse>

    @GET(AppWebServices.API_GET_NO_VISITS_TODAY)
    fun getNoVisitsToday(@Query("SaleOfficerID") soID: Int): Observable<GetServerResponse>

    @GET(AppWebServices.API_GET_DEALER_VISITS)
    fun getDealerVisits(@Query("SaleOfficerID") soID: Int): Observable<GetServerResponse>

    @GET(AppWebServices.API_GET_EVENTS)
    fun getEvents(@Query("SaleOfficerID") soID: Int): Observable<GetServerResponse>

    @GET(AppWebServices.API_GET_COMBINED_VISITS)
    fun getCombinedVisits(@Query("SaleOfficerID") soID: Int): Observable<GetServerResponse>

    @POST(AppWebServices.API_WEEKLY_VISIT_PLAN)
    fun regWeeklyVisitPlan(@Body visitPlan: VisitPlans): Observable<GetServerResponse>

    @GET(AppWebServices.API_GET_WEEKLY_VISIT_PLAN)
    fun getVisitPlans(
        @Query("SOID") SOID: Int,
        @Query("fromDate") fromDate: String,
        @Query("toDate") toDate: String
    ): Observable<GetServerResponse>

    @POST(AppWebServices.API_UPDATE_WEEKLY_VISIT_PLAN)
    fun updateWeeklyVisitPlan(@Body uVisitPlans: UVisitPlans): Observable<GetServerResponse>


    companion object {
        fun create(): RestService {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            val httpClient = OkHttpClient.Builder()
            if (BuildConfig.DEBUG)
                httpClient.addInterceptor(logging)
            val baseURL: String
            if (AppPreference.getValue(SFApplication.getAppContext(), AppKeys.KEY_BASE_URL) == null)
                baseURL = AppWebServices.BASE_URL
            else
                baseURL =
                    AppPreference.getValue(SFApplication.getAppContext(), AppKeys.KEY_BASE_URL)

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseURL)
                .client(httpClient.build())
                .build()

            return retrofit.create(RestService::class.java)
        }
    }
}