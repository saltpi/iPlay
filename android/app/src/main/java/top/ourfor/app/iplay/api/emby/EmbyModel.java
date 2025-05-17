package top.ourfor.app.iplay.api.emby;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;
import lombok.experimental.Accessors;
import top.ourfor.app.iplay.common.model.IMediaModel;
import top.ourfor.app.iplay.common.model.SeekableRange;
import top.ourfor.app.iplay.common.type.MediaLayoutType;
import top.ourfor.app.iplay.model.ActorModel;
import top.ourfor.app.iplay.model.AlbumModel;
import top.ourfor.app.iplay.model.ImageModel;
import top.ourfor.app.iplay.model.ImageType;
import top.ourfor.app.iplay.model.MediaUserData;

public class EmbyModel {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @With
    @EqualsAndHashCode
    public static class EmbyActorModel {
        @JsonProperty("Id")
        String id;
        @JsonProperty("Name")
        String name;
        @JsonProperty("Role")
        String role;
        @JsonProperty("Type")
        String type;
        @JsonProperty("PrimaryImageTag")
        String primaryImageTag;

        @JsonProperty("Image")
        ImageModel image;

        public void buildImage(String url, ImageType type) {
            if (!url.endsWith("/")) url = url + "/";
            var prefix = type == ImageType.Emby ? "emby/" : "";
            image = ImageModel.builder()
                    .primary(url + prefix + "Items/" + id + "/Images/Primary")
                    .build();
        }

        public void buildImage(String url) {
            buildImage(url, ImageType.Emby);
        }

        public ActorModel toActorModel() {
            return ActorModel.builder()
                    .id(id)
                    .name(name)
                    .role(role)
                    .image(image)
                    .build();
        }
    }

    @Data
    @With
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class EmbyAlbumMediaModel<T extends IMediaModel> {
        @JsonProperty("title")
        String title;
        @JsonProperty("layout")
        MediaLayoutType layout;
        @JsonProperty("album")
        AlbumModel album;
        @JsonProperty("medias")
        List<T> medias;
    }

    @Data
    @With
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmbyAlbumModel implements IMediaModel {
        @JsonProperty("Id")
        String id;
        @JsonProperty("Primary")
        String primary;
        @JsonProperty("Name")
        String name;

        @JsonProperty("Image")
        ImageModel image;

        @JsonProperty("CollectionType")
        String collectionType;

        @JsonIgnore
        public boolean isMusic() {
            return "music".equals(collectionType);
        }

        @JsonIgnore
        public boolean isMovie() {
            return "movies".equals(collectionType);
        }

        @JsonIgnore
        public boolean isSeries() {
            return "tvshows".equals(collectionType);
        }

        @JsonIgnore
        public void buildImage(String url, ImageType type) {
            if (!url.endsWith("/")) url = url + "/";
            var prefix = type == ImageType.Emby ? "emby/" : "";
            image = ImageModel.builder()
                    .primary(url + prefix + "Items/" + id + "/Images/Primary")
                    .logo(url + prefix + "Items/" + id + "/Images/Logo")
                    .backdrop(url + prefix + "Items/" + id + "/Images/Backdrop")
                    .build();
        }

        @JsonIgnore
        public void buildImage(String url) {
            buildImage(url, ImageType.Emby);
        }
    }

    @Data
    @With
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmbyImageTag {
        @JsonProperty("Primary")
        private String primary;
        @JsonProperty("Thumb")
        private String thumb;
    }

    @Data
    @With
    @Builder
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmbyMediaModel implements IMediaModel {
        @JsonProperty("Id")
        String id;
        @JsonProperty("Name")
        String name;
        @JsonProperty("Overview")
        String overview;
        @JsonProperty("ProductionYear")
        String productionYear;
        @JsonProperty("EndDate")
        String endDate;
        @JsonProperty("Type")
        String type;
        @JsonProperty("SeriesId")
        String seriesId;
        @JsonProperty("SeriesName")
        String seriesName;
        @JsonProperty("SeasonId")
        String seasonId;
        @JsonProperty("SeasonName")
        String seasonName;
        @JsonProperty("IndexNumber")
        Integer indexNumber;
        @JsonProperty("ParentIndexNumber")
        Integer parentIndexNumber;
        @JsonProperty("DateCreated")
        String dateCreated;
        @JsonProperty("Genres")
        List<String> genres;
        @JsonProperty("People")
        List<EmbyActorModel> actors;
        @JsonProperty("UserData")
        EmbyUserData userData;
        @JsonProperty("ImageTags")
        EmbyImageTag imageTags;
        @JsonProperty("Image")
        ImageModel image;
        @JsonProperty("LayoutType")
        MediaLayoutType layoutType;

        @JsonProperty("PrimaryImageAspectRatio")
        Float primaryImageAspectRatio;

        @JsonIgnore
        public void buildImage(String url, ImageType type) {
            if (!url.endsWith("/")) url = url + "/";
            var prefix = type == ImageType.Emby ? "emby/" : "";
            image = ImageModel.builder()
                    .primary(url + prefix + "Items/" + id + "/Images/Primary?maxHeight=720&quality=90&tag=" + imageTags.getPrimary())
                    .logo(url + prefix + "Items/" + id + "/Images/Logo")
                    .backdrop(url + prefix + "Items/" + id + "/Images/Backdrop")
                    .thumb(url + prefix + "Items/" + id + "/Images/Thumb?fillHeight=288&fillWidth=512&quality=96&tag=" + imageTags.getThumb())
                    .fallback(type.equals("Episode") ? List.of(url + "emby/Items/" + seriesId + "/Images/Backdrop") : List.of(""))
                    .build();
            if (actors == null) return;
            for (EmbyActorModel actor : actors) {
                actor.buildImage(url, type);
            }
        }

        @JsonIgnore
        public void buildImage(String url) {
            buildImage(url, ImageType.Emby);
        }

        public boolean isSeries() {
            return type.equals("Series");
        }

        public boolean isSeason() {
            return type.equals("Season");
        }

        public boolean isEpisode() {
            return type.equals("Episode");
        }

        public boolean isMovie() {
            return type.equals("Movie");
        }

        public boolean isMusicAlbum() {
            return type.equals("MusicAlbum");
        }

        public boolean isAudio() {
            return type.equals("Audio");
        }

        public String episodeName() {
            if (parentIndexNumber == null || indexNumber == null) return name;
            return "S" + parentIndexNumber + ":E" + indexNumber + " - " + name;
        }

        public String episodeShortName() {
            if (parentIndexNumber == null || indexNumber == null) return name;
            return "S" + parentIndexNumber + ":E" + indexNumber;
        }

        @JsonIgnore
        public String getDateTime() {
            if (productionYear == null) return null;
            if (endDate == null) return productionYear;
            return productionYear + " - " + (endDate != null && endDate.length() >= 4 ? endDate.substring(0, 4) : "Now");
        }

        public top.ourfor.app.iplay.model.MediaModel toMediaModel() {
            return top.ourfor.app.iplay.model.MediaModel.builder()
                    .id(id)
                    .title(name)
                    .image(image)
                    .type(type)
                    .dateCreated(dateCreated)
                    .productionYear(productionYear)
                    .indexNumber(indexNumber)
                    .airDate(getDateTime())
                    .layoutType(layoutType)
                    .overview(overview)
                    .seasonId(seasonId)
                    .seasonName(seasonName)
                    .seriesId(seriesId)
                    .seriesName(seriesName)
                    .genres(genres)
                    .actors(actors != null ? actors.stream().map(EmbyActorModel::toActorModel).collect(Collectors.toList()) : List.of())
                    .userData(userData.toUserDataModel())
                    .build();
        }
    }

    @Data
    @With
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class EmbyMediaSource {
        @JsonProperty("Id")
        String id;
        @JsonProperty("Container")
        String container;
        @JsonProperty("Name")
        String name;
        @JsonProperty("Path")
        String path;
        @JsonProperty("DirectStreamUrl")
        String directStreamUrl;
        @JsonProperty("TranscodingUrl")
        String transcodingUrl;
        @JsonProperty("MediaStreams")
        List<EmbyMediaStream> mediaStreams;

        public void buildUrl(String url) {
            if (url.endsWith("/")) url = url.substring(0, url.length() - 1);
            if (directStreamUrl != null && !directStreamUrl.startsWith("http")) {
                directStreamUrl = url + directStreamUrl;
            }
            for (EmbyMediaStream mediaStream : mediaStreams) {
                mediaStream.buildUrl(url);
            }
        }

        public void buildDirectStreamUrl(String url, Map<String, String> params) {
            if (url.endsWith("/")) url = url.substring(0, url.length() - 1);
            if (directStreamUrl == null) {
                StringBuilder builder = new StringBuilder(url);
                builder.append("/Videos/").append(id).append("/Stream?static=true");
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    builder.append("&").append(entry.getKey()).append("=").append(entry.getValue());
                }
                directStreamUrl = builder.toString();
            }
        }
    }

    @Data
    @With
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class EmbyMediaStream {
        @JsonProperty("Codec")
        String codec;
        @JsonProperty("Language")
        String language;
        @JsonProperty("DisplayTitle")
        String displayTitle;
        @JsonProperty("DisplayLanguage")
        String displayLanguage;
        @JsonProperty("IsInterlaced")
        Boolean isInterlaced;
        @JsonProperty("IsDefault")
        Boolean isDefault;
        @JsonProperty("IsForced")
        Boolean isForced;
        @JsonProperty("IsHearingImpaired")
        Boolean isHearingImpaired;
        @JsonProperty("Type")
        String type;
        @JsonProperty("DeliveryMethod")
        String deliveryMethod;
        @JsonProperty("DeliveryUrl")
        String deliveryUrl;
        @JsonProperty("IsExternal")
        Boolean isExternal;
        @JsonProperty("Path")
        String path;

        public void buildUrl(String url) {
            if (deliveryUrl != null && !deliveryUrl.startsWith("http")) {
                deliveryUrl = url + deliveryUrl;
            }
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class EmbyPageableModel<T> {
        @JsonProperty("Items")
        List<T> items;
        @JsonProperty("TotalRecordCount")
        int totalRecordCount;
    }

    @Data
    @With
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class EmbyPlaybackData {
        @JsonIgnore
        public static long kIPLXSecond2TickScale = 10000000;

        @JsonProperty("AudioStreamIndex")
        Long audioStreamIndex;
        @JsonProperty("BufferedRanges")
        List<List<Long>> bufferedRanges;
        @JsonProperty("CanSeek")
        Boolean canSeek;
        @JsonProperty("EventName")
        String eventName;
        @JsonProperty("NowPlayingQueue")
        List<EmbyPlayingQueue> nowPlayingQueue;
        @JsonProperty("IsMuted")
        Boolean isMuted;
        @JsonProperty("IsPaused")
        Boolean isPaused;
        @JsonProperty("ItemId")
        String itemId;
        @JsonProperty("MaxStreamingBitrate")
        Long maxStreamingBitrate;
        @JsonProperty("MediaSourceId")
        String mediaSourceId;
        @JsonProperty("PlaySessionId")
        String playSessionId;
        @JsonProperty("PlayMethod")
        String playMethod;
        @JsonProperty("PlaybackRate")
        Double playbackRate;
        @JsonProperty("PlaybackStartTimeTicks")
        Long playbackStartTimeTicks;
        @JsonProperty("PlaylistIndex")
        Long playlistIndex;
        @JsonProperty("PlaylistLength")
        Long playlistLength;
        @JsonProperty("PositionTicks")
        Long positionTicks;
        @JsonProperty("RepeatMode")
        String repeatMode;
        @JsonProperty("SeekableRanges")
        List<SeekableRange> seekableRanges;
        @JsonProperty("SubtitleOffset")
        Double subtitleOffset;
        @JsonProperty("SubtitleStreamIndex")
        Long subtitleStreamIndex;
        @JsonProperty("VolumeLevel")
        Double volumeLevel;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class EmbyPlaybackDataModel {
        @JsonProperty("MediaSourceId")
        String mediaSourceId;
        @JsonProperty("PlaySessionId")
        String playSessionId;
        @JsonProperty("PlayMethod")
        String playMethod;
        @JsonProperty("ItemId")
        String itemId;
    }

    @Data
    @With
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class EmbyPlaybackModel {
        @JsonProperty("PlaySessionId")
        String sessionId;
        @JsonProperty("MediaSources")
        List<EmbyMediaSource> mediaSources;

        public void buildUrl(String baseUrl) {
            if (baseUrl.endsWith("/")) baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            for (EmbyMediaSource mediaSource : mediaSources) {
                mediaSource.buildUrl(baseUrl);
            }
        }
    }

    @Data
    @With
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class EmbyPlayingQueue {
        @JsonProperty("Id")
        String id;
        @JsonProperty("PlaylistItemId")
        String playlistItemId;
    }

    @Data
    @With
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class EmbySiteInfo {
        @JsonProperty("ServerName")
        String ServerName;
        @JsonProperty("Version")
        String Version;
        @JsonProperty("Id")
        String Id;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @With
    public static class EmbySiteUserModel {
        @JsonProperty("Name")
        String name;
        @JsonProperty("Id")
        String id;
    }

    @Data
    @With
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class EmbyUserData {
        @JsonProperty("UnplayedItemCount")
        Long unplayedItemCount;
        @JsonProperty("PlaybackPositionTicks")
        Long playbackPositionTicks;
        @JsonProperty("PlayCount")
        Long playCount;
        @JsonProperty("IsFavorite")
        Boolean isFavorite;
        @JsonProperty("Played")
        Boolean isPlayed;

        public MediaUserData toUserDataModel() {
            return MediaUserData.builder()
                    .unplayedItemCount(unplayedItemCount)
                    .playbackPositionTicks(playbackPositionTicks)
                    .isFavorite(isFavorite)
                    .build();
        }
    }

    @Data
    @EqualsAndHashCode
    @ToString
    @With
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmbyUserModel {
        @JsonProperty("AccessToken")
        String accessToken;
        @JsonProperty("ServerId")
        String serverId;
        @JsonProperty("User")
        EmbySiteUserModel user;
    }
}
