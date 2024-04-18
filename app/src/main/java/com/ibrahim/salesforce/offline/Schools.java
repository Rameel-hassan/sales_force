package com.ibrahim.salesforce.offline;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.esotericsoftware.kryo.NotNull;

@Entity(tableName = "schools",
        indices = {@Index(value = {"school_id"}, unique = true)})
public class Schools {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NotNull
    @ColumnInfo(name = "school_id")
    private int schoolID;

    @NotNull
    @ColumnInfo(name = "school_name")
    private String schoolName;

    @NotNull
    @ColumnInfo(name = "area_id")
    private int areaID;

    @NotNull
    @ColumnInfo(name = "city_id")
    private int cityID;

    public Schools(int schoolID, String schoolName, int areaID, int cityID) {
        this.schoolID = schoolID;
        this.schoolName = schoolName;
        this.areaID = areaID;
        this.cityID = cityID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSchoolID() {
        return schoolID;
    }

    public void setSchoolID(int schoolID) {
        this.schoolID = schoolID;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public int getAreaID() {
        return areaID;
    }

    public void setAreaID(int areaID) {
        this.areaID = areaID;
    }

    public int getCityID() {
        return cityID;
    }

    public void setCityID(int cityID) {
        this.cityID = cityID;
    }


}
