package com.android.autostartup.utils;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.http.util.EncodingUtils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class Utils {
    
    public static final String WHOLE_FORMAT_STRING = "yyyy-MM-dd HH:dd:mm:ss";
    public static final String DATE_FORMAT_STRING = "yyyy-MM-dd";

    public static void DisplayError(Context context, int resourceId) {
        AlertDialog.Builder b = new AlertDialog.Builder(context);
        b.setTitle("Error");
        b.setMessage(resourceId);
        b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // MainActivity.this.finish();
            }
        });
        b.show();
    }

    public static String parseHexData(String data) {
        StringBuffer sb = new StringBuffer();
        String string = data.replaceAll("\\s{1,}", "");
        if (string.startsWith("02") && string.endsWith("0D0A03")) {
            String value = string.substring(2, string.length() - 6);
            for (int i = 0; i < value.length(); i++) {
                if (i % 2 != 0) {
                    sb.append(value.charAt(i));
                }
            }
        }
        return sb.toString();
    }

    // fetch file from assets fold
    public String getFromAssets(Context context, String fileName) {
        String result = "";
        try {
            InputStream in = context.getResources().getAssets().open(fileName);
            int length = in.available();
            byte[] buffer = new byte[length];
            in.read(buffer);
            result = EncodingUtils.getString(buffer, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String formatDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return sdf.format(new Date());
    }

    public static String formatDate(String formatString, Date value) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatString, Locale.CHINA);
        return sdf.format(value);
    }
    public static String formatDate(String formatString, long value) {
        Date date = new Date(value);
        SimpleDateFormat sdf = new SimpleDateFormat(formatString, Locale.CHINA);
        return sdf.format(date);
    }

    public static Date parseDate(String value) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:dd:mm:ss", Locale.CHINA);
            return sdf.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }
}
