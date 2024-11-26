package top.ourfor.app.iplayx.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.ourfor.app.iplayx.common.model.MediaModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmbyAlbumModel implements MediaModel {
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
