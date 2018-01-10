package com.example.swapnil.youkids.db;

/**
 * Created by kajal.dani on 6/1/2016.
 */
public class App_code {

    /*Sqlite Database Components*/
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_FILE_PATH = "/YouKids";
    public static final String DATABASE_NAME = "mikenmia";

    /*Table Names*/
    public static final String TABLE_VIDEOLIST = "VideoList";

    /*Message Table With Multi Language*/
    public static final String V_ID = "Id";
    public static final String V_DATE = "date";
    public static final String V_VID = "videosid";
    public static final String V_TITLE = "video_title";
    public static final String V_VIDEO = "video";
    public static final String V_IMAGE = "video_image";
    public static final String V_DOWNLOAD = "total_download";
    public static final String V_VIEW = "total_view";

    public static final String VideoTable = "CREATE TABLE "
            + TABLE_VIDEOLIST + "("
            + V_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + V_DATE + " TEXT,"
            + V_VID + " TEXT,"
            + V_TITLE + " TEXT,"
            + V_VIDEO + " TEXT,"
            + V_IMAGE + " TEXT,"
            + V_DOWNLOAD + " TEXT,"
            + V_VIEW + " TEXT" + ")";
}
