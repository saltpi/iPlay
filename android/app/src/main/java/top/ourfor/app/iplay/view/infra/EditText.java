package top.ourfor.app.iplay.view.infra;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import top.ourfor.app.iplay.module.FontModule;

public class EditText extends androidx.appcompat.widget.AppCompatEditText {
    public EditText(@NonNull Context context) {
        super(context);
        setTypeface(FontModule.getThemeFont());
    }

    public EditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setTypeface(FontModule.getThemeFont());
    }

    public EditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface(FontModule.getThemeFont());
    }
}
