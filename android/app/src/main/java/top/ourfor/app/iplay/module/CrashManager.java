package top.ourfor.app.iplay.module;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import top.ourfor.app.iplay.R;
import top.ourfor.app.iplay.config.AppSetting;
import top.ourfor.app.iplay.util.DateTimeUtil;
import top.ourfor.app.iplay.page.Activity;

@Slf4j
public class CrashManager implements Thread.UncaughtExceptionHandler {
    private final Thread.UncaughtExceptionHandler mDefaultHandler;
    private final Application application;
    public CrashManager(Application application){
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        this.application = application;
    }
    private boolean handleException(final Throwable exc){
        if (exc == null) {
            return false;
        }
        if (exc instanceof IndexOutOfBoundsException) {
            return true;
        }
        new Thread(() -> {
            Looper.prepare();
            log.info("{}","save crash info to file");
            collectDeviceAndUserInfo(application);
            Looper.loop();
        }).start();
        return true;
    }

    private void collectDeviceAndUserInfo(Context context){
        PackageManager pm = context.getPackageManager();
        Map<String, String> infos = new HashMap<>();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName",versionName);
                infos.put("versionCode",versionCode);
                infos.put("crashTime", DateTimeUtil.formatDateTime(new Date()));
            }
        } catch (PackageManager.NameNotFoundException e) {
            log.error("{}",e.getMessage());
        }
        Field[] fields = Build.class.getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
            }
        } catch (IllegalAccessException e) {
            log.error("{}", e.getMessage());
        } finally {
            log.info("{}",infos);
        }
    }


    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        if(!handleException(throwable) && mDefaultHandler != null){
            mDefaultHandler.uncaughtException(thread, throwable);
        } else {
            log.error("{}", throwable.getMessage());
            // display stack trace
            log.error("stack info:");
            for (StackTraceElement stackTraceElement : throwable.getStackTrace()) {
                log.error("\t{}", stackTraceElement.toString());
            }

            if (AppSetting.shared.exitAfterCrash) {
                Toast.makeText(application.getApplicationContext(), application.getString(R.string.unhandled_exception), Toast.LENGTH_LONG).show();
                try{
                    Thread.sleep(2000);
                } catch (InterruptedException e){
                    log.warn("{}",e.getMessage());
                }
                Intent intent = new Intent(application.getApplicationContext(), Activity.class);
                PendingIntent restartIntent = PendingIntent.getActivity(
                        application.getApplicationContext(), 0, intent,
                        PendingIntent.FLAG_IMMUTABLE);
                AlarmManager mgr = (AlarmManager)application.getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,
                        restartIntent);
                android.os.Process.killProcess(android.os.Process.myPid());
            } else {
                Toast.makeText(application, throwable.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
