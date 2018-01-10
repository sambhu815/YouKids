package com.example.swapnil.youkids.kids;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.VideoView;

import com.example.swapnil.youkids.R;
import com.example.swapnil.youkids.support.PrefManger;
import com.example.swapnil.youkids.support.SupportUtil;

import java.io.File;
import java.io.FilenameFilter;

public class Intro_Activity extends AppCompatActivity {

    private String path;
    private String current;
    private VideoView mVideoView;

    SupportUtil support;
    PrefManger manger;

    SharedPreferences Pid;
    String str_delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_intro);

        manger = new PrefManger(this);
        support = new SupportUtil(this);
        FullScreenCall();

        Pid = getSharedPreferences("Mike", 0);
        str_delete = Pid.getString(manger.First, null);

        if (str_delete.equals("splash")) {
            DeleteFiles();
        }

        path = "android.resource://" + getPackageName() + "/" + R.raw.intro;

        mVideoView = (VideoView) findViewById(R.id.video_view);

        if (path == null || path.length() == 0) {

        } else {
            // If the path has not changed, just start the media player
            if (path.equals(current) && mVideoView != null) {
                mVideoView.start();
                mVideoView.requestFocus();
                return;
            }
            current = path;
            mVideoView.setVideoPath(path);
            mVideoView.start();
            mVideoView.requestFocus();
        }

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer arg0) {
                Intent welcome_intent = new Intent(getApplicationContext(), Home_Activity.class);
                startActivity(welcome_intent);
                Intro_Activity.this.finish();
            }
        });
    }

    private void FullScreenCall() {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    public void DeleteFiles() {
        File SDCard, dir;

        SDCard = getExternalFilesDir(null);
        dir = new File(SDCard.getAbsolutePath() + "/.Temp/");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File[] files = dir.listFiles(new GenericExtFilter(".txt"));
        for (File file : files) {
            if (!file.isDirectory()) {
                boolean reslut = file.delete();
                Log.e("TAG", "Deleted:" + reslut);
            }
        }
    }

    public class GenericExtFilter implements FilenameFilter {

        private String ext;

        public GenericExtFilter(String ext) {
            this.ext = ext;
        }

        public boolean accept(File dir, String name) {
            return (name.endsWith(ext));
        }
    }
}
