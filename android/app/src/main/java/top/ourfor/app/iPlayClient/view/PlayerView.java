package top.ourfor.app.iPlayClient.view;

import static top.ourfor.lib.mpv.TrackItem.AudioTrackName;
import static top.ourfor.lib.mpv.TrackItem.SubtitleTrackName;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.os.Build;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.uimanager.ThemedReactContext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import top.ourfor.app.iPlayClient.R;
import top.ourfor.app.iPlayClient.helper.IntervalCaller;
import top.ourfor.app.iPlayClient.module.FontModule;
import top.ourfor.app.iPlayClient.view.Player.PlayEventType;
import top.ourfor.lib.mpv.SeekableRange;
import top.ourfor.lib.mpv.TrackItem;


@Slf4j
@RequiresApi(api = Build.VERSION_CODES.O)
public class PlayerView extends ConstraintLayout
        implements LifecycleEventListener,
        PlayerEventListener,
        PlayerEventDelegate,
        PlayerSelectDelegate<PlayerSelectModel<TrackItem>> {
    String subtitleFontName = null;
    private PlayerControlView controlView;
    private PlayerContentView contentView;
    private PlayerEventView eventView;
    private PlayerFullscreenView fullscreenView;
    private boolean isFullscreen = false;
    private double duration = 0.0;
    private double position = 0.0;
    private double brightnessValue = 0;
    private double volumeValue = 0;
    @Setter
    private ThemedReactContext themedReactContext;
    @Setter
    private Consumer<HashMap<String, Object>> onPlayStateChange;
    private IntervalCaller cachedProgressCaller = new IntervalCaller(500, 0);
    private String url;
    private String title;

    public void setSubtitleFontName(String value) {
        contentView.viewModel.setSubtitleFontName(value);
    }

    public void setUrl(String url) {
        this.url = url;
        if (url != null) {
            log.info("play url {}", url);
            contentView.playFile(url);
        }
    }

    public void setTitle(String title) {
        this.title = title;
        if (title != null) {
            log.info("video title {}", title);
            controlView.setVideoTitle(title);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public PlayerView(@NonNull Context context, String url) throws IOException {
        super(context);
        setupUI(context, url);
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    void setupUI(Context context, String url) throws IOException {
        ((ReactContext)context).addLifecycleEventListener(this);

        contentView = new PlayerContentView(context);
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
        if (subtitleFontName != null) {
            viewModel.setSubtitleFontName(subtitleFontName);
        }
        viewModel.setDelegate(this);
        if (url != null) player.playFile(url);

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
                controlView.audioButton
        );
        eventView.delegate = this;
        eventView.trackSelectDelegate = this;
        val eventLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        eventLayoutParams.topToTop = LayoutParams.PARENT_ID;
        eventLayoutParams.bottomToBottom = LayoutParams.PARENT_ID;
        eventLayoutParams.leftToLeft = LayoutParams.PARENT_ID;
        eventLayoutParams.rightToRight = LayoutParams.PARENT_ID;
        addView(eventView, eventLayoutParams);

        fullscreenView = new PlayerFullscreenView(
                context,
                contentView,
                controlView,
                eventView
        );
        fullscreenView.getWindow().setWindowAnimations(android.R.style.Animation_Dialog);
        setKeepScreenOn(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onPropertyChange(PlayerPropertyType name, Object value) {
        post(() -> controlView.onPropertyChange(name, value));

        if (value == null) {
            return;
        }

        if (name == PlayerPropertyType.TimePos ||
            name == PlayerPropertyType.PausedForCache||
            name == PlayerPropertyType.Pause) {
            PlayEventType state = PlayEventType.PlayEventTypeOnProgress;
            HashMap<String, Object> data = new HashMap<>();
            if (name == PlayerPropertyType.TimePos) {
                state = PlayEventType.PlayEventTypeOnProgress;
                position = (Double) value;
                data.put("duration", duration);
                data.put("position", position);
            } else if (name == PlayerPropertyType.Pause) {
                state = PlayEventType.PlayEventTypeOnPause;
                data.put("duration", duration);
                data.put("position", position);
            } else {
                state = PlayEventType.PlayEventTypeOnPauseForCache;
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onWindowSizeChange() {
        Activity activity = themedReactContext != null ? themedReactContext.getCurrentActivity() : null;
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
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    if (controlView != null) controlView.updateFullscreenStyle(false);
                });
            }
        }

        isFullscreen = !isFullscreen;
        if (controlView != null) controlView.updateFullscreenStyle(isFullscreen);

        if (isFullscreen) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onSelectSubtitle() {
        var player = contentView.viewModel;
        var currentSubtitleId = player.currentSubtitleId();
        var subtitles = (List<TrackItem>)player.subtitles();
        controlView.updateControlVisible(false);
        eventView.showSelectView(subtitles, currentSubtitleId);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
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
        Activity activity = themedReactContext.getCurrentActivity();
        return activity != null ? activity.getWindow() : null;
    }

    @Override
    public void onHostResume() {

    }

    @Override
    public void onHostPause() {

    }

    @Override
    public void onHostDestroy() {
        ReactContext context = (ReactContext) getContext();
        context.removeLifecycleEventListener(this);
        contentView.viewModel.destroy();
        ViewGroup parent = (ViewGroup)getParent();
        parent.removeView(this);
        setKeepScreenOn(false);
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
}
