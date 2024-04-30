package com.app.salesforce.offline;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.esotericsoftware.kryo.NotNull;

@Entity(tableName = "regions")
public class Regions {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @NotNull
    @ColumnInfo(name = "region_id")
    private int regionId;

    @NotNull
    @ColumnInfo(name = "region_name")
    private String regionName;

    public Regions(int regionId, String regionName) {
        this.regionId = regionId;
        this.regionName = regionName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRegionId() {
        return regionId;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }
}
