package top.ourfor.app.iplay.api.emby;

import static top.ourfor.app.iplay.module.Bean.XGET;

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
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.With;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import top.ourfor.app.iplay.bean.IJSONAdapter;
import top.ourfor.app.iplay.common.api.IDataSourceApi;
import top.ourfor.app.iplay.common.type.MediaPlayState;
import top.ourfor.app.iplay.model.AlbumModel;
import top.ourfor.app.iplay.model.MediaModel;
import top.ourfor.app.iplay.model.UserModel;
import top.ourfor.app.iplay.store.IAppStore;
import top.ourfor.app.iplay.util.HTTPUtil;
import top.ourfor.app.iplay.util.HTTPModel;
import top.ourfor.app.iplay.common.model.SiteEndpointModel;
import top.ourfor.app.iplay.model.SiteModel;

@With
@Slf4j
@Setter
@Builder
@EqualsAndHashCode
public class EmbyApi implements IDataSourceApi {
    private static final String kDeviceProfile = "{\"DeviceProfile\":{\"MaxStaticBitrate\":140000000,\"MaxStreamingBitrate\":140000000,\"MusicStreamingTranscodingBitrate\":192000,\"DirectPlayProfiles\":[{\"Container\":\"mp4,m4v\",\"Type\":\"Video\",\"VideoCodec\":\"h264,h265,hevc,vp8,vp9\",\"AudioCodec\":\"mp3,aac,opus,flac,vorbis\"},{\"Container\":\"mkv\",\"Type\":\"Video\",\"VideoCodec\":\"h264,h265,hevc,vp8,vp9\",\"AudioCodec\":\"mp3,aac,opus,flac,vorbis\"},{\"Container\":\"flv\",\"Type\":\"Video\",\"VideoCodec\":\"h264\",\"AudioCodec\":\"aac,mp3\"},{\"Container\":\"mov\",\"Type\":\"Video\",\"VideoCodec\":\"h264\",\"AudioCodec\":\"mp3,aac,opus,flac,vorbis\"},{\"Container\":\"opus\",\"Type\":\"Audio\"},{\"Container\":\"mp3\",\"Type\":\"Audio\",\"AudioCodec\":\"mp3\"},{\"Container\":\"mp2,mp3\",\"Type\":\"Audio\",\"AudioCodec\":\"mp2\"},{\"Container\":\"aac\",\"Type\":\"Audio\",\"AudioCodec\":\"aac\"},{\"Container\":\"m4a\",\"AudioCodec\":\"aac\",\"Type\":\"Audio\"},{\"Container\":\"mp4\",\"AudioCodec\":\"aac\",\"Type\":\"Audio\"},{\"Container\":\"flac\",\"Type\":\"Audio\"},{\"Container\":\"webma,webm\",\"Type\":\"Audio\"},{\"Container\":\"wav\",\"Type\":\"Audio\",\"AudioCodec\":\"PCM_S16LE,PCM_S24LE\"},{\"Container\":\"ogg\",\"Type\":\"Audio\"},{\"Container\":\"webm\",\"Type\":\"Video\",\"AudioCodec\":\"vorbis,opus\",\"VideoCodec\":\"VP8,VP9\"}],\"TranscodingProfiles\":[{\"Container\":\"aac\",\"Type\":\"Audio\",\"AudioCodec\":\"aac\",\"Context\":\"Streaming\",\"Protocol\":\"hls\",\"MaxAudioChannels\":\"2\",\"MinSegments\":\"2\",\"BreakOnNonKeyFrames\":true},{\"Container\":\"aac\",\"Type\":\"Audio\",\"AudioCodec\":\"aac\",\"Context\":\"Streaming\",\"Protocol\":\"http\",\"MaxAudioChannels\":\"2\"},{\"Container\":\"mp3\",\"Type\":\"Audio\",\"AudioCodec\":\"mp3\",\"Context\":\"Streaming\",\"Protocol\":\"http\",\"MaxAudioChannels\":\"2\"},{\"Container\":\"opus\",\"Type\":\"Audio\",\"AudioCodec\":\"opus\",\"Context\":\"Streaming\",\"Protocol\":\"http\",\"MaxAudioChannels\":\"2\"},{\"Container\":\"wav\",\"Type\":\"Audio\",\"AudioCodec\":\"wav\",\"Context\":\"Streaming\",\"Protocol\":\"http\",\"MaxAudioChannels\":\"2\"},{\"Container\":\"opus\",\"Type\":\"Audio\",\"AudioCodec\":\"opus\",\"Context\":\"Static\",\"Protocol\":\"http\",\"MaxAudioChannels\":\"2\"},{\"Container\":\"mp3\",\"Type\":\"Audio\",\"AudioCodec\":\"mp3\",\"Context\":\"Static\",\"Protocol\":\"http\",\"MaxAudioChannels\":\"2\"},{\"Container\":\"aac\",\"Type\":\"Audio\",\"AudioCodec\":\"aac\",\"Context\":\"Static\",\"Protocol\":\"http\",\"MaxAudioChannels\":\"2\"},{\"Container\":\"wav\",\"Type\":\"Audio\",\"AudioCodec\":\"wav\",\"Context\":\"Static\",\"Protocol\":\"http\",\"MaxAudioChannels\":\"2\"},{\"Container\":\"mkv\",\"Type\":\"Video\",\"AudioCodec\":\"mp3,aac,opus,flac,vorbis\",\"VideoCodec\":\"h264,h265,hevc,vp8,vp9\",\"Context\":\"Static\",\"MaxAudioChannels\":\"2\",\"CopyTimestamps\":true},{\"Container\":\"m4s,ts\",\"Type\":\"Video\",\"AudioCodec\":\"mp3,aac\",\"VideoCodec\":\"h264,h265,hevc\",\"Context\":\"Streaming\",\"Protocol\":\"hls\",\"MaxAudioChannels\":\"2\",\"MinSegments\":\"2\",\"BreakOnNonKeyFrames\":true,\"ManifestSubtitles\":\"vtt\"},{\"Container\":\"webm\",\"Type\":\"Video\",\"AudioCodec\":\"vorbis\",\"VideoCodec\":\"vpx\",\"Context\":\"Streaming\",\"Protocol\":\"http\",\"MaxAudioChannels\":\"2\"},{\"Container\":\"mp4\",\"Type\":\"Video\",\"AudioCodec\":\"mp3,aac,opus,flac,vorbis\",\"VideoCodec\":\"h264\",\"Context\":\"Static\",\"Protocol\":\"http\"}],\"ContainerProfiles\":[],\"CodecProfiles\":[{\"Type\":\"VideoAudio\",\"Codec\":\"aac\",\"Conditions\":[{\"Condition\":\"Equals\",\"Property\":\"IsSecondaryAudio\",\"Value\":\"false\",\"IsRequired\":\"false\"}]},{\"Type\":\"VideoAudio\",\"Conditions\":[{\"Condition\":\"Equals\",\"Property\":\"IsSecondaryAudio\",\"Value\":\"false\",\"IsRequired\":\"false\"}]},{\"Type\":\"Video\",\"Codec\":\"h264\",\"Conditions\":[{\"Condition\":\"EqualsAny\",\"Property\":\"VideoProfile\",\"Value\":\"high|main|baseline|constrained baseline|high 10\",\"IsRequired\":false},{\"Condition\":\"LessThanEqual\",\"Property\":\"VideoLevel\",\"Value\":\"62\",\"IsRequired\":false}]},{\"Type\":\"Video\",\"Codec\":\"hevc\",\"Conditions\":[{\"Condition\":\"EqualsAny\",\"Property\":\"VideoCodecTag\",\"Value\":\"hvc1\",\"IsRequired\":false}]}],\"SubtitleProfiles\":[{\"Format\":\"vtt\",\"Method\":\"Hls\"},{\"Format\":\"eia_608\",\"Method\":\"VideoSideData\",\"Protocol\":\"hls\"},{\"Format\":\"eia_708\",\"Method\":\"VideoSideData\",\"Protocol\":\"hls\"},{\"Format\":\"vtt\",\"Method\":\"External\"},{\"Format\":\"ass\",\"Method\":\"External\"},{\"Format\":\"ssa\",\"Method\":\"External\"}],\"ResponseProfiles\":[{\"Type\":\"Video\",\"Container\":\"m4v\",\"MimeType\":\"video/mp4\"}]}}\n";

    SiteModel site;

    public static void login(String server, String username, String password, Consumer<Object> completion) {
        var model = HTTPModel.<EmbyModel.EmbyUserModel>builder()
                .url(server + "/emby/Users/authenticatebyname")
                .method("POST")
                .headers(Map.of("Content-Type", "application/x-www-form-urlencoded"))
                .body(String.format("Username=%s&Pw=%s", username, password))
                .typeReference(new TypeReference<EmbyModel.EmbyUserModel>() {})
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
                if (response != null) {
                    if (response.getAccessToken() == null || response.getUser() == null || response.getServerId() == null) {
                        throw new NullPointerException("Access token is null");
                    }
                    var siteModel = SiteModel.builder()
                            .user(UserModel.builder()
                                    .siteId(response.getServerId())
                                    .id(response.getUser().getId())
                                    .username(username)
                                    .password(password)
                                    .accessToken(response.getAccessToken())
                                    .build())
                            .endpoint(endpoint)
                            .build();
                    completion.accept(siteModel);
                    return;
                }
            } catch (MalformedURLException | NullPointerException e) {
                log.error("Failed to parse server url: {}", server);
                log.error("Error: ", e);
            }
            completion.accept(null);
        });
    }

    public void getAlbums(Consumer<List<AlbumModel>> completion) {
        if (site == null ||
            site.getEndpoint() == null ||
            site.getAccessToken() == null ||
            site.getUser() == null) return;

        var model = HTTPModel.<EmbyModel.EmbyPageableModel<EmbyModel.EmbyAlbumModel>>builder()
                .url(site.getEndpoint().getBaseUrl() + "emby/Users/" + site.getUserId() + "/Views")
                .method("GET")
                .query(Map.of("X-Emby-Token", site.getAccessToken()))
                .typeReference(new TypeReference<EmbyModel.EmbyPageableModel<EmbyModel.EmbyAlbumModel>>() { })
                .build();

        HTTPUtil.request(model, response -> {
            if (Objects.isNull(response)) {
                completion.accept(null);
                return;
            }
            if (response != null) {
                String baseUrl = site.getEndpoint().getBaseUrl();
                response.getItems().forEach(item -> item.buildImage(baseUrl));
                val albumItems = response.items.stream().map(item -> AlbumModel.builder()
                        .id(item.getId())
                        .title(item.getName())
                        .type(item.getCollectionType())
                        .backdrop(item.getImage().getPrimary())
                        .build()).collect(Collectors.toList());
                completion.accept(albumItems);
                return;
            }
            completion.accept(null);
        });
    }

    public void getAlbumLatestMedias(String id, Consumer<List<MediaModel>> completion) {
        if (site == null ||
            site.getEndpoint() == null ||
            site.getUser() == null) return;

        var model = HTTPModel.<List<EmbyModel.EmbyMediaModel>>builder()
                .url(site.getEndpoint().getBaseUrl() + "emby/Users/" + site.getUserId() + "/Items/Latest")
                .method("GET")
                .query(Map.of(
                        "X-Emby-Token", site.getAccessToken(),
                        "Limit", "16",
                        "ParentId", id,
                        "Recursive", "true",
                        "Fields", "BasicSyncInfo,People,Genres,SortName,Overview,CanDelete,Container,PrimaryImageAspectRatio,Prefix,DateCreated,ProductionYear,Status,EndDate"
                ))
                .typeReference(new TypeReference<List<EmbyModel.EmbyMediaModel>>() {})
                .build();

        HTTPUtil.request(model, response -> {
            if (Objects.isNull(response)) {
                completion.accept(null);
                return;
            }
            if (response != null) {
                String baseUrl = site.getEndpoint().getBaseUrl();
                response.forEach(item -> item.buildImage(baseUrl));
                val finalItems = response.stream().map(EmbyModel.EmbyMediaModel::toMediaModel).collect(Collectors.toList());
                completion.accept(finalItems);
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
        var model = HTTPModel.<EmbyModel.EmbyPageableModel<EmbyModel.EmbyMediaModel>>builder()
                .url(site.getEndpoint().getBaseUrl() + "emby/Users/" + site.getUserId() + "/Items")
                .method("GET")
                .query(params)
                .typeReference(new TypeReference<EmbyModel.EmbyPageableModel<EmbyModel.EmbyMediaModel>>() { })
                .build();

        HTTPUtil.request(model, response -> {
            if (Objects.isNull(response)) {
                completion.accept(0);
                return;
            }
            if (response != null) {
                int count = response.getTotalRecordCount();
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
        var model = HTTPModel.<EmbyModel.EmbyPageableModel<EmbyModel.EmbyMediaModel>>builder()
                .url(site.getEndpoint().getBaseUrl() + "emby/Users/" + site.getUserId() + "/Items")
                .method("GET")
                .query(params)
                .typeReference(new TypeReference<EmbyModel.EmbyPageableModel<EmbyModel.EmbyMediaModel>>() {})
                .build();

        HTTPUtil.request(model, response -> {
            if (Objects.isNull(response)) {
                completion.accept(null);
                return;
            }
            if (response != null) {
                String baseUrl = site.getEndpoint().getBaseUrl();
                val totalRecordCount = response.getTotalRecordCount();
                val items = new ArrayList<EmbyModel.EmbyMediaModel>();
                items.addAll((List<EmbyModel.EmbyMediaModel>)response.getItems());
                val part_size = Integer.valueOf(params.getOrDefault("Limit", "50"));
                val latch = new CountDownLatch((int)Math.ceil(totalRecordCount * 1.0f / part_size) - 1);
                for (int i = items.size(); i < totalRecordCount; i+=part_size) {
                    params.put("StartIndex", String.valueOf(i));
                    model.setQuery(params);
                    HTTPUtil.request(model, pageable -> {
                        if (pageable != null) {
                            items.addAll(pageable.getItems());
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

    public void getMedias(Map<String, String> query, Consumer<List<MediaModel>> completion) {
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
        val model = HTTPModel.<EmbyModel.EmbyPageableModel<EmbyModel.EmbyMediaModel>>builder()
                .url(site.getEndpoint().getBaseUrl() + "emby/Users/" + site.getUserId() + "/Items")
                .method("GET")
                .query(params)
                .typeReference(new TypeReference<EmbyModel.EmbyPageableModel<EmbyModel.EmbyMediaModel>>() {})
                .build();

        HTTPUtil.request(model, response -> {
            if (Objects.isNull(response)) {
                completion.accept(null);
                return;
            }
            if (response != null) {
                String baseUrl = site.getEndpoint().getBaseUrl();
                List<EmbyModel.EmbyMediaModel> items = ((EmbyModel.EmbyPageableModel<EmbyModel.EmbyMediaModel>) response).getItems();
                items.forEach(item -> item.buildImage(baseUrl));
                val mediaItems = items.stream().map(EmbyModel.EmbyMediaModel::toMediaModel).collect(Collectors.toList());
                completion.accept(mediaItems);
                return;
            }
            completion.accept(null);
        });
    }

    public void getSeasons(String seriesId, Consumer<Object> completion) {
        if (site == null ||
                site.getEndpoint() == null ||
                site.getUser() == null) return;

        var model = HTTPModel.<EmbyModel.EmbyPageableModel<EmbyModel.EmbyMediaModel>>builder()
                .url(site.getEndpoint().getBaseUrl() + "emby/Shows/" + seriesId + "/Seasons")
                .method("GET")
                .query(Map.of(
                        "X-Emby-Token", site.getAccessToken(),
                        "UserId", site.getUserId(),
                        "Fields", "BasicSyncInfo,People,Genres,SortName,Overview,CanDelete,Container,PrimaryImageAspectRatio,Prefix,DateCreated,ProductionYear,Status,EndDate"
                ))
                .typeReference(new TypeReference<EmbyModel.EmbyPageableModel<EmbyModel.EmbyMediaModel>>() { })
                .build();

        HTTPUtil.request(model, response -> {
            if (Objects.isNull(response)) {
                completion.accept(null);
                return;
            }
            if (response != null) {
                String baseUrl = site.getEndpoint().getBaseUrl();
                List<EmbyModel.EmbyMediaModel> items = ((EmbyModel.EmbyPageableModel<EmbyModel.EmbyMediaModel>) response).getItems();
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

        var model = HTTPModel.<EmbyModel.EmbyPageableModel<EmbyModel.EmbyMediaModel>>builder()
                .url(site.getEndpoint().getBaseUrl() + "emby/Shows/" + seriesId + "/Episodes")
                .method("GET")
                .query(Map.of(
                        "X-Emby-Token", site.getAccessToken(),
                        "UserId", site.getUserId(),
                        "SeasonId", seasonId,
                        "Fields", "BasicSyncInfo,People,Genres,SortName,Overview,CanDelete,Container,PrimaryImageAspectRatio,Prefix,DateCreated,ProductionYear,Status,EndDate"
                ))
                .typeReference(new TypeReference<EmbyModel.EmbyPageableModel<EmbyModel.EmbyMediaModel>>() {})
                .build();

        HTTPUtil.request(model, response -> {
            if (Objects.isNull(response)) {
                completion.accept(null);
                return;
            }
            if (response != null) {
                String baseUrl = site.getEndpoint().getBaseUrl();
                List<EmbyModel.EmbyMediaModel> items = ((EmbyModel.EmbyPageableModel<EmbyModel.EmbyMediaModel>) response).getItems();
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

        val store = XGET(IAppStore.class);
        val media = store.getDataSource().getMediaMap().get(id);
        val startTimeTicks = media != null && media.getUserData() != null ? media.getUserData().getPlaybackPositionTicks() : 0;
        var model = HTTPModel.<EmbyModel.EmbyPlaybackModel>builder()
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
                .typeReference(new TypeReference<EmbyModel.EmbyPlaybackModel>() {})
                .build();

        HTTPUtil.request(model, response -> {
            if (Objects.isNull(response)) {
                completion.accept(null);
                return;
            }
            if (response != null) {
                String baseUrl = site.getEndpoint().getBaseUrl();
                response.buildUrl(baseUrl);
                completion.accept(response);
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

        var model = HTTPModel.<EmbyModel.EmbyPageableModel<EmbyModel.EmbyMediaModel>>builder()
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
                .typeReference(new TypeReference<EmbyModel.EmbyPageableModel<EmbyModel.EmbyMediaModel>>() { })
                .build();

        HTTPUtil.request(model, response -> {
            if (Objects.isNull(response)) {
                completion.accept(null);
                return;
            }
            if (response != null) {
                String baseUrl = site.getEndpoint().getBaseUrl();
                List<EmbyModel.EmbyMediaModel> items = response.getItems();
                items.forEach(item -> item.buildImage(baseUrl));
                var mediaItems = items.stream().map(EmbyModel.EmbyMediaModel::toMediaModel).collect(Collectors.toList());
                completion.accept(mediaItems);
                return;
            }
            completion.accept(null);
        });
    }

    public void getResume(Consumer<Object> completion) {
        if (site == null ||
                site.getEndpoint() == null ||
                site.getUser() == null) return;

        var model = HTTPModel.<EmbyModel.EmbyPageableModel<EmbyModel.EmbyMediaModel>>builder()
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
                .typeReference(new TypeReference<EmbyModel.EmbyPageableModel<EmbyModel.EmbyMediaModel>>() {})
                .build();

        HTTPUtil.request(model, response -> {
            if (Objects.isNull(response)) {
                completion.accept(null);
                return;
            }
            if (response != null) {
                String baseUrl = site.getEndpoint().getBaseUrl();
                List<EmbyModel.EmbyMediaModel> items = ((EmbyModel.EmbyPageableModel<EmbyModel.EmbyMediaModel>) response).getItems();
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

        var model = HTTPModel.<EmbyModel.EmbyUserData>builder()
                .url(site.getEndpoint().getBaseUrl() + "emby/Users/" + site.getUserId() + "/FavoriteItems/" + id + (isFavorite ? "/" : "/Delete"))
                .method("POST")
                .body("")
                .query(Map.of(
                        "X-Emby-Token", site.getAccessToken()
                ))
                .typeReference(new TypeReference<EmbyModel.EmbyUserData>() { })
                .build();

        HTTPUtil.request(model, response -> {
            if (Objects.isNull(response)) {
                completion.accept(null);
                return;
            }
            if (response != null) {
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

        var model = HTTPModel.<EmbyModel.EmbyPageableModel<EmbyModel.EmbyMediaModel>>builder()
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
                .typeReference(new TypeReference<EmbyModel.EmbyPageableModel<EmbyModel.EmbyMediaModel>>() {})
                .build();

        HTTPUtil.request(model, response -> {
            if (Objects.isNull(response)) {
                completion.accept(null);
                return;
            }
            if (response != null) {
                String baseUrl = site.getEndpoint().getBaseUrl();
                List<EmbyModel.EmbyMediaModel> items = response.getItems();
                items.forEach(item -> item.buildImage(baseUrl));
                completion.accept(items);
                return;
            }
            completion.accept(null);
        });
    }

    public void trackPlay(MediaPlayState state, EmbyModel.EmbyPlaybackData data, Consumer<Object> completion) {
        if (site == null ||
                site.getEndpoint() == null ||
                site.getUser() == null) return;

        val playState = switch (state) {
            case STOPPED -> "Stopped";
            case PLAYING -> "Progress";
            default -> "";
        };

        var model = HTTPModel.<EmbyModel.EmbyUserData>builder()
                .url(site.getEndpoint().getBaseUrl() + "emby/Sessions/Playing/" + playState)
                .method("POST")
                .body(XGET(IJSONAdapter.class).toJSON(data))
                .headers(Map.of(
                        "X-Emby-Token", site.getAccessToken(),
                        "Content-Type", "application/json",
                        "reqformat", "json"
                ))
                .typeReference(new TypeReference<EmbyModel.EmbyUserData>() {})
                .build();

        HTTPUtil.request(model, response -> {
            if (Objects.isNull(response)) {
                completion.accept(null);
                return;
            }
            if (response != null) {
                completion.accept(response);
                return;
            }
            completion.accept(null);
        });

    }

    @Override
    public void getSiteInfo(Consumer<Object> completion) {
        if (site == null ||
                site.getEndpoint() == null ||
                site.getUser() == null) return;

        // /emby/system/info/public
        var model = HTTPModel.<EmbyModel.EmbySiteInfo>builder()
                .url(site.getEndpoint().getBaseUrl() + "emby/system/info/public")
                .method("GET")
                .typeReference(new TypeReference<EmbyModel.EmbySiteInfo>() {})
                .build();

        HTTPUtil.request(model, response -> {
            if (Objects.isNull(response)) {
                completion.accept(null);
                return;
            }
            if (response != null) {
                completion.accept(response);
                return;
            }
            completion.accept(null);
        });
    }
}
