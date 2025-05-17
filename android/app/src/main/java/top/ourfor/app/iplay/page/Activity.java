package top.ourfor.app.iplay.page;

import static top.ourfor.app.iplay.module.Bean.XGET;
import static top.ourfor.app.iplay.module.Bean.XSET;
import static top.ourfor.app.iplay.module.Bean.XWATCH;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.PictureInPictureParams;
import android.app.RemoteAction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.azhon.appupdate.manager.DownloadManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import top.ourfor.app.iplay.R;
import top.ourfor.app.iplay.action.BatteryUpdateAction;
import top.ourfor.app.iplay.action.DispatchAction;
import top.ourfor.app.iplay.action.LayoutUpdateAction;
import top.ourfor.app.iplay.action.NavigationTitleBar;
import top.ourfor.app.iplay.action.ThemeUpdateAction;
import top.ourfor.app.iplay.api.onedrive.OneDriveApi;
import top.ourfor.app.iplay.bean.INavigator;
import top.ourfor.app.iplay.common.type.LayoutType;
import top.ourfor.app.iplay.config.AppSetting;
import top.ourfor.app.iplay.databinding.ActivityBinding;
import top.ourfor.app.iplay.module.CacheModule;
import top.ourfor.app.iplay.page.setting.theme.ThemeColorModel;
import top.ourfor.app.iplay.store.IAppStore;
import top.ourfor.app.iplay.util.DeviceUtil;
import top.ourfor.app.iplay.util.PackageUtil;
import top.ourfor.app.iplay.util.PathUtil;
import top.ourfor.app.iplay.util.WindowUtil;
import top.ourfor.app.iplay.view.infra.Toolbar;
import top.ourfor.app.iplay.view.player.Player;

@Slf4j
public class Activity extends AppCompatActivity implements NavigationTitleBar,
        DispatchAction, NavigationTitleBar.ThemeManageAction, ThemeUpdateAction, LayoutUpdateAction {
    CacheModule cacheModule;
    IAppStore store;
    private ActivityBinding binding = null;

    Router router;

    ActivityEvent event = new ActivityEvent(this, null, true, null);

    BroadcastReceiver mediaReceiver;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        XSET(android.app.Activity.class, this);
        XSET(Activity.class, this);
        store = XGET(IAppStore.class);
        cacheModule = new CacheModule();
        cacheModule.clean();
        event.register();

        switchLayoutMode(AppSetting.shared.layoutType);

        binding = ActivityBinding.inflate(getLayoutInflater());
        setSupportActionBar(binding.toolbar);
        XSET(NavigationTitleBar.class, this);
        XSET(Toolbar.class, binding.toolbar);
        XSET(ActionBar.class, getSupportActionBar());
        XSET(ActivityEvent.class, event);
        applyUserConfigTheme();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        val bottomBar = binding.bottomNavigation;
        bottomBar.setItemIconTintList(null);
        XSET(BottomNavigationView.class, bottomBar);
        XSET(DispatchAction.class, this);
        XSET(ThemeManageAction.class, this);
        XWATCH(ThemeUpdateAction.class, this);
        XSET(LayoutUpdateAction.class, this);

        val toolbar = XGET(Toolbar.class);
        toolbar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                toolbar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                float y = toolbar.getY();
                int height = toolbar.getHeight();
                WindowUtil.defaultToolbarBottom = (int) (y + height + 80);
                binding.bottomNavigation.setSelectedItemId(R.id.homePage);
            }
        });


        router = new Router(binding.container, binding.bottomNavigation, binding.toolbar);
        router.scanPage();
        XSET(INavigator.class, router);
        setContentView(binding.getRoot());

        updateNavBar();

        registerReceiver(batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        if (AppSetting.shared.turnOffAutoUpgrade) return;
        XGET(ThreadPoolExecutor.class).execute(() -> {
            PackageUtil.checkUpdate(info -> {
                if (info == null) return;
                log.info("update info: {}", info);
                DownloadManager manager = new DownloadManager.Builder(this)
                        .apkUrl(info.getOrDefault("url", ""))
                        .apkName(info.getOrDefault("packageName", "iPlay.apk"))
                        .smallIcon(R.mipmap.ic_launcher)
                        .showNewerToast(true)
                        .apkVersionCode(Integer.valueOf(info.getOrDefault("versionCode", "0")))
                        .apkVersionName(info.getOrDefault("version", "0.0.0"))
                        .apkSize(PathUtil.formatSize(info.getOrDefault("size", "0")))
                        .apkDescription(info.getOrDefault("content", ""))
                        .build();
                manager.download();
            });
        });

        handleIntent(getIntent());
    }

    private void updateNavBar() {
        if (!DeviceUtil.isTV) return;
        binding.bottomNavigation.setBackgroundColor(Color.TRANSPARENT);
        binding.bottomNavigation.setItemPaddingBottom(0);
        binding.bottomNavigation.setItemPaddingTop(0);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return XGET(INavigator.class).popPage();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val navigator = XGET(INavigator.class);
            if (navigator.canGoBack()) {
                return navigator.popPage();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void setNavTitle(String title) {
        binding.toolbarTitle.setText(title);
    }

    @Override
    public void setNavTitle(int titleId) {
        binding.toolbarTitle.setText(titleId);
    }

    @Override
    public void setStatusBarTextColor(boolean isDark) {
        val window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        int flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        if (isDark) {
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        } else {
            flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        window.getDecorView().setSystemUiVisibility(flags);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    @Override
    public void switchToDarkMode(boolean isDarkMode) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        setStatusBarTextColor(!isDarkMode);
        XGET(ThemeUpdateAction.class).themeDidUpdate();
        recreate();
    }

    @Override
    public void switchToAutoModel() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        setStatusBarTextColor(isDarkMode());
        XGET(ThemeUpdateAction.class).themeDidUpdate();
        recreate();
    }

    @Override
    public void switchThemeColor(ThemeColorModel.ThemeColor color) {

    }

    @Override
    public boolean isDarkMode() {
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }

    public void applyUserConfigTheme() {
        setStatusBarTextColor(!isDarkMode());
        binding.toolbar.setPadding(0, WindowUtil.getStatusBarHeight(), 0, 0);
        if (DeviceUtil.isTV) {
            binding.bottomNavigation.setItemActiveIndicatorEnabled(false);
        }
    }

    private BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            if (scale == 0) {
                scale = 1;
            }
            float battery = level * 100 / (float)scale;
            val action = XGET(BatteryUpdateAction.class);
            if (action == null) return;
            action.onBatteryUpdate(battery);
        }
    };

    void handleIntent(Intent intent) {
        Uri uri = intent.getData();
        log.info("intent uri: {}", uri);
        if (uri != null) {
            val isOneDrive = uri.getLastPathSegment().equals("onedrive");
            if (isOneDrive) {
                String code = uri.getQueryParameter("code");
                val oneDriveApi = new OneDriveApi();
                oneDriveApi.redeem(code);
            }

            var host = uri.getHost();
            if (host.equals("play")) {
                var type = uri.getQueryParameter("type");
                if (type.equals("url")) {
                    var src = uri.getQueryParameter("url");
                    var url = new String(Base64.decode(src, Base64.DEFAULT));
                    if (url == null) return;
                    val bundle = new HashMap<String, Object>();
                    bundle.put("url", url);
                    XGET(DispatchAction.class).runOnUiThread(() -> {
                        XGET(INavigator.class).pushPage(R.id.playerPage, bundle);
                    });
                }
            }

        }
    }



    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private boolean isTV(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_LEANBACK);
    }

    @Override
    public void switchLayoutMode(LayoutType mode) {
        boolean isTV = this.isTV(this);
        if (mode == LayoutType.TV) {
            isTV = true;
        } else if (mode == LayoutType.Phone) {
            isTV = false;
        }
        DeviceUtil.isTV = isTV;
        if (isTV) {
            WindowUtil.enterFullscreen();
        }
        Configuration config = new Configuration(getResources().getConfiguration());
        config.setTo(getResources().getConfiguration());
        config.uiMode = isTV ? Configuration.UI_MODE_TYPE_TELEVISION : Configuration.UI_MODE_TYPE_NORMAL;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    @Override
    @SuppressLint({"UnspecifiedRegisterReceiverFlag", "UnspecifiedImmutableFlag"})
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode);
        if (!isInPictureInPictureMode) {
            if (mediaReceiver != null) {
                unregisterReceiver(mediaReceiver);
            }
        }

        val context = getBaseContext();
        val player = XGET(Player.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val MEDIA_CONTROL_ACTION = "iplay.action.media_control";
            val MEDIA_CONTROL_KEY = "action";
            val backwardIntent = PendingIntent.getBroadcast(this, 2, new Intent(MEDIA_CONTROL_ACTION).putExtra(MEDIA_CONTROL_KEY, "backward"), PendingIntent.FLAG_IMMUTABLE);
            val pauseIntent = PendingIntent.getBroadcast(this, 3, new Intent(MEDIA_CONTROL_ACTION).putExtra(MEDIA_CONTROL_KEY, "pause"), PendingIntent.FLAG_IMMUTABLE);
            val forwardIntent = PendingIntent.getBroadcast(this, 4, new Intent(MEDIA_CONTROL_ACTION).putExtra(MEDIA_CONTROL_KEY, "forward"), PendingIntent.FLAG_IMMUTABLE);
            setPictureInPictureParams(new PictureInPictureParams.Builder()
                    .setActions(List.of(
                            new RemoteAction(Icon.createWithResource(context, com.microsoft.fluent.mobile.icons.R.drawable.ic_fluent_skip_back_10_24_filled), "", "", backwardIntent),
                            new RemoteAction(Icon.createWithResource(context, com.microsoft.fluent.mobile.icons.R.drawable.ic_fluent_pause_24_filled), "", "", pauseIntent),
                            new RemoteAction(Icon.createWithResource(context, com.microsoft.fluent.mobile.icons.R.drawable.ic_fluent_skip_forward_10_24_filled), "", "", forwardIntent)
                    ))
                    .build());
            mediaReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent == null || intent.getExtras() == null) return;
                    val action = intent.getExtras().getString(MEDIA_CONTROL_KEY);
                    if (action == null) return;
                    switch (action) {
                        case "pause" -> player.pause();
                        case "forward" -> player.jumpForward(10);
                        case "backward" -> player.jumpBackward(10);
                        case "resume" -> player.resume();
                    }
                }
            };
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(mediaReceiver, new IntentFilter(MEDIA_CONTROL_ACTION), RECEIVER_EXPORTED);
            } else {
                registerReceiver(mediaReceiver, new IntentFilter(MEDIA_CONTROL_ACTION));
            }
        }
    }
}
