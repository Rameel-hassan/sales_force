package com.app.salesforce.offline;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCityInfo(Cities cities);

    @Query("SELECT city_name from cities")
    List<String> getAllCities();

    @Query("SELECT id, city_id, city_name, region_Id from cities where region_Id = :region_id")
    List<Cities> getCities(int region_id);

    @Query("SELECT city_id FROM cities")
    List<Integer> getAllCityIDs();

    @Query("DELETE FROM cities")
    void deleteCities();
}
