package top.ourfor.app.iPlayClient.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import top.ourfor.app.iPlayClient.R;

public class PlayerNumberValueView extends ConstraintLayout {
    private ImageView iconView;
    private SeekBar seekBar;

    public PlayerNumberValueView(@NonNull Context context) {
        super(context);
        setupUI(context);
    }

    @SuppressLint("ResourceType")
    void setupUI(Context context) {

        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(Color.argb(50, 0, 0, 0));
        gradientDrawable.setCornerRadius(10.f);
        setBackground(gradientDrawable);

        iconView = new ImageView(context);
        iconView.setImageResource(R.drawable.waveform);
        iconView.setImageTintList(ColorStateList.valueOf(Color.WHITE));
        LayoutParams params = new LayoutParams(48, 48);
        params.topToTop = LayoutParams.PARENT_ID;
        params.leftToLeft = LayoutParams.PARENT_ID;
        params.bottomToBottom = LayoutParams.PARENT_ID;
        params.topMargin = 15;
        params.bottomMargin = 15;
        params.leftMargin = 8;
        iconView.setId(8001);
        addView(iconView, params);

        seekBar = new SeekBar(context);
        seekBar.setMax(100);
        params = new LayoutParams(320, 48);
        params.topToTop = LayoutParams.PARENT_ID;
        params.leftToRight = iconView.getId();
        params.bottomToBottom = LayoutParams.PARENT_ID;
        params.topMargin = 15;
        params.bottomMargin = 15;
        addView(seekBar, params);
    }

    public void setProgress(int progress) {
        post(() -> {
            seekBar.setProgress(progress);
        });
    }

    public void setMaxValue(int value) {
        post(() -> {
            seekBar.setMax(value);
        });
    }

    public void updateIcon(int icon) {
        post(() -> {
            iconView.setImageResource(icon);
            iconView.setImageTintList(ColorStateList.valueOf(Color.WHITE));
        });
    }

    public int getProgress() {
        return seekBar.getProgress();
    }

    public void show() {
        animate().alpha(1)
                .setDuration(150)
                .start();
    }

    public void hide() {
        animate().alpha(0)
                .setStartDelay(300)
                .setDuration(200)
                .start();
    }
}
