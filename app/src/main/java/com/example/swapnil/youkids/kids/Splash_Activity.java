package com.example.swapnil.youkids.kids;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.swapnil.youkids.R;
import com.example.swapnil.youkids.db.Helper;
import com.example.swapnil.youkids.pojo.VideoLIst;
import com.example.swapnil.youkids.support.AppConstant;
import com.example.swapnil.youkids.support.PrefManger;
import com.example.swapnil.youkids.support.SupportUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Splash_Activity extends AppCompatActivity {
    public static final String TAG = Splash_Activity.class.getSimpleName();

    SupportUtil support;
    SharedPreferences Pid, datepref;
    PrefManger manger;
    SharedPreferences.Editor editor;

    int pid, processId;
    String str_success, str_mesg;

    ProgressBar progressBar;
    Helper helper;
    OkHttpClient client;
    Request request;

    List<VideoLIst> videoLists;
    String str_date, str_newdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        manger = new PrefManger(this);
        helper = new Helper(this);
        support = new SupportUtil(this);
        FullScreenCall();
        datepref = getSharedPreferences("UpdatedDate", 0);
        editor = datepref.edit();

        Pid = getSharedPreferences("Mike", 0);
        pid = Pid.getInt(manger.APP_PROCESS_ID, 0);

        if (pid == 0) {
            processId = android.os.Process.myPid();
            manger.ProcessId(processId, "splash");
        } else {
            manger.ProcessId(pid, "splash");
        }

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        videoLists = helper.getVideoList();
        Log.e("VideoList size ::", "" + videoLists.size());

        if (support.checkInternetConnectivity()) {
            if (videoLists.size() == 0) {
                progressBar.setVisibility(View.VISIBLE);
                new VideoList().execute();
                Toast.makeText(getApplicationContext(), "Please wait, Don't close Mike&Mia.", Toast.LENGTH_SHORT);
            } else {
                str_date = datepref.getString("date", null);
                new VideoListDate().execute();
            }
        } else {
            final Dialog dialog = new Dialog(this);
            Window window = dialog.getWindow();
            window.requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_error);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            final Button btn_ok = (Button) dialog.findViewById(R.id.btn_ok);

            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    Intent in = new Intent(getApplicationContext(), Intro_Activity.class);
                    startActivity(in);
                    finish();
                }
            });
            dialog.show();
        }
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

    private class VideoList extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(final String... strings) {
            try {
                client = new OkHttpClient();
                RequestBody reqbody = RequestBody.create(null, new byte[0]);

                request = new Request.Builder().url(AppConstant.videoList).post(reqbody).build();

                Response response = client.newCall(request).execute();
                Log.e("Responce", "" + response);

                if (response.code() == 400) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.error), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    });
                } else {
                    return response.body().string();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if (e instanceof SocketTimeoutException) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.error), Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        });
                    }
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    if (response != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String json = response.body().string();
                                    JSONObject jsonObject = new JSONObject(json);
                                    Log.e("VideoList ->", "" + jsonObject);

                                    str_success = jsonObject.getString(AppConstant.TAG_success);

                                    if (str_success.equals("1")) {

                                        JSONArray jsondata = jsonObject.getJSONArray(AppConstant.TAG_data);

                                        for (int i = 0; i < jsondata.length(); i++) {
                                            JSONObject object = jsondata.getJSONObject(i);

                                            String str_vid = object.getString(AppConstant.TAG_videosid);
                                            str_date = object.getString(AppConstant.TAG_date);
                                            String str_title = object.getString(AppConstant.TAG_video_title);
                                            String str_video = object.getString(AppConstant.TAG_video);
                                            String str_image = object.getString(AppConstant.TAG_video_image);
                                            String str_download = object.getString(AppConstant.TAG_total_download);
                                            String str_view = object.getString(AppConstant.TAG_total_view);

                                            VideoLIst lIst = new VideoLIst(str_vid, str_date, str_title, str_video, str_image, str_download, str_view);
                                            helper.insertVideo(lIst);
                                            helper.close();
                                        }

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                editor.putString("date", str_date);
                                                editor.commit();

                                                Intent in = new Intent(getApplicationContext(), Intro_Activity.class);
                                                startActivity(in);
                                                finish();
                                            }
                                        });
                                    } else {
                                        str_mesg = jsonObject.getString("message");
                                        alert();
                                    }
                                } catch (final JSONException e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            str_mesg = (e.getMessage() == null) ? "Connection failed!" : e.getMessage();
                                            Log.d(TAG, str_mesg);
                                            alert();
                                        }
                                    });
                                } catch (final SocketTimeoutException e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            str_mesg = (e.getMessage() == null) ? "Connection failed!" : e.getMessage();
                                            e.printStackTrace();
                                            Log.d(TAG, str_mesg);
                                            alert();
                                        }
                                    });
                                } catch (final IOException e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            str_mesg = (e.getMessage() == null) ? "Connection failed!" : e.getMessage();
                                            e.printStackTrace();
                                            Log.d(TAG, str_mesg);
                                            alert();
                                        }
                                    });
                                } catch (final NullPointerException e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            str_mesg = (e.getMessage() == null) ? "Connection failed!" : e.getMessage();
                                            Log.d(TAG, str_mesg);
                                            alert();
                                        }
                                    });
                                } catch (final Exception e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            str_mesg = (e.getMessage() == null) ? "Connection failed!" : e.getMessage();
                                            Log.d(TAG, str_mesg);
                                            alert();
                                        }
                                    });
                                }
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Splash_Activity.this);

                                alertDialogBuilder.setTitle(getResources().getString(R.string.error));

                                alertDialogBuilder
                                        .setCancelable(true)
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                System.exit(0);
                                            }
                                        });
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                            }
                        });
                    }
                }
            });
        }
    }

    private class VideoListDate extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(final String... strings) {
            try {
                client = new OkHttpClient();
                RequestBody reqbody = RequestBody.create(null, new byte[0]);

                request = new Request.Builder().url(AppConstant.onlydate).post(reqbody).build();

                Response response = client.newCall(request).execute();
                Log.e("Responce", "" + response);

                if (response.code() == 400) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.error), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    });
                } else {
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            if (e instanceof SocketTimeoutException) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.error), Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onResponse(Call call, final Response response) throws IOException {
                            if (response != null) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            String json = response.body().string();
                                            JSONObject jsonObject = new JSONObject(json);
                                            Log.e("VideoList Date ->", "" + jsonObject);

                                            str_success = jsonObject.getString(AppConstant.TAG_success);

                                            if (str_success.equals("1")) {
                                                JSONArray jsondata = jsonObject.getJSONArray(AppConstant.TAG_data);

                                                for (int i = 0; i < jsondata.length(); i++) {
                                                    JSONObject object = jsondata.getJSONObject(i);
                                                    str_newdate = object.getString(AppConstant.TAG_date);
                                                }

                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (str_date.equals(str_newdate)) {
                                                            Intent in = new Intent(getApplicationContext(), Intro_Activity.class);
                                                            startActivity(in);
                                                            finish();
                                                        } else {
                                                            progressBar.setVisibility(View.VISIBLE);
                                                            helper.DeleteDB();
                                                            new VideoList().execute();
                                                            Toast.makeText(getApplicationContext(), "Please wait, Don't close Mike&Mia.", Toast.LENGTH_SHORT);
                                                        }
                                                    }
                                                });

                                            } else {
                                                str_mesg = jsonObject.getString("message");
                                                alert();
                                            }
                                        } catch (final JSONException e) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    str_mesg = (e.getMessage() == null) ? "Connection failed!" : e.getMessage();
                                                    Log.d(TAG, str_mesg);
                                                    alert();
                                                }
                                            });
                                        } catch (final SocketTimeoutException e) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    str_mesg = (e.getMessage() == null) ? "Connection failed!" : e.getMessage();
                                                    e.printStackTrace();
                                                    Log.d(TAG, str_mesg);
                                                    alert();
                                                }
                                            });
                                        } catch (final IOException e) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    str_mesg = (e.getMessage() == null) ? "Connection failed!" : e.getMessage();
                                                    e.printStackTrace();
                                                    Log.d(TAG, str_mesg);
                                                    alert();
                                                }
                                            });
                                        } catch (final NullPointerException e) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    str_mesg = (e.getMessage() == null) ? "Connection failed!" : e.getMessage();
                                                    Log.d(TAG, str_mesg);
                                                    alert();
                                                }
                                            });
                                        } catch (final Exception e) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    str_mesg = (e.getMessage() == null) ? "Connection failed!" : e.getMessage();
                                                    Log.d(TAG, str_mesg);
                                                    alert();
                                                }
                                            });
                                        }
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Splash_Activity.this);

                                        alertDialogBuilder.setTitle(getResources().getString(R.string.error));

                                        alertDialogBuilder
                                                .setCancelable(true)
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        System.exit(0);
                                                    }
                                                });
                                        AlertDialog alertDialog = alertDialogBuilder.create();
                                        alertDialog.show();
                                    }
                                });
                            }
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private void alert() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Splash_Activity.this);

        alertDialogBuilder.setTitle(str_mesg);

        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        helper.DeleteDB();
                        System.exit(0);
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}