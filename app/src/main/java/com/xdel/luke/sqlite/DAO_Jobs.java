package com.xdel.luke.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.google.gson.Gson;
import com.xdel.luke.BottomSheet.vrpUnlockPostData;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.JobsHandler2;
import com.xdel.luke.PieceByPiecePU.PipPcs;
import com.xdel.luke.PieceByPiecePU.dbMedsJobs;
import com.xdel.luke.R;
import com.xdel.luke.Vrp.JobWithinTimeWindow;
import com.xdel.luke.jobsqueue.JobQueueLite;
import com.xdel.luke.messagingClasses.ChatMessage;
import com.xdel.luke.messagingClasses.ChatMsgSenders;
import com.xdel.luke.messagingClasses.Status;
import com.xdel.luke.messagingClasses.UserType;
import com.xdel.luke.messagingClasses.objChatMessage;
import com.xdel.luke.messagingClasses.objUnreadPUOKPFAILjobsMsgInfo;
import com.xdel.luke.objClasses.Location;
import com.xdel.luke.objClasses.NFCTag;
import com.xdel.luke.objClasses.RouteLocation;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objContactStructure;
import com.xdel.luke.objClasses.objDOCOD;
import com.xdel.luke.objClasses.objDOObject;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;
import com.xdel.luke.objClasses.objJobInfo;
import com.xdel.luke.objClasses.objNumOfCallOutPast15min;
import com.xdel.luke.objClasses.objPostalFlagSum;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by kean.ong on 03/05/2017.
 */
public class DAO_Jobs {
    public static SQLiteDatabase database;
    public static dbHelper dbhelper;

    //Databse insert sql statement
    private static final String DATABASE_INSERT = "";

    //Databse update sql statement
    private static final String DATABASE_UPDATE = "";

    //Databse delete sql statement
    private static final String DATABASE_DELETE = "DELETE FROM " + sqlStringJobs.TABLE_NAME;

    public DAO_Jobs(Context context) {
        if (dbhelper == null) {
            dbhelper = new dbHelper(context);
        }
    }

    public static void initdbHelper() {
        if (dbhelper == null) {
            dbhelper = new dbHelper(myApp.getAppContext());
        }
    }

    public static void initdbHelper(Context context) {
        if (dbhelper == null) {
            dbhelper = new dbHelper(context);
        }
    }

    public static void nullifystaticitems() {
        database = null;
    }

    /*public static Boolean databaseIsOpen() {
        return database == null ? false : true;
    }

    public static void open() throws SQLException {
        if (dbhelper != null) {
            database = dbhelper.getWritableDatabase();
        } else {
            dbhelper = new dbHelper(myApp.getAppContext());
            database = dbhelper.getWritableDatabase();
        }
    }

    public static void close() {
        if (dbhelper != null) {
            dbhelper.close();
        } else {
            dbhelper = new dbHelper(myApp.getAppContext());
            dbhelper.close();
        }
    }*/

    public static void dropLukeDB() {
        boolean asd = false;
        String[] UsernamePwd = new String[4];
        String ishomesuser;
        try {
            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(objGlobalMethods.getGcmPreferences());
            ishomesuser = UsernamePwd[3];
            if (dbhelper != null) {
                dbhelper.close();
            } else {
                dbhelper = new dbHelper(myApp.getAppContext());
                dbhelper.close();
            }
            try {
                if (Boolean.parseBoolean(ishomesuser)) {
                    asd = myApp.getAppContext().deleteDatabase(dbHelper.DATABASE_NAME);
                } else {
                    asd = myApp.getAppContext().deleteDatabase("lukedb");
                }

            } catch (Exception e) {
                String ex = e.getMessage();
                String s = JobsHandler.getStackTraceString(e);
                String f = ex + " " + s;
            }

            dbhelper = null;
            nullifystaticitems();
            //objGlobalMethods.writeToFile("LukeDB dropped at DAO_Jobs dropLukeDB.", "", "", "", "");

        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs dropLukeDB. Exception: " + e.getMessage().toString() + "\n" + "Stack: " + stack, "", "", "", "");
        }
    }

    private static void dbEndTransaction() {
        try {
            if (database != null && database.inTransaction()) {
                database.endTransaction();
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at DAO_Jobs dbEndTransaction. Exception: " + e.getMessage(), "", "", "", "");
        }

    }

    //======================================queries for loading list pages======================================

    //for dashboard
    public static List<dbObjCategoryCount> getCategoryCount() {
        List<dbObjCategoryCount> dbObjCategoryCountAL = null;
        try {
            dbObjCategoryCountAL = new ArrayList<dbObjCategoryCount>();
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();
            String sql = sqlStringJobs.DATABASE_GETCATEGORYCOUNT2();
            Cursor cursor = database.rawQuery(sql, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                dbObjCategoryCount obj = cursorTodbObjCategoryCount(cursor);
                dbObjCategoryCountAL.add(obj);
                cursor.moveToNext();
            }
            // make sure to close the cursor
            cursor.close();
            database.setTransactionSuccessful();
        } catch (Exception e) {
            dbObjCategoryCountAL = null;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getCategoryCount. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return dbObjCategoryCountAL;
        }
    }

    public static int getModelListCount() {
        int c = 0;
        try {
            String sqlstr = sqlStringJobs.getTradeInListCountSQLstr();
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    c = cursor.getInt(cursor.getColumnIndex("C"));
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            c = 0;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getModelListCount. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return c;
        }
    }

    //for dashboardsub
    public static List<dbObjJobs> getAlldbObjJobsForDashboardSubByJobsIDXs(String Postal, String JobsIDXs) {
        List<dbObjJobs> dbObjJobsAL = null;
        try {
            dbObjJobsAL = new ArrayList<dbObjJobs>();
            String sqlstr = sqlStringJobs.getAlldbObjJobsForDashboardSubByJobsIDXsSQLstr(Postal, JobsIDXs);
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    dbObjJobs obj = cursorTodbObjJobs(cursor, -1);
                    dbObjJobsAL.add(obj);
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            dbObjJobsAL = null;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getAlldbObjJobsForDashboardSubByJobsIDXs. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return dbObjJobsAL;
        }
    }

    public static List<dbObjJobs> getAlldbObjJobsForDashboardSub(String Postal) {
        List<dbObjJobs> dbObjJobsAL = null;
        try {
            dbObjJobsAL = new ArrayList<dbObjJobs>();
            String sqlstr = sqlStringJobs.getAlldbObjJobsForDashboardSubSQLstr(Postal);
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    dbObjJobs obj = cursorTodbObjJobs(cursor, -1);
                    dbObjJobsAL.add(obj);
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            dbObjJobsAL = null;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getAlldbObjJobsPerCategoryForJobsList. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return dbObjJobsAL;
        }
    }

    public static List<dbObjCategoryCount> getPerCategoryCount(Integer CatTypeID) {
        List<dbObjCategoryCount> dbObjCategoryCountAL = null;
        try {
            dbObjCategoryCountAL = new ArrayList<dbObjCategoryCount>();
            String sqlstr = sqlStringJobs.getPerCategoryCountSQLstr(CatTypeID);
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    dbObjCategoryCount obj = cursorTodbObjCategoryCount(cursor);
                    dbObjCategoryCountAL.add(obj);
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            dbObjCategoryCountAL = null;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getPerCategoryCount. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return dbObjCategoryCountAL;
        }
    }

    public static List<dbObjAddressList> getAlldbObjJobsPerCategoryForAddressList(Integer CatTypeID) {
        List<dbObjAddressList> dbObjAddressListAL = null;
        List<String> PostalAL = new ArrayList<String>();
        List<Integer> FlagSumAL = new ArrayList<Integer>();
        int FlagSum = 0;
        int isPickAndSendCount = 0;
        int JobChangesCount = 0;
        int ColdCount = 0;
        String postal = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM h:mma");
        try {
            dbObjAddressListAL = new ArrayList<dbObjAddressList>();
            String sqlstr = sqlStringJobs.getgetAlldbObjJobsPerCategoryForAddressListSQLstr(CatTypeID);
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    postal = cursor.getString(cursor.getColumnIndex("POSTAL"));
                    isPickAndSendCount = cursor.getInt(cursor.getColumnIndex("isPickAndSendCount"));
                    JobChangesCount = cursor.getInt(cursor.getColumnIndex("JobChangesCount"));
                    ColdCount = cursor.getInt(cursor.getColumnIndex("ColdCount"));

                    if (!PostalAL.contains(postal)) {
                        PostalAL.add(postal);
                        FlagSum = cursor.getInt(cursor.getColumnIndex(sqlStringJobs.COLUMN_NetsVisaTI));
                        FlagSumAL.add(FlagSum);
                        dbObjAddressList obj = cursorTodbObjAddressList(cursor, CatTypeID);
                        obj.setReqNetsCount((FlagSum & 4) == 4 ? 1 : 0);
                        obj.setReqVisaCount((FlagSum & 8) == 8 ? 1 : 0);
                        obj.setReqTiCount((FlagSum & 16) == 16 ? 1 : 0);
                        obj.setColdCount(ColdCount);
                        dbObjAddressListAL.add(obj);
                    } else {
                        int foundidx = -1;
                        int foundFlagSum = 0;
                        int currFlagSum = cursor.getInt(cursor.getColumnIndex(sqlStringJobs.COLUMN_NetsVisaTI));
                        for (int i = 0; i <= PostalAL.size() - 1; i++) {
                            if (PostalAL.get(i).equals(postal)) {
                                foundidx = i;
                                break;
                            }
                        }

                        //new
                        if (foundidx > -1 && (CatTypeID == 0 || CatTypeID == 1 || CatTypeID == 4)) {
                            dbObjAddressList obj = dbObjAddressListAL.get(foundidx);
                            long FromDateTime = cursor.getLong(cursor.getColumnIndex("FromDateTime"));
                            long ToDateTime = cursor.getLong(cursor.getColumnIndex("ToDateTime"));
                            String toText = cursor.getString(cursor.getColumnIndex("ToText"));
                            boolean puby = toText.equals("PU BY");
                            String FromDateTimeConverted = "";
                            String ToDateTimeConverted = "";
                            if (FromDateTime < obj.FromDateTime) {
                                obj.setFromDateTime(cursor.getLong(cursor.getColumnIndex("FromDateTime")));
                                FromDateTimeConverted = sdf.format(new Date(cursor.getLong(cursor.getColumnIndex("FromDateTimeConverted")) * 1000L));
                                obj.setFromDateTimeConverted(cursor.getString(cursor.getColumnIndex("FromDateTimeConverted")));
                                if (puby && ToDateTime < obj.ToDateTime) {
                                    obj.setToDateTime(cursor.getLong(cursor.getColumnIndex("ToDateTime")));
                                    ToDateTimeConverted = sdf.format(new Date(cursor.getLong(cursor.getColumnIndex("ToDateTimeConverted")) * 1000L));
                                    obj.setToDateTimeConverted(cursor.getString(cursor.getColumnIndex("ToDateTimeConverted")));
                                    obj.setToText(cursor.getString(cursor.getColumnIndex("ToText")));
                                } else {
                                    toText = obj.ToText;
                                    ToDateTimeConverted = sdf.format(new Date(obj.ToDateTime * 1000L));
                                }
                                obj.setTimingText(obj.FromText + FromDateTimeConverted
                                        + "\n" + toText + ToDateTimeConverted);
                            }
                        }
                        //new

                        if (foundidx > -1 && FlagSumAL != null && FlagSumAL.size() > 0 && FlagSumAL.size() >= (foundidx + 1)) {
                            foundFlagSum = FlagSumAL.get(foundidx);
                            FlagSum = rtnFlagSumPerPostal(foundFlagSum, currFlagSum);
                            FlagSumAL.set(foundidx, FlagSum);
                            dbObjAddressList obj = dbObjAddressListAL.get(foundidx);
                            obj.setReqNetsCount((FlagSum & 4) == 4 ? 1 : 0);
                            obj.setReqVisaCount((FlagSum & 8) == 8 ? 1 : 0);
                            obj.setReqTiCount((FlagSum & 16) == 16 ? 1 : 0);
                            obj.setColdCount(ColdCount);
                            if (obj.JobChangesCount == 0) {
                                obj.setJobChangesCount(JobChangesCount);
                            }
                            if (obj.isPickAndSendCount == 0) {
                                obj.setIsPickAndSendCount(isPickAndSendCount);
                            }

                            dbObjAddressListAL.set(foundidx, obj);
                        }
                    }
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            dbObjAddressListAL = null;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getAlldbObjJobsPerCategoryForAddressList. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return dbObjAddressListAL;
        }
    }

    public static List<dbObjJobs> getAlldbObjJobsPerCategoryForJobsListByJobsIDXs(Integer CatTypeID, String Postal, String JobsIDXs) {
        List<dbObjJobs> dbObjJobsAL = null;
        try {
            dbObjJobsAL = new ArrayList<dbObjJobs>();
            String sqlstr = sqlStringJobs.getgetAlldbObjJobsPerCategoryForJobsListByJobsIDXsSQLstr(CatTypeID, Postal, JobsIDXs);
            boolean exist = false;
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    dbObjJobs obj = cursorTodbObjJobs(cursor, CatTypeID);
                    for (int x = 0; x <= dbObjJobsAL.size() - 1; x++) {
                        exist = false;
                        if (dbObjJobsAL.get(x).JobsIDX == obj.JobsIDX) {
                            exist = true;
                            break;
                        }
                    }
                    if (!exist) {
                        dbObjJobsAL.add(obj);
                    }
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            dbObjJobsAL = null;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getAlldbObjJobsPerCategoryForJobsListByJobsIDXs. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return dbObjJobsAL;
        }
    }

    public static List<dbObjJobs> getAlldbObjJobsPerCategoryForJobsList(Integer CatTypeID, String Postal) {
        List<dbObjJobs> dbObjJobsAL = null;
        try {
            dbObjJobsAL = new ArrayList<dbObjJobs>();
            String sqlstr = sqlStringJobs.getgetAlldbObjJobsPerCategoryForJobsListSQLstr(CatTypeID, Postal);
            boolean exist = false;
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    dbObjJobs obj = cursorTodbObjJobs(cursor, CatTypeID);
                    for (int x = 0; x <= dbObjJobsAL.size() - 1; x++) {
                        exist = false;
                        if (dbObjJobsAL.get(x).JobsIDX == obj.JobsIDX) {
                            exist = true;
                            break;
                        }
                    }
                    if (!exist) {
                        dbObjJobsAL.add(obj);
                    }
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            dbObjJobsAL = null;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getAlldbObjJobsPerCategoryForJobsList. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return dbObjJobsAL;
        }
    }

    //======================================search functions======================================
    public static List<dbObjJobs> searchJobsWithoutPostal(Integer CatTypeID, String Criteria) {
        List<dbObjJobs> dbObjJobsAL = null;
        try {
            dbObjJobsAL = new ArrayList<dbObjJobs>();
            String sqlstr = sqlStringJobs.getsearchJobsWithoutPostalSQLstr(CatTypeID, Criteria.toUpperCase());
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    dbObjJobs obj = cursorTodbObjJobs(cursor, CatTypeID);
                    dbObjJobsAL.add(obj);
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            dbObjJobsAL = null;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs searchJobs. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return dbObjJobsAL;
        }
    }

    public static List<dbObjJobs> searchJobs(Integer CatTypeID, String Postal, String Criteria) {
        List<dbObjJobs> dbObjJobsAL = null;
        try {
            dbObjJobsAL = new ArrayList<dbObjJobs>();
            String sqlstr = sqlStringJobs.getsearchJobsSQLstr2(CatTypeID, Postal, Criteria.toUpperCase());
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    dbObjJobs obj = cursorTodbObjJobs(cursor, CatTypeID);
                    dbObjJobsAL.add(obj);
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            dbObjJobsAL = null;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs searchJobs. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return dbObjJobsAL;
        }
    }

    public static List<dbObjJobs> searchJobsViaJobsIDXs(String JobsIDXs) {
        List<dbObjJobs> dbObjJobsAL = null;
        try {
            dbObjJobsAL = new ArrayList<dbObjJobs>();
            String sqlstr = sqlStringJobs.getsearchJobsViaJobsIDXsSQLstr(JobsIDXs);
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    dbObjJobs obj = cursorTodbObjJobs(cursor, -1);
                    dbObjJobsAL.add(obj);
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            dbObjJobsAL = null;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs searchJobsViaJobsIDXs. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return dbObjJobsAL;
        }
    }

    public static Integer getStdRunJobsCount() {
        Integer c = 0;
        try {
            String sqlstr = sqlStringJobs.getStdRunJobsCount();
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();
            Cursor cursor = database.rawQuery(sqlstr, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                c = cursor.getInt(cursor.getColumnIndex("C"));
                cursor.moveToNext();
            }
            // make sure to close the cursor
            cursor.close();
            database.setTransactionSuccessful();
        } catch (Exception e) {
            c = 0;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getStdRunJobsCount. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return c;
        }
    }

    public static Integer getTotalJobsCountPerCatType(Integer CatTypeID) {
        Integer totalCount = 0;
        try {
            String sqlstr = sqlStringJobs.getTotalJobsCountPerCatTypeSQLstr(CatTypeID);
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();
            Cursor cursor = database.rawQuery(sqlstr, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                totalCount = cursor.getInt(cursor.getColumnIndex("CT"));
                cursor.moveToNext();
            }
            // make sure to close the cursor
            cursor.close();
            database.setTransactionSuccessful();
        } catch (Exception e) {
            totalCount = 0;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getTotalJobsCountPerCatType. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return totalCount;
        }
    }

    public static Integer getTotalOutstandingNumOfPipDip() {
        Integer totalCount = 0;
        try {
            String sqlstr = sqlStringJobs.getTotalOutstandingNumOfPipDipSqlStr();
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();
            Cursor cursor = database.rawQuery(sqlstr, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                totalCount = cursor.getInt(cursor.getColumnIndex("CT"));
                cursor.moveToNext();
            }
            // make sure to close the cursor
            cursor.close();
            database.setTransactionSuccessful();
        } catch (Exception e) {
            totalCount = 0;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getTotalOutstandingNumOfPipDip. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return totalCount;
        }
    }

    public static ArrayList<vrpUnlockPostData> getAllLocationJobsInPipDipParked() {
        ArrayList<vrpUnlockPostData> al = null;
        try {
            al = new ArrayList<vrpUnlockPostData>();
            String sqlstr = sqlStringJobs.getAllLocationJobsInPipDipParkedSqlStr();
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    vrpUnlockPostData obj = cursorTovrpUnlockPostData(cursor);
                    al.add(obj);
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            al = null;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getAllLocationJobsInPipDipParked. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            dbEndTransaction();
            return al;
        }
    }

    public static ArrayList<String> getAllPostalsInPipDip() {
        ArrayList<String> al = null;
        try {
            al = new ArrayList<String>();
            String sqlstr = sqlStringJobs.getAllPostalsInPipDipSqlStr();
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    al.add(cursor.getString(cursor.getColumnIndex("POSTAL")));
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            al = null;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getAllPostalsInPipDip. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            dbEndTransaction();
            return al;
        }
    }

    public static ArrayList<String> getAllPostalsInDip() {
        ArrayList<String> al = null;
        try {
            al = new ArrayList<String>();
            String sqlstr = sqlStringJobs.getAllPostalsInDipSqlStr();
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    al.add(cursor.getString(cursor.getColumnIndex("POSTAL")));
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            al = null;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getAllPostalsInDip. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            dbEndTransaction();
            return al;
        }
    }

    public static ArrayList<String> getAllPostalsInPipDipByAMOnlySlot() {
        ArrayList<String> al = null;
        try {
            al = new ArrayList<String>();
            String sqlstr = sqlStringJobs.getAllPostalsInPipDipByAMOnlySlotSqlStr();
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    al.add(cursor.getString(cursor.getColumnIndex("POSTAL")));
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            al = null;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getAllPostalsInPipDipByAMOnlySlot. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            dbEndTransaction();
            return al;
        }
    }

    public static ArrayList<String> getAllJobsIDXsInPipDipByAMOnlySlot() {
        ArrayList<String> al = null;
        try {
            al = new ArrayList<String>();
            String sqlstr = sqlStringJobs.getAllJobsIDXsInPipDipByAMOnlySlotSqlStr();
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    al.add(cursor.getString(cursor.getColumnIndex("JobsIDX")));
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            al = null;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getAllJobsIDXsInPipDipByAMOnlySlot. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            dbEndTransaction();
            return al;
        }
    }

    public static ArrayList<String> getAllPostalsInPipDipByAMSlot() {
        ArrayList<String> al = null;
        try {
            al = new ArrayList<String>();
            String sqlstr = sqlStringJobs.getAllPostalsInPipDipByAMSlotSqlStr();
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    al.add(cursor.getString(cursor.getColumnIndex("POSTAL")));
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            al = null;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getAllPostalsInPipDipByAMSlot. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            dbEndTransaction();
            return al;
        }
    }

    public static ArrayList<String> getAllPostalsInPipDipByPMSlot() {
        ArrayList<String> al = null;
        try {
            al = new ArrayList<String>();
            String sqlstr = sqlStringJobs.getAllPostalsInPipDipByPMSlotSqlStr();
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    al.add(cursor.getString(cursor.getColumnIndex("POSTAL")));
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            al = null;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getAllPostalsInPipDipByPMSlot. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            dbEndTransaction();
            return al;
        }
    }

    public static ArrayList<String> getAllJobsIDXsInPipDipByPMSlot() {
        ArrayList<String> al = null;
        try {
            al = new ArrayList<String>();
            String sqlstr = sqlStringJobs.getAllJobsIDXsInPipDipByPMSlotSqlStr();
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    al.add(cursor.getString(cursor.getColumnIndex("JobsIDX")));
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            al = null;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getAllJobsIDXsInPipDipByPMSlot. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            dbEndTransaction();
            return al;
        }
    }

    public static ArrayList<String> getAllPostalsInPipDipByAOHSlot() {
        ArrayList<String> al = null;
        try {
            al = new ArrayList<String>();
            String sqlstr = sqlStringJobs.getAllPostalsInPipDipByAOHSlotSqlStr();
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    al.add(cursor.getString(cursor.getColumnIndex("POSTAL")));
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            al = null;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getAllPostalsInPipDipByPMSlot. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            dbEndTransaction();
            return al;
        }
    }

    public static ArrayList<String> getAllJobsIDXsInPipDipByAOHSlot() {
        ArrayList<String> al = null;
        try {
            al = new ArrayList<String>();
            String sqlstr = sqlStringJobs.getAllJobsIDXsInPipDipByAOHSlotSqlStr();
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    al.add(cursor.getString(cursor.getColumnIndex("JobsIDX")));
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            al = null;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getAllJobsIDXsInPipDipByAOHSlot. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            dbEndTransaction();
            return al;
        }
    }

    public static ArrayList<String> getAllPostalsInPipDipBySlot(Integer SlotFlag) {
        ArrayList<String> al = null;
        try {
            al = new ArrayList<String>();
            String sqlstr = sqlStringJobs.getAllPostalsInPipDipBySlotSqlStr(SlotFlag);
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    al.add(cursor.getString(cursor.getColumnIndex("POSTAL")));
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            al = null;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getAllPostalsInPipDipBySlot. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            dbEndTransaction();
            return al;
        }
    }

    public static List<String> getAllPostalsInParked() {
        List<String> al = null;
        try {
            al = new ArrayList<String>();
            String sqlstr = sqlStringJobs.getAllPostalsInParkedSqlStr();
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    al.add(cursor.getString(cursor.getColumnIndex("POSTAL")));
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            al = null;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getAllPostalsInParked. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            dbEndTransaction();
            return al;
        }
    }

    public static List<String> getAllPostalsInPUOKPFAIL() {
        List<String> al = null;
        try {
            al = new ArrayList<String>();
            String sqlstr = sqlStringJobs.getAllPostalsInPUOKPFAILSqlStr();
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    al.add(cursor.getString(cursor.getColumnIndex("POSTAL")));
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            al = null;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getAllPostalsInPipDipParked. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            dbEndTransaction();
            return al;
        }
    }

    public static JobQueueLite getParkedValuesForCreditsSystem(String postal) {
        JobQueueLite jql = null;
        try {
            String sqlstr = sqlStringJobs.getParkedValuesForCreditsSystemSqlStr(postal);
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    jql = new JobQueueLite();
                    jql.setJCOUNT(cursor.getInt(cursor.getColumnIndex("JCOUNT")));

                    jql.setPostal(postal);
                    jql.setAddressWithPostalNoUnit(cursor.getString(cursor.getColumnIndex("AddressWithPostalNoUnit")));
                    jql.setJobsIDXs(cursor.getString(cursor.getColumnIndex("JobsIDXs")));
                    jql.setLatitude(cursor.getDouble(cursor.getColumnIndex("LAT")));
                    jql.setLongitude(cursor.getDouble(cursor.getColumnIndex("LNG")));
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at DAO_Jobs getParkedValuesForCreditsSystem. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return jql;
        }
    }

    public static Integer[] getTotalOutstandingNumOfPipDipInidividually() {
        Integer pip = 0;
        Integer dip = 0;
        Integer[] res = null;
        try {
            String sqlstr = "SELECT (select count(*) c from jobs where cattypeid = 0) pip, (select count(*) c from jobs where cattypeid = 2) dip";
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();
            Cursor cursor = database.rawQuery(sqlstr, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                pip = cursor.getInt(cursor.getColumnIndex("pip"));
                dip = cursor.getInt(cursor.getColumnIndex("dip"));
                cursor.moveToNext();
            }
            // make sure to close the cursor
            cursor.close();
            database.setTransactionSuccessful();
            res = new Integer[]{pip, dip};
        } catch (Exception e) {
            res = null;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getTotalOutstandingNumOfPipDip. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return res;
        }
    }

    public static List<dbObjCategoryCount> getTotalJobsCountPerCatTypePerPostal_And_JobsIDXsStrList(Integer CatTypeID, String Postal, String JobsIDXs) {
        List<dbObjCategoryCount> dbObjCategoryCountAL = null;
        try {
            dbObjCategoryCountAL = new ArrayList<dbObjCategoryCount>();
            String sqlstr = sqlStringJobs.getTotalJobsCountPerCatTypePerPostal_And_JobsIDXsStrSQLstr2(CatTypeID, Postal, JobsIDXs);
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();
            Cursor cursor = database.rawQuery(sqlstr, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                dbObjCategoryCount obj = cursorTodbObjCategoryCount2(cursor);
                dbObjCategoryCountAL.add(obj);
                cursor.moveToNext();
            }
            // make sure to close the cursor
            cursor.close();
            database.setTransactionSuccessful();
        } catch (Exception e) {
            dbObjCategoryCountAL = null;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getTotalJobsCountPerCatTypePerPostal_And_JobsIDXsStr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return dbObjCategoryCountAL;
        }
    }

    public static Integer getTotalJobsCountPerCatTypePerPostal_And_JobsIDXsStr(Integer CatTypeID, String Postal, String JobsIDXs) {
        Integer totalCount = 0;
        try {
            String sqlstr = sqlStringJobs.getTotalJobsCountPerCatTypePerPostal_And_JobsIDXsStrSQLstr(CatTypeID, Postal, JobsIDXs);
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();
            Cursor cursor = database.rawQuery(sqlstr, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                totalCount = cursor.getInt(cursor.getColumnIndex("CT"));
                cursor.moveToNext();
            }
            // make sure to close the cursor
            cursor.close();
            database.setTransactionSuccessful();
        } catch (Exception e) {
            totalCount = 0;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getTotalJobsCountPerCatTypePerPostal_And_JobsIDXsStr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return totalCount;
        }
    }

    public static List<dbObjCategoryCount> getTotalJobsCountPerCatTypePerPostalList(Integer CatTypeID, String Postal) {
        List<dbObjCategoryCount> dbObjCategoryCountAL = null;
        try {
            dbObjCategoryCountAL = new ArrayList<dbObjCategoryCount>();
            String sqlstr = sqlStringJobs.getTotalJobsCountPerCatTypePerPostalSQLstr2(CatTypeID, Postal);
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();
            Cursor cursor = database.rawQuery(sqlstr, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                dbObjCategoryCount obj = cursorTodbObjCategoryCount2(cursor);
                dbObjCategoryCountAL.add(obj);
                cursor.moveToNext();
            }
            // make sure to close the cursor
            cursor.close();
            database.setTransactionSuccessful();
        } catch (Exception e) {
            dbObjCategoryCountAL = null;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getTotalJobsCountPerCatTypePerPostalList. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return dbObjCategoryCountAL;
        }
    }

    public static Integer getTotalJobsCountPerCatTypePerPostal(Integer CatTypeID, String Postal) {
        Integer totalCount = 0;
        try {
            String sqlstr = sqlStringJobs.getTotalJobsCountPerCatTypePerPostalSQLstr(CatTypeID, Postal);
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();
            Cursor cursor = database.rawQuery(sqlstr, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                totalCount = cursor.getInt(cursor.getColumnIndex("CT"));
                cursor.moveToNext();
            }
            // make sure to close the cursor
            cursor.close();
            database.setTransactionSuccessful();
        } catch (Exception e) {
            totalCount = 0;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getTotalJobsCountPerCatTypePerPostal. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return totalCount;
        }
    }

    public static Integer getTotalOriPIPDIPJobsCountByPostalSQLstr(String Postal) {
        Integer totalCount = 0;
        try {
            String sqlstr = sqlStringJobs.getTotalOriPIPDIPJobsCountByPostalSQLstr(Postal);
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();
            Cursor cursor = database.rawQuery(sqlstr, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                totalCount = cursor.getInt(cursor.getColumnIndex("CT"));
                cursor.moveToNext();
            }
            // make sure to close the cursor
            cursor.close();
            database.setTransactionSuccessful();
        } catch (Exception e) {
            totalCount = 0;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getTotalOriPIPDIPJobsCountByPostalSQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return totalCount;
        }
    }

    public static Integer getTotalStdRunJobsCountperPostal(String Postal) {
        Integer totalCount = 0;
        try {
            String sqlstr = sqlStringJobs.getTotalStdRunJobsCountperPostalSQLstr(Postal);
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();
            Cursor cursor = database.rawQuery(sqlstr, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                totalCount = cursor.getInt(cursor.getColumnIndex("CT"));
                cursor.moveToNext();
            }
            // make sure to close the cursor
            cursor.close();
            database.setTransactionSuccessful();
        } catch (Exception e) {
            totalCount = 0;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getTotalStdRunJobsCountperPostal. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return totalCount;
        }
    }

    public static Integer getTotalPIPDIPJobsCountPerPostal(String Postal) {
        Integer totalCount = 0;
        try {
            String sqlstr = sqlStringJobs.getTotalPIPDIPJobsCountPerPostalSQLstr(Postal);
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();
            Cursor cursor = database.rawQuery(sqlstr, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                totalCount = cursor.getInt(cursor.getColumnIndex("CT"));
                cursor.moveToNext();
            }
            // make sure to close the cursor
            cursor.close();
            database.setTransactionSuccessful();
        } catch (Exception e) {
            totalCount = 0;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getTotalPIPDIPJobsCountPerPostal. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return totalCount;
        }
    }

    public static Integer getTotalPIPDIPJobsCountPerPostalWithJobsIDXs(String Postal, String JobsIDXs) {
        Integer totalCount = 0;
        try {
            String sqlstr = sqlStringJobs.getTotalPIPDIPJobsCountPerPostalWithJobsIDXsSQLstr(Postal, JobsIDXs);
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();
            Cursor cursor = database.rawQuery(sqlstr, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                totalCount = cursor.getInt(cursor.getColumnIndex("CT"));
                cursor.moveToNext();
            }
            // make sure to close the cursor
            cursor.close();
            database.setTransactionSuccessful();
        } catch (Exception e) {
            totalCount = 0;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getTotalPIPDIPJobsCountPerPostalWithJobsIDXs. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return totalCount;
        }
    }

    public static Integer chkIfHasLunchTimeAvoid(String JobsIDXs) {
        Integer totalCount = 0;
        try {
            String sqlstr = sqlStringJobs.chkIfHasLunchTimeAvoid(JobsIDXs);
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();
            Cursor cursor = database.rawQuery(sqlstr, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                totalCount = cursor.getInt(cursor.getColumnIndex(sqlStringJobs.COLUMN_HASLUNCHTIME));
                cursor.moveToNext();
            }
            // make sure to close the cursor
            cursor.close();
            database.setTransactionSuccessful();
        } catch (Exception e) {
            totalCount = 0;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs chkIfHasLunchTimeAvoid. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return totalCount;
        }
    }

    public static List<dbObjTradeInList> getTradeInModelByModel(String Model, Double statedValue) {
        List<dbObjTradeInList> dbObjTradeInListAL = null;
        dbObjTradeInList[] AL = null;
        try {
            dbObjTradeInListAL = new ArrayList<dbObjTradeInList>();
            String ModelWithoutSpaces = Model.replaceAll("\\s+", "");
            ModelWithoutSpaces = ModelWithoutSpaces.replaceAll("'", "''");
            String sqlstr = sqlStringJobs.getTradeInListByModelSQLstr(ModelWithoutSpaces.trim(), statedValue);
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    dbObjTradeInList obj = cursorTodbObjTradeInList(cursor);
                    dbObjTradeInListAL.add(obj);
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            dbObjTradeInListAL = null;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getTradeInModelByModel. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return dbObjTradeInListAL;
        }
    }

    //======================================INSERT UPDATE DELETE CLEAR======================================

    public static int rtnFlagSumPerPostal(int originalflagsum, int passedflagsum) {
        try {
            if ((passedflagsum & 4) == 4 && ((originalflagsum & 4) != 4)) {
                originalflagsum += 4;
            }
            if ((passedflagsum & 8) == 8 && ((originalflagsum & 8) != 8)) {
                originalflagsum += 8;
            }
            if ((passedflagsum & 16) == 16 && ((originalflagsum & 16) != 16)) {
                originalflagsum += 16;
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at DAO_Jobs rtnFlagSumPerPostal. Exception: " + s, "", "", "", "");
        } finally {
            return originalflagsum;
        }
    }

    public static int rtnNetsVisaTIval(objDOCOD[] CODList, int NetsVisaTI) {
        try {
            for (int y = 0; y <= CODList.length - 1; y++) {
                //NetsVisaTI FLAG: 4==nets 8==visa 16==tradein
                //eg. (ji.FLAG & 8) == 8 ? 8 : (ji.FLAG & 512) == 512 ? 512 : (ji.FLAG & 1024) == 1024 ? 1024 : 0;
                NetsVisaTI += CODList[y].AmountTypeIDX == 3 && (NetsVisaTI & 4) != 4 ? 4 : CODList[y].AmountTypeIDX == 4 && (NetsVisaTI & 8) != 8 ? 8 :
                        CODList[y].AmountTypeIDX == 6 && (CODList[y].Reference != null && CODList[y].Reference != "") && (NetsVisaTI & 16) != 16 ? 16 : 0;
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at DAO_Jobs rtnNetsVisaTIval. Exception: " + s, "", "", "", "");
        } finally {
            return NetsVisaTI;
        }
    }

    public static ArrayList<objInsertJobInfo> rtnInsertJobInfoAL(ArrayList<objJobsInsertionInfo> objJobsInsertionInfoList) {
        ArrayList<objInsertJobInfo> list = new ArrayList<>();
        objInsertJobInfo iji = null;
        long JobsIDX = 0;
        long ClientIDX = 0;
        long ContactIDX = 0;
        String INDUSTRY = "";
        int ExpType = 0;
        int FLAG = 0;
        int FLAG2 = 0;
        int SlotFlag = 0;
        int NetsVisaTI = 0;
        int PCS = 0;
        long StatusCodeIDX = 0;
        int IsPartOne = 0;
        int IsDunning = 0;
        int hasDOCOD = 0;
        int hasTradeIn = 0;
        String Postal = "";
        String AddressWithPostalNoUnit = "";
        String AddressWithoutPostal = "";
        Double LAT = 0.0;
        Double LNG = 0.0;
        Long FM_AddressIDX = Long.parseLong("0");
        Long FM_ContactIDX = Long.parseLong("0");
        Long TO_AddressIDX = Long.parseLong("0");
        Long TO_ContactIDX = Long.parseLong("0");
        String ExpTypeDesc = "";
        String PartDesc = "";
        String JobNo = "";
        String CNNo = "";
        String CompanyName = "";
        String ContactName = "";
        String DO_INV_No = "";
        String DO_INV_IDX = "";
        String TAGS = "";
        String CHECKLISTS = "";
        Long[] SQLiteDateSecondsLong = null;
        Long FromDateTime = Long.parseLong("0");
        Long PickByDateTime = Long.parseLong("0");
        Long ReadyDateTime = Long.parseLong("0");
        Long ToDateTime = Long.parseLong("0");
        Long ExtDateTime = Long.parseLong("0");
        Long ExtFromDateTime = Long.parseLong("0");
        boolean ExtDateTimeisLaterDate = false;
        String JobInfoJsonStr = "";
        objContactStructure contactStructure = null;
        objJobInfo ji = null;
        Gson gson = new Gson();
        objDOCOD[] CODList = null;
        objJobsInsertionInfo JobsInsertionInfo = null;
        objJobInfo[] JobInfoAL = null;
        int CatTypeID = -1;
        int ExtCount = 0;
        int HASLUNCHTIME = 0;
        String DelAddFull = "";
        try {
            if (objJobsInsertionInfoList != null && objJobsInsertionInfoList.size() > 0) {
                for (int x = 0; x <= objJobsInsertionInfoList.size() - 1; x++) {
                    JobsInsertionInfo = objJobsInsertionInfoList.get(x);
                    JobInfoAL = JobsInsertionInfo.getJobInfoAL();
                    CatTypeID = JobsInsertionInfo.getCatTypeID();
                    if (JobInfoAL != null && JobInfoAL.length > 0) {
                        for (int i = 0; i <= JobInfoAL.length - 1; i++) {
                            NetsVisaTI = 0;
                            CODList = null;
                            Postal = "";
                            AddressWithPostalNoUnit = "";
                            AddressWithoutPostal = "";
                            LAT = 0.0;
                            LNG = 0.0;
                            FM_AddressIDX = Long.parseLong("0");
                            FM_ContactIDX = Long.parseLong("0");
                            TO_AddressIDX = Long.parseLong("0");
                            TO_ContactIDX = Long.parseLong("0");
                            IsPartOne = 0;
                            PartDesc = "";
                            IsDunning = 0;
                            CompanyName = "";
                            ContactName = "";
                            DO_INV_No = "";
                            DO_INV_IDX = "";
                            TAGS = "";
                            CHECKLISTS = "";
                            ExtCount = 0;
                            ExtDateTimeisLaterDate = false;
                            HASLUNCHTIME = 0;

                            ji = null;
                            ji = JobInfoAL[i];
                            JobsIDX = ji.JobsIDX;
                            if (JobsIDX == Long.parseLong("3333734")) {
                                JobsIDX = ji.JobsIDX;
                            }

                            ClientIDX = ji.ClientIDX;
                            ContactIDX = ji.ContactIDX;
                            INDUSTRY = ji.INDUSTRY;

                            if (CatTypeID == 0 || CatTypeID == 1 || CatTypeID == 4) {
                                ////pip/pu/fp
                                if (ji.PULunch_Avoid != null && ji.PULunch_Avoid &&
                                        ji.PULunch_From != null && !ji.PULunch_From.equals("") &&
                                        ji.PULunch_To != null && !ji.PULunch_To.equals("")) {
                                    HASLUNCHTIME = 1;
                                }

                                if (ji.PURedirectedLocation != null) {
                                    Postal = ji.PURedirectedLocation.getPOSTALCODE();
                                    LAT = ji.PURedirectedLocation.LAT;
                                    LNG = ji.PURedirectedLocation.LNG;
                                    AddressWithPostalNoUnit = objGlobalMethods.returnAddressWithPostalNoUnit(ji.PURedirectedLocation).toUpperCase();
                                    AddressWithoutPostal = objGlobalMethods.returnAddressWithoutPostal(ji.PURedirectedLocation).toUpperCase();
                                    CompanyName = ji.PURedirectedLocation.COMPANY != "" ? ji.PURedirectedLocation.COMPANY : "";
                                    contactStructure = ji.PURedirectedLocation.Contacts != null ? ji.PURedirectedLocation.Contacts[0] : null;
                                    ContactName = contactStructure != null ? contactStructure.NAME : "";
                                    FM_AddressIDX = ji.PURedirectedLocation.IDX;
                                    FM_ContactIDX = contactStructure != null ? contactStructure.IDX : 0;
                                } else if (ji.PULocation != null) {
                                    Postal = ji.PULocation.getPOSTALCODE();
                                    LAT = ji.PULocation.LAT;
                                    LNG = ji.PULocation.LNG;
                                    AddressWithPostalNoUnit = objGlobalMethods.returnAddressWithPostalNoUnit(ji.PULocation).toUpperCase();
                                    AddressWithoutPostal = objGlobalMethods.returnAddressWithoutPostal(ji.PULocation).toUpperCase();
                                    CompanyName = ji.PULocation.COMPANY != "" ? ji.PULocation.COMPANY : "";
                                    contactStructure = ji.PULocation.Contacts != null ? ji.PULocation.Contacts[0] : null;
                                    ContactName = contactStructure != null ? contactStructure.NAME : "";
                                    FM_AddressIDX = ji.PULocation.IDX;
                                    FM_ContactIDX = contactStructure != null ? contactStructure.IDX : 0;
                                }

                                if (ji.DLRedirectedLocation != null) {
                                    contactStructure = ji.DLRedirectedLocation.Contacts != null ? ji.DLRedirectedLocation.Contacts[0] : null;
                                    TO_AddressIDX = ji.DLRedirectedLocation.IDX;
                                    TO_ContactIDX = contactStructure != null ? contactStructure.IDX : 0;
                                    DelAddFull = objGlobalMethods.returnFullAddress(ji.DLRedirectedLocation);
                                } else if (ji.DLLocation != null) {
                                    contactStructure = ji.DLLocation.Contacts != null ? ji.DLLocation.Contacts[0] : null;
                                    TO_AddressIDX = ji.DLLocation.IDX;
                                    TO_ContactIDX = contactStructure != null ? contactStructure.IDX : 0;
                                    DelAddFull = objGlobalMethods.returnFullAddress(ji.DLLocation);
                                }
                            } else if (CatTypeID == 2 || CatTypeID == 3 || CatTypeID == 5 || CatTypeID == 6 || CatTypeID == 7) {
                                ////dip/okp/fd/pd/tt
                                if (ji.DLLunch_Avoid != null && ji.DLLunch_Avoid &&
                                        ji.DLLunch_From != null && !ji.DLLunch_From.equals("") &&
                                        ji.DLLunch_To != null && !ji.DLLunch_To.equals("")) {
                                    HASLUNCHTIME = 1;
                                }

                                if (ji.DLRedirectedLocation != null) {
                                    Postal = ji.DLRedirectedLocation.getPOSTALCODE();
                                    LAT = ji.DLRedirectedLocation.LAT;
                                    LNG = ji.DLRedirectedLocation.LNG;
                                    AddressWithPostalNoUnit = objGlobalMethods.returnAddressWithPostalNoUnit(ji.DLRedirectedLocation).toUpperCase();
                                    AddressWithoutPostal = objGlobalMethods.returnAddressWithoutPostal(ji.DLRedirectedLocation).toUpperCase();
                                    CompanyName = ji.DLRedirectedLocation.COMPANY != "" ? ji.DLRedirectedLocation.COMPANY : "";
                                    contactStructure = ji.DLRedirectedLocation.Contacts != null ? ji.DLRedirectedLocation.Contacts[0] : null;
                                    ContactName = contactStructure != null ? contactStructure.NAME : "";
                                    TO_AddressIDX = ji.DLRedirectedLocation.IDX;
                                    TO_ContactIDX = contactStructure != null ? contactStructure.IDX : 0;
                                } else if (ji.DLLocation != null) {
                                    Postal = ji.DLLocation.getPOSTALCODE();
                                    LAT = ji.DLLocation.LAT;
                                    LNG = ji.DLLocation.LNG;
                                    AddressWithPostalNoUnit = objGlobalMethods.returnAddressWithPostalNoUnit(ji.DLLocation).toUpperCase();
                                    AddressWithoutPostal = objGlobalMethods.returnAddressWithoutPostal(ji.DLLocation).toUpperCase();
                                    CompanyName = ji.DLLocation.COMPANY != "" ? ji.DLLocation.COMPANY : "";
                                    contactStructure = ji.DLLocation.Contacts != null ? ji.DLLocation.Contacts[0] : null;
                                    ContactName = contactStructure != null ? contactStructure.NAME : "";
                                    TO_AddressIDX = ji.DLLocation.IDX;
                                    TO_ContactIDX = contactStructure != null ? contactStructure.IDX : 0;
                                }

                                if (ji.PURedirectedLocation != null) {
                                    contactStructure = ji.PURedirectedLocation.Contacts != null ? ji.PURedirectedLocation.Contacts[0] : null;
                                    FM_AddressIDX = ji.PURedirectedLocation.IDX;
                                    FM_ContactIDX = contactStructure != null ? contactStructure.IDX : 0;
                                } else if (ji.PULocation != null) {
                                    contactStructure = ji.PULocation.Contacts != null ? ji.PULocation.Contacts[0] : null;
                                    FM_AddressIDX = ji.PULocation.IDX;
                                    FM_ContactIDX = contactStructure != null ? contactStructure.IDX : 0;
                                }
                            }

                            ExpType = ji.ExpressType;
                            if (ji.ParentJobsIDX > 0) {
                                //Part = " - Part 2";
                                PartDesc = " - " + myApp.getAppContext().getString(R.string.part2);
                                //IsPartOne = 0;
                                IsPartOne = ji.LinkedJobs != null && ji.LinkedJobs.length > 0 ? 1 : 0;
                            } else if (ji.ParentJobsIDX == 0) {
                                if (ji.LinkedJobs != null) {
                                    if (ji.LinkedJobs.length > 0) {
                                        //Part = " - Part 1";
                                        PartDesc = " - " + myApp.getAppContext().getString(R.string.part1);
                                        IsPartOne = 1;
                                    }
                                }
                            }

                            if ((ji.ClientIDX == 14753 || ji.ClientIDX == 4868) && ji.DeliveryContents.toUpperCase().equals("DUNNING DOCUMENT")) {
                                IsDunning = 1;
                            }

                            JobNo = ji.JobNo != "" ? ji.JobNo : "";
                            CNNo = ji.ConsignmentNote != "" ? ji.ConsignmentNote : "";
                            SQLiteDateSecondsLong = objGlobalMethods.returnAllDatesSQLiteLong(ji);
                            FromDateTime = SQLiteDateSecondsLong[0] != null ? SQLiteDateSecondsLong[0] : Long.parseLong("0");
                            PickByDateTime = SQLiteDateSecondsLong[1] != null ? SQLiteDateSecondsLong[1] : Long.parseLong("0");
                            ReadyDateTime = SQLiteDateSecondsLong[2] != null ? SQLiteDateSecondsLong[2] : Long.parseLong("0");
                            ToDateTime = SQLiteDateSecondsLong[3] != null ? SQLiteDateSecondsLong[3] : Long.parseLong("0");
                            ExtDateTime = SQLiteDateSecondsLong[4] != null ? SQLiteDateSecondsLong[4] : Long.parseLong("0");
                            ExtFromDateTime = SQLiteDateSecondsLong[5] != null ? SQLiteDateSecondsLong[5] : Long.parseLong("0");
                            //FLAG = (ji.FLAG & 8) == 8 ? 8 : (ji.FLAG & 512) == 512 ? 512 : (ji.FLAG & 1024) == 1024 ? 1024 : 0;
                            FLAG = ji.FLAG == null ? 0 : ji.FLAG;
                            FLAG2 = ji.FLAG2 == null ? 0 : ji.FLAG2;
                            SlotFlag = ji.SlotFlag;
                            PCS = ji.getPieces();
                            StatusCodeIDX = ji.StatusCodeIDX;

                            if (ji.DOList != null && ji.DOList.length > 0) {
                                for (int d = 0; d <= ji.DOList.length - 1; d++) {
                                    DO_INV_No += "[" + String.valueOf(ji.DOList[d].getInvoice()).toUpperCase() + "]";
                                    DO_INV_IDX += "[" + String.valueOf(ji.DOList[d].getDOIDX()).toUpperCase() + "]";
                                    CODList = ji.DOList[d].CODList;
                                    if (CODList != null && CODList.length > 0) {
                                        NetsVisaTI = rtnNetsVisaTIval(CODList, NetsVisaTI);
                                    }
                                }
                            } else {
                                DO_INV_No = "";
                                DO_INV_IDX = "";
                                NetsVisaTI = 0;
                            }

                            if (ji.BarcodeTags != null && ji.BarcodeTags.length > 0) {
                                for (int d = 0; d <= ji.BarcodeTags.length - 1; d++) {
                                    TAGS += "[" + ji.BarcodeTags[d].toUpperCase() + "]";
                                }
                            } else {
                                TAGS = "";
                            }

                            if (ji.CheckList != null && ji.CheckList.length > 0) {
                                for (int d = 0; d <= ji.CheckList.length - 1; d++) {
                                    CHECKLISTS += "[" + ji.CheckList[d].toUpperCase() + "]";
                                }
                            } else {
                                CHECKLISTS = "";
                            }

                            hasDOCOD = JobsHandler.checkforDOCOD(ji) ? 1 : 0;
                            hasTradeIn = JobsHandler.hasTradeIn(ji) ? 1 : 0;
                            ExtCount = ji.ExtCount;

                            if (CatTypeID == 2 && ExtDateTime != null && ExtDateTime > 0 && ExtCount > 0) {
                                ExtDateTimeisLaterDate = JobsHandler.isLaterDate(ExtDateTime * 1000L, 1);
                                if (ExtDateTimeisLaterDate) {
                                    ExtCount = 0;
                                }
                            }

                            JobInfoJsonStr = gson.toJson(ji);

                            iji = new objInsertJobInfo(JobsIDX, ClientIDX, ContactIDX, INDUSTRY, CatTypeID, Postal, AddressWithPostalNoUnit, AddressWithoutPostal, LAT, LNG,
                                    FM_AddressIDX, FM_ContactIDX, TO_AddressIDX, TO_ContactIDX, ExpType, objGlobalVariables.returnExpTypeDESC(ExpType), IsPartOne, PartDesc,
                                    IsDunning, JobNo, CNNo, CompanyName, ContactName, DO_INV_No, DO_INV_IDX, TAGS,
                                    FromDateTime, PickByDateTime, ReadyDateTime, ToDateTime, ExtDateTime, FLAG, PCS, StatusCodeIDX,
                                    JobInfoJsonStr, hasDOCOD, hasTradeIn, CHECKLISTS, NetsVisaTI, ExtCount, FLAG2, HASLUNCHTIME, DelAddFull, SlotFlag);
                            iji.setExtFromDateTime(ExtFromDateTime);
                            list.add(iji);
                        }
                    }
                }
            }
        } catch (Exception e) {
            list = null;
            objGlobalMethods.writeToFile("Exception at DAO_Jobs rtnInsertJobInfoAL. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return list;
        }
    }

    public static void insertJobsBatch3(ArrayList<objInsertJobInfo> list) {
        int insertedCount = 0;
        objInsertJobInfo iji;
        try {
            if (list != null && list.size() > 0) {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                SQLiteStatement stmt = database.compileStatement(sqlStringJobs.getInsertJobInfoSQLstr());
                for (int i = 0; i <= list.size() - 1; i++) {
                    iji = list.get(i);
                    stmt.bindLong(1, iji.JobsIDX); //COLUMN_JobsIDX
                    stmt.bindLong(2, iji.ClientIDX); //COLUMN_ClientIDX
                    stmt.bindLong(3, iji.ContactIDX); //COLUMN_ContactIDX
                    stmt.bindString(4, iji.INDUSTRY); //COLUMN_INDUSTRY
                    stmt.bindLong(5, iji.CatTypeID); //COLUMN_CatTypeID
                    stmt.bindString(6, iji.Postal); //COLUMN_Postal
                    stmt.bindString(7, iji.AddressWithPostalNoUnit); //COLUMN_AddressWithPostalNoUnit
                    stmt.bindString(8, iji.AddressWithoutPostal); //COLUMN_AddressWithoutPostal
                    stmt.bindDouble(9, iji.LAT); //COLUMN_LAT
                    stmt.bindDouble(10, iji.LNG); //COLUMN_LNG
                    stmt.bindDouble(11, iji.FM_AddressIDX); //COLUMN_FM_AddressIDX
                    stmt.bindDouble(12, iji.FM_ContactIDX); //COLUMN_FM_ContactIDX
                    stmt.bindDouble(13, iji.TO_AddressIDX); //COLUMN_TO_AddressIDX
                    stmt.bindDouble(14, iji.TO_ContactIDX); //COLUMN_TO_ContactIDX
                    stmt.bindLong(15, iji.ExpType); //COLUMN_ExpType
                    stmt.bindString(16, iji.ExpTypeDESC); //COLUMN_ExpTypeDesc
                    stmt.bindLong(17, iji.IsPartOne); //COLUMN_IsPartOne
                    stmt.bindString(18, iji.PartDesc); //COLUMN_PartDesc
                    stmt.bindLong(19, iji.IsDunning); //COLUMN_IsDunning
                    stmt.bindString(20, iji.JobNo); //COLUMN_JobNo
                    stmt.bindString(21, iji.CNNo); //COLUMN_CNNo
                    stmt.bindString(22, iji.CompanyName); //COLUMN_CompanyName
                    stmt.bindString(23, iji.ContactName); //COLUMN_ContactName
                    stmt.bindString(24, iji.DO_INV_No); //COLUMN_DO_INV_No
                    stmt.bindString(25, iji.DO_INV_IDX); //COLUMN_DO_INV_No
                    stmt.bindString(26, iji.TAGS); //COLUMN_TAGS
                    stmt.bindLong(27, iji.FromDateTime); //COLUMN_FromDateTime
                    stmt.bindLong(28, iji.PickByDateTime); //COLUMN_PickByDateTime
                    stmt.bindLong(29, iji.ReadyDateTime); //COLUMN_ReadyDateTime
                    stmt.bindLong(30, iji.ToDateTime); //COLUMN_ToDateTime
                    stmt.bindLong(31, iji.ExtDateTime); //COLUMN_ExtDateTime
                    stmt.bindLong(32, iji.FLAG); //COLUMN_FLAG
                    stmt.bindLong(33, iji.PCS); //COLUMN_PCS
                    stmt.bindLong(34, iji.StatusCodeIDX); //COLUMN_StatusCodeIDX
                    stmt.bindString(35, iji.JobInfoJsonStr); //COLUMN_JobInfoJsonStr
                    stmt.bindLong(36, iji.hasDOCOD); //COLUMN_HasDOCOD
                    stmt.bindLong(37, iji.hasTradeIn); //COLUMN_HasTradeIn
                    stmt.bindString(38, iji.CHECKLISTS); //COLUMN_CHECKLISTS
                    stmt.bindLong(39, iji.NetsVisaTI); //COLUMN_NetsVisaTI
                    stmt.bindLong(40, iji.ExtCount); //COLUMN_ExtCount
                    stmt.bindLong(41, iji.FLAG2); //COLUMN_FLAG2
                    stmt.bindLong(42, iji.SlotFlag);
                    stmt.bindLong(43, iji.HASLUNCHTIME); //COLUMN_HASLUNCHTIME
                    stmt.bindLong(44, iji.ExtFromDateTime); //COLUMN_ExtFromDateTime
                    stmt.bindString(45, iji.DelAddFull); //COLUMN_DelAddFull

                    long entryID = stmt.executeInsert();
                    stmt.clearBindings();
                    insertedCount++;
                }
            }
            database.setTransactionSuccessful();
            objGlobalMethods.writeToFile("insertJobsBatch3 completed. Total inserted: " + String.valueOf(insertedCount), "", "", "", "");
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs insertJobsBatch3. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
        }
    }

    public static void insertJobs(objJobInfo ji, int CatTypeID, String aJobInfoJsonStr) {
        long JobsIDX = 0;
        long ClientIDX = 0;
        long ContactIDX = 0;
        String INDUSTRY = "";
        int ExpType = 0;
        int FLAG = 0;
        int FLAG2 = 0;
        int SlotFlag = 0;
        int NetsVisaTI = 0;
        int PCS = 0;
        long StatusCodeIDX = 0;
        int IsPartOne = 0;
        int IsDunning = 0;
        int hasDOCOD = 0;
        int hasTradeIn = 0;
        String Postal = "";
        String AddressWithPostalNoUnit = "";
        String AddressWithoutPostal = "";
        Double LAT = 0.0;
        Double LNG = 0.0;
        Long FM_AddressIDX = Long.parseLong("0");
        Long FM_ContactIDX = Long.parseLong("0");
        Long TO_AddressIDX = Long.parseLong("0");
        Long TO_ContactIDX = Long.parseLong("0");
        String ExpTypeDesc = "";
        String PartDesc = "";
        String JobNo = "";
        String CNNo = "";
        String CompanyName = "";
        String ContactName = "";
        String DO_INV_No = "";
        String DO_INV_IDX = "";
        String TAGS = "";
        String CHECKLISTS = "";
        Long[] SQLiteDateSecondsLong = null;
        Long FromDateTime = Long.parseLong("0");
        Long PickByDateTime = Long.parseLong("0");
        Long ReadyDateTime = Long.parseLong("0");
        Long ToDateTime = Long.parseLong("0");
        Long ExtDateTime = Long.parseLong("0");
        String JobInfoJsonStr = "";
        objContactStructure contactStructure = null;
        objDOCOD[] CODList = null;
        String DelAddFull = "";

        try {
            if (ji != null) {
                JobsIDX = ji.JobsIDX;
                ClientIDX = ji.ClientIDX;
                ContactIDX = ji.ContactIDX;
                INDUSTRY = ji.INDUSTRY;

                if (CatTypeID == 0 || CatTypeID == 1 || CatTypeID == 4) {
                    ////pip/pu/fp
                    if (ji.PURedirectedLocation != null) {
                        Postal = ji.PURedirectedLocation.getPOSTALCODE();
                        LAT = ji.PURedirectedLocation.LAT;
                        LNG = ji.PURedirectedLocation.LNG;
                        AddressWithPostalNoUnit = objGlobalMethods.returnAddressWithPostalNoUnit(ji.PURedirectedLocation).toUpperCase();
                        AddressWithoutPostal = objGlobalMethods.returnAddressWithoutPostal(ji.PURedirectedLocation).toUpperCase();
                        CompanyName = ji.PURedirectedLocation.COMPANY != "" ? ji.PURedirectedLocation.COMPANY : "";
                        contactStructure = ji.PURedirectedLocation.Contacts != null ? ji.PURedirectedLocation.Contacts[0] : null;
                        ContactName = contactStructure != null ? contactStructure.NAME : "";
                        FM_AddressIDX = ji.PURedirectedLocation.IDX;
                        FM_ContactIDX = contactStructure != null ? contactStructure.IDX : 0;

                    } else if (ji.PULocation != null) {
                        Postal = ji.PULocation.getPOSTALCODE();
                        LAT = ji.PULocation.LAT;
                        LNG = ji.PULocation.LNG;
                        AddressWithPostalNoUnit = objGlobalMethods.returnAddressWithPostalNoUnit(ji.PULocation).toUpperCase();
                        AddressWithoutPostal = objGlobalMethods.returnAddressWithoutPostal(ji.PULocation).toUpperCase();
                        CompanyName = ji.PULocation.COMPANY != "" ? ji.PULocation.COMPANY : "";
                        contactStructure = ji.PULocation.Contacts != null ? ji.PULocation.Contacts[0] : null;
                        ContactName = contactStructure != null ? contactStructure.NAME : "";
                        FM_AddressIDX = ji.PULocation.IDX;
                        FM_ContactIDX = contactStructure != null ? contactStructure.IDX : 0;
                    }

                    if (ji.DLRedirectedLocation != null) {
                        contactStructure = ji.DLRedirectedLocation.Contacts != null ? ji.DLRedirectedLocation.Contacts[0] : null;
                        TO_AddressIDX = ji.DLRedirectedLocation.IDX;
                        TO_ContactIDX = contactStructure != null ? contactStructure.IDX : 0;
                        DelAddFull = objGlobalMethods.returnFullAddress(ji.DLRedirectedLocation);

                    } else if (ji.DLLocation != null) {
                        contactStructure = ji.DLLocation.Contacts != null ? ji.DLLocation.Contacts[0] : null;
                        TO_AddressIDX = ji.DLLocation.IDX;
                        TO_ContactIDX = contactStructure != null ? contactStructure.IDX : 0;
                        DelAddFull = objGlobalMethods.returnFullAddress(ji.DLLocation);
                    }

                } else if (CatTypeID == 2 || CatTypeID == 3 || CatTypeID == 5 || CatTypeID == 6 || CatTypeID == 7) {
                    ////dip/okp/fd/pd/transit
                    if (ji.DLRedirectedLocation != null) {
                        Postal = ji.DLRedirectedLocation.getPOSTALCODE();
                        LAT = ji.DLRedirectedLocation.LAT;
                        LNG = ji.DLRedirectedLocation.LNG;
                        AddressWithPostalNoUnit = objGlobalMethods.returnAddressWithPostalNoUnit(ji.DLRedirectedLocation).toUpperCase();
                        AddressWithoutPostal = objGlobalMethods.returnAddressWithoutPostal(ji.DLRedirectedLocation).toUpperCase();
                        CompanyName = ji.DLRedirectedLocation.COMPANY != "" ? ji.DLRedirectedLocation.COMPANY : "";
                        contactStructure = ji.DLRedirectedLocation.Contacts != null ? ji.DLRedirectedLocation.Contacts[0] : null;
                        ContactName = contactStructure != null ? contactStructure.NAME : "";
                        TO_AddressIDX = ji.DLRedirectedLocation.IDX;
                        TO_ContactIDX = contactStructure != null ? contactStructure.IDX : 0;
                    } else if (ji.DLLocation != null) {
                        Postal = ji.DLLocation.getPOSTALCODE();
                        LAT = ji.DLLocation.LAT;
                        LNG = ji.DLLocation.LNG;
                        AddressWithPostalNoUnit = objGlobalMethods.returnAddressWithPostalNoUnit(ji.DLLocation).toUpperCase();
                        AddressWithoutPostal = objGlobalMethods.returnAddressWithoutPostal(ji.DLLocation).toUpperCase();
                        CompanyName = ji.DLLocation.COMPANY != "" ? ji.DLLocation.COMPANY : "";
                        contactStructure = ji.DLLocation.Contacts != null ? ji.DLLocation.Contacts[0] : null;
                        ContactName = contactStructure != null ? contactStructure.NAME : "";
                        TO_AddressIDX = ji.DLLocation.IDX;
                        TO_ContactIDX = contactStructure != null ? contactStructure.IDX : 0;
                    }

                    if (ji.PURedirectedLocation != null) {
                        contactStructure = ji.PURedirectedLocation.Contacts != null ? ji.PURedirectedLocation.Contacts[0] : null;
                        FM_AddressIDX = ji.PURedirectedLocation.IDX;
                        FM_ContactIDX = contactStructure != null ? contactStructure.IDX : 0;
                    } else if (ji.PULocation != null) {
                        contactStructure = ji.PULocation.Contacts != null ? ji.PULocation.Contacts[0] : null;
                        FM_AddressIDX = ji.PULocation.IDX;
                        FM_ContactIDX = contactStructure != null ? contactStructure.IDX : 0;
                    }
                }

                ExpType = ji.ExpressType;
                if (ji.ParentJobsIDX > 0) {
                    //Part = " - Part 2";
                    PartDesc = " - " + myApp.getAppContext().getString(R.string.part2);
                    IsPartOne = ji.LinkedJobs != null && ji.LinkedJobs.length > 0 ? 1 : 0;
                } else if (ji.ParentJobsIDX == 0) {
                    if (ji.LinkedJobs != null) {
                        if (ji.LinkedJobs.length > 0) {
                            //Part = " - Part 1";
                            PartDesc = " - " + myApp.getAppContext().getString(R.string.part1);
                            IsPartOne = 1;
                        }
                    }
                }

                if ((ji.ClientIDX == 14753 || ji.ClientIDX == 4868) && ji.DeliveryContents.toUpperCase().equals("DUNNING DOCUMENT")) {
                    IsDunning = 1;
                }

                JobNo = ji.JobNo != "" ? ji.JobNo : "";
                CNNo = ji.ConsignmentNote != "" ? ji.ConsignmentNote : "";
                SQLiteDateSecondsLong = objGlobalMethods.returnAllDatesSQLiteLong(ji);
                FromDateTime = SQLiteDateSecondsLong[0] != null ? SQLiteDateSecondsLong[0] : Long.parseLong("0");
                PickByDateTime = SQLiteDateSecondsLong[1] != null ? SQLiteDateSecondsLong[1] : Long.parseLong("0");
                ReadyDateTime = SQLiteDateSecondsLong[2] != null ? SQLiteDateSecondsLong[2] : Long.parseLong("0");
                ToDateTime = SQLiteDateSecondsLong[3] != null ? SQLiteDateSecondsLong[3] : Long.parseLong("0");
                ExtDateTime = SQLiteDateSecondsLong[4] != null ? SQLiteDateSecondsLong[4] : Long.parseLong("0");
                //FLAG = (ji.FLAG & 8) == 8 ? 8 : (ji.FLAG & 512) == 512 ? 512 : (ji.FLAG & 1024) == 1024 ? 1024 : 0;
                FLAG = ji.FLAG == null ? 0 : ji.FLAG;
                FLAG2 = ji.FLAG2 == null ? 0 : ji.FLAG2;
                SlotFlag = ji.SlotFlag;
                PCS = ji.getPieces();
                JobInfoJsonStr = aJobInfoJsonStr;
                StatusCodeIDX = ji.StatusCodeIDX;

                if (ji.DOList != null && ji.DOList.length > 0) {
                    for (int d = 0; d <= ji.DOList.length - 1; d++) {
                        DO_INV_No += "[" + String.valueOf(ji.DOList[d].getInvoice()).toUpperCase() + "]";
                        DO_INV_IDX += "[" + String.valueOf(ji.DOList[d].getDOIDX()).toUpperCase() + "]";
                        CODList = ji.DOList[d].CODList;
                        if (CODList != null && CODList.length > 0) {
                            NetsVisaTI = rtnNetsVisaTIval(CODList, NetsVisaTI);
                        }
                    }
                } else {
                    DO_INV_No = "";
                    DO_INV_IDX = "";
                    NetsVisaTI = 0;
                }

                if (ji.BarcodeTags != null && ji.BarcodeTags.length > 0) {
                    for (int d = 0; d <= ji.BarcodeTags.length - 1; d++) {
                        TAGS += "[" + ji.BarcodeTags[d].toUpperCase() + "]";
                    }
                } else {
                    TAGS = "";
                }

                if (ji.CheckList != null && ji.CheckList.length > 0) {
                    for (int d = 0; d <= ji.CheckList.length - 1; d++) {
                        CHECKLISTS += "[" + ji.CheckList[d].toUpperCase() + "]";
                    }
                } else {
                    CHECKLISTS = "";
                }

                hasDOCOD = JobsHandler.checkforDOCOD(ji) ? 1 : 0;
                hasTradeIn = JobsHandler.hasTradeIn(ji) ? 1 : 0;

                ContentValues values = new ContentValues();
                values.put(sqlStringJobs.COLUMN_JobsIDX, JobsIDX);
                values.put(sqlStringJobs.COLUMN_ClientIDX, ClientIDX);
                values.put(sqlStringJobs.COLUMN_ContactIDX, ContactIDX);
                values.put(sqlStringJobs.COLUMN_INDUSTRY, INDUSTRY);
                values.put(sqlStringJobs.COLUMN_CatTypeID, CatTypeID);
                values.put(sqlStringJobs.COLUMN_Postal, Postal);
                values.put(sqlStringJobs.COLUMN_AddressWithPostalNoUnit, AddressWithPostalNoUnit);
                values.put(sqlStringJobs.COLUMN_AddressWithoutPostal, AddressWithoutPostal);
                values.put(sqlStringJobs.COLUMN_LAT, LAT);
                values.put(sqlStringJobs.COLUMN_LNG, LNG);
                values.put(sqlStringJobs.COLUMN_FM_AddressIDX, FM_AddressIDX);
                values.put(sqlStringJobs.COLUMN_FM_ContactIDX, FM_ContactIDX);
                values.put(sqlStringJobs.COLUMN_TO_AddressIDX, TO_AddressIDX);
                values.put(sqlStringJobs.COLUMN_TO_ContactIDX, TO_ContactIDX);
                values.put(sqlStringJobs.COLUMN_ExpType, ExpType);
                values.put(sqlStringJobs.COLUMN_ExpTypeDesc, objGlobalVariables.returnExpTypeDESC(ExpType));
                values.put(sqlStringJobs.COLUMN_IsPartOne, IsPartOne);
                values.put(sqlStringJobs.COLUMN_PartDesc, PartDesc);
                values.put(sqlStringJobs.COLUMN_IsDunning, IsDunning);
                values.put(sqlStringJobs.COLUMN_JobNo, JobNo);
                values.put(sqlStringJobs.COLUMN_CNNo, CNNo);
                values.put(sqlStringJobs.COLUMN_CompanyName, CompanyName);
                values.put(sqlStringJobs.COLUMN_ContactName, ContactName);
                values.put(sqlStringJobs.COLUMN_DO_INV_No, DO_INV_No);
                values.put(sqlStringJobs.COLUMN_DO_INV_IDX, DO_INV_IDX);
                values.put(sqlStringJobs.COLUMN_TAGS, TAGS);
                values.put(sqlStringJobs.COLUMN_FromDateTime, FromDateTime);
                values.put(sqlStringJobs.COLUMN_PickByDateTime, PickByDateTime);
                values.put(sqlStringJobs.COLUMN_ReadyDateTime, ReadyDateTime);
                values.put(sqlStringJobs.COLUMN_ToDateTime, ToDateTime);
                values.put(sqlStringJobs.COLUMN_ExtDateTime, ExtDateTime);
                values.put(sqlStringJobs.COLUMN_FLAG, FLAG);
                values.put(sqlStringJobs.COLUMN_FLAG2, FLAG2);
                values.put(sqlStringJobs.COLUMN_SlotFlag, SlotFlag);
                values.put(sqlStringJobs.COLUMN_PCS, PCS);
                values.put(sqlStringJobs.COLUMN_StatusCodeIDX, StatusCodeIDX);
                values.put(sqlStringJobs.COLUMN_JobInfoJsonStr, JobInfoJsonStr);
                values.put(sqlStringJobs.COLUMN_HasDOCOD, hasDOCOD);
                values.put(sqlStringJobs.COLUMN_HasTradeIn, hasTradeIn);
                values.put(sqlStringJobs.COLUMN_CHECKLISTS, CHECKLISTS);
                values.put(sqlStringJobs.COLUMN_NetsVisaTI, NetsVisaTI);
                values.put(sqlStringJobs.COLUMN_HASLUNCHTIME, ji.HASLUNCHTIME);
                values.put(sqlStringJobs.COLUMN_DelAddFull, DelAddFull);

                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                int isdeleted = database.delete(sqlStringJobs.TABLE_NAME, sqlStringJobs.COLUMN_JobsIDX + " = " + JobsIDX, null);
                long insertId = database.insert(sqlStringJobs.TABLE_NAME, null, values);
                Boolean isinserted = false;
                //source taken from http://www.vogella.com/tutorials/AndroidSQLite/article.html
                database.setTransactionSuccessful();
                if (insertId > 0) {
                    isinserted = true;
                    //objGlobalMethods.writeToFile("insertJobs completed. JobsIDX: " + String.valueOf(JobsIDX),"","","","");
                }
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs insertJobs. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
        }
    }

    public static void insertTradeInList(dbObjTradeInList[] list) {

        try {
            if (list != null && list.length > 0) {
                deleteAllTradeInModels();
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                SQLiteStatement stmt = database.compileStatement(sqlStringJobs.getInsertTradeInListSQLstr());
                for (int i = 0; i <= list.length - 1; i++) {
                    dbObjTradeInList obj = list[i];
                    stmt.bindLong(1, obj.ClientIDX);
                    stmt.bindString(2, obj.Brand);
                    stmt.bindString(3, obj.Model);
                    stmt.bindDouble(4, obj.Value);
                    long entryID = stmt.executeInsert();
                    stmt.clearBindings();
                }
                database.setTransactionSuccessful();
                //objGlobalMethods.writeToFile("insertTradeInList completed successfully.", "", "", "", "");
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs insertTradeInList. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
        }
    }

    public static void insertNFCTAGS(NFCTag[] list) {
        try {
            if (list != null && list.length > 0) {
                deleteAllNFCTAGS();
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                SQLiteStatement stmt = database.compileStatement(sqlStringJobs.getInsertNFCTagsSQLstr());
                for (int i = 0; i <= list.length - 1; i++) {
                    NFCTag obj = list[i];
                    stmt.bindLong(1, obj.CAIDX1);
                    stmt.bindLong(2, obj.CNIDX1);
                    stmt.bindLong(3, obj.CAIDX2);
                    stmt.bindLong(4, obj.CNIDX2);
                    stmt.bindString(5, obj.UID);
                    long entryID = stmt.executeInsert();
                    stmt.clearBindings();
                }
                stmt.bindLong(1, Long.parseLong("1257605"));
                stmt.bindLong(2, Long.parseLong("0"));
                stmt.bindLong(3, Long.parseLong("1257605"));
                stmt.bindLong(4, Long.parseLong("0"));
                stmt.bindString(5, "04F7103AE74C84");
                long entryID = stmt.executeInsert();
                stmt.clearBindings();

                stmt.bindLong(1, Long.parseLong("989949"));
                stmt.bindLong(2, Long.parseLong("0"));
                stmt.bindLong(3, Long.parseLong("989949"));
                stmt.bindLong(4, Long.parseLong("0"));
                stmt.bindString(5, "04AA0C42E74C84");
                long entryID2 = stmt.executeInsert();
                stmt.clearBindings();

                database.setTransactionSuccessful();
                //objGlobalMethods.writeToFile("insertNFCTAGS completed successfully.", "", "", "", "");
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs insertNFCTAGS. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
        }
    }

    //=================================================Messages=================================================

    public static void insertChatMsgSenders2(ChatMsgSenders cms) {
        try {
            if (cms != null) {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                SQLiteStatement stmt = database.compileStatement(sqlStringJobs.getInsertChatMsgSendersSQLstr2());
                stmt.bindLong(1, Long.parseLong(cms.UserIDX));
                stmt.bindString(2, cms.UserName);
                stmt.bindString(3, cms.messageTime);
                stmt.bindLong(4, cms.Read ? 1 : 0);
                stmt.bindLong(5, cms.notiID);
                stmt.bindLong(6, cms.msgDateLong);
                stmt.bindLong(7, Long.parseLong(cms.JobsIDX));
                long entryID = stmt.executeInsert();
                stmt.clearBindings();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs insertChatMsgSenders2. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
        }
    }

    public static long insertSentChatMessage(objChatMessage msg) {
        Long TimeStamp = Long.parseLong("0");
        long entryID = 0;
        try {
            if (msg != null) {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                SQLiteStatement stmt = database.compileStatement(sqlStringJobs.getInsertChatMessageSQLstr());
                TimeStamp = Long.parseLong(msg.TimeStamp);
                stmt.bindLong(1, TimeStamp);
                stmt.bindLong(2, msg.IDX);
                stmt.bindLong(3, msg.UserIDX);
                stmt.bindString(4, msg.UserName);
                stmt.bindLong(5, msg.JobsIDX);
                stmt.bindString(6, msg.Message);
                stmt.bindLong(7, msg.Direction);
                stmt.bindLong(8, msg.MessageStatus);
                stmt.bindLong(9, msg.UserType);
                stmt.bindLong(10, msg.ackIDX);
                entryID = stmt.executeInsert();
                stmt.clearBindings();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            entryID = 0;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs insertSentChatMessage. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return entryID;
        }
    }

    public static void insertChatMessage(objChatMessage msg) {
        Long TimeStamp = Long.parseLong("0");
        try {
            if (msg != null) {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                SQLiteStatement stmt = database.compileStatement(sqlStringJobs.getInsertChatMessageSQLstr());
                TimeStamp = Long.parseLong(msg.TimeStamp);
                stmt.bindLong(1, TimeStamp);
                stmt.bindLong(2, msg.IDX);
                stmt.bindLong(3, msg.UserIDX);
                stmt.bindString(4, msg.UserName);
                stmt.bindLong(5, msg.JobsIDX);
                stmt.bindString(6, msg.Message);
                stmt.bindLong(7, msg.Direction);
                stmt.bindLong(8, msg.MessageStatus);
                stmt.bindLong(9, msg.UserType);
                stmt.bindLong(10, msg.ackIDX);
                long entryID = stmt.executeInsert();
                stmt.clearBindings();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs insertChatMessage. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
        }
    }

    public static void insertChatMessagesBatch(objChatMessage[] list) {
        Long TimeStamp = Long.parseLong("0");
        List<Long> listDataHeader = new ArrayList<Long>();
        HashMap<Long, List<Long>> uidxjidx = new HashMap<>();

        ArrayList<Long> headerAl = new ArrayList<>();
        ArrayList<Long> jobsidxal;
        ArrayList itemAL = new ArrayList();
        ArrayList<Long> useridxal = new ArrayList();
        int headerALselectedIDX = 0;
        try {
            if (list != null && list.length > 0) {
                deleteAllChatMsgSenders();
                deleteAllChatMessages();
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                SQLiteStatement stmt = database.compileStatement(sqlStringJobs.getInsertChatMessageSQLstr());
                for (int i = 0; i <= list.length - 1; i++) {
                    objChatMessage msg = list[i];
                    TimeStamp = objGlobalMethods.DateToLong(msg.TimeStamp);
                    stmt.bindLong(1, TimeStamp);
                    stmt.bindLong(2, msg.IDX);
                    stmt.bindLong(3, msg.UserIDX);
                    stmt.bindString(4, msg.UserName);
                    stmt.bindLong(5, msg.JobsIDX);
                    stmt.bindString(6, msg.Message);
                    stmt.bindLong(7, msg.Direction);
                    stmt.bindLong(8, Status.SENT.ordinal());
                    stmt.bindLong(9, msg.Direction == 2 || msg.Direction == 200 ? UserType.OTHER.ordinal() : UserType.SELF.ordinal());
                    stmt.bindLong(10, msg.ackIDX);
                    long entryID = stmt.executeInsert();
                    stmt.clearBindings();
                }
                database.setTransactionSuccessful();
                database.endTransaction();

                final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("HH:mm");
                String sqlstr = sqlStringJobs.getLastMessageForAllUsers();
                List al = new ArrayList();

                if (sqlstr != "") {
                    database = dbhelper.getWritableDatabase();
                    database.beginTransaction();
                    ChatMsgSenders cms = null;
                    Cursor cursor = database.rawQuery(sqlstr, null);
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        cms = new ChatMsgSenders(
                                cursor.getString(cursor.getColumnIndex(sqlStringJobs.COLUMN_UserIDX)),
                                cursor.getString(cursor.getColumnIndex(sqlStringJobs.COLUMN_UserName)),
                                null,
                                SIMPLE_DATE_FORMAT.format(cursor.getLong(cursor.getColumnIndex(sqlStringJobs.COLUMN_TimeStamp))),
                                cursor.getInt(cursor.getColumnIndex(sqlStringJobs.COLUMN_Direction)) == 200 ? false : true,
                                1,
                                cursor.getLong(cursor.getColumnIndex(sqlStringJobs.COLUMN_TimeStamp)),
                                String.valueOf(cursor.getLong(cursor.getColumnIndex(sqlStringJobs.COLUMN_JobsIDX))));
                        al.add(cms);
                        cursor.moveToNext();
                    }
                    // make sure to close the cursor
                    cursor.close();
                    //database.setTransactionSuccessful();
                }

                if (al.size() > 0) {
                    stmt = database.compileStatement(sqlStringJobs.getInsertChatMsgSendersSQLstr2());
                    for (int i = 0; i <= al.size() - 1; i++) {
                        ChatMsgSenders cms = (ChatMsgSenders) al.get(i);
                        if (cms.UserName != null) {
                            stmt.bindLong(1, Long.parseLong(cms.UserIDX));
                            stmt.bindString(2, cms.UserName);
                            stmt.bindString(3, cms.messageTime);
                            stmt.bindLong(4, cms.Read ? 1 : 0);
                            stmt.bindLong(5, cms.notiID);
                            stmt.bindLong(6, cms.msgDateLong);
                            stmt.bindLong(7, Long.parseLong(cms.JobsIDX));
                            long entryID = stmt.executeInsert();
                            stmt.clearBindings();
                        }
                    }
                }
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs insertChatMessagesBatch. Exception: " + e.getMessage().toString() + "\n" + "Stack: " + stack, "", "", "", "");
        } finally {
            dbEndTransaction();
        }
    }

    public static List<ChatMsgSenders> getChatMsgSendersListForAllUsers() {
        List<ChatMsgSenders> al = new ArrayList<ChatMsgSenders>();
        ChatMsgSenders cms = null;
        final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("HH:mm");
        try {
            String sqlstr = sqlStringJobs.getLastMessageForAllUsers();
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {

                    cms = new ChatMsgSenders(
                            cursor.getString(cursor.getColumnIndex(sqlStringJobs.COLUMN_UserIDX)),
                            cursor.getString(cursor.getColumnIndex(sqlStringJobs.COLUMN_UserName)),
                            null,
                            SIMPLE_DATE_FORMAT.format(cursor.getLong(cursor.getColumnIndex(sqlStringJobs.COLUMN_TimeStamp))),
                            cursor.getInt(cursor.getColumnIndex(sqlStringJobs.COLUMN_Direction)) == 200 ? false : true,
                            1,
                            cursor.getLong(cursor.getColumnIndex(sqlStringJobs.COLUMN_TimeStamp)),
                            String.valueOf(cursor.getLong(cursor.getColumnIndex(sqlStringJobs.COLUMN_JobsIDX))));
                    cms.setUN(cursor.getString(cursor.getColumnIndex("UN")));
                    al.add(cms);
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            al = null;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getChatMsgSendersListForAllUsers. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return al;
        }
    }

    public static List<ChatMsgSenders> getChatMsgSendersList() {
        List<ChatMsgSenders> al = new ArrayList<ChatMsgSenders>();
        ChatMsgSenders cms = null;
        try {
            String sqlstr = sqlStringJobs.getChatMsgSendersListSQLstr();
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    cms = cursorToChatMsgSenders(cursor, true);
                    al.add(cms);
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            al = null;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getChatMsgSendersList. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return al;
        }
    }

    public static void updateChatMessageStatus(long msgidx, int aStatus) {
        try {
            ContentValues values = new ContentValues();
            values.put(sqlStringJobs.COLUMN_MessageStatus, aStatus);
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();
            long updateId = database.update(sqlStringJobs.TABLE_NAME_CHATMESSAGE_TBL, values, sqlStringJobs.COLUMN_SQLiteMessageID + " = " + msgidx, null);
            Boolean isupdated = false;
            if (updateId > 0) {
                isupdated = true;
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs updateChatMessageStatus. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
        }
    }

    public static ArrayList<ChatMsgSenders> getChatMsgSendersWithJobsIDXnChatMsgSendersIDXList(Long JobsIDX) {
        ChatMsgSenders cms = null;
        ArrayList<ChatMsgSenders> al = null;
        try {
            String sqlstr = sqlStringJobs.getChatMsgSendersWithJobsIDXnChatMsgSendersIDXListSQLstr2(JobsIDX);
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    cms = cursorToChatMsgSenders(cursor, false);
                    if (al == null) {
                        al = new ArrayList<>();
                    }
                    al.add(cms);
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            cms = null;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getChatMsgSendersWithJobsIDXnChatMsgSendersIDXList. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return al;
        }
    }

    public static ChatMsgSenders getChatMsgSendersWithJobsIDXnChatMsgSendersIDX(Long JobsIDX, Long UserIDX) {
        ChatMsgSenders cms = null;
        dbObjTradeInList[] AL = null;
        try {
            String sqlstr = sqlStringJobs.getChatMsgSendersWithJobsIDXnChatMsgSendersIDXSQLstr2(UserIDX, JobsIDX);
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    cms = cursorToChatMsgSenders(cursor, false);
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            cms = null;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getChatMsgSendersWithJobsIDXnChatMsgSendersIDX. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return cms;
        }
    }

    public static void updateChatMsgSenders(ChatMsgSenders cms) {
        try {
            ContentValues values = new ContentValues();
            values.put(sqlStringJobs.COLUMN_READ, cms.Read == null || !cms.Read ? 0 : 1);
            values.put(sqlStringJobs.COLUMN_messageTime, cms.messageTime);
            values.put(sqlStringJobs.COLUMN_msgDateLong, cms.msgDateLong);
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();
            //long updateId = database.update(sqlStringJobs.TABLE_NAME_ChatMsgSenders_TBL, values, sqlStringJobs.COLUMN_UserIDX + " = " + Long.parseLong(cms.UserIDX), null);
            long updateId = database.update(sqlStringJobs.TABLE_NAME_ChatMsgSenders_TBL, values, sqlStringJobs.COLUMN_UserIDX + " = " + Long.parseLong(cms.UserIDX) + " AND "
                    + sqlStringJobs.COLUMN_JobsIDX + " = " + Long.parseLong(cms.JobsIDX), null);
            Boolean isupdated = false;
            if (updateId > 0) {
                isupdated = true;
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs updateChatMsgSenders. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
        }
    }

    public static void updateChatMsgSendersByJobsIDX(Long JobsIDX) {
        try {
            ContentValues values = new ContentValues();
            values.put(sqlStringJobs.COLUMN_READ, 1);
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();
            long updateId = database.update(sqlStringJobs.TABLE_NAME_ChatMsgSenders_TBL, values, sqlStringJobs.COLUMN_JobsIDX + " = " + JobsIDX, null);
            Boolean isupdated = false;
            if (updateId > 0) {
                isupdated = true;
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs updateChatMsgSendersByJobsIDX. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
        }
    }

    public static Integer getChatMsgSendersCount() {
        Integer c = 0;
        try {
            String sqlstr = sqlStringJobs.getChatMsgSendersCountSQLstr();
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();
            Cursor cursor = database.rawQuery(sqlstr, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                c = cursor.getInt(cursor.getColumnIndex("CC"));
                cursor.moveToNext();
            }
            // make sure to close the cursor
            cursor.close();
            database.setTransactionSuccessful();
        } catch (Exception e) {
            c = 0;
            objGlobalMethods.writeToFile("Exception at DAO_Jobs getChatMsgSendersCount. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return c;
        }
    }

    public static Integer getUnreadMessageCount() {
        Integer c = 0;
        try {
            String sqlstr = sqlStringJobs.getUnreadMessageCount();
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();
            Cursor cursor = database.rawQuery(sqlstr, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                c = cursor.getInt(cursor.getColumnIndex("C"));
                cursor.moveToNext();
            }
            // make sure to close the cursor
            cursor.close();
            database.setTransactionSuccessful();
        } catch (Exception e) {
            c = 0;
            objGlobalMethods.writeToFile("Exception at DAO_Jobs getUnreadMessageCount. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return c;
        }
    }

    public static Integer getJobCountByJobsIDX(long JobsIDX) {
        Integer c = 0;
        try {
            String sqlstr = sqlStringJobs.getJobCountByJobsIDXSQLstr(JobsIDX);
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();
            Cursor cursor = database.rawQuery(sqlstr, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                c = cursor.getInt(cursor.getColumnIndex("C"));
                cursor.moveToNext();
            }
            // make sure to close the cursor
            cursor.close();
            database.setTransactionSuccessful();
        } catch (Exception e) {
            c = 0;
            objGlobalMethods.writeToFile("Exception at DAO_Jobs getJobCountByJobsIDX. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return c;
        }
    }

    public static ArrayList<ChatMessage> getchatMessages2(Long JobsIDX, Long UserIDX) {
        ArrayList<ChatMessage> cmar = new ArrayList<>();
        ChatMessage cm = null;
        try {
            String sqlstr = sqlStringJobs.getAllMsgByJobsIDXnChatMsgSendersIDX(JobsIDX, UserIDX);
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    cm = cursorToChatMessage(cursor);
                    cmar.add(cm);
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            cmar = null;
            objGlobalMethods.writeToFile("Exception at DAO_Jobs getchatMessages2. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return cmar;
        }
    }

    public static ArrayList<ChatMessage> getJobChangesMessages(Long JobsIDX) {
        ArrayList<ChatMessage> cmar = new ArrayList<>();
        ChatMessage cm = null;
        try {
            String sqlstr = sqlStringJobs.getJobChangesMessages(JobsIDX);
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    cm = cursorToChatMessage(cursor);
                    cmar.add(cm);
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            cmar = null;
            objGlobalMethods.writeToFile("Exception at DAO_Jobs getJobChangesMessages. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return cmar;
        }
    }

    public static ArrayList getUnAckJobChangesMsgIDXByJobsIDX(Long JobsIDX) {
        ArrayList mal = new ArrayList();
        ArrayList al = new ArrayList<>();
        Long msgID = Long.parseLong("0");
        Long IDX = Long.parseLong("0");
        Integer notiID = Integer.parseInt("0");
        try {
            String sqlstr = sqlStringJobs.getUnAckJobChangesMsgIDXByJobsIDX(JobsIDX);
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    al = new ArrayList<>();
                    msgID = cursor.getLong(cursor.getColumnIndex(sqlStringJobs.COLUMN_SQLiteMessageID));
                    IDX = cursor.getLong(cursor.getColumnIndex(sqlStringJobs.COLUMN_IDX));
                    notiID = cursor.getInt(cursor.getColumnIndex(sqlStringJobs.COLUMN_notiID));
                    al.add(IDX);
                    al.add(notiID);
                    al.add(msgID);
                    mal.add(al);
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            mal = null;
            objGlobalMethods.writeToFile("Exception at DAO_Jobs getUnAckJobChangesMsgIDXByJobsIDX. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return mal;
        }
    }

    public static ArrayList<objUnreadPUOKPFAILjobsMsgInfo> getUnreadPUOKPFAILjobsMsgInfo() {
        ArrayList<objUnreadPUOKPFAILjobsMsgInfo> al = null;
        objUnreadPUOKPFAILjobsMsgInfo obj = null;
        try {
            String sqlstr = sqlStringJobs.getUnreadPUOKPFAILjobsMsgInfo();
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();
            Cursor cursor = database.rawQuery(sqlstr, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                obj = cursorToobjUnreadPUOKPFAILjobsMsgInfo(cursor);
                if (obj != null) {
                    if (al == null) {
                        al = new ArrayList<>();
                    }
                    al.add(obj);
                }
                cursor.moveToNext();
            }
            // make sure to close the cursor
            cursor.close();
            database.setTransactionSuccessful();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at DAO_Jobs getUnreadPUOKPFAILjobsMsgInfo. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return al;
        }
    }

    //=================================================CallLogs=================================================

    public static boolean insertCallLog(String TelNum, Long LastCallTimeStamp, Integer catTypeID, boolean ReqCallLogImg, boolean connected) {
        long entryID = 0;
        boolean asd = false;
        try {
            if (TelNum != null && !TelNum.isEmpty() && !TelNum.equals("") && TelNum != "") {
                if (LastCallTimeStamp == null) {
                    LastCallTimeStamp = new Date().getTime();
                }

                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                SQLiteStatement stmt = database.compileStatement(sqlStringJobs.getInsertCallLogSQLstr());
                stmt.bindString(1, TelNum);
                stmt.bindLong(2, 1);
                stmt.bindLong(3, catTypeID);
                stmt.bindLong(4, LastCallTimeStamp);
                stmt.bindLong(5, ReqCallLogImg ? 1 : 0);
                stmt.bindLong(6, connected ? 1 : 0);
                entryID = stmt.executeInsert();
                stmt.clearBindings();
                database.setTransactionSuccessful();
                asd = true;
            }
        } catch (Exception e) {
            asd = false;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs insertCallLog. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return asd;
        }
    }

    public static Integer CountTelNumCalledOut(String TelNum) {
        Integer asd = 0;
        try {
            String sqlstr = sqlStringJobs.getCountTelNumCalledOutSQLstr(TelNum);
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    asd = cursor.getInt(cursor.getColumnIndex("C"));
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs CountTelNumCalledOut. Exception: " + e.getMessage().toString(), "", "", "", "");
            asd = 0;
        } finally {
            dbEndTransaction();
            return asd;
        }
    }

    public static ArrayList<objNumOfCallOutPast15min> getNumOfCallOutPast7minArr(String TelNum) {
        ArrayList<objNumOfCallOutPast15min> al = new ArrayList<>();
        objNumOfCallOutPast15min obj = null;
        try {
            String sqlstr = sqlStringJobs.getNumOfCallOutPast7minSQLstr(TelNum);
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    obj = new objNumOfCallOutPast15min(cursor.getInt(cursor.getColumnIndex(sqlStringJobs.COLUMN_ReqCallLogImg)), cursor.getInt(cursor.getColumnIndex(sqlStringJobs.COLUMN_CallConnected)), cursor.getInt(cursor.getColumnIndex("C")));
                    al.add(obj);
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at DAO_Jobs getNumOfCallOutPast7minArr. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return al;
        }
    }

    public static ArrayList<objNumOfCallOutPast15min> getNumOfCallOutPast15minArr(String TelNum) {
        ArrayList<objNumOfCallOutPast15min> al = new ArrayList<>();
        objNumOfCallOutPast15min obj = null;
        try {
            String sqlstr = sqlStringJobs.getNumOfCallOutPast15minSQLstr(TelNum);
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    obj = new objNumOfCallOutPast15min(cursor.getInt(cursor.getColumnIndex(sqlStringJobs.COLUMN_ReqCallLogImg)), cursor.getInt(cursor.getColumnIndex(sqlStringJobs.COLUMN_CallConnected)), cursor.getInt(cursor.getColumnIndex("C")));
                    al.add(obj);
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at DAO_Jobs getNumOfCallOutPast15minArr. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return al;
        }
    }

    public static ArrayList<objNumOfCallOutPast15min> getNumOfCallOutPast30minArr(String TelNum) {
        ArrayList<objNumOfCallOutPast15min> al = new ArrayList<>();
        objNumOfCallOutPast15min obj = null;
        try {
            String sqlstr = sqlStringJobs.getNumOfCallOutPast30minSQLstr(TelNum);
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    obj = new objNumOfCallOutPast15min(cursor.getInt(cursor.getColumnIndex(sqlStringJobs.COLUMN_ReqCallLogImg)), cursor.getInt(cursor.getColumnIndex(sqlStringJobs.COLUMN_CallConnected)), cursor.getInt(cursor.getColumnIndex("C")));
                    al.add(obj);
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at DAO_Jobs getNumOfCallOutPast30minArr. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return al;
        }
    }

    public static ArrayList<objNumOfCallOutPast15min> getNumOfCallOutPast60minArr(String TelNum) {
        ArrayList<objNumOfCallOutPast15min> al = new ArrayList<>();
        objNumOfCallOutPast15min obj = null;
        try {
            String sqlstr = sqlStringJobs.getNumOfCallOutPast60minSQLstr(TelNum);
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    obj = new objNumOfCallOutPast15min(cursor.getInt(cursor.getColumnIndex(sqlStringJobs.COLUMN_ReqCallLogImg)), cursor.getInt(cursor.getColumnIndex(sqlStringJobs.COLUMN_CallConnected)), cursor.getInt(cursor.getColumnIndex("C")));
                    al.add(obj);
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at DAO_Jobs getNumOfCallOutPast60minArr. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return al;
        }
    }

    //=================================================JobQue=================================================

    public static String getAddressWithPostalNoUnitByPostal(String postal) {
        String val = "";
        try {
            String sqlstr = sqlStringJobs.getAddressWithPostalNoUnitByPostalSQLstr(postal);
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();
            Cursor cursor = database.rawQuery(sqlstr, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                val = cursor.getString(cursor.getColumnIndex(sqlStringJobs.COLUMN_AddressWithPostalNoUnit));
                cursor.moveToNext();
            }
            // make sure to close the cursor
            cursor.close();
            database.setTransactionSuccessful();
        } catch (Exception e) {
            val = "";
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getAddressWithPostalNoUnitByPostal. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return val;
        }
    }

    public static String getAddressWithoutPostalByJobsIDX(String JobsIDX) {
        String val = "";
        try {
            String sqlstr = sqlStringJobs.getAddressWithoutPostalByJobsIDX(JobsIDX);
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();
            Cursor cursor = database.rawQuery(sqlstr, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                val = cursor.getString(cursor.getColumnIndex(sqlStringJobs.COLUMN_AddressWithoutPostal));
                cursor.moveToNext();
            }
            // make sure to close the cursor
            cursor.close();
            database.setTransactionSuccessful();
        } catch (Exception e) {
            val = "";
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getAddressWithoutPostalByJobsIDX. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return val;
        }
    }

    public static String getAddressWithPostalNoUnitByJobsIDX(String JobsIDX) {
        String val = "";
        try {
            String sqlstr = sqlStringJobs.getAddressWithPostalNoUnitByJobsIDX(JobsIDX);
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();
            Cursor cursor = database.rawQuery(sqlstr, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                val = cursor.getString(cursor.getColumnIndex(sqlStringJobs.COLUMN_AddressWithPostalNoUnit));
                cursor.moveToNext();
            }
            // make sure to close the cursor
            cursor.close();
            database.setTransactionSuccessful();
        } catch (Exception e) {
            val = "";
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getAddressWithPostalNoUnitByJobsIDX. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return val;
        }
    }

    public static Integer[] getpNumdNumByJobsIDXs(String JobsIDXs) {
        Integer[] al = new Integer[]{0, 0};
        try {
            String sqlstr = sqlStringJobs.getpNumdNumByJobsIDX(JobsIDXs);
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    al[0] = cursor.getInt(cursor.getColumnIndex("pNum"));
                    al[1] = cursor.getInt(cursor.getColumnIndex("dNum"));
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getpNumdNumByJobsIDXs. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return al;
        }
    }

    //=================================================PIPPCS=================================================

    public static List<dbMedsJobs> getAllMedsColdbObjJobsList() {
        List<dbMedsJobs> dbObjJobsAL = null;
        try {
            dbObjJobsAL = new ArrayList<dbMedsJobs>();
            String sqlstr = sqlStringJobs.getAllMedsColSqlStr();
            boolean exist = false;
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    dbMedsJobs obj = cursorToMedToColdbObjJobs(cursor);
                    for (int x = 0; x <= dbObjJobsAL.size() - 1; x++) {
                        exist = false;
                        if (dbObjJobsAL.get(x).JobsIDX == obj.JobsIDX) {
                            exist = true;
                            break;
                        }
                    }
                    if (!exist) {
                        dbObjJobsAL.add(obj);
                    }
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            dbObjJobsAL = null;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getAllMedsColdbObjJobsList. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return dbObjJobsAL;
        }
    }

    public static void bulkInsertPIPPCS(ArrayList<PipPcs> list) {
        try {
            if (list != null && list.size() > 0) {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();

                String sql = "INSERT OR IGNORE INTO " + sqlStringJobs.TABLE_NAME_PIPPCS_TBL +
                        "(" + sqlStringJobs.COLUMN_JobsIDX + ", " + sqlStringJobs.COLUMN_PCS + ", " + sqlStringJobs.COLUMN_PCS_SCANNED + ", " + sqlStringJobs.COLUMN_PCS_SCANNEDNOW + ") " +
                        "VALUES (?, ?, ?, ?)";

                for (PipPcs p : list) {
                    Object[] bindArgs = new Object[]{p.JobsIDX, p.PCS, p.PCS_SCANNED, 0};
                    database.execSQL(sql, bindArgs);
                }
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs bulkInsertPIPPCS. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
        }
    }

    public static void InsertPIPPCS(PipPcs p) {
        try {
            if (p != null) {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();

                String sql = "INSERT OR IGNORE INTO " + sqlStringJobs.TABLE_NAME_PIPPCS_TBL +
                        "(" + sqlStringJobs.COLUMN_JobsIDX + ", " + sqlStringJobs.COLUMN_PCS + ", " + sqlStringJobs.COLUMN_PCS_SCANNED + ") " +
                        "VALUES (?, ?, ?)";

                Object[] bindArgs = new Object[]{p.JobsIDX, p.PCS, p.PCS_SCANNED};
                database.execSQL(sql, bindArgs);

                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs InsertPIPPCS. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
        }
    }

    public static void cleanPIPPCS() {
        try {
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();
            String deleteQuery = "DELETE FROM " + sqlStringJobs.TABLE_NAME_PIPPCS_TBL +
                    " WHERE " + sqlStringJobs.COLUMN_JobsIDX + " NOT IN " +
                    "(SELECT j." + sqlStringJobs.COLUMN_JobsIDX +
                    " FROM " + sqlStringJobs.TABLE_NAME + " j " +
                    " WHERE j." + sqlStringJobs.COLUMN_CatTypeID + " = 0)";

            database.execSQL(deleteQuery);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at DAO_Jobs cleanPIPPCS. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            dbEndTransaction();
        }
    }



    public static ArrayList<PipPcs> getJobsIDXNotInPIPPCS(String ContactIDX, String ClientIDX) {
        ArrayList<PipPcs> pippcslist = null;
        long JobsIDX = 0;
        int pcs = 0;
        PipPcs p = null;
        try {
            String sqlstr = sqlStringJobs.getJobsIDXNotInPIPPCSSqlStr(ContactIDX, ClientIDX);
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    JobsIDX = cursor.getLong(cursor.getColumnIndex("JobsIDX"));
                    pcs = cursor.getInt(cursor.getColumnIndex("PCS"));
                    p = new PipPcs(JobsIDX, pcs, 0);
                    if (pippcslist == null)
                        pippcslist = new ArrayList<PipPcs>();
                    pippcslist.add(p);

                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            pippcslist = null;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getJobsIDXNotInPIPPCS. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return pippcslist;
        }
    }

    public static void updatePIPPCS(ArrayList<PipPcs> list) {
        try {
            if (list != null && list.size() > 0) {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();

                String sql = "UPDATE " + sqlStringJobs.TABLE_NAME_PIPPCS_TBL +
                        " SET " + sqlStringJobs.COLUMN_PCS_SCANNED + " = ? " +
                        " WHERE " + sqlStringJobs.COLUMN_JobsIDX + " = ?";

                android.database.sqlite.SQLiteStatement statement = database.compileStatement(sql);

                for (PipPcs item : list) {
                    statement.clearBindings();
                    statement.bindLong(1, item.PCS_SCANNED);
                    statement.bindLong(2, item.JobsIDX);
                    statement.execute();
                }

                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs updatePIPPCS. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
        }
    }

    public static void updatePIPPCS_ZeroScanned() {
        try {
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();

            String sql = "UPDATE PIPPCS " +
                    " SET PCS_SCANNED = ? , PCS_SCANNEDNOW = ? ";

            android.database.sqlite.SQLiteStatement statement = database.compileStatement(sql);

            statement.clearBindings();
            statement.bindLong(1, 0);
            statement.bindLong(2, 0);
            statement.execute();

            database.setTransactionSuccessful();
            objGlobalMethods.writeToFile("updatePIPPCS_ZeroScanned", "", "", "", "");
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs updatePIPPCS_ZeroScanned. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
        }
    }

    public static void updatePIPPCS_ZeroScannedNow() {
        try {
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();

            String sql = "UPDATE PIPPCS " +
                    " SET PCS_SCANNEDNOW = ? ";

            android.database.sqlite.SQLiteStatement statement = database.compileStatement(sql);

            statement.clearBindings();
            statement.bindLong(1, 0);
            statement.execute();

            database.setTransactionSuccessful();
            objGlobalMethods.writeToFile("updatePIPPCS_ZeroScannedNow", "", "", "", "");
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs updatePIPPCS_ZeroScannedNow. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
        }
    }

    public static void updatePIPPCS_1by1(long JobsIDX, int v) {
        try {
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();

            String sql = "UPDATE PIPPCS " +
                    " SET PCS_SCANNED = PCS_SCANNED + ? " +
                    ", PCS_SCANNEDNOW = PCS_SCANNEDNOW + ? " +
                    " WHERE JobsIDX = ?";

            android.database.sqlite.SQLiteStatement statement = database.compileStatement(sql);

            statement.clearBindings();
            statement.bindLong(1, v);
            statement.bindLong(2, v);
            statement.bindLong(3, JobsIDX);
            statement.execute();

            database.setTransactionSuccessful();
            objGlobalMethods.writeToFile("updatePIPPCS_1by1 JobsIDX " + String.valueOf(JobsIDX) + " PCS_SCANNED + " + String.valueOf(v) + " PCS_SCANNEDNOW = " + String.valueOf(v), "", "", "", "");
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs updatePIPPCS_1by1. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
        }
    }

    public static List<Long> getPIPPCS_to_Update() {
        List<Long> al = null;
        long JobsIDX = 0;
        try {
            String sqlstr = sqlStringJobs.getPIPPCS_to_UpdateSqlStr();
            boolean exist = false;
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    JobsIDX = cursor.getLong(cursor.getColumnIndex("JobsIDX"));
                    if (al == null)
                        al = new ArrayList<Long>();
                    if (al != null && !al.contains(JobsIDX))
                        al.add(JobsIDX);
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            al = null;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getPIPPCS_to_Update. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return al;
        }
    }

    public static List<Long> getPIPPCS_selected() {
        List<Long> al = null;
        long JobsIDX = 0;
        try {
            String sqlstr = sqlStringJobs.getPIPPCS_selectedSqlStr();
            boolean exist = false;
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    JobsIDX = cursor.getLong(cursor.getColumnIndex("JobsIDX"));
                    if (al == null)
                        al = new ArrayList<Long>();
                    if (al != null && !al.contains(JobsIDX))
                        al.add(JobsIDX);
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            al = null;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getPIPPCS_selected. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return al;
        }
    }

    public static int getPCS_SCANNEDNOW() {
        int asd = 0;
        try {
            String sqlstr = sqlStringJobs.getPCS_SCANNEDNOWSqlStr();
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    asd = cursor.getInt(cursor.getColumnIndex("C"));
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getPCS_SCANNEDNOW. Exception: " + e.getMessage().toString(), "", "", "", "");
            asd = 0;
        } finally {
            dbEndTransaction();
            return asd;
        }
    }

    public static int getMedJobsCount() {
        int asd = 0;
        try {
            String sqlstr = sqlStringJobs.getMedJobsCountSqlStr();
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    asd = cursor.getInt(cursor.getColumnIndex("C"));
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getMedJobsCount. Exception: " + e.getMessage().toString(), "", "", "", "");
            asd = 0;
        } finally {
            dbEndTransaction();
            return asd;
        }
    }

    //=================================================Others=================================================

    public static int getCheckIfJobIsPickAndSend(Long JobsIDX) {
        int asd = 0;
        try {
            String sqlstr = sqlStringJobs.getCheckIfJobIsPickAndSend(JobsIDX);
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    asd = cursor.getInt(cursor.getColumnIndex("CT"));
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getCheckIfJobIsPickAndSend. Exception: " + e.getMessage().toString(), "", "", "", "");
            asd = 0;
        } finally {
            dbEndTransaction();
            return asd;
        }
    }

    public static int getPIPDIP_NFC_JobsCount() {
        int asd = 0;
        try {
            String sqlstr = sqlStringJobs.getPIPDIP_NFC_JobsCountSQlStr();
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    asd = cursor.getInt(cursor.getColumnIndex("CT"));
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getPIPDIP_NFC_JobsCount. Exception: " + e.getMessage().toString(), "", "", "", "");
            asd = 0;
        } finally {
            dbEndTransaction();
            return asd;
        }
    }

    public static ArrayList<Integer> getFlag2SumArr() {
        ArrayList<Integer> asd = new ArrayList<>();
        Integer flag2 = 0;
        try {
            String sqlstr = sqlStringJobs.getFlag2SumArr();
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    flag2 = cursor.getInt(cursor.getColumnIndex("F2"));
                    if (!asd.contains(flag2)) {
                        asd.add(flag2);
                    }
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getFlag2SumArr. Exception: " + e.getMessage().toString(), "", "", "", "");
            asd = null;
        } finally {
            dbEndTransaction();
            return asd;
        }
    }

    public static ArrayList<Integer> getFlagSumArr() {
        ArrayList<Integer> asd = new ArrayList<>();
        Integer flag = 0;
        try {
            String sqlstr = sqlStringJobs.getFlagSumArr();
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    flag = cursor.getInt(cursor.getColumnIndex(sqlStringJobs.COLUMN_NetsVisaTI));
                    if (!asd.contains(flag)) {
                        asd.add(flag);
                    }
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getFlagSumArr. Exception: " + e.getMessage().toString(), "", "", "", "");
            asd = null;
        } finally {
            dbEndTransaction();
            return asd;
        }
    }

    public static ArrayList<objPostalFlagSum> getDistinctPostalFlagSum() {
        ArrayList<objPostalFlagSum> al = null;
        objPostalFlagSum obj = null;
        try {
            String sqlstr = sqlStringJobs.getDistinctPostalFlagSumSqlStr();
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    obj = cursorToobjPostalFlagSum(cursor);
                    if (obj != null) {
                        if (al == null) {
                            al = new ArrayList<objPostalFlagSum>();
                            al.add(obj);
                        } else {
                            al.add(obj);
                        }
                    }
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getDistinctPostalFlagSum. Exception: " + e.getMessage().toString(), "", "", "", "");
            al = null;
        } finally {
            dbEndTransaction();
            return al;
        }
    }

    public static ArrayList<objPostalFlagSum> getDistinctPostalFlag2Sum() {
        ArrayList<objPostalFlagSum> al = null;
        objPostalFlagSum obj = null;
        try {
            String sqlstr = sqlStringJobs.getDistinctPostalFlag2SumSqlStr();
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    obj = cursorToobjPostalFlagSum(cursor);
                    if (obj != null) {
                        if (al == null) {
                            al = new ArrayList<objPostalFlagSum>();
                            al.add(obj);
                        } else {
                            al.add(obj);
                        }
                    }
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getDistinctPostalFlag2Sum. Exception: " + e.getMessage().toString(), "", "", "", "");
            al = null;
        } finally {
            dbEndTransaction();
            return al;
        }
    }

    public static Boolean checkIfJobExist(Long JobsIDX) {
        Boolean asd = false;
        Integer totalCount = 0;
        try {
            String sqlstr = sqlStringJobs.getcheckIfJobExistSQLstr(JobsIDX);
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();
            Cursor cursor = database.rawQuery(sqlstr, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                totalCount = cursor.getInt(cursor.getColumnIndex("C"));
                cursor.moveToNext();
            }
            // make sure to close the cursor
            cursor.close();
            database.setTransactionSuccessful();
            if (totalCount > 0) {
                asd = true;
            }
        } catch (Exception e) {
            asd = true;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs checkIfJobExist. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return asd;
        }
    }

    public static void updateJobAddressFromPUtoDIP(int CatTypeID, Long JobsIDX, String JobInfoJsonStr) {
        String Postal = "";
        String AddressWithPostalNoUnit = "";
        String AddressWithoutPostal = "";
        Double LAT = 0.0;
        Double LNG = 0.0;
        String CompanyName = "";
        String ContactName = "";
        objContactStructure contactStructure = null;
        objJobInfo ji = null;
        Gson gson = new Gson();

        try {
            if (JobInfoJsonStr != "") {
                ji = gson.fromJson(JobInfoJsonStr, objJobInfo.class);

                if (CatTypeID == 0 || CatTypeID == 1 || CatTypeID == 4) {
                    ////pip/pu/fp
                    if (ji.PURedirectedLocation != null) {
                        Postal = ji.PURedirectedLocation.getPOSTALCODE();
                        LAT = ji.PURedirectedLocation.LAT;
                        LNG = ji.PURedirectedLocation.LNG;
                        AddressWithPostalNoUnit = objGlobalMethods.returnAddressWithPostalNoUnit(ji.PURedirectedLocation).toUpperCase();
                        AddressWithoutPostal = objGlobalMethods.returnAddressWithoutPostal(ji.PURedirectedLocation).toUpperCase();
                        CompanyName = ji.PURedirectedLocation.COMPANY != "" ? ji.PURedirectedLocation.COMPANY : "";
                        contactStructure = ji.PURedirectedLocation.Contacts != null ? ji.PURedirectedLocation.Contacts[0] : null;
                        ContactName = contactStructure != null ? contactStructure.NAME : "";
                    } else if (ji.PULocation != null) {
                        Postal = ji.PULocation.getPOSTALCODE();
                        LAT = ji.PULocation.LAT;
                        LNG = ji.PULocation.LNG;
                        AddressWithPostalNoUnit = objGlobalMethods.returnAddressWithPostalNoUnit(ji.PULocation).toUpperCase();
                        AddressWithoutPostal = objGlobalMethods.returnAddressWithoutPostal(ji.PULocation).toUpperCase();
                        CompanyName = ji.PULocation.COMPANY != "" ? ji.PULocation.COMPANY : "";
                        contactStructure = ji.PULocation.Contacts != null ? ji.PULocation.Contacts[0] : null;
                        ContactName = contactStructure != null ? contactStructure.NAME : "";
                    }
                } else if (CatTypeID == 2 || CatTypeID == 3 || CatTypeID == 5 || CatTypeID == 6 || CatTypeID == 7) {
                    ////dip/okp/fd/pd/transit
                    if (ji.DLRedirectedLocation != null) {
                        Postal = ji.DLRedirectedLocation.getPOSTALCODE();
                        LAT = ji.DLRedirectedLocation.LAT;
                        LNG = ji.DLRedirectedLocation.LNG;
                        AddressWithPostalNoUnit = objGlobalMethods.returnAddressWithPostalNoUnit(ji.DLRedirectedLocation).toUpperCase();
                        AddressWithoutPostal = objGlobalMethods.returnAddressWithoutPostal(ji.DLRedirectedLocation).toUpperCase();
                        CompanyName = ji.DLRedirectedLocation.COMPANY != "" ? ji.DLRedirectedLocation.COMPANY : "";
                        contactStructure = ji.DLRedirectedLocation.Contacts != null ? ji.DLRedirectedLocation.Contacts[0] : null;
                        ContactName = contactStructure != null ? contactStructure.NAME : "";
                    } else if (ji.DLLocation != null) {
                        Postal = ji.DLLocation.getPOSTALCODE();
                        LAT = ji.DLLocation.LAT;
                        LNG = ji.DLLocation.LNG;
                        AddressWithPostalNoUnit = objGlobalMethods.returnAddressWithPostalNoUnit(ji.DLLocation).toUpperCase();
                        AddressWithoutPostal = objGlobalMethods.returnAddressWithoutPostal(ji.DLLocation).toUpperCase();
                        CompanyName = ji.DLLocation.COMPANY != "" ? ji.DLLocation.COMPANY : "";
                        contactStructure = ji.DLLocation.Contacts != null ? ji.DLLocation.Contacts[0] : null;
                        ContactName = contactStructure != null ? contactStructure.NAME : "";
                    }
                }

                ContentValues values = new ContentValues();
                values.put(sqlStringJobs.COLUMN_CatTypeID, CatTypeID);
                values.put(sqlStringJobs.COLUMN_Postal, Postal);
                values.put(sqlStringJobs.COLUMN_AddressWithPostalNoUnit, AddressWithPostalNoUnit);
                values.put(sqlStringJobs.COLUMN_AddressWithoutPostal, AddressWithoutPostal);
                values.put(sqlStringJobs.COLUMN_LAT, LAT);
                values.put(sqlStringJobs.COLUMN_LNG, LNG);
                values.put(sqlStringJobs.COLUMN_CompanyName, CompanyName);
                values.put(sqlStringJobs.COLUMN_ContactName, ContactName);

                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                long updateId = database.update(sqlStringJobs.TABLE_NAME, values, sqlStringJobs.COLUMN_JobsIDX + " = " + JobsIDX, null);
                Boolean isupdated = false;
                if (updateId > 0) {
                    isupdated = true;
                }
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs updateJobAddressFromPUtoDIP. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
        }
    }

    public static void updateJobByJobsIDXnCatTypeID(Long JobsIDX, int CatTypeID, objJobInfo ji, String aJobInfoJsonStr) {
        int ExpType = 0;
        int FLAG = 0;
        int PCS = 0;
        long StatusCodeIDX = 0;
        int IsPartOne = 0;
        int IsDunning = 0;
        String Postal = "";
        String AddressWithPostalNoUnit = "";
        String AddressWithoutPostal = "";
        Double LAT = 0.0;
        Double LNG = 0.0;
        String ExpTypeDesc = "";
        String PartDesc = "";
        String JobNo = "";
        String CNNo = "";
        String CompanyName = "";
        String ContactName = "";
        String DO_INV_No = "";
        String DO_INV_IDX = "";
        String TAGS = "";
        Long[] SQLiteDateSecondsLong = null;
        Long FromDateTime = Long.parseLong("0");
        Long PickByDateTime = Long.parseLong("0");
        Long ReadyDateTime = Long.parseLong("0");
        Long ToDateTime = Long.parseLong("0");
        Long ExtDateTime = Long.parseLong("0");
        Long ExtFromDateTime = Long.parseLong("0");
        String JobInfoJsonStr = "";

        long ClientIDX = 0;
        long ContactIDX = 0;
        String INDUSTRY = "";

        objContactStructure contactStructure = null;

        try {
            if (CatTypeID == 0 || CatTypeID == 1 || CatTypeID == 4) {
                ////pip/pu/fp
                if (ji.PURedirectedLocation != null) {
                    Postal = ji.PURedirectedLocation.getPOSTALCODE();
                    LAT = ji.PURedirectedLocation.LAT;
                    LNG = ji.PURedirectedLocation.LNG;
                    AddressWithPostalNoUnit = objGlobalMethods.returnAddressWithPostalNoUnit(ji.PURedirectedLocation).toUpperCase();
                    AddressWithoutPostal = objGlobalMethods.returnAddressWithoutPostal(ji.PURedirectedLocation).toUpperCase();
                    CompanyName = ji.PURedirectedLocation.COMPANY != "" ? ji.PURedirectedLocation.COMPANY : "";
                    contactStructure = ji.PURedirectedLocation.Contacts != null ? ji.PURedirectedLocation.Contacts[0] : null;
                    ContactName = contactStructure != null ? contactStructure.NAME : "";
                } else if (ji.PULocation != null) {
                    Postal = ji.PULocation.getPOSTALCODE();
                    LAT = ji.PULocation.LAT;
                    LNG = ji.PULocation.LNG;
                    AddressWithPostalNoUnit = objGlobalMethods.returnAddressWithPostalNoUnit(ji.PULocation).toUpperCase();
                    AddressWithoutPostal = objGlobalMethods.returnAddressWithoutPostal(ji.PULocation).toUpperCase();
                    CompanyName = ji.PULocation.COMPANY != "" ? ji.PULocation.COMPANY : "";
                    contactStructure = ji.PULocation.Contacts != null ? ji.PULocation.Contacts[0] : null;
                    ContactName = contactStructure != null ? contactStructure.NAME : "";
                }
            } else if (CatTypeID == 2 || CatTypeID == 3 || CatTypeID == 5 || CatTypeID == 6 || CatTypeID == 7) {
                ////dip/okp/fd/pd/transit
                if (ji.DLRedirectedLocation != null) {
                    Postal = ji.DLRedirectedLocation.getPOSTALCODE();
                    LAT = ji.DLRedirectedLocation.LAT;
                    LNG = ji.DLRedirectedLocation.LNG;
                    AddressWithPostalNoUnit = objGlobalMethods.returnAddressWithPostalNoUnit(ji.DLRedirectedLocation).toUpperCase();
                    AddressWithoutPostal = objGlobalMethods.returnAddressWithoutPostal(ji.DLRedirectedLocation).toUpperCase();
                    CompanyName = ji.DLRedirectedLocation.COMPANY != "" ? ji.DLRedirectedLocation.COMPANY : "";
                    contactStructure = ji.DLRedirectedLocation.Contacts != null ? ji.DLRedirectedLocation.Contacts[0] : null;
                    ContactName = contactStructure != null ? contactStructure.NAME : "";
                } else if (ji.DLLocation != null) {
                    Postal = ji.DLLocation.getPOSTALCODE();
                    LAT = ji.DLLocation.LAT;
                    LNG = ji.DLLocation.LNG;
                    AddressWithPostalNoUnit = objGlobalMethods.returnAddressWithPostalNoUnit(ji.DLLocation).toUpperCase();
                    AddressWithoutPostal = objGlobalMethods.returnAddressWithoutPostal(ji.DLLocation).toUpperCase();
                    CompanyName = ji.DLLocation.COMPANY != "" ? ji.DLLocation.COMPANY : "";
                    contactStructure = ji.DLLocation.Contacts != null ? ji.DLLocation.Contacts[0] : null;
                    ContactName = contactStructure != null ? contactStructure.NAME : "";
                }
            }

            ExpType = ji.ExpressType;
            if (ji.ParentJobsIDX > 0) {
                //Part = " - Part 2";
                PartDesc = " - " + myApp.getAppContext().getString(R.string.part2);
                IsPartOne = 0;
            } else if (ji.ParentJobsIDX == 0) {
                if (ji.LinkedJobs != null) {
                    if (ji.LinkedJobs.length > 0) {
                        //Part = " - Part 1";
                        PartDesc = " - " + myApp.getAppContext().getString(R.string.part1);
                        IsPartOne = 1;
                    }
                }
            }

            if ((ji.ClientIDX == 14753 || ji.ClientIDX == 4868) && ji.DeliveryContents.toUpperCase().equals("DUNNING DOCUMENT")) {
                IsDunning = 1;
            }

            JobNo = ji.JobNo != "" ? ji.JobNo : "";
            CNNo = ji.ConsignmentNote != "" ? ji.ConsignmentNote : "";
            SQLiteDateSecondsLong = objGlobalMethods.returnAllDatesSQLiteLong(ji);
            FromDateTime = SQLiteDateSecondsLong[0] != null ? SQLiteDateSecondsLong[0] : Long.parseLong("0");
            PickByDateTime = SQLiteDateSecondsLong[1] != null ? SQLiteDateSecondsLong[1] : Long.parseLong("0");
            ReadyDateTime = SQLiteDateSecondsLong[2] != null ? SQLiteDateSecondsLong[2] : Long.parseLong("0");
            ToDateTime = SQLiteDateSecondsLong[3] != null ? SQLiteDateSecondsLong[3] : Long.parseLong("0");
            ExtDateTime = SQLiteDateSecondsLong[4] != null ? SQLiteDateSecondsLong[4] : Long.parseLong("0");
            ExtFromDateTime = SQLiteDateSecondsLong[5] != null ? SQLiteDateSecondsLong[5] : Long.parseLong("0");
            //FLAG = (ji.FLAG & 8) == 8 ? 8 : (ji.FLAG & 512) == 512 ? 512 : (ji.FLAG & 1024) == 1024 ? 1024 : 0;
            FLAG = ji.FLAG == null ? 0 : ji.FLAG;
            PCS = ji.getPieces();
            JobInfoJsonStr = aJobInfoJsonStr;
            StatusCodeIDX = ji.StatusCodeIDX;

            ClientIDX = ji.ClientIDX;
            ContactIDX = ji.ContactIDX;
            INDUSTRY = ji.INDUSTRY;

            if (ji.DOList != null && ji.DOList.length > 0) {
                for (int d = 0; d <= ji.DOList.length - 1; d++) {
                    DO_INV_No += "[" + String.valueOf(ji.DOList[d].getInvoice()).toUpperCase() + "]";
                    DO_INV_IDX += "[" + String.valueOf(ji.DOList[d].getDOIDX()).toUpperCase() + "]";
                }
            } else {
                DO_INV_No = "";
                DO_INV_IDX = "";
            }

            if (ji.BarcodeTags != null && ji.BarcodeTags.length > 0) {
                for (int d = 0; d <= ji.BarcodeTags.length - 1; d++) {
                    TAGS += "[" + ji.BarcodeTags[d].toUpperCase() + "]";
                }
            } else {
                TAGS = "";
            }

            ContentValues values = new ContentValues();
            values.put(sqlStringJobs.COLUMN_ClientIDX, ClientIDX);
            values.put(sqlStringJobs.COLUMN_ContactIDX, ContactIDX);
            values.put(sqlStringJobs.COLUMN_INDUSTRY, INDUSTRY);
            values.put(sqlStringJobs.COLUMN_CatTypeID, CatTypeID);
            values.put(sqlStringJobs.COLUMN_Postal, Postal);
            values.put(sqlStringJobs.COLUMN_AddressWithPostalNoUnit, AddressWithPostalNoUnit);
            values.put(sqlStringJobs.COLUMN_AddressWithoutPostal, AddressWithoutPostal);
            values.put(sqlStringJobs.COLUMN_LAT, LAT);
            values.put(sqlStringJobs.COLUMN_LNG, LNG);
            values.put(sqlStringJobs.COLUMN_ExpType, ExpType);
            values.put(sqlStringJobs.COLUMN_ExpTypeDesc, objGlobalVariables.returnExpTypeDESC(ExpType));
            values.put(sqlStringJobs.COLUMN_IsPartOne, IsPartOne);
            values.put(sqlStringJobs.COLUMN_PartDesc, PartDesc);
            values.put(sqlStringJobs.COLUMN_IsDunning, IsDunning);
            values.put(sqlStringJobs.COLUMN_JobNo, JobNo);
            values.put(sqlStringJobs.COLUMN_CNNo, CNNo);
            values.put(sqlStringJobs.COLUMN_CompanyName, CompanyName);
            values.put(sqlStringJobs.COLUMN_ContactName, ContactName);
            values.put(sqlStringJobs.COLUMN_DO_INV_No, DO_INV_No);
            values.put(sqlStringJobs.COLUMN_DO_INV_IDX, DO_INV_IDX);
            values.put(sqlStringJobs.COLUMN_TAGS, TAGS);
            values.put(sqlStringJobs.COLUMN_FromDateTime, FromDateTime);
            values.put(sqlStringJobs.COLUMN_PickByDateTime, PickByDateTime);
            values.put(sqlStringJobs.COLUMN_ReadyDateTime, ReadyDateTime);
            values.put(sqlStringJobs.COLUMN_ToDateTime, ToDateTime);
            values.put(sqlStringJobs.COLUMN_ExtDateTime, ExtDateTime);
            values.put(sqlStringJobs.COLUMN_ExtFromDateTime, ExtFromDateTime);
            values.put(sqlStringJobs.COLUMN_FLAG, FLAG);
            values.put(sqlStringJobs.COLUMN_PCS, PCS);
            values.put(sqlStringJobs.COLUMN_StatusCodeIDX, StatusCodeIDX);
            values.put(sqlStringJobs.COLUMN_JobInfoJsonStr, JobInfoJsonStr);
            values.put(sqlStringJobs.COLUMN_ExtCount, ji.ExtCount);

            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();
            long updateId = database.update(sqlStringJobs.TABLE_NAME, values, sqlStringJobs.COLUMN_CatTypeID + " = " + CatTypeID + " AND "
                    + sqlStringJobs.COLUMN_JobsIDX + " = " + JobsIDX, null);
            Boolean isupdated = false;
            if (updateId > 0) {
                isupdated = true;
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs updateJobByJobsIDXnCatTypeID. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
        }
    }

    public static void updateJobInfoPrefStr(Long JobsIDX, String JobInfoJsonStr) {
        try {
            ContentValues values = new ContentValues();
            values.put(sqlStringJobs.COLUMN_JobInfoJsonStr, JobInfoJsonStr);
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();
            long updateId = database.update(sqlStringJobs.TABLE_NAME, values, sqlStringJobs.COLUMN_JobsIDX + " = " + JobsIDX, null);
            Boolean isupdated = false;
            if (updateId > 0) {
                isupdated = true;
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs updateJobInfoPrefStr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
        }
    }

    public static void updateJobInfoPromiseFromTo(Long JobsIDX, String JobInfoJsonStr, long From, long To) {
        try {
            ContentValues values = new ContentValues();
            values.put(sqlStringJobs.COLUMN_JobInfoJsonStr, JobInfoJsonStr);
            values.put(sqlStringJobs.COLUMN_PromisedFrom, From);
            values.put(sqlStringJobs.COLUMN_PromisedTo, To);
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();
            long updateId = database.update(sqlStringJobs.TABLE_NAME, values, sqlStringJobs.COLUMN_JobsIDX + " = " + JobsIDX, null);
            Boolean isupdated = false;
            if (updateId > 0) {
                isupdated = true;
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs updateJobInfoPromiseFromTo. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
        }
    }

    public static void updateJobsCatTypeID(Integer CatTypeID, Long JobsIDX) {
        try {
            ContentValues values = new ContentValues();
            values.put(sqlStringJobs.COLUMN_CatTypeID, CatTypeID);
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();
            long updateId = database.update(sqlStringJobs.TABLE_NAME, values, sqlStringJobs.COLUMN_JobsIDX + " = " + JobsIDX, null);
            Boolean isupdated = false;
            if (updateId > 0) {
                isupdated = true;
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs updateJobsCatTypeID. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            if (objGlobalMethods.IsDefaultVRPpage())
                JobsHandler2.processedPostalCreditNew();
        }
    }

    public static void updateJobsStatusCodeIDX(Long JobsIDX, Long StatusCodeIDX) {
        try {
            ContentValues values = new ContentValues();
            values.put(sqlStringJobs.COLUMN_StatusCodeIDX, StatusCodeIDX);
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();
            long updateId = database.update(sqlStringJobs.TABLE_NAME, values, sqlStringJobs.COLUMN_JobsIDX + " = " + JobsIDX, null);
            Boolean isupdated = false;
            if (updateId > 0) {
                isupdated = true;
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs updateJobsCatTypeID. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
        }
    }

    public static void updateJobsStatusCodeIDXByIDXrange(String JobsIDXsStr, Long StatusCodeIDX) {
        try {
            ContentValues values = new ContentValues();
            values.put(sqlStringJobs.COLUMN_StatusCodeIDX, StatusCodeIDX);
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();
            long updateId = database.update(sqlStringJobs.TABLE_NAME, values, sqlStringJobs.COLUMN_JobsIDX + " IN (" + JobsIDXsStr + ") ", null);
            Boolean isupdated = false;
            if (updateId > 0) {
                isupdated = true;
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs updateJobsStatusCodeIDXByIDXrange. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
        }
    }

    public static void updateChatMessageDirection(long idx, Integer Direction) {
        try {
            ContentValues values = new ContentValues();
            values.put(sqlStringJobs.COLUMN_Direction, Direction);
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();
            long updateId = database.update(sqlStringJobs.TABLE_NAME_CHATMESSAGE_TBL, values, sqlStringJobs.COLUMN_SQLiteMessageID + " = '" + idx + "'", null);
            Boolean isupdated = false;
            if (updateId > 0) {
                isupdated = true;
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs updateChatMessageDirection. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
        }
    }

    public static void deleteJobByIDXrange(String JobsIDXsStr) {
        try {
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();
            int rowsupdate = database.delete(sqlStringJobs.TABLE_NAME, sqlStringJobs.COLUMN_JobsIDX + " IN (" + JobsIDXsStr + ") ", null);
            database.setTransactionSuccessful();
            //objGlobalMethods.writeToFile("deleteJobByIDXrange completed. JobsIDX: " + JobsIDXsStr, "", "", "", "");
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs deleteJobByIDXrange. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
        }
    }

    public static void deleteJobsByCatTypeID(Integer CatTypeID) {
        try {
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();
            int rowsupdate = database.delete(sqlStringJobs.TABLE_NAME, sqlStringJobs.COLUMN_CatTypeID + " = ? ", new String[]{String.valueOf(CatTypeID)});
            database.setTransactionSuccessful();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs deleteJobsByCatTypeID. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
        }
    }

    public static void deleteAllJobs() {
        try {
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();
            int rowsupdate = database.delete(sqlStringJobs.TABLE_NAME, sqlStringJobs.COLUMN_CatTypeID + " IN ('0','1','2','3','4','5') ", null);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs deleteAllJobs. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
        }
    }

    public static void deleteAllTradeInModels() {
        try {
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();
            int rowsupdate = database.delete(sqlStringJobs.TABLE_NAME_TRADEINLIST, null, null);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs deleteAllTradeInModels. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
        }
    }

    public static void deleteAllNFCTAGS() {
        try {
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();
            int rowsupdate = database.delete(sqlStringJobs.TABLE_NAME_NFCTAGS, null, null);
            int x = rowsupdate;
            int y = x * 2;
            database.setTransactionSuccessful();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs deleteAllNFCTAGS. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
        }
    }

    public static void deleteAllChatMsgSenders() {
        try {
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();
            int rowsupdate = database.delete(sqlStringJobs.TABLE_NAME_ChatMsgSenders_TBL, null, null);
            int x = rowsupdate;
            int y = x * 2;
            database.setTransactionSuccessful();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs deleteAllChatMsgSenders. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
        }
    }

    public static void deleteAllChatMessages() {
        try {
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();
            int rowsupdate = database.delete(sqlStringJobs.TABLE_NAME_CHATMESSAGE_TBL, null, null);
            int x = rowsupdate;
            int y = x * 2;
            database.setTransactionSuccessful();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs deleteAllChatMessages. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
        }
    }

    public static Long[] getAllJobsIDXPerCategory(Integer CatTypeID) {
        List<Long> AL = null;
        Long[] arr = null;
        try {
            AL = new ArrayList<Long>();
            String sqlstr = sqlStringJobs.getAllJobsIDXPerCategory(CatTypeID);
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    Long JobsIDX = cursor.getLong(cursor.getColumnIndex(sqlStringJobs.COLUMN_JobsIDX));
                    AL.add(JobsIDX);
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
            }
            if (AL != null && AL.size() > 0) {
                arr = new Long[AL.size()];
                for (int i = 0; i <= AL.size() - 1; i++) arr[i] = AL.get(i);
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            arr = null;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getAllJobsIDXPerCategory. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return arr;
        }
    }

    public static void updateJobCNno(Long JobsIDX, String cnno, String currJobInfoJsonStr) {
        try {
            ContentValues values = new ContentValues();
            values.put(sqlStringJobs.COLUMN_CNNo, cnno);
            values.put(sqlStringJobs.COLUMN_JobInfoJsonStr, currJobInfoJsonStr);
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();
            long updateId = database.update(sqlStringJobs.TABLE_NAME, values, sqlStringJobs.COLUMN_JobsIDX + " = " + JobsIDX, null);
            Boolean isupdated = false;
            if (updateId > 0) {
                isupdated = true;
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs updateJobCNno. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
        }
    }

    public static void updateJobBCT(Long JobsIDX, String[] bct, String currJobInfoJsonStr) {
        String TAGS = "";

        try {
            if (bct != null && bct.length > 0) {
                for (int d = 0; d <= bct.length - 1; d++) {
                    TAGS += "[" + bct[d].toUpperCase() + "]";
                }
            } else {
                TAGS = "";
            }

            ContentValues values = new ContentValues();
            values.put(sqlStringJobs.COLUMN_TAGS, TAGS);
            values.put(sqlStringJobs.COLUMN_JobInfoJsonStr, currJobInfoJsonStr);
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();
            long updateId = database.update(sqlStringJobs.TABLE_NAME, values, sqlStringJobs.COLUMN_JobsIDX + " = " + JobsIDX, null);
            Boolean isupdated = false;
            if (updateId > 0) {
                isupdated = true;
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs updateJobBCT. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
        }
    }

    //======================================GCM IntentService portion======================================

    public static List<dbObjJobs> getAlldbObjJobsPerCategeory(Integer CatTypeID) {
        List<dbObjJobs> dbObjJobsAL = null;

        try {
            dbObjJobsAL = new ArrayList<dbObjJobs>();
            String sqlstr = sqlStringJobs.getAlldbObjJobsPerCategeorySQLstr(CatTypeID);
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    dbObjJobs obj = cursorTodbObjJobs(cursor, CatTypeID);
                    dbObjJobsAL.add(obj);
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            dbObjJobsAL = null;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getAlldbObjJobsPerCategeory. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return dbObjJobsAL;
        }
    }

    public static List<dbObjJobs> getAlldbObjJobsPerCategeoryWithOrigParkedJobs(Integer CatTypeID) {
        List<dbObjJobs> dbObjJobsAL = null;

        try {
            dbObjJobsAL = new ArrayList<dbObjJobs>();
            String sqlstr = sqlStringJobs.getAlldbObjJobsPerCategeoryWithOrigParkedJobsSQLstr(CatTypeID);
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    dbObjJobs obj = cursorTodbObjJobs(cursor, CatTypeID);
                    dbObjJobsAL.add(obj);
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            dbObjJobsAL = null;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getAlldbObjJobsPerCategeoryWithOrigParkedJobs. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return dbObjJobsAL;
        }
    }

    public static List<dbObjJobs> getAllParkedJobsdbObjJobsByPostals(String postals) {
        List<dbObjJobs> dbObjJobsAL = null;

        try {
            dbObjJobsAL = new ArrayList<dbObjJobs>();
            String sqlstr = sqlStringJobs.getAllParkedJobsdbObjJobsByPostals(postals);
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    dbObjJobs obj = cursorTodbObjJobs(cursor, 6);
                    dbObjJobsAL.add(obj);
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            dbObjJobsAL = null;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getAllParkedJobsdbObjJobsByPostals. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return dbObjJobsAL;
        }
    }

    public static String getNFCtagListByCAIDXorCNIDX(String caidx, String cnidx) {
        List<String> al = new ArrayList<>();
        String asd = "";
        String full = "";
        try {
            String sqlstr = sqlStringJobs.getNFCtagByContactIDXsqlstr(caidx, cnidx);
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    asd = cursor.getString(cursor.getColumnIndex(sqlStringJobs.COLUMN_NFCTAGS_UID));
                    al.add(asd);
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
            }

            if (al.size() > 0) {
                Gson gson = new Gson();
                full = gson.toJson(al);
                if (full.equals("[]")) {
                    full = "";
                }
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            full = "";
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getNFCtagListByCAIDXorCNIDX. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return full;
        }
    }

    public static NFCTag getSearchForLocationNFCTagsRecordViaNFC(String tagID) {
        NFCTag obj = null;
        try {
            String sqlstr = sqlStringJobs.getSearchForLocationNFCTagsRecordViaNFC(tagID);
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    obj = cursorToNFCTag(cursor);
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            obj = null;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getSearchForLocationNFCTagsRecordViaNFC. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return obj;
        }
    }

    public static NFCTag getSearchForLocationNFCTagsRecordViaAddressIDX(String tagID) {
        NFCTag obj = null;
        try {
            String sqlstr = sqlStringJobs.getSearchForLocationNFCTagsRecordViaAddressIDX(tagID);
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    obj = cursorToNFCTag(cursor);
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            obj = null;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getSearchForLocationNFCTagsRecordViaAddressIDX. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return obj;
        }
    }

    public static List<dbObjJobs> getAllJobsPerLocationIDXTagPerCategoryWithOutBagTags(Long CAIDX1, Integer CatTypeID) {
        List<dbObjJobs> dbObjJobsAL = null;
        try {
            dbObjJobsAL = new ArrayList<dbObjJobs>();
            String sqlstr = sqlStringJobs.getAllJobsPerLocationIDXTagPerCategoryWithOutBagTags(CAIDX1, CatTypeID);

            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    dbObjJobs obj = cursorTodbObjJobs(cursor, CatTypeID);
                    dbObjJobsAL.add(obj);
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            dbObjJobsAL = null;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getAllJobsPerLocationIDXTagPerCategoryWithOutBagTags. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return dbObjJobsAL;
        }
    }

    public static List<dbObjJobs> getAllJobsPerLocationNFCTagPerCategoryWithOutBagTags(Long CAIDX1, Long CAIDX2, Integer CatTypeID) {
        List<dbObjJobs> dbObjJobsAL = null;
        try {
            dbObjJobsAL = new ArrayList<dbObjJobs>();
            String sqlstr = sqlStringJobs.getAllJobsPerLocationNFCTagPerCategoryWithOutBagTags(CAIDX1, CAIDX2, CatTypeID);
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    dbObjJobs obj = cursorTodbObjJobs(cursor, CatTypeID);
                    dbObjJobsAL.add(obj);
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            dbObjJobsAL = null;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getAllJobsPerLocationNFCTagPerCategoryWithOutBagTags. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return dbObjJobsAL;
        }
    }

    public static List<Long> getAllPIPPUDIPOKPJobsIDXSQLstr() {
        List<Long> getAllPIPPUDIPOKPJobsIDXSQLstr = new ArrayList<>();
        long JobsIDX = 0;
        try {
            String sqlstr = sqlStringJobs.getAllPIPPUDIPOKPJobsIDXSQLstr();
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    JobsIDX = cursor.getLong(cursor.getColumnIndex(sqlStringJobs.COLUMN_JobsIDX));
                    getAllPIPPUDIPOKPJobsIDXSQLstr.add(JobsIDX);
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            getAllPIPPUDIPOKPJobsIDXSQLstr = null;
            objGlobalMethods.writeToFile("Exception at DAO_Jobs getAllPIPPUDIPOKPJobsIDXSQLstr. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return getAllPIPPUDIPOKPJobsIDXSQLstr;
        }
    }

    public static List<Long> getAllPIPPUDIPOKPFPFDPDJobsIDXSQLstr() {
        List<Long> getAllPIPPUDIPOKPFPFDJobsIDXSQLstr = new ArrayList<>();
        long JobsIDX = 0;
        try {
            String sqlstr = sqlStringJobs.getAllPIPPUDIPOKPFPFDPDJobsIDXSQLstr();
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    JobsIDX = cursor.getLong(cursor.getColumnIndex(sqlStringJobs.COLUMN_JobsIDX));
                    getAllPIPPUDIPOKPFPFDJobsIDXSQLstr.add(JobsIDX);
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            getAllPIPPUDIPOKPFPFDJobsIDXSQLstr = null;
            objGlobalMethods.writeToFile("Exception at DAO_Jobs getAllPIPPUDIPOKPFPFDPDJobsIDXSQLstr. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return getAllPIPPUDIPOKPFPFDJobsIDXSQLstr;
        }
    }

    public static List<Long> getAllPIPDIPJobsIDXSQLstr() {
        List<Long> getAllPIPDIPJobsIDXSQLstr = new ArrayList<>();
        long JobsIDX = 0;
        try {
            String sqlstr = sqlStringJobs.getAllPIPDIPJobsIDXSQLstr();
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    JobsIDX = cursor.getLong(cursor.getColumnIndex(sqlStringJobs.COLUMN_JobsIDX));
                    getAllPIPDIPJobsIDXSQLstr.add(JobsIDX);
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            getAllPIPDIPJobsIDXSQLstr = null;
            objGlobalMethods.writeToFile("Exception at DAO_Jobs getAllPIPDIPJobsIDXSQLstr. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return getAllPIPDIPJobsIDXSQLstr;
        }
    }

    public static RouteLocation[] getRouteLocationAL() {
        List<RouteLocation> RouteLocationList = null;
        RouteLocation[] AL = null;
        List<String> PostalAL = new ArrayList<String>();

        List<Long> PUJobsIDXList = null;
        List<Long> DLJobsIDXList = null;
        try {
            RouteLocationList = new ArrayList<RouteLocation>();
            String sqlstr = sqlStringJobs.getAllLocations();
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    if (!PostalAL.contains(cursor.getString(cursor.getColumnIndex("POSTAL")))) {
                        PostalAL.add(cursor.getString(cursor.getColumnIndex("POSTAL")));
                        RouteLocation obj = cursorToRouteLocation(cursor);
                        Long[] JobsIDXList = new Long[]{cursor.getLong(cursor.getColumnIndex("JobsIDX"))};
                        obj.setJobsIDXList(JobsIDXList);

                        //this portion do here enough, only do 1x
                        PUJobsIDXList = Arrays.asList(new Long[cursor.getInt(cursor.getColumnIndex("PIP"))]);
                        DLJobsIDXList = Arrays.asList(new Long[cursor.getInt(cursor.getColumnIndex("DIP"))]);
                        obj.setPUJobsIDXList(PUJobsIDXList.toArray(new Long[PUJobsIDXList.size()]));
                        obj.setDLJobsIDXList(DLJobsIDXList.toArray(new Long[DLJobsIDXList.size()]));
                        //this portion do here enough, only do 1x

                        RouteLocationList.add(obj);
                        cursor.moveToNext();
                    } else {
                        for (int i = 0; i <= RouteLocationList.size() - 1; i++) {
                            RouteLocation obj = RouteLocationList.get(i);
                            if (obj.Location.postal.equals(cursor.getString(cursor.getColumnIndex("POSTAL")))) {
                                List<Long> list = null;
                                if (obj.getJobsIDXList() != null) {
                                    list = new ArrayList<Long>(Arrays.asList(obj.getJobsIDXList()));
                                } else {
                                    list = new ArrayList<Long>();
                                }
                                list.add(cursor.getLong(cursor.getColumnIndex("JobsIDX")));

                                Long[] JobsIDXList = list.toArray(new Long[list.size()]);
                                obj.setJobsIDXList(JobsIDXList);
                                RouteLocationList.set(i, obj);
                                break;
                            }
                        }
                        cursor.moveToNext();
                    }
                }
                // make sure to close the cursor
                cursor.close();
            }

            AL = RouteLocationList != null || RouteLocationList.size() > 0 ? RouteLocationList.toArray(new RouteLocation[RouteLocationList.size()]) : null;
            database.setTransactionSuccessful();
        } catch (Exception e) {
            AL = null;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getRouteLocationAL. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return AL;
        }
    }

    public static Boolean JobsIDXisStillPIPorDIP(Long JobsIDX) {
        Boolean asd = false;
        Integer totalCount = 0;
        try {
            String sqlstr = sqlStringJobs.getJobsCountForPIPnDIP_Only(JobsIDX);
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();
            Cursor cursor = database.rawQuery(sqlstr, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                totalCount = cursor.getInt(cursor.getColumnIndex("C"));
                cursor.moveToNext();
            }
            // make sure to close the cursor
            cursor.close();

            if (totalCount > 0) {
                asd = true;
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs JobsIDXisStillPIPorDIP. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return asd;
        }
    }

    public static objRouteJobEarliestTiming getobjRouteJobEarliestTiming(String JobsIDXs) {
        objRouteJobEarliestTiming j = null;

        try {
            String sqlstr = sqlStringJobs.getEarliestTimingAmongJobs(JobsIDXs);
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();
            Cursor cursor = database.rawQuery(sqlstr, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                j = cursorToobjRouteJobEarliestTiming(cursor);
                cursor.moveToNext();
            }
            // make sure to close the cursor
            cursor.close();

            database.setTransactionSuccessful();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getobjRouteJobEarliestTiming. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return j;
        }
    }

    public static List<Long> getAlldbObjJobsIDXsPerCategory(Integer CatTypeID) {
        List<Long> dbJobsIDXsAL = null;

        try {
            dbJobsIDXsAL = new ArrayList<Long>();
            String sqlstr = sqlStringJobs.getAlldbObjJobsIDXsPerCategorySQLstr(CatTypeID);
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    Long JobsIDX = cursor.getLong(cursor.getColumnIndex(sqlStringJobs.COLUMN_JobsIDX));
                    dbJobsIDXsAL.add(JobsIDX);
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            dbJobsIDXsAL = null;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getAlldbObjJobsIDXsPerCategory. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return dbJobsIDXsAL;
        }
    }

    public static List<Long> getAllPIPnotUpdatedJobsIDX() {
        List<Long> dbJobsIDXsAL = null;

        try {
            dbJobsIDXsAL = new ArrayList<Long>();
            String sqlstr = sqlStringJobs.getAllPIPnotUpdatedJobsIDXSQLstr();
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    Long JobsIDX = cursor.getLong(cursor.getColumnIndex(sqlStringJobs.COLUMN_JobsIDX));
                    dbJobsIDXsAL.add(JobsIDX);
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            dbJobsIDXsAL = null;
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getAllPIPnotUpdatedJobsIDX. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            dbEndTransaction();
            return dbJobsIDXsAL;
        }
    }

    public static ArrayList<objDOObject> getDOINV_ListViaJobsIDXs(String JobsIDXsAL) {
        ArrayList<objDOObject> al = new ArrayList<objDOObject>();
        try {
            String sqlstr = sqlStringJobs.getDOListViaJobsIDXsSqlStr2(JobsIDXsAL);
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    objDOObject obj = cursorToobjDOObject(cursor);
                    al.add(obj);
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            al = null;
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getDOINV_ListViaJobsIDXs. Exception: " + e.getMessage().toString() + "\n" + "Stack: " + stack, "", "", "", "");
        } finally {
            dbEndTransaction();
            return al;
        }
    }

    public static JobWithinTimeWindow getJobWithinTimeWindow(Long JobsIDX, long CustomETA_long) {
        JobWithinTimeWindow obj = null;
        try {
            String sqlstr = sqlStringJobs.getJobWithinTimeWindow(JobsIDX, CustomETA_long);
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();
            Cursor cursor = database.rawQuery(sqlstr, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                obj = cursorToJobWithinTimeWindow(cursor);
                cursor.moveToNext();
            }
            // make sure to close the cursor
            cursor.close();
            database.setTransactionSuccessful();
        } catch (Exception e) {
            obj = new JobWithinTimeWindow();
        } finally {
            dbEndTransaction();
            return obj;
        }
    }

    public static objMapJob getAllJobsIDXPerPostal(String postal) {
        List<Long> AL = null;
        Long[] arr = null;
        objMapJob m = null;
        int pip = 0;
        int dip = 0;
        try {
            AL = new ArrayList<Long>();
            String sqlstr = sqlStringJobs.getAllJobsIDXPerPostal(postal);
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    Long JobsIDX = cursor.getLong(cursor.getColumnIndex(sqlStringJobs.COLUMN_JobsIDX));
                    AL.add(JobsIDX);
                    pip = cursor.getInt(cursor.getColumnIndex("pip"));
                    dip = cursor.getInt(cursor.getColumnIndex("dip"));
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
            }
            if (AL != null && AL.size() > 0) {
                arr = new Long[AL.size()];
                for (int i = 0; i <= AL.size() - 1; i++) arr[i] = AL.get(i);
                m = new objMapJob();
                m.setJobsIDX(arr);
                m.setPIPcount(pip);
                m.setDIPcount(dip);
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            m = null;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getAllJobsIDXPerPostal. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return m;
        }
    }

    public static int getPIPDIPJobsCountPerPostal(String postal) {
        int c = 0;
        ;
        try {
            String sqlstr = sqlStringJobs.getPIPDIPJobsCountPerPostal(postal);
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();
            Cursor cursor = database.rawQuery(sqlstr, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                c = cursor.getInt(cursor.getColumnIndex("C"));
                cursor.moveToNext();
            }
            // make sure to close the cursor
            cursor.close();
            database.setTransactionSuccessful();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getAllJobsIDXPerPostal. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return c;
        }
    }


    public static int getJobCountViaIDXs(long[] JobsIDXs) {
        int c = 0;
        try {
            String sqlstr = sqlStringJobs.getJobCountViaIDXsSqlStr(JobsIDXs);
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();
            Cursor cursor = database.rawQuery(sqlstr, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                c = cursor.getInt(cursor.getColumnIndex("C"));
                cursor.moveToNext();
            }
            // make sure to close the cursor
            cursor.close();
            database.setTransactionSuccessful();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getJobCountViaIDXs. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return c;
        }
    }

    public static ArrayList<Location> getAllPipDipLocations() {
        ArrayList<Location> AL = null;
        Location l = null;
        try {
            AL = new ArrayList<Location>();
            String sqlstr = sqlStringJobs.getAllPipDipLocations();
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    l = new Location();
                    l.postal = cursor.getString(cursor.getColumnIndex(sqlStringJobs.COLUMN_Postal));
                    l.lat = String.valueOf(cursor.getDouble(cursor.getColumnIndex(sqlStringJobs.COLUMN_LAT)));
                    l.lon = String.valueOf(cursor.getDouble(cursor.getColumnIndex(sqlStringJobs.COLUMN_LNG)));
                    l.full_address = cursor.getString(cursor.getColumnIndex(sqlStringJobs.COLUMN_AddressWithPostalNoUnit));
                    //l.jobsidxs = cursor.getString(cursor.getColumnIndex("JOBSIDXS"));
                    AL.add(l);
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            AL = null;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getAllPipDipLocations. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return AL;
        }
    }

    public static String getAllPipDipPostals() {
        String v = "";
        String val = "";
        try {
            String sqlstr = sqlStringJobs.getAllPipDipPostals();
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();
            Cursor cursor = database.rawQuery(sqlstr, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                v = cursor.getString(cursor.getColumnIndex(sqlStringJobs.COLUMN_Postal));
                val += "'" + v + "',";
                cursor.moveToNext();
            }
            // make sure to close the cursor
            cursor.close();
            database.setTransactionSuccessful();
            if (!objGlobalMethods.IsEmptyString(val) && val.length() > 0) {
                val = val.substring(0, val.length() - 1);
            }
        } catch (Exception e) {
            val = "";
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getAllPipDipPostals. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return val;
        }
    }

    public static void updateParkedJobsToDIPCat(String PipDipPostals) {
        try {
            ContentValues values = new ContentValues();
            values.put(sqlStringJobs.COLUMN_CatTypeID, 2);
            values.put(sqlStringJobs.COLUMN_UNPARK, 1);
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();
            long updateId = database.update(sqlStringJobs.TABLE_NAME, values, sqlStringJobs.COLUMN_Postal + " IN (" + PipDipPostals + ") AND " + sqlStringJobs.COLUMN_CatTypeID + " = 6", null);
            boolean isupdated = updateId > 0;
            database.setTransactionSuccessful();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs updateJobsCatTypeID. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
        }
    }

    public static boolean updateParkedDIPJobsToParkedCatByPostal(String postal) {
        boolean isupdated = false;
        try {
            ContentValues values = new ContentValues();
            values.put(sqlStringJobs.COLUMN_CatTypeID, 6);
            values.put(sqlStringJobs.COLUMN_UNPARK, 0);
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();
            long updateId = database.update(sqlStringJobs.TABLE_NAME, values, sqlStringJobs.COLUMN_UNPARK + " = 1 AND " + sqlStringJobs.COLUMN_CatTypeID + " = 2 AND "
                    + sqlStringJobs.COLUMN_Postal + " = '" + postal + "'", null);
            isupdated = updateId > 0;
            database.setTransactionSuccessful();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs updateParkedDIPJobsToParkedCatByPostal. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return isupdated;
        }
    }

    public static boolean updateParkedDIPJobsToParkedCat() {
        boolean isupdated = false;
        try {
            ContentValues values = new ContentValues();
            values.put(sqlStringJobs.COLUMN_CatTypeID, 6);
            values.put(sqlStringJobs.COLUMN_UNPARK, 0);
            if (dbhelper == null) {
                dbhelper = new dbHelper(myApp.getAppContext());
            }
            database = dbhelper.getWritableDatabase();
            database.beginTransaction();
            long updateId = database.update(sqlStringJobs.TABLE_NAME, values, sqlStringJobs.COLUMN_UNPARK + " = 1 AND " + sqlStringJobs.COLUMN_CatTypeID + " = 2", null);
            isupdated = updateId > 0;
            database.setTransactionSuccessful();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs updateParkedDIPJobsToParkedCat. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return isupdated;
        }
    }

    public static Long getColdItemJobIDX() {
        List<Long> AL = null;
        long JobsIDX = 0;
        try {
            AL = new ArrayList<Long>();
            String sqlstr = sqlStringJobs.getColdItemJobIDX();
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    JobsIDX = cursor.getLong(cursor.getColumnIndex(sqlStringJobs.COLUMN_JobsIDX));
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            JobsIDX = 0;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getColdItemJobIDX. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return JobsIDX;
        }
    }

    public static Long[] getColdItemJobIDXs() {
        Long[] JobsIDXarr = null;
        List<Long> AL = null;
        long JobsIDX = 0;
        try {
            AL = new ArrayList<Long>();
            String sqlstr = sqlStringJobs.getColdItemJobIDXs();
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    JobsIDX = cursor.getLong(cursor.getColumnIndex(sqlStringJobs.COLUMN_JobsIDX));
                    if (AL == null) {
                        AL = new ArrayList<>();
                    }
                    AL.add(JobsIDX);
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
            }
            database.setTransactionSuccessful();
            if (AL != null && AL.size() > 0) {
                JobsIDXarr = AL.toArray(new Long[AL.size()]);
            }
        } catch (Exception e) {
            JobsIDXarr = null;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getColdItemJobIDXs. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return JobsIDXarr;
        }
    }

    public static Integer getDIPCatTypeCountByJobsIDX(Long JobsIDX) {
        int c = 0;
        try {

            String sqlstr = sqlStringJobs.getDIPCatTypeCountByJobsIDX(JobsIDX);
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    c = cursor.getInt(cursor.getColumnIndex("CT"));
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            c = 0;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs getDIPCatTypeCountByJobsIDX. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return c;
        }
    }

    //======================================INSERT UPDATE DELETE CLEAR======================================

    public static String gethfJobInfoJsonStr(String Postal, int CatTypeID) {
        String hfJobInfoJsonStr = "";
        try {
            String sqlstr = sqlStringJobs.gethfJobInfoJsonStrSQLstr(Postal, CatTypeID);
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    hfJobInfoJsonStr = cursor.getString(cursor.getColumnIndex(sqlStringJobs.COLUMN_JobInfoJsonStr));
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            hfJobInfoJsonStr = "";
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs gethfJobInfoJsonStr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return hfJobInfoJsonStr;
        }
    }

    public static String gethfJobInfoJsonStrByJobsIDX(Long JobsIDX) {
        String hfJobInfoJsonStr = "";
        try {
            String sqlstr = sqlStringJobs.gethfJobInfoJsonStrByJobsIDXSQLstr(JobsIDX);
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    hfJobInfoJsonStr = cursor.getString(cursor.getColumnIndex(sqlStringJobs.COLUMN_JobInfoJsonStr));
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            hfJobInfoJsonStr = "";
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs gethfJobInfoJsonStrByJobsIDX. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return hfJobInfoJsonStr;
        }
    }

    public static dbObjSimpleJobInfo gethfJobInfoJsonStrAndLatLngByJobsIDX(Long JobsIDX) {
        dbObjSimpleJobInfo obj = null;
        try {
            String sqlstr = sqlStringJobs.gethfJobInfoJsonStrAndLatLngByJobsIDXSQLstr(JobsIDX);
            if (sqlstr != "") {
                if (dbhelper == null) {
                    dbhelper = new dbHelper(myApp.getAppContext());
                }
                database = dbhelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = database.rawQuery(sqlstr, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    obj = new dbObjSimpleJobInfo(cursor.getString(cursor.getColumnIndex(sqlStringJobs.COLUMN_JobInfoJsonStr)),
                            cursor.getDouble(cursor.getColumnIndex(sqlStringJobs.COLUMN_LAT)),
                            cursor.getDouble(cursor.getColumnIndex(sqlStringJobs.COLUMN_LNG)),
                            cursor.getInt(cursor.getColumnIndex(sqlStringJobs.COLUMN_CatTypeID)),
                            cursor.getString(cursor.getColumnIndex(sqlStringJobs.COLUMN_Postal)),
                            cursor.getString(cursor.getColumnIndex(sqlStringJobs.COLUMN_AddressWithPostalNoUnit)),
                            cursor.getString(cursor.getColumnIndex(sqlStringJobs.COLUMN_AddressWithoutPostal)));
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            obj = null;
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs gethfJobInfoJsonStrAndLatLngByJobsIDX. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            dbEndTransaction();
            return obj;
        }
    }

    private static vrpUnlockPostData cursorTovrpUnlockPostData(Cursor cursor) {
        vrpUnlockPostData d = null;
        try {
            d = new vrpUnlockPostData();
            d.setPOSTAL(cursor.getString(cursor.getColumnIndex("Postal")));
            d.setAddressWithPostalNoUnit(cursor.getString(cursor.getColumnIndex("AddressWithPostalNoUnit")));
            d.setPIP(cursor.getInt(cursor.getColumnIndex("PIP")));
            d.setDIP(cursor.getInt(cursor.getColumnIndex("DIP")));
            d.setRDYDT(cursor.getLong(cursor.getColumnIndex("RDYDT")));
            d.setPUBYDT(cursor.getLong(cursor.getColumnIndex("PUBYDT")));
            d.setFDT(cursor.getLong(cursor.getColumnIndex("FDT")));
            d.setTDT(cursor.getLong(cursor.getColumnIndex("TDT")));
            d.setEFDT(cursor.getLong(cursor.getColumnIndex("EFDT")));
            d.setETDT(cursor.getLong(cursor.getColumnIndex("ETDT")));
            d.setJOBSIDXS(cursor.getString(cursor.getColumnIndex("JOBSIDXS")));

            d.setColdCount(cursor.getInt(cursor.getColumnIndex("ColdCount")));
            d.setReqNetsCount(cursor.getInt(cursor.getColumnIndex("ReqNetsCount")));
            d.setReqVisaCount(cursor.getInt(cursor.getColumnIndex("ReqVisaCount")));
            d.setReqTiCount(cursor.getInt(cursor.getColumnIndex("ReqTiCount")));
        } catch (Exception e) {
            d = null;
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("", "", "", "", "");
        } finally {
            return d;
        }
    }

    private static dbObjJobs cursorTodbObjJobs(Cursor cursor, int CatTypeIDX) {
        dbObjJobs dbjobs = null;
        String FromDateTimeConverted = "";
        String ToDateTimeConverted = "";
        String ExtFromDateTimeConverted = "";
        String ExtDateTimeConverted = "";
        //SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM h:mma");
        long ToDateTime, ExtFromDateTime, ExtDateTime;

        try {
            dbjobs = new dbObjJobs();
            //dbjobs.set_ID(cursor.getLong(cursor.getColumnIndex("COLUMN_ID")));
            dbjobs.setJobsIDX(cursor.getLong(cursor.getColumnIndex("JobsIDX")));
            dbjobs.setCatTypeID(cursor.getInt(cursor.getColumnIndex("CatTypeID")));
            dbjobs.setPostal(cursor.getString(cursor.getColumnIndex("Postal")));
            dbjobs.setAddressWithPostalNoUnit(cursor.getString(cursor.getColumnIndex("AddressWithPostalNoUnit")));
            dbjobs.setAddressWithoutPostal(cursor.getString(cursor.getColumnIndex("AddressWithoutPostal")));
            dbjobs.setLAT(cursor.getDouble(cursor.getColumnIndex("LAT")));
            dbjobs.setLNG(cursor.getDouble(cursor.getColumnIndex("LNG")));
            dbjobs.setFM_AddressIDX(cursor.getLong(cursor.getColumnIndex(sqlStringJobs.COLUMN_FM_AddressIDX)));
            dbjobs.setFM_ContactIDX(cursor.getLong(cursor.getColumnIndex(sqlStringJobs.COLUMN_FM_ContactIDX)));
            dbjobs.setTO_AddressIDX(cursor.getLong(cursor.getColumnIndex(sqlStringJobs.COLUMN_TO_AddressIDX)));
            dbjobs.setTO_ContactIDX(cursor.getLong(cursor.getColumnIndex(sqlStringJobs.COLUMN_TO_ContactIDX)));
            dbjobs.setExpType(cursor.getInt(cursor.getColumnIndex("ExpType")));
            dbjobs.setExpTypeDesc(cursor.getString(cursor.getColumnIndex("ExpTypeDesc")));
            dbjobs.setIsPartOne(cursor.getInt(cursor.getColumnIndex("IsPartOne")) == 0 ? false : true);
            dbjobs.setPartDesc(cursor.getString(cursor.getColumnIndex("PartDesc")));
            dbjobs.setIsDunning(cursor.getInt(cursor.getColumnIndex("IsDunning")) == 0 ? false : true);
            dbjobs.setHasDOCOD(cursor.getInt(cursor.getColumnIndex("HasDOCOD")) == 0 ? false : true);
            dbjobs.setHasTradeIn(cursor.getInt(cursor.getColumnIndex("HasTradeIn")) == 0 ? false : true);
            dbjobs.setJobNo(cursor.getString(cursor.getColumnIndex("JobNo")));
            dbjobs.setCNNo(cursor.getString(cursor.getColumnIndex("CNNo")));
            dbjobs.setCompanyName(cursor.getString(cursor.getColumnIndex("CompanyName")));
            dbjobs.setContactName(cursor.getString(cursor.getColumnIndex("ContactName")));
            dbjobs.setDO_INV_No(cursor.getString(cursor.getColumnIndex("DO_INV_No")));
            dbjobs.setDO_INV_IDX(cursor.getString(cursor.getColumnIndex("DO_INV_IDX")));
            dbjobs.setTAGS(cursor.getString(cursor.getColumnIndex("TAGS")));
            dbjobs.setFromDateTime(cursor.getString(cursor.getColumnIndex("FromDateTime")));
            dbjobs.setPickByDateTime(cursor.getString(cursor.getColumnIndex("PickByDateTime")));
            dbjobs.setReadyDateTime(cursor.getString(cursor.getColumnIndex("ReadyDateTime")));
            dbjobs.setToDateTime(cursor.getString(cursor.getColumnIndex("ToDateTime")));
            dbjobs.setExtDateTime(cursor.getString(cursor.getColumnIndex("ExtDateTime")));
            dbjobs.setFLAG(cursor.getInt(cursor.getColumnIndex("FLAG")));
            dbjobs.setPCS(cursor.getInt(cursor.getColumnIndex("PCS")));
            dbjobs.setStatusCodeIDX(cursor.getLong(cursor.getColumnIndex("StatusCodeIDX")));
            dbjobs.setIsNewlyInserted(cursor.getInt(cursor.getColumnIndex("IsNewlyInserted")));
            dbjobs.setJobInfoJsonStr(cursor.getString(cursor.getColumnIndex(sqlStringJobs.COLUMN_JobInfoJsonStr)));
            dbjobs.setCheckLists(cursor.getString(cursor.getColumnIndex(sqlStringJobs.COLUMN_CHECKLISTS)));
            dbjobs.setNetsVisaTi(cursor.getInt(cursor.getColumnIndex(sqlStringJobs.COLUMN_NetsVisaTI)));
            dbjobs.setisPickAndSendCount(cursor.getInt(cursor.getColumnIndex("isPickAndSendCount")));
            dbjobs.setJobChangesCount(cursor.getInt(cursor.getColumnIndex("JobChangesCount")));
            dbjobs.setHASLUNCHTIME(cursor.getInt(cursor.getColumnIndex("HASLUNCHTIM")) > 0 ? true : false);

            FromDateTimeConverted = sdf.format(new Date(cursor.getLong(cursor.getColumnIndex("FromDateTimeConverted")) * 1000L));
            ToDateTimeConverted = sdf.format(new Date(cursor.getLong(cursor.getColumnIndex("ToDateTimeConverted")) * 1000L));

            if (CatTypeIDX == 2 || CatTypeIDX == 3 || CatTypeIDX == 5 || CatTypeIDX == 6 || CatTypeIDX == 7) {
                ToDateTime = cursor.getLong(cursor.getColumnIndex("ToDateTime"));
                ExtFromDateTime = cursor.getLong(cursor.getColumnIndex("ExtFromDateTime"));
                ExtDateTime = cursor.getLong(cursor.getColumnIndex("ExtDateTime"));

                if (ExtFromDateTime > 0 && ExtFromDateTime != ToDateTime) {
                    //OR
                    ExtFromDateTimeConverted = sdf.format(new Date(cursor.getLong(cursor.getColumnIndex("ExtFromDateTime")) * 1000L));
                    ExtDateTimeConverted = sdf.format(new Date(cursor.getLong(cursor.getColumnIndex("ExtDateTime")) * 1000L));
                    dbjobs.setTimingTxt(cursor.getString(cursor.getColumnIndex("FromText")) + FromDateTimeConverted + "\n" +
                            cursor.getString(cursor.getColumnIndex("ToText")) + ToDateTimeConverted + "\n" + myApp.getAppContext().getString(R.string.lblor) + "\n" +
                            cursor.getString(cursor.getColumnIndex("FromText")) + ExtFromDateTimeConverted + "\n" +
                            cursor.getString(cursor.getColumnIndex("ToText")) + ExtDateTimeConverted
                    );
                } else if (ExtFromDateTime > 0 && ExtFromDateTime == ToDateTime) {
                    //EXT TIL
                    ExtDateTimeConverted = sdf.format(new Date(cursor.getLong(cursor.getColumnIndex("ExtDateTime")) * 1000L));
                    dbjobs.setTimingTxt(cursor.getString(cursor.getColumnIndex("FromText")) + FromDateTimeConverted + "\n" +
                            cursor.getString(cursor.getColumnIndex("ToText")) + ToDateTimeConverted + "\n" + myApp.getAppContext().getString(R.string.alnext) + " " + ExtDateTimeConverted);
                } else {
                    //NONE
                    dbjobs.setTimingTxt(cursor.getString(cursor.getColumnIndex("FromText")) + FromDateTimeConverted + "\n" +
                            cursor.getString(cursor.getColumnIndex("ToText")) + ToDateTimeConverted);
                }

                /*if (cursor.getLong(cursor.getColumnIndex("ExtDateTime")) > 0) {
                    ExtDateTimeConverted = sdf.format(new Date(cursor.getLong(cursor.getColumnIndex("ExtDateTime")) * 1000L));
                    dbjobs.setTimingTxt(cursor.getString(cursor.getColumnIndex("FromText")) + FromDateTimeConverted + "\n" +
                            cursor.getString(cursor.getColumnIndex("ToText")) + ToDateTimeConverted + "\n" + myApp.getAppContext().getString(R.string.alnext) + " " + ExtDateTimeConverted);
                } else {
                    dbjobs.setTimingTxt(cursor.getString(cursor.getColumnIndex("FromText")) + FromDateTimeConverted + "\n" +
                            cursor.getString(cursor.getColumnIndex("ToText")) + ToDateTimeConverted);
                }*/
            } else {
                dbjobs.setTimingTxt(cursor.getString(cursor.getColumnIndex("FromText")) + FromDateTimeConverted + "\n" +
                        cursor.getString(cursor.getColumnIndex("ToText")) + ToDateTimeConverted);
            }
                /*dbjobs.setTimingTxt(cursor.getString(cursor.getColumnIndex("FromText")) + FromDateTimeConverted + " " +
                    cursor.getString(cursor.getColumnIndex("ToText")) + ToDateTimeConverted);*/
        } catch (Exception e) {
            dbjobs = null;
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs cursorTodbObjJobs. Exception: " + e.getMessage().toString() + "\n" + "Stack: " + stack, "", "", "", "");
        } finally {
            return dbjobs;
        }
    }

    private static dbMedsJobs cursorToMedToColdbObjJobs(Cursor cursor) {
        dbMedsJobs dbjobs = null;
        String FromDateTimeConverted = "";
        String ToDateTimeConverted = "";
        String ExtFromDateTimeConverted = "";
        String ExtDateTimeConverted = "";
        //SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM h:mma");
        long ToDateTime, ExtFromDateTime, ExtDateTime;

        try {
            dbjobs = new dbMedsJobs();
            //dbjobs.set_ID(cursor.getLong(cursor.getColumnIndex("COLUMN_ID")));
            dbjobs.setJobsIDX(cursor.getLong(cursor.getColumnIndex("JobsIDX")));
            dbjobs.setPostal(cursor.getString(cursor.getColumnIndex("Postal")));
            dbjobs.setAddressWithPostalNoUnit(cursor.getString(cursor.getColumnIndex("AddressWithPostalNoUnit")));
            dbjobs.setAddressWithoutPostal(cursor.getString(cursor.getColumnIndex("AddressWithoutPostal")));
            dbjobs.setLAT(cursor.getDouble(cursor.getColumnIndex("LAT")));
            dbjobs.setLNG(cursor.getDouble(cursor.getColumnIndex("LNG")));
            dbjobs.setExpType(cursor.getInt(cursor.getColumnIndex("ExpType")));
            dbjobs.setExpTypeDesc(cursor.getString(cursor.getColumnIndex("ExpTypeDesc")));
            dbjobs.setJobNo(cursor.getString(cursor.getColumnIndex("JobNo")));
            dbjobs.setCNNo(cursor.getString(cursor.getColumnIndex("CNNo")));
            dbjobs.setFLAG(cursor.getInt(cursor.getColumnIndex("FLAG")));
            dbjobs.setFLAG2(cursor.getInt(cursor.getColumnIndex("FLAG2")));
            dbjobs.setPCS(cursor.getInt(cursor.getColumnIndex("PCS")));
            dbjobs.setFPCS(cursor.getInt(cursor.getColumnIndex("FPCS")));
            dbjobs.setPCS_SCANNED(cursor.getInt(cursor.getColumnIndex("PCS_SCANNED")));
            dbjobs.setJobInfoJsonStr(cursor.getString(cursor.getColumnIndex(sqlStringJobs.COLUMN_JobInfoJsonStr)));
            dbjobs.setCheckLists(cursor.getString(cursor.getColumnIndex(sqlStringJobs.COLUMN_CHECKLISTS)));
            dbjobs.setDelAddFull(cursor.getString(cursor.getColumnIndex(sqlStringJobs.COLUMN_DelAddFull)));
            dbjobs.setPUC(cursor.getInt(cursor.getColumnIndex("PUC")));

            FromDateTimeConverted = sdf.format(new Date(cursor.getLong(cursor.getColumnIndex("FromDateTimeConverted")) * 1000L));
            ToDateTimeConverted = sdf.format(new Date(cursor.getLong(cursor.getColumnIndex("ToDateTimeConverted")) * 1000L));

            dbjobs.setTimingTxt(cursor.getString(cursor.getColumnIndex("FromText")) + FromDateTimeConverted + "\n" +
                    cursor.getString(cursor.getColumnIndex("ToText")) + ToDateTimeConverted);

        } catch (Exception e) {
            dbjobs = null;
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs cursorToMedToColdbObjJobs. Exception: " + e.getMessage().toString() + "\n" + "Stack: " + stack, "", "", "", "");
        } finally {
            return dbjobs;
        }
    }

    private static objRouteJobEarliestTiming cursorToobjRouteJobEarliestTiming(Cursor cursor) {
        objRouteJobEarliestTiming j = null;
        try {
            j = new objRouteJobEarliestTiming();
            j.setStatusCodeIDX(cursor.getLong(cursor.getColumnIndex("StatusCodeIDX")));
            j.setExpectedTime(cursor.getString(cursor.getColumnIndex("expectedtime")));

            j.setReadyDateTime(cursor.getString(cursor.getColumnIndex("ReadyDateTime")));
            j.setPickByDateTime(cursor.getString(cursor.getColumnIndex("PickByDateTime")));
            j.setFromDateTime(cursor.getString(cursor.getColumnIndex("FromDateTime")));
            j.setToDateTime(cursor.getString(cursor.getColumnIndex("ToDateTime")));
            j.setExtDateTime(cursor.getString(cursor.getColumnIndex("ExtDateTime")));
            j.setExtFromDateTime(cursor.getString(cursor.getColumnIndex("ExtFromDateTime")));

            j.setLate(cursor.getInt(cursor.getColumnIndex("IsLate")) == 0 ? false : true);
        } catch (Exception e) {
            j = null;
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs cursorToobjRouteJobEarliestTiming. Exception: " + e.getMessage().toString() + "\n" + "Stack: " + stack, "", "", "", "");
        } finally {
            return j;
        }
    }

    private static dbObjCategoryCount cursorTodbObjCategoryCount2(Cursor cursor) {
        dbObjCategoryCount obj = null;
        try {
            obj = new dbObjCategoryCount();
            obj.setCount(cursor.getInt(cursor.getColumnIndex("CT")));

            obj.setReqNetsCount(cursor.getInt(cursor.getColumnIndex("ReqNetsCount")));
            obj.setReqVisaCount(cursor.getInt(cursor.getColumnIndex("ReqVisaCount")));
            obj.setReqTiCount(cursor.getInt(cursor.getColumnIndex("ReqTiCount")));
            obj.setColdCount(cursor.getInt(cursor.getColumnIndex("ColdCount")));
        } catch (Exception e) {
            obj = null;
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs cursorTodbObjCategoryCount2. Exception: " + e.getMessage().toString() + "\n" + "Stack: " + stack, "", "", "", "");
        } finally {
            return obj;
        }
    }

    private static dbObjCategoryCount cursorTodbObjCategoryCount(Cursor cursor) {
        dbObjCategoryCount obj = null;
        try {
            obj = new dbObjCategoryCount();
            obj.setCatTypeID(cursor.getInt(cursor.getColumnIndex("CatTypeID")));
            obj.setCount(cursor.getInt(cursor.getColumnIndex("CT")));

            obj.setReqNetsCount(cursor.getInt(cursor.getColumnIndex("ReqNetsCount")));
            obj.setReqVisaCount(cursor.getInt(cursor.getColumnIndex("ReqVisaCount")));
            obj.setReqTiCount(cursor.getInt(cursor.getColumnIndex("ReqTiCount")));
            obj.setColdCount(cursor.getInt(cursor.getColumnIndex("ColdCount")));
        } catch (Exception e) {
            obj = null;
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs cursorTodbObjCategoryCount. Exception: " + e.getMessage().toString() + "\n" + "Stack: " + stack, "", "", "", "");
        } finally {
            return obj;
        }
    }

    private static RouteLocation cursorToRouteLocation(Cursor cursor) {
        RouteLocation obj = null;
        try {
            obj = new RouteLocation();
            Location location = new Location();
            location.postal = cursor.getString(cursor.getColumnIndex("POSTAL"));
            location.full_address = cursor.getString(cursor.getColumnIndex("ADDRESS"));
            location.lat = String.valueOf(cursor.getDouble(cursor.getColumnIndex("LAT")));
            location.lon = String.valueOf(cursor.getDouble(cursor.getColumnIndex("LNG")));
            obj.Location = location;
        } catch (Exception e) {
            obj = null;
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs cursorToRouteLocation. Exception: " + e.getMessage().toString() + "\n" + "Stack: " + stack, "", "", "", "");
        } finally {
            return obj;
        }
    }

    private static dbObjAddressList cursorTodbObjAddressList(Cursor cursor, int CatTypeIDX) {
        dbObjAddressList obj = null;
        //SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM h:mma");
        String FromDateTimeConverted = "";
        String ToDateTimeConverted = "";
        String ExtFromDateTimeConverted = "";
        String ExtDateTimeConverted = "";
        long ToDateTime, ExtFromDateTime, ExtDateTime;
        int HasExt = 0;

        try {
            Context cxt = myApp.getAppContext();
            obj = new dbObjAddressList();
            obj.setJOBCOUNT(cursor.getInt(cursor.getColumnIndex("JOBCOUNT")));
            obj.setAddress(cursor.getString(cursor.getColumnIndex("ADDRESS")));
            obj.setPostal(cursor.getString(cursor.getColumnIndex("POSTAL")));
            obj.setFromDateTime(cursor.getLong(cursor.getColumnIndex("FromDateTime")));
            FromDateTimeConverted = sdf.format(new Date(cursor.getLong(cursor.getColumnIndex("FromDateTimeConverted")) * 1000L));

            obj.setFromDateTimeConverted(cursor.getString(cursor.getColumnIndex("FromDateTimeConverted")));
            obj.setFromText(cursor.getString(cursor.getColumnIndex("FromText")));
            obj.setToDateTime(cursor.getLong(cursor.getColumnIndex("ToDateTime")));
            ToDateTimeConverted = sdf.format(new Date(cursor.getLong(cursor.getColumnIndex("ToDateTimeConverted")) * 1000L));

            obj.setToDateTimeConverted(cursor.getString(cursor.getColumnIndex("ToDateTimeConverted")));
            obj.setToText(cursor.getString(cursor.getColumnIndex("ToText")));

            if (CatTypeIDX == 2 || CatTypeIDX == 3 || CatTypeIDX == 5 || CatTypeIDX == 6 || CatTypeIDX == 7) {
                obj.setExtDateTime(cursor.getLong(cursor.getColumnIndex("ExtDateTime")));

                HasExt = (cursor.getInt(cursor.getColumnIndex("HasExt")));

                if (obj.JOBCOUNT > 1) {
                    obj.setTimingText(cursor.getString(cursor.getColumnIndex("FromText")) + FromDateTimeConverted + "\n" +
                            cursor.getString(cursor.getColumnIndex("ToText")) + ToDateTimeConverted +
                            (HasExt > 0 ? "\n" + cxt.getString(R.string.clickforext) : "")
                    );
                } else {
                    ToDateTime = cursor.getLong(cursor.getColumnIndex("ToDateTime"));
                    ExtFromDateTime = cursor.getLong(cursor.getColumnIndex("ExtFromDateTime"));
                    ExtDateTime = cursor.getLong(cursor.getColumnIndex("ExtDateTime"));

                    if (ExtFromDateTime > 0 && ExtFromDateTime != ToDateTime) {
                        //OR
                        ExtFromDateTimeConverted = sdf.format(new Date(cursor.getLong(cursor.getColumnIndex("ExtFromDateTime")) * 1000L));
                        ExtDateTimeConverted = sdf.format(new Date(cursor.getLong(cursor.getColumnIndex("ExtDateTime")) * 1000L));
                        obj.setTimingText(
                                cursor.getString(cursor.getColumnIndex("FromText")) + FromDateTimeConverted + "\n" +
                                        cursor.getString(cursor.getColumnIndex("ToText")) + ToDateTimeConverted + "\n" + cxt.getString(R.string.lblor) + "\n" +
                                        cursor.getString(cursor.getColumnIndex("FromText")) + ExtFromDateTimeConverted + "\n" +
                                        cursor.getString(cursor.getColumnIndex("ToText")) + ExtDateTimeConverted
                        );
                    } else if (ExtFromDateTime > 0 && ExtFromDateTime == ToDateTime) {
                        //EXT TIL
                        ExtDateTimeConverted = sdf.format(new Date(cursor.getLong(cursor.getColumnIndex("ExtDateTime")) * 1000L));
                        obj.setTimingText(cursor.getString(cursor.getColumnIndex("FromText")) + FromDateTimeConverted + "\n" +
                                cursor.getString(cursor.getColumnIndex("ToText")) + ToDateTimeConverted + "\n" + cxt.getString(R.string.alnext) + " " + ExtDateTimeConverted);
                    } else {
                        //NONE
                        obj.setTimingText(cursor.getString(cursor.getColumnIndex("FromText")) + FromDateTimeConverted + "\n" +
                                cursor.getString(cursor.getColumnIndex("ToText")) + ToDateTimeConverted);
                    }
                }

                /*if (cursor.getLong(cursor.getColumnIndex("ExtDateTime")) > 0) {
                    ExtDateTimeConverted = sdf.format(new Date(cursor.getLong(cursor.getColumnIndex("ExtDateTime")) * 1000L));
                    obj.setTimingText(cursor.getString(cursor.getColumnIndex("FromText")) + FromDateTimeConverted + "\n" +
                            cursor.getString(cursor.getColumnIndex("ToText")) + ToDateTimeConverted + "\n" + myApp.getAppContext().getString(R.string.alnext) + " " + ExtDateTimeConverted);
                } else {
                    obj.setTimingText(cursor.getString(cursor.getColumnIndex("FromText")) + FromDateTimeConverted + "\n" + cursor.getString(cursor.getColumnIndex("ToText")) + ToDateTimeConverted);
                }*/
            } else {
                obj.setTimingText(cursor.getString(cursor.getColumnIndex("FromText")) + FromDateTimeConverted + "\n" + cursor.getString(cursor.getColumnIndex("ToText")) + ToDateTimeConverted);
            }

            obj.setSignaureFlag(cursor.getInt(cursor.getColumnIndex("SignaureFlagCount")) == 0 ? false : true);
            obj.setDNFFlag(cursor.getInt(cursor.getColumnIndex("DNFFlagCount")) == 0 ? false : true);
            obj.setOTPFlag(cursor.getInt(cursor.getColumnIndex("OTPFlagCount")) == 0 ? false : true);
            obj.setIsLate(cursor.getInt(cursor.getColumnIndex("IsLate")) == 0 ? false : true);
            obj.setHasNewlyInserted(cursor.getInt(cursor.getColumnIndex("NewlyInsertedCT")) == 0 ? false : true);
            obj.setLAT(cursor.getDouble(cursor.getColumnIndex("LAT")));
            obj.setLNG(cursor.getDouble(cursor.getColumnIndex("LNG")));
            obj.setJobInfoJsonStr(cursor.getString(cursor.getColumnIndex(sqlStringJobs.COLUMN_JobInfoJsonStr)));

            obj.setHasDOCOD(cursor.getInt(cursor.getColumnIndex("HasDOCODcount")) == 0 ? false : true);
            obj.setHasTradeIn(cursor.getInt(cursor.getColumnIndex("HasTradeIncount")) == 0 ? false : true);
            obj.setHasP2(cursor.getInt(cursor.getColumnIndex("HasP2")));

            obj.setIsIntendedRecOnlyCount(cursor.getInt(cursor.getColumnIndex("isIntendedRecOnlyCount")));
            obj.setisTotallyExplicitCount(cursor.getInt(cursor.getColumnIndex("isTotallyExplicitCount")));
            obj.setcanSlotLBCount(cursor.getInt(cursor.getColumnIndex("canSlotLBCount")));
            obj.setcanSlotDoorCount(cursor.getInt(cursor.getColumnIndex("canSlotDoorCount")));
            obj.setcanGuardCount(cursor.getInt(cursor.getColumnIndex("canGuardCount")));
            obj.setcanDoorstepCount(cursor.getInt(cursor.getColumnIndex("canDoorstepCount")));
            obj.setcontactlessCount(cursor.getInt(cursor.getColumnIndex("contactlessCount")));
            obj.setIsPickAndSendCount(cursor.getInt(cursor.getColumnIndex("isPickAndSendCount")));
            obj.setJobChangesCount(cursor.getInt(cursor.getColumnIndex("JobChangesCount")));
            obj.setPriorityCount(cursor.getInt(cursor.getColumnIndex("PriorityCount")));
            obj.setHASLUNCHTIME(cursor.getInt(cursor.getColumnIndex("HASLUNCHTIMEcount")) > 0 ? true : false);
        } catch (Exception e) {
            obj = null;
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs cursorTodbObjAddressList. Exception: " + e.getMessage().toString() + "\n" + "Stack: " + stack, "", "", "", "");
        } finally {
            return obj;
        }
    }

    private static dbObjTradeInList cursorTodbObjTradeInList(Cursor cursor) {
        dbObjTradeInList obj = null;
        try {
            obj = new dbObjTradeInList(cursor.getLong(cursor.getColumnIndex(sqlStringJobs.COLUMN_TRADEINLIST_ClientIDX)),
                    cursor.getString(cursor.getColumnIndex(sqlStringJobs.COLUMN_TRADEINLIST_Brand)),
                    cursor.getString(cursor.getColumnIndex(sqlStringJobs.COLUMN_TRADEINLIST_Model)),
                    cursor.getDouble(cursor.getColumnIndex(sqlStringJobs.COLUMN_TRADEINLIST_Value)),
                    cursor.getString(cursor.getColumnIndex("BrandModel")),
                    cursor.getString(cursor.getColumnIndex("BrandModelNoSpaces")));
        } catch (Exception e) {
            obj = null;
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs cursorTodbObjTradeInList. Exception: " + e.getMessage().toString() + "\n" + "Stack: " + stack, "", "", "", "");
        } finally {
            return obj;
        }
    }

    private static ChatMsgSenders cursorToChatMsgSenders(Cursor cursor, boolean takeaddress) {
        ChatMsgSenders cms = null;
        String username = "";
        String address = "";
        String jobsidx = "";
        Long JobsIDXL = Long.parseLong("0");
        try {
            jobsidx = cursor.getString(cursor.getColumnIndex(sqlStringJobs.COLUMN_JobsIDX));
            JobsIDXL = jobsidx.isEmpty() || jobsidx.equals("") ? Long.parseLong("0") : Long.parseLong(jobsidx);
            username = cursor.getString(cursor.getColumnIndex(sqlStringJobs.COLUMN_UserName));
            try {
                address = takeaddress ? cursor.getString(cursor.getColumnIndex("AddressWithPostalNoUnit")) : "";
                username += JobsIDXL == 0 ? "" : (address.isEmpty() || address.equals("")) ? "\n" + "Job not found." : "\n" + address;
            } catch (Exception e) {
                //do nothing
            }
            cms = new ChatMsgSenders(
                    String.valueOf(cursor.getLong(cursor.getColumnIndex(sqlStringJobs.COLUMN_UserIDX))),
                    username,
                    null,
                    cursor.getString(cursor.getColumnIndex(sqlStringJobs.COLUMN_messageTime)),
                    cursor.getInt(cursor.getColumnIndex(sqlStringJobs.COLUMN_READ)) == 1 ? true : false,
                    cursor.getInt(cursor.getColumnIndex(sqlStringJobs.COLUMN_notiID)),
                    cursor.getLong(cursor.getColumnIndex(sqlStringJobs.COLUMN_msgDateLong)),
                    cursor.getString(cursor.getColumnIndex(sqlStringJobs.COLUMN_JobsIDX)));
            cms.setUN(cursor.getString(cursor.getColumnIndex("UN")));
        } catch (Exception e) {
            cms = null;
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs cursorToChatMsgSenders. Exception: " + e.getMessage().toString() + "\n" + "Stack: " + stack, "", "", "", "");
        } finally {
            return cms;
        }
    }

    private static ChatMessage cursorToChatMessage(Cursor cursor) {
        ChatMessage cm = null;
        try {
            cm = new ChatMessage(
                    cursor.getInt(cursor.getColumnIndex(sqlStringJobs.COLUMN_SQLiteMessageID)),
                    String.valueOf(cursor.getString(cursor.getColumnIndex(sqlStringJobs.COLUMN_Message))),
                    UserType.getUserType(cursor.getInt(cursor.getColumnIndex(sqlStringJobs.COLUMN_UserType))),
                    Status.getStatus(cursor.getInt(cursor.getColumnIndex(sqlStringJobs.COLUMN_MessageStatus))),
                    cursor.getLong(cursor.getColumnIndex(sqlStringJobs.COLUMN_IDX)),
                    cursor.getLong(cursor.getColumnIndex(sqlStringJobs.COLUMN_TimeStamp)),
                    cursor.getLong(cursor.getColumnIndex(sqlStringJobs.COLUMN_TimeStamp))
            );
            cm.setDirection(cursor.getInt(cursor.getColumnIndex(sqlStringJobs.COLUMN_Direction)));
        } catch (Exception e) {
            cm = null;
            objGlobalMethods.writeToFile("Exception at DAO_Jobs cursorToChatMessage. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return cm;
        }
    }

    private static NFCTag cursorToNFCTag(Cursor cursor) {
        NFCTag obj = null;
        try {
            obj = new NFCTag(cursor.getLong(cursor.getColumnIndex(sqlStringJobs.COLUMN_NFCTAGS_CAIDX1)),
                    cursor.getLong(cursor.getColumnIndex(sqlStringJobs.COLUMN_NFCTAGS_CNIDX1)),
                    cursor.getLong(cursor.getColumnIndex(sqlStringJobs.COLUMN_NFCTAGS_CAIDX2)),
                    cursor.getLong(cursor.getColumnIndex(sqlStringJobs.COLUMN_NFCTAGS_CNIDX2)),
                    cursor.getString(cursor.getColumnIndex(sqlStringJobs.COLUMN_NFCTAGS_UID)));
        } catch (Exception e) {
            obj = null;
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs cursorToNFCTag. Exception: " + e.getMessage().toString() + "\n" + "Stack: " + stack, "", "", "", "");
        } finally {
            return obj;
        }
    }

    private static objPostalFlagSum cursorToobjPostalFlagSum(Cursor cursor) {
        objPostalFlagSum obj = null;
        try {
            obj = new objPostalFlagSum(cursor.getString(cursor.getColumnIndex(sqlStringJobs.COLUMN_Postal)),
                    cursor.getInt(cursor.getColumnIndex("FlagSum")));
        } catch (Exception e) {
            obj = null;
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at DAO_Jobs cursorToobjPostalFlagSum. Exception: " + e.getMessage().toString() + "\n" + "Stack: " + stack, "", "", "", "");
        } finally {
            return obj;
        }
    }

    private static objDOObject cursorToobjDOObject(Cursor cursor) {
        objDOObject obj = new objDOObject();
        try {
            obj.setInvoice(cursor.getString(cursor.getColumnIndex(sqlStringJobs.COLUMN_DO_INV_No)));
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at DAO_Jobs cursorToobjDOObject. Exception: " + s, "", "", "", "");
            obj = null;
        } finally {
            return obj;
        }
    }

    private static JobWithinTimeWindow cursorToJobWithinTimeWindow(Cursor cursor) {
        JobWithinTimeWindow obj = new JobWithinTimeWindow();
        try {
            obj.setTRUEFALSE(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex("TRUEFALSE"))));
            obj.setC1(cursor.getLong(cursor.getColumnIndex("C1")));
            obj.setC2(cursor.getLong(cursor.getColumnIndex("C2")));
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at DAO_Jobs cursorToJobWithinTimeWindow. Exception: " + s, "", "", "", "");
            obj = new JobWithinTimeWindow();
        } finally {
            return obj;
        }
    }

    private static objUnreadPUOKPFAILjobsMsgInfo cursorToobjUnreadPUOKPFAILjobsMsgInfo(Cursor c) {
        objUnreadPUOKPFAILjobsMsgInfo obj = new objUnreadPUOKPFAILjobsMsgInfo();
        try {
            obj.setJobsidx(c.getLong(c.getColumnIndex(sqlStringJobs.COLUMN_JobsIDX)));
            obj.setIdx(c.getLong(c.getColumnIndex(sqlStringJobs.COLUMN_IDX)));
            obj.setUseridx(c.getLong(c.getColumnIndex(sqlStringJobs.COLUMN_UserIDX)));
            obj.setCount(c.getInt(c.getColumnIndex("JC")));
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at DAO_Jobs cursorToobjUnreadPUOKPFAILjobsMsgInfo. Exception: " + s, "", "", "", "");
            obj = null;
        } finally {
            return obj;
        }
    }

}

