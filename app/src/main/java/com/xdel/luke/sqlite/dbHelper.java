package com.xdel.luke.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.xdel.luke.objClasses.objGlobalMethods;

/**
 * Created by kean.ong on 03/05/2017.
 */
public class dbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "luke.db";
    private static final int DATABASE_VERSION = 23; //version 4.88 was DATABASE_VERSION = 21
    //v10::CALLHISTORY table added
    //v11::NetsVisaTI column added

    public dbHelper(Context context) {
        //super(context, DATABASE_NAME, null, DATABASE_VERSION);
        super(context, "lukedb", null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        try {
            database.execSQL(sqlStringJobs.CREATE_JOB_TABLE());
            database.execSQL(sqlStringJobs.CREATE_TRADE_IN_LIST_TABLE());
            database.execSQL(sqlStringJobs.CREATE_NFCTAGS_TABLE());
            database.execSQL(sqlStringJobs.CREATE_CABINET_UNIVERSAL_TABLE());
            database.execSQL(sqlStringJobs.CREATE_ChatMsgSenders_TABLE());
            database.execSQL(sqlStringJobs.CREATE_ChatMessage_TABLE());
            database.execSQL(sqlStringJobs.CREATE_CALLHISTORY_TABLE());
            database.execSQL(sqlStringJobs.CREATE_PIPPCS_TABLE());
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at dbHelper onCreate. Exception: " + e.getMessage(),"","","","");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");*/

        //we simply drop the whole table, not altering it, even if table is altered during development, we will drop the whole table, since we r not storing data in device
        try {
            if (newVersion > oldVersion) {
                db.execSQL("DROP TABLE IF EXISTS " + sqlStringJobs.TABLE_NAME);
                db.execSQL("DROP TABLE IF EXISTS " + sqlStringJobs.TABLE_NAME_TRADEINLIST);
                db.execSQL("DROP TABLE IF EXISTS " + sqlStringJobs.TABLE_NAME_NFCTAGS);
                db.execSQL("DROP TABLE IF EXISTS " + sqlStringJobs.TABLE_NAME_CABINET_UNIVERSAL_TBL);
                db.execSQL("DROP TABLE IF EXISTS " + sqlStringJobs.TABLE_NAME_ChatMsgSenders_TBL);
                db.execSQL("DROP TABLE IF EXISTS " + sqlStringJobs.TABLE_NAME_CHATMESSAGE_TBL);
                db.execSQL("DROP TABLE IF EXISTS " + sqlStringJobs.TABLE_NAME_CALLHISTORY_TBL);
                db.execSQL("DROP TABLE IF EXISTS " + sqlStringJobs.TABLE_NAME_PIPPCS_TBL);
                onCreate(db);
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at dbHelper onUpgrade. Exception: " + e.getMessage(),"","","","");
        }
    }

    public void deleteAllTables(SQLiteDatabase db){
        try {
            db.execSQL("DROP TABLE IF EXISTS " + sqlStringJobs.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + sqlStringJobs.TABLE_NAME_TRADEINLIST);
            db.execSQL("DROP TABLE IF EXISTS " + sqlStringJobs.TABLE_NAME_NFCTAGS);
            db.execSQL("DROP TABLE IF EXISTS " + sqlStringJobs.TABLE_NAME_CABINET_UNIVERSAL_TBL);
            db.execSQL("DROP TABLE IF EXISTS " + sqlStringJobs.TABLE_NAME_ChatMsgSenders_TBL);
            db.execSQL("DROP TABLE IF EXISTS " + sqlStringJobs.TABLE_NAME_CHATMESSAGE_TBL);
            db.execSQL("DROP TABLE IF EXISTS " + sqlStringJobs.TABLE_NAME_CALLHISTORY_TBL);
            db.execSQL("DROP TABLE IF EXISTS " + sqlStringJobs.TABLE_NAME_PIPPCS_TBL);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at dbHelper deleteAllTables. Exception: " + e.getMessage(),"","","","");
        }
    }



}
