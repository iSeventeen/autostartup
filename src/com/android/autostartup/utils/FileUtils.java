package com.android.autostartup.utils;

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

    private static final String PROJECT_EXTERNAL_DIR = EXTERNAL_DIR + "/Autostartup/";
    private static final String VIDEO_EXTERNAL_DIR = PROJECT_EXTERNAL_DIR + "video/";
    private static final String AUDIO_EXTERNAL_DIR = PROJECT_EXTERNAL_DIR + "audio/";
    private static final String HOME_BG_EXTERNAL_DIR = PROJECT_EXTERNAL_DIR + "bg/";
    private static final String PICS_EXTERNAL_DIR = PROJECT_EXTERNAL_DIR + "pictures/";

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
        String videoPath = VIDEO_EXTERNAL_DIR + "video.mp4";
        if (!new File(videoPath).exists()) {
            videoPath = "android.resource://" + context.getPackageName() + "/" + R.raw.video;
        }
        return videoPath;
    }

    public static MediaPlayer getAudioPlayer(Context context) {
        String audioPath = AUDIO_EXTERNAL_DIR + "audio.mp3";
        if (new File(audioPath).exists()) {
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

    public static Bitmap getPictureurlImg(String pictureurl) throws IOException {
        URL url = new URL(pictureurl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5 * 1000);
        InputStream in = conn.getInputStream();
        Bitmap bm = BitmapFactory.decodeStream(in);
        // 保存本地图片
        String fileName = getFileName(pictureurl);
        savePicToCard(bm, fileName);
        in.close();
        return bm;

    }

    /**
     * 获取网络图片名称
     * 
     * @param pictureurl
     * @return
     */
    public static String getFileName(String pictureurl) {

        String regstr = "(http:|https:)\\/\\/[\\S\\.:/]*\\/(\\S*)\\.(jpg|png|gif)";
        String postfix = "", filename = "";
        Pattern patternForImg = Pattern.compile(regstr, Pattern.CASE_INSENSITIVE);
        Matcher matcher = patternForImg.matcher(pictureurl);
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
        // this.downloadpcurl = onlineFilePath;
    }

    public static void loadAndSavePic(Context context, String url) {
        try {
            Bitmap bitmap = Picasso.with(context).load(url).get();
            FileUtils.savePicToCard(bitmap, getFileName(url));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
