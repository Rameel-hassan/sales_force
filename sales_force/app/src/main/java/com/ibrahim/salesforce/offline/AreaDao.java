package com.app.salesforce.offline;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AreaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAreaInfo(Areas areas);

    @Query("SELECT area_name from areas")
    List<String> getAllAreas();

    @Query("SELECT area_id FROM areas")
    List<Integer> getAllAreaIDs();

    @Query("DELETE FROM areas")
    void deleteAreas();

}
