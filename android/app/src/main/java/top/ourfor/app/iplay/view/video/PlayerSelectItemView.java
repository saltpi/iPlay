package top.ourfor.app.iplay.view.video;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import lombok.Getter;
import top.ourfor.app.iplay.databinding.PlayerSelectItemBinding;
import top.ourfor.app.iplay.util.DeviceUtil;

@Getter
public class PlayerSelectItemView extends ConstraintLayout {
    static int iconSize = 36;
    static int normalColor = Color.parseColor("#506200EE");
    static int focusColor = Color.parseColor("#FF6200EE");

    PlayerSelectItemBinding binding;

    @SuppressLint("ResourceType")
    public PlayerSelectItemView(@NonNull Context context) {
        super(context);
        binding = PlayerSelectItemBinding.inflate(LayoutInflater.from(context), this, true);
        setupUI(context);
    }

    private void setupUI(Context context) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(normalColor);
        gradientDrawable.setCornerRadius(DeviceUtil.dpToPx(5));
        setBackground(gradientDrawable);

        if (DeviceUtil.isTV) {
            setFocusable(true);
            setOnFocusChangeListener((v, hasFocus) -> {
                gradientDrawable.setColor(hasFocus ? focusColor : normalColor);
                setBackground(gradientDrawable);
            });
        }
    }

    public void setIsSelected(boolean flag) {
        int icon = NO_ID;
        if (flag) {
            icon = com.microsoft.fluent.mobile.icons.R.drawable.ic_fluent_checkbox_checked_24_filled;
        } else {
            icon = com.microsoft.fluent.mobile.icons.R.drawable.ic_fluent_checkbox_unchecked_24_filled;
        }
        binding.itemIcon.setImageResource(icon);
        binding.itemIcon.setImageTintList(ColorStateList.valueOf(Color.WHITE));
    }

    public void setText(String text) {
        binding.nameLabel.setText(text);
    }

    public TextView getTextView() {
        return binding.nameLabel;
    }
}
