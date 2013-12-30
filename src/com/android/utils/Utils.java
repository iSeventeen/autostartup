package com.android.utils;

import java.io.InputStream;

import org.apache.http.util.EncodingUtils;

import android.content.Context;

public class Utils {

    public static String parseHexData(String data) {
        StringBuffer sb = new StringBuffer();

        if (data.startsWith("02") && data.endsWith("03")) {
            String[] strings = data.replaceAll("\\s{1,}", " ").split(" ");
            for (int i = 1; i < strings.length - 1; i++) {
                sb.append(Integer.parseInt(strings[i], 16));
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
}
