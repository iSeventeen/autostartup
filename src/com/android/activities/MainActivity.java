package com.android.activities;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;

import org.apache.http.util.EncodingUtils;

import com.android.autostartup.R;
import com.android.serialport.SerialPort;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;


public class MainActivity extends Activity implements OnClickListener {

    private static final int DELAY_MILLIS = 5000;

    private RelativeLayout mAvatarLayout;
    private RelativeLayout mVideoLayout;

    private Button mVideoBtn;
    private Button mAvatarBtn;

    private TextView mHealthTextView;

    private VideoView mVideoView;
    private MediaPlayer mediaPlayer;

    private String videoPath;
    private int positionWhenPaused = -1;

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            updateView();
        }
    };

    // -------------------Serial------------------------
    private Application mApplication;
    private SerialPort mSerialPort;
    // private OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;

    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                int size;
                try {
                    byte[] buffer = new byte[64];
                    if (mInputStream == null)
                        return;
                    size = mInputStream.read(buffer);
                    if (size > 0) {
                        onDataReceived(buffer, size);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    // -------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // hide title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // set full screen
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        initSerialPort();
        initView();
    }

    @Override
    protected void onStart() {
        mVideoView.setVideoURI(Uri.parse(videoPath));
        // videoView.setMediaController(new MediaController(MainActivity.this));
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                // videoView.setVideoPath(videopath);
                mVideoView.start();
            }
        });
        mVideoView.start();
        super.onStart();
    }

    private void initSerialPort() {
        mApplication = (Application) getApplication();
        try {
            mSerialPort = mApplication.getSerialPort();
            // mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();

            mReadThread = new ReadThread();
            mReadThread.start();

        } catch (InvalidParameterException e) {
            DisplayError(R.string.error_configuration);
        } catch (SecurityException e) {
            DisplayError(R.string.error_security);
        } catch (IOException e) {
            DisplayError(R.string.error_unknown);
        }
    }

    private void initView() {
        initLayout();
        initBtns();
        initVideoView();
        initMediaPlayer();
    }

    private void initLayout() {
        mAvatarLayout = (RelativeLayout) findViewById(R.id.layout_avatar);
        mVideoLayout = (RelativeLayout) findViewById(R.id.layout_video);
    }

    private void initBtns() {
        mVideoBtn = (Button) findViewById(R.id.btn_video);
        mVideoBtn.setOnClickListener(this);
        mAvatarBtn = (Button) findViewById(R.id.btn_avatar);
        mAvatarBtn.setOnClickListener(this);

        mHealthTextView = (TextView) findViewById(R.id.health_content);
    }

    private void initMediaPlayer() {
        mediaPlayer = MediaPlayer.create(this, R.raw.ml);
    }

    private void initVideoView() {
        mVideoView = (VideoView) findViewById(R.id.videoview);
        // String path =
        // "http://rmcdn.2mdn.net/MotifFiles/html/1248596/android_1330378998288.mp4";
        videoPath = "android.resource://" + getPackageName() + "/" + R.raw.video;
        // videoView.setVideoPath(new File("/1.mp4").getAbsolutePath());

        mVideoView.requestFocus();
    }

    private void updateView() {
        if (null != mVideoView) {
            showOrHide(true);
            mVideoView.start();
        }
    }

    private void onDataReceived(final byte[] buffer, final int size) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                handler.removeCallbacks(runnable);
                if (mVideoView.canPause()) {
                    mVideoView.pause();
                }
                mHealthTextView.setText(new String(buffer, 0, size));
                showOrHide(false);
                mediaPlayer.start();

                handler.postDelayed(runnable, DELAY_MILLIS);
            }
        });
    }

    private void showOrHide(boolean isShowVideo) {
        mAvatarLayout.setVisibility(isShowVideo ? View.GONE : View.VISIBLE);
        mVideoLayout.setVisibility(isShowVideo ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View view) {
        if (view == mVideoBtn) {
            if (null != mVideoView) {
                showOrHide(true);
                mVideoView.start();
            }
        } else if (view == mAvatarBtn) {
            handler.removeCallbacks(runnable);

            if (mVideoView.canPause()) {
                mVideoView.pause();
            }

            showOrHide(false);
            mediaPlayer.start();
            handler.postDelayed(runnable, DELAY_MILLIS);
        }
    }

    @Override
    protected void onPause() {
        positionWhenPaused = mVideoView.getCurrentPosition();
        mVideoView.stopPlayback();
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (positionWhenPaused >= 0) {
            mVideoView.seekTo(positionWhenPaused);
            positionWhenPaused = -1;
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (mReadThread != null)
            mReadThread.interrupt();
        mApplication.closeSerialPort();
        mSerialPort = null;
        super.onDestroy();
    }

    // fetch file from assets fold
    public String getFromAssets(String fileName) {
        String result = "";
        try {
            InputStream in = getResources().getAssets().open(fileName);
            int length = in.available();
            byte[] buffer = new byte[length];
            in.read(buffer);
            result = EncodingUtils.getString(buffer, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private void DisplayError(int resourceId) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Error");
        b.setMessage(resourceId);
        b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // MainActivity.this.finish();
            }
        });
        b.show();
    }

}
