package com.android.autostartup.activity;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.android.autostartup.R;
import com.android.autostartup.app.Application;
import com.android.autostartup.controller.StudentController;
import com.android.autostartup.controller.StudentController.StudentUpdateCallback;
import com.android.autostartup.controller.server.Server;
import com.android.autostartup.model.Parent;
import com.android.autostartup.model.Student;
import com.android.autostartup.serialport.SerialPort;
import com.android.autostartup.utils.FileUtils;
import com.android.autostartup.utils.Utils;
import com.squareup.picasso.Picasso;

@TargetApi(Build.VERSION_CODES.ECLAIR)
public class MainActivity_backup extends Activity implements StudentUpdateCallback, OnClickListener {

    private static final int DELAY_MILLIS = 5000;

    private RelativeLayout mDetailInfoLayout;
    private RelativeLayout mVideoLayout;

    private LinearLayout mParentsLayout;
    private ImageView mStudentImageView;

    private TextView mNameTextView;
    private TextView mNumberTextView;
    private TextView mClassTextView;
    private TextView mTimeTextView;

    private Button mVideoBtn;
    private Button mAvatarBtn;

    private VideoView mVideoView;
    private MediaPlayer mediaPlayer;

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
    private SerialPort mSerialPort = null;
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
        // TODO
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        initSerialPort();
        initView();
        StudentController.addStudentUpdateCallback(this);
        StudentController.getStudentInformation("1234567890");
    }

    @Override
    protected void onStart() {
        String url = FileUtils.getVideoPath(MainActivity_backup.this);
        mVideoView.setVideoURI(Uri.parse(url));
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mVideoView.setVideoURI(Uri.parse(FileUtils.getVideoPath(MainActivity_backup.this)));
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
        mDetailInfoLayout = (RelativeLayout) findViewById(R.id.layout_student_info);
        mVideoLayout = (RelativeLayout) findViewById(R.id.layout_video);
    }

    private void initBtns() {
        mVideoBtn = (Button) findViewById(R.id.btn_video);
        mVideoBtn.setOnClickListener(this);
        mAvatarBtn = (Button) findViewById(R.id.btn_avatar);
        mAvatarBtn.setOnClickListener(this);

        // -------------------------------------------------------------------
        mNameTextView = (TextView) findViewById(R.id.text_welcome);

        // -------------------------------------------------------------------
        mNumberTextView = (TextView) findViewById(R.id.text_student_number);
        mClassTextView = (TextView) findViewById(R.id.text_student_class);
        mTimeTextView = (TextView) findViewById(R.id.text_student_register_time);

        // -------------------------------------------------------------------
        mParentsLayout = (LinearLayout) findViewById(R.id.layout_parent_avatar);
        mStudentImageView = (ImageView) findViewById(R.id.img_student);

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

    private String hexData;

    private void onDataReceived(final byte[] buffer, final int size) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                handler.removeCallbacks(runnable);
                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                }
                resetMediaPlayer();

                hexData = new String(buffer, 0, size);

                StudentController.getStudentInformation(hexData);
            }
        });
    }

    /*
     * private ImageView mParentImageView; private ImageView mChildImageView;
     * private void updateImageView2(String hexData) { String basePath =
     * FileUtils.getProjectPath() + "/"; String cardNumber =
     * Utils.parseHexData(hexData); if (null != cardNumber) {
     * FileUtils.createPathByCardNo(MainActivity.this, cardNumber); Bitmap
     * parentBitmap = BitmapFactory.decodeFile(basePath + cardNumber +
     * "/parent.jpg"); mParentImageView.setImageBitmap(parentBitmap); Bitmap
     * childBitmap = BitmapFactory.decodeFile(basePath + cardNumber +
     * "/child.jpg"); mChildImageView.setImageBitmap(childBitmap); } }
     */

    private void showOrHide(boolean isShowVideo) {
        mDetailInfoLayout.setVisibility(isShowVideo ? View.GONE : View.VISIBLE);
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
            // updateImageView(hexData);
            StudentController.getStudentInformation(hexData.replaceAll(" ", ""));
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
        StudentController.removeStudentUpdateCallback(this);
        Application.getRequestQueue().cancelAll(Server.TAG);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            release();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void release() {
        if (mReadThread != null) {
            mReadThread.interrupt();
        }
        mApplication.closeSerialPort();
        mSerialPort = null;

        /*
         * if (mVideoView.isPlaying()) { mVideoView.stopPlayback(); }
         * 
         * if (mediaPlayer.isPlaying()) { mediaPlayer.stop(); }
         * mediaPlayer.release();
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

    @Override
    public void updateStudent(Student student) {
        // updateImageView(hexData);
        updateViews(student);

        showOrHide(false);
        mediaPlayer.start();

        // TODO
        // handler.postDelayed(runnable, DELAY_MILLIS);
    }

    private void updateViews(Student student) {
        // ----------------------------------------------------------------------------
        mNameTextView.setText(getString(R.string.welcome, student.name));
        mNumberTextView.setText(getString(R.string.student_number, student.cardId.substring(0, 9)));
        mClassTextView.setText(getString(R.string.student_class, "豆豆班"));
        mTimeTextView.setText(getString(R.string.student_register_time, Utils.formatDate()));

        Picasso.with(this).load(Server.BASE_URL + student.avatar).into(mStudentImageView);

        // -----------------------------------------------------------------------------
        mParentsLayout.removeAllViews();
        /*
        for (Parent parent : student.parents) {
            String imgUrl = parent.avatar;
            ImageView imageView = new ImageView(this);
            imageView.setBackgroundResource(R.drawable.parent);
            int width = 180;
            int height = 105;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
            params.bottomMargin = 5;
            imageView.setLayoutParams(params);
            mParentsLayout.addView(imageView);
        }
        */
    }
}
