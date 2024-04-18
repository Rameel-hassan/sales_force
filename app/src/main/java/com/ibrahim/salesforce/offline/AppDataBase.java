package com.ibrahim.salesforce.offline;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Regions.class, Cities.class, Areas.class, Schools.class, SchLocations.class}, version = 4, exportSchema = false)
@TypeConverters(Converters.class)
public abstract class AppDataBase extends RoomDatabase {

    public abstract RegionDao regionDao();

    public abstract CityDao cityDao();

    public abstract AreaDao areaDao();

    public abstract SchoolDao schoolDao();

    public abstract SchLocationsDao schLocationsDao();

    public static AppDataBase sInstance;


    public static AppDataBase getInstance(final Context context) {

        if (sInstance == null) {
            synchronized (AppDataBase.class) {
                if (sInstance == null) {
                    sInstance = buildDataBaseBuild(context);
                }
            }
        }
        return sInstance;
    }

    private static AppDataBase buildDataBaseBuild(Context context) {
        return Room.databaseBuilder(context,
                AppDataBase.class, "myappdb")
                .fallbackToDestructiveMigration()
                .build();
    }
}