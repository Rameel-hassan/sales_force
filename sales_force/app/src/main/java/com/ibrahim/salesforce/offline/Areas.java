package com.app.salesforce.offline;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.esotericsoftware.kryo.NotNull;

@Entity(tableName = "areas",
        indices = {@Index(value = {"area_id", "area_name"}, unique = true)})
public class Areas {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NotNull
    @ColumnInfo(name = "area_id")
    private int areaId;

    @NotNull
    @ColumnInfo(name = "area_name")
    private String areaName;

    @NotNull
    @ColumnInfo(name = "city_Id")
    private int cityId;

    public Areas(int areaId, String areaName, int cityId) {
        this.areaId = areaId;
        this.areaName = areaName;
        this.cityId = cityId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}
