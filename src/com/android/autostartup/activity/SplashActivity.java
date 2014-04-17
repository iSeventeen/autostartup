package com.android.autostartup.activity;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.android.autostartup.R;
import com.android.autostartup.controller.server.Server;
import com.android.autostartup.service.DBSyncService;
import com.android.autostartup.service.PollingUtils;
import com.android.autostartup.utils.FileUtils;

public class SplashActivity extends Activity {

    private static final String TAG = SplashActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
     // hide title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // set full screen
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);

        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onStart() {
        super.onStart();
        PollingUtils.startPollingService(this, 20, DBSyncService.class,
                DBSyncService.ACTION);

        new LoadDataTask().execute();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    private class LoadDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // TODO Add loading progress...
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                // load background of home screen
                String srcUrl = Server.BASE_URL + "assets/files/picture/bg.png";
                if (new File(srcUrl).exists()) {
                    FileUtils.loadAndSavePic(srcUrl);
                }
                // TODO load xml file(e.g. setting garten info)

                // load audio
                srcUrl = Server.BASE_URL + "assets/files/audio/audio.mp3";
                if (new File(srcUrl).exists()) {
                    FileUtils.loadAndSaveAudio(srcUrl);
                }

                // load video
                srcUrl = Server.BASE_URL + "assets/files/video/video.mp4";
                if (new File(srcUrl).exists()) {
                    FileUtils.loadAndSaveVideo(srcUrl);
                }

            } catch (IOException e) {
                Log.e(TAG, "load data failed!");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            SplashActivity.this.finish();
        }
    }
}
