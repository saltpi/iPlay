package top.ourfor.app.iplayx.view.video;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.With;


@Data
@Builder
@With
public class PlayerSourceModel {
    public enum PlayerSourceType {
        None,
        Video,
        Audio,
        Playlist,
        Title,
        Subtitle,
        PosterImage,
        LogoImage,
        InfoText,
    }

    private String id;
    private PlayerSourceType type;
    private String name;
    private String url;
    private String value;

    private List<PlayerSourceModel> playlist;
}
