package com.android.autostartup.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

public class DBSyncService extends Service {

    private static final String TAG = DBSyncService.class.getSimpleName();

    public static final String ACTION = "com.android.autostartup.service.DBSyncService";

    private SyncStudentsFromServer studentService;
    private SyncParentFromServer parentService;

    private Context context;

    @Override
    public IBinder onBind(Intent arg0) {
        Log.i(TAG, "onBind...");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate...");
        context = getApplicationContext();
        studentService = new SyncStudentsFromServer(context);
        parentService = new SyncParentFromServer(context);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.i(TAG, "onStart...");
        new UpdateStudentTask().execute();
        new UpdateParentTask().execute();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy...");
    }

    private class UpdateStudentTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            studentService.updateStudentDataFromServer();
            return null;
        }
    }

    private class UpdateParentTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            parentService.updateParentDataFromServer();
            return null;
        }
    }

}
