package top.ourfor.app.iPlayClient.view;

import android.content.Context;
import android.util.Log;

public class DemoView extends androidx.appcompat.widget.AppCompatTextView {

    public DemoView(Context context) {
        super(context);
    }

    @Override
    protected void onDetachedFromWindow() {
        Log.d("VideoView", "unmount");
        super.onDetachedFromWindow();
    }
}
