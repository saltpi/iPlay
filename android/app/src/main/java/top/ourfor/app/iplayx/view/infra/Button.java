package top.ourfor.app.iplayx.view.infra;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import top.ourfor.app.iplayx.module.FontModule;

public class Button extends androidx.appcompat.widget.AppCompatButton {
    public Button(@NonNull Context context) {
        super(context);
        setTypeface(FontModule.getThemeFont());
    }

    public Button(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setTypeface(FontModule.getThemeFont());
    }

    public Button(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface(FontModule.getThemeFont());
    }
}
