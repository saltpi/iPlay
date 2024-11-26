package top.ourfor.app.iplayx.api.dandan;

import static top.ourfor.app.iplayx.module.Bean.XGET;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import top.ourfor.app.iplayx.util.HTTPModel;
import top.ourfor.app.iplayx.util.HTTPUtil;

public class DanDanPlayApi {
    public static void search(String keyword, Consumer<DanDanPlayModel.AnimeSearchResult> completion) {
        HTTPModel model = HTTPModel.builder()
                .url("https://api.dandanplay.net/api/v2/search/episodes")
                .method("GET")
                .query(Map.of(
                        "anime", keyword
                ))
                .headers(Map.of("Content-Type", "application/json"))
                .typeReference(new TypeReference<DanDanPlayModel.AnimeSearchResult>() {})
                .build();

        HTTPUtil.request(model, response -> {
            if (Objects.isNull(response)) {
                completion.accept(null);
                return;
            }
            if (response instanceof DanDanPlayModel.AnimeSearchResult result) {
                completion.accept(result);
                return;
            }
            completion.accept(null);
        });
    }

    public static void comments(Integer episodeId, Consumer<DanDanPlayModel.CommentsResult> completion) {
        HTTPModel model = HTTPModel.builder()
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
            if (response instanceof DanDanPlayModel.CommentsResult result) {
                completion.accept(result);
                return;
            }
            completion.accept(null);
        });
    }
}
