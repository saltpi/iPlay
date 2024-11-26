package top.ourfor.app.iplayx.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@With
@EqualsAndHashCode
public class EmbyActorModel {
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
}
