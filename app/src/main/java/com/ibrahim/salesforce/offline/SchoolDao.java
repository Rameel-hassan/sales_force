package com.ibrahim.salesforce.offline;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SchoolDao {

    @Insert
    void addSchools(Schools schools);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSchoolInfo(Schools schools);

    @Query("SELECT school_name from schools")
    List<String> getAllSchools();

    @Query("SELECT school_id FROM schools")
    List<Integer> getAllSchoolsIDs();

    @Query("DELETE FROM schools")
    void deleteSchools();
}
