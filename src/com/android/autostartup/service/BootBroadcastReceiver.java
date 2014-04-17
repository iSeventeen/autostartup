package com.android.autostartup.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.android.autostartup.activity.SplashActivity;

public class BootBroadcastReceiver extends BroadcastReceiver {

    static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)) {

            Intent mainIntent = new Intent(context, SplashActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mainIntent);
        }
    }

}
