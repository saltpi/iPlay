package top.ourfor.app.iplay.api.jellyfin;

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
import lombok.val;
import top.ourfor.app.iplay.api.emby.EmbyModel;
import top.ourfor.app.iplay.bean.IJSONAdapter;
import top.ourfor.app.iplay.common.api.IDataSourceApi;
import top.ourfor.app.iplay.common.model.SiteEndpointModel;
import top.ourfor.app.iplay.common.type.MediaPlayState;
import top.ourfor.app.iplay.model.AlbumModel;
import top.ourfor.app.iplay.model.ImageType;
import top.ourfor.app.iplay.model.MediaModel;
import top.ourfor.app.iplay.model.SiteModel;
import top.ourfor.app.iplay.model.UserModel;
import top.ourfor.app.iplay.store.IAppStore;
import top.ourfor.app.iplay.util.HTTPModel;
import top.ourfor.app.iplay.util.HTTPUtil;

@Setter
@Builder
@With
@EqualsAndHashCode
public class JellyfinApi implements IDataSourceApi {
    private static final String kDeviceProfile = "{\"DeviceProfile\":{\"MaxStreamingBitrate\":120000000,\"MaxStaticBitrate\":100000000,\"MusicStreamingTranscodingBitrate\":384000,\"DirectPlayProfiles\":[{\"Container\":\"webm\",\"Type\":\"Video\",\"VideoCodec\":\"vp8\",\"AudioCodec\":\"vorbis\"},{\"Container\":\"mp4,m4v\",\"Type\":\"Video\",\"VideoCodec\":\"h264,hevc,vp9\",\"AudioCodec\":\"aac,ac3,eac3,flac,alac,vorbis\"},{\"Container\":\"mov\",\"Type\":\"Video\",\"VideoCodec\":\"h264\",\"AudioCodec\":\"aac,ac3,eac3,flac,alac,vorbis\"},{\"Container\":\"mp3\",\"Type\":\"Audio\"},{\"Container\":\"aac\",\"Type\":\"Audio\"},{\"Container\":\"m4a\",\"AudioCodec\":\"aac\",\"Type\":\"Audio\"},{\"Container\":\"m4b\",\"AudioCodec\":\"aac\",\"Type\":\"Audio\"},{\"Container\":\"flac\",\"Type\":\"Audio\"},{\"Container\":\"alac\",\"Type\":\"Audio\"},{\"Container\":\"m4a\",\"AudioCodec\":\"alac\",\"Type\":\"Audio\"},{\"Container\":\"m4b\",\"AudioCodec\":\"alac\",\"Type\":\"Audio\"},{\"Container\":\"webma\",\"Type\":\"Audio\"},{\"Container\":\"webm\",\"AudioCodec\":\"webma\",\"Type\":\"Audio\"},{\"Container\":\"wav\",\"Type\":\"Audio\"},{\"Container\":\"hls\",\"Type\":\"Video\",\"VideoCodec\":\"hevc,h264\",\"AudioCodec\":\"aac,ac3,eac3,flac,alac\"},{\"Container\":\"hls\",\"Type\":\"Video\",\"VideoCodec\":\"h264\",\"AudioCodec\":\"aac,mp3,ac3,eac3\"}],\"TranscodingProfiles\":[{\"Container\":\"mp4\",\"Type\":\"Audio\",\"AudioCodec\":\"aac\",\"Context\":\"Streaming\",\"Protocol\":\"hls\",\"MaxAudioChannels\":\"6\",\"MinSegments\":\"2\",\"BreakOnNonKeyFrames\":true},{\"Container\":\"aac\",\"Type\":\"Audio\",\"AudioCodec\":\"aac\",\"Context\":\"Streaming\",\"Protocol\":\"http\",\"MaxAudioChannels\":\"6\"},{\"Container\":\"mp3\",\"Type\":\"Audio\",\"AudioCodec\":\"mp3\",\"Context\":\"Streaming\",\"Protocol\":\"http\",\"MaxAudioChannels\":\"6\"},{\"Container\":\"wav\",\"Type\":\"Audio\",\"AudioCodec\":\"wav\",\"Context\":\"Streaming\",\"Protocol\":\"http\",\"MaxAudioChannels\":\"6\"},{\"Container\":\"mp3\",\"Type\":\"Audio\",\"AudioCodec\":\"mp3\",\"Context\":\"Static\",\"Protocol\":\"http\",\"MaxAudioChannels\":\"6\"},{\"Container\":\"aac\",\"Type\":\"Audio\",\"AudioCodec\":\"aac\",\"Context\":\"Static\",\"Protocol\":\"http\",\"MaxAudioChannels\":\"6\"},{\"Container\":\"wav\",\"Type\":\"Audio\",\"AudioCodec\":\"wav\",\"Context\":\"Static\",\"Protocol\":\"http\",\"MaxAudioChannels\":\"6\"},{\"Container\":\"mp4\",\"Type\":\"Video\",\"AudioCodec\":\"aac,ac3,eac3,flac,alac\",\"VideoCodec\":\"hevc,h264\",\"Context\":\"Streaming\",\"Protocol\":\"hls\",\"MaxAudioChannels\":\"6\",\"MinSegments\":\"2\",\"BreakOnNonKeyFrames\":true},{\"Container\":\"ts\",\"Type\":\"Video\",\"AudioCodec\":\"aac,mp3,ac3,eac3\",\"VideoCodec\":\"h264\",\"Context\":\"Streaming\",\"Protocol\":\"hls\",\"MaxAudioChannels\":\"6\",\"MinSegments\":\"2\",\"BreakOnNonKeyFrames\":true}],\"ContainerProfiles\":[],\"CodecProfiles\":[{\"Type\":\"Video\",\"Codec\":\"h264\",\"Conditions\":[{\"Condition\":\"NotEquals\",\"Property\":\"IsAnamorphic\",\"Value\":\"true\",\"IsRequired\":false},{\"Condition\":\"EqualsAny\",\"Property\":\"VideoProfile\",\"Value\":\"high|main|baseline|constrained baseline\",\"IsRequired\":false},{\"Condition\":\"EqualsAny\",\"Property\":\"VideoRangeType\",\"Value\":\"SDR\",\"IsRequired\":false},{\"Condition\":\"LessThanEqual\",\"Property\":\"VideoLevel\",\"Value\":\"52\",\"IsRequired\":false},{\"Condition\":\"NotEquals\",\"Property\":\"IsInterlaced\",\"Value\":\"true\",\"IsRequired\":false}]},{\"Type\":\"Video\",\"Codec\":\"hevc\",\"Conditions\":[{\"Condition\":\"NotEquals\",\"Property\":\"IsAnamorphic\",\"Value\":\"true\",\"IsRequired\":false},{\"Condition\":\"EqualsAny\",\"Property\":\"VideoProfile\",\"Value\":\"main|main 10\",\"IsRequired\":false},{\"Condition\":\"EqualsAny\",\"Property\":\"VideoRangeType\",\"Value\":\"SDR|HDR10|HLG|DOVI|DOVIWithHDR10|DOVIWithHLG|DOVIWithSDR\",\"IsRequired\":false},{\"Condition\":\"LessThanEqual\",\"Property\":\"VideoLevel\",\"Value\":\"183\",\"IsRequired\":false},{\"Condition\":\"NotEquals\",\"Property\":\"IsInterlaced\",\"Value\":\"true\",\"IsRequired\":false},{\"Condition\":\"EqualsAny\",\"Property\":\"VideoCodecTag\",\"Value\":\"hvc1|dvh1\",\"IsRequired\":true},{\"Condition\":\"LessThanEqual\",\"Property\":\"VideoFramerate\",\"Value\":\"60\",\"IsRequired\":true}]},{\"Type\":\"Video\",\"Codec\":\"vp9\",\"Conditions\":[{\"Condition\":\"EqualsAny\",\"Property\":\"VideoRangeType\",\"Value\":\"SDR|HDR10|HLG\",\"IsRequired\":false}]},{\"Type\":\"Video\",\"Codec\":\"av1\",\"Conditions\":[{\"Condition\":\"NotEquals\",\"Property\":\"IsAnamorphic\",\"Value\":\"true\",\"IsRequired\":false},{\"Condition\":\"EqualsAny\",\"Property\":\"VideoProfile\",\"Value\":\"main\",\"IsRequired\":false},{\"Condition\":\"EqualsAny\",\"Property\":\"VideoRangeType\",\"Value\":\"SDR|HDR10|HLG\",\"IsRequired\":false},{\"Condition\":\"LessThanEqual\",\"Property\":\"VideoLevel\",\"Value\":\"15\",\"IsRequired\":false}]}],\"SubtitleProfiles\":[{\"Format\":\"vtt\",\"Method\":\"External\"},{\"Format\":\"ass\",\"Method\":\"External\"},{\"Format\":\"ssa\",\"Method\":\"External\"}],\"ResponseProfiles\":[{\"Type\":\"Video\",\"Container\":\"m4v\",\"MimeType\":\"video/mp4\"}]}}";

    private static final String authHeaderValue = String.format("MediaBrowser Client=\"%s\", Device=\"%s\", DeviceId=\"%s\", Version=\"%s\"", "iPlay", "Android", "9999999", "v1.0.0");


    SiteModel site;

    public static void login(String server, String username, String password, Consumer<Object> completion) {
        var model = HTTPModel.<EmbyModel.EmbyUserModel>builder()
                .url(server + (server.endsWith("/") ? "" : "/") + "Users/authenticatebyname")
                .method("POST")
                .headers(Map.of("x-emby-authorization", authHeaderValue, "Content-Type", "application/json"))
                .body(XGET(IJSONAdapter.class).toJSON(Map.of("Username", username, "Pw", password)))
                .typeReference(new TypeReference<EmbyModel.EmbyUserModel>() { })
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
                    var siteModel = SiteModel.builder()
                            .user(UserModel.builder()
                                    .siteId(response.getServerId())
                                    .username(response.getUser().getName())
                                    .password(password)
                                    .id(response.getUser().getId())
                                    .accessToken(response.getAccessToken())
                                    .build())
                            .endpoint(endpoint)
                            .build();
                    completion.accept(siteModel);
                    return;
                }
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            completion.accept(null);
        });
    }

    public void getAlbums(Consumer<List<AlbumModel>> completion) {
        if (site == null ||
            site.getEndpoint() == null ||
            site.getUser() == null) return;

        var model = HTTPModel.<EmbyModel.EmbyPageableModel<EmbyModel.EmbyAlbumModel>>builder()
                .url(site.getEndpoint().getBaseUrl() + "Users/" + site.getUserId() + "/Views")
                .method("GET")
                .headers(Map.of("x-emby-authorization", authHeaderValue, "X-Emby-Token", site.getAccessToken()))
                .typeReference(new TypeReference<EmbyModel.EmbyPageableModel<EmbyModel.EmbyAlbumModel>>() { })
                .build();

        HTTPUtil.request(model, response -> {
            if (Objects.isNull(response)) {
                completion.accept(null);
                return;
            }
            if (response != null) {
                String baseUrl = site.getEndpoint().getBaseUrl();
                response.getItems().forEach(item -> item.buildImage(baseUrl, ImageType.Jellyfin));
                val albumItems = response.getItems().stream().map(item -> AlbumModel.builder()
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
                .url(site.getEndpoint().getBaseUrl() + "Users/" + site.getUserId() + "/Items/Latest")
                .method("GET")
                .headers(Map.of("x-emby-authorization", authHeaderValue, "X-Emby-Token", site.getAccessToken()))
                .query(Map.of(
                        "X-Emby-Token", site.getAccessToken(),
                        "Limit", "16",
                        "ParentId", id,
                        "Recursive", "true",
                        "Fields", "BasicSyncInfo,People,Genres,SortName,Overview,CanDelete,Container,PrimaryImageAspectRatio,Prefix,DateCreated,ProductionYear,Status,EndDate"
                ))
                .typeReference(new TypeReference<List<EmbyModel.EmbyMediaModel>>() {
                })
                .build();

        HTTPUtil.request(model, response -> {
            if (Objects.isNull(response)) {
                completion.accept(null);
                return;
            }
            if (response != null) {
                String baseUrl = site.getEndpoint().getBaseUrl();
                response.forEach(item -> item.buildImage(baseUrl, ImageType.Jellyfin));
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
                .url(site.getEndpoint().getBaseUrl() + "Users/" + site.getUserId() + "/Items")
                .headers(Map.of("x-emby-authorization", authHeaderValue, "X-Emby-Token", site.getAccessToken()))
                .method("GET")
                .query(params)
                .typeReference(new TypeReference<EmbyModel.EmbyPageableModel<EmbyModel.EmbyMediaModel>>() {
                })
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
        var model = HTTPModel.<EmbyModel.EmbyPageableModel<EmbyModel.EmbyMediaModel>>builder()
                .url(site.getEndpoint().getBaseUrl() + "Users/" + site.getUserId() + "/Items")
                .headers(Map.of("x-emby-authorization", authHeaderValue, "X-Emby-Token", site.getAccessToken()))
                .method("GET")
                .query(params)
                .typeReference(new TypeReference<EmbyModel.EmbyPageableModel<EmbyModel.EmbyMediaModel>>() {
                })
                .build();

        HTTPUtil.request(model, response -> {
            if (Objects.isNull(response)) {
                completion.accept(null);
                return;
            }
            if (response != null) {
                String baseUrl = site.getEndpoint().getBaseUrl();
                List<EmbyModel.EmbyMediaModel> items = response.getItems();
                items.forEach(item -> item.buildImage(baseUrl, ImageType.Jellyfin));
                val mediaItems = items.stream().map(EmbyModel.EmbyMediaModel::toMediaModel).collect(Collectors.toList());
                completion.accept(mediaItems);
                return;
            }
            completion.accept(null);
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
                .url(site.getEndpoint().getBaseUrl() + "Users/" + site.getUserId() + "/Items")
                .headers(Map.of("x-emby-authorization", authHeaderValue, "X-Emby-Token", site.getAccessToken()))
                .method("GET")
                .query(params)
                .typeReference(new TypeReference<EmbyModel.EmbyPageableModel<EmbyModel.EmbyMediaModel>>() {
                })
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
                items.addAll(response.getItems());
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

    public void getSeasons(String seriesId, Consumer<Object> completion) {
        if (site == null ||
                site.getEndpoint() == null ||
                site.getUser() == null) return;

        var model = HTTPModel.<EmbyModel.EmbyPageableModel<EmbyModel.EmbyMediaModel>>builder()
                .url(site.getEndpoint().getBaseUrl() + "Shows/" + seriesId + "/Seasons")
                .headers(Map.of("x-emby-authorization", authHeaderValue, "X-Emby-Token", site.getAccessToken()))
                .method("GET")
                .query(Map.of(
                        "X-Emby-Token", site.getAccessToken(),
                        "UserId", site.getUserId(),
                        "Fields", "BasicSyncInfo,People,Genres,SortName,Overview,CanDelete,Container,PrimaryImageAspectRatio,Prefix,DateCreated,ProductionYear,Status,EndDate"
                ))
                .typeReference(new TypeReference<EmbyModel.EmbyPageableModel<EmbyModel.EmbyMediaModel>>() {
                })
                .build();

        HTTPUtil.request(model, response -> {
            if (Objects.isNull(response)) {
                completion.accept(null);
                return;
            }
            if (response != null) {
                String baseUrl = site.getEndpoint().getBaseUrl();
                List<EmbyModel.EmbyMediaModel> items = response.getItems();
                items.forEach(item -> item.buildImage(baseUrl, ImageType.Jellyfin));
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
                .url(site.getEndpoint().getBaseUrl() + "Shows/" + seriesId + "/Episodes")
                .headers(Map.of("x-emby-authorization", authHeaderValue, "X-Emby-Token", site.getAccessToken()))
                .method("GET")
                .query(Map.of(
                        "X-Emby-Token", site.getAccessToken(),
                        "UserId", site.getUserId(),
                        "SeasonId", seasonId,
                        "Fields", "BasicSyncInfo,People,Genres,SortName,Overview,CanDelete,Container,PrimaryImageAspectRatio,Prefix,DateCreated,ProductionYear,Status,EndDate"
                ))
                .typeReference(new TypeReference<EmbyModel.EmbyPageableModel<EmbyModel.EmbyMediaModel>>() {
                })
                .build();

        HTTPUtil.request(model, response -> {
            if (Objects.isNull(response)) {
                completion.accept(null);
                return;
            }
            if (response != null) {
                String baseUrl = site.getEndpoint().getBaseUrl();
                List<EmbyModel.EmbyMediaModel> items = response.getItems();
                items.forEach(item -> item.buildImage(baseUrl, ImageType.Jellyfin));
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
        val startTimeTicks = media != null ? media.getUserData().getPlaybackPositionTicks() : 0;
        var model = HTTPModel.<EmbyModel.EmbyPlaybackModel>builder()
                .url(site.getEndpoint().getBaseUrl() + "Items/" + id + "/PlaybackInfo")
                .headers(Map.of("Content-Type", "application/json", "x-emby-authorization", authHeaderValue, "X-Emby-Token", site.getAccessToken()))
                .method("POST")
                .query(Map.of(
                    "StartTimeTicks", String.valueOf(startTimeTicks),
                    "IsPlayback", "false",
                    "AutoOpenLiveStream", "false",
                    "MaxStreamingBitrate", "140000000",
                    "UserId", site.getUserId(),
                    "X-Emby-Token", site.getAccessToken()
                ))
                .body(kDeviceProfile)
                .typeReference(new TypeReference<EmbyModel.EmbyPlaybackModel>() {
                })
                .build();

        HTTPUtil.request(model, response -> {
            if (Objects.isNull(response)) {
                completion.accept(null);
                return;
            }
            if (response != null) {
                String baseUrl = site.getEndpoint().getBaseUrl();
                response.buildUrl(baseUrl);
                response.getMediaSources().forEach(stream -> {
                    stream.buildDirectStreamUrl(baseUrl, Map.of(
                        "api_key", site.getAccessToken(),
                        "DeviceId", "9999999",
                        "MediaSourceId", stream.getId()
                    ));
                });
                completion.accept(response);
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
                .url(site.getEndpoint().getBaseUrl() + "Users/" + site.getUserId() + "/Items/Resume")
                .headers(Map.of("x-emby-authorization", authHeaderValue, "X-Emby-Token", site.getAccessToken()))
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
                .typeReference(new TypeReference<EmbyModel.EmbyPageableModel<EmbyModel.EmbyMediaModel>>() {
                })
                .build();

        HTTPUtil.request(model, response -> {
            if (Objects.isNull(response)) {
                completion.accept(null);
                return;
            }
            if (response != null) {
                String baseUrl = site.getEndpoint().getBaseUrl();
                List<EmbyModel.EmbyMediaModel> items = response.getItems();
                items.forEach(item -> item.buildImage(baseUrl, ImageType.Jellyfin));
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
                .url(site.getEndpoint().getBaseUrl() + "Users/" + site.getUserId() + "/FavoriteItems/" + id + (isFavorite ? "/" : "/Delete"))
                .headers(Map.of("x-emby-authorization", authHeaderValue, "X-Emby-Token", site.getAccessToken()))
                .method("POST")
                .body("")
                .query(Map.of(
                        "X-Emby-Token", site.getAccessToken()
                ))
                .typeReference(new TypeReference<EmbyModel.EmbyUserData>() {
                })
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
                .url(site.getEndpoint().getBaseUrl() + "Items")
                .method("GET")
                .query(Map.of(
                        "X-Emby-Token", site.getAccessToken(),
                        "SortBy", "IsFavoriteOrLiked,Random",
                        "IncludeItemTypes", "Movie,Series,MusicArtist",
                        "Limit", "20",
                        "Recursive", "true",
                        "ImageTypeLimit", "0",
                        "UserId", site.getUserId()
                ))
                .typeReference(new TypeReference<EmbyModel.EmbyPageableModel<EmbyModel.EmbyMediaModel>>() {
                })
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
                .url(site.getEndpoint().getBaseUrl() + "Sessions/Playing/" + playState)
                .headers(Map.of("x-emby-authorization", authHeaderValue, "X-Emby-Token", site.getAccessToken()))
                .method("POST")
                .body(XGET(IJSONAdapter.class).toJSON(data))
                .headers(Map.of(
                        "X-Emby-Token", site.getAccessToken(),
                        "Content-Type", "application/json",
                        "reqformat", "json"
                ))
                .typeReference(new TypeReference<EmbyModel.EmbyUserData>() {
                })
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
    public void getSimilar(String id, Consumer<Object> completion) {
        // /Items/{Id}/Similar
        if (site == null ||
                site.getEndpoint() == null ||
                site.getUser() == null) return;

        var model = HTTPModel.<EmbyModel.EmbyPageableModel<EmbyModel.EmbyMediaModel>>builder()
                .url(site.getEndpoint().getBaseUrl() + "Items/" + id + "/Similar")
                .method("GET")
                .headers(Map.of("Authorization", authHeaderValue, "X-Emby-Token", site.getAccessToken()))
                .query(Map.of(
                        "Fields", "BasicSyncInfo,People,Genres,SortName,Overview,CanDelete,Container,PrimaryImageAspectRatio,Prefix,DateCreated,ProductionYear,Status,EndDate",
                        "Limit", "16",
                        "UserId", site.getUserId()
                ))
                .typeReference(new TypeReference<EmbyModel.EmbyPageableModel<EmbyModel.EmbyMediaModel>>() {
                })
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

    @Override
    public void getSiteInfo(Consumer<Object> completion) {
        if (site == null ||
                site.getEndpoint() == null ||
                site.getUser() == null) return;

        // /emby/system/info/public
        val model = HTTPModel.<EmbyModel.EmbySiteInfo>builder()
                .url(site.getEndpoint().getBaseUrl() + "system/info/public")
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
