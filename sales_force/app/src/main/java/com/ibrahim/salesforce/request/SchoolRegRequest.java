package com.app.salesforce.request;

import com.app.salesforce.response.CurrentSyllabus;
import com.app.salesforce.response.GetBookSellerResponse;

import java.util.List;

public class SchoolRegRequest {
    private int RetailerID;
    private String ShopOrSchoolName;
    private String PrincipleName;
    private String ContactNo;
    private String WhatsAppNo;
    private String Phone2;
    private String Phone1;
    private int SalesOfficerID;
    private String Email;
    private int RegionId, CityID;
    private int ZoneID,CurrentSyllabus;
    private int AreaID;
    private String SyllabusSelection;
    private String NewSessionStartDate;
    private double Lattitude;
    private double Longitude;
    private String LocationName;
    private String Address;
    private String Token;
    private String ContactPerson;
    private String ContactPersonCellNo;
    private String EducationSystem;
    private int NoOfBranches;
    private int StudentStrength;
    private int NoOfTeachers;
    private int FeeStructure;
    private List<CurrentSyllabus> CompititorInformation;
    private List<GetBookSellerResponse.BookSeller> ShopsRelatedToRetailer;
    private boolean BookShop;
    private String Picture1;
    private String Picture2;
    private String Customercode;
    private String WorkingPriority;
    private String Website;
    private String ExamBoard;
    private String CountryName;
    private String Remarks;
    private int ID;
    private boolean IsVerified;
    private String SampleMonth;
    private int DistrictID;
    private int TehsilID;

    private String CustomerType;

    private String VendorType="School";
    private int SubAreaID;

    public String getVendorType() {
        return VendorType;
    }

    public void setVendorType(String vendorType) {
        VendorType = vendorType;
    }

    public int getSubAreaID() {
        return SubAreaID;
    }

    public void setSubAreaID(int subAreaID) {
        SubAreaID = subAreaID;
    }

    public int getRetailerID() {
        return RetailerID;
    }

    public void setRetailerID(int retailerID) {
        RetailerID = retailerID;
    }

    public String getShopOrSchoolName() {
        return ShopOrSchoolName;
    }

    public void setShopOrSchoolName(String shopOrSchoolName) {
        ShopOrSchoolName = shopOrSchoolName;
    }

    public String getPrincipleName() {
        return PrincipleName;
    }

    public void setPrincipleName(String principleName) {
        PrincipleName = principleName;
    }

    public String getContactNo() {
        return ContactNo;
    }

    public void setContactNo(String contactNo) {
        ContactNo = contactNo;
    }

    public String getPhone2() {
        return Phone2;
    }

    public void setPhone2(String phone2) {
        Phone2 = phone2;
    }

    public int getSalesOfficerID() {
        return SalesOfficerID;
    }

    public void setSalesOfficerID(int salesOfficerID) {
        SalesOfficerID = salesOfficerID;
    }

    public int getCurrentSyllabus() {
        return CurrentSyllabus;
    }

    public void setCurrentSyllabus(int currentSyllabus) {
        CurrentSyllabus = currentSyllabus;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public int getRegionId() {
        return RegionId;
    }

    public void setRegionId(int regionId) {
        RegionId = regionId;
    }

    public int getCityID() {
        return CityID;
    }

    public void setCityID(int cityID) {
        CityID = cityID;
    }

    public int getZoneID() {
        return ZoneID;
    }

    public void setZoneID(int zoneID) {
        ZoneID = zoneID;
    }

    public int getAreaID() {
        return AreaID;
    }

    public void setAreaID(int areaID) {
        AreaID = areaID;
    }

    public double getLattitude() {
        return Lattitude;
    }

    public void setLattitude(double lattitude) {
        Lattitude = lattitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public String getLocationName() {
        return LocationName;
    }

    public void setLocationName(String locationName) {
        LocationName = locationName;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }

    public String getContactPerson() {
        return ContactPerson;
    }

    public void setContactPerson(String contactPerson) {
        ContactPerson = contactPerson;
    }

    public String getContactPersonCellNo() {
        return ContactPersonCellNo;
    }

    public void setContactPersonCellNo(String contactPersonCellNo) {
        ContactPersonCellNo = contactPersonCellNo;
    }

    public String getEducationSystem() {
        return EducationSystem;
    }

    public void setEducationSystem(String educationSystem) {
        EducationSystem = educationSystem;
    }

    public int getNoOfBranches() {
        return NoOfBranches;
    }

    public void setNoOfBranches(int noOfBranches) {
        NoOfBranches = noOfBranches;
    }

    public int getStudentStrength() {
        return StudentStrength;
    }

    public void setStudentStrength(int studentStrength) {
        StudentStrength = studentStrength;
    }

    public int getNoOfTeachers() {
        return NoOfTeachers;
    }

    public void setNoOfTeachers(int noOfTeachers) {
        NoOfTeachers = noOfTeachers;
    }

    public int getFeeStructure() {
        return FeeStructure;
    }

    public void setFeeStructure(int feeStructure) {
        FeeStructure = feeStructure;
    }
    public List<CurrentSyllabus> getCompititorInformation() {
        return CompititorInformation;
    }

    public void setCompititorInformation(List<CurrentSyllabus> CompititorInformation) {
        this.CompititorInformation = CompititorInformation;
    }

    public boolean isBookShop() {
        return BookShop;
    }

    public void setBookShop(boolean bookShop) {
        BookShop = bookShop;
    }

    public String getPicture1() {
        return Picture1;
    }

    public void setPicture1(String picture1) {
        Picture1 = picture1;
    }

    public String getPicture2() {
        return Picture2;
    }

    public void setPicture2(String picture2) {
        Picture2 = picture2;
    }

    public String getCustomercode() {
        return Customercode;
    }

    public void setCustomercode(String customercode) {
        Customercode = customercode;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public boolean isVerified() {
        return IsVerified;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setVerified(boolean verified) {
        IsVerified = verified;
    }

    public String getPhone1() {
        return Phone1;
    }

    public void setPhone1(String phone1) {
        Phone1 = phone1;
    }

    public String getWhatsAppNo() {
        return WhatsAppNo;
    }

    public void setWhatsAppNo(String whatsAppNo) {
        WhatsAppNo = whatsAppNo;
    }
    public String getSyllabusSelection() {
        return SyllabusSelection;
    }

    public void setSyllabusSelection(String syllabusSelection) {
        SyllabusSelection = syllabusSelection;
    }

    public String getSessionStart() {
        return NewSessionStartDate;
    }

    public void setSessionStart(String sessionStart) {
        NewSessionStartDate = sessionStart;
    }

    public List<GetBookSellerResponse.BookSeller> getShopsRelatedToRetailer() {
        return ShopsRelatedToRetailer;
    }

    public void setShopsRelatedToRetailer(List<GetBookSellerResponse.BookSeller> shopsRelatedToRetailer) {
        ShopsRelatedToRetailer = shopsRelatedToRetailer;
    }
    public String getSampleMonth() {
        return SampleMonth;
    }

    public void setSampleMonth(String sampleMonth) {
        SampleMonth = sampleMonth;
    }

    public int getDistrictID() {
        return DistrictID;
    }

    public void setDistrictID(int districtID) {
        DistrictID = districtID;
    }

    public int getTehsilID() {
        return TehsilID;
    }

    public void setTehsilID(int tehsilID) {
        TehsilID = tehsilID;
    }

    public String getWorkingPriority() {
        return WorkingPriority;
    }

    public void setWorkingPriority(String workingPriority) {
        WorkingPriority = workingPriority;
    }

    public String getWebsite() {
        return Website;
    }

    public void setWebsite(String website) {
        Website = website;
    }

    public String getExamBoard() {
        return ExamBoard;
    }

    public void setExamBoard(String examBoard) {
        ExamBoard = examBoard;
    }

    public String getCountryName() {
        return CountryName;
    }

    public void setCountryName(String countryName) {
        CountryName = countryName;
    }

    public void setCustomerType(String customerType) {
        CustomerType=customerType;
    }

    public String getCustomerType() {
       return CustomerType;
    }
}
