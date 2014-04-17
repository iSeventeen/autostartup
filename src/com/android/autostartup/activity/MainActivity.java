package com.android.autostartup.activity;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import com.android.autostartup.dao.ParentDao;
import com.android.autostartup.dao.StudentDao;
import com.android.autostartup.model.Parent;
import com.android.autostartup.model.Student;
import com.android.autostartup.serialport.SerialPort;
import com.android.autostartup.service.DBSyncService;
import com.android.autostartup.service.PollingUtils;
import com.android.autostartup.utils.FileUtils;
import com.android.autostartup.utils.Utils;

@TargetApi(Build.VERSION_CODES.ECLAIR)
public class MainActivity extends Activity implements OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

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
    private VideoView mCameraVideo;
    private MediaPlayer mediaPlayer;

    private int positionWhenPaused = -1;

    private StudentDao studentDao;
    private ParentDao parentDao;

    private String hexData;

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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        studentDao = new StudentDao(this);
        parentDao = new ParentDao(this);

        List<Student> students = studentDao.getAll();
        for (Student student : students) {
            Log.i("MainActivity>all>students", student.toString());
        }
        List<Parent> parents = parentDao.getAll();
        for (Parent parent : parents) {
            Log.i("MainActivity>all>parents", parent.toString());
        }

        initSerialPort();
        initView();
        updateViews("1234560");
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupVideoView();
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
        mediaPlayer = FileUtils.getAudioPlayer(this);

        // mediaPlayer.prepare();
    }

    private void initVideoView() {
        mVideoView = (VideoView) findViewById(R.id.videoview);
        mVideoView.requestFocus();

        mCameraVideo = (VideoView) findViewById(R.id.cctv);
        mCameraVideo.setVideoPath(FileUtils.getVideoPath(MainActivity.this));
        mCameraVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mCameraVideo.setVideoPath(FileUtils.getVideoPath(MainActivity.this));
                mCameraVideo.start();
            }
        });
    }

    private void setupVideoView() {
        final String url = FileUtils.getVideoPath(MainActivity.this);
        mVideoView.setVideoPath(url);
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                // mVideoView.setVideoURI(Uri.parse(FileUtils.getVideoPath(MainActivity.this)));
                mVideoView.setVideoPath(url);
                mVideoView.start();
            }
        });
        mVideoView.start();
    }

    private void updateView() {
        resetMediaPlayer();
        if (null != mCameraVideo && mCameraVideo.isPlaying()) {
            mCameraVideo.stopPlayback();
        }

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
                if (null != mVideoView && mVideoView.isPlaying()) {
                    mVideoView.pause();
                }
                resetMediaPlayer();

                hexData = new String(buffer, 0, size);
                updateViews(hexData);
            }
        });
    }

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

                if (null != mCameraVideo && mCameraVideo.isPlaying()) {
                    mCameraVideo.stopPlayback();
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

            if (null != mVideoView && mVideoView.canPause()) {
                mVideoView.pause();
            }

            // ------------------------------------------------------------
            String hexData = "02 30 30 30 38 38 34 36 34 38 36 0D 0A 03";
            updateViews("1234561");
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
        super.onDestroy();
        Log.i(TAG, "onDestroy...");
        release();
        // TODO need check!!
        if (null != mediaPlayer) {
            mediaPlayer.release();
        }

        PollingUtils.stopPollingService(this, DBSyncService.class, DBSyncService.ACTION);
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

    public void updateViews(String cardId) {

        Parent parent = parentDao.getByCardId(cardId);
        if (null != parent) {
            // TODO REFACTOR
            List<Parent> parents = parentDao.getByStudentId(parent.student);
            Student student = studentDao.findById(parent.student);
            updateViews(student, parents);

            showOrHide(false);
            mediaPlayer.start();
            if (null != mCameraVideo && !mCameraVideo.isPlaying()) {
                mCameraVideo.start();
            }

            // TODO
            // handler.postDelayed(runnable, DELAY_MILLIS);
        }
    }

    private void updateViews(Student student, List<Parent> parents) {
        mNameTextView.setText(getString(R.string.welcome, student.name));
        mNumberTextView.setText(getString(R.string.student_number, "1111"));
        mClassTextView.setText(getString(R.string.student_class, "豆豆班"));
        mTimeTextView.setText(getString(R.string.student_register_time,
                Utils.formatDate(Utils.DATE_FORMAT_STRING, student.createdAt)));

        new LoadStudentPicTask().execute(student.avatar);

        // TODO
        mParentsLayout.removeAllViews();
        for (Parent parent : parents) {
            new LoadParentPicTask().execute(parent.avatar);
        }

    }

    private class LoadStudentPicTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            String filePath = FileUtils.PICS_EXTERNAL_DIR + params[0];

            return BitmapFactory.decodeFile(filePath);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mStudentImageView.setImageBitmap(bitmap);
        }
    }

    private class LoadParentPicTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            String filePath = FileUtils.PICS_EXTERNAL_DIR + params[0];

            return BitmapFactory.decodeFile(filePath);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            ImageView imageView = new ImageView(MainActivity.this);
            imageView.setImageBitmap(bitmap);
            int width = 180;
            int height = 105;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
            params.bottomMargin = 5;
            imageView.setLayoutParams(params);
            mParentsLayout.addView(imageView);
        }
    }
}
