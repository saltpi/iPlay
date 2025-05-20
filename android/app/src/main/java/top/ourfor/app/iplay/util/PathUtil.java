package top.ourfor.app.iplay.util;

import androidx.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.regex.Pattern;

import lombok.val;

public class PathUtil {
    static Pattern mediaExtRegex = Pattern.compile(".*\\.(mp4|ios|m2ts|mkv|avi|mov|flv|wmv|rmvb|rm|3gp|mpg|mpeg|ts|webm|vob|f4v|ogv|ogg|drc|gif|gifv|mng|avi|mov|qt|wmv|yuv|rm|rmvb|asf|amv|mp4|mpg|mpeg|m4v|3gp|3g2|flv|f4v|f4p|f4a|f4b)$", Pattern.CASE_INSENSITIVE);
    public static String of(String ...paths) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (String path : paths) {
            if (!first && path == "/") {
                continue;
            }
            if (path.startsWith("/")) {
                if (path.endsWith("/")) {
                    builder.append(path.substring(0, path.length() - 1));
                } else {
                    builder.append(path);
                }
            } else {
                if (path.endsWith("/")) {
                    builder.append(first ? "" : "/").append(path.substring(0, path.length() - 1));
                } else {
                    builder.append(first ? "" : "/").append(path);
                }
            }
            first = false;
        }
        return builder.toString();
    }

    @NonNull
    public static String getContent(InputStream is) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(is);
        byte[] buffer = new byte[1024];
        int length = -1;
        var allBytes = new byte[0];
        while ((length = bis.read(buffer)) != -1) {
            allBytes = Arrays.copyOf(allBytes, allBytes.length + length);
            System.arraycopy(buffer, 0, allBytes, allBytes.length - length, length);
        }
        val content = new String(allBytes);
        bis.close();
        return content;
    }

    public static boolean isMedia(String name) {
        return mediaExtRegex.matcher(name).matches();
    }

    public static String parent(String path) {
        var parentPath = path.substring(0, path.lastIndexOf('/'));
        if (parentPath.isEmpty()) {
            parentPath = "/";
        }
        return parentPath;
    }

    public static String formatSize(String size) {
        return formatSize(Long.parseLong(size));
    }

    public static String formatSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return size / 1024 + " KB";
        } else if (size < 1024 * 1024 * 1024) {
            return size / 1024 / 1024 + " MB";
        } else {
            return size / 1024 / 1024 / 1024 + " GB";
        }
    }
}
