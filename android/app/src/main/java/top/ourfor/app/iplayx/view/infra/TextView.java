package top.ourfor.app.iplayx.view.infra;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import top.ourfor.app.iplayx.module.FontModule;

public class TextView extends androidx.appcompat.widget.AppCompatTextView {
    public TextView(Context context) {
        super(context);
        setTypeface(FontModule.getThemeFont());
    }

    public TextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setTypeface(FontModule.getThemeFont());
    }

    public TextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface(FontModule.getThemeFont());
    }
}
