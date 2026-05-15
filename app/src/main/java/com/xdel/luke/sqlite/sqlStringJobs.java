package com.xdel.luke.sqlite;

import com.xdel.luke.JobsHandler;
import com.xdel.luke.PieceByPiecePU.PipPcs;
import com.xdel.luke.R;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by kean.ong on 16/05/2017.
 */
public class sqlStringJobs {
    public static final String TABLE_NAME = "Jobs";
    public static final String TABLE_NAME_TRADEINLIST = "Tradeinlist";
    public static final String TABLE_NAME_NFCTAGS = "NFCTags";
    public static final String TABLE_NAME_CABINET_UNIVERSAL_TBL = "CabinetUniversalTbl";
    public static final String TABLE_NAME_ChatMsgSenders_TBL = "ChatMsgSenders";
    public static final String TABLE_NAME_CHATMESSAGE_TBL = "ChatMessage";
    public static final String TABLE_NAME_JOBQUE_TBL = "JobQue";
    public static final String TABLE_NAME_CALLHISTORY_TBL = "CALLHISTORY";
    public static final String TABLE_NAME_TEMP_LOGGER_TBL = "TEMPLOGGER";
    public static final String TABLE_NAME_PIPPCS_TBL = "PIPPCS";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_JobsIDX = "JobsIDX";
    public static final String COLUMN_ClientIDX = "ClientIDX";
    public static final String COLUMN_ContactIDX = "ContactIDX";
    public static final String COLUMN_INDUSTRY = "INDUSTRY";
    public static final String COLUMN_CatTypeID = "CatTypeID";
    public static final String COLUMN_Postal = "Postal";
    public static final String COLUMN_AddressWithPostalNoUnit = "AddressWithPostalNoUnit";
    public static final String COLUMN_AddressWithoutPostal = "AddressWithoutPostal";
    public static final String COLUMN_AddressWithBlkStreet = "AddressWithBlkStreet";
    public static final String COLUMN_LAT = "LAT";
    public static final String COLUMN_LNG = "LNG";
    public static final String COLUMN_ExpType = "ExpType";
    public static final String COLUMN_ExpTypeDesc = "ExpTypeDesc";
    public static final String COLUMN_IsPartOne = "IsPartOne";
    public static final String COLUMN_PartDesc = "PartDesc";
    public static final String COLUMN_IsDunning = "IsDunning";
    public static final String COLUMN_JobNo = "JobNo";
    public static final String COLUMN_CNNo = "CNNo";
    public static final String COLUMN_CompanyName = "CompanyName";
    public static final String COLUMN_ContactName = "ContactName";
    public static final String COLUMN_DO_INV_No = "DO_INV_No";
    public static final String COLUMN_DO_INV_IDX = "DO_INV_IDX";
    public static final String COLUMN_TAGS = "TAGS";
    public static final String COLUMN_FromDateTime = "FromDateTime";
    public static final String COLUMN_PickByDateTime = "PickByDateTime";
    public static final String COLUMN_ReadyDateTime = "ReadyDateTime";
    public static final String COLUMN_ToDateTime = "ToDateTime";
    public static final String COLUMN_ExtDateTime = "ExtDateTime";
    public static final String COLUMN_FLAG = "FLAG";
    public static final String COLUMN_PCS = "PCS";
    public static final String COLUMN_PCS_SCANNED = "PCS_SCANNED";
    public static final String COLUMN_PCS_SCANNEDNOW = "PCS_SCANNEDNOW";
    public static final String COLUMN_StatusCodeIDX = "StatusCodeIDX";
    public static final String COLUMN_IsNewlyInserted = "IsNewlyInserted";
    public static final String COLUMN_JobInfoJsonStr = "JobInfoJsonStr";
    public static final String COLUMN_HasDOCOD = "HasDOCOD";
    public static final String COLUMN_HasTradeIn = "HasTradeIn";
    public static final String COLUMN_CHECKLISTS = "CHECKLISTS";
    public static final String COLUMN_FM_AddressIDX = "FM_AddressIDX";
    public static final String COLUMN_FM_ContactIDX = "FM_ContactIDX";
    public static final String COLUMN_TO_AddressIDX = "TO_AddressIDX";
    public static final String COLUMN_TO_ContactIDX = "TO_ContactIDX";
    public static final String COLUMN_NetsVisaTI = "NetsVisaTI";
    public static final String COLUMN_ExtCount = "ExtCount";
    public static final String COLUMN_PromisedFrom = "PromisedFrom";
    public static final String COLUMN_PromisedTo = "PromisedTo";
    public static final String COLUMN_FLAG2 = "FLAG2";
    public static final String COLUMN_SlotFlag = "SlotFlag";
    public static final String COLUMN_HASLUNCHTIME = "HASLUNCHTIM";
    public static final String COLUMN_UNPARK = "UNPARK";
    public static final String COLUMN_ExtFromDateTime = "ExtFromDateTime";
    public static final String COLUMN_DelAddFull = "DelAddFull";

    //Tradeinlist
    public static final String COLUMN_TRADEINLIST_ClientIDX = "ClientIDX";
    public static final String COLUMN_TRADEINLIST_Brand = "Brand";
    public static final String COLUMN_TRADEINLIST_Model = "Model";
    public static final String COLUMN_TRADEINLIST_Value = "Value";
    //Tradeinlist

    //NFCTags
    public static final String COLUMN_NFCTAGS_CAIDX1 = "CAIDX1";
    public static final String COLUMN_NFCTAGS_CNIDX1 = "CNIDX1";
    public static final String COLUMN_NFCTAGS_CAIDX2 = "CAIDX2";
    public static final String COLUMN_NFCTAGS_CNIDX2 = "CNIDX2";
    public static final String COLUMN_NFCTAGS_UID = "UID";
    //NFCTags

    //cabinet universal table\
    //cabinet ID column name shd b the default COLUMN_ID on top
    public static final String COLUMN_CABINET_DRIVERIDX = "DRIVERIDX";
    public static final String COLUMN_CABINET_JOBSIDX = "JOBSIDX";
    public static final String COLUMN_CABINET_PULID = "PULID";
    public static final String COLUMN_CABINET_OKPLID = "OKPLID";
    public static final String COLUMN_CABINET_BAGID = "BAGID";
    //cabinet universal table

    //insertUpdateJobStatusTBL
    public static final String COLUMN_ARegID = "ARegID";
    public static final String COLUMN_AJobsIDX = "AJobsIDX";

    //insert ChatMsgSenders

    public static final String COLUMN_messageTime = "messageTime";
    public static final String COLUMN_READ = "Read";
    public static final String COLUMN_notiID = "notiID";
    public static final String COLUMN_msgDateLong = "msgDateLong";

    //insert Messages
    public static final String COLUMN_SQLiteMessageID = "SQLiteMessageID";
    public static final String COLUMN_TimeStamp = "TimeStamp";
    public static final String COLUMN_IDX = "IDX";
    public static final String COLUMN_UserIDX = "UserIDX";
    public static final String COLUMN_UserName = "UserName";
    public static final String COLUMN_Message = "Message";
    public static final String COLUMN_Direction = "Direction";
    public static final String COLUMN_MessageStatus = "MessageStatus";
    public static final String COLUMN_UserType = "UserType";
    public static final String COLUMN_ackIDX = "ackIDX";
    public static final String COLUMN_ChatMsgSenders_id = "ChatMsgSenders_id";

    public static final String COLUMN_TelNum = "TelNum";
    public static final String COLUMN_CallCount = "CallCount";
    public static final String COLUMN_LastCallTimeStamp = "LastCallTimeStamp";
    public static final String COLUMN_ReqCallLogImg = "ReqCallLogImg";
    public static final String COLUMN_CallConnected = "CallConnected";

    //JobQue
    public static final String COLUMN_DRIVERIDX = "DRIVERIDX";
    public static final String COLUMN_OPS_Queue = "OPS_Queue";
    public static final String COLUMN_OPS_ETA = "OPS_ETA";
    public static final String COLUMN_GC_Queue = "GC_Queue";
    public static final String COLUMN_GC_ETA = "GC_ETA";
    public static final String COLUMN_VRP_Queue = "VRP_Queue";
    public static final String COLUMN_VRP_ETA = "VRP_ETA";
    public static final String COLUMN_PostalCode = "PostalCode";
    public static final String COLUMN_Latitude = "Latitude";
    public static final String COLUMN_Longitude = "Longitude";

    //templogger
    public static final String COLUMN_TEMPERATURE = "TEMPERATURE";

    public static String[] allColumns = {COLUMN_ID, COLUMN_JobsIDX, COLUMN_CatTypeID, COLUMN_Postal, COLUMN_AddressWithPostalNoUnit, COLUMN_AddressWithoutPostal, COLUMN_LAT, COLUMN_LNG
            , COLUMN_ExpType, COLUMN_ExpTypeDesc, COLUMN_IsPartOne, COLUMN_PartDesc, COLUMN_IsDunning, COLUMN_JobNo, COLUMN_CNNo, COLUMN_CompanyName, COLUMN_ContactName, COLUMN_DO_INV_No, COLUMN_DO_INV_IDX, COLUMN_TAGS, COLUMN_FromDateTime, COLUMN_PickByDateTime,
            COLUMN_ReadyDateTime, COLUMN_ToDateTime, COLUMN_ExtDateTime, COLUMN_FLAG, COLUMN_PCS, COLUMN_StatusCodeIDX, COLUMN_IsNewlyInserted, COLUMN_JobInfoJsonStr, COLUMN_CHECKLISTS};


    // Database creation sql statement
    public static final String CREATE_JOB_TABLE() {
        String sqlstr = "CREATE TABLE IF NOT EXISTS "
                + TABLE_NAME
                + "( " + COLUMN_ID + " integer primary key autoincrement, "
                + COLUMN_JobsIDX + " integer not null, "
                + COLUMN_ClientIDX + " integer not null, "
                + COLUMN_ContactIDX + " integer not null, "
                + COLUMN_INDUSTRY + " text, "
                + COLUMN_CatTypeID + " integer not null, "
                + COLUMN_Postal + " text, "
                + COLUMN_AddressWithPostalNoUnit + " text, "
                + COLUMN_AddressWithoutPostal + " text, "
                + COLUMN_LAT + " real, "
                + COLUMN_LNG + " real, "
                + COLUMN_FM_AddressIDX + " integer, "
                + COLUMN_FM_ContactIDX + " integer, "
                + COLUMN_TO_AddressIDX + " integer, "
                + COLUMN_TO_ContactIDX + " integer, "
                + COLUMN_ExpType + " integer not null, "
                + COLUMN_ExpTypeDesc + " text, "
                + COLUMN_IsPartOne + " integer , "
                + COLUMN_PartDesc + " text, "
                + COLUMN_IsDunning + " integer , "
                + COLUMN_JobNo + " text not null, "
                + COLUMN_CNNo + " text, "
                + COLUMN_CompanyName + " text, "
                + COLUMN_ContactName + " text, "
                + COLUMN_DO_INV_No + " text, "
                + COLUMN_DO_INV_IDX + " text, "
                + COLUMN_TAGS + " text, "
                + COLUMN_FromDateTime + " integer , "
                + COLUMN_PickByDateTime + " integer , "
                + COLUMN_ReadyDateTime + " integer , "
                + COLUMN_ToDateTime + " integer , "
                + COLUMN_ExtDateTime + " integer , "
                + COLUMN_FLAG + " integer, "
                + COLUMN_PCS + " integer, "
                + COLUMN_StatusCodeIDX + " integer, "
                + COLUMN_IsNewlyInserted + " integer, "
                + COLUMN_JobInfoJsonStr + " text, "
                + COLUMN_HasDOCOD + " integer, "
                + COLUMN_HasTradeIn + " integer, "
                + COLUMN_CHECKLISTS + " text, "
                + COLUMN_NetsVisaTI + " integer, "
                + COLUMN_ExtCount + " integer, "
                + COLUMN_PromisedFrom + " integer, "
                + COLUMN_PromisedTo + " integer, "
                + COLUMN_FLAG2 + " integer, "
                + COLUMN_SlotFlag + " integer, "
                + COLUMN_HASLUNCHTIME + " integer, "
                + COLUMN_UNPARK + " integer, "
                + COLUMN_ExtFromDateTime + " integer, "
                + COLUMN_DelAddFull + " text);";
        return sqlstr;
    }

    public static final String CREATE_PIPPCS_TABLE() {
        String sqlstr = "CREATE TABLE IF NOT EXISTS "
                + TABLE_NAME_PIPPCS_TBL
                + "( " + COLUMN_ID + " integer primary key autoincrement, "
                + COLUMN_JobsIDX + " integer UNIQUE, "
                + COLUMN_PCS + " integer not null, "
                + COLUMN_PCS_SCANNED + " integer not null, "
                + COLUMN_PCS_SCANNEDNOW + " integer not null);";
        return sqlstr;
    }

    public static final String CREATE_TRADE_IN_LIST_TABLE() {
        String sqlstr = "CREATE TABLE IF NOT EXISTS "
                + TABLE_NAME_TRADEINLIST
                + "( " + COLUMN_ID + " integer primary key autoincrement, "
                + COLUMN_TRADEINLIST_ClientIDX + " integer not null, "
                + COLUMN_TRADEINLIST_Brand + " text, "
                + COLUMN_TRADEINLIST_Model + " text, "
                + COLUMN_TRADEINLIST_Value + " real);";
        return sqlstr;
    }

    public static final String CREATE_CALLHISTORY_TABLE() {
        String sqlstr = "CREATE TABLE IF NOT EXISTS "
                + TABLE_NAME_CALLHISTORY_TBL
                + "( " + COLUMN_ID + " integer primary key autoincrement, "
                + COLUMN_TelNum + " text, "
                + COLUMN_CallCount + " integer, "
                + COLUMN_CatTypeID + " integer, "
                + COLUMN_LastCallTimeStamp + " integer, "
                + COLUMN_ReqCallLogImg + " integer, "
                + COLUMN_CallConnected + " integer);";
        return sqlstr;
    }

    public static final String CREATE_NFCTAGS_TABLE() {
        String sqlstr = "CREATE TABLE IF NOT EXISTS "
                + TABLE_NAME_NFCTAGS
                + "( " + COLUMN_ID + " integer primary key autoincrement, "
                + COLUMN_NFCTAGS_CAIDX1 + " integer, "
                + COLUMN_NFCTAGS_CNIDX1 + " integer, "
                + COLUMN_NFCTAGS_CAIDX2 + " integer, "
                + COLUMN_NFCTAGS_CNIDX2 + " integer, "
                + COLUMN_NFCTAGS_UID + " text);";
        return sqlstr;
    }

    public static final String CREATE_CABINET_UNIVERSAL_TABLE() {
        String sqlstr = "CREATE TABLE IF NOT EXISTS "
                + TABLE_NAME_CABINET_UNIVERSAL_TBL
                + "( " + COLUMN_ID + " integer primary key autoincrement, "
                + COLUMN_CABINET_JOBSIDX + " integer, "
                + COLUMN_CABINET_PULID + " integer, "
                + COLUMN_CABINET_OKPLID + " integer, "
                + COLUMN_CABINET_BAGID + " text);";
        return sqlstr;
    }

    public static final String DATABASE_GETDBOBJJOBSQUERY() {
        String sqlstr = "";

        try {
            sqlstr = "SELECT  JB." + COLUMN_ID + ", JB." + COLUMN_JobsIDX + ", JB." + COLUMN_CatTypeID + ", JB." + COLUMN_Postal + ", JB." + COLUMN_AddressWithPostalNoUnit + ", JB." + COLUMN_AddressWithoutPostal + ", JB." +
                    COLUMN_LAT + ", JB." + COLUMN_LNG + ", JB." + COLUMN_ExpType + ", JB." + COLUMN_ExpTypeDesc + ", JB." + COLUMN_IsPartOne + ", JB." + COLUMN_PartDesc + ", JB." + COLUMN_IsDunning
                    + ", JB." + COLUMN_HasDOCOD + ", JB." + COLUMN_HasTradeIn + ", JB." + COLUMN_JobNo + ", JB." +
                    COLUMN_CNNo + ", JB." + COLUMN_CompanyName + ", JB." + COLUMN_ContactName + ", JB." + COLUMN_DO_INV_No + ", JB." + COLUMN_DO_INV_IDX + ", JB." + COLUMN_TAGS + ", JB." + COLUMN_FromDateTime + ", JB." + COLUMN_PickByDateTime + ", JB." +
                    COLUMN_ReadyDateTime + ", JB." + COLUMN_ToDateTime + ", JB." + COLUMN_ExtFromDateTime + ", JB." + COLUMN_ExtDateTime + ", JB." + COLUMN_FLAG + ", JB." + COLUMN_PCS + ", JB." + COLUMN_StatusCodeIDX + ", JB." + COLUMN_IsNewlyInserted
                    + ", IFNULL(JB." + COLUMN_FM_AddressIDX + ",0) AS " + COLUMN_FM_AddressIDX + ", IFNULL(JB." + COLUMN_FM_ContactIDX + ",0) AS " + COLUMN_FM_ContactIDX
                    + ", IFNULL(JB." + COLUMN_TO_AddressIDX + ",0) AS " + COLUMN_TO_AddressIDX + " , IFNULL(JB." + COLUMN_TO_ContactIDX + ",0) AS " + COLUMN_TO_ContactIDX
                    + ", IFNULL(JB." + COLUMN_JobInfoJsonStr + ",'') AS " + COLUMN_JobInfoJsonStr + ", " +
                    " IFNULL(JB." + COLUMN_CHECKLISTS + ",'') AS " + COLUMN_CHECKLISTS + ", " +
                    " IFNULL(JB." + COLUMN_NetsVisaTI + ",'0') AS " + COLUMN_NetsVisaTI + ", " +
                    //================================================================================================================================================
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) THEN datetime(JB." + COLUMN_ReadyDateTime + ", 'unixepoch', 'localtime') " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN datetime(JB." + COLUMN_FromDateTime + ", 'unixepoch', 'localtime') " +
                    "    END ) AS FromDateTimeConverted , " +
                    //================================================================================================================================================
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) THEN 'PU FM ' " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN 'DEL FM ' " +
                    "    END ) AS FromText , " +
                    //================================================================================================================================================
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) " +
                    "           THEN ( CASE WHEN JB." + COLUMN_PickByDateTime + " = JB." + COLUMN_ReadyDateTime + " THEN datetime(JB." + COLUMN_ToDateTime + ", 'unixepoch', 'localtime') " +
                    "                       ELSE datetime(JB." + COLUMN_PickByDateTime + ", 'unixepoch', 'localtime') " +
                    "                END ) " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN datetime(JB." + COLUMN_ToDateTime + ", 'unixepoch', 'localtime') " +
                    "      END ) AS ToDateTimeConverted , " +
                    //================================================================================================================================================
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) " +
                    "           THEN ( CASE WHEN JB." + COLUMN_PickByDateTime + " = JB." + COLUMN_ReadyDateTime + " THEN 'DEL BY ' " +
                    "                       ELSE 'PU BY ' " +
                    "                 END ) " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN 'DEL BY ' " +
                    "    END ) AS ToText, " +
                    //================================================================================================================================================
                    "    0 isPickAndSendCount, 0 JobChangesCount, 0 HASLUNCHTIM " +
                    "FROM " + TABLE_NAME + " JB " +
                    "WHERE   ";
        } catch (Exception e) {
            sqlstr = "";
            Date dt = new Date();
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs DATABASE_GETDBOBJJOBSQUERY. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getTradeInListByBrandSQLstr(String Brand) {
        String sqlstr = "";
        String whereStr = "";
        try {
            whereStr = " TRADE." + COLUMN_TRADEINLIST_Brand + " = '" + Brand + "' ORDER BY TRADE." + COLUMN_TRADEINLIST_Model;
            sqlstr = "SELECT TRADE." + COLUMN_TRADEINLIST_ClientIDX + " AS ClientIDX, TRADE."
                    + COLUMN_TRADEINLIST_Brand + " AS Brand, TRADE."
                    + COLUMN_TRADEINLIST_Model + " AS Model, TRADE."
                    + COLUMN_TRADEINLIST_Value + " AS Value"
                    + " FROM " + TABLE_NAME_TRADEINLIST + " TRADE WHERE " + whereStr;
        } catch (Exception e) {
            sqlstr = "";
            Date dt = new Date();
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getTradeInListByBrandSQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getTradeInListByModelSQLstr(String Model, Double statedValue) {
        String sqlstr = "";
        String whereStr = "";
        String whereSubStr = "";

        try {
            whereSubStr = "UPPER((SELECT " + COLUMN_TRADEINLIST_Brand + " FROM " + TABLE_NAME_TRADEINLIST +
                    " TRADE2 WHERE REPLACE(UPPER(TRADE2." + COLUMN_TRADEINLIST_Brand + ")||UPPER(TRADE2."
                    + COLUMN_TRADEINLIST_Model + "),' ', '') = REPLACE(UPPER('" + Model + "'),' ','') LIMIT 1))";
            whereStr = " UPPER(TRADE." + COLUMN_TRADEINLIST_Brand + ") = " + whereSubStr
                    + " AND ((REPLACE(UPPER(TRADE." + COLUMN_TRADEINLIST_Brand + ")||' '||UPPER(TRADE." + COLUMN_TRADEINLIST_Model
                    + "),' ', '') = REPLACE(UPPER('" + Model + "'),' ','')) OR TRADE." + COLUMN_TRADEINLIST_Value + " = '" + statedValue + "')"
                    + " ORDER BY Model DESC";

            sqlstr = "SELECT TRADE." + COLUMN_TRADEINLIST_ClientIDX + " AS ClientIDX, TRADE."
                    + COLUMN_TRADEINLIST_Brand + " AS Brand, TRADE."
                    + COLUMN_TRADEINLIST_Model + " AS Model, TRADE."
                    + COLUMN_TRADEINLIST_Value + " AS Value, TRADE."
                    + COLUMN_TRADEINLIST_Brand + "||' '||TRADE." + COLUMN_TRADEINLIST_Model + " AS BrandModel, "
                    + "REPLACE(UPPER(TRADE." + COLUMN_TRADEINLIST_Brand + ")||''||UPPER(TRADE." + COLUMN_TRADEINLIST_Model + "),' ','') AS BrandModelNoSpaces"
                    + " FROM " + TABLE_NAME_TRADEINLIST + " TRADE WHERE " + whereStr;
        } catch (Exception e) {
            sqlstr = "";
            Date dt = new Date();
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getTradeInListByBrandSQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getTradeInListCountSQLstr() {
        String sqlstr = "";
        try {
            sqlstr = "SELECT COUNT(*) AS C FROM " + TABLE_NAME_TRADEINLIST;
        } catch (Exception e) {
            sqlstr = "";
            Date dt = new Date();
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getTradeInListCountSQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getInsertJobInfoSQLstr2() {
        String sqlstr = "";
        try {
            sqlstr = "INSERT INTO " + TABLE_NAME +
                    " (" +
                    COLUMN_JobsIDX + "," +
                    COLUMN_CatTypeID + "," +
                    COLUMN_Postal + "," +
                    COLUMN_AddressWithPostalNoUnit + "," +
                    COLUMN_AddressWithoutPostal + "," +
                    COLUMN_LAT + "," +
                    COLUMN_LNG + "," +
                    COLUMN_FM_AddressIDX + "," +
                    COLUMN_FM_ContactIDX + "," +
                    COLUMN_TO_AddressIDX + "," +
                    COLUMN_TO_ContactIDX + "," +
                    COLUMN_ExpType + "," +
                    COLUMN_ExpTypeDesc + "," +
                    COLUMN_IsPartOne + "," +
                    COLUMN_PartDesc + "," +
                    COLUMN_IsDunning + "," +
                    COLUMN_JobNo + "," +
                    COLUMN_CNNo + "," +
                    COLUMN_CompanyName + "," +
                    COLUMN_ContactName + "," +
                    COLUMN_DO_INV_No + "," +
                    COLUMN_DO_INV_IDX + "," +
                    COLUMN_TAGS + "," +
                    COLUMN_FromDateTime + "," +
                    COLUMN_PickByDateTime + "," +
                    COLUMN_ReadyDateTime + "," +
                    COLUMN_ToDateTime + "," +
                    COLUMN_ExtDateTime + "," +
                    COLUMN_FLAG + "," +
                    COLUMN_PCS + "," +
                    COLUMN_StatusCodeIDX + "," +
                    COLUMN_JobInfoJsonStr + "," +
                    COLUMN_HasDOCOD + "," +
                    COLUMN_HasTradeIn + "," +
                    COLUMN_CHECKLISTS + "," +
                    COLUMN_NetsVisaTI + "," +
                    COLUMN_AddressWithBlkStreet +
                    ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getInsertJobInfoSQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getInsertJobInfoSQLstr() {
        String sqlstr = "";
        try {
            sqlstr = "INSERT INTO " + TABLE_NAME +
                    " (" +
                    COLUMN_JobsIDX + "," +
                    COLUMN_ClientIDX + "," +
                    COLUMN_ContactIDX + "," +
                    COLUMN_INDUSTRY + "," +
                    COLUMN_CatTypeID + "," +
                    COLUMN_Postal + "," +
                    COLUMN_AddressWithPostalNoUnit + "," +
                    COLUMN_AddressWithoutPostal + "," +
                    COLUMN_LAT + "," +
                    COLUMN_LNG + "," +
                    COLUMN_FM_AddressIDX + "," +
                    COLUMN_FM_ContactIDX + "," +
                    COLUMN_TO_AddressIDX + "," +
                    COLUMN_TO_ContactIDX + "," +
                    COLUMN_ExpType + "," +
                    COLUMN_ExpTypeDesc + "," +
                    COLUMN_IsPartOne + "," +
                    COLUMN_PartDesc + "," +
                    COLUMN_IsDunning + "," +
                    COLUMN_JobNo + "," +
                    COLUMN_CNNo + "," +
                    COLUMN_CompanyName + "," +
                    COLUMN_ContactName + "," +
                    COLUMN_DO_INV_No + "," +
                    COLUMN_DO_INV_IDX + "," +
                    COLUMN_TAGS + "," +
                    COLUMN_FromDateTime + "," +
                    COLUMN_PickByDateTime + "," +
                    COLUMN_ReadyDateTime + "," +
                    COLUMN_ToDateTime + "," +
                    COLUMN_ExtDateTime + "," +
                    COLUMN_FLAG + "," +
                    COLUMN_PCS + "," +
                    COLUMN_StatusCodeIDX + "," +
                    COLUMN_JobInfoJsonStr + "," +
                    COLUMN_HasDOCOD + "," +
                    COLUMN_HasTradeIn + "," +
                    COLUMN_CHECKLISTS + "," +
                    COLUMN_NetsVisaTI + "," +
                    COLUMN_ExtCount + "," +
                    COLUMN_FLAG2 + "," +
                    COLUMN_SlotFlag + "," +
                    COLUMN_HASLUNCHTIME + "," +
                    COLUMN_ExtFromDateTime + "," +
                    COLUMN_DelAddFull +
                    ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getInsertJobInfoSQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getInsertTradeInListSQLstr() {
        String sqlstr = "";
        try {
            sqlstr = "INSERT INTO " + TABLE_NAME_TRADEINLIST +
                    " (" +
                    COLUMN_TRADEINLIST_ClientIDX + "," +
                    COLUMN_TRADEINLIST_Brand + "," +
                    COLUMN_TRADEINLIST_Model + "," +
                    COLUMN_TRADEINLIST_Value +
                    ") VALUES (?,?,?,?);";
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getInsertTradeInListSQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getInsertNFCTagsSQLstr() {
        String sqlstr = "";
        try {
            sqlstr = "INSERT INTO " + TABLE_NAME_NFCTAGS +
                    " (" +
                    COLUMN_NFCTAGS_CAIDX1 + "," +
                    COLUMN_NFCTAGS_CNIDX1 + "," +
                    COLUMN_NFCTAGS_CAIDX2 + "," +
                    COLUMN_NFCTAGS_CNIDX2 + "," +
                    COLUMN_NFCTAGS_UID +
                    ") VALUES (?,?,?,?,?);";
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getInsertNFCTagsSQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getInsertCabinetSQLstr() {
        String sqlstr = "";
        try {
            sqlstr = "INSERT INTO " + TABLE_NAME_CABINET_UNIVERSAL_TBL +
                    " (" +
                    COLUMN_CABINET_JOBSIDX + "," +
                    COLUMN_CABINET_PULID + "," +
                    COLUMN_CABINET_OKPLID + "," +
                    COLUMN_CABINET_BAGID +
                    ") VALUES (?,?,?,?);";
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getInsertCabinetSQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    //=================================================TEMPLOGGER=================================================

    public static final String CREATE_TEMPLOGGER_TABLE(){
        String sqlstr = "CREATE TABLE IF NOT EXISTS "
                + TABLE_NAME_TEMP_LOGGER_TBL
                + "( " + COLUMN_ID + " integer primary key autoincrement, "
                + COLUMN_JobsIDX + " integer not null, "
                + COLUMN_TEMPERATURE + " real not null, "
                + COLUMN_TimeStamp + " integer not null);";
        return sqlstr;
    }

    public static String getInsertTempLoggerSQLstr() {
        String sqlstr = "";
        try {
            sqlstr = "INSERT INTO " + TABLE_NAME_TEMP_LOGGER_TBL +
                    " (" +
                    COLUMN_JobsIDX + "," +
                    COLUMN_TEMPERATURE + "," +
                    COLUMN_TimeStamp +
                    ") VALUES (?,?,?);";
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getInsertTempLoggerSQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getJobTempByJobsIDX(String JobsIDX) {
        String sqlstr = "";
        try {
            sqlstr = "SELECT IFNULL(T." + COLUMN_TEMPERATURE + ", 0) " + COLUMN_TEMPERATURE + ", IFNULL(T." + COLUMN_TimeStamp + ", 0) " + COLUMN_TimeStamp
                    + " FROM " + TABLE_NAME_TEMP_LOGGER_TBL + "T WHERE T." + COLUMN_JobsIDX + " = " + JobsIDX;
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getJobTempByJobsIDX. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    //=================================================JobQue=================================================

    public static final String CREATE_JOBQUE_TABLE() {
        String sqlstr = "CREATE TABLE IF NOT EXISTS "
                + TABLE_NAME_JOBQUE_TBL
                + "( "
                + COLUMN_ID + " integer primary key autoincrement, "
                + COLUMN_DRIVERIDX + " integer, "
                + COLUMN_JobsIDX + " integer, "
                + COLUMN_OPS_Queue + " integer, "
                + COLUMN_OPS_ETA + " integer, "
                + COLUMN_GC_Queue + " integer, "
                + COLUMN_GC_ETA + " integer, "
                + COLUMN_VRP_Queue + " integer, "
                + COLUMN_VRP_ETA + " integer, "
                + COLUMN_PostalCode + " text, "
                + COLUMN_Latitude + " real, "
                + COLUMN_Longitude + " real);";
        return sqlstr;
    }

    public static String getInsertJobQue() {
        String sqlstr = "";
        try {
            sqlstr = "INSERT INTO " + TABLE_NAME_JOBQUE_TBL +
                    " (" +
                    COLUMN_DRIVERIDX + "," +
                    COLUMN_JobsIDX + "," +
                    COLUMN_OPS_Queue + "," +
                    COLUMN_OPS_ETA + "," +
                    COLUMN_GC_Queue + "," +
                    COLUMN_GC_ETA + "," +
                    COLUMN_VRP_Queue + "," +
                    COLUMN_VRP_ETA + "," +
                    COLUMN_PostalCode + "," +
                    COLUMN_Latitude + "," +
                    COLUMN_Longitude +
                    ") VALUES (?,?,?,?,?,?,?,?,?,?,?);";
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getInsertJobQue. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getAddressWithPostalNoUnitByPostalSQLstr(String postal) {
        String sqlstr = "";

        try {
            /*sqlstr = "SELECT DISTINCT IFNULL(" + COLUMN_AddressWithPostalNoUnit + ",'') " + COLUMN_AddressWithPostalNoUnit
                    + " FROM " + TABLE_NAME
                    + " WHERE " + COLUMN_Postal + "  = '" + postal
                    + "' AND " + COLUMN_StatusCodeIDX + " IN (103, 104) LIMIT 1";*/
            sqlstr = "SELECT DISTINCT IFNULL(" + COLUMN_AddressWithPostalNoUnit + ",'') " + COLUMN_AddressWithPostalNoUnit
                    + " FROM " + TABLE_NAME
                    + " WHERE " + COLUMN_Postal + "  = '" + postal
                    + "' AND " + COLUMN_CatTypeID + " IN (0, 2) LIMIT 1";
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at sqlStringJobs getAddressWithPostalNoUnitByPostalSQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
            sqlstr = "";
        } finally {
            return sqlstr;
        }
    }

    public static String getpNumdNumByJobsIDX(String JobsIDXs) {
        String sqlstr = "";

        try {
            sqlstr = "SELECT "
                    + "(SELECT COUNT(*) C FROM " + TABLE_NAME
                    + " WHERE " + COLUMN_JobsIDX + " IN (" + JobsIDXs + ") AND " + COLUMN_CatTypeID + " = 0) pNum, "
                    + "(SELECT COUNT(*) C FROM " + TABLE_NAME
                    + " WHERE " + COLUMN_JobsIDX + " IN (" + JobsIDXs + ") AND " + COLUMN_CatTypeID + " = 2) dNum";
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at sqlStringJobs getpNumdNumByJobsIDX. Exception: " + e.getMessage().toString(), "", "", "", "");
            sqlstr = "";
        } finally {
            return sqlstr;
        }
    }

    //=================================================Messages=================================================

    public static final String CREATE_ChatMsgSenders_TABLE() {
        /*String sqlstr = "CREATE TABLE IF NOT EXISTS "
                + TABLE_NAME_ChatMsgSenders_TBL
                + "( " + COLUMN_ID + " integer primary key autoincrement, "
                + COLUMN_UserIDX + " integer not null, "
                + COLUMN_UserName + " text UNIQUE, "
                + COLUMN_messageTime + " text, "
                + COLUMN_READ + " integer, "
                + COLUMN_notiID + " integer not null, "
                + COLUMN_msgDateLong + " integer, "
                + COLUMN_JobsIDX + " integer not null UNIQUE);";*/
        String sqlstr = "CREATE TABLE IF NOT EXISTS "
                + TABLE_NAME_ChatMsgSenders_TBL
                + "( " + COLUMN_ID + " integer primary key autoincrement, "
                + COLUMN_UserIDX + " integer not null, "
                + COLUMN_UserName + " text, "
                + COLUMN_messageTime + " text, "
                + COLUMN_READ + " integer, "
                + COLUMN_notiID + " integer not null, "
                + COLUMN_msgDateLong + " integer, "
                + COLUMN_JobsIDX + " integer not null, UNIQUE(" + COLUMN_UserIDX + "," + COLUMN_JobsIDX + ") ON CONFLICT IGNORE);";
        return sqlstr;
    }

    public static final String CREATE_ChatMessage_TABLE() {
        String sqlstr = "CREATE TABLE IF NOT EXISTS "
                + TABLE_NAME_CHATMESSAGE_TBL
                + "( " + COLUMN_SQLiteMessageID + " integer primary key autoincrement, "
                + COLUMN_TimeStamp + " integer not null, "
                + COLUMN_IDX + " integer not null, "
                + COLUMN_UserIDX + " integer not null, "
                + COLUMN_UserName + " text, "
                + COLUMN_JobsIDX + " integer not null, "
                + COLUMN_Message + " text not null, "
                + COLUMN_Direction + " integer, "
                + COLUMN_MessageStatus + " integer, "
                + COLUMN_UserType + " integer, "
                + COLUMN_ackIDX + " integer);";
        return sqlstr;
    }

    public static String getInsertChatMsgSendersSQLstr2() {
        String sqlstr = "";
        try {
            sqlstr = "INSERT INTO " + TABLE_NAME_ChatMsgSenders_TBL +
                    " (" +
                    COLUMN_UserIDX + "," +
                    COLUMN_UserName + "," +
                    COLUMN_messageTime + "," +
                    COLUMN_READ + "," +
                    COLUMN_notiID + "," +
                    COLUMN_msgDateLong + "," +
                    COLUMN_JobsIDX +
                    ") VALUES (?,?,?,?,?,?,?);";
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getInsertChatMsgSendersSQLstr2. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getinsertChatMsgSendersIfNotExistSQLstr() {
        String sqlstr = "";
        try {
            sqlstr = "INSERT OR IGNORE INTO " + TABLE_NAME_ChatMsgSenders_TBL +
                    " (" +
                    COLUMN_UserIDX + "," +
                    COLUMN_UserName + "," +
                    COLUMN_messageTime + "," +
                    COLUMN_READ + "," +
                    COLUMN_notiID + "," +
                    COLUMN_msgDateLong + "," +
                    COLUMN_JobsIDX +
                    ") VALUES (?,?,?,?,?,?,?);";
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getinsertChatMsgSendersIfNotExistSQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getInsertChatMsgSendersSQLstr() {
        String sqlstr = "";
        try {
            sqlstr = "INSERT INTO " + TABLE_NAME_ChatMsgSenders_TBL +
                    " (" +
                    COLUMN_UserIDX + "," +
                    COLUMN_UserName + "," +
                    COLUMN_messageTime + "," +
                    COLUMN_READ + "," +
                    COLUMN_notiID + "," +
                    COLUMN_msgDateLong +
                    ") VALUES (?,?,?,?,?,?);";
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getInsertChatMsgSendersSQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getChatMsgSendersSQLstr2(Long UserIDX, String aUserName) {
        String sqlstr = "";
        try {
            String useridx = UserIDX == null || UserIDX == 0 ? "0" : String.valueOf(UserIDX);
            sqlstr = "SELECT * FROM " + TABLE_NAME_ChatMsgSenders_TBL + " CMS WHERE CMS." + COLUMN_UserIDX + " = " + useridx + " AND CMS." + COLUMN_UserName + " = '" + aUserName + "'";
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getChatMsgSendersSQLstr2. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getChatMsgSendersWithJobsIDXnChatMsgSendersIDXListSQLstr2(Long aJobsIDX) {
        String sqlstr = "";
        try {
            String JobsIDX = aJobsIDX == null || aJobsIDX == 0 ? "0" : String.valueOf(aJobsIDX);
            //sqlstr = "SELECT * FROM " + TABLE_NAME_ChatMsgSenders_TBL + " CMS WHERE CMS." + COLUMN_UserIDX + " = " + UserIDX + " AND CMS." + COLUMN_JobsIDX + " = " + JobsIDX;
            sqlstr = "SELECT CMS.*, (SELECT IFNULL(CM." + COLUMN_UserName + ", '') FROM " + TABLE_NAME_CHATMESSAGE_TBL +
                    " CM WHERE CM." + COLUMN_UserIDX + " = CMS." + COLUMN_UserIDX + " AND CM." + COLUMN_JobsIDX + " = CMS." + COLUMN_JobsIDX + " LIMIT 1) UN FROM " +
                    TABLE_NAME_ChatMsgSenders_TBL + " CMS WHERE CMS." + COLUMN_JobsIDX + " = " + JobsIDX;
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getChatMsgSendersWithJobsIDXnChatMsgSendersIDXSQLstr2. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getChatMsgSendersWithJobsIDXnChatMsgSendersIDXSQLstr2(Long aUserIDX, Long aJobsIDX) {
        String sqlstr = "";
        try {
            String UserIDX = aUserIDX == null || aUserIDX == 0 ? "0" : String.valueOf(aUserIDX);
            String JobsIDX = aJobsIDX == null || aJobsIDX == 0 ? "0" : String.valueOf(aJobsIDX);
            //sqlstr = "SELECT * FROM " + TABLE_NAME_ChatMsgSenders_TBL + " CMS WHERE CMS." + COLUMN_UserIDX + " = " + UserIDX + " AND CMS." + COLUMN_JobsIDX + " = " + JobsIDX;
            sqlstr = "SELECT CMS.*, (SELECT IFNULL(CM." + COLUMN_UserName + ", '') FROM " + TABLE_NAME_CHATMESSAGE_TBL +
                    " CM WHERE CM." + COLUMN_UserIDX + " = CMS." + COLUMN_UserIDX + " AND CM." + COLUMN_JobsIDX + " = CMS." + COLUMN_JobsIDX + " LIMIT 1) UN FROM " +
                    TABLE_NAME_ChatMsgSenders_TBL + " CMS WHERE CMS." + COLUMN_UserIDX + " = " + UserIDX + " AND CMS." + COLUMN_JobsIDX + " = " + JobsIDX;
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getChatMsgSendersWithJobsIDXnChatMsgSendersIDXSQLstr2. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getChatMsgSendersSQLstr(Long UserIDX) {
        String sqlstr = "";
        try {
            String useridx = UserIDX == null || UserIDX == 0 ? "0" : String.valueOf(UserIDX);
            sqlstr = "SELECT * FROM " + TABLE_NAME_ChatMsgSenders_TBL + " CMS WHERE CMS." + COLUMN_UserIDX + " = " + useridx;
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getChatMsgSendersSQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getChatMsgSendersListSQLstr() {
        String sqlstr = "";
        try {
            //sqlstr = "SELECT CMS.*, (SELECT UserName FROM " + TABLE_NAME_CHATMESSAGE_TBL + " CM WHERE CM.UserIDX = CMS.UserIDX LIMIT 1) UN FROM " + TABLE_NAME_ChatMsgSenders_TBL + " CMS ORDER BY CMS.msgDateLong DESC";
            sqlstr = "SELECT CMS.*, (SELECT UserName FROM " + TABLE_NAME_CHATMESSAGE_TBL
                    + " CM WHERE CM.UserIDX = CMS.UserIDX LIMIT 1) UN, IFNULL(J.AddressWithPostalNoUnit,'') AddressWithPostalNoUnit FROM "
                    + TABLE_NAME_ChatMsgSenders_TBL + " CMS LEFT JOIN " + TABLE_NAME + " J ON J." + COLUMN_JobsIDX
                    + " = CMS." + COLUMN_JobsIDX + " ORDER BY CMS.msgDateLong DESC";
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getChatMsgSendersListSQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getInsertChatMessageSQLstr() {
        String sqlstr = "";
        try {
            sqlstr = "INSERT INTO " + TABLE_NAME_CHATMESSAGE_TBL +
                    " (" +
                    COLUMN_TimeStamp + "," +
                    COLUMN_IDX + "," +
                    COLUMN_UserIDX + "," +
                    COLUMN_UserName + "," +
                    COLUMN_JobsIDX + "," +
                    COLUMN_Message + "," +
                    COLUMN_Direction + "," +
                    COLUMN_MessageStatus + "," +
                    COLUMN_UserType + "," +
                    COLUMN_ackIDX +
                    ") VALUES (?,?,?,?,?,?,?,?,?,?);";
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getInsertChatMessageSQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getChatMsgSendersCountSQLstr() {
        String sqlstr = "";
        try {
            sqlstr = "SELECT (SELECT COUNT(*) AS C1 FROM " + TABLE_NAME_ChatMsgSenders_TBL + ") + (SELECT COUNT(*) AS C2 FROM + " + TABLE_NAME_CHATMESSAGE_TBL + ") AS CC";
        } catch (Exception e) {
            sqlstr = "";
            //String s = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getChatMsgSendersCountSQLstr. Exception: " + e.getMessage().toString() + "\nStack: " + s,"","","","");
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getChatMsgSendersCountSQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getLastMessageByChatMsgSendersIDX(Long _id) {
        String sqlstr = "";
        try {
            sqlstr = "SELECT * FROM " + TABLE_NAME_CHATMESSAGE_TBL + " MSG WHERE MSG." + COLUMN_ChatMsgSenders_id + " = " + _id + " ORDER BY MSG." + COLUMN_TimeStamp + " DESC LIMIT 1";
        } catch (Exception e) {
            sqlstr = "";
            //String s = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getLastMessageByUserIDX. Exception: " + e.getMessage().toString() + "\nStack: " + s,"","","","");
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getLastMessageByChatMsgSendersIDX. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getLastMessageByUserIDX2(Long JobsIDX, Long UserIDX) {
        String sqlstr = "";
        try {
            //sqlstr = "SELECT * FROM " + TABLE_NAME_CHATMESSAGE_TBL + " MSG WHERE MSG." + COLUMN_JobsIDX + " = " + JobsIDX + " AND MSG." + COLUMN_UserIDX + " = " + UserIDX + " ORDER BY MSG." + COLUMN_TimeStamp + " DESC LIMIT 1";
            /*sqlstr = "SELECT MSG.*, (CASE WHEN IFNULL(J." + COLUMN_CNNo + ",'') == '' THEN J." + COLUMN_JobNo + " ELSE J." + COLUMN_CNNo + " END) AS " + COLUMN_CNNo + " FROM " + TABLE_NAME_CHATMESSAGE_TBL +
                    " MSG LEFT JOIN " + TABLE_NAME + " J ON J." + COLUMN_JobsIDX + " = MSG." + COLUMN_JobsIDX + " WHERE MSG." + COLUMN_JobsIDX + " = '" + JobsIDX +
                    "' AND MSG." + COLUMN_UserIDX + " = '" + UserIDX + "' ORDER BY MSG." + COLUMN_TimeStamp + " DESC LIMIT 1";*/
            sqlstr = "SELECT MSG.*, (SELECT (CASE WHEN IFNULL(J." + COLUMN_CNNo + ",'') == '' THEN J." + COLUMN_JobNo + " ELSE J." + COLUMN_CNNo + " END) FROM " + TABLE_NAME + " J WHERE J."
                    + COLUMN_JobsIDX + " = ?) AS " + COLUMN_CNNo + " FROM " + TABLE_NAME_CHATMESSAGE_TBL + " MSG where MSG." + COLUMN_JobsIDX + " = ? AND MSG." + COLUMN_UserIDX
                    + " = ? ORDER BY MSG." + COLUMN_TimeStamp + " DESC LIMIT 1";
        } catch (Exception e) {
            sqlstr = "";
            //String s = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getLastMessageByUserIDX. Exception: " + e.getMessage().toString() + "\nStack: " + s,"","","","");
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getLastMessageByUserIDX2. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getLastMessageForAllUsers() {
        String sqlstr = "";
        try {
            /*sqlstr = "SELECT DISTINCT CM."+ COLUMN_UserIDX + ", CM." + COLUMN_UserName + " UN" +
                    ", (CASE WHEN CM." + COLUMN_JobsIDX + " == 0 THEN CM." + COLUMN_UserName + " ELSE CM." + COLUMN_UserName +
                    " || ' (' || (CASE WHEN IFNULL(J." + COLUMN_CNNo + ",'') == '' THEN J." + COLUMN_JobNo + " ELSE J." + COLUMN_CNNo + " END) || ')' END) " + COLUMN_UserName +
                    ", (CASE WHEN IFNULL(J." + COLUMN_CNNo + ",'') == '' THEN J." + COLUMN_JobNo + " ELSE J." + COLUMN_CNNo + " END) " + COLUMN_CNNo +
                    ", CM." + COLUMN_JobsIDX + ", (SELECT CM2." + COLUMN_TimeStamp + " FROM " + TABLE_NAME_CHATMESSAGE_TBL + " CM2 WHERE CM2." + COLUMN_UserIDX +
                    " = CM." + COLUMN_UserIDX + " AND CM2." + COLUMN_JobsIDX + " = CM." + COLUMN_JobsIDX + " ORDER BY CM2." + COLUMN_TimeStamp + " DESC LIMIT 1) " + COLUMN_TimeStamp +
                    ", (SELECT CM2." + COLUMN_Direction + " FROM " + TABLE_NAME_CHATMESSAGE_TBL + " CM2 WHERE CM2." + COLUMN_UserIDX + " = CM." + COLUMN_UserIDX +
                    " AND CM2." + COLUMN_JobsIDX + " = CM." + COLUMN_JobsIDX + " ORDER BY CM2." + COLUMN_TimeStamp + " DESC LIMIT 1) " + COLUMN_Direction +
                    " FROM " + TABLE_NAME_CHATMESSAGE_TBL + " CM LEFT JOIN " + TABLE_NAME + " J ON J." + COLUMN_JobsIDX + " = CM." + COLUMN_JobsIDX +
                    " WHERE CM." + COLUMN_UserName + " <> '' " +
                    " ORDER BY " + COLUMN_TimeStamp + " DESC";*/

            //sqlstr = "SELECT DISTINCT CM.UserIDX, CM.UserName UN, (CASE WHEN CM.JobsIDX == 0 THEN CM.UserName ELSE CM.UserName || ' (' || IFNULL((CASE WHEN IFNULL(J.CNNo,'') == '' THEN J.JobNo ELSE J.CNNo END), CM.JobsIDX) || ')' END) UserName, (CASE WHEN IFNULL(J.CNNo,'') == '' THEN J.JobNo ELSE J.CNNo END) CNNo, CM.JobsIDX, (SELECT CM2.TimeStamp FROM ChatMessage CM2 WHERE CM2.UserIDX = CM.UserIDX AND CM2.JobsIDX = CM.JobsIDX ORDER BY CM2.TimeStamp DESC LIMIT 1) TimeStamp, (SELECT CM2.Direction FROM ChatMessage CM2 WHERE CM2.UserIDX = CM.UserIDX AND CM2.JobsIDX = CM.JobsIDX ORDER BY CM2.TimeStamp DESC LIMIT 1) Direction FROM ChatMessage CM LEFT JOIN Jobs J ON J.JobsIDX = CM.JobsIDX ORDER BY TimeStamp DESC";
            sqlstr = "SELECT DISTINCT CM." + COLUMN_UserIDX + ", CM." + COLUMN_UserName + " UN" +
                    ", (CASE WHEN CM." + COLUMN_JobsIDX + " == 0 THEN CM." + COLUMN_UserName + " ELSE CM." + COLUMN_UserName +
                    " || ' (' || IFNULL((CASE WHEN IFNULL(J." + COLUMN_CNNo + ",'') == '' THEN J." + COLUMN_JobNo + " ELSE J." + COLUMN_CNNo + " END), CM." + COLUMN_JobsIDX + ") || ')' END) " + COLUMN_UserName +
                    ", (CASE WHEN IFNULL(J." + COLUMN_CNNo + ",'') == '' THEN J." + COLUMN_JobNo + " ELSE J." + COLUMN_CNNo + " END) " + COLUMN_CNNo +
                    ", CM." + COLUMN_JobsIDX + ", (SELECT CM2." + COLUMN_TimeStamp + " FROM " + TABLE_NAME_CHATMESSAGE_TBL + " CM2 WHERE CM2." + COLUMN_UserIDX +
                    " = CM." + COLUMN_UserIDX + " AND CM2." + COLUMN_JobsIDX + " = CM." + COLUMN_JobsIDX + " ORDER BY CM2." + COLUMN_TimeStamp + " DESC LIMIT 1) " + COLUMN_TimeStamp +
                    ", (SELECT CM2." + COLUMN_Direction + " FROM " + TABLE_NAME_CHATMESSAGE_TBL + " CM2 WHERE CM2." + COLUMN_UserIDX + " = CM." + COLUMN_UserIDX +
                    " AND CM2." + COLUMN_JobsIDX + " = CM." + COLUMN_JobsIDX + " ORDER BY CM2." + COLUMN_TimeStamp + " DESC LIMIT 1) " + COLUMN_Direction +
                    " FROM " + TABLE_NAME_CHATMESSAGE_TBL + " CM LEFT JOIN " + TABLE_NAME + " J ON J." + COLUMN_JobsIDX + " = CM." + COLUMN_JobsIDX +
                    " ORDER BY " + COLUMN_TimeStamp + " DESC";
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getAllLastMessage. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getJobCnno(Long JobsIDX) {
        String sqlstr = "";
        try {
            sqlstr = "SELECT IFNULL(" + COLUMN_CNNo + ", " + COLUMN_JobNo + ") AS " + COLUMN_CNNo + " FROM " + TABLE_NAME + " J WHERE J." + COLUMN_JobsIDX + " = " + JobsIDX;
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getJobCnno. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getLastMessageByUserIDX(Long UserIDX) {
        String sqlstr = "";
        try {
            sqlstr = "SELECT * FROM " + TABLE_NAME_CHATMESSAGE_TBL + " MSG WHERE MSG." + COLUMN_UserIDX + " = " + UserIDX + " ORDER BY MSG." + COLUMN_TimeStamp + " DESC LIMIT 1";
        } catch (Exception e) {
            sqlstr = "";
            //String s = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getLastMessageByUserIDX. Exception: " + e.getMessage().toString() + "\nStack: " + s,"","","","");
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getLastMessageByUserIDX. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getAllMsgByJobsIDXnChatMsgSendersIDX(Long JobsIDX, Long UserIDX) {
        String sqlstr = "";
        try {
            sqlstr = "SELECT * FROM " + TABLE_NAME_CHATMESSAGE_TBL + " MSG WHERE MSG." + COLUMN_JobsIDX + " = " + JobsIDX + " AND MSG." + COLUMN_UserIDX + " = " + UserIDX + " ORDER BY MSG." + COLUMN_TimeStamp;
        } catch (Exception e) {
            sqlstr = "";
            //String s = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getAllMsgByUserIDX. Exception: " + e.getMessage().toString() + "\nStack: " + s,"","","","");
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getAllMsgByChatMsgSendersIDX. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getAllMsgByChatMsgSendersIDX(Long _id) {
        String sqlstr = "";
        try {
            sqlstr = "SELECT * FROM " + TABLE_NAME_CHATMESSAGE_TBL + " MSG WHERE MSG." + COLUMN_ChatMsgSenders_id + " = " + _id + " ORDER BY MSG." + COLUMN_TimeStamp;
        } catch (Exception e) {
            sqlstr = "";
            //String s = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getAllMsgByUserIDX. Exception: " + e.getMessage().toString() + "\nStack: " + s,"","","","");
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getAllMsgByChatMsgSendersIDX. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getAllMsgByUserIDX(Long UserIDX) {
        String sqlstr = "";
        try {
            sqlstr = "SELECT * FROM " + TABLE_NAME_CHATMESSAGE_TBL + " MSG WHERE MSG." + COLUMN_UserIDX + " = " + UserIDX + " ORDER BY MSG." + COLUMN_TimeStamp;
        } catch (Exception e) {
            sqlstr = "";
            //String s = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getAllMsgByUserIDX. Exception: " + e.getMessage().toString() + "\nStack: " + s,"","","","");
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getAllMsgByUserIDX. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getUnreadMessageCount() {
        String sqlstr = "";
        try {
            sqlstr = "SELECT COUNT(*) C FROM " + TABLE_NAME_ChatMsgSenders_TBL + " WHERE " + COLUMN_READ + " = 0";
        } catch (Exception e) {
            sqlstr = "";
        } finally {
            return sqlstr;
        }
    }

    public static String getJobCountByJobsIDXSQLstr(long JobsIDX) {
        String sqlstr = "";
        try {
            sqlstr = "SELECT COUNT(*) C FROM " + TABLE_NAME + " WHERE " + COLUMN_JobsIDX + " = " + JobsIDX;
        } catch (Exception e) {
            sqlstr = "";
        } finally {
            return sqlstr;
        }
    }

    //=================================================Messages=================================================

    //Database get Dashboard Category count list
    public static final String DATABASE_GETCATEGORYCOUNT() {
        String sqlstr = "";
        try {
            sqlstr = "SELECT DISTINCT " + COLUMN_CatTypeID + ", " +
                    "(SELECT COUNT (*) AS C FROM " + TABLE_NAME + " T2 WHERE T2." + COLUMN_CatTypeID + " = T." + COLUMN_CatTypeID + ") AS CT " +
                    "FROM " + TABLE_NAME + " T " +
                    "ORDER BY T." + COLUMN_CatTypeID;
        } catch (Exception e) {
            sqlstr = "";
        } finally {
            return sqlstr;
        }
    }

    public static final String DATABASE_GETCATEGORYCOUNT2() {
        String sqlstr = "";
        try {
            sqlstr = "SELECT DISTINCT " + COLUMN_CatTypeID + ", " +
                    "(SELECT COUNT (*) AS C FROM " + TABLE_NAME + " T2 WHERE T2." + COLUMN_CatTypeID + " = T." + COLUMN_CatTypeID + ") AS CT, " +
                    "(SELECT COUNT(*) AS C FROM " + TABLE_NAME + " T3 WHERE T3." + COLUMN_CatTypeID + " = T." + COLUMN_CatTypeID + " AND (FLAG2 & 64) == 64) AS ColdCount, " +
                    "(SELECT COUNT(*) AS C FROM " + TABLE_NAME + " T3 WHERE T3." + COLUMN_CatTypeID + " = T." + COLUMN_CatTypeID + " AND (NetsVisaTi & 4) == 4) AS ReqNetsCount, " +
                    "(SELECT COUNT(*) AS C FROM " + TABLE_NAME + " T3 WHERE T3." + COLUMN_CatTypeID + " = T." + COLUMN_CatTypeID + " AND (NetsVisaTi & 8) == 8) AS ReqVisaCount, " +
                    "(SELECT COUNT(*) AS C FROM " + TABLE_NAME + " T3 WHERE T3." + COLUMN_CatTypeID + " = T." + COLUMN_CatTypeID + " AND (NetsVisaTi & 16) == 16) AS ReqTiCount " +
                    "FROM " + TABLE_NAME + " T " +
                    "ORDER BY T." + COLUMN_CatTypeID;
        } catch (Exception e) {
            sqlstr = "";
        } finally {
            return sqlstr;
        }
    }

    public static final String DATABASE_GET_PER_CATEGORY_COUNT() {
        String sqlstr = "";

        try {
            sqlstr = "SELECT JB." + COLUMN_CatTypeID + ", " +
                    "(SELECT COUNT (*) AS C FROM " + TABLE_NAME + " JB2 WHERE JB2." + COLUMN_CatTypeID + " = JB." + COLUMN_CatTypeID + ") AS CT " +
                    "FROM " + TABLE_NAME + " JB ";
        } catch (Exception e) {
            sqlstr = "";
        } finally {
            return sqlstr;
        }
    }

    public static final String DATABASE_GET_PER_CATEGORY_COUNT2() {
        String sqlstr = "";

        try {
            sqlstr = "SELECT JB." + COLUMN_CatTypeID + ", " +
                    "(SELECT COUNT (*) AS C FROM " + TABLE_NAME + " JB2 WHERE JB2." + COLUMN_CatTypeID + " = JB." + COLUMN_CatTypeID + ") AS CT, " +
                    "(SELECT COUNT(*) AS C FROM " + TABLE_NAME + " T3 WHERE T3." + COLUMN_CatTypeID + " = JB." + COLUMN_CatTypeID + " AND (FLAG2 & 64) == 64) AS ColdCount, " +
                    "(SELECT COUNT(*) AS C FROM " + TABLE_NAME + " T3 WHERE T3." + COLUMN_CatTypeID + " = JB." + COLUMN_CatTypeID + " AND (NetsVisaTi & 4) == 4) AS ReqNetsCount, " +
                    "(SELECT COUNT(*) AS C FROM " + TABLE_NAME + " T3 WHERE T3." + COLUMN_CatTypeID + " = JB." + COLUMN_CatTypeID + " AND (NetsVisaTi & 8) == 8) AS ReqVisaCount, " +
                    "(SELECT COUNT(*) AS C FROM " + TABLE_NAME + " T3 WHERE T3." + COLUMN_CatTypeID + " = JB." + COLUMN_CatTypeID + " AND (NetsVisaTi & 16) == 16) AS ReqTiCount " +
                    "FROM " + TABLE_NAME + " JB ";
        } catch (Exception e) {
            sqlstr = "";
        } finally {
            return sqlstr;
        }
    }

    public static String getPerCategoryCountSQLstr(Integer CatTypeID) {
        String sqlstr = "";

        try {
            sqlstr = sqlStringJobs.DATABASE_GET_PER_CATEGORY_COUNT2() +
                    " WHERE JB." + sqlStringJobs.COLUMN_CatTypeID + " = '" + String.valueOf(CatTypeID) + "'";
            if (CatTypeID == 6) {
                sqlstr += " AND IFNULL(JB." + COLUMN_UNPARK + ", 0) = 0";
            }
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getPerCategoryCountSQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getgetAlldbObjJobsPerCategoryForAddressListSQLstr(Integer CatTypeID) {
        String sqlstr = "";
        try {
            sqlstr = "SELECT  JB." + COLUMN_AddressWithPostalNoUnit + " AS ADDRESS, IFNULL(JB." + COLUMN_JobInfoJsonStr + ",'') AS " + COLUMN_JobInfoJsonStr + ", " +
                    "    JB." + COLUMN_Postal + " AS POSTAL , " +
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) THEN JB." + COLUMN_ReadyDateTime + " " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN JB." + COLUMN_FromDateTime + " " +
                    "    END ) AS FromDateTime , " +
                    //================================================================================================================================================
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) THEN JB." + COLUMN_ReadyDateTime + " " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN JB." + COLUMN_FromDateTime + " " +
                    "    END ) AS FromDateTimeConverted , " +
                    //================================================================================================================================================
                    /*"    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) THEN 'PU FM ' " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5) THEN 'DEL FM ' " +
                    "    END ) AS FromText , " +*/
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) THEN '" + myApp.getAppContext().getString(R.string.alnpufm) + " ' " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN '" + myApp.getAppContext().getString(R.string.alndelfm) + " ' " +
                    "    END ) AS FromText , " +
                    //================================================================================================================================================
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) " +
                    "           THEN ( CASE WHEN JB." + COLUMN_PickByDateTime + " = JB." + COLUMN_ReadyDateTime + " THEN JB." + COLUMN_ToDateTime + " " +
                    "                       ELSE JB." + COLUMN_PickByDateTime + " " +
                    "                 END ) " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN JB." + COLUMN_ToDateTime + " " +
                    "    END ) AS ToDateTime , " +
                    //================================================================================================================================================
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) " +
                    "           THEN ( CASE WHEN JB." + COLUMN_PickByDateTime + " = JB." + COLUMN_ReadyDateTime + " THEN JB." + COLUMN_ToDateTime + " " +
                    "                       ELSE JB." + COLUMN_PickByDateTime + " " +
                    "                END ) " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN JB." + COLUMN_ToDateTime + " " +
                    "      END ) AS ToDateTimeConverted , " +
                    //================================================================================================================================================
                    "    JB." + COLUMN_ExtDateTime + " AS ExtDateTime, " +
                    "    IFNULL(JB." + COLUMN_NetsVisaTI + ",'0') AS " + COLUMN_NetsVisaTI + ", " +
                    //================================================================================================================================================
                    "    JB." + COLUMN_ExtFromDateTime + ", " +
                    /*"    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) " +
                    "           THEN ( CASE WHEN JB." + COLUMN_PickByDateTime + " = JB." + COLUMN_ReadyDateTime + " THEN 'DEL BY ' " +
                    "                       ELSE 'PU BY ' " +
                    "                 END ) " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5) THEN 'DEL BY ' " +
                    "    END ) AS ToText , " +*/
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) " +
                    "           THEN ( CASE WHEN JB." + COLUMN_PickByDateTime + " = JB." + COLUMN_ReadyDateTime + " THEN '" + myApp.getAppContext().getString(R.string.alndelby1) + " ' " +
                    "                       ELSE '" + myApp.getAppContext().getString(R.string.alnpuby1) + " ' " +
                    "                 END ) " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN '" + myApp.getAppContext().getString(R.string.alndelby1) + " ' " +
                    "    END ) AS ToText , " +
                    //================================================================================================================================================
                    "     (SELECT COUNT(*) AS CT FROM " + TABLE_NAME + " JB2 WHERE JB2." + COLUMN_CatTypeID + " = JB." + COLUMN_CatTypeID + " AND JB2." + COLUMN_Postal +
                    " = JB." + COLUMN_Postal + ") AS JOBCOUNT, " +
                    //================================================================================================================================================
                    "     (SELECT COUNT(*) AS CT FROM " + TABLE_NAME + " JB2 WHERE JB2." + COLUMN_CatTypeID + " = JB." + COLUMN_CatTypeID + " AND JB2." + COLUMN_Postal +
                    " = JB." + COLUMN_Postal + " AND JB2.IsPartOne = 1) AS HasP2, " +
                    //================================================================================================================================================
                    "     (SELECT COUNT(*) AS CT FROM " + TABLE_NAME + " JB2 WHERE JB2." + COLUMN_CatTypeID + " = JB." + COLUMN_CatTypeID + " AND JB2." + COLUMN_Postal +
                    " = JB." + COLUMN_Postal + " AND (JB2.FLAG & 1024) = 1024) AS SignaureFlagCount, " +
                    //================================================================================================================================================
                    "     (SELECT COUNT(*) AS CT FROM " + TABLE_NAME + " JB2 WHERE JB2." + COLUMN_CatTypeID + " = JB." + COLUMN_CatTypeID + " AND JB2." + COLUMN_Postal +
                    " = JB." + COLUMN_Postal + " AND (JB2.FLAG & 8) = 8) AS DNFFlagCount, " +
                    //================================================================================================================================================
                    "     (SELECT COUNT(*) AS CT FROM " + TABLE_NAME + " JB2 WHERE JB2." + COLUMN_CatTypeID + " = JB." + COLUMN_CatTypeID + " AND JB2." + COLUMN_Postal +
                    " = JB." + COLUMN_Postal + " AND (JB2.FLAG & 512) = 512 AND (JB2.FLAG2 & 32) <> 32) AS OTPFlagCount, " +
                    //================================================================================================================================================
                    "     (SELECT COUNT(*) AS CT FROM " + TABLE_NAME + " JB2 WHERE JB2." + COLUMN_CatTypeID + " = JB." + COLUMN_CatTypeID + " AND JB2." + COLUMN_Postal +
                    " = JB." + COLUMN_Postal + " AND (JB2.FLAG2 & 64) = 64) AS ColdCount, " +
                    //================================================================================================================================================


                    "     (SELECT COUNT(*) AS CT FROM " + TABLE_NAME + " JB2 WHERE JB2." + COLUMN_CatTypeID + " = JB." + COLUMN_CatTypeID + " AND JB2." + COLUMN_Postal +
                    " = JB." + COLUMN_Postal + " AND ((JB2.FLAG & 16) = 16)) AS isIntendedRecOnlyCount, " +
                    //================================================================================================================================================
                    /*"     (SELECT COUNT(*) AS CT FROM " + TABLE_NAME + " JB2 WHERE JB2." + COLUMN_CatTypeID + " = JB." + COLUMN_CatTypeID + " AND JB2." + COLUMN_Postal +
                    " = JB." + COLUMN_Postal + " AND ((JB2.FLAG & 16) <> 16) AND ((JB2.FLAG & 131072) = 131072) AND ((JB2.FLAG & 1) <> 1) AND ((JB2.FLAG & 2) <> 2) AND ((JB2.FLAG & 4) <> 4)) AS isTotallyExplicitCount, " +*/
                    "     (0) AS isTotallyExplicitCount, " +
                    //================================================================================================================================================
                    "     (SELECT COUNT(*) AS CT FROM " + TABLE_NAME + " JB2 WHERE JB2." + COLUMN_CatTypeID + " = JB." + COLUMN_CatTypeID + " AND JB2." + COLUMN_Postal +
                    " = JB." + COLUMN_Postal + " AND ((JB2.FLAG & 1) = 1)) AS canSlotLBCount, " +
                    //================================================================================================================================================
                    "     (SELECT COUNT(*) AS CT FROM " + TABLE_NAME + " JB2 WHERE JB2." + COLUMN_CatTypeID + " = JB." + COLUMN_CatTypeID + " AND JB2." + COLUMN_Postal +
                    " = JB." + COLUMN_Postal + " AND ((JB2.FLAG & 2) = 2)) AS canSlotDoorCount, " +
                    //================================================================================================================================================
                    "     (SELECT COUNT(*) AS CT FROM " + TABLE_NAME + " JB2 WHERE JB2." + COLUMN_CatTypeID + " = JB." + COLUMN_CatTypeID + " AND JB2." + COLUMN_Postal +
                    " = JB." + COLUMN_Postal + " AND ((JB2.FLAG2 & 2048) = 2048)) AS canDoorstepCount, " +
                    //================================================================================================================================================
                    "     (SELECT COUNT(*) AS CT FROM " + TABLE_NAME + " JB2 WHERE JB2." + COLUMN_CatTypeID + " = JB." + COLUMN_CatTypeID + " AND JB2." + COLUMN_Postal +
                    " = JB." + COLUMN_Postal + " AND ((JB2.FLAG & 4) = 4)) AS canGuardCount, " +
                    //================================================================================================================================================
                    "     (SELECT COUNT(*) AS CT FROM " + TABLE_NAME + " JB2 WHERE JB2." + COLUMN_CatTypeID + " = JB." + COLUMN_CatTypeID + " AND JB2." + COLUMN_Postal +
                    " = JB." + COLUMN_Postal + " AND ((JB2.FLAG & 8388608) = 8388608)) AS isPickAndSendCount, " +
                    //================================================================================================================================================
                    //================================================================================================================================================
                    "     (SELECT COUNT(*) AS CT FROM " + TABLE_NAME + " JB2 WHERE JB2." + COLUMN_CatTypeID + " = JB." + COLUMN_CatTypeID + " AND JB2." + COLUMN_Postal +
                    " = JB." + COLUMN_Postal + " AND ((JB2.FLAG & 67108864) = 67108864)) AS contactlessCount, " +
                    //================================================================================================================================================
                    "(" +
                    "(SELECT COUNT (*) C FROM " + TABLE_NAME + " JB2 WHERE JB2." + COLUMN_CatTypeID + " = 2 AND JB2." + COLUMN_ExpType + " = 0 AND JB2." + COLUMN_ExtCount + " > 1 AND JB2." + COLUMN_Postal + " = JB." + COLUMN_Postal + " ) + " +
                    "(SELECT COUNT (*) C FROM " + TABLE_NAME + " JB2 WHERE JB2." + COLUMN_CatTypeID + " = 2 AND JB2." + COLUMN_ExpType + " <> 0 AND JB2." + COLUMN_ExtCount + " > 0 AND JB2." + COLUMN_Postal + " = JB." + COLUMN_Postal + " ) + " +
                    "(SELECT COUNT (*) C FROM " + TABLE_NAME + " JB2 WHERE JB2." + COLUMN_CatTypeID + " = 2 AND JB2." + COLUMN_Postal + " = JB." + COLUMN_Postal + " AND (JB2." + COLUMN_FLAG + " & 8) = 8 ) " +
                    ") PriorityCount, " +
                    //================================================================================================================================================
                    "     (SELECT COUNT(*) AS CT FROM " + TABLE_NAME_CHATMESSAGE_TBL + " CM WHERE CM." + COLUMN_JobsIDX + " = JB." + COLUMN_JobsIDX + " AND CM." + COLUMN_Message +
                    " LIKE '%[job changes]%') AS JobChangesCount, " +
                    //, (select count(*) as c from chatmessage where jobsidx = jb.jobsidx and message like '%[job changes]%') as JobChangesCount
                    //================================================================================================================================================

                    /*"         (CASE WHEN strftime('%s','now') " +
                    "           - ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) " +
                    "                    THEN ( CASE WHEN JB." + COLUMN_PickByDateTime + " = JB." + COLUMN_ReadyDateTime + " THEN JB." + COLUMN_ToDateTime + " " +
                    "                                ELSE JB." + COLUMN_PickByDateTime + " " +
                    "                           END ) " +
                    "                    WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5) " +
                    "                    THEN JB." + COLUMN_ToDateTime + " " +
                    "                    END ) >= 0 THEN 1 ELSE 0 " +
                    "          END) AS IsLate, " +*/
                    "         (CASE WHEN strftime('%s','now') " +
                    "           - ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) " +
                    "                    THEN ( CASE WHEN JB." + COLUMN_PickByDateTime + " = JB." + COLUMN_ReadyDateTime + " THEN JB." + COLUMN_ToDateTime + " " +
                    "                                ELSE JB." + COLUMN_PickByDateTime + " " +
                    "                           END ) " +
                    "                    WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) " +
                    "                    THEN ( CASE WHEN JB." + COLUMN_ExtDateTime + " > 0 THEN JB." + COLUMN_ExtDateTime + " " +
                    "                                ELSE JB." + COLUMN_ToDateTime + " " +
                    "                           END ) " +
                    "                    END ) >= 0 THEN 1 ELSE 0 " +
                    "          END) AS IsLate, " +
                    //================================================================================================================================================
                    //================================================================================================================================================
                    "     (SELECT COUNT(*) AS CT FROM " + TABLE_NAME + " JB2 WHERE JB2." + COLUMN_CatTypeID + " = JB." + COLUMN_CatTypeID + " AND JB2." + COLUMN_Postal +
                    " = JB." + COLUMN_Postal + " AND JB2." + COLUMN_IsNewlyInserted + " > 0) AS NewlyInsertedCT, " +
                    //================================================================================================================================================
                    "     (SELECT LAT FROM " + TABLE_NAME + " JB2 WHERE JB2." + COLUMN_CatTypeID + " = JB." + COLUMN_CatTypeID + " AND JB2." + COLUMN_Postal +
                    " = JB." + COLUMN_Postal + " LIMIT 1) AS LAT, " +
                    //================================================================================================================================================
                    "     (SELECT LNG FROM " + TABLE_NAME + " JB2 WHERE JB2." + COLUMN_CatTypeID + " = JB." + COLUMN_CatTypeID + " AND JB2." + COLUMN_Postal +
                    " = JB." + COLUMN_Postal + " LIMIT 1) AS LNG, " +
                    //================================================================================================================================================
                    "     (SELECT COUNT(*) AS CT FROM " + TABLE_NAME + " JB2 WHERE JB2." + COLUMN_CatTypeID + " = JB." + COLUMN_CatTypeID + " AND JB2." + COLUMN_Postal +
                    " = JB." + COLUMN_Postal + " AND JB2." + COLUMN_HasDOCOD + " = 1) AS HasDOCODcount, " +
                    //================================================================================================================================================
                    "     (SELECT COUNT(*) AS CT FROM " + TABLE_NAME + " JB2 WHERE JB2." + COLUMN_CatTypeID + " = JB." + COLUMN_CatTypeID + " AND JB2." + COLUMN_Postal +
                    " = JB." + COLUMN_Postal + " AND JB2." + COLUMN_HasTradeIn + " = 1) AS HasTradeIncount, " +
                    //================================================================================================================================================
                    "     (SELECT COUNT(*) AS CT FROM " + TABLE_NAME + " JB2 WHERE JB2." + COLUMN_CatTypeID + " = JB." + COLUMN_CatTypeID + " AND JB2." + COLUMN_Postal +
                    " = JB." + COLUMN_Postal + " AND IFNULL(JB2." + COLUMN_HASLUNCHTIME + ", 0) = 1) AS HASLUNCHTIMEcount, " +
                    //================================================================================================================================================
                    "     (SELECT COUNT(*) AS CT FROM " + TABLE_NAME + " JB2 WHERE JB2." + COLUMN_CatTypeID + " = JB." + COLUMN_CatTypeID + " AND JB2." + COLUMN_Postal +
                    " = JB." + COLUMN_Postal + " AND JB2." + COLUMN_ExtFromDateTime + " > 0) AS HasExt " +
                    //================================================================================================================================================
                    "FROM    " + TABLE_NAME + " JB " +
                    "WHERE   JB." + COLUMN_CatTypeID + " = '" + String.valueOf(CatTypeID) + "' " +
                    "ORDER BY ( strftime('%s','now') " +
                    "           - ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) " +
                    "                    THEN ( CASE WHEN JB." + COLUMN_PickByDateTime + " = JB." + COLUMN_ReadyDateTime + " THEN JB." + COLUMN_ToDateTime + " " +
                    "                                ELSE JB." + COLUMN_PickByDateTime + " " +
                    "                           END ) " +
                    "                    WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) " +
                    "                    THEN JB." + COLUMN_ToDateTime + " " +
                    "               END ) ) DESC";

        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getgetAlldbObjJobsPerCategoryForAddressListSQLstring. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getgetAlldbObjJobsPerCategoryForJobsListByJobsIDXsSQLstr(Integer CatTypeID, String Postal, String JobsIDXs) {
        String sqlstr = "";

        try {

            sqlstr = "SELECT  JB." + COLUMN_ID + ", JB." + COLUMN_JobsIDX + ", JB." + COLUMN_CatTypeID + ", JB." + COLUMN_Postal + ", JB." + COLUMN_AddressWithPostalNoUnit + ", JB." + COLUMN_AddressWithoutPostal + ", JB." +
                    COLUMN_LAT + ", JB." + COLUMN_LNG + ", JB." + COLUMN_ExpType + ", JB." + COLUMN_ExpTypeDesc + ", JB." + COLUMN_IsPartOne + ", JB." + COLUMN_PartDesc + ", JB." + COLUMN_IsDunning
                    + ", JB." + COLUMN_HasDOCOD + ", JB." + COLUMN_HasTradeIn + ", JB." + COLUMN_JobNo + ", JB." +
                    COLUMN_CNNo + ", JB." + COLUMN_CompanyName + ", JB." + COLUMN_ContactName + ", JB." + COLUMN_DO_INV_No + ", JB." + COLUMN_DO_INV_IDX + ", JB." + COLUMN_TAGS + ", JB." + COLUMN_FromDateTime + ", JB." + COLUMN_PickByDateTime + ", JB." +
                    COLUMN_ReadyDateTime + ", JB." + COLUMN_ToDateTime + ", JB." + COLUMN_ExtDateTime + ", JB." + COLUMN_ExtFromDateTime + ", JB." + COLUMN_FLAG + ", JB." + COLUMN_PCS + ", JB." + COLUMN_StatusCodeIDX + ", JB." + COLUMN_IsNewlyInserted +
                    ", IFNULL(JB." + COLUMN_FM_AddressIDX + ",0) AS " + COLUMN_FM_AddressIDX + ", IFNULL(JB." + COLUMN_FM_ContactIDX + ",0) AS " + COLUMN_FM_ContactIDX
                    + ", IFNULL(JB." + COLUMN_TO_AddressIDX + ",0) AS " + COLUMN_TO_AddressIDX + " , IFNULL(JB." + COLUMN_TO_ContactIDX + ",0) AS " + COLUMN_TO_ContactIDX +
                    ", IFNULL(JB." + COLUMN_JobInfoJsonStr + ",'') AS " + COLUMN_JobInfoJsonStr + ", " +
                    " IFNULL(JB." + COLUMN_CHECKLISTS + ",'') AS " + COLUMN_CHECKLISTS + ", " +
                    " IFNULL(JB." + COLUMN_NetsVisaTI + ",'0') AS " + COLUMN_NetsVisaTI + ", " +
                    //================================================================================================================================================
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) THEN JB." + COLUMN_ReadyDateTime + " " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN JB." + COLUMN_FromDateTime + " " +
                    "    END ) AS FromDateTimeConverted , " +
                    //================================================================================================================================================
                    /*"    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) THEN 'PU FM ' " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5) THEN 'DEL FM ' " +
                    "    END ) AS FromText , " +*/
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) THEN '" + myApp.getAppContext().getString(R.string.alnpufm) + " ' " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN '" + myApp.getAppContext().getString(R.string.alndelfm) + " ' " +
                    "    END ) AS FromText , " +
                    //================================================================================================================================================
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) " +
                    "           THEN ( CASE WHEN JB." + COLUMN_PickByDateTime + " = JB." + COLUMN_ReadyDateTime + " THEN JB." + COLUMN_ToDateTime + " " +
                    "                       ELSE JB." + COLUMN_PickByDateTime + " " +
                    "                END ) " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN JB." + COLUMN_ToDateTime + " " +
                    "      END ) AS ToDateTimeConverted , " +
                    //================================================================================================================================================
                    /*"    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) " +
                    "           THEN ( CASE WHEN JB." + COLUMN_PickByDateTime + " = JB." + COLUMN_ReadyDateTime + " THEN 'DEL BY ' " +
                    "                       ELSE 'PU BY ' " +
                    "                 END ) " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5) THEN 'DEL BY ' " +
                    "    END ) AS ToText " +*/
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) " +
                    "           THEN ( CASE WHEN JB." + COLUMN_PickByDateTime + " = JB." + COLUMN_ReadyDateTime + " THEN '" + myApp.getAppContext().getString(R.string.alndelby1) + " ' " +
                    "                       ELSE '" + myApp.getAppContext().getString(R.string.alnpuby1) + " ' " +
                    "                 END ) " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN '" + myApp.getAppContext().getString(R.string.alndelby1) + " ' " +
                    "    END ) AS ToText, " +
                    //================================================================================================================================================
                    "     ( CASE WHEN ((JB.FLAG & 8388608) = 8388608) THEN 1 ELSE 0 END) AS isPickAndSendCount, " +
                    //================================================================================================================================================
                    "     (SELECT COUNT(*) AS CT FROM " + TABLE_NAME_CHATMESSAGE_TBL + " CM WHERE CM." + COLUMN_JobsIDX + " = JB." + COLUMN_JobsIDX + " AND CM." + COLUMN_Message +
                    "       LIKE '%[job changes]%') AS JobChangesCount, " +
                    //================================================================================================================================================
                    "     IFNULL(" + COLUMN_HASLUNCHTIME + ", 0) AS " + COLUMN_HASLUNCHTIME + " " +
                    //================================================================================================================================================
                    "FROM " + TABLE_NAME + " JB " +
                    "WHERE   JB." + COLUMN_CatTypeID + " = '" + String.valueOf(CatTypeID) + "' AND JB." + COLUMN_Postal + " = '" + Postal + "' " +
                    "AND JB." + COLUMN_JobsIDX + " IN (" + JobsIDXs + ") " +
                    "ORDER BY ( strftime('%s','now') " +
                    "           - ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) " +
                    "                    THEN ( CASE WHEN JB." + COLUMN_PickByDateTime + " = JB." + COLUMN_ReadyDateTime + " THEN JB." + COLUMN_ToDateTime + " " +
                    "                                ELSE JB." + COLUMN_PickByDateTime + " " +
                    "                           END ) " +
                    "                    WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) " +
                    "                    THEN JB." + COLUMN_ToDateTime + " " +
                    "               END ) ) DESC";

        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getgetAlldbObjJobsPerCategoryForJobsListByJobsIDXsSQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getAllMedsColByContactIDXdbObjJobsListSQLstr() {
        String sqlstr = "";

        try {

            sqlstr = "SELECT  JB." + COLUMN_ID + ", JB." + COLUMN_JobsIDX + ", JB." + COLUMN_CatTypeID + ", JB." + COLUMN_Postal + ", JB." + COLUMN_AddressWithPostalNoUnit + ", JB." + COLUMN_AddressWithoutPostal + ", JB." +
                    COLUMN_LAT + ", JB." + COLUMN_LNG + ", JB." + COLUMN_ExpType + ", JB." + COLUMN_ExpTypeDesc + ", JB." + COLUMN_IsPartOne + ", JB." + COLUMN_PartDesc + ", JB." + COLUMN_IsDunning
                    + ", JB." + COLUMN_HasDOCOD + ", JB." + COLUMN_HasTradeIn + ", JB." + COLUMN_JobNo + ", JB." +
                    COLUMN_CNNo + ", JB." + COLUMN_CompanyName + ", JB." + COLUMN_ContactName + ", JB." + COLUMN_DO_INV_No + ", JB." + COLUMN_DO_INV_IDX + ", JB." + COLUMN_TAGS + ", JB." + COLUMN_FromDateTime + ", JB." + COLUMN_PickByDateTime + ", JB." +
                    COLUMN_ReadyDateTime + ", JB." + COLUMN_ToDateTime + ", JB." + COLUMN_ExtDateTime + ", JB." + COLUMN_ExtFromDateTime + ", JB." + COLUMN_FLAG + ", JB." + COLUMN_PCS + ", JB." + COLUMN_StatusCodeIDX + ", JB." + COLUMN_IsNewlyInserted +
                    ", IFNULL(JB." + COLUMN_FM_AddressIDX + ",0) AS " + COLUMN_FM_AddressIDX + ", IFNULL(JB." + COLUMN_FM_ContactIDX + ",0) AS " + COLUMN_FM_ContactIDX
                    + ", IFNULL(JB." + COLUMN_TO_AddressIDX + ",0) AS " + COLUMN_TO_AddressIDX + " , IFNULL(JB." + COLUMN_TO_ContactIDX + ",0) AS " + COLUMN_TO_ContactIDX +
                    ", IFNULL(JB." + COLUMN_JobInfoJsonStr + ",'') AS " + COLUMN_JobInfoJsonStr + ", " +
                    " IFNULL(JB." + COLUMN_CHECKLISTS + ",'') AS " + COLUMN_CHECKLISTS + ", " +
                    " IFNULL(JB." + COLUMN_NetsVisaTI + ",'0') AS " + COLUMN_NetsVisaTI + ", " +
                    //================================================================================================================================================
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) THEN JB." + COLUMN_ReadyDateTime + " " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN JB." + COLUMN_FromDateTime + " " +
                    "    END ) AS FromDateTimeConverted , " +
                    //================================================================================================================================================
                    /*"    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) THEN 'PU FM ' " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5) THEN 'DEL FM ' " +
                    "    END ) AS FromText , " +*/
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) THEN '" + myApp.getAppContext().getString(R.string.alnpufm) + " ' " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN '" + myApp.getAppContext().getString(R.string.alndelfm) + " ' " +
                    "    END ) AS FromText , " +
                    //================================================================================================================================================
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) " +
                    "           THEN ( CASE WHEN JB." + COLUMN_PickByDateTime + " = JB." + COLUMN_ReadyDateTime + " THEN JB." + COLUMN_ToDateTime + " " +
                    "                       ELSE JB." + COLUMN_PickByDateTime + " " +
                    "                END ) " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN JB." + COLUMN_ToDateTime + " " +
                    "      END ) AS ToDateTimeConverted , " +
                    //================================================================================================================================================
                    /*"    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) " +
                    "           THEN ( CASE WHEN JB." + COLUMN_PickByDateTime + " = JB." + COLUMN_ReadyDateTime + " THEN 'DEL BY ' " +
                    "                       ELSE 'PU BY ' " +
                    "                 END ) " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5) THEN 'DEL BY ' " +
                    "    END ) AS ToText " +*/
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) " +
                    "           THEN ( CASE WHEN JB." + COLUMN_PickByDateTime + " = JB." + COLUMN_ReadyDateTime + " THEN '" + myApp.getAppContext().getString(R.string.alndelby1) + " ' " +
                    "                       ELSE '" + myApp.getAppContext().getString(R.string.alnpuby1) + " ' " +
                    "                 END ) " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN '" + myApp.getAppContext().getString(R.string.alndelby1) + " ' " +
                    "    END ) AS ToText, " +
                    //================================================================================================================================================
                    "     ( CASE WHEN ((JB.FLAG & 8388608) = 8388608) THEN 1 ELSE 0 END) AS isPickAndSendCount, " +
                    //================================================================================================================================================
                    "     (SELECT COUNT(*) AS CT FROM " + TABLE_NAME_CHATMESSAGE_TBL + " CM WHERE CM." + COLUMN_JobsIDX + " = JB." + COLUMN_JobsIDX + " AND CM." + COLUMN_Message +
                    "       LIKE '%[job changes]%') AS JobChangesCount, " +
                    //================================================================================================================================================
                    "     IFNULL(" + COLUMN_HASLUNCHTIME + ", 0) AS " + COLUMN_HASLUNCHTIME + ", " +
                    //================================================================================================================================================
                    "     IFNULL(JB." + COLUMN_DelAddFull + ", 0) AS " + COLUMN_DelAddFull + " " +
                    //================================================================================================================================================
                    "FROM " + TABLE_NAME + " JB " +
                    "WHERE   JB." + COLUMN_CatTypeID + " = 0 AND JB." + COLUMN_ContactIDX + " = 3650876 " +
                    "ORDER BY ( strftime('%s','now') " +
                    "           - ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) " +
                    "                    THEN ( CASE WHEN JB." + COLUMN_PickByDateTime + " = JB." + COLUMN_ReadyDateTime + " THEN JB." + COLUMN_ToDateTime + " " +
                    "                                ELSE JB." + COLUMN_PickByDateTime + " " +
                    "                           END ) " +
                    "                    WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) " +
                    "                    THEN JB." + COLUMN_ToDateTime + " " +
                    "               END ) ) DESC";

        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getAllMedsColByContactIDXdbObjJobsListSQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getAllMedsColSqlStr() {
        String sql = "";
        try {
            /*sql = "SELECT P.JobsIDX, P.PCS, P.PCS_SCANNED, (P.PCS - P.PCS_SCANNED) FPCS, JB.Postal, JB.AddressWithPostalNoUnit, JB.AddressWithoutPostal, " +
                    "JB.LAT, JB.LNG, JB.ExpType, JB.ExpTypeDesc, JB.JobNo, JB.CNNo, JB.FLAG, JB.FLAG2, IFNULL(JB.JobInfoJsonStr,'') AS JobInfoJsonStr, IFNULL(JB.CHECKLISTS,'') AS CHECKLISTS, " +
                    "(CASE WHEN JB.CatTypeID IN (0,1,4) THEN JB.ReadyDateTime  WHEN JB.CatTypeID IN (2,3,5,6,7) THEN JB.FromDateTime END) AS FromDateTimeConverted, " +
                    "(CASE WHEN JB.CatTypeID IN (0,1,4) THEN 'PU FM ' WHEN JB.CatTypeID IN (2,3,5,6,7) THEN 'DEL FM ' END) AS FromText, " +
                    "(CASE WHEN JB.CatTypeID IN (0,1,4) THEN ( CASE WHEN JB.PickByDateTime = JB.ReadyDateTime THEN JB.ToDateTime  ELSE JB.PickByDateTime END)  WHEN JB.CatTypeID IN (2,3,5,6,7) THEN JB.ToDateTime END) AS ToDateTimeConverted, " +
                    "(CASE WHEN JB.CatTypeID IN (0,1,4) THEN ( CASE WHEN JB.PickByDateTime = JB.ReadyDateTime THEN 'DEL BY ' ELSE 'PU BY ' END) WHEN JB.CatTypeID IN (2,3,5,6,7) THEN 'DEL BY ' END) AS ToText, " +
                    "IFNULL(JB.DelAddFull, 0) AS DelAddFull " +
                    "FROM PIPPCS P " +
                    "LEFT JOIN JOBS JB ON JB.JobsIDX = P.JobsIDX " +
                    "WHERE JB.CatTypeID = 0  " +
                    "AND (JB.ContactIDX = 3650876 OR JB.ClientIDX = 28657)  " +
                    "ORDER BY P.PCS";*/

            sql = "SELECT P.JobsIDX, P.PCS, P.PCS_SCANNED, (P.PCS - P.PCS_SCANNED) FPCS, JB.Postal, JB.AddressWithPostalNoUnit, JB.AddressWithoutPostal, " +
                    "JB.LAT, JB.LNG, JB.ExpType, JB.ExpTypeDesc, JB.JobNo, JB.CNNo, JB.FLAG, JB.FLAG2, IFNULL(JB.JobInfoJsonStr,'') AS JobInfoJsonStr, IFNULL(JB.CHECKLISTS,'') AS CHECKLISTS, " +
                    "(CASE WHEN JB.CatTypeID IN (0,1,4) THEN JB.ReadyDateTime  WHEN JB.CatTypeID IN (2,3,5,6,7) THEN JB.FromDateTime END) AS FromDateTimeConverted, " +
                    "(CASE WHEN JB.CatTypeID IN (0,1,4) THEN 'PU FM ' WHEN JB.CatTypeID IN (2,3,5,6,7) THEN 'DEL FM ' END) AS FromText, " +
                    "(CASE WHEN JB.CatTypeID IN (0,1,4) THEN ( CASE WHEN JB.PickByDateTime = JB.ReadyDateTime THEN JB.ToDateTime  ELSE JB.PickByDateTime END)  WHEN JB.CatTypeID IN (2,3,5,6,7) THEN JB.ToDateTime END) AS ToDateTimeConverted, " +
                    "(CASE WHEN JB.CatTypeID IN (0,1,4) THEN ( CASE WHEN JB.PickByDateTime = JB.ReadyDateTime THEN 'DEL BY ' ELSE 'PU BY ' END) WHEN JB.CatTypeID IN (2,3,5,6,7) THEN 'DEL BY ' END) AS ToText, " +
                    "IFNULL(JB.DelAddFull, 0) AS DelAddFull, " +
                    "(SELECT COUNT(*) C FROM JOBS " +
                    "WHERE " +
                    "(CatTypeID = 1 AND Postal = JB.Postal AND " +
                    "(ContactIDX IN (3650876) OR " +
                    "ClientIDX IN (28657) OR " +
                    "INDUSTRY = 'healthcare: facilities' " +
                    ")) ) PUC " +
                    "FROM PIPPCS P " +
                    "LEFT JOIN JOBS JB ON JB.JobsIDX = P.JobsIDX " +
                    "WHERE JB.CatTypeID = 0 " +
                    "AND (JB.ContactIDX = 3650876 OR JB.ClientIDX = 28657) " +
                    "ORDER BY P.PCS";
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at sqlStringJobs getAllMedsColSqlStr. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return sql;
        }
    }

    public static String getJobsIDXNotInPIPPCSSqlStr(String ContactIDX, String ClientIDX) {
        String sql = "";
        try {
            /*sql = "SELECT JobsIDX FROM JOBS  " +
                    "WHERE " +
                    "(CatTypeID = 0 AND " +
                    "(ContactIDX IN (3650876) OR " +
                    "ClientIDX IN (28657) OR " +
                    "INDUSTRY = 'healthcare: facilities' " +
                    ")) AND JobsIDX NOT IN ( " +
                    "SELECT JobsIDX FROM PIPPCS " +
                    ")";*/

            sql = "SELECT JobsIDX, PCS FROM JOBS  " +
                    "WHERE " +
                    "(CatTypeID = 0 AND " +
                    "(ContactIDX IN (" + ContactIDX + ") OR " +
                    "ClientIDX IN (" + ClientIDX + ") OR " +
                    "INDUSTRY = 'healthcare: facilities' " +
                    ")) AND JobsIDX NOT IN ( " +
                    "SELECT JobsIDX FROM PIPPCS " +
                    ")";
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at sqlStringJobs getJobsIDXNotInPIPPCSSqlStr. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return sql;
        }
    }

    public static String getPIPPCS_to_UpdateSqlStr() {
        String sql = "";
        try {
            sql = "SELECT JobsIDX " +
                    "FROM PIPPCS " +
                    "WHERE PCS = PCS_SCANNED";
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at sqlStringJobs getPIPPCS_to_UpdateSqlStr. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return sql;
        }
    }

    public static String getPIPPCS_selectedSqlStr() {
        String sql = "";
        try {
            sql = "SELECT JobsIDX " +
                    "FROM PIPPCS " +
                    "WHERE PCS_SCANNED > 0 AND PCS_SCANNEDNOW > 0";
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at sqlStringJobs getPIPPCS_selectedSqlStr. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return sql;
        }
    }

    public static String getPCS_SCANNEDNOWSqlStr() {
        String sql = "";
        try {
            sql = "SELECT SUM(PCS_SCANNEDNOW) C " +
                    "FROM PIPPCS " +
                    "WHERE PCS_SCANNED > 0 AND PCS_SCANNEDNOW > 0";
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at sqlStringJobs getPIPPCS_selectedSqlStr. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return sql;
        }
    }

    public static String getMedJobsCountSqlStr() {
        String sql = "";
        try {
            sql = "SELECT COUNT(*) C " +
                    "FROM PIPPCS";
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at sqlStringJobs getMedJobsCountSqlStr. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return sql;
        }
    }

    public static String getgetAlldbObjJobsPerCategoryForJobsListSQLstr(Integer CatTypeID, String Postal) {
        String sqlstr = "";

        try {

            sqlstr = "SELECT  JB." + COLUMN_ID + ", JB." + COLUMN_JobsIDX + ", JB." + COLUMN_CatTypeID + ", JB." + COLUMN_Postal + ", JB." + COLUMN_AddressWithPostalNoUnit + ", JB." + COLUMN_AddressWithoutPostal + ", JB." +
                    COLUMN_LAT + ", JB." + COLUMN_LNG + ", JB." + COLUMN_ExpType + ", JB." + COLUMN_ExpTypeDesc + ", JB." + COLUMN_IsPartOne + ", JB." + COLUMN_PartDesc + ", JB." + COLUMN_IsDunning
                    + ", JB." + COLUMN_HasDOCOD + ", JB." + COLUMN_HasTradeIn + ", JB." + COLUMN_JobNo + ", JB." +
                    COLUMN_CNNo + ", JB." + COLUMN_CompanyName + ", JB." + COLUMN_ContactName + ", JB." + COLUMN_DO_INV_No + ", JB." + COLUMN_DO_INV_IDX + ", JB." + COLUMN_TAGS + ", JB." + COLUMN_FromDateTime + ", JB." + COLUMN_PickByDateTime + ", JB." +
                    COLUMN_ReadyDateTime + ", JB." + COLUMN_ToDateTime + ", JB." + COLUMN_ExtDateTime + ", JB." + COLUMN_ExtFromDateTime + ", JB." + COLUMN_FLAG + ", JB." + COLUMN_PCS + ", JB." + COLUMN_StatusCodeIDX + ", JB." + COLUMN_IsNewlyInserted +
                    ", IFNULL(JB." + COLUMN_FM_AddressIDX + ",0) AS " + COLUMN_FM_AddressIDX + ", IFNULL(JB." + COLUMN_FM_ContactIDX + ",0) AS " + COLUMN_FM_ContactIDX
                    + ", IFNULL(JB." + COLUMN_TO_AddressIDX + ",0) AS " + COLUMN_TO_AddressIDX + " , IFNULL(JB." + COLUMN_TO_ContactIDX + ",0) AS " + COLUMN_TO_ContactIDX +
                    ", IFNULL(JB." + COLUMN_JobInfoJsonStr + ",'') AS " + COLUMN_JobInfoJsonStr + ", " +
                    " IFNULL(JB." + COLUMN_CHECKLISTS + ",'') AS " + COLUMN_CHECKLISTS + ", " +
                    " IFNULL(JB." + COLUMN_NetsVisaTI + ",'0') AS " + COLUMN_NetsVisaTI + ", " +
                    //================================================================================================================================================
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) THEN JB." + COLUMN_ReadyDateTime + " " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN JB." + COLUMN_FromDateTime + " " +
                    "    END ) AS FromDateTimeConverted , " +
                    //================================================================================================================================================
                    /*"    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) THEN 'PU FM ' " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5) THEN 'DEL FM ' " +
                    "    END ) AS FromText , " +*/
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) THEN '" + myApp.getAppContext().getString(R.string.alnpufm) + " ' " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN '" + myApp.getAppContext().getString(R.string.alndelfm) + " ' " +
                    "    END ) AS FromText , " +
                    //================================================================================================================================================
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) " +
                    "           THEN ( CASE WHEN JB." + COLUMN_PickByDateTime + " = JB." + COLUMN_ReadyDateTime + " THEN JB." + COLUMN_ToDateTime + " " +
                    "                       ELSE JB." + COLUMN_PickByDateTime + " " +
                    "                END ) " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN JB." + COLUMN_ToDateTime + " " +
                    "      END ) AS ToDateTimeConverted , " +
                    //================================================================================================================================================
                    /*"    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) " +
                    "           THEN ( CASE WHEN JB." + COLUMN_PickByDateTime + " = JB." + COLUMN_ReadyDateTime + " THEN 'DEL BY ' " +
                    "                       ELSE 'PU BY ' " +
                    "                 END ) " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5) THEN 'DEL BY ' " +
                    "    END ) AS ToText " +*/
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) " +
                    "           THEN ( CASE WHEN JB." + COLUMN_PickByDateTime + " = JB." + COLUMN_ReadyDateTime + " THEN '" + myApp.getAppContext().getString(R.string.alndelby1) + " ' " +
                    "                       ELSE '" + myApp.getAppContext().getString(R.string.alnpuby1) + " ' " +
                    "                 END ) " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN '" + myApp.getAppContext().getString(R.string.alndelby1) + " ' " +
                    "    END ) AS ToText, " +
                    //================================================================================================================================================
                    "     ( CASE WHEN ((JB.FLAG & 8388608) = 8388608) THEN 1 ELSE 0 END) AS isPickAndSendCount, " +
                    //================================================================================================================================================
                    "     (SELECT COUNT(*) AS CT FROM " + TABLE_NAME_CHATMESSAGE_TBL + " CM WHERE CM." + COLUMN_JobsIDX + " = JB." + COLUMN_JobsIDX + " AND CM." + COLUMN_Message +
                    "       LIKE '%[job changes]%') AS JobChangesCount, " +
                    //================================================================================================================================================
                    "     IFNULL(" + COLUMN_HASLUNCHTIME + ", 0) AS " + COLUMN_HASLUNCHTIME + " " +
                    //================================================================================================================================================
                    "FROM " + TABLE_NAME + " JB " +
                    "WHERE   JB." + COLUMN_CatTypeID + " = '" + String.valueOf(CatTypeID) + "' AND JB." + COLUMN_Postal + " = '" + Postal + "' " +
                    "ORDER BY ( strftime('%s','now') " +
                    "           - ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) " +
                    "                    THEN ( CASE WHEN JB." + COLUMN_PickByDateTime + " = JB." + COLUMN_ReadyDateTime + " THEN JB." + COLUMN_ToDateTime + " " +
                    "                                ELSE JB." + COLUMN_PickByDateTime + " " +
                    "                           END ) " +
                    "                    WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) " +
                    "                    THEN JB." + COLUMN_ToDateTime + " " +
                    "               END ) ) DESC";

        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getgetAlldbObjJobsPerCategoryForJobsListSQLstring. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getAlldbObjJobsForDashboardSubByJobsIDXsSQLstr(String Postal, String JobsIDXs) {
        String sqlstr = "";
        try {
            sqlstr = "SELECT  JB." + COLUMN_ID + ", JB." + COLUMN_JobsIDX + ", JB." + COLUMN_CatTypeID + ", JB." + COLUMN_Postal + ", JB." + COLUMN_AddressWithPostalNoUnit + ", JB." + COLUMN_AddressWithoutPostal + ", JB." +
                    COLUMN_LAT + ", JB." + COLUMN_LNG + ", JB." + COLUMN_ExpType + ", JB." + COLUMN_ExpTypeDesc + ", JB." + COLUMN_IsPartOne + ", JB." + COLUMN_PartDesc + ", JB." + COLUMN_IsDunning +
                    ", JB." + COLUMN_HasDOCOD + ", JB." + COLUMN_HasTradeIn + ", JB." + COLUMN_JobNo + ", JB." +
                    COLUMN_CNNo + ", JB." + COLUMN_CompanyName + ", JB." + COLUMN_ContactName + ", JB." + COLUMN_DO_INV_No + ", JB." + COLUMN_DO_INV_IDX + ", JB." + COLUMN_TAGS + ", JB." + COLUMN_FromDateTime + ", JB." + COLUMN_PickByDateTime + ", JB." +
                    COLUMN_ReadyDateTime + ", JB." + COLUMN_ToDateTime + ", JB." + COLUMN_ExtDateTime + ", JB." + COLUMN_ExtFromDateTime + ", JB." + COLUMN_FLAG + ", JB." + COLUMN_PCS + ", JB." + COLUMN_StatusCodeIDX + ", JB." + COLUMN_IsNewlyInserted +
                    ", IFNULL(JB." + COLUMN_FM_AddressIDX + ",0) AS " + COLUMN_FM_AddressIDX + ", IFNULL(JB." + COLUMN_FM_ContactIDX + ",0) AS " + COLUMN_FM_ContactIDX
                    + ", IFNULL(JB." + COLUMN_TO_AddressIDX + ",0) AS " + COLUMN_TO_AddressIDX + " , IFNULL(JB." + COLUMN_TO_ContactIDX + ",0) AS " + COLUMN_TO_ContactIDX +
                    ", IFNULL(JB." + COLUMN_JobInfoJsonStr + ",'') AS " + COLUMN_JobInfoJsonStr + ", " +
                    " IFNULL(JB." + COLUMN_CHECKLISTS + ",'') AS " + COLUMN_CHECKLISTS + ", " +
                    " IFNULL(JB." + COLUMN_NetsVisaTI + ",'0') AS " + COLUMN_NetsVisaTI + ", " +
                    //================================================================================================================================================
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) THEN JB." + COLUMN_ReadyDateTime + " " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN JB." + COLUMN_FromDateTime + " " +
                    "    END ) AS FromDateTimeConverted , " +
                    //================================================================================================================================================
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) THEN 'PU FM ' " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN 'DEL FM ' " +
                    "    END ) AS FromText , " +
                    //================================================================================================================================================
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) " +
                    "           THEN ( CASE WHEN JB." + COLUMN_PickByDateTime + " = JB." + COLUMN_ReadyDateTime + " THEN JB." + COLUMN_ToDateTime + " " +
                    "                       ELSE JB." + COLUMN_PickByDateTime + " " +
                    "                END ) " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN JB." + COLUMN_ToDateTime + " " +
                    "      END ) AS ToDateTimeConverted , " +
                    //================================================================================================================================================
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) " +
                    "           THEN ( CASE WHEN JB." + COLUMN_PickByDateTime + " = JB." + COLUMN_ReadyDateTime + " THEN 'DEL BY ' " +
                    "                       ELSE 'PU BY ' " +
                    "                 END ) " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN 'DEL BY ' " +
                    "    END ) AS ToText, " +
                    //================================================================================================================================================
                    "    0 isPickAndSendCount, 0 JobChangesCount, 0 HASLUNCHTIM " +
                    "FROM " + TABLE_NAME + " JB " +
                    "WHERE   JB." + COLUMN_CatTypeID + " IN (0, 2) AND JB." + COLUMN_Postal + " = '" + Postal + "' " +
                    "AND JB." + COLUMN_JobsIDX + " IN (" + JobsIDXs + ") " +
                    "ORDER BY ( strftime('%s','now') " +
                    "           - ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) " +
                    "                    THEN ( CASE WHEN JB." + COLUMN_PickByDateTime + " = JB." + COLUMN_ReadyDateTime + " THEN JB." + COLUMN_ToDateTime + " " +
                    "                                ELSE JB." + COLUMN_PickByDateTime + " " +
                    "                           END ) " +
                    "                    WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) " +
                    "                    THEN JB." + COLUMN_ToDateTime + " " +
                    "               END ) ) DESC";

        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getAlldbObjJobsForDashboardSubByJobsIDXsSQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getAlldbObjJobsForDashboardSubSQLstr(String Postal) {
        String sqlstr = "";

        try {

            sqlstr = "SELECT  JB." + COLUMN_ID + ", JB." + COLUMN_JobsIDX + ", JB." + COLUMN_CatTypeID + ", JB." + COLUMN_Postal + ", JB." + COLUMN_AddressWithPostalNoUnit + ", JB." + COLUMN_AddressWithoutPostal + ", JB." +
                    COLUMN_LAT + ", JB." + COLUMN_LNG + ", JB." + COLUMN_ExpType + ", JB." + COLUMN_ExpTypeDesc + ", JB." + COLUMN_IsPartOne + ", JB." + COLUMN_PartDesc + ", JB." + COLUMN_IsDunning +
                    ", JB." + COLUMN_HasDOCOD + ", JB." + COLUMN_HasTradeIn + ", JB." + COLUMN_JobNo + ", JB." +
                    COLUMN_CNNo + ", JB." + COLUMN_CompanyName + ", JB." + COLUMN_ContactName + ", JB." + COLUMN_DO_INV_No + ", JB." + COLUMN_DO_INV_IDX + ", JB." + COLUMN_TAGS + ", JB." + COLUMN_FromDateTime + ", JB." + COLUMN_PickByDateTime + ", JB." +
                    COLUMN_ReadyDateTime + ", JB." + COLUMN_ToDateTime + ", JB." + COLUMN_ExtDateTime + ", JB." + COLUMN_ExtFromDateTime + ", JB." + COLUMN_FLAG + ", JB." + COLUMN_PCS + ", JB." + COLUMN_StatusCodeIDX + ", JB." + COLUMN_IsNewlyInserted +
                    ", IFNULL(JB." + COLUMN_FM_AddressIDX + ",0) AS " + COLUMN_FM_AddressIDX + ", IFNULL(JB." + COLUMN_FM_ContactIDX + ",0) AS " + COLUMN_FM_ContactIDX
                    + ", IFNULL(JB." + COLUMN_TO_AddressIDX + ",0) AS " + COLUMN_TO_AddressIDX + " , IFNULL(JB." + COLUMN_TO_ContactIDX + ",0) AS " + COLUMN_TO_ContactIDX +
                    ", IFNULL(JB." + COLUMN_JobInfoJsonStr + ",'') AS " + COLUMN_JobInfoJsonStr + ", " +
                    " IFNULL(JB." + COLUMN_CHECKLISTS + ",'') AS " + COLUMN_CHECKLISTS + ", " +
                    " IFNULL(JB." + COLUMN_NetsVisaTI + ",'0') AS " + COLUMN_NetsVisaTI + ", " +
                    //================================================================================================================================================
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) THEN JB." + COLUMN_ReadyDateTime + " " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN JB." + COLUMN_FromDateTime + " " +
                    "    END ) AS FromDateTimeConverted , " +
                    //================================================================================================================================================
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) THEN 'PU FM ' " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN 'DEL FM ' " +
                    "    END ) AS FromText , " +
                    //================================================================================================================================================
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) " +
                    "           THEN ( CASE WHEN JB." + COLUMN_PickByDateTime + " = JB." + COLUMN_ReadyDateTime + " THEN JB." + COLUMN_ToDateTime + " " +
                    "                       ELSE JB." + COLUMN_PickByDateTime + " " +
                    "                END ) " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN JB." + COLUMN_ToDateTime + " " +
                    "      END ) AS ToDateTimeConverted , " +
                    //================================================================================================================================================
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) " +
                    "           THEN ( CASE WHEN JB." + COLUMN_PickByDateTime + " = JB." + COLUMN_ReadyDateTime + " THEN 'DEL BY ' " +
                    "                       ELSE 'PU BY ' " +
                    "                 END ) " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN 'DEL BY ' " +
                    "    END ) AS ToText, " +
                    //================================================================================================================================================
                    "    0 isPickAndSendCount, 0 JobChangesCount, 0 HASLUNCHTIM " +
                    "FROM " + TABLE_NAME + " JB " +
                    "WHERE   JB." + COLUMN_CatTypeID + " IN (0, 2) AND JB." + COLUMN_Postal + " = '" + Postal + "' " +
                    "ORDER BY ( strftime('%s','now') " +
                    "           - ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) " +
                    "                    THEN ( CASE WHEN JB." + COLUMN_PickByDateTime + " = JB." + COLUMN_ReadyDateTime + " THEN JB." + COLUMN_ToDateTime + " " +
                    "                                ELSE JB." + COLUMN_PickByDateTime + " " +
                    "                           END ) " +
                    "                    WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) " +
                    "                    THEN JB." + COLUMN_ToDateTime + " " +
                    "               END ) ) DESC";

        } catch (Exception e) {
            sqlstr = "";
            Date dt = new Date();
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getAlldbObjJobsForDashboardSubSQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getsearchJobsWithoutPostalSQLstr(Integer CatTypeID, String Criteria) {
        String sqlstr = "";
        String whereStr = "";
        String convertedJobsIDXStr = "";
        boolean needORstatement = false;
        boolean needANDstatement = false;
        boolean searchViaJX = false;
        boolean searchViaTAG = false;
        boolean searchViaIX = false;
        boolean searchViaInvNo = false;

        try {
            if (CatTypeID != null && CatTypeID >= 0) {
                whereStr += " (JB." + COLUMN_CatTypeID + " = '" + String.valueOf(CatTypeID) + "' ";
            }

            if (whereStr != "") {
                whereStr += ") ";
                //WHERE (JB.COLUMN_CatTypeID = XXXX)
                //WHERE (JB.COLUMN_CatTypeID = XXXX AND JB.Postal = XXXX)
            }

            if (Criteria.indexOf(objGlobalVariables.JobIXKey) == 0 && whereStr != "") {
                convertedJobsIDXStr = Criteria.substring(2, Criteria.length());
                if (needORstatement) {
                    searchViaJX = true;
                    whereStr += " OR JB." + COLUMN_JobsIDX + " = '" + convertedJobsIDXStr + "' ";
                    needORstatement = true;
                } else if (!needORstatement) {
                    searchViaJX = true;
                    whereStr += " AND (JB." + COLUMN_JobsIDX + " = '" + convertedJobsIDXStr + "' ";
                    needORstatement = true;
                }
            } else if (Criteria.indexOf(objGlobalVariables.JobIXKey) == 0 && whereStr == "") {
                searchViaJX = true;
                convertedJobsIDXStr = Criteria.substring(2, Criteria.length());
                whereStr += " (JB." + COLUMN_JobsIDX + " = '" + convertedJobsIDXStr + "' ";
                needORstatement = true;
            }

            searchViaTAG = objGlobalMethods.verifyBarcode(Criteria);
            if (searchViaTAG && whereStr != "") {
                whereStr += " AND (UPPER(JB." + COLUMN_TAGS + ") LIKE '%" + "[" + Criteria + "]" + "%' ";
                needORstatement = true;
                //WHERE (JB.COLUMN_CatTypeID = XXXX) AND (JB.TAGS LIKE '%[XXXX]%'
                //WHERE (JB.COLUMN_CatTypeID = XXXX AND JB.Postal = XXXX) AND (JB.TAGS LIKE '%[XXXX]%'
            } else if (searchViaTAG && whereStr == "") {
                whereStr += " (UPPER(JB." + COLUMN_TAGS + ") LIKE '%" + "[" + Criteria + "]" + "%' ";
                needORstatement = true;
                //WHERE (JB.TAGS LIKE '%[XXXX]%'
            }

            if (Criteria.indexOf(objGlobalVariables.InvoiceIXKey) == 0 && whereStr != "") {
                convertedJobsIDXStr = Criteria.substring(2, Criteria.length());
                if (needORstatement) {
                    searchViaIX = true;
                    whereStr += " OR JB." + COLUMN_DO_INV_IDX + " LIKE '%" + "[" + convertedJobsIDXStr + "]" + "%' ";
                    needORstatement = true;
                } else if (!needORstatement) {
                    searchViaIX = true;
                    whereStr += " AND (JB." + COLUMN_DO_INV_IDX + " LIKE '%" + "[" + convertedJobsIDXStr + "]" + "%' ";
                    needORstatement = true;
                }
            } else if (Criteria.indexOf(objGlobalVariables.InvoiceIXKey) == 0 && whereStr == "") {
                searchViaIX = true;
                convertedJobsIDXStr = Criteria.substring(2, Criteria.length());
                whereStr += " (JB." + COLUMN_DO_INV_IDX + " LIKE '%" + "[" + convertedJobsIDXStr + "]" + "%' ";
                needORstatement = true;
            }

            if (Criteria.indexOf(objGlobalVariables.EIKEY) == 0 && whereStr != "") {
                convertedJobsIDXStr = Criteria.substring(5, Criteria.length());
                if (needORstatement) {
                    searchViaInvNo = true;
                    whereStr += " OR UPPER(JB." + COLUMN_DO_INV_No + ") LIKE '%" + "[" + convertedJobsIDXStr + "]" + "%' ";
                    needORstatement = true;
                    //WHERE (JB.COLUMN_CatTypeID = XXXX) AND (JB.TAGS LIKE '%[XXXX]%' OR JB.DO_INV_No LIKE '%XXXX%'
                    //WHERE (JB.COLUMN_CatTypeID = XXXX AND JB.Postal = XXXX) AND (JB.TAGS LIKE '%[XXXX]%' OR JB.DO_INV_No LIKE '%XXXX%'
                    //WHERE (JB.TAGS LIKE '%[XXXX]%' OR JB.DO_INV_No LIKE '%XXXX%'
                } else if (!needORstatement) {
                    searchViaInvNo = true;
                    whereStr += " AND (UPPER(JB." + COLUMN_DO_INV_No + ") LIKE '%" + "[" + convertedJobsIDXStr + "]" + "%' ";
                    needORstatement = true;
                    //WHERE (JB.COLUMN_CatTypeID = XXXX) AND (JB.DO_INV_No LIKE '%XXXX%'
                    //WHERE (JB.COLUMN_CatTypeID = XXXX AND JB.Postal = XXXX) AND (JB.DO_INV_No LIKE '%XXXX%'
                }
            } else if (Criteria.indexOf(objGlobalVariables.EIKEY) == 0 && whereStr == "") {
                searchViaInvNo = true;
                convertedJobsIDXStr = Criteria.substring(5, Criteria.length());
                whereStr += " (UPPER(JB." + COLUMN_DO_INV_No + ") LIKE '%" + "[" + convertedJobsIDXStr + "]" + "%' ";
                needORstatement = true;
                //WHERE (JB.DO_INV_No LIKE '%[XXXX]%'
            }

            if (!searchViaJX && !searchViaTAG && !searchViaIX && !searchViaInvNo) {
                if ((Criteria.indexOf(objGlobalVariables.CNKey) == 0 || Criteria.indexOf(objGlobalVariables.SMKey) == 0) && whereStr != "") {
                    if (needORstatement) {
                        whereStr += " OR UPPER(JB." + COLUMN_CNNo + ") = '" + Criteria + "' ";
                        needORstatement = true;
                    } else if (!needORstatement) {
                        whereStr += " AND (UPPER(JB." + COLUMN_CNNo + ") = '" + Criteria + "' ";
                        needORstatement = true;
                    }
                } else if ((Criteria.indexOf(objGlobalVariables.CNKey) == 0 || Criteria.indexOf(objGlobalVariables.SMKey) == 0) && whereStr == "") {
                    whereStr += " (UPPER(JB." + COLUMN_CNNo + ") = '" + Criteria + "' ";
                    needORstatement = true;
                }
            }

            if (!searchViaJX && !searchViaTAG && !searchViaIX && !searchViaInvNo) {
                if (whereStr != "") {
                    if (needORstatement) {
                        whereStr += " OR UPPER(JB." + COLUMN_JobNo + ") = '" + Criteria + "' ";
                        needORstatement = true;
                    } else if (!needORstatement) {
                        whereStr += " AND (UPPER(JB." + COLUMN_JobNo + ") = '" + Criteria + "' ";
                        needORstatement = true;
                    }
                } else if (whereStr == "") {
                    whereStr += " (UPPER(JB." + COLUMN_JobNo + ") = '" + Criteria + "' ";
                    needORstatement = true;
                }
            }

            if (!searchViaJX && !searchViaTAG && !searchViaIX && !searchViaInvNo) {
                if (!searchViaIX && whereStr != "") {
                    if (needORstatement) {
                        whereStr += " OR UPPER(JB." + COLUMN_DO_INV_No + ") LIKE '%" + "[" + Criteria + "]" + "%' ";
                        needORstatement = true;
                    } else if (!needORstatement) {
                        whereStr += " AND (UPPER(JB." + COLUMN_DO_INV_No + ") LIKE '%" + "[" + Criteria + "]" + "%' ";
                        needORstatement = true;
                    }
                } else if (!searchViaIX && whereStr == "") {
                    whereStr += " (UPPER(JB." + COLUMN_DO_INV_No + ") LIKE '%" + "[" + Criteria + "]" + "%' ";
                    needORstatement = true;
                }
            }

            if (!searchViaJX && !searchViaTAG && !searchViaIX && !searchViaInvNo) {
                if (whereStr != "") {
                    if (needORstatement) {
                        whereStr += " OR UPPER(JB." + COLUMN_CNNo + ") = '" + Criteria + "' ";
                        needORstatement = true;
                    } else if (!needORstatement) {
                        whereStr += " AND (UPPER(JB." + COLUMN_CNNo + ") = '" + Criteria + "' ";
                        needORstatement = true;
                    }
                } else if (whereStr != "") {
                    whereStr += " (UPPER(JB." + COLUMN_CNNo + ") = '" + Criteria + "' ";
                    needORstatement = true;
                }
            }

            if (!searchViaJX && !searchViaTAG && !searchViaIX && !searchViaInvNo) {
                if (whereStr != "") {
                    if (needORstatement) {
                        whereStr += "  OR UPPER(JB." + COLUMN_TAGS + ") LIKE '%" + "[" + Criteria + "]" + "%' ";
                        needORstatement = true;
                    } else if (!needORstatement) {
                        whereStr += " AND (UPPER(JB." + COLUMN_TAGS + ") LIKE '%" + "[" + Criteria + "]" + "%' ";
                        needORstatement = true;
                    }
                } else if (!searchViaTAG && whereStr == "") {
                    whereStr += " (UPPER(JB." + COLUMN_TAGS + ") LIKE '%" + "[" + Criteria + "]" + "%' ";
                    needORstatement = true;
                }
            }

            if (needORstatement) {
                whereStr += ")";
            }

            sqlstr = "SELECT  JB." + COLUMN_ID + ", JB." + COLUMN_JobsIDX + ", JB." + COLUMN_CatTypeID + ", JB." + COLUMN_Postal + ", JB." + COLUMN_AddressWithPostalNoUnit + ", JB." + COLUMN_AddressWithoutPostal + ", JB." +
                    COLUMN_LAT + ", JB." + COLUMN_LNG + ", JB." + COLUMN_ExpType + ", JB." + COLUMN_ExpTypeDesc + ", JB." + COLUMN_IsPartOne + ", JB." + COLUMN_PartDesc + ", JB." + COLUMN_IsDunning +
                    ", JB." + COLUMN_HasDOCOD + ", JB." + COLUMN_HasTradeIn + ", JB." + COLUMN_JobNo + ", JB." +
                    COLUMN_CNNo + ", JB." + COLUMN_CompanyName + ", JB." + COLUMN_ContactName + ", JB." + COLUMN_DO_INV_No + ", JB." + COLUMN_DO_INV_IDX + ", JB." + COLUMN_TAGS + ", JB." + COLUMN_FromDateTime + ", JB." + COLUMN_PickByDateTime + ", JB." +
                    COLUMN_ReadyDateTime + ", JB." + COLUMN_ToDateTime + ", JB." + COLUMN_ExtDateTime + ", JB." + COLUMN_ExtFromDateTime + ", JB." + COLUMN_FLAG + ", JB." + COLUMN_PCS + ", JB." + COLUMN_StatusCodeIDX + ", JB." + COLUMN_IsNewlyInserted +
                    ", IFNULL(JB." + COLUMN_FM_AddressIDX + ",0) AS " + COLUMN_FM_AddressIDX + ", IFNULL(JB." + COLUMN_FM_ContactIDX + ",0) AS " + COLUMN_FM_ContactIDX
                    + ", IFNULL(JB." + COLUMN_TO_AddressIDX + ",0) AS " + COLUMN_TO_AddressIDX + " , IFNULL(JB." + COLUMN_TO_ContactIDX + ",0) AS " + COLUMN_TO_ContactIDX +
                    ", IFNULL(JB." + COLUMN_JobInfoJsonStr + ",'') AS " + COLUMN_JobInfoJsonStr + ", " +
                    " IFNULL(JB." + COLUMN_CHECKLISTS + ",'') AS " + COLUMN_CHECKLISTS + ", " +
                    " IFNULL(JB." + COLUMN_NetsVisaTI + ",'0') AS " + COLUMN_NetsVisaTI + ", " +
                    //================================================================================================================================================
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) THEN datetime(JB." + COLUMN_ReadyDateTime + ", 'unixepoch', 'localtime') " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN datetime(JB." + COLUMN_FromDateTime + ", 'unixepoch', 'localtime') " +
                    "    END ) AS FromDateTimeConverted , " +
                    //================================================================================================================================================
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) THEN 'PU FM ' " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN 'DEL FM ' " +
                    "    END ) AS FromText , " +
                    //================================================================================================================================================
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) " +
                    "           THEN ( CASE WHEN JB." + COLUMN_PickByDateTime + " = JB." + COLUMN_ReadyDateTime + " THEN datetime(JB." + COLUMN_ToDateTime + ", 'unixepoch', 'localtime') " +
                    "                       ELSE datetime(JB." + COLUMN_PickByDateTime + ", 'unixepoch', 'localtime') " +
                    "                END ) " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN datetime(JB." + COLUMN_ToDateTime + ", 'unixepoch', 'localtime') " +
                    "      END ) AS ToDateTimeConverted , " +
                    //================================================================================================================================================
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) " +
                    "           THEN ( CASE WHEN JB." + COLUMN_PickByDateTime + " = JB." + COLUMN_ReadyDateTime + " THEN 'DEL BY ' " +
                    "                       ELSE 'PU BY ' " +
                    "                 END ) " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN 'DEL BY ' " +
                    "    END ) AS ToText, " +
                    //================================================================================================================================================
                    "    0 isPickAndSendCount, 0 JobChangesCount, 0 HASLUNCHTIM " +
                    "FROM " + TABLE_NAME + " JB " +
                    "WHERE   " + whereStr;

        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getsearchJobsWithoutPostalSQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getsearchJobsSQLstr2(Integer CatTypeID, String Postal, String Criteria) {
        String sqlstr = "";
        String whereStr = "";
        String convertedJobsIDXStr = "";
        boolean needORstatement = false;
        boolean needANDstatement = false;
        boolean searchViaJX = false;
        boolean searchViaTAG = false;
        boolean searchViaIX = false;
        boolean searchViaInvNo = false;

        try {
            if (CatTypeID != null && CatTypeID >= 0) {
                whereStr += " (JB." + COLUMN_CatTypeID + " = '" + String.valueOf(CatTypeID) + "' ";
            }

            if (Postal != null && Postal != "" && whereStr != "") {
                whereStr += " AND JB." + COLUMN_Postal + " = '" + Postal + "' ";
                //WHERE (JB.COLUMN_CatTypeID = XXXX AND JB.COLUMN_Postal = XXXX
            } else if (Postal != null && Postal != "" && whereStr == "") {
                whereStr += " (JB." + COLUMN_Postal + " = '" + Postal + "' ";
                //WHERE (JB.Postal = XXXX
            }

            if (whereStr != "") {
                whereStr += ") ";
                //WHERE (JB.COLUMN_CatTypeID = XXXX)
                //WHERE (JB.COLUMN_CatTypeID = XXXX AND JB.Postal = XXXX)
            }

            if (Criteria.indexOf(objGlobalVariables.JobIXKey) == 0 && whereStr != "") {
                convertedJobsIDXStr = Criteria.substring(2, Criteria.length());
                if (needORstatement) {
                    searchViaJX = true;
                    whereStr += " OR JB." + COLUMN_JobsIDX + " = '" + convertedJobsIDXStr + "' ";
                    needORstatement = true;
                } else if (!needORstatement) {
                    searchViaJX = true;
                    whereStr += " AND (JB." + COLUMN_JobsIDX + " = '" + convertedJobsIDXStr + "' ";
                    needORstatement = true;
                }
            } else if (Criteria.indexOf(objGlobalVariables.JobIXKey) == 0 && whereStr == "") {
                searchViaJX = true;
                convertedJobsIDXStr = Criteria.substring(2, Criteria.length());
                whereStr += " (JB." + COLUMN_JobsIDX + " = '" + convertedJobsIDXStr + "' ";
                needORstatement = true;
            }

            searchViaTAG = objGlobalMethods.verifyBarcode(Criteria);
            if (searchViaTAG && whereStr != "") {
                whereStr += " AND (UPPER(JB." + COLUMN_TAGS + ") LIKE '%" + "[" + Criteria + "]" + "%' ";
                needORstatement = true;
                //WHERE (JB.COLUMN_CatTypeID = XXXX) AND (JB.TAGS LIKE '%[XXXX]%'
                //WHERE (JB.COLUMN_CatTypeID = XXXX AND JB.Postal = XXXX) AND (JB.TAGS LIKE '%[XXXX]%'
            } else if (searchViaTAG && whereStr == "") {
                whereStr += " (UPPER(JB." + COLUMN_TAGS + ") LIKE '%" + "[" + Criteria + "]" + "%' ";
                needORstatement = true;
                //WHERE (JB.TAGS LIKE '%[XXXX]%'
            }

            if (Criteria.indexOf(objGlobalVariables.InvoiceIXKey) == 0 && whereStr != "") {
                convertedJobsIDXStr = Criteria.substring(2, Criteria.length());
                if (needORstatement) {
                    searchViaIX = true;
                    whereStr += " OR JB." + COLUMN_DO_INV_IDX + " LIKE '%" + "[" + convertedJobsIDXStr + "]" + "%' ";
                    needORstatement = true;
                } else if (!needORstatement) {
                    searchViaIX = true;
                    whereStr += " AND (JB." + COLUMN_DO_INV_IDX + " LIKE '%" + "[" + convertedJobsIDXStr + "]" + "%' ";
                    needORstatement = true;
                }
            } else if (Criteria.indexOf(objGlobalVariables.InvoiceIXKey) == 0 && whereStr == "") {
                searchViaIX = true;
                convertedJobsIDXStr = Criteria.substring(2, Criteria.length());
                whereStr += " (JB." + COLUMN_DO_INV_IDX + " LIKE '%" + "[" + convertedJobsIDXStr + "]" + "%' ";
                needORstatement = true;
            }

            if (Criteria.indexOf(objGlobalVariables.EIKEY) == 0 && whereStr != "") {
                convertedJobsIDXStr = Criteria.substring(5, Criteria.length());
                if (needORstatement) {
                    searchViaInvNo = true;
                    whereStr += " OR UPPER(JB." + COLUMN_DO_INV_No + ") LIKE '%" + "[" + convertedJobsIDXStr + "]" + "%' ";
                    needORstatement = true;
                    //WHERE (JB.COLUMN_CatTypeID = XXXX) AND (JB.TAGS LIKE '%[XXXX]%' OR JB.DO_INV_No LIKE '%XXXX%'
                    //WHERE (JB.COLUMN_CatTypeID = XXXX AND JB.Postal = XXXX) AND (JB.TAGS LIKE '%[XXXX]%' OR JB.DO_INV_No LIKE '%XXXX%'
                    //WHERE (JB.TAGS LIKE '%[XXXX]%' OR JB.DO_INV_No LIKE '%XXXX%'
                } else if (!needORstatement) {
                    searchViaInvNo = true;
                    whereStr += " AND (UPPER(JB." + COLUMN_DO_INV_No + ") LIKE '%" + "[" + convertedJobsIDXStr + "]" + "%' ";
                    needORstatement = true;
                    //WHERE (JB.COLUMN_CatTypeID = XXXX) AND (JB.DO_INV_No LIKE '%XXXX%'
                    //WHERE (JB.COLUMN_CatTypeID = XXXX AND JB.Postal = XXXX) AND (JB.DO_INV_No LIKE '%XXXX%'
                }
            } else if (Criteria.indexOf(objGlobalVariables.EIKEY) == 0 && whereStr == "") {
                searchViaInvNo = true;
                convertedJobsIDXStr = Criteria.substring(5, Criteria.length());
                whereStr += " (UPPER(JB." + COLUMN_DO_INV_No + ") LIKE '%" + "[" + convertedJobsIDXStr + "]" + "%' ";
                needORstatement = true;
                //WHERE (JB.DO_INV_No LIKE '%[XXXX]%'
            }

            if (!searchViaJX && !searchViaTAG && !searchViaIX && !searchViaInvNo) {
                if ((Criteria.indexOf(objGlobalVariables.CNKey) == 0 || Criteria.indexOf(objGlobalVariables.SMKey) == 0) && whereStr != "") {
                    if (needORstatement) {
                        whereStr += " OR UPPER(JB." + COLUMN_CNNo + ") = '" + Criteria + "' ";
                        needORstatement = true;
                    } else if (!needORstatement) {
                        whereStr += " AND (UPPER(JB." + COLUMN_CNNo + ") = '" + Criteria + "' ";
                        needORstatement = true;
                    }
                } else if ((Criteria.indexOf(objGlobalVariables.CNKey) == 0 || Criteria.indexOf(objGlobalVariables.SMKey) == 0) && whereStr == "") {
                    whereStr += " (UPPER(JB." + COLUMN_CNNo + ") = '" + Criteria + "' ";
                    needORstatement = true;
                }
            }

            if (!searchViaJX && !searchViaTAG && !searchViaIX && !searchViaInvNo) {
                if (whereStr != "") {
                    if (needORstatement) {
                        whereStr += " OR UPPER(JB." + COLUMN_JobNo + ") = '" + Criteria + "' ";
                        needORstatement = true;
                    } else if (!needORstatement) {
                        whereStr += " AND (UPPER(JB." + COLUMN_JobNo + ") = '" + Criteria + "' ";
                        needORstatement = true;
                    }
                } else if (whereStr == "") {
                    whereStr += " (UPPER(JB." + COLUMN_JobNo + ") = '" + Criteria + "' ";
                    needORstatement = true;
                }
            }

            if (!searchViaJX && !searchViaTAG && !searchViaIX && !searchViaInvNo) {
                if (!searchViaIX && whereStr != "") {
                    if (needORstatement) {
                        whereStr += " OR UPPER(JB." + COLUMN_DO_INV_No + ") LIKE '%" + "[" + Criteria + "]" + "%' ";
                        needORstatement = true;
                    } else if (!needORstatement) {
                        whereStr += " AND (UPPER(JB." + COLUMN_DO_INV_No + ") LIKE '%" + "[" + Criteria + "]" + "%' ";
                        needORstatement = true;
                    }
                } else if (!searchViaIX && whereStr == "") {
                    whereStr += " (UPPER(JB." + COLUMN_DO_INV_No + ") LIKE '%" + "[" + Criteria + "]" + "%' ";
                    needORstatement = true;
                }
            }

            if (!searchViaJX && !searchViaTAG && !searchViaIX && !searchViaInvNo) {
                if (whereStr != "") {
                    if (needORstatement) {
                        whereStr += " OR UPPER(JB." + COLUMN_CNNo + ") = '" + Criteria + "' ";
                        needORstatement = true;
                    } else if (!needORstatement) {
                        whereStr += " AND (UPPER(JB." + COLUMN_CNNo + ") = '" + Criteria + "' ";
                        needORstatement = true;
                    }
                } else if (whereStr != "") {
                    whereStr += " (UPPER(JB." + COLUMN_CNNo + ") = '" + Criteria + "' ";
                    needORstatement = true;
                }
            }

            if (!searchViaJX && !searchViaTAG && !searchViaIX && !searchViaInvNo) {
                if (whereStr != "") {
                    if (needORstatement) {
                        whereStr += "  OR UPPER(JB." + COLUMN_TAGS + ") LIKE '%" + "[" + Criteria + "]" + "%' ";
                        needORstatement = true;
                    } else if (!needORstatement) {
                        whereStr += " AND (UPPER(JB." + COLUMN_TAGS + ") LIKE '%" + "[" + Criteria + "]" + "%' ";
                        needORstatement = true;
                    }
                } else if (!searchViaTAG && whereStr == "") {
                    whereStr += " (UPPER(JB." + COLUMN_TAGS + ") LIKE '%" + "[" + Criteria + "]" + "%' ";
                    needORstatement = true;
                }
            }

            if (needORstatement) {
                whereStr += ")";
            }

            sqlstr = "SELECT  JB." + COLUMN_ID + ", JB." + COLUMN_JobsIDX + ", JB." + COLUMN_CatTypeID + ", JB." + COLUMN_Postal + ", JB." + COLUMN_AddressWithPostalNoUnit + ", JB." + COLUMN_AddressWithoutPostal + ", JB." +
                    COLUMN_LAT + ", JB." + COLUMN_LNG + ", JB." + COLUMN_ExpType + ", JB." + COLUMN_ExpTypeDesc + ", JB." + COLUMN_IsPartOne + ", JB." + COLUMN_PartDesc + ", JB." + COLUMN_IsDunning +
                    ", JB." + COLUMN_HasDOCOD + ", JB." + COLUMN_HasTradeIn + ", JB." + COLUMN_JobNo + ", JB." +
                    COLUMN_CNNo + ", JB." + COLUMN_CompanyName + ", JB." + COLUMN_ContactName + ", JB." + COLUMN_DO_INV_No + ", JB." + COLUMN_DO_INV_IDX + ", JB." + COLUMN_TAGS + ", JB." + COLUMN_FromDateTime + ", JB." + COLUMN_PickByDateTime + ", JB." +
                    COLUMN_ReadyDateTime + ", JB." + COLUMN_ToDateTime + ", JB." + COLUMN_ExtDateTime + ", JB." + COLUMN_ExtFromDateTime + ", JB." + COLUMN_FLAG + ", JB." + COLUMN_PCS + ", JB." + COLUMN_StatusCodeIDX + ", JB." + COLUMN_IsNewlyInserted +
                    ", IFNULL(JB." + COLUMN_FM_AddressIDX + ",0) AS " + COLUMN_FM_AddressIDX + ", IFNULL(JB." + COLUMN_FM_ContactIDX + ",0) AS " + COLUMN_FM_ContactIDX
                    + ", IFNULL(JB." + COLUMN_TO_AddressIDX + ",0) AS " + COLUMN_TO_AddressIDX + " , IFNULL(JB." + COLUMN_TO_ContactIDX + ",0) AS " + COLUMN_TO_ContactIDX +
                    ", IFNULL(JB." + COLUMN_JobInfoJsonStr + ",'') AS " + COLUMN_JobInfoJsonStr + ", " +
                    " IFNULL(JB." + COLUMN_CHECKLISTS + ",'') AS " + COLUMN_CHECKLISTS + ", " +
                    " IFNULL(JB." + COLUMN_NetsVisaTI + ",'0') AS " + COLUMN_NetsVisaTI + ", " +
                    //================================================================================================================================================
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) THEN datetime(JB." + COLUMN_ReadyDateTime + ", 'unixepoch', 'localtime') " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN datetime(JB." + COLUMN_FromDateTime + ", 'unixepoch', 'localtime') " +
                    "    END ) AS FromDateTimeConverted , " +
                    //================================================================================================================================================
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) THEN 'PU FM ' " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN 'DEL FM ' " +
                    "    END ) AS FromText , " +
                    //================================================================================================================================================
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) " +
                    "           THEN ( CASE WHEN JB." + COLUMN_PickByDateTime + " = JB." + COLUMN_ReadyDateTime + " THEN datetime(JB." + COLUMN_ToDateTime + ", 'unixepoch', 'localtime') " +
                    "                       ELSE datetime(JB." + COLUMN_PickByDateTime + ", 'unixepoch', 'localtime') " +
                    "                END ) " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN datetime(JB." + COLUMN_ToDateTime + ", 'unixepoch', 'localtime') " +
                    "      END ) AS ToDateTimeConverted , " +
                    //================================================================================================================================================
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) " +
                    "           THEN ( CASE WHEN JB." + COLUMN_PickByDateTime + " = JB." + COLUMN_ReadyDateTime + " THEN 'DEL BY ' " +
                    "                       ELSE 'PU BY ' " +
                    "                 END ) " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN 'DEL BY ' " +
                    "    END ) AS ToText, " +
                    //================================================================================================================================================
                    "    0 isPickAndSendCount, 0 JobChangesCount, 0 HASLUNCHTIM " +
                    "FROM " + TABLE_NAME + " JB " +
                    "WHERE   " + whereStr;

        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getsearchJobs. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getsearchJobsViaJobsIDXsSQLstr(String JobsIDXs) {
        String sqlstr = "";
        String whereStr = "";
        try {

            whereStr = " JB." + COLUMN_JobsIDX + " IN (" + JobsIDXs + ")";
            sqlstr = DATABASE_GETDBOBJJOBSQUERY() + whereStr;

        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getsearchJobsViaJobsIDXs. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getTotalJobsCountPerCatTypeSQLstr(Integer CatTypeID) {
        String sqlstr = "";

        try {
            sqlstr = "SELECT COUNT (*) AS CT " +
                    "FROM " + TABLE_NAME + " JB " +
                    "WHERE JB." + COLUMN_CatTypeID + " = '" + CatTypeID + "' ";
        } catch (Exception e) {
            sqlstr = "";
            Date dt = new Date();
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getTotalJobsCountPerCatTypeSQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getTotalJobsCountPerCatTypePerPostal_And_JobsIDXsStrSQLstr2(Integer CatTypeID, String Postal, String JobsIDXs) {
        String sqlstr = "";

        try {
            sqlstr = "SELECT COUNT (*) AS CT, " +
                    "(SELECT COUNT(*) AS C FROM " + TABLE_NAME + " T3 WHERE T3." + COLUMN_CatTypeID + " = JB." + COLUMN_CatTypeID + " AND (T3.NetsVisaTi & 4) == 4 AND " +
                    "T3.JobsIDX IN (" + JobsIDXs + ") AND T3.Postal = '" + Postal + "') AS ReqNetsCount, " +
                    "(SELECT COUNT(*) AS C FROM " + TABLE_NAME + " T3 WHERE T3." + COLUMN_CatTypeID + " = JB." + COLUMN_CatTypeID + " AND (T3.NetsVisaTi & 8) == 8 AND " +
                    "T3.JobsIDX IN (" + JobsIDXs + ") AND T3.Postal = '" + Postal + "') AS ReqVisaCount, " +
                    "(SELECT COUNT(*) AS C FROM " + TABLE_NAME + " T3 WHERE T3." + COLUMN_CatTypeID + " = JB." + COLUMN_CatTypeID + " AND (T3.NetsVisaTi & 16) == 16 AND " +
                    "T3.JobsIDX IN (" + JobsIDXs + ") AND T3.Postal = '" + Postal + "') AS ReqTiCount " +
                    "FROM " + TABLE_NAME + " JB " +
                    "WHERE JB." + COLUMN_CatTypeID + " = '" + CatTypeID + "' " +
                    "AND JB." + COLUMN_JobsIDX + " IN (" + JobsIDXs + ") " +
                    "AND JB." + COLUMN_Postal + " = '" + Postal + "' ";
        } catch (Exception e) {
            sqlstr = "";
            Date dt = new Date();
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getTotalJobsCountPerCatTypePerPostal_And_JobsIDXsStrSQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getTotalJobsCountPerCatTypePerPostal_And_JobsIDXsStrSQLstr(Integer CatTypeID, String Postal, String JobsIDXs) {
        String sqlstr = "";

        try {
            sqlstr = "SELECT COUNT (*) AS CT " +
                    "FROM " + TABLE_NAME + " JB " +
                    "WHERE JB." + COLUMN_CatTypeID + " = '" + CatTypeID + "' " +
                    "AND JB." + COLUMN_JobsIDX + " IN (" + JobsIDXs + ") " +
                    "AND JB." + COLUMN_Postal + " = '" + Postal + "' ";
        } catch (Exception e) {
            sqlstr = "";
            Date dt = new Date();
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getTotalJobsCountPerCatTypePerPostal_And_JobsIDXsStrSQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getTotalJobsCountPerCatTypePerPostalSQLstr2(Integer CatTypeID, String Postal) {
        String sqlstr = "";

        try {
            sqlstr = "SELECT COUNT (*) AS CT, " +
                    "(SELECT COUNT(*) AS C FROM " + TABLE_NAME + " T3 WHERE T3." + COLUMN_CatTypeID + " = JB." + COLUMN_CatTypeID + " AND (T3.FLAG2 & 64) == 64 AND " +
                    "T3." + COLUMN_Postal + " = '" + Postal + "') AS ColdCount, " +
                    "(SELECT COUNT(*) AS C FROM " + TABLE_NAME + " T3 WHERE T3." + COLUMN_CatTypeID + " = JB." + COLUMN_CatTypeID + " AND (T3.NetsVisaTi & 4) == 4 AND " +
                    "T3." + COLUMN_Postal + " = '" + Postal + "') AS ReqNetsCount, " +
                    "(SELECT COUNT(*) AS C FROM " + TABLE_NAME + " T3 WHERE T3." + COLUMN_CatTypeID + " = JB." + COLUMN_CatTypeID + " AND (T3.NetsVisaTi & 8) == 8 AND " +
                    "T3." + COLUMN_Postal + " = '" + Postal + "') AS ReqVisaCount, " +
                    "(SELECT COUNT(*) AS C FROM " + TABLE_NAME + " T3 WHERE T3." + COLUMN_CatTypeID + " = JB." + COLUMN_CatTypeID + " AND (T3.NetsVisaTi & 16) == 16 AND " +
                    "T3." + COLUMN_Postal + " = '" + Postal + "') AS ReqTiCount " +
                    "FROM " + TABLE_NAME + " JB " +
                    "WHERE JB." + COLUMN_CatTypeID + " = '" + CatTypeID + "' " +
                    "AND JB." + COLUMN_Postal + " = '" + Postal + "' ";
        } catch (Exception e) {
            sqlstr = "";
            Date dt = new Date();
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getTotalJobsCountPerCatTypePerPostalSQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getTotalJobsCountPerCatTypePerPostalSQLstr(Integer CatTypeID, String Postal) {
        String sqlstr = "";

        try {
            sqlstr = "SELECT COUNT (*) AS CT " +
                    "FROM " + TABLE_NAME + " JB " +
                    "WHERE JB." + COLUMN_CatTypeID + " = '" + CatTypeID + "' " +
                    "AND JB." + COLUMN_Postal + " = '" + Postal + "' ";
        } catch (Exception e) {
            sqlstr = "";
            Date dt = new Date();
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getTotalJobsCountPerCatTypePerPostalSQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getTotalOriPIPDIPJobsCountByPostalSQLstr(String Postal) {
        String sqlstr = "";

        try {
            sqlstr = "SELECT COUNT (*) AS CT " +
                    "FROM " + TABLE_NAME + " JB " +
                    "WHERE JB." + COLUMN_CatTypeID + " IN (0,2) " +
                    "AND JB." + COLUMN_Postal + " = '" + Postal + "' AND IFNULL(JB." + COLUMN_UNPARK + ",0) = 0";
        } catch (Exception e) {
            sqlstr = "";
            Date dt = new Date();
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getTotalOriPIPDIPJobsCountByPostalSQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getTotalPIPDIPJobsCountPerPostalSQLstr(String Postal) {
        String sqlstr = "";

        try {
            sqlstr = "SELECT COUNT (*) AS CT " +
                    "FROM " + TABLE_NAME + " JB " +
                    "WHERE JB." + COLUMN_CatTypeID + " IN (0, 2) " +
                    "AND JB." + COLUMN_Postal + " = '" + Postal + "' ";
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getTotalPIPDIPJobsCountPerPostalSQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getTotalPIPDIPJobsCountPerPostalWithJobsIDXsSQLstr(String Postal, String JobsIDXs) {
        String sqlstr = "";

        try {
            sqlstr = "SELECT COUNT (*) AS CT " +
                    "FROM " + TABLE_NAME + " JB " +
                    "WHERE JB." + COLUMN_CatTypeID + " IN (0, 2) " +
                    "AND JB." + COLUMN_JobsIDX + " IN (" + JobsIDXs + ") " +
                    "AND JB." + COLUMN_Postal + " = '" + Postal + "' ";
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getTotalPIPDIPJobsCountPerPostalWithJobsIDXsSQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getTotalPUFDJobsCountSQLstr() {
        String sqlstr = "";

        try {
            sqlstr = "SELECT COUNT (*) AS CT " +
                    "FROM " + TABLE_NAME + " JB " +
                    "WHERE JB." + COLUMN_CatTypeID + " IN (1, 5)";
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getgetTotalPUFDJobsCountSQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getTotalOutstandingPUOKPCODsSQLstr(String JobsIDXs) {
        String sqlstr = "";

        try {
            sqlstr = "SELECT COUNT (*) AS CT " +
                    "FROM " + TABLE_NAME + " JB " +
                    "WHERE JB." + COLUMN_CatTypeID + " IN (1, 3) " +
                    "AND JB." + COLUMN_JobsIDX + " IN (" + JobsIDXs + ")";
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getTotalOutstandingPUOKPCODsSQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getAlldbObjJobsPerCategeorySQLstr(Integer CatTypeID) {
        String sqlstr = "";
        String whereStr = "";
        try {
            whereStr = " JB." + COLUMN_CatTypeID + " = '" + CatTypeID + "'";
            sqlstr = DATABASE_GETDBOBJJOBSQUERY() + whereStr;
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getAlldbObjJobsPerCategeorySQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getAlldbObjJobsPerCategeoryWithOrigParkedJobsSQLstr(Integer CatTypeID) {
        String sqlstr = "";
        String whereStr = "";
        try {
            whereStr = " (JB." + COLUMN_CatTypeID + " = '" + CatTypeID + "')";
            whereStr += " OR (JB." + COLUMN_CatTypeID + " = '2' AND IFNULL(JB." + COLUMN_UNPARK + ", 0) = 1)";
            sqlstr = DATABASE_GETDBOBJJOBSQUERY() + whereStr;
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getAlldbObjJobsPerCategeoryWithOrigParkedJobsSQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getAllParkedJobsdbObjJobsByPostals(String postals) {
        String sqlstr = "";
        String whereStr = "";
        try {
            whereStr = " (JB." + COLUMN_CatTypeID + " = '6') AND JB." + COLUMN_Postal + " IN ('" + postals + "')";
            sqlstr = DATABASE_GETDBOBJJOBSQUERY() + whereStr;
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getAllParkedJobsdbObjJobsByPostals. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getAlldbObjJobsIDXsPerCategorySQLstr(Integer CatTypeID) {
        String sqlstr = "";
        String whereStr = "";

        try {
            whereStr = " JB." + COLUMN_CatTypeID + " = '" + String.valueOf(CatTypeID) + "'" + (CatTypeID == 2 ? " AND IFNULL(JB." + COLUMN_UNPARK + ",0) = 0"   : "");

            sqlstr = "SELECT JB." + COLUMN_JobsIDX + " FROM " + TABLE_NAME + " JB WHERE " + whereStr;
        } catch (Exception e) {
            sqlstr = "";
            Date dt = new Date();
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getAlldbObjJobsIDXsPerCategorySQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getAllPIPnotUpdatedJobsIDXSQLstr() {
        String sqlstr = "";
        String whereStr = "";
        try {
            whereStr = " JB." + COLUMN_CatTypeID + " = '0' AND JB." + COLUMN_StatusCodeIDX + " IN (0,235,165)";
            sqlstr = "SELECT JB." + COLUMN_JobsIDX + " FROM " + TABLE_NAME + " JB WHERE " + whereStr;
        } catch (Exception e) {
            sqlstr = "";
            Date dt = new Date();
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getAlldbObjJobsPerCategeorySQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String gethfJobInfoJsonStrSQLstr(String Postal, int CatTypeID) {
        String sqlstr = "";

        try {
            sqlstr = "SELECT  IFNULL(JB." + COLUMN_JobInfoJsonStr + ",'') AS " + COLUMN_JobInfoJsonStr + " " +
                    "FROM    " + TABLE_NAME + " JB " +
                    "WHERE   JB." + COLUMN_Postal + " = '" + Postal + "' AND JB." + COLUMN_CatTypeID + " = '" + CatTypeID + "'";
        } catch (Exception e) {
            sqlstr = "";
            Date dt = new Date();
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs gethfJobInfoJsonStrSQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String gethfJobInfoJsonStrByJobsIDXSQLstr(Long JobsIDX) {
        String sqlstr = "";

        try {
            sqlstr = "SELECT  IFNULL(JB." + COLUMN_JobInfoJsonStr + ",'') AS " + COLUMN_JobInfoJsonStr + " " +
                    "FROM    " + TABLE_NAME + " JB " +
                    "WHERE   JB." + COLUMN_JobsIDX + " = '" + JobsIDX + "'";
        } catch (Exception e) {
            sqlstr = "";
            Date dt = new Date();
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs gethfJobInfoJsonStrByJobsIDXSQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String gethfJobInfoJsonStrAndLatLngByJobsIDXSQLstr(Long JobsIDX) {
        String sqlstr = "";

        try {
            sqlstr = "SELECT  IFNULL(JB." + COLUMN_JobInfoJsonStr + ",'') AS " + COLUMN_JobInfoJsonStr + ", " +
                    "IFNULL(JB." + COLUMN_LAT + ",'0') AS " + COLUMN_LAT + ", " +
                    "IFNULL(JB." + COLUMN_LNG + ",'0') AS " + COLUMN_LNG + ", " +
                    "IFNULL(JB." + COLUMN_CatTypeID + ",'0') AS " + COLUMN_CatTypeID + ", " +
                    "IFNULL(JB." + COLUMN_Postal + ",'0') AS " + COLUMN_Postal + ", " +
                    "IFNULL(JB." + COLUMN_AddressWithPostalNoUnit + ",'') AS " + COLUMN_AddressWithPostalNoUnit + ", " +
                    "IFNULL(JB." + COLUMN_AddressWithoutPostal + ",'') AS " + COLUMN_AddressWithoutPostal + " " +

                    "FROM    " + TABLE_NAME + " JB " +
                    "WHERE   JB." + COLUMN_JobsIDX + " = '" + JobsIDX + "'";
        } catch (Exception e) {
            sqlstr = "";
            Date dt = new Date();
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs gethfJobInfoJsonStrAndLatLngByJobsIDX. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getCheckListItemsByJobsIDX(Long JobsIDX) {
        String sqlstr = "";

        try {
            sqlstr = "SELECT  IFNULL(JB." + COLUMN_CHECKLISTS + ",'') AS " + COLUMN_CHECKLISTS + " " +
                    "FROM    " + TABLE_NAME + " JB " +
                    "WHERE   JB." + COLUMN_JobsIDX + " = '" + JobsIDX + "'";
        } catch (Exception e) {
            sqlstr = "";
            Date dt = new Date();
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getCheckListItemsByJobsIDX. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getcheckIfJobExistSQLstr(Long JobsIDX) {
        String sqlstr = "";

        try {
            sqlstr = "SELECT COUNT (*) AS C FROM " + TABLE_NAME + " JB WHERE JB." + COLUMN_JobsIDX + " = " + JobsIDX;
        } catch (Exception e) {
            sqlstr = "";
        } finally {
            return sqlstr;
        }
    }

    public static String getAllLocations() {
        String sqlstr = "";

        try {
            /*sqlstr = "SELECT JB." + COLUMN_JobsIDX + ", JB." + COLUMN_Postal + " AS POSTAL, (SELECT JB2." + COLUMN_AddressWithPostalNoUnit + " FROM " + TABLE_NAME + " JB2 WHERE JB2." + COLUMN_Postal + " = JB." + COLUMN_Postal + " LIMIT 1) AS ADDRESS, JB." + COLUMN_LAT
                    + " AS LAT, JB." + COLUMN_LNG + " AS LNG FROM "
                    + TABLE_NAME + " JB WHERE JB." + COLUMN_CatTypeID + " IN (0,2) ORDER BY JB." + COLUMN_Postal;*/

            sqlstr = "SELECT JB." + COLUMN_JobsIDX + ", JB." + COLUMN_Postal + " AS POSTAL, "
                    + "(SELECT JB2." + COLUMN_AddressWithPostalNoUnit + " FROM " + TABLE_NAME + " JB2 WHERE JB2." + COLUMN_Postal + " = JB." + COLUMN_Postal + " LIMIT 1) AS ADDRESS, "
                    + "JB." + COLUMN_LAT + " AS LAT, "
                    + "JB." + COLUMN_LNG + " AS LNG, "
                    + "(SELECT COUNT(*) C FROM " + TABLE_NAME + " JB3 WHERE JB3." + COLUMN_Postal + " = JB." + COLUMN_Postal + " AND JB3." + COLUMN_CatTypeID + " = 0) PIP, "
                    + "(SELECT COUNT(*) C FROM " + TABLE_NAME + " JB3 WHERE JB3." + COLUMN_Postal + " = JB." + COLUMN_Postal + " AND JB3." + COLUMN_CatTypeID + " = 2) DIP FROM "
                    + TABLE_NAME + " JB WHERE JB." + COLUMN_CatTypeID + " IN (0,2) ORDER BY JB." + COLUMN_Postal;

        } catch (Exception e) {
            sqlstr = "";
        } finally {
            return sqlstr;
        }
    }

    public static String getJobsCountForPIPnDIP_Only(Long JobsIDX) {
        String sqlstr = "";

        try {
            sqlstr = "SELECT COUNT(*) AS C FROM "
                    + TABLE_NAME + " JB WHERE JB." + COLUMN_CatTypeID + " IN (0,2) AND JB." + COLUMN_JobsIDX + " = " + JobsIDX + " ORDER BY JB." + COLUMN_Postal;
        } catch (Exception e) {
            sqlstr = "";
        } finally {
            return sqlstr;
        }
    }

    public static String getAllJobsIDXPerCategory(Integer CatTypeID) {
        String sqlstr = "";

        try {
            sqlstr = "SELECT JB." + COLUMN_JobsIDX + " " +
                    "FROM " + TABLE_NAME + " JB " +
                    "WHERE JB." + COLUMN_CatTypeID + " = '" + CatTypeID + "'";
        } catch (Exception e) {
            sqlstr = "";
            Date dt = new Date();
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getAllJobsIDXPerCategory. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getNFCtagByContactIDXsqlstr(String caidx, String cnidx) {
        String sqlstr = "";
        try {
            sqlstr = "SELECT DISTINCT IFNULL(NFC." + COLUMN_NFCTAGS_UID + ",'') AS " + COLUMN_NFCTAGS_UID + " " +
                    "FROM    " + TABLE_NAME_NFCTAGS + " NFC " +
                    "WHERE   ((NFC." + COLUMN_NFCTAGS_CAIDX2 + " = '" + caidx + "') OR (NFC." + COLUMN_NFCTAGS_CNIDX2 + " = '" + cnidx + "'))";

            /*sqlstr = "SELECT  IFNULL(NFC." + COLUMN_NFCTAGS_CAIDX1 + ",'0') AS " + COLUMN_NFCTAGS_CAIDX1 + ", " +
                    "IFNULL(NFC." + COLUMN_NFCTAGS_CNIDX1 + ",'0') AS " + COLUMN_NFCTAGS_CNIDX1 + ", " +
                    "IFNULL(NFC." + COLUMN_NFCTAGS_CAIDX2 + ",'0') AS " + COLUMN_NFCTAGS_CAIDX2 + ", " +
                    "IFNULL(NFC." + COLUMN_NFCTAGS_CNIDX2 + ",'0') AS " + COLUMN_NFCTAGS_CNIDX2 + ", " +
                    "IFNULL(NFC." + COLUMN_NFCTAGS_UID + ",'') AS " + COLUMN_NFCTAGS_UID + " " +
                    "FROM    " + TABLE_NAME_NFCTAGS + " NFC " +
                    "WHERE   ((NFC." + COLUMN_NFCTAGS_CAIDX2 + " = '" + caidx + "') OR (NFC." + COLUMN_NFCTAGS_CNIDX2 + " = '" + cnidx + "'))";*/
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getNFCtagByContactIDXsqlstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getSearchForLocationNFCTagsRecordViaNFC(String tagID) {
        String sqlstr = "";

        try {
            sqlstr = "SELECT  IFNULL(NFC." + COLUMN_NFCTAGS_CAIDX1 + ",'0') AS " + COLUMN_NFCTAGS_CAIDX1 + ", " +
                    "IFNULL(NFC." + COLUMN_NFCTAGS_CNIDX1 + ",'0') AS " + COLUMN_NFCTAGS_CNIDX1 + ", " +
                    "IFNULL(NFC." + COLUMN_NFCTAGS_CAIDX2 + ",'0') AS " + COLUMN_NFCTAGS_CAIDX2 + ", " +
                    "IFNULL(NFC." + COLUMN_NFCTAGS_CNIDX2 + ",'0') AS " + COLUMN_NFCTAGS_CNIDX2 + ", " +
                    "IFNULL(NFC." + COLUMN_NFCTAGS_UID + ",'') AS " + COLUMN_NFCTAGS_UID + " " +
                    "FROM    " + TABLE_NAME_NFCTAGS + " NFC " +
                    "WHERE   NFC." + COLUMN_NFCTAGS_UID + " = '" + tagID + "' AND (NFC." + COLUMN_NFCTAGS_CAIDX1
                    + " = NFC." + COLUMN_NFCTAGS_CAIDX2
                    + ")";
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getSearchForNFCTagsRecord. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getSearchForLocationNFCTagsRecordViaAddressIDX(String tagID) {
        String sqlstr = "";

        try {
            sqlstr = "SELECT  IFNULL(NFC." + COLUMN_NFCTAGS_CAIDX1 + ",'0') AS " + COLUMN_NFCTAGS_CAIDX1 + ", " +
                    "IFNULL(NFC." + COLUMN_NFCTAGS_CNIDX1 + ",'0') AS " + COLUMN_NFCTAGS_CNIDX1 + ", " +
                    "IFNULL(NFC." + COLUMN_NFCTAGS_CAIDX2 + ",'0') AS " + COLUMN_NFCTAGS_CAIDX2 + ", " +
                    "IFNULL(NFC." + COLUMN_NFCTAGS_CNIDX2 + ",'0') AS " + COLUMN_NFCTAGS_CNIDX2 + ", " +
                    "IFNULL(NFC." + COLUMN_NFCTAGS_UID + ",'') AS " + COLUMN_NFCTAGS_UID + " " +
                    "FROM    " + TABLE_NAME_NFCTAGS + " NFC " +
                    "WHERE   (NFC." + COLUMN_NFCTAGS_CAIDX1 + " = '" + tagID + "' OR NFC." + COLUMN_NFCTAGS_CAIDX2 + " = '" + tagID + "')" +
                    "AND   (NFC." + COLUMN_NFCTAGS_CAIDX1 + " = NFC." + COLUMN_NFCTAGS_CAIDX2 + ")";
        } catch (Exception e) {
            sqlstr = "";
            Date dt = new Date();
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getSearchForNFCTagsRecord. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getAllJobsPerLocationNFCTagPerCategoryWithOutBagTags(Long CAIDX1, Long CAIDX2, Integer CatTypeID) {
        String sqlstr = "";
        String whereStr = "";

        try {
            sqlstr = "SELECT  JB." + COLUMN_ID + ", JB." + COLUMN_JobsIDX + ", JB." + COLUMN_CatTypeID + ", JB." + COLUMN_Postal + ", JB." + COLUMN_AddressWithPostalNoUnit + ", JB." + COLUMN_AddressWithoutPostal + ", JB." +
                    COLUMN_LAT + ", JB." + COLUMN_LNG + ", JB." + COLUMN_ExpType + ", JB." + COLUMN_ExpTypeDesc + ", JB." + COLUMN_IsPartOne + ", JB." + COLUMN_PartDesc + ", JB." + COLUMN_IsDunning
                    + ", JB." + COLUMN_HasDOCOD + ", JB." + COLUMN_HasTradeIn + ", JB." + COLUMN_JobNo + ", JB." +
                    COLUMN_CNNo + ", JB." + COLUMN_CompanyName + ", JB." + COLUMN_ContactName + ", JB." + COLUMN_DO_INV_No + ", JB." + COLUMN_DO_INV_IDX + ", JB." + COLUMN_TAGS + ", JB." + COLUMN_FromDateTime + ", JB." + COLUMN_PickByDateTime + ", JB." +
                    COLUMN_ReadyDateTime + ", JB." + COLUMN_ToDateTime + ", JB." + COLUMN_ExtDateTime + ", JB." + COLUMN_ExtFromDateTime + ", JB." + COLUMN_FLAG + ", JB." + COLUMN_PCS + ", JB." + COLUMN_StatusCodeIDX + ", JB." + COLUMN_IsNewlyInserted +
                    ", IFNULL(JB." + COLUMN_FM_AddressIDX + ",0) AS " + COLUMN_FM_AddressIDX + ", IFNULL(JB." + COLUMN_FM_ContactIDX + ",0) AS " + COLUMN_FM_ContactIDX
                    + ", IFNULL(JB." + COLUMN_TO_AddressIDX + ",0) AS " + COLUMN_TO_AddressIDX + " , IFNULL(JB." + COLUMN_TO_ContactIDX + ",0) AS " + COLUMN_TO_ContactIDX +
                    ", IFNULL(JB." + COLUMN_JobInfoJsonStr + ",'') AS " + COLUMN_JobInfoJsonStr + ", " +
                    " IFNULL(JB." + COLUMN_CHECKLISTS + ",'') AS " + COLUMN_CHECKLISTS + ", " +
                    " IFNULL(JB." + COLUMN_NetsVisaTI + ",'0') AS " + COLUMN_NetsVisaTI + ", " +
                    //================================================================================================================================================
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) THEN JB." + COLUMN_ReadyDateTime + " " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN JB." + COLUMN_FromDateTime + " " +
                    "    END ) AS FromDateTimeConverted , " +
                    //================================================================================================================================================
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) THEN 'PU FM ' " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN 'DEL FM ' " +
                    "    END ) AS FromText , " +
                    //================================================================================================================================================
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) " +
                    "           THEN ( CASE WHEN JB." + COLUMN_PickByDateTime + " = JB." + COLUMN_ReadyDateTime + " THEN JB." + COLUMN_ToDateTime + " " +
                    "                       ELSE JB." + COLUMN_PickByDateTime + " " +
                    "                END ) " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN JB." + COLUMN_ToDateTime + " " +
                    "      END ) AS ToDateTimeConverted , " +
                    //================================================================================================================================================
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) " +
                    "           THEN ( CASE WHEN JB." + COLUMN_PickByDateTime + " = JB." + COLUMN_ReadyDateTime + " THEN 'DEL BY ' " +
                    "                       ELSE 'PU BY ' " +
                    "                 END ) " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN 'DEL BY ' " +
                    "    END ) AS ToText, " +
                    //================================================================================================================================================
                    "    0 isPickAndSendCount, 0 JobChangesCount, 0 HASLUNCHTIM " +
                    "FROM " + TABLE_NAME + " JB WHERE ";


            whereStr = "(JB." + COLUMN_CatTypeID + " = '" + CatTypeID + "') ";

            if (CatTypeID == 0) {
                whereStr += "AND (JB." + COLUMN_FM_AddressIDX + " = " + CAIDX1 + " OR JB." + COLUMN_FM_AddressIDX + " = " + CAIDX2 + ") ";
            } else if (CatTypeID == 2) {
                whereStr += "AND (JB." + COLUMN_TO_AddressIDX + " = " + CAIDX1 + " OR JB." + COLUMN_TO_AddressIDX + " = " + CAIDX2 + ") ";
            }

            if (CatTypeID == 0) {
                whereStr += " ORDER BY " + COLUMN_FromDateTime + " ";
            } else if (CatTypeID == 2) {
                whereStr += " ORDER BY " + COLUMN_ToDateTime + " ";
            }

            sqlstr = sqlstr + whereStr;

        } catch (Exception e) {
            sqlstr = "";
            Date dt = new Date();
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getAllJobsPerLocationNFCTagPerCategory. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getAllJobsPerLocationNFCTagPerCategory(Long CAIDX1, Long CAIDX2, Integer CatTypeID) {
        String sqlstr = "";
        String whereStr = "";

        try {
            sqlstr = "SELECT  JB." + COLUMN_ID + ", JB." + COLUMN_JobsIDX + ", JB." + COLUMN_CatTypeID + ", JB." + COLUMN_Postal + ", JB." + COLUMN_AddressWithPostalNoUnit + ", JB." + COLUMN_AddressWithoutPostal + ", JB." +
                    COLUMN_LAT + ", JB." + COLUMN_LNG + ", JB." + COLUMN_ExpType + ", JB." + COLUMN_ExpTypeDesc + ", JB." + COLUMN_IsPartOne + ", JB." + COLUMN_PartDesc + ", JB." + COLUMN_IsDunning
                    + ", JB." + COLUMN_HasDOCOD + ", JB." + COLUMN_HasTradeIn + ", JB." + COLUMN_JobNo + ", JB." +
                    COLUMN_CNNo + ", JB." + COLUMN_CompanyName + ", JB." + COLUMN_ContactName + ", JB." + COLUMN_DO_INV_No + ", JB." + COLUMN_DO_INV_IDX + ", JB." + COLUMN_TAGS + ", JB." + COLUMN_FromDateTime + ", JB." + COLUMN_PickByDateTime + ", JB." +
                    COLUMN_ReadyDateTime + ", JB." + COLUMN_ToDateTime + ", JB." + COLUMN_ExtDateTime + ", JB." + COLUMN_FLAG + ", JB." + COLUMN_PCS + ", JB." + COLUMN_StatusCodeIDX + ", JB." + COLUMN_IsNewlyInserted +
                    ", IFNULL(JB." + COLUMN_FM_AddressIDX + ",0) AS " + COLUMN_FM_AddressIDX + ", IFNULL(JB." + COLUMN_FM_ContactIDX + ",0) AS " + COLUMN_FM_ContactIDX
                    + ", IFNULL(JB." + COLUMN_TO_AddressIDX + ",0) AS " + COLUMN_TO_AddressIDX + " , IFNULL(JB." + COLUMN_TO_ContactIDX + ",0) AS " + COLUMN_TO_ContactIDX +
                    ", IFNULL(JB." + COLUMN_JobInfoJsonStr + ",'') AS " + COLUMN_JobInfoJsonStr + ", " +
                    " IFNULL(JB." + COLUMN_CHECKLISTS + ",'') AS " + COLUMN_CHECKLISTS + ", " +
                    " IFNULL(JB." + COLUMN_NetsVisaTI + ",'0') AS " + COLUMN_NetsVisaTI + ", " +
                    //================================================================================================================================================
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) THEN JB." + COLUMN_ReadyDateTime + " " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN JB." + COLUMN_FromDateTime + " " +
                    "    END ) AS FromDateTimeConverted , " +
                    //================================================================================================================================================
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) THEN 'PU FM ' " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN 'DEL FM ' " +
                    "    END ) AS FromText , " +
                    //================================================================================================================================================
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) " +
                    "           THEN ( CASE WHEN JB." + COLUMN_PickByDateTime + " = JB." + COLUMN_ReadyDateTime + " THEN JB." + COLUMN_ToDateTime + " " +
                    "                       ELSE JB." + COLUMN_PickByDateTime + " " +
                    "                END ) " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN JB." + COLUMN_ToDateTime + " " +
                    "      END ) AS ToDateTimeConverted , " +
                    //================================================================================================================================================
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) " +
                    "           THEN ( CASE WHEN JB." + COLUMN_PickByDateTime + " = JB." + COLUMN_ReadyDateTime + " THEN 'DEL BY ' " +
                    "                       ELSE 'PU BY ' " +
                    "                 END ) " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN 'DEL BY ' " +
                    "    END ) AS ToText " +
                    //================================================================================================================================================
                    "FROM " + TABLE_NAME + " JB WHERE ";


            whereStr = "(JB." + COLUMN_CatTypeID + " = '" + CatTypeID + "') ";

            whereStr += "AND (IFNULL(JB." + COLUMN_TAGS + ",'') <> '') ";

            if (CatTypeID == 0) {
                whereStr += "AND (JB." + COLUMN_FM_AddressIDX + " = " + CAIDX1 + " OR JB." + COLUMN_FM_AddressIDX + " = " + CAIDX2 + ") ";
            } else if (CatTypeID == 2) {
                whereStr += "AND (JB." + COLUMN_TO_AddressIDX + " = " + CAIDX1 + " OR JB." + COLUMN_TO_AddressIDX + " = " + CAIDX2 + ") ";
            }

            if (CatTypeID == 0) {
                whereStr += " ORDER BY " + COLUMN_FromDateTime + " ";
            } else if (CatTypeID == 2) {
                whereStr += " ORDER BY " + COLUMN_ToDateTime + " ";
            }

            sqlstr = sqlstr + whereStr;

        } catch (Exception e) {
            sqlstr = "";
            Date dt = new Date();
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getAllJobsPerLocationNFCTagPerCategory. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getAllJobsPerLocationIDXTagPerCategoryWithOutBagTags(Long CAIDX1, Integer CatTypeID) {
        String sqlstr = "";
        String whereStr = "";

        try {

            sqlstr = "SELECT  JB." + COLUMN_ID + ", JB." + COLUMN_JobsIDX + ", JB." + COLUMN_CatTypeID + ", JB." + COLUMN_Postal + ", JB." + COLUMN_AddressWithPostalNoUnit + ", JB." + COLUMN_AddressWithoutPostal + ", JB." +
                    COLUMN_LAT + ", JB." + COLUMN_LNG + ", JB." + COLUMN_ExpType + ", JB." + COLUMN_ExpTypeDesc + ", JB." + COLUMN_IsPartOne + ", JB." + COLUMN_PartDesc + ", JB." + COLUMN_IsDunning
                    + ", JB." + COLUMN_HasDOCOD + ", JB." + COLUMN_HasTradeIn + ", JB." + COLUMN_JobNo + ", JB." +
                    COLUMN_CNNo + ", JB." + COLUMN_CompanyName + ", JB." + COLUMN_ContactName + ", JB." + COLUMN_DO_INV_No + ", JB." + COLUMN_DO_INV_IDX + ", JB." + COLUMN_TAGS + ", JB." + COLUMN_FromDateTime + ", JB." + COLUMN_PickByDateTime + ", JB." +
                    COLUMN_ReadyDateTime + ", JB." + COLUMN_ToDateTime + ", JB." + COLUMN_ExtDateTime + ", JB." + COLUMN_ExtFromDateTime + ", JB." + COLUMN_FLAG + ", JB." + COLUMN_PCS + ", JB." + COLUMN_StatusCodeIDX + ", JB." + COLUMN_IsNewlyInserted +
                    ", IFNULL(JB." + COLUMN_FM_AddressIDX + ",0) AS " + COLUMN_FM_AddressIDX + ", IFNULL(JB." + COLUMN_FM_ContactIDX + ",0) AS " + COLUMN_FM_ContactIDX
                    + ", IFNULL(JB." + COLUMN_TO_AddressIDX + ",0) AS " + COLUMN_TO_AddressIDX + " , IFNULL(JB." + COLUMN_TO_ContactIDX + ",0) AS " + COLUMN_TO_ContactIDX +
                    ", IFNULL(JB." + COLUMN_JobInfoJsonStr + ",'') AS " + COLUMN_JobInfoJsonStr + ", " +
                    " IFNULL(JB." + COLUMN_CHECKLISTS + ",'') AS " + COLUMN_CHECKLISTS + ", " +
                    " IFNULL(JB." + COLUMN_NetsVisaTI + ",'0') AS " + COLUMN_NetsVisaTI + ", " +
                    //================================================================================================================================================
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) THEN JB." + COLUMN_ReadyDateTime + " " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN JB." + COLUMN_FromDateTime + " " +
                    "    END ) AS FromDateTimeConverted , " +
                    //================================================================================================================================================
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) THEN 'PU FM ' " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN 'DEL FM ' " +
                    "    END ) AS FromText , " +
                    //================================================================================================================================================
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) " +
                    "           THEN ( CASE WHEN JB." + COLUMN_PickByDateTime + " = JB." + COLUMN_ReadyDateTime + " THEN JB." + COLUMN_ToDateTime + " " +
                    "                       ELSE JB." + COLUMN_PickByDateTime + " " +
                    "                END ) " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN JB." + COLUMN_ToDateTime + " " +
                    "      END ) AS ToDateTimeConverted , " +
                    //================================================================================================================================================
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) " +
                    "           THEN ( CASE WHEN JB." + COLUMN_PickByDateTime + " = JB." + COLUMN_ReadyDateTime + " THEN 'DEL BY ' " +
                    "                       ELSE 'PU BY ' " +
                    "                 END ) " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN 'DEL BY ' " +
                    "    END ) AS ToText, " +
                    //================================================================================================================================================
                    "    0 isPickAndSendCount, 0 JobChangesCount, 0 HASLUNCHTIM " +
                    "FROM " + TABLE_NAME + " JB WHERE ";


            whereStr = "(JB." + COLUMN_CatTypeID + " = '" + CatTypeID + "') ";

            if (CatTypeID == 0) {
                whereStr += "AND (JB." + COLUMN_FM_AddressIDX + " = " + CAIDX1 + " OR JB." + COLUMN_FM_AddressIDX + " = " + CAIDX1 + ") ";
            } else if (CatTypeID == 2) {
                whereStr += "AND (JB." + COLUMN_TO_AddressIDX + " = " + CAIDX1 + " OR JB." + COLUMN_TO_AddressIDX + " = " + CAIDX1 + ") ";
            }

            if (CatTypeID == 0) {
                whereStr += " ORDER BY " + COLUMN_FromDateTime + " ";
            } else if (CatTypeID == 2) {
                whereStr += " ORDER BY " + COLUMN_ToDateTime + " ";
            }

            sqlstr = sqlstr + whereStr;
            //sqlstr = DATABASE_GETDBOBJJOBSQUERY() + whereStr;
        } catch (Exception e) {
            sqlstr = "";
            Date dt = new Date();
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getAllJobsPerLocationIDXTagPerCategoryWithOutBagTags. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getAllJobsPerLocationIDXTagPerCategory(Long CAIDX1, Integer CatTypeID) {
        String sqlstr = "";
        String whereStr = "";

        try {

            sqlstr = "SELECT  JB." + COLUMN_ID + ", JB." + COLUMN_JobsIDX + ", JB." + COLUMN_CatTypeID + ", JB." + COLUMN_Postal + ", JB." + COLUMN_AddressWithPostalNoUnit + ", JB." + COLUMN_AddressWithoutPostal + ", JB." +
                    COLUMN_LAT + ", JB." + COLUMN_LNG + ", JB." + COLUMN_ExpType + ", JB." + COLUMN_ExpTypeDesc + ", JB." + COLUMN_IsPartOne + ", JB." + COLUMN_PartDesc + ", JB." + COLUMN_IsDunning
                    + ", JB." + COLUMN_HasDOCOD + ", JB." + COLUMN_HasTradeIn + ", JB." + COLUMN_JobNo + ", JB." +
                    COLUMN_CNNo + ", JB." + COLUMN_CompanyName + ", JB." + COLUMN_ContactName + ", JB." + COLUMN_DO_INV_No + ", JB." + COLUMN_DO_INV_IDX + ", JB." + COLUMN_TAGS + ", JB." + COLUMN_FromDateTime + ", JB." + COLUMN_PickByDateTime + ", JB." +
                    COLUMN_ReadyDateTime + ", JB." + COLUMN_ToDateTime + ", JB." + COLUMN_ExtDateTime + ", JB." + COLUMN_FLAG + ", JB." + COLUMN_PCS + ", JB." + COLUMN_StatusCodeIDX + ", JB." + COLUMN_IsNewlyInserted +
                    ", IFNULL(JB." + COLUMN_FM_AddressIDX + ",0) AS " + COLUMN_FM_AddressIDX + ", IFNULL(JB." + COLUMN_FM_ContactIDX + ",0) AS " + COLUMN_FM_ContactIDX
                    + ", IFNULL(JB." + COLUMN_TO_AddressIDX + ",0) AS " + COLUMN_TO_AddressIDX + " , IFNULL(JB." + COLUMN_TO_ContactIDX + ",0) AS " + COLUMN_TO_ContactIDX +
                    ", IFNULL(JB." + COLUMN_JobInfoJsonStr + ",'') AS " + COLUMN_JobInfoJsonStr + ", " +
                    " IFNULL(JB." + COLUMN_CHECKLISTS + ",'') AS " + COLUMN_CHECKLISTS + ", " +
                    " IFNULL(JB." + COLUMN_NetsVisaTI + ",'0') AS " + COLUMN_NetsVisaTI + ", " +
                    //================================================================================================================================================
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) THEN JB." + COLUMN_ReadyDateTime + " " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN JB." + COLUMN_FromDateTime + " " +
                    "    END ) AS FromDateTimeConverted , " +
                    //================================================================================================================================================
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) THEN 'PU FM ' " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN 'DEL FM ' " +
                    "    END ) AS FromText , " +
                    //================================================================================================================================================
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) " +
                    "           THEN ( CASE WHEN JB." + COLUMN_PickByDateTime + " = JB." + COLUMN_ReadyDateTime + " THEN JB." + COLUMN_ToDateTime + " " +
                    "                       ELSE JB." + COLUMN_PickByDateTime + " " +
                    "                END ) " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN JB." + COLUMN_ToDateTime + " " +
                    "      END ) AS ToDateTimeConverted , " +
                    //================================================================================================================================================
                    "    ( CASE WHEN JB." + COLUMN_CatTypeID + " IN (0,1,4) " +
                    "           THEN ( CASE WHEN JB." + COLUMN_PickByDateTime + " = JB." + COLUMN_ReadyDateTime + " THEN 'DEL BY ' " +
                    "                       ELSE 'PU BY ' " +
                    "                 END ) " +
                    "           WHEN JB." + COLUMN_CatTypeID + " IN (2,3,5,6,7) THEN 'DEL BY ' " +
                    "    END ) AS ToText " +
                    //================================================================================================================================================
                    "FROM " + TABLE_NAME + " JB WHERE ";


            whereStr = "(JB." + COLUMN_CatTypeID + " = '" + CatTypeID + "') ";

            whereStr += "AND (IFNULL(JB." + COLUMN_TAGS + ",'') <> '') ";

            if (CatTypeID == 0) {
                whereStr += "AND (JB." + COLUMN_FM_AddressIDX + " = " + CAIDX1 + " OR JB." + COLUMN_FM_AddressIDX + " = " + CAIDX1 + ") ";
            } else if (CatTypeID == 2) {
                whereStr += "AND (JB." + COLUMN_TO_AddressIDX + " = " + CAIDX1 + " OR JB." + COLUMN_TO_AddressIDX + " = " + CAIDX1 + ") ";
            }

            if (CatTypeID == 0) {
                whereStr += " ORDER BY " + COLUMN_FromDateTime + " ";
            } else if (CatTypeID == 2) {
                whereStr += " ORDER BY " + COLUMN_ToDateTime + " ";
            }

            sqlstr = sqlstr + whereStr;
            //sqlstr = DATABASE_GETDBOBJJOBSQUERY() + whereStr;
        } catch (Exception e) {
            sqlstr = "";
            Date dt = new Date();
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getAllJobsPerLocationNFCTagPerCategory. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getWholeCabinetSQLstr() {
        String sqlstr = "";
        String whereStr = "";

        try {
            sqlstr = "SELECT * FROM " + TABLE_NAME_CABINET_UNIVERSAL_TBL;
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getWholeCabinetSQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getAllPIPPUDIPOKPJobsIDXSQLstr() {
        String sqlstr = "";
        String whereStr = "";

        try {
            sqlstr = "SELECT J." + COLUMN_JobsIDX + " FROM " + TABLE_NAME + " J WHERE J." + COLUMN_CatTypeID + " IN (0,1,2,3)";
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getAllPIPPUDIPOKPJobsIDXSQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getAllPIPPUDIPOKPFPFDPDJobsIDXSQLstr() {
        String sqlstr = "";
        String whereStr = "";

        try {
            //sqlstr = "SELECT J." + COLUMN_JobsIDX + " FROM " + TABLE_NAME + " J WHERE J." + COLUMN_CatTypeID + " IN (0,1,2,3,4,5)";
            sqlstr = "SELECT J." + COLUMN_JobsIDX + " FROM " + TABLE_NAME + " J WHERE J." + COLUMN_CatTypeID + " IN (0,1,2,3,4,5,6)";
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getAllPIPPUDIPOKPFPFDPDJobsIDXSQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getAllPIPDIPJobsIDXSQLstr() {
        String sqlstr = "";
        String whereStr = "";

        try {
            sqlstr = "SELECT J." + COLUMN_JobsIDX + " FROM " + TABLE_NAME + " J WHERE J." + COLUMN_CatTypeID + " IN (0,2)";
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getAllPIPDIPJobsIDXSQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }



    //=================================================Call Log=================================================

    public static String getInsertCallLogSQLstr() {
        String sqlstr = "";
        try {
            sqlstr = "INSERT INTO " + TABLE_NAME_CALLHISTORY_TBL +
                    " (" +
                    COLUMN_TelNum + "," +
                    COLUMN_CallCount + "," +
                    COLUMN_CatTypeID + "," +
                    COLUMN_LastCallTimeStamp + "," +
                    COLUMN_ReqCallLogImg + "," +
                    COLUMN_CallConnected +
                    ") VALUES (?,?,?,?,?,?);";
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getInsertCallLogSQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getUpdateCallLogSQLstr(String TelNum, Long LastCallTimeStamp, Integer catTypeID) {
        String sqlstr = "";
        try {
            sqlstr = "UPDATE " + TABLE_NAME_CALLHISTORY_TBL + " SET " + COLUMN_CallCount + " = " + COLUMN_CallCount + " + 1, " + COLUMN_LastCallTimeStamp + " = " + LastCallTimeStamp
                    + " WHERE " + COLUMN_TelNum + " = " + TelNum + " AND " + COLUMN_CatTypeID + " = " + catTypeID;
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getUpdateCallLogSQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getCountTelNumCalledOutSQLstr(String TelNum) {
        String sqlstr = "";
        try {
            sqlstr = "SELECT COUNT(*) AS C FROM "
                    + TABLE_NAME_CALLHISTORY_TBL + " WHERE " + COLUMN_TelNum + " IN (" + TelNum + ")";
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getCountTelNumCalledOutSQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getNumOfCallOutPast7minSQLstr(String TelNum){
        String sqlstr = "";
        long currTimeLong = new Date().getTime();
        try {
            sqlstr = "SELECT " + COLUMN_ReqCallLogImg + ", " + COLUMN_CallConnected + ", COUNT(*) AS C FROM " + TABLE_NAME_CALLHISTORY_TBL
                    + " WHERE " + COLUMN_TelNum + " IN (" + TelNum + ") AND "
                    + String.valueOf(currTimeLong) + " - " + COLUMN_LastCallTimeStamp + " <= 420000 GROUP BY " + COLUMN_ReqCallLogImg
                    + " ORDER BY " + COLUMN_LastCallTimeStamp;
            //900000 in milliseconds == 15 min
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getNumOfCallOutPast7minSQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getNumOfCallOutPast15minSQLstr(String TelNum) {
        String sqlstr = "";
        long currTimeLong = new Date().getTime();
        try {
            sqlstr = "SELECT " + COLUMN_ReqCallLogImg + ", " + COLUMN_CallConnected + ", COUNT(*) AS C FROM " + TABLE_NAME_CALLHISTORY_TBL
                    + " WHERE " + COLUMN_TelNum + " IN (" + TelNum + ") AND "
                    + String.valueOf(currTimeLong) + " - " + COLUMN_LastCallTimeStamp + " <= 900000 GROUP BY " + COLUMN_ReqCallLogImg
                    + " ORDER BY " + COLUMN_LastCallTimeStamp;
            //900000 in milliseconds == 15 min
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getNumOfCallOutPast15minSQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getNumOfCallOutPast30minSQLstr(String TelNum) {
        String sqlstr = "";
        long currTimeLong = new Date().getTime();
        try {
            sqlstr = "SELECT " + COLUMN_ReqCallLogImg + ", " + COLUMN_CallConnected + ", COUNT(*) AS C FROM " + TABLE_NAME_CALLHISTORY_TBL
                    + " WHERE " + COLUMN_TelNum + " IN (" + TelNum + ") AND "
                    + String.valueOf(currTimeLong) + " - " + COLUMN_LastCallTimeStamp + " <= 1800000 GROUP BY " + COLUMN_ReqCallLogImg
                    + " ORDER BY " + COLUMN_LastCallTimeStamp;
            //900000 in milliseconds == 15 min
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getNumOfCallOutPast30minSQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getNumOfCallOutPast60minSQLstr(String TelNum) {
        String sqlstr = "";
        long currTimeLong = new Date().getTime();
        try {
            sqlstr = "SELECT " + COLUMN_ReqCallLogImg + ", " + COLUMN_CallConnected + ", COUNT(*) AS C FROM " + TABLE_NAME_CALLHISTORY_TBL
                    + " WHERE " + COLUMN_TelNum + " IN (" + TelNum + ") AND "
                    + String.valueOf(currTimeLong) + " - " + COLUMN_LastCallTimeStamp + " <= 3600000 GROUP BY " + COLUMN_ReqCallLogImg
                    + " ORDER BY " + COLUMN_LastCallTimeStamp;
            //900000 in milliseconds == 15 min
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getNumOfCallOutPast60minSQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getPIPDIP_NFC_JobsCountSQlStr() {
        String sqlstr = "";
        try {
            sqlstr = "SELECT COUNT(*) CT FROM " + TABLE_NAME + " WHERE " + COLUMN_CatTypeID + " IN (0,2) AND ((FLAG & 262144) = 262144)";
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getPIPDIP_NFC_JobsCountSQlStr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getFlag2SumArr() {
        String sqlstr = "";
        try {
            sqlstr = "SELECT DISTINCT IFNULL(" + COLUMN_FLAG2 + ", 0) F2 FROM " + TABLE_NAME + " WHERE " + COLUMN_CatTypeID + " = 2 ORDER BY " + COLUMN_FLAG2;
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getFlag2SumArr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getFlagSumArr() {
        String sqlstr = "";
        try {
            //sqlstr = "SELECT SUM(DISTINCT " + COLUMN_NetsVisaTI + ") AS FlagSum FROM " + TABLE_NAME + " WHERE " + COLUMN_CatTypeID + " = 2";
            sqlstr = "SELECT DISTINCT " + COLUMN_NetsVisaTI + " FROM " + TABLE_NAME + " WHERE " + COLUMN_NetsVisaTI + " > 0 AND " + COLUMN_CatTypeID + " = 2";
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getFlagSumArr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getFlagSum() {
        String sqlstr = "";
        try {
            sqlstr = "SELECT SUM(DISTINCT " + COLUMN_NetsVisaTI + ") AS FlagSum FROM " + TABLE_NAME + " WHERE " + COLUMN_CatTypeID + " = 2";
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getFlagSum. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getDistinctPostalFlagSumSqlStr() {
        String sqlstr = "";
        try {
            /*sqlstr = "SELECT DISTINCT J1." + COLUMN_Postal + ", (SELECT SUM(DISTINCT J2." + COLUMN_NetsVisaTI + ") " +
                    "FROM " + TABLE_NAME + " J2 WHERE J2." + COLUMN_CatTypeID + " = 2 and J2." + COLUMN_Postal + " = J1." + COLUMN_Postal + ") AS FlagSum " +
                    "FROM " + TABLE_NAME + " J1 WHERE J1." + COLUMN_NetsVisaTI + " > 0 and J1." + COLUMN_CatTypeID + " = 2 ORDER BY J1." + COLUMN_Postal;*/
            sqlstr = "SELECT DISTINCT J1." + COLUMN_Postal + ", IFNULL(J1." + COLUMN_NetsVisaTI + ",'0') AS FlagSum FROM " + TABLE_NAME +
                    " J1 WHERE J1." + COLUMN_NetsVisaTI + " > 0 and J1." + COLUMN_CatTypeID + " = 2 ORDER BY J1." + COLUMN_Postal;
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getDistinctPostalFlagSumSqlStr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getDistinctPostalFlag2SumSqlStr() {
        String sqlstr = "";
        try {
            sqlstr = "SELECT DISTINCT J1." + COLUMN_Postal + ", IFNULL(J1." + COLUMN_FLAG2 + ",'0') AS FlagSum FROM " + TABLE_NAME +
                    " J1 WHERE J1." + COLUMN_FLAG2 + " > 0 and J1." + COLUMN_CatTypeID + " IN (0,2) ORDER BY J1." + COLUMN_Postal;
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getDistinctPostalFlag2SumSqlStr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getDOListViaJobsIDXsSqlStr(ArrayList<Long> JobsIDXsAL) {
        String sqlstr = "";
        try {
            String jidxs = "";
            if (JobsIDXsAL != null && JobsIDXsAL.size() > 0) {
                for (int i = 0; i <= JobsIDXsAL.size() - 1; i++) {
                    if (i == 0) {
                        jidxs += String.valueOf(JobsIDXsAL.get(i));
                    } else {
                        jidxs += ", " + String.valueOf(JobsIDXsAL.get(i));
                    }
                }
            } else {
                jidxs = "0";
            }

            sqlstr = "SELECT " + COLUMN_JobsIDX + ", IFNULL(" + COLUMN_DO_INV_No + ",'') " + COLUMN_DO_INV_No + " FROM " + TABLE_NAME +
                    " WHERE " + COLUMN_JobsIDX + " IN (" + jidxs + ") AND IFNULL(" + COLUMN_DO_INV_No + ",'') <> ''";
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getDOListViaJobsIDXsSqlStr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getJobCountViaIDXsSqlStr(long[] JobsIDXs) {
        String sqlstr = "";
        try {
            String jidxs = "";
            if (JobsIDXs != null && JobsIDXs.length > 0) {
                for (int i = 0; i <= JobsIDXs.length - 1; i++) {
                    if (i == 0) {
                        jidxs += String.valueOf(JobsIDXs[i]);
                    } else {
                        jidxs += ", " + String.valueOf(JobsIDXs[i]);
                    }
                }
            } else {
                jidxs = "0";
            }

            sqlstr = "SELECT COUNT(*) C FROM " + TABLE_NAME +
                    " WHERE " + COLUMN_JobsIDX + " IN (" + jidxs + ") AND " + COLUMN_CatTypeID + " IN (2,6,7)";
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getJobCountViaIDXsSqlStr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getDOListViaJobsIDXsSqlStr2(String JobsIDXsAL) {
        String sqlstr = "";
        try {
            String jidxs = "0";
            if (!JobsIDXsAL.isEmpty() && !JobsIDXsAL.equals("") && JobsIDXsAL != "" && !JobsIDXsAL.equals("[]") && JobsIDXsAL != "[]") {
                jidxs = JobsIDXsAL;
            }
            sqlstr = "SELECT " + COLUMN_JobsIDX + ", IFNULL(" + COLUMN_DO_INV_No + ",'') " + COLUMN_DO_INV_No + " FROM " + TABLE_NAME +
                    " WHERE " + COLUMN_JobsIDX + " IN (" + jidxs + ") AND IFNULL(" + COLUMN_DO_INV_No + ",'') <> ''";
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getDOListViaJobsIDXsSqlStr2. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getTotalOutstandingNumOfPipDipSqlStr() {
        String sqlstr = "";
        try {
            sqlstr = "SELECT COUNT(*) CT FROM " + TABLE_NAME + " WHERE " + COLUMN_CatTypeID + " IN (0,2)";
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getTotalOutstandingNumOfPipDipSqlStr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getTotalNumPipDipParkedJobsSqlStr() {
        String sqlstr = "";
        try {
            sqlstr = "SELECT COUNT(*) CT FROM " + TABLE_NAME + " WHERE " + COLUMN_StatusCodeIDX + " IN (103,104,155)";
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getTotalNumPipDipParkedJobsSqlStr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getTotalLocationsInPipDipParkedSqlStr() {
        String sqlstr = "";
        try {
            sqlstr = "SELECT COUNT(distinct postal) CT FROM JOBS WHERE CATTYPEID IN (0,2,6)";
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getTotalLocationsInPipDipParkedSqlStr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getAllLocationJobsInPipDipParkedSqlStr() {
        String sqlstr = "";
        try {
            sqlstr = "SELECT DISTINCT J.POSTAL, " +
                    "(SELECT JJ.AddressWithPostalNoUnit FROM JOBS JJ WHERE JJ.Postal = J.Postal LIMIT 1) AddressWithPostalNoUnit, " +
                    "(SELECT COUNT(JJJ.JOBSIDX) C FROM JOBS JJJ WHERE JJJ.Postal = J.Postal AND JJJ.CatTypeID = 0) PIP, " +
                    "(SELECT COUNT(JJJ.JOBSIDX) C FROM JOBS JJJ WHERE JJJ.Postal = J.Postal AND JJJ.CatTypeID IN (2,6)) DIP, " +
                    "IFNULL( " +
                    "( " +
                    "SELECT JJJJ.ReadyDateTime FROM JOBS JJJJ WHERE JJJJ.Postal = J.Postal AND JJJJ.CatTypeID = 0 ORDER BY JJJJ.ReadyDateTime LIMIT 1 " +
                    "), 0) RDYDT, " +
                    "IFNULL( " +
                    "( " +
                    "SELECT JJJJ.PickByDateTime FROM JOBS JJJJ WHERE JJJJ.Postal = J.Postal AND JJJJ.CatTypeID = 0 AND JJJJ.ReadyDateTime <> JJJJ.PickByDateTime " +
                    "AND DATE(datetime(PickByDateTime, 'unixepoch','localtime')) = DATE('now') AND datetime(PickByDateTime, 'unixepoch','localtime') > datetime('now', 'localtime') " +
                    "ORDER BY JJJJ.PickByDateTime LIMIT 1 " +
                    "), 0) PUBYDT, " +
                    "IFNULL( " +
                    "( " +
                    "SELECT JJJJ.FromDateTime FROM JOBS JJJJ WHERE JJJJ.Postal = J.Postal AND JJJJ.CatTypeID IN (0,2,6) ORDER BY JJJJ.FromDateTime LIMIT 1 " +
                    "), 0) FDT, " +
                    "IFNULL( " +
                    "( " +
                    "SELECT JJJJ.ToDateTime FROM JOBS JJJJ WHERE JJJJ.Postal = J.Postal AND JJJJ.CatTypeID IN (0,2,6) ORDER BY JJJJ.ToDateTime LIMIT 1 " +
                    "), 0) TDT, " +
                    "IFNULL( " +
                    "( " +
                    "SELECT JJJJ.ExtFromDateTime FROM JOBS JJJJ WHERE JJJJ.Postal = J.Postal AND JJJJ.CatTypeID IN (2,6) AND JJJJ.ExtFromDateTime > 0 ORDER BY JJJJ.ExtFromDateTime LIMIT 1 " +
                    "), 0) EFDT, " +
                    "IFNULL( " +
                    "( " +
                    "SELECT JJJJ.ExtDateTime FROM JOBS JJJJ WHERE JJJJ.Postal = J.Postal AND JJJJ.CatTypeID IN (2,6) AND JJJJ.ExtDateTime > 0 ORDER BY JJJJ.ExtDateTime LIMIT 1 " +
                    "), 0) ETDT, " +
                    "( " +
                    "SELECT group_concat(JJJJ.JOBSIDX, ',') FROM JOBS JJJJ WHERE JJJJ.CatTypeID IN (0,2,6) AND JJJJ.Postal = J.Postal " +
                    ") JOBSIDXS, " +
                    "( " +
                    "SELECT COUNT(*) C FROM JOBS JJJJ WHERE JJJJ.CatTypeID IN (0,2,6) AND JJJJ.Postal = J.Postal AND (JJJJ.FLAG2 & 64) = 64 " +
                    ") ColdCount, " +
                    "( " +
                    "SELECT COUNT(*)C FROM JOBS JJJJ WHERE JJJJ.CatTypeID IN (0,2,6) AND JJJJ.Postal = J.Postal AND (JJJJ.NetsVisaTi & 4) = 4 " +
                    ") ReqNetsCount, " +
                    "( " +
                    "SELECT COUNT(*)C FROM JOBS JJJJ WHERE JJJJ.CatTypeID IN (0,2,6) AND JJJJ.Postal = J.Postal AND (JJJJ.NetsVisaTi & 8) = 8 " +
                    ") ReqVisaCount, " +
                    "( " +
                    "SELECT COUNT(*)C FROM JOBS JJJJ WHERE JJJJ.CatTypeID IN (0,2,6) AND JJJJ.Postal = J.Postal AND (JJJJ.NetsVisaTi & 16) = 16 " +
                    ") ReqTiCount " +
                    "FROM JOBS J " +
                    "WHERE " +
                    //"J.CATTYPEID IN (0,2,6) " +
                    "J.CATTYPEID IN (0,2) " +
                    "ORDER BY TDT, ETDT";
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getAllLocationJobsInPipDipParkedSqlStr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getAllPostalsInPipDipSqlStr() {
        String sql = "";
        try {
            sql = "SELECT DISTINCT J.Postal POSTAL " +
                    "FROM JOBS J WHERE " +
                    //"J.CatTypeID IN (0,2,6) ORDER BY POSTAL";
                    "J.CatTypeID IN (0,2) ORDER BY POSTAL";
        } catch (Exception e) {
            sql = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getAllPostalsInPipDipSqlStr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sql;
        }
    }

    public static String getAllPostalsInDipSqlStr() {
        String sql = "";
        try {
            sql = "SELECT DISTINCT J.Postal POSTAL " +
                    "FROM JOBS J WHERE " +
                    "J.CatTypeID IN (2) ORDER BY POSTAL";
        } catch (Exception e) {
            sql = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getAllPostalsInDipSqlStr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sql;
        }
    }

    public static String getAllPostalsInPipDipByAMOnlySlotSqlStr() {
        String sql = "";
        try {
            /*sql = "SELECT DISTINCT J.Postal POSTAL " +
                    "FROM JOBS J WHERE " +
                    "J.CatTypeID IN (0,2) AND ((J.SlotFlag & 1) = 1) AND ((J.SlotFlag & 2) != 2) AND ((J.SlotFlag & 4) != 4) ORDER BY POSTAL";*/

            ////following code same as above, 1 call instead of 3 calls
           sql =  "SELECT DISTINCT J.Postal POSTAL " +
                   "FROM JOBS J WHERE " +
                   "J.CatTypeID IN (2) AND ((J.SlotFlag & 7) = 1) ORDER BY POSTAL";
        } catch (Exception e) {
            sql = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getAllPostalsInPipDipByAMOnlySlotSqlStr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sql;
        }
    }

    public static String getAllJobsIDXsInPipDipByAMOnlySlotSqlStr() {
        String sql = "";
        try {
            /*sql = "SELECT DISTINCT J.Postal POSTAL " +
                    "FROM JOBS J WHERE " +
                    "J.CatTypeID IN (0,2) AND ((J.SlotFlag & 1) = 1) AND ((J.SlotFlag & 2) != 2) AND ((J.SlotFlag & 4) != 4) ORDER BY POSTAL";*/

            ////following code same as above, 1 call instead of 3 calls
            sql =  "SELECT J.JobsIDX JobsIDX " +
                    "FROM JOBS J WHERE " +
                    "J.CatTypeID IN (2) AND ((J.SlotFlag & 7) = 1)";
        } catch (Exception e) {
            sql = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getAllJobsIDXsInPipDipByAMOnlySlotSqlStr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sql;
        }
    }

    public static String getAllPostalsInPipDipByAMSlotSqlStr() {
        String sql = "";
        try {
            sql = "SELECT DISTINCT J.Postal POSTAL " +
                    "FROM JOBS J WHERE " +
                    "J.CatTypeID IN (2) AND ((J.SlotFlag & 1) = 1) AND ((J.SlotFlag & 4) != 4) ORDER BY POSTAL";
        } catch (Exception e) {
            sql = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getAllPostalsInPipDipByAMSlotSqlStr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sql;
        }
    }

    public static String getAllPostalsInPipDipByPMOnlySlotSqlStr() {
        String sql = "";
        try {
            sql = "SELECT DISTINCT J.Postal POSTAL " +
                    "FROM JOBS J WHERE " +
                    "J.CatTypeID IN (0,2) AND ((J.SlotFlag & 2) = 2) AND ((J.SlotFlag & 4) != 4) ORDER BY POSTAL";
        } catch (Exception e) {
            sql = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getAllPostalsInPipDipByPMOnlySlotSqlStr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sql;
        }
    }

    public static String getAllPostalsInPipDipByPMSlotSqlStr() {
        String sql = "";
        try {
            /*sql = "SELECT DISTINCT J.Postal POSTAL " +
                    "FROM JOBS J WHERE " +
                    "J.CatTypeID IN (0,2) AND ((J.SlotFlag & 2) = 2) AND ((J.SlotFlag & 4) != 4) ORDER BY POSTAL";*/

            sql = "SELECT DISTINCT J.Postal POSTAL " +
                    "FROM JOBS J WHERE " +
                    "J.CatTypeID IN (2) AND ((J.SlotFlag & 6) = 2) ORDER BY POSTAL";
        } catch (Exception e) {
            sql = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getAllPostalsInPipDipByPMSlotSqlStr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sql;
        }
    }

    public static String getAllJobsIDXsInPipDipByPMSlotSqlStr() {
        String sql = "";
        try {
            /*sql = "SELECT DISTINCT J.Postal POSTAL " +
                    "FROM JOBS J WHERE " +
                    "J.CatTypeID IN (0,2) AND ((J.SlotFlag & 1) = 1) AND ((J.SlotFlag & 2) != 2) AND ((J.SlotFlag & 4) != 4) ORDER BY POSTAL";*/

            ////following code same as above, 1 call instead of 3 calls
            sql =  "SELECT J.JobsIDX JobsIDX " +
                    "FROM JOBS J WHERE " +
                    "J.CatTypeID IN (2) AND ((J.SlotFlag & 6) = 2)";
        } catch (Exception e) {
            sql = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getAllJobsIDXsInPipDipByPMSlotSqlStr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sql;
        }
    }

    public static String getAllPostalsInPipDipByAOHSlotSqlStr() {
        String sql = "";
        try {
            /*sql = "SELECT DISTINCT J.Postal POSTAL " +
                    "FROM JOBS J WHERE " +
                    "J.CatTypeID IN (0,2) AND ((J.SlotFlag & 4) = 4) ORDER BY POSTAL";*/

            sql = "SELECT DISTINCT J.Postal POSTAL " +
                    "FROM JOBS J WHERE " +
                    "J.CatTypeID IN (2) AND ((J.SlotFlag & 4) = 4) ORDER BY POSTAL";
        } catch (Exception e) {
            sql = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getAllPostalsInPipDipByAOHSlotSqlStr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sql;
        }
    }

    public static String getAllJobsIDXsInPipDipByAOHSlotSqlStr() {
        String sql = "";
        try {
            /*sql = "SELECT DISTINCT J.Postal POSTAL " +
                    "FROM JOBS J WHERE " +
                    "J.CatTypeID IN (0,2) AND ((J.SlotFlag & 1) = 1) AND ((J.SlotFlag & 2) != 2) AND ((J.SlotFlag & 4) != 4) ORDER BY POSTAL";*/

            ////following code same as above, 1 call instead of 3 calls
            sql =  "SELECT J.JobsIDX JobsIDX " +
                    "FROM JOBS J WHERE " +
                    "J.CatTypeID IN (2) AND ((J.SlotFlag & 4) = 4)";
        } catch (Exception e) {
            sql = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getAllJobsIDXsInPipDipByAOHSlotSqlStr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sql;
        }
    }


    public static String getAllPostalsInPipDipBySlotSqlStr(Integer SlotFlag) {
        String sql = "";
        try {
            if (SlotFlag == 2) {
                sql = "SELECT DISTINCT J.Postal POSTAL " +
                        "FROM JOBS J WHERE " +
                        "J.CatTypeID IN (0,2) AND ((J.SlotFlag & " + SlotFlag + ") = " + SlotFlag + ") AND ((J.SlotFlag & 4) != 4) ORDER BY POSTAL";
            } else {
                sql = "SELECT DISTINCT J.Postal POSTAL " +
                        "FROM JOBS J WHERE " +
                        "J.CatTypeID IN (0,2) AND ((J.SlotFlag & " + SlotFlag + ") = " + SlotFlag + ") ORDER BY POSTAL";
            }
        } catch (Exception e) {
            sql = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getAllPostalsInPipDipBySlotSqlStr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sql;
        }
    }

    public static String getAllPostalsInParkedSqlStr() {
        String sql = "";
        try {
            sql = "SELECT DISTINCT J.Postal POSTAL " +
                    "FROM JOBS J WHERE " +
                    //"J.CatTypeID IN (0,2,6) ORDER BY POSTAL";
                    "J.CatTypeID IN (6) ORDER BY POSTAL";
        } catch (Exception e) {
            sql = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getAllPostalsInParkedSqlStr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sql;
        }
    }

    public static String getAllPostalsInPUOKPFAILSqlStr() {
        String sql = "";
        try {
            /*sql = "SELECT DISTINCT J.Postal POSTAL " +
                    "FROM JOBS J WHERE " +
                    "J.CatTypeID IN (1,3,4,5) ORDER BY POSTAL";*/
            sql = "SELECT DISTINCT J.Postal POSTAL " +
                    "FROM JOBS J WHERE " +
                    "J.CatTypeID IN (3,5) ORDER BY POSTAL";
        } catch (Exception e) {
            sql = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getAllPostalsInPUOKPFAILSqlStr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sql;
        }
    }

    public static String getParkedValuesForCreditsSystemSqlStr(String postal) {
        String sql = "";
        try {
            sql = "SELECT DISTINCT J.Postal, J.LAT, J.LNG, J.AddressWithPostalNoUnit, " +
                    "( " +
                    "SELECT COUNT(J2.JobsIDX) C " +
                    "FROM JOBS J2 " +
                    "WHERE J2.CatTypeID = 6 AND J2.Postal = '" + postal + "' " +
                    ") JCOUNT, " +
                    "( " +
                    "SELECT " +
                    "    GROUP_CONCAT(J3.JobsIDX, ',') " +
                    "FROM JOBS J3 " +
                    "WHERE J3.CatTypeID = 6 AND J3.Postal = '" + postal + "' " +
                    ") JobsIDXs " +
                    "FROM JOBS J " +
                    "WHERE J.CatTypeID = 6 AND J.Postal = '" + postal + "'";
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at sqlStringJObs getParkedValuesForCreditsSystemSqlStr. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return sql;
        }
    }

    public static String getJobWithinTimeWindow(Long JobsIDX, long CustomETA_long) {
        String sqlstr = "";
        long currTimeLong = new Date().getTime();
        try {
            sqlstr = "SELECT (" +
                    "CASE " +
                    "WHEN " + COLUMN_CatTypeID + " = 2 AND (" + COLUMN_FromDateTime + " - " + String.valueOf(CustomETA_long) + " < 1800) THEN 'TRUE' " +
                    "WHEN " + COLUMN_CatTypeID + " = 0 AND (" + COLUMN_ReadyDateTime + " - " + String.valueOf(CustomETA_long) + " < 1800) THEN 'TRUE' " +
                    "ELSE 'FALSE' " +
                    "END) AS TRUEFALSE, (" + COLUMN_FromDateTime + " - " + String.valueOf(CustomETA_long) + ") AS C1, (" + COLUMN_ReadyDateTime + " - " + String.valueOf(CustomETA_long) + ") AS C2 " +
                    "FROM " + TABLE_NAME + " WHERE " + COLUMN_JobsIDX + " = '" + JobsIDX + "'";
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getJobWithinTimeWindow. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getCheckIfJobIsPickAndSend(Long JobsIDX) {
        String sqlstr = "";
        try {
            sqlstr = "SELECT COUNT(*) CT FROM " + TABLE_NAME + " WHERE (FLAG & 8388608) = 8388608 AND " + COLUMN_JobsIDX + " = '" + JobsIDX + "'";
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getCheckIfJobIsPickAndSend. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getJobChangesMessages(Long JobsIDX) {
        String sqlstr = "";
        try {
            sqlstr = "SELECT * FROM " + TABLE_NAME_CHATMESSAGE_TBL + " WHERE " + COLUMN_Message + " LIKE '%[job changes]%' AND " + COLUMN_JobsIDX + " = '" + JobsIDX + "' ORDER BY " + COLUMN_TimeStamp;
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getJobChangesMessages. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getUnAckJobChangesMsgIDXByJobsIDX(Long JobsIDX) {
        String sqlstr = "";
        try {
            //sqlstr = "SELECT " + COLUMN_IDX + " FROM " + TABLE_NAME_CHATMESSAGE_TBL + " WHERE " + COLUMN_Message + " LIKE '%[job changes]%' AND " + COLUMN_JobsIDX + " = '" +JobsIDX + "' AND " + COLUMN_Direction + " == 200";
            sqlstr = "SELECT cm." + COLUMN_SQLiteMessageID + ", cm." + COLUMN_IDX + ", cms." + COLUMN_notiID + " FROM " + TABLE_NAME_CHATMESSAGE_TBL +
                    " cm LEFT JOIN " + TABLE_NAME_ChatMsgSenders_TBL + " cms ON cms." + COLUMN_UserName + " = cm." + COLUMN_UserName + " AND cms." + COLUMN_UserIDX +
                    " = cm." + COLUMN_UserIDX + " WHERE cm." + COLUMN_Message + " LIKE '%[job changes]%' AND cm." + COLUMN_JobsIDX + " = '" + JobsIDX + "' AND cm." + COLUMN_Direction + " == 200";
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getUnAckJobChangesMsgIDXByJobsIDX. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getEarliestTimingAmongJobs(String JobsIDXs){
        String sqlstr = "";
        try {
            /*sqlstr = "SELECT jobsidx, jobno, statuscodeidx," +
                    " (case when statuscodeidx == 103 then (case when pickbydatetime == readydatetime then todatetime else pickbydatetime end) else todatetime end) expectedtime," +
                    " ((case when statuscodeidx == 103 then (case when pickbydatetime == readydatetime then todatetime else pickbydatetime end) else todatetime end) - strftime('%s','now')) as timediff," +
                    " (case when (case when statuscodeidx == 103 then (case when pickbydatetime == readydatetime then todatetime else pickbydatetime end) else todatetime end) - strftime('%s','now') <= 1800 THEN 1 ELSE 0 END) AS IsLate," +
                    " strftime('%s','now') now, ReadyDateTime, PickByDateTime, FromDateTime, ToDateTime, ExtDateTime" +
                    " from jobs where jobsidx in (" + JobsIDXs + ") and statuscodeidx in (103,104) order by expectedtime limit 1";*/

            sqlstr = "SELECT jobsidx, jobno, statuscodeidx," +
                    " (" +
                        " case when statuscodeidx == 103" +
                        " then (" +
                            " case when pickbydatetime == readydatetime then todatetime else pickbydatetime end )" +
                        " when ExtDateTime > 0" +
                        " then ExtDateTime else todatetime end ) expectedtime," +
                    " ((case when statuscodeidx == 103 then (case when pickbydatetime == readydatetime then todatetime else pickbydatetime end) else todatetime end) - strftime('%s','now')) as timediff," +
                    " (case when (case when statuscodeidx == 103 then (case when pickbydatetime == readydatetime then todatetime else pickbydatetime end) else todatetime end) - strftime('%s','now') <= 1800 THEN 1 ELSE 0 END) AS IsLate," +
                    " strftime('%s','now') now, ReadyDateTime, PickByDateTime, FromDateTime, ToDateTime, ExtDateTime, ExtFromDateTime" +
                    " from jobs where jobsidx in (" + JobsIDXs + ") and statuscodeidx in (103,104) order by expectedtime limit 1";

            /*sqlstr = "SELECT jobsidx, jobno, statuscodeidx," +
                    " (" +
                    " case when statuscodeidx == 103" +
                    " then (" +
                    " case when pickbydatetime == readydatetime then todatetime else pickbydatetime end )" +
                    " when ExtDateTime > 0" +
                    " then todatetime else todatetime end ) expectedtime," +
                    " ((case when statuscodeidx == 103 then (case when pickbydatetime == readydatetime then todatetime else pickbydatetime end) else todatetime end) - strftime('%s','now')) as timediff," +
                    " (case when (case when statuscodeidx == 103 then (case when pickbydatetime == readydatetime then todatetime else pickbydatetime end) else todatetime end) - strftime('%s','now') <= 1800 THEN 1 ELSE 0 END) AS IsLate," +
                    " strftime('%s','now') now, ReadyDateTime, PickByDateTime, FromDateTime, ToDateTime, ExtDateTime, ExtFromDateTime" +
                    " from jobs where jobsidx in (" + JobsIDXs + ") and statuscodeidx in (103,104) order by expectedtime limit 1";*/



        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getEarliestTimingAmongJobs. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getAddressWithPostalNoUnitByJobsIDX(String JobsIDX) {
        String sqlstr = "";
        try {
            sqlstr = "select IFNULL(AddressWithPostalNoUnit,'') AS AddressWithPostalNoUnit from jobs where JobsIDX = '" + JobsIDX + "'";
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getAddressWithPostalNoUnitByJobsIDX. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getAddressWithoutPostalByJobsIDX(String JobsIDX) {
        String sqlstr = "";
        try {
            sqlstr = "select IFNULL(" + COLUMN_AddressWithoutPostal + ",'') AS " + COLUMN_AddressWithoutPostal + " from  " + TABLE_NAME + " WHERE " + COLUMN_JobsIDX + " = '" + JobsIDX + "'";
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getAddressWithoutPostalByJobsIDX. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getAllJobsIDXPerPostal(String postal) {
        String sqlstr = "";

        try {
            sqlstr = "SELECT JB." + COLUMN_JobsIDX + " " +
                    ", (SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE " + COLUMN_Postal + " = '" + postal + "' AND " + COLUMN_CatTypeID + " = 0) pip, " +
                    "(SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE " + COLUMN_Postal + " = '" + postal + "' AND " + COLUMN_CatTypeID + " = 2) dip " +
                    "FROM " + TABLE_NAME + " JB WHERE JB.Postal = '" + postal + "' AND " + COLUMN_CatTypeID + " IN (0,2)";
        } catch (Exception e) {
            sqlstr = "";
            Date dt = new Date();
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getAllJobsIDXPerPostal. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getPIPDIPJobsCountPerPostal(String postal) {
        String sqlstr = "";

        try {
            sqlstr = "SELECT COUNT(*) C " +
                    "FROM " + TABLE_NAME + " JB WHERE JB.Postal = '" + postal + "' AND " + COLUMN_CatTypeID + " IN (0,2)";
        } catch (Exception e) {
            sqlstr = "";
            Date dt = new Date();
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getPIPDIPJobsCountPerPostal. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getAllPipDipLocations() {
        String sqlstr = "";

        try {
            /*sqlstr = "SELECT DISTINCT ji." + COLUMN_Postal + ", ji." + COLUMN_LAT + ", ji." + COLUMN_LNG + ", " +
                    "(SELECT " + COLUMN_AddressWithPostalNoUnit + " FROM " + TABLE_NAME + " WHERE " + COLUMN_Postal +
                    " = ji." + COLUMN_Postal + " and " + COLUMN_LAT + " = ji." + COLUMN_LAT + " AND " + COLUMN_LNG + " = ji." + COLUMN_LNG + " LIMIT 1) " + COLUMN_AddressWithPostalNoUnit +
                    ", (SELECT group_concat(DISTINCT " + COLUMN_JobsIDX + ") FROM " + TABLE_NAME + " WHERE " + COLUMN_CatTypeID + " IN (0,2) AND " + COLUMN_Postal +
                    " = ji." + COLUMN_Postal + ") JOBSIDXS FROM " + TABLE_NAME + " ji WHERE ji." + COLUMN_CatTypeID + " IN (0,2)";*/

            sqlstr = "SELECT DISTINCT ji." + COLUMN_Postal + ", ji." + COLUMN_LAT + ", ji." + COLUMN_LNG + ", " +
                    "(SELECT " + COLUMN_AddressWithPostalNoUnit + " FROM " + TABLE_NAME + " WHERE " + COLUMN_Postal +
                    " = ji." + COLUMN_Postal + " and " + COLUMN_LAT + " = ji." + COLUMN_LAT + " AND " + COLUMN_LNG + " = ji." + COLUMN_LNG + " LIMIT 1) " + COLUMN_AddressWithPostalNoUnit +
                    " FROM " + TABLE_NAME + " ji WHERE ji." + COLUMN_CatTypeID + " IN (0,2)";
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getAllPipDipLocations. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getAllPipDipPostals() {
        String sqlstr = "";

        try {
            sqlstr = "SELECT DISTINCT ji." + COLUMN_Postal +
                    " FROM " + TABLE_NAME + " ji WHERE ji." + COLUMN_CatTypeID + " IN (0,2)";
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getAllPipDipPostals. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String chkIfHasLunchTimeAvoid(String JobsIDX) {
        String sqlstr = "";
        try {
            sqlstr = "SELECT IFNULL(" + COLUMN_HASLUNCHTIME + ", 0) AS " + COLUMN_HASLUNCHTIME + " FROM " + TABLE_NAME + " WHERE " + COLUMN_JobsIDX + " = '" + JobsIDX + "'";
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs chkIfHasLunchTimeAvoid. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getFromTimeToTimeOfJob(String JobsIDX) {
        String sqlstr = "";
        try {
            sqlstr = "SELECT datetime(JB." + COLUMN_FromDateTime + ", 'unixepoch', 'localtime') FromDateTimeConverted, " +
                    "datetime(JB." + COLUMN_ToDateTime + ", 'unixepoch', 'localtime') ToDateTimeConverted, " +
                    "datetime(JB." + COLUMN_ExtDateTime + ", 'unixepoch', 'localtime') ExtDateTimeConverted,  FROM " + TABLE_NAME + " JB WHERE JB." + COLUMN_JobsIDX + " = '" + JobsIDX + "'";
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getFromTimeToTimeOfJob. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getColdItemJobIDX() {
        String sqlstr = "";
        try {
            sqlstr = "SELECT IFNULL(" + COLUMN_JobsIDX + ", 0) " + COLUMN_JobsIDX +" FROM " + TABLE_NAME + " WHERE (" + COLUMN_FLAG2 + " & 64) = 64 AND " + COLUMN_CatTypeID + " IN (0,2) LIMIT 1";
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getColdItemJobIDX. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getColdItemJobIDXs() {
        String sqlstr = "";
        try {
            sqlstr = "SELECT IFNULL(" + COLUMN_JobsIDX + ", 0) " + COLUMN_JobsIDX +" FROM " + TABLE_NAME + " WHERE (" + COLUMN_FLAG2 + " & 64) = 64 AND " + COLUMN_CatTypeID + " IN (0,2)";
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getColdItemJobIDXs. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getChatMessageAckIDXByJobsIDXs(String idxs) {
        String sqlstr = "";
        try {
            sqlstr = "SELECT * FROM " + TABLE_NAME_CHATMESSAGE_TBL + " WHERE " + COLUMN_JobsIDX + " IN (" + idxs + ") AND " + COLUMN_Direction + " = 200";
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getChatMessageAckIDXByJobsIDXs. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getUnreadPUOKPFAILjobsMsgInfo() {
        String sqlstr = "";
        try {
            sqlstr = "select cm." + COLUMN_JobsIDX + ", cm." + COLUMN_IDX + ", cm." + COLUMN_UserIDX + ", (select count(*) from jobs where " + COLUMN_JobsIDX +
                    " = cm." + COLUMN_JobsIDX + " and " + COLUMN_CatTypeID + " in (0,2)) JC from chatmessage cm where cm.direction = 200 and JC = 0";
            //sqlstr += " and c = 0";
            /*sqlstr = "select cm.jobsidx, cm.idx, cm.useridx, " +
                    "(select notiID from chatmsgsenders where jobsidx = cm.jobsidx and useridx = cm.useridx) notiID, " +
                    "(select count(*) from jobs where jobsidx = cm.jobsidx and cattypeid in (0,2)) c " +
                    "from chatmessage cm where cm.direction = 200";*/
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getUnreadPUOKPFAILjobsMsgInfo. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getStdRunJobsCount() {
        String sqlstr = "";
        try {
            sqlstr = "SELECT COUNT(*) C FROM JOBS WHERE CATTYPEID IN (0,1,2,6) AND SUBSTR(JOBNO, 5,1) = 'S'";

        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getStdRunJobsCount. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getTotalStdRunJobsCountperPostalSQLstr(String Postal) {
        String sqlstr = "";

        try {
            /*sqlstr = "SELECT COUNT (*) AS CT " +
                    "FROM " + TABLE_NAME + " JB " +
                    "WHERE JB." + COLUMN_CatTypeID + " IN (0, 2) " +
                    "AND JB." + COLUMN_Postal + " = '" + Postal + "' ";*/

            sqlstr = "SELECT COUNT(*)  CT " +
                    "FROM " + TABLE_NAME + " JB " +
                    "WHERE JB." + COLUMN_CatTypeID + " IN (0, 1, 2, 6) " +
                    "AND JB." + COLUMN_Postal + " = '" + Postal + "' AND SUBSTR(JOBNO, 5, 1) = 'S'";
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getTotalStdRunJobsCountperPostalSQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getDIPCatTypeCountByJobsIDX(Long JobsIDX){
        String sqlstr = "";

        try {
            sqlstr = "SELECT COUNT(*)  CT " +
                    "FROM " + TABLE_NAME + " JB " +
                    "WHERE JB." + COLUMN_JobsIDX + " = '" + JobsIDX + "' AND JB." + COLUMN_CatTypeID + " = 2";
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getDIPCatTypeCountByJobsIDX. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }

    public static String getUpdateJobsStatusCodeIDXMassSQQLstr(String JobsIDXsStr, Long StatusCodeIDX) {
        String sqlstr = "";
        try {
            sqlstr = "UPDATE " + TABLE_NAME + " SET " + COLUMN_StatusCodeIDX + " = " + StatusCodeIDX + " "
                    + " WHERE " + COLUMN_JobsIDX + " IN (" + JobsIDXsStr + ")";
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getUpdateJobsStatusCodeIDXMassSQQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }


    //=================================================PIPPCS=================================================

    public static String getInsertPIPPCSSQLstr(PipPcs pp) {
        String sqlstr = "";
        try {
            sqlstr = "INSERT INTO " + TABLE_NAME_NFCTAGS +
                    " (" +
                    COLUMN_NFCTAGS_CAIDX1 + "," +
                    COLUMN_NFCTAGS_CNIDX1 + "," +
                    COLUMN_NFCTAGS_CAIDX2 + "," +
                    COLUMN_NFCTAGS_CNIDX2 + "," +
                    COLUMN_NFCTAGS_UID +
                    ") VALUES (?,?,?,?,?);";
        } catch (Exception e) {
            sqlstr = "";
            objGlobalMethods.writeToFile("Exception caught at sqlStringJobs getInsertNFCTagsSQLstr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return sqlstr;
        }
    }




    //=================================================PIPPCS=================================================


}
