package com.android.activities;

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
}
