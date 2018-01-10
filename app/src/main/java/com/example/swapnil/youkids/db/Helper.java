package com.example.swapnil.youkids.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.example.swapnil.youkids.pojo.VideoLIst;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Swapnil.Patel on 9/20/2016.
 */

public class Helper extends SQLiteOpenHelper {

    SQLiteDatabase db;

    public Helper(Context context) {
        super(context,
                /*Environment.getExternalStorageDirectory()
                        + File.separator
                        + App_code.DATABASE_FILE_PATH
                        + File.separator
                        +*/ App_code.DATABASE_NAME, null,
                App_code.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(App_code.VideoTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + App_code.VideoTable);
    }

    public void DeleteDB() {
        db = this.getWritableDatabase();
        db.delete(App_code.TABLE_VIDEOLIST, null, null);
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }


    public boolean insertVideo(VideoLIst videoLIst) {
        db = this.getWritableDatabase();
        boolean value = false;

        try {
            ContentValues cv = new ContentValues();
            cv.put(App_code.V_DATE, videoLIst.getDate());
            cv.put(App_code.V_VID, videoLIst.getVid());
            cv.put(App_code.V_TITLE, videoLIst.getTitle());
            cv.put(App_code.V_VIDEO, videoLIst.getVideo());
            cv.put(App_code.V_IMAGE, videoLIst.getImage());
            cv.put(App_code.V_DOWNLOAD, videoLIst.getDownload());
            cv.put(App_code.V_VIEW, videoLIst.getView());

            long ins = db.insert(App_code.TABLE_VIDEOLIST, null, cv);

            if (ins != -1) {
                value = true;
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return value;
    }

    public List<VideoLIst> getVideoList() {
        VideoLIst list;
        db = this.getReadableDatabase();
        List<VideoLIst> videoLIsts = new ArrayList<>();
        String q = "SELECT * FROM " + App_code.TABLE_VIDEOLIST;
        Log.e("Video List >>", q);

        Cursor cursor = db.rawQuery(q, null);

        if (cursor.moveToFirst()) {
            do {
                list = new VideoLIst();
                list.setDate(cursor.getString(1));
                list.setVid(cursor.getString(2));
                list.setTitle(cursor.getString(3));
                list.setVideo(cursor.getString(4));
                list.setImage(cursor.getString(5));
                list.setDownload(cursor.getString(6));
                list.setView(cursor.getString(7));
                videoLIsts.add(list);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return videoLIsts;
    }
}
