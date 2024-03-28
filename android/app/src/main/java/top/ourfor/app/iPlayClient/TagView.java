package top.ourfor.app.iPlayClient;

import android.content.Context;
import android.util.Log;

public class TagView extends androidx.appcompat.widget.AppCompatTextView {

    public TagView(Context context) {
        super(context);
    }

    @Override
    protected void onDetachedFromWindow() {
        Log.d("VideoView", "unmount");
        super.onDetachedFromWindow();
    }
}
