package top.ourfor.app.iplay.api.iplay;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.With;
import lombok.val;
import top.ourfor.app.iplay.model.ImageModel;
import top.ourfor.app.iplay.model.MediaUserData;

public class iPlayModel {

    @Data
    @With
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Image {
        public String backdrop;
        public String primary;
        public String logo;
    }

    @Data
    @With
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserDataModel {
        public boolean favorite;
        public Long lastPlayTime;
    }

    @Data
    @With
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response<T> {
        public int code;
        public String message;
        public T data;
    }

    @Data
    @With
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlbumModel {
        public String id;
        public String siteId;
        public String parentId;
        public String name;
        public Image image;
    }

    @Data
    @With
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static public class ActorModel {
        String id;
        String name;
        String description;
        String sex;
        String avatar;
    }

    @Data
    @With
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    static public class TagModel {
        String id;
        String name;
        String url;
    }

    @Data
    @With
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PublicInfoModel {
        String id;
        String name;
        String version;
    }

    @Data
    @With
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MediaModel {
        String id;
        String siteId;
        String parentId;
        String title;
        String description;
        String duration;
        List<TagModel> tags;
        Image image;

        List<ActorModel> actors;

        UserDataModel userData;


        public top.ourfor.app.iplay.model.MediaModel toMediaModel() {
            val builder = top.ourfor.app.iplay.model.MediaModel.builder();
            if (actors != null) {
                builder.actors(actors.stream().map(actor -> top.ourfor.app.iplay.model.ActorModel.builder()
                        .id(actor.id)
                        .image(ImageModel.builder()
                                .primary(actor.avatar)
                                .build())
                        .name(actor.name)
                        .description(actor.description)
                        .build()).collect(Collectors.toList()));
            }

            if (tags != null) {
                builder.genres(tags.stream().map(tag -> tag.name).collect(Collectors.toList()));
            }

            return builder
                    .title(title)
                    .overview(description)
                    .seriesId(parentId)
                    .id(id)
                    .type("Movie")
                    .duration(duration)
                    .userData(MediaUserData.builder()
                            .isFavorite(userData != null ? userData.favorite : false)
                            .playbackPositionTicks(userData != null ? userData.lastPlayTime : 0)
                            .build())
                    .image(ImageModel.builder()
                            .thumb(image.backdrop)
                            .primary(image.primary)
                            .backdrop(image.backdrop)
                            .logo(image.logo)
                            .build())
                    .build();
        }
    }

    @Data
    @With
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SourceModel {
        public String name;
        public String type;
        public String url;
    }
}
