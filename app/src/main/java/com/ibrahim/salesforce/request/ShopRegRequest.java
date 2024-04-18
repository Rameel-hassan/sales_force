package com.ibrahim.salesforce.request;

public class ShopRegRequest {
    private int RetailerID;
    private String TypeOfShop;
    private String ShopName;
    private String OwnerName;
    private String ContactNo;
    private String Phone2;
    private int SalesOfficerID;
    private String Email;
    private int RegionId,CityID;
    private int ZoneID;
    private int AreaID;
    private double Lattitude;
    private double Longitude;
    private String LocationName;
    private String Address;
    private String Token;
    private String ContactPerson;
    private String ContactPersonCellNo;
    private String Picture1;
    private String Picture2;
    private String Remarks;

    private boolean IsVerified;

    public int getRetailerID() {
        return RetailerID;
    }

    public void setRetailerID(int retailerID) {
        RetailerID = retailerID;
    }

    public String getTypeOfShop() {
        return TypeOfShop;
    }

    public void setTypeOfShop(String typeOfShop) {
        TypeOfShop = typeOfShop;
    }

    public String getShopName() {
        return ShopName;
    }

    public void setShopName(String shopName) {
        ShopName = shopName;
    }

    public String getOwnerName() {
        return OwnerName;
    }

    public void setOwnerName(String ownerName) {
        OwnerName = ownerName;
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

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
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

    public int getRegionId() {
        return RegionId;
    }

    public void setRegionId(int regionId) {
        RegionId = regionId;
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

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public boolean isVerified() {
        return IsVerified;
    }

    public void setVerified(boolean verified) {
        IsVerified = verified;
    }
}
