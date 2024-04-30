package com.app.salesforce.database;

/**
 * @author Rameel Hassan
 * Created 20/07/2022 at 11:05 pm
 */
public class DBConstants {

    // Database Info
    public static final String DATABASE_NAME = "salesforce_database";
    public static final int DATABASE_VERSION = 1;

    // Table Names
    public static final String TABLE_USER_LOCATION = "location_points";

    // General Table Columns
    public static final String KEY_ID = "id";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_ADMIN_COMPANY_ID = "admin_id";
    public static final String KEY_CREATED_DATE = "created_date";
    public static final String KEY_LAT = "lat";
    public static final String KEY_LNG = "lng";


    public static String getCreateUserTrackerTableQuery()
    {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_USER_LOCATION +
                "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_USER_ID + " INTEGER ," +
                KEY_ADMIN_COMPANY_ID + " INTEGER ," +
                KEY_LNG + " REAL DEFAULT 0," +
                KEY_LAT + " REAL DEFAULT 0," +
                KEY_CREATED_DATE + " REAL" +
                ")";
        return CREATE_TABLE;
    }

}
