package top.ourfor.app.iplay.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.With;
import top.ourfor.app.iplay.common.model.IMediaModel;
import top.ourfor.app.iplay.common.type.MediaLayoutType;

@Data
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MediaModel implements IMediaModel {
    String id;
    String title;
    String type;
    String overview;
    String duration;

    String seriesId;
    String seriesName;

    String seasonId;
    String seasonName;

    Integer indexNumber;

    String dateCreated;
    String airDate;
    String productionYear;

    List<String> genres;

    List<ActorModel> actors;

    ImageModel image;
    MediaUserData userData;

    MediaLayoutType layoutType;

    @Override
    public String getName() {
        return title;
    }

    public boolean isAlbum() {
        return (
            type.equals("BoxSet") ||
            type.equals("Album") ||
            type.equals("Playlist")
        );
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
}
