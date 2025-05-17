package top.ourfor.app.iplay.util;


import android.util.Base64;

public class Base64Util {

    public static String encode(String content) {
        return Base64.encodeToString(content.getBytes(), Base64.URL_SAFE | Base64.NO_WRAP);
    }
}
