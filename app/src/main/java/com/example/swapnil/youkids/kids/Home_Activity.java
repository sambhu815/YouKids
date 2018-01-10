package com.example.swapnil.youkids.kids;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.os.StrictMode;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.swapnil.youkids.R;
import com.example.swapnil.youkids.adapter.RecyclerView_Adapter;
import com.example.swapnil.youkids.db.Helper;
import com.example.swapnil.youkids.fragment.Menu_Fragment;
import com.example.swapnil.youkids.pojo.VideoLIst;
import com.example.swapnil.youkids.support.PrefManger;
import com.example.swapnil.youkids.support.RecyclerTouchListener;
import com.example.swapnil.youkids.support.SupportUtil;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

public class Home_Activity extends AppCompatActivity {

    SupportUtil support;

    ImageView iv_menu;
    TextView tv_label;

    private static RecyclerView recyclerView;
    RecyclerView_Adapter recyclerView_adapter;

    File SDCard, dir, dirtemp;

    PrefManger manger;
    Helper helper;
    List<VideoLIst> videoLIsts;

    LinearLayoutManager linearLayoutManager;
    public static int index = -1;
    public static int top = -1;

    FragmentManager fragmentManager;
    Parcelable parce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_home);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        helper = new Helper(this);
        manger = new PrefManger(this);
        fragmentManager = getSupportFragmentManager();

        //FullScreenCall();
        support = new SupportUtil(this);

        SDCard = getExternalFilesDir(null);
        dir = new File(SDCard.getAbsolutePath() + "/.YouKids/");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        iv_menu = (ImageView) findViewById(R.id.iv_menu);
        tv_label = (TextView) findViewById(R.id.tv_label);

        linearLayoutManager = new LinearLayoutManager(Home_Activity.this, LinearLayoutManager.HORIZONTAL, false);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(3);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setLayoutManager(linearLayoutManager);

        videoLIsts = helper.getVideoList();

        recyclerView_adapter = new RecyclerView_Adapter(Home_Activity.this, videoLIsts);
        recyclerView.setAdapter(recyclerView_adapter);
        recyclerView_adapter.notifyDataSetChanged();

        iv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Menu_Fragment().showFragment(getSupportFragmentManager());
            }
        });

        tv_label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("http://www.google.com");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        parce = linearLayoutManager.onSaveInstanceState();
        outState.putParcelable("state", parce);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null)
            parce = savedInstanceState.getParcelable("state");
    }

    public void refresh() {
        recyclerView.invalidate();
        recyclerView.refreshDrawableState();
        recyclerView_adapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        index = linearLayoutManager.findFirstVisibleItemPosition();
        View v = recyclerView.getChildAt(0);
        top = (v == null) ? 0 : (v.getTop() - recyclerView.getPaddingTop());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (index != -1) {
            linearLayoutManager.scrollToPositionWithOffset(index, top);
        }

        if (parce != null) {
            linearLayoutManager.onRestoreInstanceState(parce);
        }
    }

    @Override
    public void onBackPressed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                } else {
                    alertExit();
                }
            }
        });
    }

    private void alertExit() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Home_Activity.this);

        alertDialogBuilder.setTitle("Are You sure want to Exit?");

        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dirtemp = new File(SDCard.getAbsolutePath() + "/.Temp/");
                        File[] files = dirtemp.listFiles(new GenericExtFilter(".txt"));
                        for (File file : files) {
                            if (!file.isDirectory()) {
                                boolean reslut = file.delete();
                                Log.e("TAG", "Deleted:" + reslut);
                            }
                        }

                        Intent in = new Intent(Intent.ACTION_MAIN);
                        in.addCategory(Intent.CATEGORY_HOME);
                        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(in);
                        finish();
                        System.exit(0);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
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
