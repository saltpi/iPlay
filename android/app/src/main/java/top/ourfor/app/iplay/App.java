package top.ourfor.app.iplay;

import static top.ourfor.app.iplay.module.Bean.XSET;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatDelegate;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import top.ourfor.app.iplay.bean.IJSONAdapter;
import top.ourfor.app.iplay.bean.IKVStorage;
import top.ourfor.app.iplay.config.AppSetting;
import top.ourfor.app.iplay.module.FontModule;
import top.ourfor.app.iplay.store.SimpleInMemoryStore;
import top.ourfor.app.iplay.util.DeviceUtil;
import top.ourfor.app.iplay.util.JacksonJsonAdapter;
import top.ourfor.app.iplay.util.MMKVStorage;
import top.ourfor.app.iplay.module.CrashManager;
import top.ourfor.app.iplay.store.IAppStore;

@Slf4j
public class App extends Application {
    private ThreadPoolExecutor executor = null;

    @Override
    public void onCreate() {
        super.onCreate();
        val context = getApplicationContext();
        FontModule.initFont(context);
        DeviceUtil.init(context);
        XSET(Application.class, this);
        XSET(Context.class, context);
        XSET(IJSONAdapter.class, JacksonJsonAdapter.shared);
        XSET(IKVStorage.class, MMKVStorage.shared);
        XSET(IAppStore.class, SimpleInMemoryStore.shared);
        // get device cpu core count
        val coreCount = DeviceUtil.cpuCoreCount();
        executor = new ThreadPoolExecutor(coreCount, coreCount * 2, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        XSET(ThreadPoolExecutor.class, executor);
        AppCompatDelegate.setDefaultNightMode(AppSetting.shared.appTheme());
        setupExceptionHandler();
    }

    void setupExceptionHandler() {
        val crashManager = new CrashManager(this);
        Thread.setDefaultUncaughtExceptionHandler(crashManager);
        var exitAfterCrash = AppSetting.shared.exitAfterCrash;
        if (exitAfterCrash) {
            return;
        }

        new Handler(Looper.getMainLooper()).post(() -> {
            //noinspection InfiniteLoopStatement
            while (true) {
                try {
                    Looper.loop();
                } catch (Throwable e) {
                    log.error("loop error", e);
                }
            }
        });
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        executor = null;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        log.info("on low memory");
    }
}

