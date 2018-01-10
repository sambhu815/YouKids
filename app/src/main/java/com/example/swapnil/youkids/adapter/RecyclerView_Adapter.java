package com.example.swapnil.youkids.adapter;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.swapnil.youkids.fragment.VideoHome_Fragment;
import com.example.swapnil.youkids.kids.Home_Activity;
import com.example.swapnil.youkids.R;
import com.example.swapnil.youkids.kids.Intro_Activity;
import com.example.swapnil.youkids.kids.Splash_Activity;
import com.example.swapnil.youkids.pojo.VideoLIst;
import com.example.swapnil.youkids.support.AppConstant;
import com.example.swapnil.youkids.support.SupportUtil;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RecyclerView_Adapter extends RecyclerView.Adapter<RecyclerView_Adapter.RecyclerViewHolder> {

    List<VideoLIst> videoLIsts;
    private Context context;

    //Constructor
    public RecyclerView_Adapter(Context context, List<VideoLIst> videoLIsts) {
        this.context = context;
        this.videoLIsts = videoLIsts;
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        public TextView title, cur_pg_tv, tv_id, tv_url;
        public ImageView imageview, iv_play, iv_download;
        RelativeLayout rl_progress;
        ProgressBar progress_bar;
        String str_videoUrl, str_videoName, str_videoId;

        Future<File> downloading;
        File videoTempWrite;
        SupportUtil support;

        public RecyclerViewHolder(View view) {
            super(view);

            this.support = new SupportUtil(context);

            this.title = (TextView) view.findViewById(R.id.tv_title);
            this.tv_id = (TextView) view.findViewById(R.id.tv_id);
            this.tv_url = (TextView) view.findViewById(R.id.tv_url);

            this.imageview = (ImageView) view.findViewById(R.id.coverImageView);
            this.iv_play = (ImageView) view.findViewById(R.id.iv_play);
            this.iv_download = (ImageView) view.findViewById(R.id.iv_download);

            this.rl_progress = (RelativeLayout) view.findViewById(R.id.rl_progress);
            this.progress_bar = (ProgressBar) view.findViewById(R.id.progress_bar);
            this.cur_pg_tv = (TextView) view.findViewById(R.id.cur_pg_tv);
        }
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_items, parent, false);
        RecyclerViewHolder holder = new RecyclerViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder viewHolder, final int position) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        viewHolder.title.setText(videoLIsts.get(position).getTitle());

        viewHolder.str_videoId = videoLIsts.get(position).getVid();
        viewHolder.str_videoUrl = videoLIsts.get(position).getVideo();
        viewHolder.str_videoName = videoLIsts.get(position).getTitle();

        // Load Imges of video file from url
        final String str_url = videoLIsts.get(position).getImage();
        if (str_url.isEmpty()) {
            viewHolder.imageview.setImageResource(R.mipmap.ic_launcher);
        } else {
            Picasso.with(context)
                    .load(str_url)
                    .fit()
                    .centerCrop()
                    // .error(R.mipmap.ic_launcher)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(viewHolder.imageview, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(context)
                                    .load(str_url)
                                    .fit()
                                    .centerCrop()
                                    .error(R.mipmap.ic_launcher)
                                    .into(viewHolder.imageview);
                        }
                    });
        }

        //File Directory for getting downloaded file from folder
        File SDCard = context.getExternalFilesDir(null);
        final File dir = new File(SDCard.getAbsolutePath() + "/.YouKids/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        final File video = new File(dir, viewHolder.str_videoName + ".mp4");

        if (!video.exists()) {
            viewHolder.iv_download.setVisibility(View.VISIBLE);
            viewHolder.iv_play.setVisibility(View.GONE);
        } else {
            viewHolder.iv_download.setVisibility(View.GONE);
            viewHolder.iv_play.setVisibility(View.VISIBLE);
        }

        //File Directory for getting temp file which are not downloaded completely.
        final File dirtemp = new File(SDCard.getAbsolutePath() + "/.Temp/");
        if (!dirtemp.exists()) {
            dirtemp.mkdirs();
        }

        final File videoTemp = new File(dirtemp, viewHolder.str_videoName + ".txt");

       /* RotateAnimation anim = new RotateAnimation(0.0f, 360.f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(700);
        viewHolder.iv_load.startAnimation(anim);

        if (videoTemp.exists()) {
            viewHolder.iv_download.setVisibility(View.GONE);
            viewHolder.iv_load.setVisibility(View.VISIBLE);
        } else {
            viewHolder.iv_load.setVisibility(View.GONE);
        }*/

        //Button - show progress of video file
        viewHolder.rl_progress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyDataSetChanged();
                notifyItemChanged(position);

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context.getApplicationContext(),
                                "Downloading under progress, it will play once it's complete downloading. Thank You.",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        //Button - Download video
        viewHolder.iv_download.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View view) {
                new Thread(new Runnable() {
                    public void run() {
                        if (viewHolder.support.checkInternetConnectivity()) {
                            viewHolder.str_videoUrl = videoLIsts.get(position).getVideo();
                            viewHolder.str_videoName = videoLIsts.get(position).getTitle();
                            viewHolder.str_videoId = videoLIsts.get(position).getVid();

                            viewHolder.videoTempWrite = new File(dirtemp, viewHolder.str_videoName + ".txt");

                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context.getApplicationContext(),
                                            "Downloading started..",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                            new DownloadFile().execute(viewHolder.str_videoUrl);
                            new DownloadVideo().execute();
                            new ViewVideo().execute();
                        } else {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    final Dialog dialog = new Dialog(context);
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
                                        }
                                    });
                                    dialog.show();
                                }
                            });
                        }
                    }
                }).start();
            }

            class DownloadFile extends AsyncTask<String, String, String> {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        public void run() {
                            showProgress(viewHolder.str_videoUrl);
                        }
                    });
                }

                @Override
                protected String doInBackground(String... strings) {
                    try {
                        viewHolder.downloading = Ion.with(context)
                                .load(viewHolder.str_videoUrl)
                                .progressBar(viewHolder.progress_bar)
                                .progressHandler(new ProgressCallback() {
                                    @Override
                                    public void onProgress(final long downloaded, final long total) {
                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            @Override
                                            public void run() {
                                                float per = ((float) downloaded / total) * 100;
                                                viewHolder.cur_pg_tv.setText((int) per + "%");
                                            }
                                        });
                                    }
                                })
                                .write(viewHolder.videoTempWrite)
                                .setCallback(new FutureCallback<File>() {
                                    @Override
                                    public void onCompleted(Exception e, File result) {
                                        if (e != null) {
                                            try {
                                                viewHolder.rl_progress.setVisibility(View.GONE);
                                                viewHolder.iv_download.setVisibility(View.VISIBLE);

                                                currptvideo();

                                                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                                                vibrator.vibrate(1000);
                                                Toast.makeText(context, "downloading Canceled.", Toast.LENGTH_LONG).show();
                                                return;
                                            } catch (Exception e1) {
                                                e1.printStackTrace();
                                            }
                                        }
                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            public void run() {
                                                viewHolder.rl_progress.setVisibility(View.GONE);
                                                viewHolder.iv_download.setVisibility(View.GONE);
                                                viewHolder.iv_play.setVisibility(View.VISIBLE);

                                                File newFile = new File(dir, viewHolder.videoTempWrite.getName());
                                                FileChannel output = null;
                                                FileChannel input = null;

                                                try {
                                                    output = new FileOutputStream(newFile).getChannel();
                                                    input = new FileInputStream(viewHolder.videoTempWrite).getChannel();
                                                    input.transferTo(0, input.size(), output);

                                                    input.close();
                                                } catch (FileNotFoundException e1) {
                                                    e1.printStackTrace();
                                                } catch (IOException e1) {
                                                    e1.printStackTrace();
                                                }

                                                File newVideo = new File(dir, viewHolder.str_videoName + ".mp4");
                                                videoTemp.renameTo(newVideo);

                                                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                                                vibrator.vibrate(1000);

                                                CustomNotification(viewHolder.str_videoName);
                                                Toast.makeText(context, "Download Complete..", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                    } catch (final Exception e) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                showError();
                            }
                        });
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    notifyDataSetChanged();
                    notifyItemChanged(position);
                }
            }

            //Custom Notification call when video download complete
            private void CustomNotification(String name) {
                notifyDataSetChanged();
                notifyItemChanged(position);

                ChangeUI();
                DeleteFiles();

                Random random = new Random();
                String strtitle = name + " (Mike & Mia)";
                String strtext = "Downloading Completed.";

                // Open NotificationView Class on Notification Click
                Intent intent = new Intent(context.getApplicationContext(), Home_Activity.class);
                // Send data to NotificationView Class
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                intent.putExtra("title", strtitle);
                intent.putExtra("text", strtext);

                // Open NotificationView.java Activity
                PendingIntent pIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, intent,
                        0);

                //Create Notification using NotificationCompat.Builder
                NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(context.getApplicationContext())
                        // Set Icon
                        .setSmallIcon(R.mipmap.ic_launcher)
                        // Set Title
                        .setContentTitle(strtitle)
                        // Set Text
                        .setContentText(strtext)
                        // Set PendingIntent into Notification
                        .setContentIntent(pIntent)
                        // Dismiss Notification
                        .setAutoCancel(true);

                Notification notification = builder.build();
                notification.defaults = Notification.DEFAULT_VIBRATE;
                notification.defaults = Notification.DEFAULT_SOUND;
                notification.contentIntent = pIntent;

                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                int m = random.nextInt(9999 - 1000) + 1000;
                manager.notify(m, notification);
            }

            //Delete currept or un completed video file from temp or youkids folder
            void showError() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    public void run() {
                        notifyDataSetChanged();
                        notifyItemChanged(position);

                        viewHolder.rl_progress.setVisibility(View.GONE);
                        viewHolder.iv_play.setVisibility(View.GONE);
                        viewHolder.iv_download.setVisibility(View.VISIBLE);

                        currptvideo();

                        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(1000);
                        Toast.makeText(context, "downloading Canceled.", Toast.LENGTH_LONG).show();
                    }
                });
            }

            //Update UI in Recyclerview after complete download video file
            private void ChangeUI() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                        notifyItemChanged(position);
                        ((Home_Activity) context).refresh();
                    }
                });
            }

            private void showProgress(String str_videoUrl) {
                notifyDataSetChanged();
                notifyItemChanged(position);
                if (viewHolder.rl_progress.getVisibility() == View.GONE) {
                    viewHolder.rl_progress.setVisibility(View.VISIBLE);
                    viewHolder.iv_download.setVisibility(View.GONE);
                    viewHolder.cur_pg_tv.setText("Wait...");
                    viewHolder.progress_bar.setProgress(0);
                }
            }

            //Update video download count in backend
            class DownloadVideo extends AsyncTask<String, String, String> {

                @Override
                protected String doInBackground(String... strings) {
                    try {
                        OkHttpClient client = new OkHttpClient();

                        FormBody.Builder builder = new FormBody.Builder()
                                .add("videosid", viewHolder.str_videoId);

                        RequestBody requestBody = builder.build();

                        Request request = new Request.Builder().url(AppConstant.videoDownload).post(requestBody).build();

                        Response response = client.newCall(request).execute();
                        Log.e("Responce", "" + response);
                        Log.e("Download Video : ", response.body().string());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }

            //Update video view count in backend
            class ViewVideo extends AsyncTask<String, String, String> {

                @Override
                protected String doInBackground(String... strings) {
                    try {
                        OkHttpClient client = new OkHttpClient();

                        FormBody.Builder builder = new FormBody.Builder()
                                .add("videosid", viewHolder.str_videoId);

                        RequestBody requestBody = builder.build();

                        Request request = new Request.Builder().url(AppConstant.videoViews).post(requestBody).build();

                        Response response = client.newCall(request).execute();
                        Log.e("Responce", "" + response);
                        Log.e("View Videos : ", response.body().string());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }

            //Delete .txt file from folder while connection lost or kill application from background
            private void currptvideo() {
                try {
                    File file = new File(context.getExternalFilesDir(null).getAbsolutePath()
                            + "/.YouKids/" + viewHolder.str_videoName + ".txt");
                    Uri uri = Uri.fromFile(file);

                    File deleteFile = new File(uri.getPath());
                    deleteFile.delete();

                    if (deleteFile.exists()) {
                        deleteFile.getCanonicalFile().delete();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //Button - Play video in media player
        viewHolder.iv_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(context.getExternalFilesDir(null).getAbsolutePath()
                        + "/.YouKids/" + viewHolder.str_videoName + ".mp4");
                Uri uri = Uri.fromFile(file);

                VideoHome_Fragment fragment = new VideoHome_Fragment();
                Bundle bundle = new Bundle();
                bundle.putString("name", viewHolder.str_videoName);
                bundle.putString("video", uri.toString());
                fragment.setArguments(bundle);

                fragment.showFragment(((AppCompatActivity) context).getSupportFragmentManager());
            }
        });
    }

    //Delete file from folder
    public void DeleteFiles() {
        File SDCard, dir;

        SDCard = context.getExternalFilesDir(null);
        dir = new File(SDCard.getAbsolutePath() + "/.YouKids/");
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

    @Override
    public int getItemCount() {
        return videoLIsts.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
