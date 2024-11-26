package top.ourfor.app.iplayx.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import lombok.val;
import top.ourfor.app.iplayx.R;
import top.ourfor.app.iplayx.common.model.Color;
import top.ourfor.app.iplayx.common.model.ColorScheme;
import top.ourfor.app.iplayx.databinding.TagViewBinding;
import top.ourfor.app.iplayx.util.DeviceUtil;

public class TagView extends ConstraintLayout {
    private TagViewBinding binding = null;
    private TypedArray style = null;

    public TagView(@NonNull Context context) {
        super(context);
        setup(context);
    }

    public TagView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        style = context.obtainStyledAttributes(attrs , R.styleable.TagViewStyle);
        setup(context);
        style.recycle();
    }

    public TagView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        style = context.obtainStyledAttributes(attrs , R.styleable.TagViewStyle);
        setup(context);
        style.recycle();
    }

    public TagView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        style = context.obtainStyledAttributes(attrs , R.styleable.TagViewStyle);
        setup(context);
        style.recycle();
    }

    public void setup(Context context) {
        binding = TagViewBinding.inflate(LayoutInflater.from(context), this);
        if (style == null) {
            return;
        }
        val text = style.getString(R.styleable.TagViewStyle_tagText);
        if (text != null) {
            binding.label.setText(text);
        }
        val color = style.getString(R.styleable.TagViewStyle_tagColor);
        if (color != null) {
            setColor(color);
        }

        if (DeviceUtil.isTV) {
            setFocusable(true);
            View.OnFocusChangeListener onFocusChangeListener = (v, hasFocus) -> {
                v.setBackgroundResource(hasFocus ? R.drawable.button_focus : R.drawable.button_normal);
                v.setScaleX(hasFocus ? 1.05f : 1.0f);
                v.setScaleY(hasFocus ? 1.05f : 1.0f);
            };
            View.OnHoverListener onHoverListener = (v, event) -> {
                val isHover = event.getAction() == MotionEvent.ACTION_HOVER_ENTER;
                v.setBackgroundResource(isHover ? R.drawable.button_focus : R.drawable.button_normal);
                v.setScaleX(isHover ? 1.05f : 1.0f);
                v.setScaleY(isHover ? 1.05f : 1.0f);
                return true;
            };
            setOnHoverListener(onHoverListener);
            setOnFocusChangeListener(onFocusChangeListener);
        }
    }

    public void setText(String text) {
        binding.label.setText(text);
    }

    public void setTextSize(float size) {
        binding.label.setTextSize(size);
    }

    public void setColor(Color color) {
        val theme = color;
        binding.label.setTextColor(theme.textColor());
        setBackgroundColor(theme.backgroundColor());
        val drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(DeviceUtil.dpToPx(5));
        drawable.setStroke(DeviceUtil.dpToPx(2), theme.borderColor());
        drawable.setColor(theme.backgroundColor());
        setBackground(drawable);
        setClipToOutline(true);
    }
    public void setColor(String color) {
        var c = ColorScheme.shared.getScheme().get(color);
        if (c == null) {
            c = ColorScheme.shared.getScheme().get("red");
        }
        setColor(c);
    }
}
