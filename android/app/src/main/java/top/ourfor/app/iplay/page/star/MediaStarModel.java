package top.ourfor.app.iplay.page.star;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import top.ourfor.app.iplay.common.type.MediaType;
import top.ourfor.app.iplay.model.MediaModel;

@With
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaStarModel {
    String name;
    MediaType type;
    List<MediaModel> medias;
}
