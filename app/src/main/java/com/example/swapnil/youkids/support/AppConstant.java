package com.example.swapnil.youkids.support;

import com.koushikdutta.async.future.Future;

import java.io.File;

/**
 * Created by Swapnil.Patel on 7/21/2016.
 */
public class AppConstant {
    Future<File> downloading;

    public static final String base_url = "http://designample.com/youtube/api/";

    public static final String videoList = base_url + "videoList";
    public static final String contactUs = base_url + "contactUs";
    public static final String cms = base_url + "cms";
    public static final String videoViews = base_url + "videoViews";
    public static final String videoDownload = base_url + "videoDownload";
    public static final String onlydate = base_url + "onlydate";

    /*------------------------Global Varibales---------------------------------------------------*/

    public static final String TAG_success = "success";
    public static final String TAG_data = "data";

    /*---------------VideoList Page--------------------------------------------------*/

    public static final String TAG_videosid = "videosid";
    public static final String TAG_video_title = "video_title";
    public static final String TAG_video = "video";
    public static final String TAG_video_image = "video_image";
    public static final String TAG_total_download = "total_download";
    public static final String TAG_total_view = "total_view";
    public static final String TAG_date = "last_updated_on";

    /*------------for ContactUs List------------------------------------------*/

    public static final String TAG_name = "name";
    public static final String TAG_email = "email";
    public static final String TAG_message = "message";
    public static final String TAG_contact_no = "contact_no";


    /*---------------for Equity Market List----------------------------------------*/

    public static final String TAG_title = "title";
    public static final String TAG_description = "description";
}
