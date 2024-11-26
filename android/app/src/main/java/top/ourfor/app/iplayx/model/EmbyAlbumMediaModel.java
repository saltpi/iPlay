package top.ourfor.app.iplayx.model;

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
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class EmbyAlbumMediaModel<T extends MediaModel> {
    @JsonProperty("title")
    String title;
    @JsonProperty("layout")
    MediaLayoutType layout;
    @JsonProperty("album")
    EmbyAlbumModel album;
    @JsonProperty("medias")
    List<T> medias;
}
