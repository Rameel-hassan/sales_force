package com.app.salesforce.request;

/**
 * @author Rameel Hassan
 * Created 04/04/2024 at 12:31 pm
 */
public class RouteDetail {

    int id;
    int adminCompanyID;
    int userID;
    String startingTime;
    String endingTime;
    Double startingLat;
    Double startingLong;

    Double endingLat;
    Double endingLong;

    String startingPhone;
    String endingPhone;

    String route;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAdminCompanyID() {
        return adminCompanyID;
    }

    public void setAdminCompanyID(int adminCompanyID) {
        this.adminCompanyID = adminCompanyID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getStartingTime() {
        return startingTime;
    }

    public void setStartingTime(String startingTime) {
        this.startingTime = startingTime;
    }

    public String getEndingTime() {
        return endingTime;
    }

    public void setEndingTime(String endingTime) {
        this.endingTime = endingTime;
    }

    public Double getStartingLat() {
        return startingLat;
    }

    public void setStartingLat(Double startingLat) {
        this.startingLat = startingLat;
    }

    public Double getStartingLong() {
        return startingLong;
    }

    public void setStartingLong(Double startingLong) {
        this.startingLong = startingLong;
    }

    public Double getEndingLat() {
        return endingLat;
    }

    public void setEndingLat(Double endingLat) {
        this.endingLat = endingLat;
    }

    public Double getEndingLong() {
        return endingLong;
    }

    public void setEndingLong(Double endingLong) {
        this.endingLong = endingLong;
    }

    public String getStartingPhone() {
        return startingPhone;
    }

    public void setStartingPhone(String startingPhone) {
        this.startingPhone = startingPhone;
    }

    public String getEndingPhone() {
        return endingPhone;
    }

    public void setEndingPhone(String endingPhone) {
        this.endingPhone = endingPhone;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }
}
