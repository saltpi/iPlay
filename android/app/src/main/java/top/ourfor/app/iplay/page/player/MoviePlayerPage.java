package top.ourfor.app.iplay.page.player;

import static top.ourfor.app.iplay.api.emby.EmbyModel.EmbyPlaybackData.kIPLXSecond2TickScale;
import static top.ourfor.app.iplay.module.Bean.XGET;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import top.ourfor.app.iplay.R;
import top.ourfor.app.iplay.action.DispatchAction;
import top.ourfor.app.iplay.action.NavigationTitleBar;
import top.ourfor.app.iplay.api.dandan.DanDanPlayApi;
import top.ourfor.app.iplay.api.dandan.DanDanPlayModel;
import top.ourfor.app.iplay.api.emby.EmbyModel;
import top.ourfor.app.iplay.bean.IJSONAdapter;
import top.ourfor.app.iplay.common.annotation.ViewController;
import top.ourfor.app.iplay.common.model.SeekableRange;
import top.ourfor.app.iplay.common.model.WebMediaMessage;
import top.ourfor.app.iplay.common.type.MediaLayoutType;
import top.ourfor.app.iplay.common.type.MediaPlayState;
import top.ourfor.app.iplay.config.AppSetting;
import top.ourfor.app.iplay.model.MediaModel;
import top.ourfor.app.iplay.page.Page;
import top.ourfor.app.iplay.page.home.MediaViewCell;
import top.ourfor.app.iplay.page.media.PlayerConfigPanelViewModel;
import top.ourfor.app.iplay.util.DeviceUtil;
import top.ourfor.app.iplay.util.IntervalCaller;
import top.ourfor.app.iplay.util.WindowUtil;
import top.ourfor.app.iplay.store.IAppStore;
import top.ourfor.app.iplay.view.ListView;
import top.ourfor.app.iplay.view.player.PlayerEventType;
import top.ourfor.app.iplay.view.video.PlayerSourceModel;
import top.ourfor.app.iplay.view.video.PlayerView;

@Slf4j
@ViewController(name = "movie_player_page")
public class MoviePlayerPage implements Page {
    private ConstraintLayout contentView = null;
    private PlayerView playerView = null;
    private String id = null;
    private String url = null;
    private String title = null;
    private PlayerConfigPanelViewModel.MediaSourceModel source = null;
    private EmbyModel.EmbyPlaybackData playbackData = null;
    private IntervalCaller caller;

    @Getter
    Context context;

    Map<String, Object> params;

    Queue<MediaModel> playlist;

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
        if (source != null) {
            setupWithSource(source);
        } else if (id != null) {
            setupWithId(id);
        } else if (url != null) {
            setupWithUrl(url);
        }
    }

    private void setupWithSource(PlayerConfigPanelViewModel.MediaSourceModel source) {
        playEmbyMediaWithId(source.getMedia().getId());
    }

    void setupWithUrl(String url) {
        playerView.post(() -> {
            playerView.setOption(AppSetting.shared.getPlayerConfig());
            if (url.startsWith("iplay://")) {
                var urlObj = Uri.parse(url);
                var source = urlObj.getQueryParameter("source");
                var option = urlObj.getQueryParameter("option");
                val json = XGET(IJSONAdapter.class);
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
        playEmbyMediaWithId(id);
    }

    void playEmbyMediaWithId(String id) {
        val store = XGET(IAppStore.class);
        assert store != null;
        val media = store.getDataSource().getMediaMap().get(id);
        if (media == null) return;
        val name = media.getSeriesName() != null ? media.getSeriesName() : media.getName();
        DanDanPlayApi.search(name, result -> {
            if (result == null) return;
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
            val video = source != null && source.getVideo() != null ? source.getVideo() : sources.stream().filter(v -> v.getType() == PlayerSourceModel.PlayerSourceType.Video).findFirst().get();
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
            val url = source != null ? video.getUrl() : store.getPlayUrl(playback);
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
                XGET(IAppStore.class).trackPlay(state, playbackData);
            }
            caller.invoke(() -> {
                XGET(IAppStore.class).trackPlay(state, playbackData);
            });
        });

        playerView.setOnPlayEnd(() -> {
            if (playlist != null && !playlist.isEmpty()) {
                val next = playlist.poll();
                if (next != null) {
                    log.info("play next: {}", next);
                    playerView.showLoading();
                    onSelectMedia(next);
                }
            }
        });

        playerView.setOnPlaylistTap(playerView -> {
            val context = getContext();
            val listView = new ListView<MediaModel>(context);
            listView.viewModel.viewCell = MediaViewCell.class;
            listView.viewModel.isSelected = (model) -> model.getId().equals(this.id);
            listView.listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            listView.listView.setPadding(DeviceUtil.dpToPx(3), DeviceUtil.dpToPx(3), DeviceUtil.dpToPx(3), DeviceUtil.dpToPx(3));
            List<MediaModel> items = null;
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
                val model = e.getModel();
                onSelectMedia(model);
                if (model.isEpisode()) {
                    setupPlaylist(model);
                }
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

        if (AppSetting.shared.isAutoPlayNextEpisode()) {
            setupPlaylist(media);
        }
    }

    private void setupPlaylist(MediaModel media) {
        var store = XGET(IAppStore.class);
        assert store != null;
        List<MediaModel> items = null;
        if (media.isEpisode()) {
            items = store.getDataSource().getSeasonEpisodes().get(media.getSeasonId());
        } else {
            return;
        }
        if (items == null || items.isEmpty()) {
            store.getEpisodes(media.getSeriesId(), media.getSeasonId(), episodes -> {
                // add all items next to current item
                val idx = episodes.indexOf(media);
                if (idx >= 0) {
                    playlist = episodes.stream().skip(idx + 1).collect(Collectors.toCollection(LinkedList::new));
                    log.info("playlist: {}", playlist);
                }
            });
        } else {
            // add all items next to current item
            val idx = items.indexOf(media);
            if (idx >= 0) {
                playlist = items.stream().skip(idx + 1).collect(Collectors.toCollection(LinkedList::new));
                log.info("playlist: {}", playlist);
            }
        }
    }

    void onSelectMedia(MediaModel media) {
        if (media == null) return;
        id = media.getId();
        val store = XGET(IAppStore.class);
        assert store != null;
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
                playerView.resume();
            });
        });
    }

    @Override
    public void viewWillDisappear() {
        WindowUtil.exitFullscreen();
        val action = XGET(NavigationTitleBar.ThemeManageAction.class);
        action.setStatusBarTextColor(!action.isDarkMode());
        XGET(IAppStore.class).trackPlay(MediaPlayState.STOPPED, playbackData);
    }

    @Override
    public void viewDidDisappear() {
        playerView.onHostDestroy();
    }

    @Override
    public void create(Context context, Map<String, Object> params) {
        this.context = context;
        this.params = (Map<String, Object>)params;
        XGET(ActionBar.class).hide();
        val args = params;
        id = (String) args.getOrDefault("id", null);
        url = (String) args.getOrDefault("url", null);
        title = (String) args.getOrDefault("title", null);
        source = (PlayerConfigPanelViewModel.MediaSourceModel) args.getOrDefault("source", null);
        setupUI(getContext());
        bind();
        WindowUtil.enterFullscreen();
    }

    @Override
    public View view() {
        return contentView;
    }

    @Override
    public int id() {
        return R.id.playerPage;
    }
}

