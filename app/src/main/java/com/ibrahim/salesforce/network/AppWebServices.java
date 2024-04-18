package com.ibrahim.salesforce.network;


public interface AppWebServices {

    // DEMO
//    String BASE_URL = "https://impliessolutions.raheemsalesforcecrm.com/api/";
//    String BASE_URL = "https://raheemsalesforcecrm.com/API/";
//    String BASE_URL = "https://salesforcecrm.impliessolutions.com/API/";
//    String BASE_URL = "https://albakiomarketcrm.raheemsalesforcecrm.com/API/";
    // Al Bakio
    //String BASE_URL = "https://albakiosalesautomation.com/Api/";
    // Cantab
//    String BASE_URL = "https://cantabsalesforcecrm.com/API/";
    //easy sales
    String BASE_URL = "https://salesautomation.impliesapps.com/API/";

    //gohar sales
    //String BASE_URL = "https://goharsalesforce.com/Api/";
//    String BASE_URL = "https://goharcrm.impliesapps.com/Api/";

    //Generic URL
    String GET_SAMPLES_HISTORY = "GetSamplesHistory";
    String GET_TARGETED_SCHOOL_LIST = "TargatedSchoolList";
    String GET_SUBJECTS_SERIES_WISE = "GetSubjectsSeriesWise";
    String API_ADD_AREA = "AddArea";
    String API_ADD_SUB_AREA = "AddSubArea";
    String API_USER_LOGIN = "login";
    String API_SHOP_REGISTRATION = "ShopRegistration";
    String API_SCHOOL_REGISTRATION = "SchoolRegistration";
    String AP_SCHOOL_EDIT_INFO = "SchoolEditNew";
    String API_VALIDATE_LATLONG = "ValidateLatLong";
    String API_START_ROUTE_DETAILS = "startRouteDetails";
    String API_END_ROUTE_DETAILS = "endRouteDetails";

    String API_DAILY_ACTIVITY = "DailyActivity";
    String API_ASSIGN_TARGATED_PRODUCT = "AssignTargatedProduct";
    String API_GET_ITEMS = "Item";
    String API_GET_RETAILER_SAMPLE = "GetRetailerSampling";
    String API_GET_SUBCAT = "SubCategory";
    String API_GET_SUBCAT_A = "SubCategoryA";
    String API_RESET_LATLONG = "resetLatLong";
    String API_GET_CITIES = "Cities";
    String API_GET_SUB_AREAS = "SubAreas";
    String API_CITIES_RELATED_TO_REGIONAL_HEAD = "ShowAllCitiesToRegionalHead";
    String API_GET_AREAS = "Areas";
    String API_GET_ZONES = "Zones";
    String API_GET_RETAILERS_LOCATIONS = "GetRetailersLocationAreaWise";
    String API_GET_SELLERS = "Shops";
    String API_Visit_Map_View = "VisitDetailMapViewNew";
    String API_Visit_DETAIL = "VisitDetailNew";
    String API_DATE_WISE_TARGET_SCHOOLS = "GetDateWiseTargatedSchools";
    String API_GET_CUSTOMER_RELATED_TO_SO = "GetCustomerRelatedToSO";
    String API_GET_CUSTOMER_RELATED_TO_SUBAREA = "GetCustomerSubAreaWise";
    String API_GET_RETAILERS_AREA_WISE = "GetRetailersAreaWise";
    String API_GET_ACTIVE_REMINDERS = "ActiveReminders";
    String API_GET_PENDING_REMINDERS = "PendingReminders";
    String API_DELETE__REMINDER = "CancelReminders";
    String API_RESCHEDULE_REMINDER = "RescheduleReminders";
    String API_GET_SCHOOL_VISITS_TODAY = "SchoolVisitsToday";
    String API_GET_NEW_SCHOOLS_VISITS = "SchoolVisitsToday";
    String API_GET_NEW_SCHOOLS_TODAY = "NewSchoolsToday";
    String API_Visits_This_Month = "VisitsThisMonth";
    String API_Visits_Last_Month = "VisitsLastMonth";
    String Api_abcent_ = "soabsenttoday";
    String APi_Parent = "SoPresentToday";
    String API_GET_LEAVE = "Leave";
    String API_GET_TODAY_REMINDERS = "TodayReminders";
    String API_GET_NO_VISITS_TODAY = "NoVisitsToday";
    String API_GET_SCHOOL_INFO_FOR_EDIT = "GetSchoolinfoForEdit";
    String API_DAY_IN_DAY_OUT = "DayInDayOut";
    String API_CHANGE_PASSWORD = "PasswordEdit";
    String API_GET_CLASSES_RELATED_TO_SUBJECT = "GetClassRelatedToSubject";
    String API_DELETE_RETAILER = "DeleteRetailer";
    String API_ORDER_FOR_EXCEL = "OnlineAttendanceExpenses";
    String API_GET_SERIES_SUBJECT_WISE = "GetSeriesRelatedtoSubject";
    String API_GET_RETAILER_SESSION = "GetRetailerSessionMonthWise";
    String API_GET_ACTIVITY_PURPOSE_RETAILER_WISE = "GetActivityPurposeRetailerWise";
    String API_GET_DISTRICTS_PROVINCE_WISE = "GetDistrictsProvinceWise";
    String API_GET_TEHSIL_DISTRICT_WISE = "GetTehsilDistrictWise";
    String API_GET_DEALER_CITY_WISE = "GetDealersCityWise";
    String API_GET_ALL_CITIES = "AllCities";
    String API_POST_MULTI_PURPOSE_VISIT = "CombinedVisits";
    String API_GET_DEALER_VISITS = "GetDealerVisitsToday";
    String API_GET_EVENTS = "GetEventsToday";
    String API_GET_COMBINED_VISITS = "GetCombinedVisitsToday";
    String API_AREAS_RELATEDTO_REGIONALHEAD = "ShowAllAreasToRegionalHead";
    String API_GET_ALL_SCHOOLS_BY_CITY = "GetRetailersLocationAreaWise";
    // API for getting info about client and phone number.
    String API_GET_SCHOOL_INFO = "SchoolInfo";
    String API_WEEKLY_VISIT_PLAN = "WeeklyVisitPlan";
    String GET_TARGATED_PRODUCTS = "GetTargatedProducts";
    String API_GET_WEEKLY_VISIT_PLAN = "GetVisitPlans";
    String API_UPDATE_WEEKLY_VISIT_PLAN = "UpdateVisitPlan";
    String API_SAVE_MAPS_ENDS = "GetLiveLoc";
    String API_GET_LOCATIONS_END_POINT = "UserMapPoints";
    String API_GET_LOCATIONS_N_END_POINT = "UserNMapPoints";

    String API_GET_LIVE_LOCATION = "GetLiveLoc";
    String API_GET_USER_ROUTE_POINTS = "UserNMapPoints";
    String API_GET_USER_ENDING_KM_FARE = "UserWiseKM";
    String API_GET_DASHBOARD_ENTRIES = "MainDashboard";
    String API_GET_TODAY_TARGETED_SCHOOLS = "GetTodayTargatedSchools";
    String API_GET_USER_ASSIGNED_SAMPLE = "UserAssignedSample";
    String API_GET_USER_ASSIGNED_SAMPLE_DETAILS = "UserAssignedSampleDetail";
    String GET_SAMPLE_DETAILS = "GetSamplesHistoryDetail";
    String REMOVE_SAMPLE_DEMAND = "RemoveSampleDemand";
    String VISIT_HISTORY = "GetVisitHistory";
    String SUBMITTING_COMMENT = "NoInterestVisit";


//    https://impliessolutions.raheemsalesforcecrm.com/API/SchoolInfo?RetailerID=1
}
