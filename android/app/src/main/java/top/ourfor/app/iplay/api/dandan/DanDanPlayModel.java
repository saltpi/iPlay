package top.ourfor.app.iplay.api.dandan;

import java.util.List;

import lombok.Data;
import lombok.val;

public class DanDanPlayModel {

    @Data
    public static class Episode {
        Integer episodeId;
        String episodeTitle;
    }

    @Data
    public static class Anime {
        Integer animeId;
        String animeTitle;
        String type;
        String typeDescription;
        List<Episode> episodes;
    }

    @Data
    public static class AnimeSearchResult {
        Boolean hasMore;
        List<Anime> animes;
    }

    public enum CommentType {
        Normal,
        Top,
        Bottom
    }

    @Data
    public static class CommentAttribute {
        Float time;
        CommentType type;
        Integer color;
        String sender;
        String content;
    }

    @Data
    public static class Comment {
        String cid;
        String p;
        String m;

        CommentAttribute attributes;

        public CommentAttribute getAttributes() {
            if (attributes == null) {
                attributes = new CommentAttribute();
                attributes.content = m;
                val comps = p.split(",");
                if (comps.length > 0) {
                    attributes.time = Float.parseFloat(comps[0]);
                }
                if (comps.length > 1) {
                    val comp = Integer.valueOf(comps[1]);
                    if (comp == 1) {
                        attributes.type = CommentType.Normal;
                    } else if (comp == 5) {
                        attributes.type = CommentType.Top;
                    } else {
                        attributes.type = CommentType.Bottom;
                    }
                }
                if (comps.length > 2) {
                    attributes.color = Integer.parseInt(comps[2]);
                }
                if (comps.length > 3) {
                    attributes.sender = comps[3];
                }
            }
            return attributes;
        }
    }

    @Data
    public static class CommentsResult {
        List<Comment> comments;
        Integer count;
    }
}
