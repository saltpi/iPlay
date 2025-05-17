package top.ourfor.app.iplay.api.dandan;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import top.ourfor.app.iplay.util.HTTPModel;
import top.ourfor.app.iplay.util.HTTPUtil;

public class DanDanPlayApi {
    public static void search(String keyword, Consumer<DanDanPlayModel.AnimeSearchResult> completion) {
        var model = HTTPModel.<DanDanPlayModel.AnimeSearchResult>builder()
                .url("https://api.dandanplay.net/api/v2/search/episodes")
                .method("GET")
                .query(Map.of(
                        "anime", keyword
                ))
                .headers(Map.of("Content-Type", "application/json"))
                .typeReference(new TypeReference<DanDanPlayModel.AnimeSearchResult>() { })
                .build();

        HTTPUtil.request(model, response -> {
            if (Objects.isNull(response)) {
                completion.accept(null);
                return;
            }
            if (response != null) {
                completion.accept(response);
                return;
            }
            completion.accept(null);
        });
    }

    public static void comments(Integer episodeId, Consumer<DanDanPlayModel.CommentsResult> completion) {
        var model = HTTPModel.<DanDanPlayModel.CommentsResult>builder()
                .url("https://api.dandanplay.net/api/v2/comment/" + episodeId)
                .method("GET")
                .headers(Map.of("Content-Type", "application/json"))
                .typeReference(new TypeReference<DanDanPlayModel.CommentsResult>() {})
                .build();

        HTTPUtil.request(model, response -> {
            if (Objects.isNull(response)) {
                completion.accept(null);
                return;
            }
            if (response != null) {
                completion.accept(response);
                return;
            }
            completion.accept(null);
        });
    }
}
