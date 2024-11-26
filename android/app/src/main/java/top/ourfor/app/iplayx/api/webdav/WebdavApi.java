package top.ourfor.app.iplayx.api.webdav;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.With;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import okhttp3.Response;
import top.ourfor.app.iplayx.util.HTTPUtil;
import top.ourfor.app.iplayx.util.HTTPModel;
import top.ourfor.app.iplayx.model.WebDavSiteModel;

@With
@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebdavApi {
    WebDavSiteModel site;

    public void bindSite(WebDavSiteModel site) {
        this.site = site;
    }

    public void createDirectory(String path, Consumer<Object> completion) {
        var baseUrl = site.getServer();
        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }

        HTTPModel model = HTTPModel.builder()
                .url(baseUrl + path)
                .method("MKCOL")
                .headers(Map.of(
                        "Authorization", "Basic " + HTTPUtil.base64Encode(site.getUsername() + ":" + site.getPassword())
                ))
                .build();

        HTTPUtil.request(model, response -> {
            if (Objects.isNull(response)) {
                completion.accept(null);
                return;
            }

            if (response instanceof Response) {
                Response res = (Response) response;
                val code = res.code();
                if (code >= 200 && code < 300) {
                    completion.accept(true);
                    return;
                }
            }
            completion.accept(false);
            log.info("{}", response);
        });
    }

    public void writeToFile(String path, String content, Consumer<Object> completion) {
        var baseUrl = site.getServer();
        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }

        HTTPModel model = HTTPModel.builder()
                .url(baseUrl + path)
                .method("PUT")
                .body(content)
                .headers(Map.of(
                        "Authorization", "Basic " + HTTPUtil.base64Encode(site.getUsername() + ":" + site.getPassword()),
                        "Content-Type", "application/octet-stream"
                ))
                .build();

        HTTPUtil.request(model, response -> {
            if (Objects.isNull(response)) {
                completion.accept(null);
                return;
            }

            log.info("write file result {}", response);
            if (response instanceof Response) {
                Response res = (Response) response;
                val code = res.code();
                if (code >= 200 && code < 300) {
                    completion.accept(true);
                    return;
                }
            }
            completion.accept(false);
        });
    }

    public void readFromFile(String path, Consumer<Object> completion) {
        var baseUrl = site.getServer();
        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }

        HTTPModel model = HTTPModel.builder()
                .url(baseUrl + path)
                .method("GET")
                .headers(Map.of(
                        "Authorization", "Basic " + HTTPUtil.base64Encode(site.getUsername() + ":" + site.getPassword())
                ))
                .build();

        HTTPUtil.request(model, response -> {
            if (Objects.isNull(response)) {
                completion.accept(null);
                return;
            }

            if (response instanceof Response) {
                Response res = (Response) response;
                assert res.body() != null;
                String content = null;
                try {
                    content = res.body().string();
                    val code = res.code();
                    if (code >= 200 && code < 300) {
                        completion.accept(content);
                        return;
                    }
                } catch (IOException e) {
                    log.error("read from file {}: {}", path, e);
                }

            }
            completion.accept(false);
        });
    }
}
