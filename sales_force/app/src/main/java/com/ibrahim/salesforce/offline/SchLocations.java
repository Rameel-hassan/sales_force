package com.app.salesforce.offline;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.esotericsoftware.kryo.NotNull;


@Entity(tableName = "schlocations",
        indices = {@Index(value = {"school_id", "school_name", "latitude", "longitude"}, unique = true)})
public class SchLocations {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NotNull
    @ColumnInfo(name = "school_id")
    private int schoolId;

    @NotNull
    @ColumnInfo(name = "school_name")
    private String schoolName;

    @NotNull
    @ColumnInfo(name = "latitude")
    private Double latitude;

    @NotNull
    @ColumnInfo(name = "longitude")
    private Double longitude;

    public SchLocations(int schoolId, String schoolName, Double latitude, Double longitude) {
        this.schoolId = schoolId;
        this.schoolName = schoolName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(int schoolId) {
        this.schoolId = schoolId;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
