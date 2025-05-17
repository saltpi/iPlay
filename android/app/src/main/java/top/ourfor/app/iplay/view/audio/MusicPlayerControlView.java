package top.ourfor.app.iplay.view.audio;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import top.ourfor.app.iplay.R;
import top.ourfor.app.iplay.common.datetime.DateTimeMonitor;
import top.ourfor.app.iplay.common.device.BatteryMonitor;
import top.ourfor.app.iplay.common.network.NetworkMonitor;
import top.ourfor.app.iplay.databinding.MusicPlayerControlBinding;
import top.ourfor.app.iplay.util.DateTimeUtil;
import top.ourfor.app.iplay.util.DeviceUtil;
import top.ourfor.app.iplay.util.IntervalCaller;
import top.ourfor.app.iplay.util.LayoutUtil;
import top.ourfor.app.iplay.view.player.Player;
import top.ourfor.app.iplay.view.video.PlayerControlItemView;
import top.ourfor.app.iplay.view.video.PlayerEventListener;
import top.ourfor.app.iplay.view.video.PlayerPropertyType;
import top.ourfor.app.iplay.view.video.PlayerSlider;

@Setter
@Getter
@Slf4j
public class MusicPlayerControlView extends ConstraintLayout implements PlayerEventListener {
    private float delta = 0.25f;
    public PlayerControlItemView playButton;
    public PlayerControlItemView subtitleButton;
    public PlayerControlItemView audioButton;
    public PlayerControlItemView backwardButton;
    public PlayerControlItemView forwardButton;
    public PlayerControlItemView pipButton;
    public PlayerControlItemView playlistButton;
    public TextView elapseTimeLabel;
    public TextView remainTimeLabel;
    public TextView titleLabel;
    public ImageView poster;
    public PlayerSlider progressBar;
    public float speed = 1.0f;
    public View rightBar;
    public View bottomBar;
    public View statusBar;

    MusicPlayerControlBinding binding;
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

    public MusicPlayerControlView(Context context) {
        super(context);
        binding = MusicPlayerControlBinding.inflate(LayoutInflater.from(context), this, true);
        binding.getRoot().setLayoutParams(LayoutUtil.fill());
        setupUI();
        bind();
    }

    public void setVideoTitle(String value) {
        videoTitle = value;
        binding.titleLabel.setText(value);
    }

    public void updateFullscreenStyle(boolean isFullscreen) {
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
        subtitleButton = binding.caption;
        audioButton = binding.voice;
        progressBar = binding.slider;
        playButton = binding.play;
        backwardButton = binding.backward;
        forwardButton = binding.forward;
        elapseTimeLabel = binding.elapseLabel;
        titleLabel = binding.titleLabel;
        pipButton = binding.pipEnter;
        playlistButton = binding.playlist;
        remainTimeLabel = binding.durationLabel;
        poster = binding.musicPoster;
        updatePlayerSlider();
    }

    private void bind() {
        binding.play.setOnClickListener(v -> {
            log.debug("play");
            boolean isPlaying = player != null && player.isPlaying();
            int resId = isPlaying ? R.drawable.play : R.drawable.pause;
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

        binding.caption.setOnClickListener(v -> delegate.onSelectSubtitle());
        binding.voice.setOnClickListener(v -> delegate.onSelectAudio());
        binding.backward.setOnClickListener(v -> player.jumpBackward(15));
        binding.forward.setOnClickListener(v -> player.jumpForward(15));
        binding.pipEnter.setOnClickListener(v -> delegate.onPipEnter());
        binding.playlist.setOnClickListener(v -> delegate.onTapPlaylist());
    }

    public boolean isViewPresent() {
        return getAlpha() != 0;
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
        if (!(view instanceof PlayerControlItemView)) {
            return;
        }
        val controlView = (PlayerControlItemView) view;
        controlView.setIcon(resId);
    }

    @Override
    public void onPropertyChange(PlayerPropertyType type, Object value) {
        if (value == null) {
            return;
        }
        if (type == PlayerPropertyType.Duration) {
            double duration = (double) value;
            binding.slider.setMax((int) duration);
            binding.elapseLabel.setText(Double.toString(duration));
            binding.elapseLabel.setText(formatTime(binding.slider.getProgress()));
            binding.durationLabel.setText(formatTime(binding.slider.getMax()));
        } else if (type == PlayerPropertyType.TimePos) {
            if (!shouldUpdateProgress) {
                return;
            }

            double time = (double) value;
            updateProgressCaller.invoke(() -> post(() -> {
                binding.slider.setProgress((int) time);
                binding.elapseLabel.setText(formatTime(binding.slider.getProgress()));
                binding.durationLabel.setText(formatTime(binding.slider.getMax()));
            }));
        } else if (type == PlayerPropertyType.EofReached) {
            boolean isEof = (boolean)value;
            if (isEof) {
                updateIcon(binding.play, R.drawable.play);
            }
        }
    }

    private String formatTime(int current) {
        String part1 = DateTimeUtil.formatTime(current);
        return part1;
    }

    public void useCloseButton(boolean value) {

    }
}
