package top.ourfor.app.iplayx.view.video;

import static top.ourfor.app.iplayx.module.Bean.XGET;
import static top.ourfor.app.iplayx.module.Bean.XSET;
import static top.ourfor.lib.mpv.TrackItem.AudioTrackName;
import static top.ourfor.lib.mpv.TrackItem.SubtitleTrackName;
import static top.ourfor.lib.mpv.TrackItem.VideoTrackName;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import top.ourfor.app.iplayx.R;
import top.ourfor.app.iplayx.module.Bean;
import top.ourfor.app.iplayx.module.FontModule;
import top.ourfor.app.iplayx.module.GlideApp;
import top.ourfor.app.iplayx.util.DeviceUtil;
import top.ourfor.app.iplayx.util.IntervalCaller;
import top.ourfor.app.iplayx.util.WindowUtil;
import top.ourfor.app.iplayx.view.player.Player;
import top.ourfor.app.iplayx.view.player.PlayerEventType;
import top.ourfor.lib.mpv.SeekableRange;
import top.ourfor.lib.mpv.TrackItem;


@Slf4j
public class PlayerView extends ConstraintLayout
        implements PlayerEventListener,
        PlayerEventDelegate,
        PlayerSelectDelegate<PlayerSelectModel<TrackItem>> {
    private static final List<Integer> screenOrientations = List.of(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE, ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
    String subtitleFontName = null;
    @Getter
    private PlayerControlView controlView;
    private PlayerContentView contentView;
    private PlayerEventView eventView;
    @Getter
    private PlayerCommentView commentView;
    private ImageView backdropView;
    private PlayerFullscreenView fullscreenView;
    private boolean isFullscreen = false;
    private double duration = 0.0;
    private double position = 0.0;
    private double brightnessValue = 0;
    private double volumeValue = 0;
    @Setter
    private Consumer<HashMap<String, Object>> onPlayStateChange;
    private IntervalCaller cachedProgressCaller = new IntervalCaller(500, 0);
    private String url;
    private String title;
    private Map<String, String> option;
    private List<PlayerSourceModel> sources;
    private int orientation = 0;

    @Setter
    private Consumer<PlayerView> onPlaylistTap;

    public void setSubtitleFontName(String value) {
        contentView.viewModel.setSubtitleFontName(value);
    }

    public void setOption(Map<String, String> option) {
        this.option = option;
        contentView.viewModel.applyOption(option);
    }

    public void setSources(List<PlayerSourceModel> sources) {
        log.info("sources", sources);
        this.sources = sources;
        ArrayList<PlayerSourceModel> subtitles = new ArrayList<>();
        for (var source : sources) {
            PlayerSourceModel.PlayerSourceType type = source.getType();
            if (type == PlayerSourceModel.PlayerSourceType.LogoImage) {
                controlView.updateLogo(source.getValue());
            } else if (type == PlayerSourceModel.PlayerSourceType.PosterImage) {
                updateBackdrop(source.getUrl());
            } else if (type == PlayerSourceModel.PlayerSourceType.Subtitle) {
                subtitles.add(source);
            } else if (type == PlayerSourceModel.PlayerSourceType.Title) {
                setTitle(source.getValue());
            }
        }
        if (subtitles.size() > 0) {
            contentView.viewModel.loadSubtitle(subtitles);
        }
    }

    public void setUrl(String url) {
        this.url = url;
        if (url != null) {
            log.info("play url {}", url);
            contentView.playFile(url);
            eventView.showLoadIndicator(true);
        }
    }

    public void setUrl(String url, String audioUrl) {
        this.url = url;
        if (url != null) {
            log.info("play url {}", url);
            contentView.playFile(url, audioUrl);
            eventView.showLoadIndicator(true);
        }
    }

    public void setTitle(String title) {
        this.title = title;
        if (title != null) {
            log.info("video title {}", title);
            controlView.setVideoTitle(title);
        }
    }

    public PlayerView(@NonNull Context context) throws IOException {
        this(context, null);
    }

    public PlayerView(@NonNull Context context, String url) throws IOException {
        super(context);
        setupUI(context, url);
        XSET(Player.class, this.contentView.viewModel);
    }

    void setupUI(Context context, String url) throws IOException {
        setBackgroundColor(Color.BLACK);
        backdropView = new ImageView(context);
        val backdropLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        addView(backdropView, backdropLayoutParams);

        contentView = new PlayerContentView(context);
        contentView.setZOrderOnTop(true);
        val player = contentView;
        val contentLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        contentLayoutParams.topToTop = LayoutParams.PARENT_ID;
        contentLayoutParams.bottomToBottom = LayoutParams.PARENT_ID;
        contentLayoutParams.leftToLeft = LayoutParams.PARENT_ID;
        contentLayoutParams.rightToRight = LayoutParams.PARENT_ID;
        addView(contentView, contentLayoutParams);

        copySubtitleFont(context.getFilesDir().getPath());

        player.initialize(
                context.getFilesDir().getPath(),
                context.getCacheDir().getPath(),
                FontModule.getFontPath(context)
        );
        val viewModel = player.viewModel;
        viewModel.setCachePath(getContext().getCacheDir().getPath());
        if (subtitleFontName != null) {
            viewModel.setSubtitleFontName(subtitleFontName);
        }
        viewModel.setDelegate(this);
        if (url != null) player.playFile(url);

        commentView = new PlayerCommentView(context);
        addView(commentView, contentLayoutParams);

        val controlView = new PlayerControlView(context);
        val controlLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        controlLayoutParams.topToTop = LayoutParams.PARENT_ID;
        controlLayoutParams.bottomToBottom = LayoutParams.PARENT_ID;
        controlLayoutParams.leftToLeft = LayoutParams.PARENT_ID;
        controlLayoutParams.rightToRight = LayoutParams.PARENT_ID;
        controlView.player = viewModel;
        controlView.delegate = this;
        addView(controlView, controlLayoutParams);
        this.controlView = controlView;

        eventView = new PlayerEventView(context);
        eventView.ignoreAreas = List.of(
                controlView.playButton,
                controlView.fullscreenButton,
                controlView.progressBar,
                controlView.subtitleButton,
                controlView.audioButton,
                controlView.videoButton,
                controlView.backwardButton,
                controlView.forwardButton,
                controlView.statusBar,
                controlView.bottomBar,
                controlView.pipButton,
                controlView.playlistButton,
                controlView.orientationButton,
                controlView.speedButton
        );
        eventView.delegate = this;
        eventView.trackSelectDelegate = this;
        eventView.controlView = new WeakReference<>(controlView);
        val eventLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        eventLayoutParams.topToTop = LayoutParams.PARENT_ID;
        eventLayoutParams.bottomToBottom = LayoutParams.PARENT_ID;
        eventLayoutParams.leftToLeft = LayoutParams.PARENT_ID;
        eventLayoutParams.rightToRight = LayoutParams.PARENT_ID;
        addView(eventView, eventLayoutParams);

        fullscreenView = new PlayerFullscreenView(
                context,
                backdropView,
                contentView,
                controlView,
                eventView
        );
        fullscreenView.getWindow().setWindowAnimations(android.R.style.Animation_Dialog);
        controlView.setAlpha(0.0f);
        setKeepScreenOn(true);
        post(() -> {
            eventView.showLoadIndicator(true);
        });
    }

    public void useCloseButton(boolean value) {
        controlView.useCloseButton(value);
    }

    private void updateBackdrop(String backdrop) {
        GlideApp.with(this)
                .load(backdrop)
                .into(backdropView);
    }

    @Override
    public void onEvent(PlayerGestureType type, Object value) {
        switch (type) {
            case None:
                brightnessValue = getBrightnessValue();
                volumeValue = getVolumeValue();
                PlayerGestureType targetType = (PlayerGestureType) value;
                if (targetType == PlayerGestureType.Brightness) {
                    eventView.numberValueView.setMaxValue(getBrightnessMaxValue());
                    eventView.numberValueView.updateIcon(R.drawable.lightbulb_min);
                    eventView.numberValueView.show();
                } else if (targetType == PlayerGestureType.Volume) {
                    eventView.numberValueView.setMaxValue(getVolumeMaxValue());
                    eventView.numberValueView.updateIcon(R.drawable.waveform);
                    eventView.numberValueView.show();
                }
                break;
            case HideControl:
                eventView.numberValueView.hide();
                if (eventView.isSelectViewPresent()) {
                    eventView.closeSelectView();
                } else {
                    controlView.toggleVisible();
                }
                break;
            case Seek:
                int delta = 10;
                if ((Float) value > 0) {
                    contentView.viewModel.jumpForward(delta);
                } else {
                    contentView.viewModel.jumpBackward(delta);
                }
                break;
            case Volume:
                delta = ((Float) value).intValue();
                setVolumeValue((int) (volumeValue + delta));
                eventView.numberValueView.setProgress((int) (volumeValue + delta));
                break;
            case Brightness:
                delta = ((Float) value).intValue();
                setBrightnessValue((int) (brightnessValue + delta));
                eventView.numberValueView.setProgress((int) (brightnessValue + delta));
                break;
            default:
                // Handle null or other cases
                break;
        }
    }

    @Override
    public void onPropertyChange(PlayerPropertyType name, Object value) {
        post(() -> controlView.onPropertyChange(name, value));

        if (value == null) {
            return;
        }

        if (name == PlayerPropertyType.TimePos ||
            name == PlayerPropertyType.PausedForCache||
            name == PlayerPropertyType.Pause) {
            PlayerEventType.PlayEventType state = PlayerEventType.PlayEventType.PlayEventTypeOnProgress;
            HashMap<String, Object> data = new HashMap<>();
            if (name == PlayerPropertyType.TimePos) {
                state = PlayerEventType.PlayEventType.PlayEventTypeOnProgress;
                position = (Double) value;
                data.put("duration", duration);
                data.put("position", position);
                if (position > 0 && duration > 0) {
                    post(() -> {
                        contentView.setZOrderOnTop(false);
                        eventView.showLoadIndicator(false);
                    });

                    if (commentView.getDisplayComment().getValue() &&
                        commentView.getCurrentTime().getValue() + 10 < position) {
                        commentView.getCurrentTime().postValue((float) position);
                    }
                }
            } else if (name == PlayerPropertyType.Pause) {
                state = PlayerEventType.PlayEventType.PlayEventTypeOnPause;
                data.put("duration", duration);
                data.put("position", position);
            } else {
                state = PlayerEventType.PlayEventType.PlayEventTypeOnPauseForCache;
                var isPause = (boolean)value;
                if (eventView != null) {
                    post(() -> {
                        eventView.showLoadIndicator(isPause);
                    });
                }
            }

            data.put("type", state.value);
            if (onPlayStateChange != null) {
                onPlayStateChange.accept(data);
            }
        } else if (name == PlayerPropertyType.Duration) {
            duration = (Double) value;
        } else if (name == PlayerPropertyType.DemuxerCacheState) {
            if (!(value instanceof SeekableRange[])) {
                return;
            }
            val ranges = (SeekableRange[])value;
            double maxValue = duration;
            cachedProgressCaller.invoke(() -> post(() -> controlView.progressBar.setRanges(ranges, maxValue)));
        }
    }

    @Override
    public void onWindowSizeChange() {
        Activity activity = getContext() instanceof Activity ? (Activity)getContext() : null;
        if (activity == null) return;
        Window window = activity.getWindow();

        if (isFullscreen) {
            if (fullscreenView != null) fullscreenView.dismiss();
            requestLayout();
            WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(window, window.getDecorView());
            WindowCompat.setDecorFitsSystemWindows(window, true);
            controller.show(WindowInsetsCompat.Type.systemBars());
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            if (fullscreenView != null) fullscreenView.show();
            WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(window, window.getDecorView());
            WindowCompat.setDecorFitsSystemWindows(window, false);
            controller.hide(WindowInsetsCompat.Type.systemBars());
            controller.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            if (fullscreenView != null) {
                fullscreenView.setOnDismissListener(dialog -> {
                    WindowInsetsControllerCompat controller1 = new WindowInsetsControllerCompat(window, window.getDecorView());
                    WindowCompat.setDecorFitsSystemWindows(window, true);
                    controller1.show(WindowInsetsCompat.Type.systemBars());
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    boolean isTablet = DeviceUtil.isTablet(getContext());
                    activity.setRequestedOrientation(isTablet ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    if (controlView != null) controlView.updateFullscreenStyle(false);
                });
            }
        }

        isFullscreen = !isFullscreen;
        if (controlView != null) controlView.updateFullscreenStyle(isFullscreen);

        if (isFullscreen) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            boolean isTablet = DeviceUtil.isTablet(getContext());
            activity.setRequestedOrientation(isTablet ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void onPipEnter() {
        controlView.updateControlVisible(false);
        XGET(Activity.class).enterPictureInPictureMode();
    }

    @Override
    public void onSelectSubtitle() {
        var player = contentView.viewModel;
        var currentSubtitleId = player.currentSubtitleId();
        var subtitles = (List<TrackItem>)player.subtitles();
        controlView.updateControlVisible(false);
        eventView.showSelectView(subtitles, currentSubtitleId);
    }

    @Override
    public void onSelectVideo() {
        var player = contentView.viewModel;
        var currentSubtitleId = player.currentSubtitleId();
        var videos = sources.stream().filter(source-> source.getType().equals(PlayerSourceModel.PlayerSourceType.Video))
                .map(source -> TrackItem.builder()
                .id(source.getUrl())
                .lang(source.getName())
                .type(VideoTrackName)
                .build()).collect(Collectors.toList());
        controlView.updateControlVisible(false);
        eventView.showSelectView(videos, currentSubtitleId);
    }

    @Override
    public void onSelectAudio() {
        var player = contentView.viewModel;
        var currentAudioId = player.currentAudioId();
        var audios = (List<TrackItem>)player.audios();
        controlView.updateControlVisible(false);
        eventView.showSelectView(audios, currentAudioId);
    }

    void copySubtitleFont(String configDir) throws IOException {
        val ins = getContext().getAssets().open("subfont.ttf", AssetManager.ACCESS_STREAMING);
        val outFile = new File(configDir + "/subfont.ttf");
        val out = new FileOutputStream(outFile);
        if (outFile.length() == ins.available()) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ins.transferTo(out);
        } else {
            byte[] buffer = new byte[1024];
            int length = -1;
            while ((length = ins.read(buffer)) != -1) {
                out.write(buffer, 0, length);
            }
        }
        ins.close();
        out.close();
    }

    @Override
    public void onTapPlaylist() {
        if (onPlaylistTap != null) {
            onPlaylistTap.accept(this);
        }
    }

    @Override
    public void onTapComment() {
        commentView.displayComment.postValue(!commentView.displayComment.getValue());
    }

    @Override
    public void onOrientationChange() {
        orientation = (orientation + 1) % screenOrientations.size();
        WindowUtil.enterFullscreen(screenOrientations.get(orientation));
    }

    @Override
    public void onClose() {
        PlayerSelectDelegate.super.onClose();
    }

    @Override
    public void onSelect(PlayerSelectModel<TrackItem> data) {
        val item = data.getItem();
        if (item == null) return;
        if (item.type.equals(SubtitleTrackName)) {
            contentView.viewModel.useSubtitle(item.id);
        } else if (item.type.equals(AudioTrackName)) {
            contentView.viewModel.useAudio(item.id);
        } else if (item.type.equals(VideoTrackName)) {
            contentView.viewModel.useVideo(item.id);
        }
    }

    @Override
    public void onDeselect(PlayerSelectModel<TrackItem> data) {
        PlayerSelectDelegate.super.onDeselect(data);
    }

    @Override
    protected void onDetachedFromWindow() {
        setKeepScreenOn(false);
        contentView.viewModel.destroy();
        super.onDetachedFromWindow();
    }

    public int getBrightnessValue() {
        final int defVal = 50;
        Window window = getWindow();
        if (window != null) {
            Float value = window.getAttributes().screenBrightness;
            if (value != null) return (int) (value * 100);
        }
        return defVal;
    }

    public void setBrightnessValue(int value) {
        int newValue = Math.min(Math.max(0, value), 100);
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.screenBrightness = newValue / 100.0f;
            window.setAttributes(attributes);
        }
    }

    public int getBrightnessMaxValue() {
        return 100;
    }

    public int getVolumeMaxValue() {
        AudioManager audioService = (AudioManager)getContext().getSystemService(Context.AUDIO_SERVICE);
        return audioService.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    public int getVolumeValue() {
        AudioManager audioService = (AudioManager)getContext().getSystemService(Context.AUDIO_SERVICE);
        return audioService.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    public void setVolumeValue(int value) {
        AudioManager audioService = (AudioManager)getContext().getSystemService(Context.AUDIO_SERVICE);
        int maxValue = audioService.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int newValue = Math.min(Math.max(0, value), maxValue);
        audioService.setStreamVolume(AudioManager.STREAM_MUSIC, newValue, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
    }

    private Window getWindow() {
        Activity activity = getContext() instanceof Activity ? (Activity)getContext() : null;
        return activity != null ? activity.getWindow() : null;
    }

    public void onHostDestroy() {
        contentView.viewModel.destroy();
        Bean.remove(Player.class);
        ViewGroup parent = (ViewGroup)getParent();
        parent.removeView(this);
        setKeepScreenOn(false);
    }

    @Override
    public PlayerContentView getContentView() {
        return contentView;
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
        post(() -> {
            measure(
                    MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.EXACTLY)
            );
            layout(getLeft(), getTop(), getRight(), getBottom());
        });
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        if (eventView.isSelectViewPresent()) {
            if (keyCode == KeyEvent.KEYCODE_BACK && action == KeyEvent.ACTION_DOWN) {
                eventView.closeSelectView();
                return true;
            } else {
                return super.dispatchKeyEvent(event);
            }
        }

        if (controlView.isViewPresent()) {
            if (keyCode == KeyEvent.KEYCODE_BACK && action == KeyEvent.ACTION_DOWN) {
                controlView.toggleVisible();
                return true;
            } else {
                return super.dispatchKeyEvent(event);
            }
        }

        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    controlView.toggleVisible();
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (action == KeyEvent.ACTION_DOWN) {
                    contentView.viewModel.jumpBackward(10);
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            if (action == KeyEvent.ACTION_DOWN) {
                contentView.viewModel.jumpForward(10);
            }
                return true;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                if (action == KeyEvent.ACTION_DOWN) {
                    if (contentView.viewModel.isPlaying()) {
                        contentView.viewModel.pause();
                    } else {
                        contentView.viewModel.resume();
                    }
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }

    public void setLastWatchPosition(long lastWatchPosition) {
        contentView.viewModel.setLastWatchPosition(lastWatchPosition);
    }
}
