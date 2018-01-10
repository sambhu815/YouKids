package com.example.swapnil.youkids.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.swapnil.youkids.R;
import com.example.swapnil.youkids.support.AppConstant;
import com.example.swapnil.youkids.support.SupportUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by sambh on 25-10-2017.
 */

public class Aboutus_Fragment extends Fragment {
    public static final String TAG = Aboutus_Fragment.class.getSimpleName();
    View rootView;

    FragmentManager fragmentManager;
    private AppCompatActivity activity;

    TextView tv_title, tv_dec;

    SupportUtil support;

    OkHttpClient client;
    Request request;

    String str_mesg;
    String str_id;

    public Aboutus_Fragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (AppCompatActivity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        str_id = getArguments().getString("id");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_aboutus, null);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        support = new SupportUtil(activity);

        tv_title = (TextView) rootView.findViewById(R.id.tv_title);
        tv_dec = (TextView) rootView.findViewById(R.id.tv_dec);


        if (support.checkInternetConnectivity()) {
            new Aboutus().execute();
        } else {
            Snackbar.make(activity.findViewById(android.R.id.content), "Please Check your Internet Connection", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Setting", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(
                                    new Intent(Settings.ACTION_SETTINGS));
                        }
                    }).show();
        }
        return rootView;
    }

    private class Aboutus extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(final String... strings) {
            try {
                client = new OkHttpClient();

                FormBody.Builder builder = new FormBody.Builder()
                        .add("id", str_id);

                RequestBody requestBody = builder.build();

                request = new Request.Builder().url(AppConstant.cms).post(requestBody).build();

                Response response = client.newCall(request).execute();
                Log.e("Responce", "" + response);
                int status = response.code();

                if (response.code() == 400) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(activity.findViewById(android.R.id.content), getResources().getString(R.string.error), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    });
                } else {
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            if (e instanceof SocketTimeoutException) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Snackbar.make(activity.findViewById(android.R.id.content), getResources().getString(R.string.error), Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onResponse(Call call, final Response response) throws IOException {
                            if (response != null) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            String json = response.body().string();
                                            JSONObject jsonObject = new JSONObject(json);
                                            Log.e("AboutUs ->", "" + jsonObject);

                                            String str_success = jsonObject.getString(AppConstant.TAG_success);

                                            if (str_success.equals("1")) {
                                                JSONObject jsondata = jsonObject.getJSONObject(AppConstant.TAG_data);

                                                tv_title.setText(jsondata.getString(AppConstant.TAG_title));
                                                tv_dec.setText(jsondata.getString(AppConstant.TAG_description));
                                            } else {
                                                str_mesg = jsonObject.getString("message");
                                                alert();
                                            }
                                        } catch (final JSONException e) {
                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    str_mesg = (e.getMessage() == null) ? "Connection failed!" : e.getMessage();
                                                    Log.d(TAG, e.getMessage());
                                                    alert();
                                                }
                                            });
                                        } catch (final SocketTimeoutException e) {
                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    str_mesg = (e.getMessage() == null) ? "Connection failed!" : e.getMessage();
                                                    e.printStackTrace();
                                                    Log.d(TAG, e.getMessage());
                                                    alert();
                                                }
                                            });
                                        } catch (final IOException e) {
                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    str_mesg = (e.getMessage() == null) ? "Connection failed!" : e.getMessage();
                                                    e.printStackTrace();
                                                    Log.d(TAG, e.getMessage());
                                                    alert();
                                                }
                                            });
                                        } catch (final NullPointerException e) {
                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    str_mesg = (e.getMessage() == null) ? "Connection failed!" : e.getMessage();
                                                    Log.d(TAG, e.getMessage());
                                                    alert();
                                                }
                                            });
                                        } catch (final Exception e) {
                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    str_mesg = (e.getMessage() == null) ? "Connection failed!" : e.getMessage();
                                                    Log.d(TAG, e.getMessage());
                                                    alert();
                                                }
                                            });
                                        }
                                    }
                                });
                            } else {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

                                        alertDialogBuilder.setTitle(getResources().getString(R.string.error));

                                        alertDialogBuilder
                                                .setCancelable(true)
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        fragmentManager.popBackStack();
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
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

        alertDialogBuilder.setTitle(str_mesg);

        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        fragmentManager.popBackStack();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void showFragment(final FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(
                R.anim.fragmentv_slide_bottom_enter,
                0,
                0,
                R.anim.fragmentv_slide_top_exit);
        transaction.add(Window.ID_ANDROID_CONTENT, Aboutus_Fragment.this, "AboutUs");
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }
}
