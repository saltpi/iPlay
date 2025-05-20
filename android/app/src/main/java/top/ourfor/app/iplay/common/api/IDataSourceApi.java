package top.ourfor.app.iplay.common.api;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import top.ourfor.app.iplay.api.emby.EmbyModel;
import top.ourfor.app.iplay.common.type.MediaPlayState;
import top.ourfor.app.iplay.model.AlbumModel;
import top.ourfor.app.iplay.model.MediaModel;
import top.ourfor.app.iplay.model.SiteModel;

public interface IDataSourceApi {

    default void setSiteModel(SiteModel site) { }

    static void login(String server, String username, String password, Consumer<Object> completion) { }

    default void getSiteInfo(Consumer<Object> completion) { }

    default void getAlbums(Consumer<List<AlbumModel>> completion) { }

    default void getAlbumLatestMedias(String id, Consumer<List<MediaModel>> completion) { }

    default void getMediasCount(Map<String, String> query, Consumer<Integer> completion) { }

    default void getMedias(Map<String, String> query, Consumer<List<MediaModel>> completion) { }

    default void getAllMedias(Map<String, String> query, Consumer<Object> completion) { }

    default void getSeasons(String seriesId, Consumer<Object> completion) { }

    default void getEpisodes(String seriesId, String seasonId, Consumer<Object> completion) { }

    default void getPlayback(String id, Consumer<Object> completion) { }

    default void getResume(Consumer<Object> completion) { }

    default void getSimilar(String id, Consumer<Object> completion) { }

    default void getDetail(String id, Consumer<MediaModel> completion) { }

    default void markFavorite(String id, boolean isFavorite, Consumer<Object> completion) { }

    default void trackPlay(MediaPlayState state, EmbyModel.EmbyPlaybackData data, Consumer<Object> completion) { }

    default void getRecommendations(Consumer<Object> completion) { }

    default int preferedPageSize() { return 100; }
}
