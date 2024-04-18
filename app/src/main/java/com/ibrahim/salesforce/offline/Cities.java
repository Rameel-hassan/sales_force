package com.ibrahim.salesforce.offline;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.esotericsoftware.kryo.NotNull;

@Entity(tableName = "cities",
        indices = {@Index(value = {"city_id", "city_name"}, unique = true)})
public class Cities {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NotNull
    @ColumnInfo(name = "city_id")
    private int cityId;

    @NotNull
    @ColumnInfo(name = "city_name")
    private String cityName;

    @NotNull
    @ColumnInfo(name = "region_Id")
    private int regionId;

    public Cities(int cityId, String cityName, int regionId) {
        this.cityId = cityId;
        this.cityName = cityName;
        this.regionId = regionId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getRegionId() {
        return regionId;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
    }
}
