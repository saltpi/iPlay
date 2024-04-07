package top.ourfor.app.iPlayClient.module;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;

public class FontModule extends ReactContextBaseJavaModule {
    static private String moduleName = "FontModule";

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
        } catch (NoSuchFieldException e) {
            Log.d(moduleName, e.toString());
        } catch (IllegalAccessException e) {
            Log.d(moduleName, e.toString());
        }
    }

    FontModule(ReactApplicationContext context) {
        super(context);
    }

    public static String getFontPath(Context context) {
        File filesDir = context.getExternalFilesDir("");
        File fontDir = new File(filesDir, "font");
        return fontDir.getPath();
    }

    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    public static void scanExternalFont(Context context) throws IllegalAccessException, NoSuchMethodException, NoSuchFieldException {
        File filesDir = context.getExternalFilesDir("");
        File fontDir = new File(filesDir, "font");
        if (!fontDir.exists()) {
            boolean created = fontDir.mkdirs();
            if (created) {
                Log.d(moduleName, "create fontDir success: " + fontDir.getPath());
            } else {
                Log.d(moduleName, "create fontDir failed: " + fontDir.getPath());
            }
        }

        File[] ttfs = fontDir.listFiles(f -> f.isFile() && f.getName().endsWith("ttf"));
        Map<String, Typeface> fontMap = getSystemFontMap();
        for (File ttf : ttfs) {
            Typeface font = Typeface.createFromFile(ttf);
            String familyName = ttf.getName().replace(".ttf", "");
            fontMap.put(familyName, font);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @ReactMethod
    public void fontFamilyListAsync(Promise promise) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        WritableArray array = fontFamilyList();
        promise.resolve(array);
    }

    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @ReactMethod(isBlockingSynchronousMethod = true)
    public WritableArray fontFamilyList() throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        scanExternalFont(getReactApplicationContext());
        ArrayList<String> fontNames = new ArrayList<>();
        fontNames.addAll(systemFontMap.keySet());
        return Arguments.fromList(fontNames);
    }


    @NonNull
    @Override
    public String getName() {
        return moduleName;
    }
}
