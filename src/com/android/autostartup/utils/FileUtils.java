package com.android.autostartup.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.android.autostartup.R;
import com.squareup.picasso.Picasso;

public class FileUtils {

    private static final String TAG = "FileUtils";

    private static final String EXTERNAL_DIR = Environment.getExternalStorageDirectory()
            .getAbsolutePath();

    public static final String PROJECT_EXTERNAL_DIR = EXTERNAL_DIR + "/Autostartup/";
    public static final String VIDEO_EXTERNAL_DIR = PROJECT_EXTERNAL_DIR + "video/";
    public static final String AUDIO_EXTERNAL_DIR = PROJECT_EXTERNAL_DIR + "audio/";
    public static final String HOME_BG_EXTERNAL_DIR = PROJECT_EXTERNAL_DIR + "bg/";
    public static final String PICS_EXTERNAL_DIR = PROJECT_EXTERNAL_DIR + "picture/";

    public static final String FILE_VIDEO_PATH = VIDEO_EXTERNAL_DIR + "video.mp4";
    public static final String FILE_AUDIO_PATH = AUDIO_EXTERNAL_DIR + "audio.mp3";

    public static void createFolders(Context context) {
        createProjectPath();
        createVideoFolder(context);
        createAudioFolder(context);
    }

    public static void createProjectPath() {
        createPath(PROJECT_EXTERNAL_DIR);
    }

    public static void createVideoFolder(Context context) {
        createPath(VIDEO_EXTERNAL_DIR);
    }

    public static void createAudioFolder(Context context) {
        createPath(AUDIO_EXTERNAL_DIR);
    }

    public static String getProjectPath() {
        return PROJECT_EXTERNAL_DIR;
    }

    public static String getVideoPath(Context context) {
        String videoPath = FILE_VIDEO_PATH;
        if (!new File(videoPath).exists()) {
            videoPath = "android.resource://" + context.getPackageName() + "/" + R.raw.video;
        }
        Log.i(TAG, "video path:" + videoPath);
        return videoPath;
    }

    public static String getAudioPath(Context context) {
        String audioPath = FILE_AUDIO_PATH;
        if (!new File(audioPath).exists()) {
            audioPath = "android.resource://" + context.getPackageName() + "/" + R.raw.audio;
        }
        Log.i(TAG, "audio path:" + audioPath);
        return audioPath;
    }

    public static MediaPlayer getAudioPlayer(Context context) {
        String audioPath = FILE_AUDIO_PATH;
        if (new File(audioPath).exists()) {
            Log.i(TAG, "audio path:" + audioPath);
            return MediaPlayer.create(context, Uri.parse(audioPath));
        } else {
            return MediaPlayer.create(context, R.raw.audio);
        }
    }

    public static void createPathByCardNo(Context context, String cardNumber) {
        String dir = PROJECT_EXTERNAL_DIR + cardNumber;
        if (createPath(dir)) {
            readRawSource(context, dir + "/" + "child.jpg", R.raw.child);
            readRawSource(context, dir + "/" + "parent.jpg", R.raw.parent);
        }
    }

    public static void readRawSource(Context context, String path, int source) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                InputStream inputStream = context.getResources().openRawResource(source);

                FileOutputStream fileOutputStream = new FileOutputStream(file);
                byte[] buffer = new byte[8192];
                int count = 0;
                while ((count = inputStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, count);
                }
                fileOutputStream.close();
                inputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static boolean hasSdcard() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean createPath(String directory) {
        if (hasSdcard()) {
            File file = new File(directory);

            if (!file.exists()) {
                file.mkdirs();
            }
            return true;
        } else {
            // do nothing
            Log.i(TAG, "no sdcard");
            return false;
        }
    }

    public static void loadAndSaveVideo(String srcUrl) throws IOException {
        loadMediaFile(srcUrl, VIDEO_EXTERNAL_DIR);
    }

    public static void loadAndSaveAudio(String srcUrl) throws IOException {
        loadMediaFile(srcUrl, AUDIO_EXTERNAL_DIR);
    }

    public static void loadMediaFile(String srcUrl, String dirPath) throws IOException {

        InputStream in = getInputStream(srcUrl);

        write2SD(dirPath, srcUrl, in);

        in.close();
    }

    public static void loadAndSavePic(String srcUrl) throws IOException {
        InputStream in = getInputStream(srcUrl);
        Bitmap bm = BitmapFactory.decodeStream(in);

        savePicToCard(bm, getFileNameFromUrl(srcUrl));

        in.close();
    }

    public static InputStream getInputStream(String srcUrl) throws IOException {
        URL url = new URL(srcUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5 * 1000);

        return conn.getInputStream();
    }

    public static void write2SD(String dirPath, String url, InputStream input) {
        try {
            File file = new File(dirPath, getFileNameFromUrl(url));
            BufferedInputStream bis = new BufferedInputStream(input);

            ByteArrayBuffer baf = new ByteArrayBuffer(5000);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baf.toByteArray());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            Log.e(TAG, "write2SD:" + e.getMessage());
        }
    }

    public static String getFileNameFromUrl(String url) {

        String regstr = "(http:|https:)\\/\\/[\\S\\.:/]*\\/(\\S*)\\.(jpg|png|gif|mp4|mp3)";
        String postfix = "", filename = "";
        Pattern patternForImg = Pattern.compile(regstr, Pattern.CASE_INSENSITIVE);
        Matcher matcher = patternForImg.matcher(url);
        if (matcher.find()) {
            filename = matcher.group(2);
            postfix = matcher.group(3);
        }
        return filename + "." + postfix;
    }

    public static void savePicToCard(Bitmap bm, String fileName) throws IOException {
        File dirFile = new File(PICS_EXTERNAL_DIR);
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
        String onlineFilePath = PICS_EXTERNAL_DIR + fileName;
        File myOnlineFile = new File(onlineFilePath);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myOnlineFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        bos.flush();
        bos.close();
    }

    public static void loadAndSavePic(Context context, String url) {
        try {
            Bitmap bitmap = Picasso.with(context).load(url).get();
            FileUtils.savePicToCard(bitmap, getFileNameFromUrl(url));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
