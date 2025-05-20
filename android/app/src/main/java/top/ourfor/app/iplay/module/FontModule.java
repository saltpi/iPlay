package top.ourfor.app.iplay.module;

import android.content.Context;
import android.graphics.Typeface;

import androidx.core.content.res.ResourcesCompat;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import top.ourfor.app.iplay.R;
import top.ourfor.app.iplay.config.AppSetting;

@Slf4j
public class FontModule {
    static private Map<String, Typeface> systemFontMap;

    static Map<String, Typeface> getSystemFontMap() throws NoSuchFieldException, IllegalAccessException {
        Typeface typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL);
        Field field = Typeface.class.getDeclaredField("sSystemFontMap");
        field.setAccessible(true);
        Map<String, Typeface> systemFontMap = (Map<String, Typeface>) field.get(typeface);
        return systemFontMap;
    }

    static public void obtainSystemFont() {
        try {
            systemFontMap = getSystemFontMap();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("obtain system font failed", e);
        }
    }

    public static List<String> getFontFamilyList() {
        if (systemFontMap == null) {
            obtainSystemFont();
        }
        return new ArrayList<>(systemFontMap.keySet());
    }

    public static Typeface getThemeFont() {
        return getFont(AppSetting.shared.fontFamily);
    }
    public static Typeface getFont(String familyName) {
        if (systemFontMap == null) {
            obtainSystemFont();
        }
        return systemFontMap.get(familyName);
    }

    public static String getFontPath(Context context) {
        File filesDir = context.getExternalFilesDir("");
        File fontDir = new File(filesDir, "font");
        return fontDir.getPath();
    }

    public static void initFont(Context context) {
        try {
            scanInternalFont(context);
            scanExternalFont(context);
        } catch (Exception e) {
            log.error("scan external font failed", e);
        }
    }

    public static void scanInternalFont(Context context) throws NoSuchFieldException, IllegalAccessException {
        var fonts = List.of(R.font.lxgw_wen_kai_screen, R.font.twemoji_mozilla);
        var fontMap = getSystemFontMap();
        for (int resId : fonts) {
            Typeface font = ResourcesCompat.getFont(context, resId);
            var familyName = context.getResources().getResourceEntryName(resId);
            fontMap.put(familyName, font);
        }
    }

    public static void scanExternalFont(Context context) throws IllegalAccessException, NoSuchMethodException, NoSuchFieldException {
        File filesDir = context.getExternalFilesDir("");
        File fontDir = new File(filesDir, "font");
        if (!fontDir.exists()) {
            boolean created = fontDir.mkdirs();
            if (created) {
                log.debug("create fontDir success: {}", fontDir.getPath());
            } else {
                log.debug("create fontDir failed: {}", fontDir.getPath());
            }
        }

        File[] ttfs = fontDir.listFiles(f -> f.isFile() && f.getName().endsWith("ttf"));
        var fontMap = getSystemFontMap();
        for (File ttf : ttfs) {
            Typeface font = Typeface.createFromFile(ttf);
            var familyName = ttf.getName().replace(".ttf", "");
            fontMap.put(familyName, font);
        }
    }

}
