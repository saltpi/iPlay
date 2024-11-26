package top.ourfor.app.iplayx.page.player;

import static top.ourfor.app.iplayx.model.EmbyPlaybackData.kIPLXSecond2TickScale;
import static top.ourfor.app.iplayx.module.Bean.XGET;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.ContentInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fasterxml.jackson.core.type.TypeReference;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import top.ourfor.app.iplayx.R;
import top.ourfor.app.iplayx.action.DispatchAction;
import top.ourfor.app.iplayx.action.NavigationTitleBar;
import top.ourfor.app.iplayx.api.dandan.DanDanPlayApi;
import top.ourfor.app.iplayx.api.dandan.DanDanPlayModel;
import top.ourfor.app.iplayx.bean.JSONAdapter;
import top.ourfor.app.iplayx.common.model.SeekableRange;
import top.ourfor.app.iplayx.common.model.WebMediaMessage;
import top.ourfor.app.iplayx.common.type.MediaLayoutType;
import top.ourfor.app.iplayx.common.type.MediaPlayState;
import top.ourfor.app.iplayx.config.AppSetting;
import top.ourfor.app.iplayx.model.EmbyMediaModel;
import top.ourfor.app.iplayx.page.Page;
import top.ourfor.app.iplayx.page.home.MediaViewCell;
import top.ourfor.app.iplayx.util.DeviceUtil;
import top.ourfor.app.iplayx.util.IntervalCaller;
import top.ourfor.app.iplayx.util.WindowUtil;
import top.ourfor.app.iplayx.model.EmbyPlaybackData;
import top.ourfor.app.iplayx.model.EmbyPlayingQueue;
import top.ourfor.app.iplayx.store.GlobalStore;
import top.ourfor.app.iplayx.view.ListView;
import top.ourfor.app.iplayx.view.player.PlayerEventType;
import top.ourfor.app.iplayx.view.video.PlayerSourceModel;
import top.ourfor.app.iplayx.view.video.PlayerView;

@Slf4j
public class MoviePlayerPage implements Page {
    private ConstraintLayout contentView = null;
    private PlayerView playerView = null;
    private String id = null;
    private String url = null;
    private String title = null;
    private EmbyPlaybackData playbackData = null;
    private IntervalCaller caller;

    @Getter
    Context context;

    HashMap<String, Object> params;


    public void onCreate(@Nullable Bundle savedInstanceState) {
        XGET(ActionBar.class).hide();
        val args = params;
        id = (String) args.getOrDefault("id", null);
        url = (String) args.getOrDefault("url", null);
        title = (String) args.getOrDefault("title", null);
        setupUI(getContext());
        bind();
        WindowUtil.enterFullscreen();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return contentView;
    }

    @SneakyThrows
    void setupUI(Context context) {
        contentView = new ConstraintLayout(context);
        contentView.setBackgroundColor(Color.BLACK);
        playerView = new PlayerView(context);
        playerView.useCloseButton(true);
        val playerLayout = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        playerLayout.topToTop = LayoutParams.PARENT_ID;
        playerLayout.bottomToBottom = LayoutParams.PARENT_ID;
        playerLayout.leftToLeft = LayoutParams.PARENT_ID;
        playerLayout.rightToRight = LayoutParams.PARENT_ID;
        contentView.addView(playerView, playerLayout);
    }

    void bind() {
        if (id != null) {
            setupWithId(id);
        } else if (url != null) {
            setupWithUrl(url);
        }
    }

    void setupWithUrl(String url) {
        playerView.post(() -> {
            playerView.setOption(AppSetting.shared.getPlayerConfig());
            if (url.startsWith("iplay://")) {
                var urlObj = Uri.parse(url);
                var source = urlObj.getQueryParameter("source");
                var option = urlObj.getQueryParameter("option");
                val json = XGET(JSONAdapter.class);
                var sourceDict = json.fromJSON(source, new TypeReference<WebMediaMessage>() { });
                var optionDict = json.fromJSON(option, new TypeReference<Map<String, String>>() { });
                var audioFile = sourceDict.getAudio();
                playerView.setOption(optionDict);
                playerView.setUrl(sourceDict.getVideo(), audioFile);
            } else {
                playerView.setUrl(url);
            }
            if (title != null) {
                playerView.setTitle(title);
            }
        });
    }

    void setupWithId(String id) {
        val store = XGET(GlobalStore.class);
        val media = store.getDataSource().getMediaMap().get(id);
        if (media == null) return;
        val name = media.getSeriesName() != null ? media.getSeriesName() : media.getName();
        DanDanPlayApi.search(name, result -> {
            val animes = result.getAnimes();
            if (animes == null || animes.isEmpty()) {
                playerView.getControlView().post(() -> playerView.getControlView().commentButton.setVisibility(View.GONE));
                return;
            }
            val collect = animes.stream().filter(anime -> anime.getType().equals("tvseries")).collect(Collectors.toList());
            if (collect.isEmpty()) {
                playerView.getControlView().post(() -> playerView.getControlView().commentButton.setVisibility(View.GONE));
                return;
            }
            val episodes = collect.get(0);
            val idx = media.getIndexNumber() - 1;
            if (idx < 0 || idx >= episodes.getEpisodes().size()) return;
            val episode = episodes.getEpisodes().get(idx);
            DanDanPlayApi.comments(episode.getEpisodeId(), comments -> {
                playerView.getCommentView().setComments(comments.getComments().stream().map(DanDanPlayModel.Comment::getAttributes).collect(Collectors.toList()));
                if (comments.getComments().isEmpty()) {
                    playerView.getControlView().post(() -> playerView.getControlView().commentButton.setVisibility(View.GONE));
                }
            });
        });
        store.getPlayback(media.getId(), playback -> {
            if (playback == null) return;
            val sources = store.getPlaySources(media, playback);
            val video = sources.stream().filter(v -> v.getType() == PlayerSourceModel.PlayerSourceType.Video).findFirst().get();
            playbackData = EmbyPlaybackData.builder()
                    .playSessionId(playback.getSessionId())
                    .isMuted(false)
                    .isPaused(false)
                    .itemId(video.getId())
                    .eventName("")
                    .positionTicks(0L)
                    .seekableRanges(List.of(new SeekableRange(0L, 0L)))
                    .nowPlayingQueue(List.of(new EmbyPlayingQueue("", "playlistItem0")))
                    .build();
            XGET(GlobalStore.class).trackPlay(MediaPlayState.OPENING, playbackData);
            val url = store.getPlayUrl(playback);
            log.debug("video url: {}", url);
            if (url == null) return;
            this.playerView.post(() -> {
                long lastWatchPosition = media.getUserData() != null ? media.getUserData().getPlaybackPositionTicks() / kIPLXSecond2TickScale : 0;
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
                XGET(GlobalStore.class).trackPlay(state, playbackData);
            }
            caller.invoke(() -> {
                XGET(GlobalStore.class).trackPlay(state, playbackData);
            });
        });

        playerView.setOnPlaylistTap(playerView -> {
            val context = getContext();
            val listView = new ListView<EmbyMediaModel>(context);
            listView.viewModel.viewCell = MediaViewCell.class;
            listView.viewModel.isSelected = (model) -> model.getId().equals(this.id);
            listView.listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            listView.listView.setPadding(DeviceUtil.dpToPx(3), DeviceUtil.dpToPx(3), DeviceUtil.dpToPx(3), DeviceUtil.dpToPx(3));
            List<EmbyMediaModel> items = null;
            if (media.isEpisode()) {
                items = store.getDataSource().getSeasonEpisodes().get(media.getSeasonId());
            } else {
                items = store.getDataSource().getSeasonEpisodes().get(media.getId());
            }
            if (items == null || items.isEmpty()) {
                if (media.isEpisode()) {
                    store.getEpisodes(media.getSeriesId(), media.getSeasonId(), episodes -> {
                        episodes.forEach(episode -> episode.setLayoutType(MediaLayoutType.EpisodeDetail));
                        listView.setItems(episodes);
                    });
                } else {
                    store.getSimilar(media.getId(), models -> {
                        models.forEach(episode -> episode.setLayoutType(MediaLayoutType.EpisodeDetail));
                        listView.setItems(models);
                    });
                }

            } else {
                items.forEach(episode -> episode.setLayoutType(MediaLayoutType.EpisodeDetail));
                listView.setItems(items);
            }
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
            if (items != null) {
                val selectedIdx = items.indexOf(media);
                if (selectedIdx >= 0) {
                    listView.postDelayed(() -> {
                        listView.listView.smoothScrollToPosition(selectedIdx);
                    }, 200);
                }
            }
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

    void onSelectMedia(EmbyMediaModel media) {
        if (media == null) return;
        id = media.getId();
        val store = XGET(GlobalStore.class);
        store.getPlayback(media.getId(), playback -> {
            if (playback == null) return;
            val sources = store.getPlaySources(media, playback);
            val video = sources.stream().filter(v -> v.getType() == PlayerSourceModel.PlayerSourceType.Video).findFirst().get();
            playbackData = EmbyPlaybackData.builder()
                    .playSessionId(playback.getSessionId())
                    .isMuted(false)
                    .isPaused(false)
                    .itemId(video.getId())
                    .eventName("")
                    .positionTicks(0L)
                    .seekableRanges(List.of(new SeekableRange(0L, 0L)))
                    .nowPlayingQueue(List.of(new EmbyPlayingQueue("", "playlistItem0")))
                    .build();
            XGET(GlobalStore.class).trackPlay(MediaPlayState.OPENING, playbackData);
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
        WindowUtil.exitFullscreen();
        val action = XGET(NavigationTitleBar.ThemeManageAction.class);
        action.setStatusBarTextColor(!action.isDarkMode());
        XGET(GlobalStore.class).trackPlay(MediaPlayState.STOPPED, playbackData);
    }

    @Override
    public void viewDidDisappear() {
        playerView.onHostDestroy();
    }

    @Override
    public void create(Context context, Map<String, Object> params) {
        this.context = context;
        this.params = (HashMap<String, Object>)params;
        onCreate(null);
        onCreateView(LayoutInflater.from(context), null, null);
    }

    @Override
    public View view() {
        return contentView;
    }
}

