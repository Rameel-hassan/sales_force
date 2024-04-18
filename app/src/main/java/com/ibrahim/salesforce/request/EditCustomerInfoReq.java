package com.ibrahim.salesforce.request;

public class EditCustomerInfoReq {

    String ShopName,Name,Phone1,Phone2,Email,Address,ContactPerson,ContactPersonCellNo,Token;
    int StudentStrength,NoOfBranches,NoOfTeachers;

    public EditCustomerInfoReq(){}


    public String getShopName() {
        return ShopName;
    }

    public void setShopName(String shopName) {
        ShopName = shopName;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhone1() {
        return Phone1;
    }

    public void setPhone1(String phone1) {
        Phone1 = phone1;
    }

    public String getPhone2() {
        return Phone2;
    }

    public void setPhone2(String phone2) {
        Phone2 = phone2;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
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

    public int getStudentStrength() {
        return StudentStrength;
    }

    public void setStudentStrength(int studentStrength) {
        StudentStrength = studentStrength;
    }

    public int getNoOfBranches() {
        return NoOfBranches;
    }

    public void setNoOfBranches(int noOfBranches) {
        NoOfBranches = noOfBranches;
    }

    public int getNoOfTeachers() {
        return NoOfTeachers;
    }

    public void setNoOfTeachers(int noOfTeachers) {
        NoOfTeachers = noOfTeachers;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }
}
