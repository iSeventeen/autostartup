package com.android.autostartup.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class DBSyncService extends Service {

    private static final String TAG = DBSyncService.class.getSimpleName();

    public static final String ACTION = "com.android.autostartup.service.DBSyncService";

    private SyncStudentsFromServer studentService;

    @Override
    public IBinder onBind(Intent arg0) {
        Log.i(TAG, "onBind...");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate...");
        studentService = new SyncStudentsFromServer(getApplicationContext());
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.i(TAG, "onStart...");
        studentService.updateStudentDataFromServer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy...");
    }

}
