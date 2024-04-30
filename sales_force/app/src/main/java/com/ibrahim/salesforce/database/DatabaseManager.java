package com.app.salesforce.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.app.salesforce.network.LocationDetails;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Rameel Hassan
 * Created 20/07/2022 at 11:09 pm
 */
public class DatabaseManager {

    private AtomicInteger mOpenCounter = new AtomicInteger();
    private static DatabaseManager instance;
    private static DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mDatabase;
    private Context context;

    private DatabaseManager(Context context)
    {
        mDatabaseHelper=new DatabaseHelper(context);
        this.context=context;
    }

    public static synchronized DatabaseManager getInstance(Context context) {
        if (instance == null) {
            instance=new DatabaseManager(context);
        }
        return instance;
    }

    public synchronized SQLiteDatabase openDatabase() {
        if(mOpenCounter.incrementAndGet() == 1) {
            ////////Opening new database/////////
            mDatabase = mDatabaseHelper.getWritableDatabase();
        }
        return mDatabase;
    }

    public synchronized void closeDatabase() {
        if(mOpenCounter.decrementAndGet() == 0) {
            ////// Closing database////////
            mDatabase.close();

        }
    }

/////////////////////// UserTracker Table Operations//////////////////////////////////////////////

    public void addUserTrackerPoints(LocationDetails item)
    {

        SQLiteDatabase db=openDatabase();

        try {
            db.beginTransaction();


                ContentValues values = new ContentValues();
                
                values.put(DBConstants.KEY_USER_ID, item.getUserID());
                values.put(DBConstants.KEY_ADMIN_COMPANY_ID, item.getAdminCompanyID());
                values.put(DBConstants.KEY_LAT, item.getLat());
                values.put(DBConstants.KEY_LNG, item.getLng());
                values.put(DBConstants.KEY_CREATED_DATE, item.getCreatedDate());

                db.insertWithOnConflict(DBConstants.TABLE_USER_LOCATION, null, values, SQLiteDatabase.CONFLICT_IGNORE);

            

            db.setTransactionSuccessful();
        }catch (Exception ex){ex.printStackTrace();}
        finally {
            db.endTransaction();
        }
        closeDatabase();

    }

    public ArrayList<LocationDetails> getUserTrackerPoints()
    {
        ArrayList<LocationDetails> items=new ArrayList<LocationDetails>();
        String selectQuery = "SELECT  * FROM " + DBConstants.TABLE_USER_LOCATION;
        SQLiteDatabase db=openDatabase();
        try {
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor!=null && cursor.moveToFirst()) {
                do {
                    LocationDetails item=new LocationDetails();
                    item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DBConstants.KEY_ID)));
                    item.setLat(cursor.getDouble(cursor.getColumnIndexOrThrow(DBConstants.KEY_LAT)));
                    item.setLng(cursor.getDouble(cursor.getColumnIndexOrThrow(DBConstants.KEY_LNG)));
                    item.setUserID(cursor.getInt(cursor.getColumnIndexOrThrow(DBConstants.KEY_USER_ID)));
                    item.setAdminCompanyID(cursor.getInt(cursor.getColumnIndexOrThrow(DBConstants.KEY_ADMIN_COMPANY_ID)));
                    item.setCreatedDate(cursor.getLong(cursor.getColumnIndexOrThrow(DBConstants.KEY_CREATED_DATE)));
                    items.add(item);


                } while(cursor.moveToNext());
            }

        }catch (Exception ex){ex.printStackTrace();}

        closeDatabase();
        return items;
    }

    public boolean deleteUserTrackerPoint(long timeStamp)
    {
        boolean isSuccessFullyDelete = false;
        SQLiteDatabase db=openDatabase();
        try {
            isSuccessFullyDelete = db.delete(DBConstants.TABLE_USER_LOCATION, DBConstants.KEY_CREATED_DATE + "=" + timeStamp, null) > 0;
        }catch (Exception ex){ex.printStackTrace();}

        closeDatabase();
        return isSuccessFullyDelete;
    }

    public void deleteAllUserTracker()
    {
        deleteTable(DBConstants.TABLE_USER_LOCATION);
    }
    public int getUserTrackerCount() {
        return getRecordCount(DBConstants.TABLE_USER_LOCATION);
    }




    ///////////////////////////// General Operations////////////////////////////////////////////////

    public void deleteTable(String tableName)
    {
        SQLiteDatabase db=openDatabase();
        db.delete(tableName, null, null);
        closeDatabase();
    }
    public int getRecordCount(String tableName)//select count(*) from
    {
        int count=0;
        SQLiteDatabase db=openDatabase();
        try {
            count = (int) DatabaseUtils.queryNumEntries(db,tableName);
            Log.d("DatabaseManager", "Count: "+ count);
        }catch (Exception ex){ex.printStackTrace();}
        closeDatabase();

        return count;
    }

}
