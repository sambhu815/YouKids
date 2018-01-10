package com.example.swapnil.youkids.fragment;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.swapnil.youkids.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sambh on 25-10-2017.
 */

public class Video_Fragment extends Fragment {
    public static final String TAG = Video_Fragment.class.getSimpleName();
    public static int i;
    public static String videoPath;

    View rootView;
    FragmentManager fragmentManager;

    private AppCompatActivity activity;
    String str_name, str_path;
    ImageView iv_back;
    TextView tv_name;

    RelativeLayout rl_header;
    VideoView videoview;

    MediaController mediacontroller;
    String[] filelist;
    File videoFile;
    List<String> videolist;
    int progress;

    public Video_Fragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (AppCompatActivity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        str_name = getArguments().getString("name");
        str_path = getArguments().getString("video");

        i = getArguments().getInt("position");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_video, null);

        iv_back = (ImageView) rootView.findViewById(R.id.iv_back);
        tv_name = (TextView) rootView.findViewById(R.id.tv_name);
        rl_header = (RelativeLayout) rootView.findViewById(R.id.rl_header);

        videoview = (VideoView) rootView.findViewById(R.id.videoView);
        mediacontroller = new MediaController(activity);

        videoview.setVideoPath(str_path);
        mediacontroller.setAnchorView(videoview);

        rl_header.setVisibility(View.GONE);
        tv_name.setText(str_name);

        videoFile = new File(activity.getExternalFilesDir(null).getAbsolutePath() + "/.YouKids");

        if (videoFile.isDirectory()) {
            filelist = videoFile.list();
        }

        videolist = new ArrayList<String>(Arrays.asList(filelist));
        int size = videolist.size();
        for (int i = 0; i < size; i++) {
            String value = videolist.get(i);
            if (value.contains(".temp")) {
                videolist.remove(i);
                size = videolist.size();
            }
        }
        Log.e("LIst", "" + videolist);

        videoview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (rl_header.getVisibility() == View.GONE) {
                    rl_header.setVisibility(View.VISIBLE);
                } else {
                    rl_header.setVisibility(View.GONE);
                }
                return false;
            }
        });

        mediacontroller.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //"next"
                if (i == videolist.size()) i = 1;
                i++;

                if (i == videolist.size()) {
                    videoPath = videoFile + "/" + videolist.get(0);
                    videoview.setVideoPath(videoPath);
                    String exten = videolist.get(0).substring(0, videolist.get(0).lastIndexOf("."));
                    tv_name.setText(exten);
                    i = 0;
                } else {
                    videoPath = videoFile + "/" + videolist.get(i);
                    videoview.setVideoPath(videoPath);
                    String exten = videolist.get(i).substring(0, videolist.get(i).lastIndexOf("."));
                    tv_name.setText(exten);
                }
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // "previous"
                if (i == videolist.size()) i = 1;
                {
                    if (i == 0) {
                        i = videolist.size() - 1;
                        videoPath = videoFile + "/" + videolist.get(i);
                        videoview.setVideoPath(videoPath);
                        String exten = videolist.get(i).substring(0, videolist.get(i).lastIndexOf("."));
                        tv_name.setText(exten);
                    } else {
                        i--;
                        videoPath = videoFile + "/" + videolist.get(i);
                        videoview.setVideoPath(videoPath);
                        String exten = videolist.get(i).substring(0, videolist.get(i).lastIndexOf("."));
                        tv_name.setText(exten);
                    }
                }
            }
        });

        videoview.setMediaController(mediacontroller);
        videoview.start();

        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                rl_header.setVisibility(View.VISIBLE);
                if (i == videolist.size()) i = 1;
                i++;

                if (i == videolist.size()) {
                    videoview.setVideoPath(videoFile + "/" + videolist.get(0));
                    String exten = videolist.get(0).substring(0, videolist.get(0).lastIndexOf("."));
                    tv_name.setText(exten);
                    i = 0;
                } else {
                    videoPath = videoFile + "/" + videolist.get(i);
                    videoview.setVideoPath(videoPath);
                    videoview.start();
                    String exten = videolist.get(i).substring(0, videolist.get(i).lastIndexOf("."));
                    tv_name.setText(exten);
                }
            }
        });

        /*iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (videoview != null) {
                    videoview.stopPlayback();
                    videoview = null;
                }
                fragmentManager.popBackStack();
            }
        });*/
        return rootView;
    }

    @Override
    public void onPause() {
        videoview.canPause();
        progress = videoview.getCurrentPosition();
        super.onPause();
    }

    @Override
    public void onResume() {
        videoview.seekTo(progress);
        videoFile = new File(activity.getExternalFilesDir(null).getAbsolutePath() + "/.YouKids");

        if (videoFile.isDirectory()) {
            filelist = videoFile.list();
        }

        videolist = new ArrayList<String>(Arrays.asList(filelist));
        videoPath = videoFile + "/" + videolist.get(i);

        Log.e("videoPath", videoPath);
        Log.e("videoLIst", "" + videolist);
        Log.e("i", "" + i);
        super.onResume();
    }

    public void showFragment(final FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(
                R.anim.fragmentv_slide_bottom_enter,
                0,
                0,
                R.anim.fragmentv_slide_top_exit);
        transaction.add(Window.ID_ANDROID_CONTENT, Video_Fragment.this, "Video");
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }
}
