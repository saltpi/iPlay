package top.ourfor.app.iplayx.common.api;

import java.util.Map;
import java.util.function.Consumer;

import top.ourfor.app.iplayx.common.type.MediaPlayState;
import top.ourfor.app.iplayx.model.EmbyPlaybackData;
import top.ourfor.app.iplayx.model.SiteModel;

public interface EmbyLikeApi {

    default void setSiteModel(SiteModel site) { }

    static void login(String server, String username, String password, Consumer<Object> completion) { }

    default void getAlbums(Consumer<Object> completion) { }

    default void getAlbumLatestMedias(String id, Consumer<Object> completion) { }

    default void getMediasCount(Map<String, String> query, Consumer<Integer> completion) { }

    default void getMedias(Map<String, String> query, Consumer<Object> completion) { }

    default void getAllMedias(Map<String, String> query, Consumer<Object> completion) { }

    default void getSeasons(String seriesId, Consumer<Object> completion) { }

    default void getEpisodes(String seriesId, String seasonId, Consumer<Object> completion) { }

    default void getPlayback(String id, Consumer<Object> completion) { }

    default void getResume(Consumer<Object> completion) { }

    default void getSimilar(String id, Consumer<Object> completion) { }

    default void markFavorite(String id, boolean isFavorite, Consumer<Object> completion) { }

    default void trackPlay(MediaPlayState state, EmbyPlaybackData data, Consumer<Object> completion) { }

    default void getRecommendations(Consumer<Object> completion) { }
}
