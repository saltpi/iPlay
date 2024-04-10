package top.ourfor.app.iPlayClient.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import top.ourfor.app.iPlayClient.R;
import top.ourfor.app.iPlayClient.helper.IntervalCaller;

@RequiresApi(api = Build.VERSION_CODES.O)
@Setter
@Getter
@Slf4j
public class PlayerControlView extends ConstraintLayout implements PlayerEventListener {
    public static final int ICON_SMALL_SIZE = 24 * 3;
    public static final int ICON_SIZE = 32 * 3;
    public static final int ICON_TAG = 2;
    public PlayerEventListener delegate;
    private boolean shouldUpdateProgress = true;
    public Player player;
    private String videoTitle;
    private AtomicInteger resId = new AtomicInteger(8000);
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private IntervalCaller updateProgressCaller = new IntervalCaller(1000, 0);

    public PlayerControlView(Context context) {
        super(context);
        setupUI();
        bind();
    }

    public void setVideoTitle(String value) {
        videoTitle = value;
        titleLabel.setText(value);
    }

    public void updateFullscreenStyle(boolean isFullscreen) {
        if (isFullscreen) {
            updateIcon(fullscreenButton, R.drawable.arrow_up_right_and_arrow_down_left);
        } else {
            updateIcon(fullscreenButton, R.drawable.viewfinder);
        }
    }

    public View playButton = new ConstraintLayout(getContext()) {
        {
            ConstraintLayout layout = this;
            ImageView icon = new ImageView(getContext());
            icon.setTag(ICON_TAG);
            icon.setImageResource(R.drawable.pause);
            icon.setImageTintList(ColorStateList.valueOf(Color.WHITE));
            LayoutParams iconLayout = centerLayout();
            iconLayout.width = ICON_SIZE;
            iconLayout.height = ICON_SIZE;
            layout.addView(icon, iconLayout);
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setColor(Color.argb(50, 0, 0, 0));
            gradientDrawable.setCornerRadius(ICON_SIZE + 0f);
            layout.setBackground(gradientDrawable);
            layout.getRootView().setId(resId.getAndIncrement());
            layout.getRootView();
        }
    };

    private LayoutParams playButtonLayout = new LayoutParams(ICON_SIZE * 2, ICON_SIZE * 2) {
        {
            topToTop = LayoutParams.PARENT_ID;
            bottomToBottom = LayoutParams.PARENT_ID;
            rightToRight = LayoutParams.PARENT_ID;
            leftToLeft = LayoutParams.PARENT_ID;
        }
    };

    public View fullscreenButton = new ConstraintLayout(getContext()) {
        {
            ConstraintLayout layout = this;
            ImageView icon = new ImageView(getContext());
            icon.setTag(ICON_TAG);
            icon.setImageResource(R.drawable.viewfinder);
            icon.setImageTintList(ColorStateList.valueOf(Color.WHITE));
            LayoutParams iconLayout = new LayoutParams(centerLayout());
            iconLayout.width = ICON_SMALL_SIZE;
            iconLayout.height = ICON_SMALL_SIZE;
            layout.addView(icon, iconLayout);
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setColor(Color.argb(50, 0, 0, 0));
            gradientDrawable.setCornerRadius(ICON_SMALL_SIZE + 0f);
            layout.setBackground(gradientDrawable);
            layout.getRootView().setId(resId.getAndIncrement());
            layout.getRootView();
        }
    };

    private LayoutParams fullscreenLayout = new LayoutParams(ICON_SMALL_SIZE * 2, ICON_SMALL_SIZE * 2) {
        {
            topToTop = LayoutParams.PARENT_ID;
            rightToRight = LayoutParams.PARENT_ID;
            topMargin = 48;
            rightMargin = 48;
        }
    };

    public View subtitleButton = new ConstraintLayout(getContext()) {
        {
            ConstraintLayout layout = this;
            ImageView icon = new ImageView(getContext());
            icon.setTag(ICON_TAG);
            icon.setImageResource(R.drawable.captions_bubble);
            icon.setImageTintList(ColorStateList.valueOf(Color.WHITE));
            LayoutParams iconLayout = new LayoutParams(centerLayout());
            iconLayout.width = ICON_SMALL_SIZE;
            iconLayout.height = ICON_SMALL_SIZE;
            layout.addView(icon, iconLayout);
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setColor(Color.argb(50, 0, 0, 0));
            gradientDrawable.setCornerRadius(ICON_SMALL_SIZE + 0f);
            layout.setBackground(gradientDrawable);
            layout.getRootView().setId(resId.getAndIncrement());
            layout.getRootView();
        }
    };

    private LayoutParams subtitleButtonLayout = new LayoutParams(ICON_SMALL_SIZE * 2, ICON_SMALL_SIZE * 2) {
        {
            topToTop = LayoutParams.PARENT_ID;
            rightToLeft = fullscreenButton.getId();
            topMargin = 48;
            rightMargin = 48;
        }
    };

    public View audioButton = new ConstraintLayout(getContext()) {
        {
            ConstraintLayout layout = this;
            ImageView icon = new ImageView(getContext());
            icon.setTag(ICON_TAG);
            icon.setImageResource(R.drawable.waveform);
            icon.setImageTintList(ColorStateList.valueOf(Color.WHITE));
            LayoutParams iconLayout = new LayoutParams(centerLayout());
            iconLayout.width = ICON_SMALL_SIZE;
            iconLayout.height = ICON_SMALL_SIZE;
            layout.addView(icon, iconLayout);
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setColor(Color.argb(50, 0, 0, 0));
            gradientDrawable.setCornerRadius(ICON_SMALL_SIZE + 0f);
            layout.setBackground(gradientDrawable);
            layout.getRootView().setId(resId.getAndIncrement());
            layout.getRootView();
        }
    };

    private LayoutParams audioButtonLayout = new LayoutParams(ICON_SMALL_SIZE * 2, ICON_SMALL_SIZE * 2) {
        {
            topToTop = LayoutParams.PARENT_ID;
            rightToLeft = subtitleButton.getId();
            topMargin = 48;
            rightMargin = 48;
        }
    };

    public PlayerSlider progressBar = new PlayerSlider(getContext()) {
        {
            int color = Color.WHITE;
            ColorStateList colorStateList = ColorStateList.valueOf(color);
            int thumbRadius = 25;
            GradientDrawable thumb = new GradientDrawable();
            thumb.setShape(GradientDrawable.OVAL);
            thumb.setSize(thumbRadius * 2, thumbRadius * 2);
            thumb.setColor(Color.RED);
            setThumb(thumb);
            setThumbTintList(colorStateList);
            setProgressTintList(colorStateList);
            setId(resId.getAndIncrement());
            setPadding(thumbRadius, thumbRadius, thumbRadius, thumbRadius);
        }
    };


    private LayoutParams progressBarLayout = new LayoutParams(0, 60) {
        {
            matchConstraintPercentWidth = 0.9f;
            bottomToBottom = LayoutParams.PARENT_ID;
            bottomMargin = 100;
            leftToLeft = LayoutParams.PARENT_ID;
            rightToRight = LayoutParams.PARENT_ID;
        }
    };

    @SuppressLint("AppCompatCustomView")
    private TextView durationLabel = new TextView(getContext()) {
        {
            setTextSize(12.0F);
            setTextColor(Color.WHITE);
            setText(formatTime(0, 0));
            setId(resId.getAndIncrement());
        }
    };

    private LayoutParams durationLayout = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT) {
        {
            rightToRight = progressBar.getId();
            bottomToTop = progressBar.getId();
            bottomMargin = 10;
        }
    };

    @SuppressLint("AppCompatCustomView")
    public TextView titleLabel = new TextView(getContext()) {
        {
            setTextSize(14.0F);
            setTextColor(Color.WHITE);
            setId(resId.getAndIncrement());
        }
    };

    private LayoutParams titleLayout = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT) {
        {
            leftToLeft = progressBar.getId();
            bottomToTop = progressBar.getId();
            matchConstraintPercentWidth = 0.75f;
            bottomMargin = 10;
        }
    };

    private void setupUI() {
        addView(playButton, playButtonLayout);
        addView(fullscreenButton, fullscreenLayout);
        addView(subtitleButton, subtitleButtonLayout);
        addView(audioButton, audioButtonLayout);
        addView(progressBar, progressBarLayout);
        addView(durationLabel, durationLayout);
        addView(titleLabel, titleLayout);
    }

    private void bind() {
        playButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                log.debug("play");
                boolean isPlaying = player != null && player.isPlaying();
                int resId = isPlaying ? R.drawable.play : R.drawable.pause;
                post(() -> updateIcon(playButton, resId));
                if (isPlaying) {
                    player.pause();
                } else {
                    player.resume();
                }
            }
        });
        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                shouldUpdateProgress = false;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                player.seek(progressBar.getProgress());
                shouldUpdateProgress = true;
            }
        });
        fullscreenButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                delegate.onWindowSizeChange();
            }
        });

        subtitleButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                delegate.onSelectSubtitle();
            }
        });

        audioButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                delegate.onSelectAudio();
            }
        });
    }

    public void toggleVisible() {
        float newAlpha = 1.0f;
        if (getAlpha() == 1.0f) {
            newAlpha = 0.0f;
        }
        animate()
                .alpha(newAlpha)
                .setDuration(800)
                .start();
    }

    public void updateControlVisible(boolean visible) {
        float newAlpha = 0.0f;
        if (visible) {
            newAlpha = 1.0f;
        }
        if (newAlpha == getAlpha()) {
            return;
        }

        animate()
                .alpha(newAlpha)
                .setDuration(800)
                .start();
    }

    private void updateIcon(View view, int resId) {
        ImageView imageView = view.findViewWithTag(ICON_TAG);
        if (!(imageView instanceof ImageView)) {
            return;
        }
        imageView.setImageResource(resId);
        imageView.setImageTintList(ColorStateList.valueOf(Color.WHITE));
    }

    @Override
    public void onPropertyChange(PlayerPropertyType name, Object value) {
        if (value == null) {
            return;
        }
        if (name == PlayerPropertyType.Duration) {
            double duration = (double) value;
            progressBar.setMax((int) duration);
            durationLabel.setText(Double.toString(duration));
            durationLabel.setText(formatTime(progressBar.getProgress(), progressBar.getMax()));
        } else if (name == PlayerPropertyType.TimePos) {
            if (!shouldUpdateProgress) {
                return;
            }

            double time = (double) value;
            updateProgressCaller.invoke(() -> post(() -> {
                progressBar.setProgress((int) time);
                durationLabel.setText(formatTime(progressBar.getProgress(), progressBar.getMax()));
            }));
        } else if (name == PlayerPropertyType.EofReached) {
            boolean isEof = (boolean)value;
            if (isEof) {
                updateIcon(playButton, R.drawable.play);
            }
        }
    }

    private String formatTime(int current, int total) {
        Duration duration = Duration.ofSeconds(current);
        LocalDateTime time = LocalDateTime.MIN.plus(duration);
        String part1 = time.format(dateFormatter);
        duration = Duration.ofSeconds(total);
        time = LocalDateTime.MIN.plus(duration);
        String part2 = time.format(dateFormatter);
        return part1 + " / " + part2;
    }

    public static LayoutParams centerLayout() {
        LayoutParams centerParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        centerParams.topToTop = LayoutParams.PARENT_ID;
        centerParams.leftToLeft = LayoutParams.PARENT_ID;
        centerParams.rightToRight = LayoutParams.PARENT_ID;
        centerParams.bottomToBottom = LayoutParams.PARENT_ID;
        return centerParams;
    }
}