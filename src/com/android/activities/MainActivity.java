package com.android.activities;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.android.autostartup.R;
import com.android.serialport.SerialPort;
import com.android.utils.FileUtils;
import com.android.utils.Utils;

@TargetApi(Build.VERSION_CODES.ECLAIR)
public class MainActivity extends Activity implements OnClickListener {

    private static final int DELAY_MILLIS = 5000;

    private RelativeLayout mAvatarLayout;
    private RelativeLayout mVideoLayout;

    private Button mVideoBtn;
    private Button mAvatarBtn;

    private TextView mHealthTextView;

    private VideoView mVideoView;
    private MediaPlayer mediaPlayer;

    private ImageView mParentImageView;
    private ImageView mChildImageView;

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

        FileUtils.createFolders(this);

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
        mVideoView.setVideoURI(Uri.parse(FileUtils.getVideoPath(this)));
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
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

        mChildImageView = (ImageView) findViewById(R.id.img_child);
        mParentImageView = (ImageView) findViewById(R.id.img_parent);
    }

    private void initMediaPlayer() {
        try {
            mediaPlayer = FileUtils.getAudioPlayer(this);
            // mediaPlayer.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void initVideoView() {
        mVideoView = (VideoView) findViewById(R.id.videoview);

        mVideoView.requestFocus();
    }

    private void updateView() {
        resetMediaPlayer();

        if (null != mVideoView) {
            showOrHide(true);
            mVideoView.start();
        } else {
            // TODO
        }
    }

    private void resetMediaPlayer() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.reset();
            initMediaPlayer();
        }
    }

    private void onDataReceived(final byte[] buffer, final int size) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                handler.removeCallbacks(runnable);
                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                }
                resetMediaPlayer();

                String hexData = new String(buffer, 0, size);
                mHealthTextView.setText(hexData);
                updateImageView(hexData);

                showOrHide(false);
                mediaPlayer.start();

                handler.postDelayed(runnable, DELAY_MILLIS);
            }
        });
    }

    private void updateImageView(String hexData) {
        String basePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/Autostartup" + "/";
        String cardNumber = Utils.parseHexData(hexData);
        FileUtils.createPathByCardNo(MainActivity.this, cardNumber);
        Bitmap parentBitmap = BitmapFactory.decodeFile(basePath + cardNumber + "/parent.jpg");
        mParentImageView.setImageBitmap(parentBitmap);
        Bitmap childBitmap = BitmapFactory.decodeFile(basePath + cardNumber + "/child.jpg");
        mChildImageView.setImageBitmap(childBitmap);
    }

    private void showOrHide(boolean isShowVideo) {
        mAvatarLayout.setVisibility(isShowVideo ? View.GONE : View.VISIBLE);
        mVideoLayout.setVisibility(isShowVideo ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View view) {
        if (view == mVideoBtn) {
            if (null != mVideoView) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.reset();
                    initMediaPlayer();
                }
                showOrHide(true);
                mVideoView.start();
            }
        } else if (view == mAvatarBtn) {
            handler.removeCallbacks(runnable);
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.reset();
                initMediaPlayer();
            }
            if (mVideoView.canPause()) {
                mVideoView.pause();
            }

            // ------------------------------------------------------------
            String hexData = "02 30 30 30 38 38 34 36 34 38 36 0D 0A 03";
            updateImageView(hexData);
            // ------------------------------------------------------------

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
        release();

        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            release();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void release() {
        if (mReadThread != null)
            mReadThread.interrupt();
        mApplication.closeSerialPort();
        mSerialPort = null;

        /*
        if (mVideoView.isPlaying()) {
            mVideoView.stopPlayback();
        }

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.release();
         */
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
