package top.ourfor.app.iplay.config;

import static top.ourfor.app.iplay.module.Bean.XGET;

import androidx.appcompat.app.AppCompatDelegate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.val;
import top.ourfor.app.iplay.bean.IKVStorage;
import top.ourfor.app.iplay.common.type.LayoutType;
import top.ourfor.app.iplay.common.type.PlayerKernelType;
import top.ourfor.app.iplay.common.type.VideoDecodeType;
import top.ourfor.app.iplay.common.type.PictureQuality;
import top.ourfor.app.iplay.page.setting.theme.ThemeColorModel;
import top.ourfor.app.iplay.page.setting.theme.ThemeModel;
import top.ourfor.app.iplay.util.DeviceUtil;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class AppSetting {
    public static String settingCacheKey = "@setting";
    public static AppSetting shared = getShared();

    public VideoDecodeType videoDecodeType;
    public LayoutType layoutType;
    public ThemeModel.ThemeType appearance;
    public ThemeColorModel.ThemeColor themeColor;
    public boolean useStrmFirst;
    public boolean useFullScreenPlayer;
    public boolean turnOffAudio;
    public boolean turnOffAutoUpgrade;
    public PictureQuality pictureQuality;
    public boolean usePictureMultiThread;
    public boolean useExoPlayer;
    public boolean autoPlayNextEpisode;
    public PlayerKernelType playerKernel;
    public String mpvConfig;
    public String fontFamily;
    public String webHomePage;
    public boolean exitAfterCrash;
    public String allowTabs;

    static AppSetting getShared() {
        var instance = XGET(IKVStorage.class).getObject(settingCacheKey, AppSetting.class);
        if (instance == null) {
            instance = new AppSetting();
            instance.videoDecodeType = VideoDecodeType.Software;
            instance.appearance = ThemeModel.ThemeType.FOLLOW_SYSTEM;
            instance.useFullScreenPlayer = true;
            instance.useStrmFirst = false;
            instance.usePictureMultiThread = true;
            instance.turnOffAutoUpgrade = DeviceUtil.isTV || DeviceUtil.isDebugPackage;
            instance.webHomePage = "https://bing.com";
            instance.pictureQuality = PictureQuality.Auto;
            instance.playerKernel = PlayerKernelType.MPV;
            instance.layoutType = LayoutType.Auto;
            if (DeviceUtil.isTV) {
                instance.videoDecodeType = VideoDecodeType.Hardware;
                instance.playerKernel = PlayerKernelType.EXO;
            }
        }
        if (instance.allowTabs == null || instance.allowTabs.isEmpty()) {
            instance.allowTabs = String.join(",", instance.getDefaultTabs());
        }
        return instance;
    }

    @JsonIgnore
    public List<String> getDefaultTabs() {
        return List.of("search", "star", "home", "file", "setting");
    }

    @JsonIgnore
    public Map<String, String> getPlayerConfig() {
        val config = new HashMap<String, String>();
        config.put("hwdec", videoDecodeType == VideoDecodeType.Auto ? "auto" : videoDecodeType == VideoDecodeType.Hardware ? "yes" : "no");
        if (turnOffAudio) {
            config.put("aid", turnOffAudio ? "no" : "auto");
        }
        if (mpvConfig != null && !mpvConfig.isEmpty()) {
            Arrays.stream(mpvConfig.split("\\n")).forEach(line -> {
                val kv = line.split("=");
                if (kv.length == 2) {
                    config.put(kv[0].trim(), kv[1].trim());
                }
            });
        }
        return config;
    }

    @JsonIgnore
    public int appTheme() {
        int mode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        switch (appearance) {
            case DARK_MODE -> mode = AppCompatDelegate.MODE_NIGHT_YES;
            case LIGHT_MODE -> mode = AppCompatDelegate.MODE_NIGHT_NO;
        }
        return mode;
    }

    @JsonIgnore
    public void save() {
        XGET(IKVStorage.class).setObject(settingCacheKey, this);
    }
}
