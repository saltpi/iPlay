package top.ourfor.app.iPlayClient.module;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IntentModule extends ReactContextBaseJavaModule {
    public IntentModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "IntentModule";
    }

    @ReactMethod
    public void openUrl(String url) {
        log.debug("IntentModule open: {}", url);
        Intent intent;
        try {
            intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
        } catch (Exception e) {
            log.debug("IntentModule {}", e);
            return;
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Context context = BeanManager.get(Context.class);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            log.debug("IntentModule {}", e);
        }
    }

    @ReactMethod
    public void playFile(String filepath) {
        Intent intent = new Intent();
        intent.putExtra("filepath", filepath);
        Context context = BeanManager.get(Context.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName(context, "top.ourfor.app.iPlayClient.MPVActivity");
        context.startActivity(intent);
    }
}