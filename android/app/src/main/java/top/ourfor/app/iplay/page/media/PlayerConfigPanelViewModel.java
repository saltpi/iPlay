package top.ourfor.app.iplay.page.media;

import static top.ourfor.app.iplay.module.Bean.XGET;

import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.With;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import top.ourfor.app.iplay.model.MediaModel;
import top.ourfor.app.iplay.store.IAppStore;
import top.ourfor.app.iplay.view.video.PlayerSourceModel;

@Slf4j
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerConfigPanelViewModel {
    private final MutableLiveData<MediaModel> media = new MutableLiveData<>(null);
    private final MutableLiveData<MediaSourceConfigModel> mediaSource = new MutableLiveData<>(null);

    String value;

    Consumer<String> onClick;

    public void fetchMediaSource(Consumer<MediaSourceConfigModel> callback) {
        if (this.media.getValue() == null) return;

        val media = this.media.getValue();
        val store = XGET(IAppStore.class);
        assert store != null;
        store.getPlayback(media.getId(), playback -> {
            if (playback == null) return;
            val sources = store.getPlaySources(media, playback);
            val videos = sources.stream().filter(v -> v.getType() == PlayerSourceModel.PlayerSourceType.Video).collect(Collectors.toList());
            val audios = sources.stream().filter(v -> v.getType() == PlayerSourceModel.PlayerSourceType.Audio).collect(Collectors.toList());
            val subtitles = sources.stream().filter(v -> v.getType() == PlayerSourceModel.PlayerSourceType.Subtitle).collect(Collectors.toList());

            val model = MediaSourceConfigModel.builder()
                    .videos(videos)
                    .audios(audios)
                    .subtitles(subtitles)
                    .build();
            mediaSource.postValue(model);
            log.info("media source model: {}", model);
            if (callback != null) callback.accept(model);
        });
    }

    @Data
    @With
    @Builder
    public static class MediaSourceConfigModel {
        List<PlayerSourceModel> videos;
        List<PlayerSourceModel> audios;
        List<PlayerSourceModel> subtitles;
    }

    @Data
    @With
    @Builder
    public static class MediaSourceModel {
        MediaModel media;

        PlayerSourceModel video;
        PlayerSourceModel audio;
        PlayerSourceModel subtitle;
    }

}
