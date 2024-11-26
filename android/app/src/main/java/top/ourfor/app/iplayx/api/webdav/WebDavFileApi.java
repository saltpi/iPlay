package top.ourfor.app.iplayx.api.webdav;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.With;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import okhttp3.Response;
import top.ourfor.app.iplayx.api.file.File;
import top.ourfor.app.iplayx.api.file.FileType;
import top.ourfor.app.iplayx.util.HTTPModel;
import top.ourfor.app.iplayx.util.HTTPUtil;
import top.ourfor.app.iplayx.util.PathUtil;

@Slf4j
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebDavFileApi {
    String serverUrl;
    String username;
    String password;

    public void listFiles(String path, Consumer<List<File>> completion) {
        val authentication = HTTPUtil.base64Encode(username + ":" + password);
        val headers = Map.of("Authorization", "Basic " + authentication, "Depth", "1");
        val url = PathUtil.of(serverUrl, path);
        val request = HTTPModel.builder()
                .url(url)
                .method("PROPFIND")
                .headers(headers)
                .modelClass(String.class)
                .build();
        HTTPUtil.request(request, response -> {
            log.debug("response: {}", response);
            if (response instanceof String xml) {
                val resources = WebDAVResponseParser.parseWebDAVResponse(xml);
                var first = true;
                val files = new ArrayList<File>();
                for (val resource : resources) {
                    if (first) {
                        first = false;
                        continue;
                    }
                    val newPath = PathUtil.of(path, resource.getDisplayName());
                    val file = File.builder()
                            .name(resource.getDisplayName())
                            .path(newPath)
                            .size(-1L)
                            .type(resource.isCollection() ? FileType.DIRECTORY : FileType.FILE)
                            .build();
                    files.add(file);
                }
                completion.accept(files);
                return;
            }
            completion.accept(List.of());
        });
    }

    public void login(Consumer<Boolean> completion) {
        val authentication = HTTPUtil.base64Encode(username + ":" + password);
        val headers = Map.of("Authorization", "Basic " + authentication, "Depth", "1");
        val url = PathUtil.of(serverUrl, "/");
        val request = HTTPModel.builder()
                .url(url)
                .method("PROPFIND")
                .headers(headers)
                .build();
        HTTPUtil.request(request, response -> {
            if (response instanceof Response res) {
                val code = res.code();
                if (code != 401) {
                    completion.accept(true);
                } else {
                    completion.accept(false);
                }
            }
        });
    }
}
