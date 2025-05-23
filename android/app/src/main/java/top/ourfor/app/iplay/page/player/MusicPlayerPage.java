package top.ourfor.app.iplay.page.player;

import static top.ourfor.app.iplay.api.emby.EmbyModel.EmbyPlaybackData.kIPLXSecond2TickScale;
import static top.ourfor.app.iplay.module.Bean.XGET;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;
import top.ourfor.app.iplay.R;
import top.ourfor.app.iplay.action.DispatchAction;
import top.ourfor.app.iplay.api.emby.EmbyModel;
import top.ourfor.app.iplay.common.annotation.ViewController;
import top.ourfor.app.iplay.common.model.SeekableRange;
import top.ourfor.app.iplay.common.type.MediaLayoutType;
import top.ourfor.app.iplay.common.type.MediaPlayState;
import top.ourfor.app.iplay.config.AppSetting;
import top.ourfor.app.iplay.model.MediaModel;
import top.ourfor.app.iplay.page.Page;
import top.ourfor.app.iplay.page.home.MediaViewCell;
import top.ourfor.app.iplay.store.IAppStore;
import top.ourfor.app.iplay.util.DeviceUtil;
import top.ourfor.app.iplay.util.IntervalCaller;
import top.ourfor.app.iplay.util.LayoutUtil;
import top.ourfor.app.iplay.util.WindowUtil;
import top.ourfor.app.iplay.view.ListView;
import top.ourfor.app.iplay.view.audio.MusicPlayerView;
import top.ourfor.app.iplay.view.player.PlayerEventType;
import top.ourfor.app.iplay.view.video.PlayerSourceModel;

@ViewController(name = "music_player_page")
public class MusicPlayerPage implements Page {
    private ConstraintLayout contentView = null;
    private MusicPlayerView playerView = null;
    private String id = null;
    private EmbyModel.EmbyPlaybackData playbackData = null;
    private IntervalCaller caller;

    @Getter
    Context context;
    HashMap<String, Object> params;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        XGET(ActionBar.class).hide();
        val args = params;
        id = (String)args.get("id");
        setupUI(context);
        bind();
    }


    @SneakyThrows
    void setupUI(Context context) {
        contentView = new ConstraintLayout(context);
        contentView.setBackgroundResource(R.drawable.bg);
        playerView = new MusicPlayerView(context);
        contentView.addView(playerView, LayoutUtil.fill());
    }

    void bind() {
        val store = XGET(IAppStore.class);
        val media = store.getDataSource().getMediaMap().get(id);
        if (media == null) return;
        store.getPlayback(media.getId(), playback -> {
            if (playback == null) return;
            val sources = store.getPlaySources(media, playback);
            val video = sources.stream().filter(v -> v.getType() == PlayerSourceModel.PlayerSourceType.Video).findFirst().get();
            playbackData = EmbyModel.EmbyPlaybackData.builder()
                    .playSessionId(playback.getSessionId())
                    .isMuted(false)
                    .isPaused(false)
                    .itemId(video.getId())
                    .eventName("")
                    .positionTicks(0L)
                    .seekableRanges(List.of(new SeekableRange(0L, 0L)))
                    .nowPlayingQueue(List.of(new EmbyModel.EmbyPlayingQueue("", "playlistItem0")))
                    .build();
            XGET(IAppStore.class).trackPlay(MediaPlayState.OPENING, playbackData);
            val url = store.getPlayUrl(playback);
            if (url == null) return;
            this.playerView.post(() -> {
                long lastWatchPosition = media.getUserData().getPlaybackPositionTicks() / kIPLXSecond2TickScale;
                playerView.setLastWatchPosition(lastWatchPosition);
                playerView.setOption(AppSetting.shared.getPlayerConfig());
                playerView.setSources(sources);
                playerView.setUrl(url);
            });
        });

        caller = new IntervalCaller(TimeUnit.SECONDS.toMillis(10), 0);

        playerView.setOnPlayStateChange(event -> {
            if (playbackData == null) return;
            val type = PlayerEventType.PlayEventType.fromInt((int)event.get("type"));
            val eventName = switch(type) {
                case PlayEventTypeOnPause -> "Pause";
                case PlayEventTypeOnProgress -> "TimeUpdate";
                case PlayEventTypeEnd -> "Stopped";
                default -> "";
            };
            val isResume = type == PlayerEventType.PlayEventType.PlayEventTypeOnProgress && playbackData.getIsPaused();
            playbackData
                    .setIsPaused(type == PlayerEventType.PlayEventType.PlayEventTypeOnPause)
                    .setEventName(eventName);
            val position = (Double)event.get("position");
            if (position != null) {
                playbackData = playbackData.setPositionTicks(Long.valueOf(position.longValue() * kIPLXSecond2TickScale));
            }
            val state = switch(type) {
                case PlayEventTypeOnProgress -> MediaPlayState.PLAYING;
                case PlayEventTypeOnPause -> MediaPlayState.PAUSED;
                case PlayEventTypeEnd -> MediaPlayState.STOPPED;
                default -> MediaPlayState.NONE;
            };
            if (type == PlayerEventType.PlayEventType.PlayEventTypeOnPause || isResume) {
                XGET(IAppStore.class).trackPlay(state, playbackData);
            }
            caller.invoke(() -> {
                XGET(IAppStore.class).trackPlay(state, playbackData);
            });
        });

        playerView.setOnPlaylistTap(playerView -> {
            val site = store.getSite();
            val context = getContext();
            val listView = new ListView<MediaModel>(context);
            listView.viewModel.viewCell = MediaViewCell.class;
            listView.viewModel.isSelected = (model) -> model.equals(media);
            listView.listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            if (!media.isEpisode()) return;
            val items = store.getDataSource().getSeasonEpisodes().get(media.getSeasonId());
            if (items == null || items.isEmpty()) {
                store.getEpisodes(media.getSeriesId(), media.getSeasonId(), episodes -> {
                    episodes.forEach(episode -> episode.setLayoutType(MediaLayoutType.EpisodeDetail));
                    listView.setItems(episodes);
                });
            }
            listView.setItems(items);
            val builder = new AlertDialog.Builder(context);
            builder.setOnDismissListener(dlg -> {
                ViewGroup parent = (ViewGroup) listView.getParent();
                if (parent != null) {
                    parent.removeView(listView);
                }
            });
            val parent = (ViewGroup)listView.getParent();
            if (parent != null) {
                parent.removeView(listView);
            }
            builder.setView(listView);
            val dialog = builder.show();
            listView.viewModel.onClick = e -> {
                onSelectMedia(e.getModel());
                XGET(DispatchAction.class).runOnUiThread(() -> {
                    if (dialog != null) {
                        dialog.cancel();
                    }
                });
            };
            Window window = dialog.getWindow();
            if (window != null) {
                listView.setPadding(0, 0, 0, DeviceUtil.dpToPx(20));
                window.setBackgroundDrawableResource(R.drawable.dialog_bg);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    window.setBackgroundBlurRadius(8);
                }
                WindowUtil.setFullscreen(window);
                WindowManager.LayoutParams params = window.getAttributes();
                params.gravity = Gravity.BOTTOM;
                params.width = WindowManager.LayoutParams.MATCH_PARENT;
                params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                window.setAttributes(params);
                window.setWindowAnimations(R.style.DialogAnimation);
            }
        });
    }

    void onSelectMedia(MediaModel media) {
        if (media == null) return;
        val store = XGET(IAppStore.class);
        store.getPlayback(media.getId(), playback -> {
            if (playback == null) return;
            val sources = store.getPlaySources(media, playback);
            val video = sources.stream().filter(v -> v.getType() == PlayerSourceModel.PlayerSourceType.Video).findFirst().get();
            playbackData = EmbyModel.EmbyPlaybackData.builder()
                    .playSessionId(playback.getSessionId())
                    .isMuted(false)
                    .isPaused(false)
                    .itemId(video.getId())
                    .eventName("")
                    .positionTicks(0L)
                    .seekableRanges(List.of(new SeekableRange(0L, 0L)))
                    .nowPlayingQueue(List.of(new EmbyModel.EmbyPlayingQueue("", "playlistItem0")))
                    .build();
            XGET(IAppStore.class).trackPlay(MediaPlayState.OPENING, playbackData);
            val url = store.getPlayUrl(playback);
            if (url == null) return;
            this.playerView.post(() -> {
                long lastWatchPosition = media.getUserData().getPlaybackPositionTicks() / kIPLXSecond2TickScale;
                playerView.setLastWatchPosition(lastWatchPosition);
                playerView.setOption(AppSetting.shared.getPlayerConfig());
                playerView.setSources(sources);
                playerView.setUrl(url);
            });
        });
    }

    @Override
    public void viewWillDisappear() {
        XGET(IAppStore.class).trackPlay(MediaPlayState.STOPPED, playbackData);
    }

    @Override
    public void destroy() {
        playerView.onHostDestroy();
    }

    @Override
    public View view() {
        return contentView;
    }

    @Override
    public void create(Context context, Map<String, Object> params) {
        this.context = context;
        this.params = new HashMap<>(params);
        onCreate(null);
    }
}

