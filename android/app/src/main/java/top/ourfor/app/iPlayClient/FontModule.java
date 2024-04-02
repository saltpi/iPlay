package top.ourfor.app.iPlayClient;

import android.graphics.Typeface;
import android.graphics.fonts.Font;
import android.graphics.fonts.SystemFonts;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FontModule extends ReactContextBaseJavaModule {
    private ReactApplicationContext ctx;

    FontModule(ReactApplicationContext context) {
        super(context);
    }


    @ReactMethod
    public void fontFamilyListAsync(Promise promise) {
        WritableArray array = fontFamilyList();
        promise.resolve(array);
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public WritableArray fontFamilyList() {
        ArrayList<String> fontNames = new ArrayList<>();
        fontNames.add("abc");
        fontNames.add("abd");
        fontNames.add("abf");
        return Arguments.fromList(fontNames);
    }


    @NonNull
    @Override
    public String getName() {
        return "FontModule";
    }
}
