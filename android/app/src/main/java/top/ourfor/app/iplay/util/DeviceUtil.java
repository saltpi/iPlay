package top.ourfor.app.iplay.util;

import static android.content.Context.UI_MODE_SERVICE;

import android.app.UiModeManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Size;
import android.view.WindowManager;

import java.io.File;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeviceUtil {
    public static float density;
    public static int width;
    public static int height;
    public static boolean isTV;
    public static boolean isArmeabiV7a;
    public static boolean isDebugPackage;

    static public int dpToPx(float dpValue) {
        return (int) (dpValue * density + 0.5f);
    }

    static public boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    static public Size screenSize(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        return new Size(width, height);
    }

    static public float screenScale(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        float density = displayMetrics.density;
        return density;
    }

    public static void init(Context applicationContext) {
        density = screenScale(applicationContext);
        Size size = screenSize(applicationContext);
        width = size.getWidth();
        height = size.getHeight();
        UiModeManager uiModeManager = (UiModeManager) applicationContext.getSystemService(UI_MODE_SERVICE);
        isTV = uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION;
        isArmeabiV7a = isArmeabiV7a();
        isDebugPackage = applicationContext.getPackageName().contains("debug");
    }

    public static boolean isArmeabiV7a() {
        boolean hasArm64 = false;
        for (String abi : Build.SUPPORTED_ABIS) {
            if ("arm64-v8a".equals(abi)) {
                hasArm64 = true;
                break;
            }
        }

        if (hasArm64) {
            return false;
        }

        for (String abi : Build.SUPPORTED_ABIS) {
            if ("armeabi-v7a".equals(abi)) {
                return true;
            }
        }
        return false;
    }

    public static String arch() {
        boolean hasArm64 = false;
        boolean hasX86_64 = false;
        for (String abi : Build.SUPPORTED_ABIS) {
            if ("arm64-v8a".equals(abi)) {
                hasArm64 = true;
                break;
            } else if ("x86_64".equals(abi)) {
                hasX86_64 = true;
                break;
            }
        }

        if (hasArm64) {
            return "arm64-v8a";
        } else if (hasX86_64) {
            return "x86_64";
        }

        for (String abi : Build.SUPPORTED_ABIS) {
            if ("armeabi-v7a".equals(abi)) {
                return "armeabi-v7a";
            }
        }
        return "unknown";
    }

    public static float spToPx(int i) {
        return i;
    }

    public static int getVersionCode(Context context) {
        PackageManager manager = context.getPackageManager();
        int code = 0;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            code = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return code;
    }

    public static String getVersionName(Context context) {
        PackageManager manager = context.getPackageManager();
        String name = null;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            name = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return name;
    }

    public static int cpuCoreCount() {
        var CPU_CORES = 0;
        try {
            File dir = new File("/sys/devices/system/cpu/");
            File[] files = dir.listFiles((file) -> Pattern.matches("cpu[0-9]+", file.getName()));
            assert files != null;
            CPU_CORES = files.length;
        } catch (Exception e) {
            log.error("CPU core count error", e);
        }

        if (CPU_CORES < 1) {
            CPU_CORES = Runtime.getRuntime().availableProcessors();
        }

        if (CPU_CORES < 1) {
            CPU_CORES = 1;
        }
        return CPU_CORES;
    }
}
