package com.example.swapnil.youkids.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.swapnil.youkids.R;

/**
 * Created by sambh on 25-10-2017.
 */

public class Menu_Fragment extends Fragment {
    public static final String TAG = Menu_Fragment.class.getSimpleName();
    View rootView;

    FragmentManager fragmentManager;
    private AppCompatActivity activity;

    LinearLayout lin_contact, lin_about, lin_policy, lin_download;

    public Menu_Fragment() {
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
        rootView = inflater.inflate(R.layout.activity_menu, null);


        lin_contact = (LinearLayout) rootView.findViewById(R.id.lin_contact);
        lin_about = (LinearLayout) rootView.findViewById(R.id.lin_about);
        lin_policy = (LinearLayout) rootView.findViewById(R.id.lin_policy);
        lin_download = (LinearLayout) rootView.findViewById(R.id.lin_download);

        lin_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Contact_Fragment().showFragment(activity.getSupportFragmentManager());
            }
        });

        lin_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Aboutus_Fragment fragment = new Aboutus_Fragment();
                Bundle bundle = new Bundle();
                bundle.putString("id", "1");
                fragment.setArguments(bundle);
                fragment.showFragment(activity.getSupportFragmentManager());
            }
        });

        lin_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Aboutus_Fragment fragment = new Aboutus_Fragment();
                Bundle bundle = new Bundle();
                bundle.putString("id", "2");
                fragment.setArguments(bundle);
                fragment.showFragment(activity.getSupportFragmentManager());
            }
        });

        lin_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Mydownload_Fragment().showFragment(activity.getSupportFragmentManager());
            }
        });

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
        transaction.add(Window.ID_ANDROID_CONTENT, Menu_Fragment.this, "Menu");
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }
}
