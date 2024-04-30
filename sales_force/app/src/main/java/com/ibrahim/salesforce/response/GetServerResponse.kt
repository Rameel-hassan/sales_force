package com.app.salesforce.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class GetServerResponse(
    @SerializedName("Data") var Data: Data,
    @SerializedName("Exception") val Exception: Any,
    @SerializedName("Message") val Message: String,
    @SerializedName("ResultType") val ResultType: Int,
    @SerializedName("ValidationErrors") val ValidationErrors: Any,
    @SerializedName("Customers") var CustomersRelatedtoSO: List<CustomersRelatedtoSO>? = null,
    @SerializedName("Subjects") var SubjectsRelatedToSeries: List<Subjects>?=null,
    @SerializedName("SLocations")val sLocations : List<SLocations>?=null,
    @SerializedName("GetSchoolInfo")val getSchoolInfo: List<GetSchoolInfo>?=null,

    @SerializedName("VisitPlans") val GVisitPlans: ArrayList<GVisitPlans>,
    @SerializedName("NextVisitPlanData") val NextVisitPlanData: ArrayList<NextVisitPlanData>? = null,
    @SerializedName("TodayActiveReminders") val todayReminders:ArrayList<TodayActiveReminders>?=null,
//        @SerializedName("Customers") var Customers: ArrayList<Customers>? = null,
    @SerializedName("Cities") val Cities: MutableList<IdName>,
    @SerializedName("Areas") val Areas: MutableList<IdName>,
    @SerializedName("MyVisits") val MyVisits: List<MyVisits>,
    @SerializedName("ActiveReminders") val ActiveReminders: MutableList<Reminder>,
    @SerializedName("PendingReminders") val PendingReminders: MutableList<Reminder>,
    @SerializedName("MyVisitsMapView") val MyVisitsMapView: List<MyVisitsMapView>,
    @SerializedName("SchoolsForEdit") val SchoolsForEdit: List<FormData>,
    @SerializedName("Series") val Series: List<Sery>,
    @SerializedName("Schools") val RetailerSessionWise: List<School>,
    @SerializedName("ActivityPurpose") val PurposeOfActivity: List<PurposeofActivity>,
    @SerializedName("Districts") val Districts: List<IdName>,
    @SerializedName("Tehsil") val Tehsils: List<IdName>,
    @SerializedName("Shops") val SylabusID: MutableList<BookSellerID>,
    @SerializedName("Competitors") val PublisherID: MutableList<BookPublisherID>,
    @SerializedName("NewSchools") val NewSchoolsToday: MutableList<Visits>,
    @SerializedName("AllSchools") val Schools: List<Visits>, // A generic list for accessing all the data related o visits and school
    @SerializedName("AbsentSO") val AbsentSo: List<Visits>,
    @SerializedName("SaleOfficers") val PresentSo: List<Visits>,
    @SerializedName("NoVisit") val NoVisitsToday: List<Visits>,
    @SerializedName("Dealers") val dealers: List<CustomersRelatedtoSO>? = null,
    @SerializedName("LocPoints") val locPoints: RouteDetails,
    @SerializedName("MainDashboard") val mainDashboard: MainDashboardEntry



    /*  @SerializedName("DealerVisits") val dealerVisits: List<Visits>,
      @SerializedName("Events") val events: List<Visits>,
      @SerializedName("CombinedVisits") val combinedVisits: List<Visits>*/
)

data class Data(
    @SerializedName("ActivityPurpose") val ActivityPurpose: List<ActivityPurpose>,
    val Cities: Any,
    @SerializedName("CurrentSyllabus") val CurrentSyllabus: List<CurrentSyllabus>,
    @SerializedName("CustomersRelatedtoSO") var CustomersRelatedtoSO: List<CustomersRelatedtoSO>,
    @SerializedName("Fee") val Fee: List<Fee>,
    @SerializedName("MainCatg") val MainCatg: List<MainCatg>,
    @SerializedName("Name") var Name: String,
    @SerializedName("Password") var Password: String,
    @SerializedName("Region") val Region: List<Region>,
    @SerializedName("IsRegionalHead") val IsRegionalHead: Boolean,
    @SerializedName("IsRM") val IsRm: Boolean,
    @SerializedName("RegionalHeadID") val RegionalHeadID: Int,
    @SerializedName("TotalLeave") val TotalLeave: Int,
    @SerializedName("AchievedLeave") val AchievedLeave: Int,
    @SerializedName("RoleID") val RoleID: Int,
    @SerializedName("TodayReminders") val todayReminders:ArrayList<TodayActiveReminders>?=null,
    @SerializedName("IsAssignSampleMenu") val IsAssignSampleMenu: Boolean,
    @SerializedName("IsShopNameInVisitForm") val IsShopNameInVisitForm: Boolean,
    @SerializedName("SOID") val SOID: Int,
    @SerializedName("AdminCompanyID") val AdminCompanyID: Int,
    @SerializedName("IsMarked") var IsMarked: Boolean,
    @SerializedName("IsCheckOut") var IsCheckOut: Boolean,
    @SerializedName("IsRouteStarted") var IsRouteStarted: Boolean,


    @SerializedName("ProvinceID") val ProvinceID: Int,
    @SerializedName("SalesOfficer") var SalesOfficer: List<SalesOfficer>,
    @SerializedName("DayinOutDropdown") val DayinOutDropdown: MutableList<DayinOutDropdown>,
    @SerializedName("Series") val Series: List<Sery>,
    @SerializedName("Subject") val Subject: List<Subject>,
    @SerializedName("Class") val Classs: List<Clas>,
    @SerializedName("Competitor") val Competitor: List<Competitor>,
    @SerializedName("EndingReasons") val EndingReasons: List<IdName>,
    @SerializedName("PurposeOfVisit") val VisitPurposes: MutableList<IdName>?,
    @SerializedName("data") val downloadPdfUrl: String,
    @SerializedName("MultiPurposeVisit") val MultiPurposeVisit: List<ActivityPurpose>,
    @SerializedName("CombinedVisits") val CombinedVisits: List<ActivityPurpose>,
    @SerializedName("Events") val Events: List<ActivityPurpose>,
    @SerializedName("IsZoneFormat") val isZoneFormat: Boolean,
    val Token: String


)

data class Fee(@SerializedName("FeeStructID") val FeeStructID: Int, @SerializedName("FeeStructName") val FeeStructName: String) {
    override fun toString(): String {
        return FeeStructName
    }
}

data class Visits(
        @SerializedName("SaleOfficerName") val soName: String,
        @SerializedName("VisitCount") val noOfVisits: Int,
        @SerializedName("AreaName") val area: String,
        @SerializedName("DayInTime") val dateTime: String,
        @SerializedName("NewSchoolsCount") val newSchoolsCount: Int,
        @SerializedName("Reason") val reason: String,
        @SerializedName("OfflineVisits") val offlineVisits: Int,
        @SerializedName("OnlineVisits") val onlineVisits: Int,
        @SerializedName("School") val schoolName: String,
        @SerializedName("VisitPurpose") val visitPurpose: String,
        @SerializedName("SecondSaleOfficerName") val secondSO: String,
        @SerializedName("NoOfSchoolsVisited") val noOfSchoolsVisited: Int
)
data class RouteDetails(
    @SerializedName("LocPointsData") val locPointsData: List<LocPointsData>,
    @SerializedName("TotalKM") val totalKM: Double,
    @SerializedName("TotalFare") val totalFare: Double,
    @SerializedName("DayInMeter") val DayInMeter: Double
)
data class MainDashboardEntry(
    @SerializedName("targetValue") val targetValue: Int,
    @SerializedName("achivedValue") val achivedValue: Int,
    @SerializedName("totalVisits") val totalVisits: Int,
    @SerializedName("totalSampleDemand") val totalSampleDemand: Int,
    @SerializedName("totalSampleDeliver") val totalSampleDeliver: Int,
    @SerializedName("totalSampleReturn") val totalSampleReturn: Int,
    @SerializedName("totalSelectedBooks") val totalSelectedBooks: Int,
    )
data class LocPointsData(
    @SerializedName("StartLat") val startLat: Double,
    @SerializedName("StartLong") val startLong: Double,
    @SerializedName("EndLat") val endLat: Double,
    @SerializedName("EndLong") val endLong: Double,
    @SerializedName("OnCreated") val onCreated: String
)

data class Region(
        @SerializedName("ID") val ID: Int,
        @SerializedName("Name") val Name: String
) {
    override fun toString(): String {
        return Name
    }
}

data class DealerID(@SerializedName("DealerID") val ID: Int)

data class MainCatg(
        @SerializedName("MainCategDesc") val MainCategDesc: String,
        @SerializedName("MainCategID") val MainCategID: Int
) {
    override fun toString(): String {
        return MainCategDesc
    }
}

data class BookSellerID(@SerializedName("ShopID") val ID: Int)

data class BookPublisherID(@SerializedName("SylabusID") val ID: Int)
data class DayinOutDropdown(
        @SerializedName("ID") val ID: Int,
        @SerializedName("Value") val Value: String
) {
    override fun toString(): String {
        return Value
    }
}

data class IdName @JvmOverloads constructor(
        @SerializedName("ID") val ID: Int,
        @SerializedName("Name") val Name: String,
        @SerializedName("ParentID") var parentId: Int = 0
) {
    override fun toString(): String {
        return Name
    }
}

data class SalesOfficer(
        @SerializedName("ID") val ID: Int,
        @SerializedName("Name") val Name: String
) {
    override fun toString(): String {
        return Name
    }
}

data class CurrentSyllabus(
        @SerializedName("SylabusID") var SylabusID: Int,
        @SerializedName("SylabusName") var SylabusName: String,
        var ISChecked: Boolean) {
    override fun toString(): String {
        return SylabusName
    }
}

data class ActivityPurpose(
        @SerializedName("ID") val ID: Int,
        @SerializedName("Name") val Name: String
) {
    override fun toString(): String {
        return Name
    }
}

data class CustomersRelatedtoSO @JvmOverloads constructor(
        @SerializedName("ID") val ID: Int,
        @SerializedName("ISActive") val ISActive: Boolean,
        @SerializedName("ShopName") val ShopName: String,
        @SerializedName("AreaID") var AreaID: Int = 0,
        @SerializedName("CityID") var CityID: Int = 0
) : Serializable {
    override fun toString(): String {
        return ShopName
    }
}


data class PurposeofActivity(
        @SerializedName("purposeid") val ID: Int,
        @SerializedName("purposename") val Name: String
) {
    override fun toString(): String {
        return Name
    }
}

data class Customer(
        @SerializedName("ID") val ID: Int,
        @SerializedName("ShopName") val ShopName: String,
        @SerializedName("ISActive") val ISActive: Boolean
) : Serializable {
    override fun toString(): String {
        return ShopName
    }
}
data class BooksSampleInfo(
    @SerializedName("SubjectID") val SubjectID: Int,
    @SerializedName("SubjectName") val SubjectName: String,
    @SerializedName("ClassName") val ClassName: String,
    @SerializedName("TotalQnt") val TotalQnt: Int,
    @SerializedName("DeliveredQnt") val DeliveredQnt: Int,
    @SerializedName("RemainingQnt") val RemainingQnt: Int
) : Serializable {
    override fun toString(): String {
        return SubjectName
    }
}

data class MyVisits(
        @SerializedName("VisitDate") val VisitDate: String,
        @SerializedName("School") val CustomerName: String,
        @SerializedName("VisitType") val VisitType: String
)


data class MyVisitsMapView(
        @SerializedName("VisitDate") val VisitDate: String,
        @SerializedName("Lattitude") val Latitude: Double,
        @SerializedName("Longitude") val Longitude: Double,
        @SerializedName("School") val School: String,
        @SerializedName("AreaName") val Area: String
)

data class Reminder(
        @SerializedName("ReminderID") val ReminderID: Int,
        @SerializedName("SchoolName") val SchoolName: String,
        @SerializedName("ReminderDate") @Expose val ReminderDate: String)

data class Customers(
        @SerializedName("ID") val ID: Int,
        @SerializedName("ShopName") val ShopName: String,
        @SerializedName("ISActive") val ISActive: Boolean
) : Serializable {
    override fun toString(): String {
        return ShopName
    }
}

data class FormData(
    @SerializedName("ExamBoard") val ExamBoard: String,
    @SerializedName("Website") val Website:String?,
        @SerializedName("ShopName") val ShopName: String?,
        @SerializedName("Name") val Name: String,
        @SerializedName("Phone1") val Phone1: String,
        @SerializedName("Phone2") val Phone2: String?,
        @SerializedName("Email") val Email: String?,
        @SerializedName("Address") val Address: String?,
        @SerializedName("ContactPerson") val ContactPerson: String,
        @SerializedName("ContactPersonCellNo") val ContactPersonCellNo: String,
        @SerializedName("StudentStrength") val StudentStrength: Int,
        @SerializedName("NoOfBranches") val NoOfBranches: Int,
        @SerializedName("NoOfTeachers") val NoOfTeachers: Int,
        @SerializedName("CityID") val CityID: Int,
        @SerializedName("CityName") val CityName: String,
        @SerializedName("AreaID") val AreaID: Int,
        @SerializedName("AreaName") val AreaName: String,
        @SerializedName("NewSessionStartDate") val SessionStartMonth: String,
        @SerializedName("SampleMonth") val SyllSectionMonth: String,
        @SerializedName("FeeStructID") val FeeStructID: Int,
        @SerializedName("EducationSystem") val EduSystem: String,
        @SerializedName("Tehsil") val Tehsil: String,
        @SerializedName("TehsilID") val TehsilID: Int,
        @SerializedName("District") val District: String,
        @SerializedName("DistrictID") val DistrictID: Int

)

data class Sery(
        @SerializedName("ID") val ID: Int,
        @SerializedName("SeriesName") val SeriesName: String

) {
    override fun toString(): String {
        return SeriesName
    }
}

data class Subject(
        @SerializedName("ID") val ID: Int,
        @SerializedName("SubjectName") val SubjectName: String
) {
    override fun toString(): String {
        return SubjectName
    }
}
data class Subjects(
    @SerializedName("ID") val ID: Int,
    @SerializedName("SubjectName") val SubjectName: String,
    @SerializedName("SubjectCode") val SubjectCode: String,
    @SerializedName("Classes") val Classes: List<Clas>
) {
    override fun toString(): String {
        return SubjectName
    }
}
data class School(
        @SerializedName("ShopName") val SchoolName: String,
        @SerializedName("CityName") val CityName: String,
        @SerializedName("AreaName") val AreaName: String,
        @SerializedName("FV") val FirstVisit: Int,
        @SerializedName("SAM") val Sample: Int,
        @SerializedName("ASAM") val AdditionalSample: Int,
        @SerializedName("FU") val FollowUp: Int,
        @SerializedName("FinV") val FinalVisit: Int

) {
    override fun toString(): String {
        return SchoolName
    }
}

data class Clas(
        @SerializedName("ClassName") var ClassName: String,
        @SerializedName("ID") var ID: Int=-1,
        @SerializedName("ClassID") var ClassID: Int=-1
) {

    constructor():this("",1)

    var isSelected: Boolean=false

    override fun toString(): String {
        return ClassName
    }
}

data class Competitor(
    @SerializedName("CompetitorName") val CompetitorName: String,
    @SerializedName("ID") val ID: Int,
    val Subject: Sery
//val isChecked: Boolean
) {
    override fun toString(): String {
        return CompetitorName
    }
}

data class GetSchoolsResponse(
    @SerializedName("Soid") val SoID: Int,
    @SerializedName("AreaID") val AreaID: Int
)

data class TodayActiveReminders(
    @SerializedName("ReminderID") val reminderID: Int,
    @SerializedName("SchoolName") val schoolName: String,
    @SerializedName("CityName") val cityName: String,
    @SerializedName("AreaName") val areaName: String,
    @SerializedName("address") val address: String,
    @SerializedName("StudentStrength") val studentStrength: String,
    @SerializedName("LastVisitPurpose") val lastVisitPurpose: String,
    @SerializedName("LastVisitComment") val lastVisitComment: String,
    @SerializedName("LastVisitDate") val lastVisitDate: String,
    @SerializedName("ReminderDate") val reminderDate: String,

)

data class SLocations (
    @SerializedName("SchoolID") val schoolID : Int,
    @SerializedName("SchoolName") val schoolName : String,
    @SerializedName("Latitude")  val latitude : Double,
    @SerializedName("Longitude")  val longitude : Double
) : Serializable

data class GetSchoolInfo(
    @SerializedName("PrincipalName") val PrincipalName: String,
    @SerializedName("Phone1") val Phone1: String,
    @SerializedName("ExpectedQuantity") val expectedQuantity: Int,
    @SerializedName("SchoolShopName") val SchoolShopName: String,
    @SerializedName("IsMember") val isMemberSchool: Boolean

)

data class NextVisitPlanData(
    @SerializedName("IsChecked")
    var IsChecked: Boolean? = null,
    @SerializedName("PlanDate")
    var PlanDate: String? = null,
    @SerializedName("PlanDetails")
    var PlanDetails: String? = null
)

data class VisitPlans(
    @SerializedName("SOID")
    var SOID: Int?,
    @SerializedName("NextVisitPlanData")
    var nextVisitPlanData: List<NextVisitPlanData>?
) {
    constructor() : this(null, null)
}

// Get VisitPlans and Store in it.
// As GET API returns only two parameters Date and Details.
data class GVisitPlans(
    @SerializedName("IsChecked")
    var IsChecked: Boolean? = null,
    @SerializedName("PlnDate")
    var PlanDate: String? = null,
    @SerializedName("PlanDetails")
    var PlanDetails: String? = null,
    @SerializedName("bPrivate")
    var bPrivate: Boolean? = null

) {
    constructor() : this(null, null, null)
}

data class UVisitPlans(
    @SerializedName("SOID")
    var SOID: Int?,
    @SerializedName("NextVisitPlanData")
    var uNextVisitPlanData: List<NextVisitPlanData>?
) {
    constructor() : this(null, null)
}

data class MapsRouteModel(
    var AdminCompanyID:Int?=null,
    var UserID:Int?=null,
    var CreatedDate:String?=null,
    var Lat:Double?=null,
    var Long:Double?=null,
)
data class UserCordinates (
    val LocPoints: List<LOCPoint>
)
data class LOCPoint (
//    val StartLat: Double,
//    val StartLong: Double,
    val EndLat: Double,
    val EndLong: Double
)