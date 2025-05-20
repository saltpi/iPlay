package top.ourfor.app.iplay.view.video;

import static top.ourfor.app.iplay.module.Bean.XGET;
import static top.ourfor.app.iplay.module.Bean.XWATCH;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import top.ourfor.app.iplay.R;
import top.ourfor.app.iplay.action.BatteryUpdateAction;
import top.ourfor.app.iplay.bean.INavigator;
import top.ourfor.app.iplay.common.datetime.DateTimeMonitor;
import top.ourfor.app.iplay.common.device.BatteryMonitor;
import top.ourfor.app.iplay.common.network.NetworkMonitor;
import top.ourfor.app.iplay.databinding.PlayerControlBinding;
import top.ourfor.app.iplay.module.GlideApp;
import top.ourfor.app.iplay.util.DateTimeUtil;
import top.ourfor.app.iplay.util.DeviceUtil;
import top.ourfor.app.iplay.util.IdleCaller;
import top.ourfor.app.iplay.util.IntervalCaller;
import top.ourfor.app.iplay.util.LayoutUtil;
import top.ourfor.app.iplay.view.player.Player;

@Setter
@Getter
@Slf4j
public class PlayerControlView extends ConstraintLayout implements PlayerEventListener, BatteryUpdateAction {
    private float delta = 0.25f;
    public PlayerControlItemView playButton;
    public PlayerControlItemView fullscreenButton;
    public PlayerControlItemView subtitleButton;
    public PlayerControlItemView audioButton;
    public PlayerControlItemView videoButton;
    public PlayerControlItemView backwardButton;
    public PlayerControlItemView forwardButton;
    public PlayerControlItemView pipButton;
    public PlayerControlItemView playlistButton;
    public PlayerControlItemView orientationButton;
    public PlayerControlItemView speedButton;
    public PlayerControlItemView commentButton;
    public PlayerControlItemView advanceConfigButton;
    public TextView timeLabel;
    public TextView titleLabel;
    public PlayerSlider progressBar;
    public float speed = 1.0f;
    public View bottomBar;
    public View statusBar;

    PlayerControlBinding binding;
    public int ICON_SMALL_SIZE = (DeviceUtil.width <= 720 ? 16 : 24) * (int) DeviceUtil.density;
    public int ICON_SIZE = (DeviceUtil.width <= 720 ? 24 : 32) * (int) DeviceUtil.density;
    public int ICON_MARGIN_TOP = (DeviceUtil.width <= 720 ? 24 : 48);
    public int PROGRESS_BOTTOM_MARGIN = (DeviceUtil.width <= 720 ? 30 : 100);
    public static final int ICON_TAG = 2;
    public PlayerEventListener delegate;
    private boolean shouldUpdateProgress = true;
    public Player player;
    private String videoTitle;
    private IntervalCaller updateProgressCaller = new IntervalCaller(1000, 0);
    private NetworkMonitor networkMonitor;
    private DateTimeMonitor dateTimeMonitor;
    private BatteryMonitor batteryMonitor;
    private IdleCaller controlIdleCaller = new IdleCaller();

    public PlayerControlView(Context context) {
        super(context);
        binding = PlayerControlBinding.inflate(LayoutInflater.from(context), this, true);
        binding.getRoot().setLayoutParams(LayoutUtil.fill());
        setupUI();
        bind();
    }

    public void setVideoTitle(String value) {
        videoTitle = value;
        binding.titleLabel.setText(value);
    }

    public void updateFullscreenStyle(boolean isFullscreen) {
        if (isFullscreen) {
            updateIcon(binding.exit, R.drawable.arrow_up_right_and_arrow_down_left);
        } else {
            updateIcon(binding.exit, R.drawable.viewfinder);
        }
    }


    public void updatePlayerSlider() {
        var slider = binding.slider;
        int color = Color.WHITE;
        ColorStateList colorStateList = ColorStateList.valueOf(color);
        int thumbRadius = 25;
        GradientDrawable thumb = new GradientDrawable();
        thumb.setShape(GradientDrawable.OVAL);
        thumb.setSize(thumbRadius * 2, thumbRadius * 2);
        thumb.setColor(Color.RED);
        slider.setThumb(thumb);
        slider.setThumbTintList(colorStateList);
        slider.setProgressTintList(colorStateList);
        setPadding(thumbRadius, thumbRadius, thumbRadius, thumbRadius);
    };



    private void setupUI() {
        videoButton = binding.video;
        subtitleButton = binding.caption;
        audioButton = binding.voice;
        progressBar = binding.slider;
        playButton = binding.play;
        fullscreenButton = binding.exit;
        backwardButton = binding.backward;
        forwardButton = binding.forward;
        timeLabel = binding.timeLabel;
        titleLabel = binding.titleLabel;
        statusBar = binding.statusBar;
        bottomBar = binding.bottomBar;
        pipButton = binding.pipEnter;
        playlistButton = binding.playlist;
        orientationButton = binding.orientation;
        speedButton = binding.speed;
        commentButton = binding.comment;
        advanceConfigButton = binding.advanceConfig;
        updatePlayerSlider();
    }

    @SuppressLint("RestrictedApi")
    private void bind() {
        binding.play.setOnClickListener(v -> {
            log.debug("play");
            boolean isPlaying = player != null && player.isPlaying();
            int resId = isPlaying ? com.microsoft.fluent.mobile.icons.R.drawable.ic_fluent_play_48_filled : com.microsoft.fluent.mobile.icons.R.drawable.ic_fluent_pause_48_filled;
            post(() -> updateIcon(binding.play, resId));
            if (isPlaying) {
                player.pause();
            } else {
                player.resume();
            }
        });
        binding.slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                shouldUpdateProgress = false;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                player.seek(binding.slider.getProgress());
                shouldUpdateProgress = true;
            }
        });

        binding.orientation.setOnClickListener(v -> delegate.onOrientationChange());
        binding.exit.setOnClickListener(v -> delegate.onWindowSizeChange());
        binding.caption.setOnClickListener(v -> delegate.onSelectSubtitle());
        binding.video.setOnClickListener(v -> delegate.onSelectVideo());
        binding.voice.setOnClickListener(v -> delegate.onSelectAudio());
        binding.backward.setOnClickListener(v -> player.jumpBackward(15));
        binding.forward.setOnClickListener(v -> player.jumpForward(15));
        binding.speed.setOnClickListener(v -> {
            var popup = new PopupMenu(getContext(), v);
            popup.getMenuInflater().inflate(R.menu.speed_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                speed = Float.parseFloat(item.getTitle().toString().replace("x", ""));
                player.speed(speed);
                binding.speedLabel.setText(item.getTitle());
                return true;
            });
            popup.show();
        });
        binding.advanceConfig.setOnClickListener(v -> delegate.onAdvanceConfig());
        binding.pipEnter.setOnClickListener(v -> delegate.onPipEnter());
        binding.playlist.setOnClickListener(v -> delegate.onTapPlaylist());
        binding.comment.setOnClickListener(v -> delegate.onTapComment());
        networkMonitor = new NetworkMonitor();
        networkMonitor.onStatusUpdate = (speed) -> binding.networkSpeed.post(() -> {
            binding.networkSpeed.setText(speed);
            val netWorkType = NetworkMonitor.getNetWorkType(getContext());
            binding.networkIcon.setImageResource(netWorkType == NetworkMonitor.NetworkType.WIFI ? com.microsoft.fluent.mobile.icons.R.drawable.ic_fluent_wifi_1_24_filled : com.microsoft.fluent.mobile.icons.R.drawable.ic_fluent_cellular_data_1_24_filled);
        });
        networkMonitor.startMonitoring();

        dateTimeMonitor = new DateTimeMonitor();
        dateTimeMonitor.onStatusUpdate = (time) -> binding.datetime.post(() -> binding.datetime.setText(time));
        dateTimeMonitor.startMonitoring();

        batteryMonitor = new BatteryMonitor();
        batteryMonitor.onStatusUpdate = (status) -> binding.battery.post(() -> binding.battery.setText(status));
        batteryMonitor.startMonitoring();

        controlIdleCaller.setInterval(5000);
        controlIdleCaller.setRunnable(() -> {
            post(() -> updateControlVisible(false));
        });

        controlIdleCaller.schedule();
        XWATCH(BatteryUpdateAction.class, this);
    }

    public boolean isViewPresent() {
        return getAlpha() != 0;
    }

    public void toggleVisible() {
        float newAlpha = 1.0f;
        if (getAlpha() == 1.0f) {
            newAlpha = 0.0f;
        }
        if (newAlpha == 1.0f) {
            controlIdleCaller.schedule();
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

        if (visible) {
            controlIdleCaller.schedule();
        }

        animate()
                .alpha(newAlpha)
                .setDuration(800)
                .start();
    }

    private void updateIcon(View view, int resId) {
        if (!(view instanceof PlayerControlItemView)) {
            return;
        }
        val controlView = (PlayerControlItemView) view;
        controlView.setIcon(resId);
    }

    void updateLogo(String logo) {
        GlideApp.with(this)
                .load(logo)
                .into(binding.logo);
    }

    @Override
    public void onPropertyChange(PlayerPropertyType type, Object value) {
        if (value == null) {
            return;
        }
        if (type == PlayerPropertyType.Duration) {
            double duration = (double) value;
            binding.slider.setMax((int) duration);
            binding.timeLabel.setText(Double.toString(duration));
            binding.timeLabel.setText(formatTime(binding.slider.getProgress(), binding.slider.getMax()));
        } else if (type == PlayerPropertyType.TimePos) {
            if (!shouldUpdateProgress) {
                return;
            }

            double time = (double) value;
            updateProgressCaller.invoke(() -> post(() -> {
                binding.slider.setProgress((int) time);
                binding.timeLabel.setText(formatTime(binding.slider.getProgress(), binding.slider.getMax()));
            }));
        } else if (type == PlayerPropertyType.EofReached) {
            boolean isEof = (boolean)value;
            if (isEof) {
                updateIcon(binding.play, com.microsoft.fluent.mobile.icons.R.drawable.ic_fluent_play_48_filled);
            }
        }
    }

    private String formatTime(int current, int total) {
        String part1 = DateTimeUtil.formatTime(current);
        String part2 = DateTimeUtil.formatTime(total);
        return part1 + " / " + part2;
    }

    public void useCloseButton(boolean value) {
        if (!value) return;
        if (DeviceUtil.isTV) {
            binding.exit.setVisibility(View.GONE);
            return;
        }
        updateIcon(binding.exit, com.microsoft.fluent.mobile.icons.R.drawable.ic_fluent_dismiss_24_filled);
        binding.exit.setOnClickListener(v -> {
            XGET(INavigator.class).popPage();
        });
    }

    @Override
    public void onBatteryUpdate(float percent) {
        if (binding == null) return;
        binding.battery.post(() -> binding.battery.setText(percent + "%"));
    }
}
