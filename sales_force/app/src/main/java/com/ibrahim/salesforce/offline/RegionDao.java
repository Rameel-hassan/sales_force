package com.app.salesforce.offline;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RegionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRegionInfo(Regions regions);

    @Query("SELECT region_name from regions")
    List<String> getAllRegions();

    @Query("SELECT region_id FROM regions WHERE id = :r_id")
    List<Integer> getAllRegionIDs(int r_id);

    @Query("SELECT region_id FROM regions")
    List<Integer> getAllRegionIDs();

    @Query("DELETE FROM regions")
    void deleteRegions();
}
