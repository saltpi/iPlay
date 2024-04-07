package top.ourfor.app.iPlayClient.module;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;

import top.ourfor.app.iPlayClient.view.DemoView;

public class DemoViewManager extends SimpleViewManager<View> {
    ReactApplicationContext mCallerContext;

    public DemoViewManager(ReactApplicationContext reactApplicationContext) {
        mCallerContext = reactApplicationContext;
    }

    @NonNull
    @Override
    public String getName() {
        return "DemoView";
    }

    @NonNull
    @Override
    protected View createViewInstance(@NonNull ThemedReactContext themedReactContext) {
        TextView text = new DemoView(themedReactContext);
        text.setText("Hello world");
        return text;
    }
}
