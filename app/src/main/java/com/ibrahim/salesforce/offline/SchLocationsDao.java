package com.ibrahim.salesforce.offline;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;
@Dao
public interface SchLocationsDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSchLocation(List<SchLocations> sLocations);

    @Query("SELECT * from schlocations")
    List<SchLocations> getAllSchLocations();

    @Query("SELECT latitude FROM schlocations")
    List<Double> getLatitudeSchLocation();


    @Query("SELECT longitude FROM schlocations")
    List<Double> getLongitudeSchLocation();

    @Query("DELETE FROM schlocations")
    void deleteSchLocations();
}
