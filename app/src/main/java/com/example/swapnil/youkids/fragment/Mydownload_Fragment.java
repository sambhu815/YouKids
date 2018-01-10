package com.example.swapnil.youkids.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.swapnil.youkids.R;
import com.example.swapnil.youkids.adapter.MyVideoAdapter;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sambh on 25-10-2017.
 */

public class Mydownload_Fragment extends Fragment {
    public static final String TAG = Mydownload_Fragment.class.getSimpleName();
    View rootView;

    FragmentManager fragmentManager;
    private AppCompatActivity activity;

    RecyclerView list_video;
    TextView tv_view;

    String[] filelist;
    File videoFile;
    String videoPath;

    MyVideoAdapter myVideoAdapter;

    public Mydownload_Fragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (AppCompatActivity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_my_download, null);

        tv_view = (TextView) rootView.findViewById(R.id.tv_view);

        list_video = (RecyclerView) rootView.findViewById(R.id.list_video);
        list_video.setHasFixedSize(true);
        list_video.setItemViewCacheSize(3);
        list_video.setDrawingCacheEnabled(true);
        list_video.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        list_video.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));

        videoFile = new File(activity.getExternalFilesDir(null).getAbsolutePath() + "/.YouKids");

        if (videoFile.isDirectory()) {
            filelist = videoFile.list();
        }

        videoPath = videoFile.getAbsolutePath() + "/";
        List<String> videolist = new ArrayList<String>(Arrays.asList(filelist));
        int size = videolist.size();
        for (int i = 0; i < size; i++) {
            String value = videolist.get(i);
            if (value.contains(".temp")) {
                videolist.remove(i);
                size = videolist.size();
            }
        }
        Log.e("LIst", "" + videolist);

        myVideoAdapter = new MyVideoAdapter(activity, videolist, videoPath);
        list_video.setAdapter(myVideoAdapter);
        myVideoAdapter.notifyDataSetChanged();

        if (videolist.size() == 0) {
            tv_view.setVisibility(View.VISIBLE);
            list_video.setVisibility(View.GONE);
        } else {
            tv_view.setVisibility(View.GONE);
            list_video.setVisibility(View.VISIBLE);
        }
        return rootView;
    }

    public void showFragment(final FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(
                R.anim.fragmentv_slide_bottom_enter,
                0,
                0,
                R.anim.fragmentv_slide_top_exit);
        transaction.add(Window.ID_ANDROID_CONTENT, Mydownload_Fragment.this, "MyDownload");
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }
}
