package top.ourfor.app.iplay.view.video;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import lombok.val;
import top.ourfor.app.iplay.R;
import top.ourfor.app.iplay.databinding.PlayerControlItemBinding;
import top.ourfor.app.iplay.util.DeviceUtil;

public class PlayerControlItemView extends ConstraintLayout {
    private TypedArray style;
    private PlayerControlItemBinding binding;
    public PlayerControlItemView(@NonNull Context context) {
        this(context, null);
    }

    public PlayerControlItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayerControlItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        binding = PlayerControlItemBinding.inflate(LayoutInflater.from(context), this);
        style = context.obtainStyledAttributes(attrs ,R.styleable.PlayerControlItem);
        setupUI(context);
        style.recycle();
    }

    void setupUI(Context context) {
        val size = style.getDimension(R.styleable.PlayerControlItem_cornerRadius, DeviceUtil.dpToPx(32));
        val imgId = style.getResourceId(R.styleable.PlayerControlItem_src, 0);
        if (imgId != 0) {
            setIcon(imgId);
        }

        RippleDrawable drawable = new RippleDrawable(ColorStateList.valueOf(Color.argb(80, 0, 0, 0)), null, null);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(Color.argb(50, 0, 0, 0));
        gradientDrawable.setCornerRadius(size);
        drawable.addLayer(gradientDrawable);
        setBackground(drawable);

        val layout = new LayoutParams((int) (size * 2), (int) (size * 2));
        setLayoutParams(layout);

        val imageLayout = new LayoutParams((int) size, (int) size);
        imageLayout.topToTop = LayoutParams.PARENT_ID;
        imageLayout.bottomToBottom = LayoutParams.PARENT_ID;
        imageLayout.leftToLeft = LayoutParams.PARENT_ID;
        imageLayout.rightToRight = LayoutParams.PARENT_ID;
        binding.icon.setLayoutParams(imageLayout);
        setClipToOutline(true);

        if (DeviceUtil.isTV) {
            setFocusable(true);
            setOnFocusChangeListener((v, hasFocus) -> {
                gradientDrawable.setCornerRadius(size + 0f);
                if (hasFocus) {
                    gradientDrawable.setColor(Color.argb(80, 255, 255, 255));
                } else {
                    gradientDrawable.setColor(Color.argb(50, 0, 0, 0));
                }
                setBackground(gradientDrawable);
            });
        }
    }

    public void setIcon(int resId) {
        binding.icon.setImageResource(resId);
        binding.icon.setImageTintList(ColorStateList.valueOf(Color.WHITE));
    }


}
