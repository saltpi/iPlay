package top.ourfor.app.iPlayClient.module;

import android.os.Build;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import java.util.Map;

import lombok.SneakyThrows;
import top.ourfor.app.iPlayClient.view.PlayerView;

public class PlayerViewManager extends SimpleViewManager<View> {
    ReactApplicationContext mCallerContext;

    public PlayerViewManager(ReactApplicationContext reactApplicationContext) {
        mCallerContext = reactApplicationContext;
    }

    @NonNull
    @Override
    public String getName() {
        return "PlayerView";
    }

    @SneakyThrows
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @NonNull
    @Override
    protected View createViewInstance(@NonNull ThemedReactContext themedReactContext) {
        PlayerView playerView = new PlayerView(themedReactContext, "");
        playerView.setThemedReactContext(themedReactContext);
        playerView.setOnPlayStateChange(state -> {
            WritableMap event = Arguments.createMap();
            state.forEach((k, v) -> {
                if (v instanceof Double) {
                    event.putDouble(k, (Double) v);
                } else if (v instanceof Integer) {
                    event.putInt(k, (Integer)v);
                } else {
                    event.putString(k, v.toString());
                }
            });
            ReactContext context = themedReactContext;
            context.getJSModule(RCTEventEmitter.class)
                    .receiveEvent(playerView.getId(), "onPlayStateChange", event);
        });
        return playerView;
    }

    public Map getExportedCustomBubblingEventTypeConstants() {
        return MapBuilder.builder().put(
                "onPlayStateChange",
                MapBuilder.of(
                        "phasedRegistrationNames",
                        MapBuilder.of("bubbled", "onPlayStateChange")
                )
        ).build();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @ReactProp(name = "title")
    public void setTitle(PlayerView view, String title) {
        view.setTitle(title);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @ReactProp(name = "subtitleFontName")
    public void setSubtitleFontName(PlayerView view, String fontName) {
        view.setSubtitleFontName(fontName);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @ReactProp(name = "url")
    public void setUrl(PlayerView view, String url) {
        view.setUrl(url);
    }
}
