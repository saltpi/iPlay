package top.ourfor.app.iplayx.page;

import static top.ourfor.app.iplayx.module.Bean.XGET;
import static top.ourfor.app.iplayx.module.Bean.XSET;
import static top.ourfor.app.iplayx.module.Bean.XWATCH;
import static top.ourfor.app.iplayx.page.PageMaker.makePage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.BatteryManager;
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
import java.util.concurrent.ThreadPoolExecutor;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import top.ourfor.app.iplayx.R;
import top.ourfor.app.iplayx.action.BatteryUpdateAction;
import top.ourfor.app.iplayx.action.DispatchAction;
import top.ourfor.app.iplayx.action.LayoutUpdateAction;
import top.ourfor.app.iplayx.action.NavigationTitleBar;
import top.ourfor.app.iplayx.action.ThemeUpdateAction;
import top.ourfor.app.iplayx.api.onedrive.OneDriveApi;
import top.ourfor.app.iplayx.bean.Navigator;
import top.ourfor.app.iplayx.common.type.LayoutType;
import top.ourfor.app.iplayx.config.AppSetting;
import top.ourfor.app.iplayx.databinding.ActivityBinding;
import top.ourfor.app.iplayx.module.CacheModule;
import top.ourfor.app.iplayx.page.setting.theme.ThemeColorModel;
import top.ourfor.app.iplayx.store.GlobalStore;
import top.ourfor.app.iplayx.util.DeviceUtil;
import top.ourfor.app.iplayx.util.PackageUtil;
import top.ourfor.app.iplayx.util.PathUtil;
import top.ourfor.app.iplayx.util.WindowUtil;
import top.ourfor.app.iplayx.view.infra.Toolbar;

@Slf4j
@AndroidEntryPoint
public class Activity extends AppCompatActivity implements NavigationTitleBar,
        DispatchAction, NavigationTitleBar.ThemeManageAction, ThemeUpdateAction, LayoutUpdateAction {
    @Inject
    CacheModule cacheModule;
    @Inject
    GlobalStore store;
    private ActivityBinding binding = null;

    Router router;

    ActivityEvent event = new ActivityEvent(this, null, true);


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        XSET(android.app.Activity.class, this);
        XSET(Activity.class, this);
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
        XSET(Navigator.class, router);
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
        return XGET(Navigator.class).popPage();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val navigator = XGET(Navigator.class);
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
                        XGET(Navigator.class).pushPage(R.id.playerPage, bundle);
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

}
