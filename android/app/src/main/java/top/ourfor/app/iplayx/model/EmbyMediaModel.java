package top.ourfor.app.iplayx.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.With;
import top.ourfor.app.iplayx.common.model.MediaModel;
import top.ourfor.app.iplayx.common.type.MediaLayoutType;

@Data
@With
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class EmbyMediaModel implements MediaModel {
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
}
