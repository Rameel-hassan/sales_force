package com.app.salesforce.database;

/**
 * @author Rameel Hassan
 * Created 20/07/2022 at 11:02 pm
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context) {
        super(context, DBConstants.DATABASE_NAME, null, DBConstants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(DBConstants.getCreateUserTrackerTableQuery());

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DB OnUpgrade","OnUpgrade ********");
        if (oldVersion != newVersion) {

            switch(oldVersion) {

                case 1:
//                    db.execSQL("ALTER TABLE "+ DBConstants.TABLE_STAFF_MEMBER + " ADD COLUMN " + DBConstants.KEY_TENANT_ID  + "  INTEGER DEFAULT 0");
                    Log.d("DB OnUpgrade","OnUpgrade "+oldVersion+" "+newVersion);
                    break;

                default:
                    Log.e("UpgradeError ","onUpgrade() with unknown oldVersion " + oldVersion);
            }
        }

    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        //db.setForeignKeyConstraintsEnabled(true);
    }
}
