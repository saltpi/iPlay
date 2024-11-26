package top.ourfor.app.iplayx.api.emby;

import static top.ourfor.app.iplayx.module.Bean.XGET;

import com.fasterxml.jackson.core.type.TypeReference;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.With;
import lombok.val;
import top.ourfor.app.iplayx.bean.JSONAdapter;
import top.ourfor.app.iplayx.common.api.EmbyLikeApi;
import top.ourfor.app.iplayx.common.type.MediaPlayState;
import top.ourfor.app.iplayx.store.GlobalStore;
import top.ourfor.app.iplayx.util.HTTPUtil;
import top.ourfor.app.iplayx.util.HTTPModel;
import top.ourfor.app.iplayx.model.EmbyAlbumModel;
import top.ourfor.app.iplayx.model.EmbyMediaModel;
import top.ourfor.app.iplayx.model.EmbyPageableModel;
import top.ourfor.app.iplayx.model.EmbyPlaybackData;
import top.ourfor.app.iplayx.model.EmbyPlaybackModel;
import top.ourfor.app.iplayx.model.EmbyUserData;
import top.ourfor.app.iplayx.model.EmbyUserModel;
import top.ourfor.app.iplayx.common.model.SiteEndpointModel;
import top.ourfor.app.iplayx.model.SiteModel;

@Builder
@With
@EqualsAndHashCode
public class EmbyApi implements EmbyLikeApi {
    private static final String kDeviceProfile = "{\"DeviceProfile\":{\"MaxStaticBitrate\":140000000,\"MaxStreamingBitrate\":140000000,\"MusicStreamingTranscodingBitrate\":192000,\"DirectPlayProfiles\":[{\"Container\":\"mp4,m4v\",\"Type\":\"Video\",\"VideoCodec\":\"h264,h265,hevc,vp8,vp9\",\"AudioCodec\":\"mp3,aac,opus,flac,vorbis\"},{\"Container\":\"mkv\",\"Type\":\"Video\",\"VideoCodec\":\"h264,h265,hevc,vp8,vp9\",\"AudioCodec\":\"mp3,aac,opus,flac,vorbis\"},{\"Container\":\"flv\",\"Type\":\"Video\",\"VideoCodec\":\"h264\",\"AudioCodec\":\"aac,mp3\"},{\"Container\":\"mov\",\"Type\":\"Video\",\"VideoCodec\":\"h264\",\"AudioCodec\":\"mp3,aac,opus,flac,vorbis\"},{\"Container\":\"opus\",\"Type\":\"Audio\"},{\"Container\":\"mp3\",\"Type\":\"Audio\",\"AudioCodec\":\"mp3\"},{\"Container\":\"mp2,mp3\",\"Type\":\"Audio\",\"AudioCodec\":\"mp2\"},{\"Container\":\"aac\",\"Type\":\"Audio\",\"AudioCodec\":\"aac\"},{\"Container\":\"m4a\",\"AudioCodec\":\"aac\",\"Type\":\"Audio\"},{\"Container\":\"mp4\",\"AudioCodec\":\"aac\",\"Type\":\"Audio\"},{\"Container\":\"flac\",\"Type\":\"Audio\"},{\"Container\":\"webma,webm\",\"Type\":\"Audio\"},{\"Container\":\"wav\",\"Type\":\"Audio\",\"AudioCodec\":\"PCM_S16LE,PCM_S24LE\"},{\"Container\":\"ogg\",\"Type\":\"Audio\"},{\"Container\":\"webm\",\"Type\":\"Video\",\"AudioCodec\":\"vorbis,opus\",\"VideoCodec\":\"VP8,VP9\"}],\"TranscodingProfiles\":[{\"Container\":\"aac\",\"Type\":\"Audio\",\"AudioCodec\":\"aac\",\"Context\":\"Streaming\",\"Protocol\":\"hls\",\"MaxAudioChannels\":\"2\",\"MinSegments\":\"2\",\"BreakOnNonKeyFrames\":true},{\"Container\":\"aac\",\"Type\":\"Audio\",\"AudioCodec\":\"aac\",\"Context\":\"Streaming\",\"Protocol\":\"http\",\"MaxAudioChannels\":\"2\"},{\"Container\":\"mp3\",\"Type\":\"Audio\",\"AudioCodec\":\"mp3\",\"Context\":\"Streaming\",\"Protocol\":\"http\",\"MaxAudioChannels\":\"2\"},{\"Container\":\"opus\",\"Type\":\"Audio\",\"AudioCodec\":\"opus\",\"Context\":\"Streaming\",\"Protocol\":\"http\",\"MaxAudioChannels\":\"2\"},{\"Container\":\"wav\",\"Type\":\"Audio\",\"AudioCodec\":\"wav\",\"Context\":\"Streaming\",\"Protocol\":\"http\",\"MaxAudioChannels\":\"2\"},{\"Container\":\"opus\",\"Type\":\"Audio\",\"AudioCodec\":\"opus\",\"Context\":\"Static\",\"Protocol\":\"http\",\"MaxAudioChannels\":\"2\"},{\"Container\":\"mp3\",\"Type\":\"Audio\",\"AudioCodec\":\"mp3\",\"Context\":\"Static\",\"Protocol\":\"http\",\"MaxAudioChannels\":\"2\"},{\"Container\":\"aac\",\"Type\":\"Audio\",\"AudioCodec\":\"aac\",\"Context\":\"Static\",\"Protocol\":\"http\",\"MaxAudioChannels\":\"2\"},{\"Container\":\"wav\",\"Type\":\"Audio\",\"AudioCodec\":\"wav\",\"Context\":\"Static\",\"Protocol\":\"http\",\"MaxAudioChannels\":\"2\"},{\"Container\":\"mkv\",\"Type\":\"Video\",\"AudioCodec\":\"mp3,aac,opus,flac,vorbis\",\"VideoCodec\":\"h264,h265,hevc,vp8,vp9\",\"Context\":\"Static\",\"MaxAudioChannels\":\"2\",\"CopyTimestamps\":true},{\"Container\":\"m4s,ts\",\"Type\":\"Video\",\"AudioCodec\":\"mp3,aac\",\"VideoCodec\":\"h264,h265,hevc\",\"Context\":\"Streaming\",\"Protocol\":\"hls\",\"MaxAudioChannels\":\"2\",\"MinSegments\":\"2\",\"BreakOnNonKeyFrames\":true,\"ManifestSubtitles\":\"vtt\"},{\"Container\":\"webm\",\"Type\":\"Video\",\"AudioCodec\":\"vorbis\",\"VideoCodec\":\"vpx\",\"Context\":\"Streaming\",\"Protocol\":\"http\",\"MaxAudioChannels\":\"2\"},{\"Container\":\"mp4\",\"Type\":\"Video\",\"AudioCodec\":\"mp3,aac,opus,flac,vorbis\",\"VideoCodec\":\"h264\",\"Context\":\"Static\",\"Protocol\":\"http\"}],\"ContainerProfiles\":[],\"CodecProfiles\":[{\"Type\":\"VideoAudio\",\"Codec\":\"aac\",\"Conditions\":[{\"Condition\":\"Equals\",\"Property\":\"IsSecondaryAudio\",\"Value\":\"false\",\"IsRequired\":\"false\"}]},{\"Type\":\"VideoAudio\",\"Conditions\":[{\"Condition\":\"Equals\",\"Property\":\"IsSecondaryAudio\",\"Value\":\"false\",\"IsRequired\":\"false\"}]},{\"Type\":\"Video\",\"Codec\":\"h264\",\"Conditions\":[{\"Condition\":\"EqualsAny\",\"Property\":\"VideoProfile\",\"Value\":\"high|main|baseline|constrained baseline|high 10\",\"IsRequired\":false},{\"Condition\":\"LessThanEqual\",\"Property\":\"VideoLevel\",\"Value\":\"62\",\"IsRequired\":false}]},{\"Type\":\"Video\",\"Codec\":\"hevc\",\"Conditions\":[{\"Condition\":\"EqualsAny\",\"Property\":\"VideoCodecTag\",\"Value\":\"hvc1\",\"IsRequired\":false}]}],\"SubtitleProfiles\":[{\"Format\":\"vtt\",\"Method\":\"Hls\"},{\"Format\":\"eia_608\",\"Method\":\"VideoSideData\",\"Protocol\":\"hls\"},{\"Format\":\"eia_708\",\"Method\":\"VideoSideData\",\"Protocol\":\"hls\"},{\"Format\":\"vtt\",\"Method\":\"External\"},{\"Format\":\"ass\",\"Method\":\"External\"},{\"Format\":\"ssa\",\"Method\":\"External\"}],\"ResponseProfiles\":[{\"Type\":\"Video\",\"Container\":\"m4v\",\"MimeType\":\"video/mp4\"}]}}\n";

    @Setter
    SiteModel site;

    public static void login(String server, String username, String password, Consumer<Object> completion) {
        HTTPModel model = HTTPModel.builder()
                .url(server + "/emby/Users/authenticatebyname")
                .method("POST")
                .headers(Map.of("Content-Type", "application/x-www-form-urlencoded"))
                .body(String.format("Username=%s&Pw=%s", username, password))
                .modelClass(EmbyUserModel.class)
                .build();

        HTTPUtil.request(model, response -> {
            if (Objects.isNull(response)) {
                completion.accept(null);
                return;
            }
            // get host, port, and protocol from server
            try {
                URL url = new URL(server);
                SiteEndpointModel endpoint = SiteEndpointModel.builder()
                        .host(url.getHost())
                        .port(url.getPort() == -1 ? (url.getProtocol().equals("https") ? 443 : 80) : url.getPort())
                        .protocol(url.getProtocol())
                        .path(url.getPath() == null ? url.getPath() : "/")
                        .build();
                if (response instanceof EmbyUserModel) {
                    EmbyUserModel user = (EmbyUserModel) response;
                    if (user.getAccessToken() == null || user.getUser() == null || user.getServerId() == null) {
                        throw new NullPointerException("Access token is null");
                    }
                    var siteModel = SiteModel.builder()
                            .user(user)
                            .endpoint(endpoint)
                            .build();
                    completion.accept(siteModel);
                    return;
                }
            } catch (MalformedURLException | NullPointerException e) {
                e.printStackTrace();
            }
            completion.accept(null);
        });
    }

    public void getAlbums(Consumer<Object> completion) {
        if (site == null ||
            site.getEndpoint() == null ||
            site.getAccessToken() == null ||
            site.getUser() == null) return;

        HTTPModel model = HTTPModel.builder()
                .url(site.getEndpoint().getBaseUrl() + "emby/Users/" + site.getUserId() + "/Views")
                .method("GET")
                .query(Map.of("X-Emby-Token", site.getAccessToken()))
                .typeReference(new TypeReference<EmbyPageableModel<EmbyAlbumModel>>() {})
                .build();

        HTTPUtil.request(model, response -> {
            if (Objects.isNull(response)) {
                completion.accept(null);
                return;
            }
            if (response instanceof EmbyPageableModel<?>) {
                EmbyPageableModel<EmbyAlbumModel> pageableModel = (EmbyPageableModel<EmbyAlbumModel>) response;
                String baseUrl = site.getEndpoint().getBaseUrl();
                pageableModel.getItems().forEach(item -> item.buildImage(baseUrl));
                completion.accept(pageableModel);
                return;
            }
            completion.accept(null);
        });
    }

    public void getAlbumLatestMedias(String id, Consumer<Object> completion) {
        if (site == null ||
            site.getEndpoint() == null ||
            site.getUser() == null) return;

        HTTPModel model = HTTPModel.builder()
                .url(site.getEndpoint().getBaseUrl() + "emby/Users/" + site.getUserId() + "/Items/Latest")
                .method("GET")
                .query(Map.of(
                        "X-Emby-Token", site.getAccessToken(),
                        "Limit", "16",
                        "ParentId", id,
                        "Recursive", "true",
                        "Fields", "BasicSyncInfo,People,Genres,SortName,Overview,CanDelete,Container,PrimaryImageAspectRatio,Prefix,DateCreated,ProductionYear,Status,EndDate"
                ))
                .typeReference(new TypeReference<List<EmbyMediaModel>>() {})
                .build();

        HTTPUtil.request(model, response -> {
            if (Objects.isNull(response)) {
                completion.accept(null);
                return;
            }
            if (response instanceof List<?>) {
                String baseUrl = site.getEndpoint().getBaseUrl();
                List<EmbyMediaModel> items = (List<EmbyMediaModel>) response;
                items.forEach(item -> item.buildImage(baseUrl));
                completion.accept(items);
                return;
            }
            completion.accept(null);
        });
    }

    public void getMediasCount(Map<String, String> query, Consumer<Integer> completion) {
        if (site == null ||
                site.getEndpoint() == null ||
                site.getUser() == null) return;

        val fields = Map.of(
                "Recursive", "true",
                "ImageTypeLimit", "1",
                "EnableImageTypes", "Primary,Backdrop,Thumb",
                "MediaTypes", "",
                "X-Emby-Token", site.getAccessToken(),
                "Fields", "BasicSyncInfo,People,Genres,SortName,Overview,CanDelete,Container,PrimaryImageAspectRatio,Prefix,DateCreated,ProductionYear,Status,EndDate"
        );
        val params = new HashMap<String, String>();
        params.putAll(fields);
        params.putAll(query);
        HTTPModel model = HTTPModel.builder()
                .url(site.getEndpoint().getBaseUrl() + "emby/Users/" + site.getUserId() + "/Items")
                .method("GET")
                .query(params)
                .typeReference(new TypeReference<EmbyPageableModel<EmbyMediaModel>>() {})
                .build();

        HTTPUtil.request(model, response -> {
            if (Objects.isNull(response)) {
                completion.accept(0);
                return;
            }
            if (response instanceof EmbyPageableModel<?>) {
                String baseUrl = site.getEndpoint().getBaseUrl();
                int count = ((EmbyPageableModel<EmbyMediaModel>) response).getTotalRecordCount();
                completion.accept(count);
                return;
            }
            completion.accept(0);
        });
    }

    @Override
    public void getAllMedias(Map<String, String> query, Consumer<Object> completion) {
        if (site == null ||
                site.getEndpoint() == null ||
                site.getUser() == null) return;

        val fields = Map.of(
                "Recursive", "true",
                "ImageTypeLimit", "1",
                "EnableImageTypes", "Primary,Backdrop,Thumb",
                "MediaTypes", "",
                "X-Emby-Token", site.getAccessToken(),
                "Fields", "BasicSyncInfo,People,Genres,SortName,Overview,CanDelete,Container,PrimaryImageAspectRatio,Prefix,DateCreated,ProductionYear,Status,EndDate"
        );
        val params = new HashMap<String, String>();
        params.putAll(fields);
        params.putAll(query);
        HTTPModel model = HTTPModel.builder()
                .url(site.getEndpoint().getBaseUrl() + "emby/Users/" + site.getUserId() + "/Items")
                .method("GET")
                .query(params)
                .typeReference(new TypeReference<EmbyPageableModel<EmbyMediaModel>>() {})
                .build();

        HTTPUtil.request(model, response -> {
            if (Objects.isNull(response)) {
                completion.accept(null);
                return;
            }
            if (response instanceof EmbyPageableModel<?> pageableModel) {
                String baseUrl = site.getEndpoint().getBaseUrl();
                val totalRecordCount = pageableModel.getTotalRecordCount();
                val items = new ArrayList<EmbyMediaModel>();
                items.addAll((List<EmbyMediaModel>)pageableModel.getItems());
                val part_size = Integer.valueOf(params.getOrDefault("Limit", "50"));
                val latch = new CountDownLatch((int)Math.ceil(totalRecordCount * 1.0f / part_size) - 1);
                for (int i = items.size(); i < totalRecordCount; i+=part_size) {
                    params.put("StartIndex", String.valueOf(i));
                    model.setQuery(params);
                    HTTPUtil.request(model, res -> {
                        if (res instanceof EmbyPageableModel<?> pageable) {
                            items.addAll((List<EmbyMediaModel>)pageable.getItems());
                        }
                        latch.countDown();
                    });
                }
                try {
                    latch.await();
                    items.forEach(item -> item.buildImage(baseUrl));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    if (completion != null) completion.accept(items);
                }
                return;
            }
            completion.accept(null);
        });
    }

    public void getMedias(Map<String, String> query, Consumer<Object> completion) {
        if (site == null ||
                site.getEndpoint() == null ||
                site.getUser() == null) return;

        val fields = Map.of(
                "Recursive", "true",
                "ImageTypeLimit", "1",
                "EnableImageTypes", "Primary,Backdrop,Thumb",
                "MediaTypes", "",
                "X-Emby-Token", site.getAccessToken(),
                "Fields", "BasicSyncInfo,People,Genres,SortName,Overview,CanDelete,Container,PrimaryImageAspectRatio,Prefix,DateCreated,ProductionYear,Status,EndDate"
        );
        val params = new HashMap<String, String>();
        params.putAll(fields);
        params.putAll(query);
        HTTPModel model = HTTPModel.builder()
                .url(site.getEndpoint().getBaseUrl() + "emby/Users/" + site.getUserId() + "/Items")
                .method("GET")
                .query(params)
                .typeReference(new TypeReference<EmbyPageableModel<EmbyMediaModel>>() {})
                .build();

        HTTPUtil.request(model, response -> {
            if (Objects.isNull(response)) {
                completion.accept(null);
                return;
            }
            if (response instanceof EmbyPageableModel<?>) {
                String baseUrl = site.getEndpoint().getBaseUrl();
                List<EmbyMediaModel> items = ((EmbyPageableModel<EmbyMediaModel>) response).getItems();
                items.forEach(item -> item.buildImage(baseUrl));
                completion.accept(items);
                return;
            }
            completion.accept(null);
        });
    }

    public void getSeasons(String seriesId, Consumer<Object> completion) {
        if (site == null ||
                site.getEndpoint() == null ||
                site.getUser() == null) return;

        HTTPModel model = HTTPModel.builder()
                .url(site.getEndpoint().getBaseUrl() + "emby/Shows/" + seriesId + "/Seasons")
                .method("GET")
                .query(Map.of(
                        "X-Emby-Token", site.getAccessToken(),
                        "UserId", site.getUserId(),
                        "Fields", "BasicSyncInfo,People,Genres,SortName,Overview,CanDelete,Container,PrimaryImageAspectRatio,Prefix,DateCreated,ProductionYear,Status,EndDate"
                ))
                .typeReference(new TypeReference<EmbyPageableModel<EmbyMediaModel>>() {})
                .build();

        HTTPUtil.request(model, response -> {
            if (Objects.isNull(response)) {
                completion.accept(null);
                return;
            }
            if (response instanceof EmbyPageableModel<?>) {
                String baseUrl = site.getEndpoint().getBaseUrl();
                List<EmbyMediaModel> items = ((EmbyPageableModel<EmbyMediaModel>) response).getItems();
                items.forEach(item -> item.buildImage(baseUrl));
                completion.accept(items);
                return;
            }
            completion.accept(null);
        });
    }

    public void getEpisodes(String seriesId, String seasonId, Consumer<Object> completion) {
        if (site == null ||
                site.getEndpoint() == null ||
                site.getUser() == null) return;

        HTTPModel model = HTTPModel.builder()
                .url(site.getEndpoint().getBaseUrl() + "emby/Shows/" + seriesId + "/Episodes")
                .method("GET")
                .query(Map.of(
                        "X-Emby-Token", site.getAccessToken(),
                        "UserId", site.getUserId(),
                        "SeasonId", seasonId,
                        "Fields", "BasicSyncInfo,People,Genres,SortName,Overview,CanDelete,Container,PrimaryImageAspectRatio,Prefix,DateCreated,ProductionYear,Status,EndDate"
                ))
                .typeReference(new TypeReference<EmbyPageableModel<EmbyMediaModel>>() {})
                .build();

        HTTPUtil.request(model, response -> {
            if (Objects.isNull(response)) {
                completion.accept(null);
                return;
            }
            if (response instanceof EmbyPageableModel<?>) {
                String baseUrl = site.getEndpoint().getBaseUrl();
                List<EmbyMediaModel> items = ((EmbyPageableModel<EmbyMediaModel>) response).getItems();
                items.forEach(item -> item.buildImage(baseUrl));
                completion.accept(items);
                return;
            }
            completion.accept(null);
        });
    }

    public void getPlayback(String id, Consumer<Object> completion) {
        if (site == null ||
            site.getEndpoint() == null ||
            site.getUser() == null) return;

        val store = XGET(GlobalStore.class);
        val media = store.getDataSource().getMediaMap().get(id);
        val startTimeTicks = media != null && media.getUserData() != null ? media.getUserData().getPlaybackPositionTicks() : 0;
        HTTPModel model = HTTPModel.builder()
                .url(site.getEndpoint().getBaseUrl() + "emby/Items/" + id + "/PlaybackInfo")
                .method("POST")
                .query(Map.of(
                    "StartTimeTicks", String.valueOf(startTimeTicks),
                    "IsPlayback", "false",
                    "AutoOpenLiveStream", "false",
                    "MaxStreamingBitrate", "140000000",
                    "UserId", site.getUserId(),
                    "reqformat", "json",
                    "X-Emby-Token", site.getAccessToken()
                ))
                .headers(Map.of("Content-Type", "application/json"))
                .body(kDeviceProfile)
                .typeReference(new TypeReference<EmbyPlaybackModel>() {})
                .build();

        HTTPUtil.request(model, response -> {
            if (Objects.isNull(response)) {
                completion.accept(null);
                return;
            }
            if (response instanceof EmbyPlaybackModel) {
                String baseUrl = site.getEndpoint().getBaseUrl();
                EmbyPlaybackModel source = (EmbyPlaybackModel) response;
                source.buildUrl(baseUrl);
                completion.accept(source);
                return;
            }
            completion.accept(null);
        });
    }

    @Override
    public void getSimilar(String id, Consumer<Object> completion) {
        // /Items/{Id}/Similar
        if (site == null ||
                site.getEndpoint() == null ||
                site.getUser() == null) return;

        HTTPModel model = HTTPModel.builder()
                .url(site.getEndpoint().getBaseUrl() + "Items/" + id + "/Similar")
                .method("GET")
                .query(Map.of(
                        "X-Emby-Token", site.getAccessToken(),
                        "Recursive", "true",
                        "Fields", "BasicSyncInfo,People,Genres,SortName,Overview,CanDelete,Container,PrimaryImageAspectRatio,Prefix,DateCreated,ProductionYear,Status,EndDate",
                        "ImageTypeLimit", "1",
                        "EnableImageTypes", "Primary,Backdrop,Thumb",
                        "MediaTypes", "Video",
                        "Limit", "16"
                ))
                .typeReference(new TypeReference<EmbyPageableModel<EmbyMediaModel>>() {})
                .build();

        HTTPUtil.request(model, response -> {
            if (Objects.isNull(response)) {
                completion.accept(null);
                return;
            }
            if (response instanceof EmbyPageableModel<?>) {
                String baseUrl = site.getEndpoint().getBaseUrl();
                List<EmbyMediaModel> items = ((EmbyPageableModel<EmbyMediaModel>) response).getItems();
                items.forEach(item -> item.buildImage(baseUrl));
                completion.accept(items);
                return;
            }
            completion.accept(null);
        });
    }

    public void getResume(Consumer<Object> completion) {
        if (site == null ||
                site.getEndpoint() == null ||
                site.getUser() == null) return;

        HTTPModel model = HTTPModel.builder()
                .url(site.getEndpoint().getBaseUrl() + "emby/Users/" + site.getUserId() + "/Items/Resume")
                .method("GET")
                .query(Map.of(
                        "X-Emby-Token", site.getAccessToken(),
                        "Recursive", "true",
                        "Fields", "BasicSyncInfo,People,Genres,SortName,Overview,CanDelete,Container,PrimaryImageAspectRatio,Prefix,DateCreated,ProductionYear,Status,EndDate",
                        "ImageTypeLimit", "1",
                        "EnableImageTypes", "Primary,Backdrop,Thumb",
                        "MediaTypes", "Video",
                        "Limit", "50"
                ))
                .typeReference(new TypeReference<EmbyPageableModel<EmbyMediaModel>>() {})
                .build();

        HTTPUtil.request(model, response -> {
            if (Objects.isNull(response)) {
                completion.accept(null);
                return;
            }
            if (response instanceof EmbyPageableModel<?>) {
                String baseUrl = site.getEndpoint().getBaseUrl();
                List<EmbyMediaModel> items = ((EmbyPageableModel<EmbyMediaModel>) response).getItems();
                items.forEach(item -> item.buildImage(baseUrl));
                completion.accept(items);
                return;
            }
            completion.accept(null);
        });
    }

    public void markFavorite(String id, boolean isFavorite, Consumer<Object> completion) {
        if (site == null ||
                site.getEndpoint() == null ||
                site.getUser() == null) return;

        HTTPModel model = HTTPModel.builder()
                .url(site.getEndpoint().getBaseUrl() + "emby/Users/" + site.getUserId() + "/FavoriteItems/" + id + (isFavorite ? "/" : "/Delete"))
                .method("POST")
                .body("")
                .query(Map.of(
                        "X-Emby-Token", site.getAccessToken()
                ))
                .modelClass(EmbyUserData.class)
                .build();

        HTTPUtil.request(model, response -> {
            if (Objects.isNull(response)) {
                completion.accept(null);
                return;
            }
            if (response instanceof EmbyUserData) {
                completion.accept(response);
                return;
            }
            completion.accept(null);
        });
    }

    @Override
    public void getRecommendations(Consumer<Object> completion) {
        if (site == null ||
                site.getEndpoint() == null ||
                site.getUser() == null) return;

        HTTPModel model = HTTPModel.builder()
                .url(site.getEndpoint().getBaseUrl() + "emby/Items")
                .method("GET")
                .query(Map.of(
                        "X-Emby-Token", site.getAccessToken(),
                        "SortBy", "IsFavoriteOrLiked,Random",
                        "IncludeItemTypes", "Movie,Series,MusicArtist",
                        "Limit", "20",
                        "Recursive", "true",
                        "ImageTypeLimit", "0",
                        "UserId", site.getUserId(),
                        "Fields", "BasicSyncInfo,People,Genres,SortName,Overview,CanDelete,Container,PrimaryImageAspectRatio,Prefix,DateCreated,ProductionYear,Status,EndDate"
                ))
                .typeReference(new TypeReference<EmbyPageableModel<EmbyMediaModel>>() {})
                .build();

        HTTPUtil.request(model, response -> {
            if (Objects.isNull(response)) {
                completion.accept(null);
                return;
            }
            if (response instanceof EmbyPageableModel<?>) {
                String baseUrl = site.getEndpoint().getBaseUrl();
                List<EmbyMediaModel> items = ((EmbyPageableModel<EmbyMediaModel>) response).getItems();
                items.forEach(item -> item.buildImage(baseUrl));
                completion.accept(items);
                return;
            }
            completion.accept(null);
        });
    }

    public void trackPlay(MediaPlayState state, EmbyPlaybackData data, Consumer<Object> completion) {
        if (site == null ||
                site.getEndpoint() == null ||
                site.getUser() == null) return;

        val playState = switch (state) {
            case STOPPED -> "Stopped";
            case PLAYING -> "Progress";
            default -> "";
        };

        HTTPModel model = HTTPModel.builder()
                .url(site.getEndpoint().getBaseUrl() + "emby/Sessions/Playing/" + playState)
                .method("POST")
                .body(XGET(JSONAdapter.class).toJSON(data))
                .headers(Map.of(
                        "X-Emby-Token", site.getAccessToken(),
                        "Content-Type", "application/json",
                        "reqformat", "json"
                ))
                .modelClass(EmbyUserData.class)
                .build();

        HTTPUtil.request(model, response -> {
            if (Objects.isNull(response)) {
                completion.accept(null);
                return;
            }
            if (response instanceof EmbyUserData) {
                completion.accept(response);
                return;
            }
            completion.accept(null);
        });

    }
}
