package com.example.swapnil.youkids.support;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Swapnil.Patel on 10-10-2017.
 */

public class PrefManger {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "Mike";
    public final String APP_PROCESS_ID = "APP_PROCESS_ID";
    public final String First = "delete";

    public PrefManger(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void ProcessId(int id, String s1) {
        editor.putInt(APP_PROCESS_ID, android.os.Process.myPid());
        editor.putString(First, s1);
        editor.commit();
    }
}
