package com.example.swapnil.youkids.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.VideoBitmapDecoder;
import com.example.swapnil.youkids.R;
import com.example.swapnil.youkids.fragment.Video_Fragment;
import com.example.swapnil.youkids.kids.Home_Activity;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by Swapnil.Patel on 17-08-2017.
 */

public class MyVideoAdapter extends RecyclerView.Adapter<MyVideoAdapter.ViewHolder> {

    Context context;
    List<String> filelist;
    String videoPath;


    public MyVideoAdapter(Context context, List<String> filelist, String videoPath) {
        this.context = context;
        this.filelist = filelist;
        this.videoPath = videoPath;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public ImageView imageview, iv_more, iv_play;
        String name;
        CardView card_view;

        public ViewHolder(View view) {
            super(view);

            this.title = (TextView) view.findViewById(R.id.tv_title);
            this.imageview = (ImageView) view.findViewById(R.id.coverImageView);

            this.iv_more = (ImageView) view.findViewById(R.id.iv_more);
            this.iv_play = (ImageView) view.findViewById(R.id.iv_play);
            this.card_view = (CardView) view.findViewById(R.id.card_view);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_mydownload, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        Glide.with(context)
                .asBitmap()
                .load(Uri.fromFile(new File(videoPath + filelist.get(position))))
                .thumbnail(0.1f)
                .into(holder.imageview);

        StringTokenizer tokenizer = new StringTokenizer(filelist.get(position), ".");
        holder.name = tokenizer.nextToken();
        holder.title.setText(holder.name);


        holder.iv_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(context.getExternalFilesDir(null).getAbsolutePath()
                        + "/.YouKids/" + holder.name + ".mp4");
                Uri uri = Uri.fromFile(file);

                Video_Fragment fragment = new Video_Fragment();
                Bundle bundle = new Bundle();
                bundle.putString("name", holder.name);
                bundle.putString("video", uri.toString());
                bundle.putInt("position", position);
                bundle.putString("close", "video");
                fragment.setArguments(bundle);

                fragment.showFragment(((AppCompatActivity) context).getSupportFragmentManager());
            }
        });

        holder.iv_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu menu = new PopupMenu(context, view);
                MenuInflater inflater = menu.getMenuInflater();
                inflater.inflate(R.menu.menu_mydownload, menu.getMenu());

                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_delete:
                                try {
                                    File file = new File(context.getExternalFilesDir(null).getAbsolutePath()
                                            + "/.YouKids/" + holder.name + ".mp4");
                                    Uri uri = Uri.fromFile(file);

                                    File deleteFile = new File(uri.getPath());
                                    deleteFile.delete();
                                    Toast.makeText(context, holder.name + " video file has been deleted. ", Toast.LENGTH_SHORT).show();
                                    ((Home_Activity) context).refresh();

                                    filelist.remove(position);
                                    notifyItemRemoved(position);
                                    notifyDataSetChanged();

                                    if (deleteFile.exists()) {
                                        deleteFile.getCanonicalFile().delete();
                                        if (deleteFile.exists()) {
                                            context.deleteFile(deleteFile.getName());
                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                return true;

                            default:
                        }
                        return false;
                    }
                });
                menu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return filelist.size();
    }
}
