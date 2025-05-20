package top.ourfor.app.iplay.api.iplay;

import static top.ourfor.app.iplay.module.Bean.XGET;

import com.fasterxml.jackson.core.type.TypeReference;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.With;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import top.ourfor.app.iplay.api.emby.EmbyModel;
import top.ourfor.app.iplay.bean.IJSONAdapter;
import top.ourfor.app.iplay.common.api.IDataSourceApi;
import top.ourfor.app.iplay.common.model.SiteEndpointModel;
import top.ourfor.app.iplay.common.type.MediaLayoutType;
import top.ourfor.app.iplay.common.type.MediaPlayState;
import top.ourfor.app.iplay.model.AlbumModel;
import top.ourfor.app.iplay.model.MediaModel;
import top.ourfor.app.iplay.model.SiteModel;
import top.ourfor.app.iplay.model.UserModel;
import top.ourfor.app.iplay.store.IAppStore;
import top.ourfor.app.iplay.util.Base64Util;
import top.ourfor.app.iplay.util.HTTPModel;
import top.ourfor.app.iplay.util.HTTPUtil;

@With
@Slf4j
@Setter
@Builder
@EqualsAndHashCode
public class iPlayApi implements IDataSourceApi {
    SiteModel site;

    public static void login(String server, String username, String password, Consumer<Object> completion) {
        val token = "Basic " + Base64Util.encode(username + ":" + password);
        log.info("Login to server: {}", server);
        var model = HTTPModel.<iPlayModel.Response<iPlayModel.PublicInfoModel>>builder()
                .url(server + "/public")
                .method("GET")
                .headers(Map.of(
                        "Content-Type", "application/json",
                        "Authorization", token
                ))
                .typeReference(new TypeReference<iPlayModel.Response<iPlayModel.PublicInfoModel>>() {
                })
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
                val info = (iPlayModel.Response<iPlayModel.PublicInfoModel>)response;
                var siteModel = SiteModel.builder()
                        .user(UserModel.builder()
                                .id("")
                                .siteId(info.data.id)
                                .username(username)
                                .password(password)
                                .accessToken(token)
                                .build())
                        .endpoint(endpoint)
                        .build();
                completion.accept(siteModel);
            } catch (MalformedURLException | NullPointerException e) {
                log.error("Failed to parse server url: {}", server);
                log.error("Error: ", e);
            }
            completion.accept(null);
        });
    }

    @SuppressWarnings("unchecked")
    public void getAlbums(Consumer<List<AlbumModel>> completion) {
        if (site == null ||
            site.getEndpoint() == null ||
            site.getAccessToken() == null ||
            site.getUser() == null) return;

        val model = HTTPModel.<iPlayModel.Response<List<iPlayModel.AlbumModel>>>builder()
                .url(site.getEndpoint().getBaseUrl() + "media/albums")
                .method("GET")
                .headers(Map.of("Authorization", site.getAccessToken()))
                .typeReference(new TypeReference<iPlayModel.Response<List<iPlayModel.AlbumModel>>>() {
                })
                .build();

        HTTPUtil.request(model, (response) -> {
            if (Objects.isNull(response)) {
                completion.accept(null);
                return;
            }
            if (response.code == 200) {
                log.info("Albums: {}", response.data);
                val items = (List<iPlayModel.AlbumModel>) response.data;
                val albumItems = items.stream().map(item -> AlbumModel.builder()
                        .id(item.getId())
                        .title(item.getName())
                        .type("album")
                        .backdrop(item.getImage().getBackdrop())
                        .build()).collect(Collectors.toList());
                completion.accept(albumItems);
                return;
            }
            completion.accept(null);
        });
    }

    @SuppressWarnings("unchecked")
    public void getAlbumLatestMedias(String id, Consumer<List<MediaModel>> completion) {
        if (site == null ||
            site.getEndpoint() == null ||
            site.getUser() == null) return;

        val model = HTTPModel.<iPlayModel.Response<List<iPlayModel.MediaModel>>>builder()
                .url(site.getEndpoint().getBaseUrl() + "media/album/" + id)
                .method("GET")
                .headers(Map.of("Authorization", site.getAccessToken()))
                .typeReference(new TypeReference<iPlayModel.Response<List<iPlayModel.MediaModel>>>() {
                })
                .build();

        HTTPUtil.request(model, response -> {
            if (Objects.isNull(response)) {
                completion.accept(null);
                return;
            }
            if (response.code == 200) {
                val items = (List<iPlayModel.MediaModel>) response.data;
                val finalItems = items.stream().map(iPlayModel.MediaModel::toMediaModel).collect(Collectors.toList());
                finalItems.forEach(item -> item.setLayoutType(MediaLayoutType.Backdrop));
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
        var id = query.getOrDefault("ParentId", "0");
        val model = HTTPModel.<iPlayModel.Response<Integer>>builder()
                .url(site.getEndpoint().getBaseUrl() + "media/album/" + id + "/count")
                .method("GET")
                .headers(Map.of("Authorization", site.getAccessToken()))
                .typeReference(new TypeReference<iPlayModel.Response<Integer>>() {
                })
                .build();

        HTTPUtil.request(model, response -> {
            if (Objects.isNull(response)) {
                completion.accept(null);
                return;
            }
            if (response.code == 200) {
                val count = (Integer) response.data;
                completion.accept(count);
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
        completion.accept(null);
    }

    @SuppressWarnings("unchecked")
    public void getMedias(Map<String, String> query, Consumer<List<MediaModel>> completion) {
        if (site == null ||
                site.getEndpoint() == null ||
                site.getUser() == null) return;

        val params = new HashMap<String, String>();
        params.put("keyword", query.get("SearchTerm"));
        if (query.get("PersonIds") != null) {
            params.put("actorId", query.get("PersonIds"));
        }
        if (query.get("ParentId") != null) {
            params.put("albumId", query.get("ParentId"));
            val startIndex = query.getOrDefault("StartIndex", "0");
            params.put("page", String.valueOf(Integer.parseInt(startIndex) / 26));
        }

        if (query.get("Filters") != null && query.get("Filters").contains("IsFavorite")) {
            params.put("favorite", "true");
        }

        val model = HTTPModel.<iPlayModel.Response<List<iPlayModel.MediaModel>>>builder()
                .url(site.getEndpoint().getBaseUrl() + "media/search")
                .headers(Map.of(
                        "Authorization", site.getAccessToken()
                ))
                .method("GET")
                .query(params)
                .typeReference(new TypeReference<iPlayModel.Response<List<iPlayModel.MediaModel>>>() {
                })
                .build();

        HTTPUtil.request(model, response -> {
            if (Objects.isNull(response)) {
                completion.accept(null);
                return;
            }
            if (response.code == 200) {
                val data = response.data;
                val items = data.stream().map(iPlayModel.MediaModel::toMediaModel).collect(Collectors.toList());
                if (query.get("PersonIds") != null || query.get("ParentId") != null) {
                    items.forEach(item -> item.setLayoutType(MediaLayoutType.Backdrop));
                }
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
        completion.accept(null);
    }

    public void getEpisodes(String seriesId, String seasonId, Consumer<Object> completion) {
        if (site == null ||
                site.getEndpoint() == null ||
                site.getUser() == null) return;
        completion.accept(null);
    }

    @SuppressWarnings("unchecked")
    public void getPlayback(String id, Consumer<Object> completion) {
        if (site == null ||
            site.getEndpoint() == null ||
            site.getUser() == null) return;

        val store = XGET(IAppStore.class);
        val media = store.getDataSource().getMediaMap().get(id);
        val model = HTTPModel.<iPlayModel.Response<List<iPlayModel.SourceModel>>>builder()
                .url(site.getEndpoint().getBaseUrl() + "media/source")
                .method("GET")
                .query(Map.of(
                    "id", id
                ))
                .headers(Map.of(
                        "Authorization", site.getAccessToken()
                ))
                .typeReference(new TypeReference<iPlayModel.Response<List<iPlayModel.SourceModel>>>() {
                })
                .build();

        HTTPUtil.request(model, response -> {
            if (Objects.isNull(response)) {
                completion.accept(null);
                return;
            }
            if (response.code == 200) {
                String siteBaseUrl = site.getEndpoint().getBaseUrl();
                if (siteBaseUrl.endsWith("/")) siteBaseUrl = siteBaseUrl.substring(0, siteBaseUrl.length()-1);
                String baseUrl = siteBaseUrl;
                val sources = (List<iPlayModel.SourceModel>)response.data;
                val embySource = EmbyModel.EmbyPlaybackModel.builder()
                        .sessionId("0")
                        .mediaSources(sources.stream().map(source -> EmbyModel.EmbyMediaSource.builder()
                                .id(Objects.requireNonNullElse(source.getName(), "0"))
                                .name(Objects.requireNonNullElse(source.getName(), media.getName()))
                                .container("manifest")
                                .mediaStreams(List.of(
                                        EmbyModel.EmbyMediaStream.builder()
                                                .type("Video")
                                                .displayTitle(media.getName())
                                                .isExternal(true)
                                                .deliveryUrl(baseUrl + source.getUrl())
                                                .build()
                                ))
                                .path(baseUrl + source.getUrl())
                                .directStreamUrl(baseUrl + source.getUrl())
                                .build()).collect(Collectors.toList()))
                        .build();
                Collections.reverse(embySource.getMediaSources());
                completion.accept(embySource);
                return;
            }
            completion.accept(null);
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public void getSimilar(String id, Consumer<Object> completion) {
        // /Items/{Id}/Similar
        if (site == null ||
                site.getEndpoint() == null ||
                site.getUser() == null) return;

        val model = HTTPModel.<iPlayModel.Response<List<iPlayModel.MediaModel>>>builder()
                .url(site.getEndpoint().getBaseUrl() + "media/similar")
                .method("GET")
                .headers(Map.of(
                        "Authorization", site.getAccessToken()
                ))
                .query(Map.of(
                        "id", id
                ))
                .typeReference(new TypeReference<iPlayModel.Response<List<iPlayModel.MediaModel>>>() {
                })
                .build();

        HTTPUtil.request(model, response -> {
            if (Objects.isNull(response)) {
                completion.accept(null);
                return;
            }
            if (response.code == 200) {
                var items = (List<iPlayModel.MediaModel>)response.data;
                var mediaItems = items.stream().map(iPlayModel.MediaModel::toMediaModel).collect(Collectors.toList());
                mediaItems.forEach(item -> item.setLayoutType(MediaLayoutType.Backdrop));
                completion.accept(mediaItems);
                return;
            }
            completion.accept(null);
        });
    }

    @Override
    public void getDetail(String id, Consumer<MediaModel> completion) {
        // /Items/{Id}/Similar
        if (site == null ||
                site.getEndpoint() == null ||
                site.getUser() == null) return;

        val model = HTTPModel.<iPlayModel.Response<iPlayModel.MediaModel>>builder()
                .url(site.getEndpoint().getBaseUrl() + "media/detail")
                .method("GET")
                .headers(Map.of(
                        "Authorization", site.getAccessToken()
                ))
                .query(Map.of(
                        "id", id
                ))
                .typeReference(new TypeReference<iPlayModel.Response<iPlayModel.MediaModel>>() {
                })
                .build();

        HTTPUtil.request(model, response -> {
            if (Objects.isNull(response)) {
                completion.accept(null);
                return;
            }
            if (response.code == 200) {
                var item = (iPlayModel.MediaModel)response.data;
                completion.accept(item.toMediaModel());
                return;
            }
            completion.accept(null);
        });
    }

    public void getResume(Consumer<Object> completion) {
        completion.accept(null);
    }

    public void markFavorite(String id, boolean isFavorite, Consumer<Object> completion) {
        if (site == null ||
                site.getEndpoint() == null ||
                site.getUser() == null) return;
        val model = HTTPModel.<iPlayModel.Response<iPlayModel.MediaModel>>builder()
                .url(site.getEndpoint().getBaseUrl() + "media/detail")
                .method("POST")
                .headers(Map.of(
                        "Authorization", site.getAccessToken(),
                        "Content-Type", "application/json"
                ))
                .body(XGET(IJSONAdapter.class).toJSON(Map.of(
                        "mediaId", id,
                        "favorite", isFavorite
                )))
                .typeReference(new TypeReference<iPlayModel.Response<iPlayModel.MediaModel>>() {
                })
                .build();

        HTTPUtil.request(model, response -> {
            if (Objects.isNull(response)) {
                completion.accept(null);
                return;
            }
            if (response.code == 200) {
                var item = (iPlayModel.MediaModel)response.data;
                completion.accept(item.toMediaModel());
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

        completion.accept(List.of());
    }

    public void trackPlay(MediaPlayState state, EmbyModel.EmbyPlaybackData data, Consumer<Object> completion) {
        if (site == null ||
                site.getEndpoint() == null ||
                site.getUser() == null) return;
        completion.accept(null);
    }

    @Override
    public void getSiteInfo(Consumer<Object> completion) {
        if (site == null ||
                site.getEndpoint() == null ||
                site.getUser() == null) return;
    }

    @Override
    public int preferedPageSize() {
        return 26;
    }
}
