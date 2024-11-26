package top.ourfor.app.iplayx.view.audio;

import static top.ourfor.app.iplayx.module.Bean.XGET;
import static top.ourfor.app.iplayx.module.Bean.XSET;
import static top.ourfor.lib.mpv.TrackItem.AudioTrackName;
import static top.ourfor.lib.mpv.TrackItem.SubtitleTrackName;
import static top.ourfor.lib.mpv.TrackItem.VideoTrackName;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.os.Build;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import top.ourfor.app.iplayx.R;
import top.ourfor.app.iplayx.module.Bean;
import top.ourfor.app.iplayx.module.FontModule;
import top.ourfor.app.iplayx.module.GlideApp;
import top.ourfor.app.iplayx.util.DeviceUtil;
import top.ourfor.app.iplayx.util.IntervalCaller;
import top.ourfor.app.iplayx.util.LayoutUtil;
import top.ourfor.app.iplayx.view.player.Player;
import top.ourfor.app.iplayx.view.player.PlayerEventType;
import top.ourfor.app.iplayx.view.video.PlayerContentView;
import top.ourfor.app.iplayx.view.video.PlayerEventDelegate;
import top.ourfor.app.iplayx.view.video.PlayerEventListener;
import top.ourfor.app.iplayx.view.video.PlayerEventView;
import top.ourfor.app.iplayx.view.video.PlayerGestureType;
import top.ourfor.app.iplayx.view.video.PlayerPropertyType;
import top.ourfor.app.iplayx.view.video.PlayerSelectDelegate;
import top.ourfor.app.iplayx.view.video.PlayerSelectModel;
import top.ourfor.app.iplayx.view.video.PlayerSourceModel;
import top.ourfor.lib.mpv.SeekableRange;
import top.ourfor.lib.mpv.TrackItem;


@Slf4j
public class MusicPlayerView extends ConstraintLayout
        implements PlayerEventListener,
        PlayerEventDelegate,
        PlayerSelectDelegate<PlayerSelectModel<TrackItem>> {
    String subtitleFontName = null;
    private MusicPlayerControlView controlView;
    private PlayerContentView contentView;
    private PlayerEventView eventView;
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

    @Setter
    private Consumer<MusicPlayerView> onPlaylistTap;

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

            } else if (type == PlayerSourceModel.PlayerSourceType.PosterImage) {
                updateBackdrop(source.getValue());
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

    public void setTitle(String title) {
        this.title = title;
        if (title != null) {
            log.info("video title {}", title);
            controlView.setVideoTitle(title);
        }
    }

    public MusicPlayerView(@NonNull Context context) throws IOException {
        this(context, null);
    }

    public MusicPlayerView(@NonNull Context context, String url) throws IOException {
        super(context);
        setupUI(context, url);
        XSET(Player.class, this.contentView.viewModel);
    }

    void setupUI(Context context, String url) throws IOException {
        contentView = new PlayerContentView(context);
        val player = contentView;
        addView(contentView, LayoutUtil.fill());

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

        val controlView = new MusicPlayerControlView(context);
        controlView.player = viewModel;
        controlView.delegate = this;
        addView(controlView, LayoutUtil.fill());
        this.controlView = controlView;

        eventView = new PlayerEventView(context);
        eventView.ignoreAreas = List.of(
                controlView.playButton,
                controlView.progressBar,
                controlView.subtitleButton,
                controlView.audioButton,
                controlView.backwardButton,
                controlView.forwardButton,
                controlView.pipButton,
                controlView.playlistButton,
                controlView.poster
        );
        eventView.delegate = this;
        eventView.trackSelectDelegate = this;
        val eventLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        eventLayoutParams.topToTop = LayoutParams.PARENT_ID;
        eventLayoutParams.bottomToBottom = LayoutParams.PARENT_ID;
        eventLayoutParams.leftToLeft = LayoutParams.PARENT_ID;
        eventLayoutParams.rightToRight = LayoutParams.PARENT_ID;
        addView(eventView, eventLayoutParams);
        controlView.setAlpha(1.0f);
        setKeepScreenOn(true);
        post(() -> {
            eventView.showLoadIndicator(true);
        });
    }

    private void updateBackdrop(String backdrop) {
        contentView.setVisibility(INVISIBLE);
        GlideApp.with(this)
                .load(backdrop)
                .into(controlView.poster);
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
        eventView.showSelectView(videos, currentSubtitleId);
    }

    @Override
    public void onSelectAudio() {
        var player = contentView.viewModel;
        var currentAudioId = player.currentAudioId();
        var audios = (List<TrackItem>)player.audios();
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
