package top.ourfor.app.iplay.store;

import static top.ourfor.app.iplay.module.Bean.XGET;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import top.ourfor.app.iplay.api.emby.EmbyModel;
import top.ourfor.app.iplay.bean.IJSONAdapter;
import top.ourfor.app.iplay.common.api.IDataSourceApi;
import top.ourfor.app.iplay.common.type.MediaPlayState;
import top.ourfor.app.iplay.common.type.MediaType;
import top.ourfor.app.iplay.model.AlbumModel;
import top.ourfor.app.iplay.model.MediaModel;
import top.ourfor.app.iplay.model.SiteModel;
import top.ourfor.app.iplay.model.drive.Drive;
import top.ourfor.app.iplay.view.video.PlayerSourceModel;

public interface IAppStore {
    IDataSourceApi getApi();
    SiteModel getSite();
    List<SiteModel> getSites();
    Drive getDrive();
    List<Drive> getDrives();
    DataSource getDataSource();

    default void addNewSite(SiteModel site) {
    }

    default void save() {
    }

    default String toJSON() {
        return XGET(IJSONAdapter.class).toJSON(this);
    }

    default String toSiteJSON() {
        return "";
    }

    default String toSiteJSON(boolean filterSync) {
        return "";
    }

    default void fromJSON(String json) {
    }

    default void fromSiteJSON(String json) {
    }

    default void getAlbums(Consumer<List<AlbumModel>> completion) {
    }

    default void markFavorite(String id, boolean isFavorite, Consumer<EmbyModel.EmbyUserData> completion) {
    }

    default void getResume(Consumer<List<MediaModel>> completion) {

    }

    default void getAlbumLatestMedias(String id, Consumer<List<MediaModel>> completion) {

    }

    default void getAllFavoriteMedias(MediaType type, Consumer<List<MediaModel>> completion) {
    }

    default void getFavoriteMedias(MediaType type, Consumer<List<MediaModel>> completion) {
    }

    default void getSeasons(String seriesId, Consumer<List<MediaModel>> completion) {
    }

    default void getEpisodes(String seriesId, String seasonId, Consumer<List<MediaModel>> completion) {
    }

    default void getPlayback(String id, Consumer<EmbyModel.EmbyPlaybackModel> completion) {
    }

    default List<PlayerSourceModel> getPlaySources(MediaModel media, EmbyModel.EmbyPlaybackModel playback) {
        return List.of();
    }

    default String getPlayUrl(EmbyModel.EmbyPlaybackModel playback) {
        return "";
    }

    default void getAlbumAllMedias(String id, Consumer<List<MediaModel>> completion) {
    }

    default void getAlbumMedias(String id, Consumer<List<MediaModel>> completion) {
    }

    default void getAlbumMedias(String id, int start, Consumer<List<MediaModel>> completion) {
    }

    default void getItems(Map<String, String> query, Consumer<List<MediaModel>> completion) {
    }


    default void trackPlay(MediaPlayState state, EmbyModel.EmbyPlaybackData data) {
    }

    default void addDrive(Drive drive) {
    }

    default void switchDrive(Drive drive) {
    }

    default void switchSite(SiteModel site) {
    }

    default void search(String keyword, Consumer<List<MediaModel>> completion) {
    }

    default void removeSite(SiteModel model) {
    }

    default void removeDrive(Drive model) {
    }

    default boolean hasValidSite() {
        return false;
    }

    default boolean hasValidDrive() {
        return false;
    }

    default void searchSuggestion(Consumer<List<MediaModel>> prompts) {

    }

    default String getSiteName() {
        return "";
    }

    default void getSimilar(String id, Consumer<List<MediaModel>> completion) {
    }

    default void getDetail(String id, Consumer<MediaModel> completion) {
    }
}
