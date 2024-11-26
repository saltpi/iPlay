package top.ourfor.app.iplayx.module;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IntentModule {
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
        Context context = Bean.get(Context.class);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            log.debug("IntentModule {}", e);
        }
    }

    public void playFile(String filepath) {
        Intent intent = new Intent();
        intent.putExtra("filepath", filepath);
        Context context = Bean.get(Context.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName(context, "top.ourfor.app.iplayx.MPVActivity");
        context.startActivity(intent);
    }
}