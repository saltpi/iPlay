package top.ourfor.app.iplay.api.cloud189;

import static top.ourfor.app.iplay.module.Bean.XGET;

import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import okhttp3.Response;
import top.ourfor.app.iplay.bean.IJSONAdapter;
import top.ourfor.app.iplay.util.HTTPModel;
import top.ourfor.app.iplay.util.HTTPUtil;
import top.ourfor.app.iplay.util.PathUtil;
import top.ourfor.app.iplay.util.RSAUtil;

@Slf4j
public class Cloud189Api {
    String cookie = null;
    Map<String, Long> ids = new HashMap<>();

    @Setter
    String username = "";
    @Setter
    String password = "";

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static public class AppConfig {
        String accountType;
        Boolean isOauth2;
        String mailSuffix;
        String returnUrl;
        String paramId;
        Long clientType;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static public class DriveResponse<T> {
        T data;
        String msg;
        Integer result;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static public class EncryptConfig {
        String pre;
        String pubKey;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static public class FileItem {
        Long id;
        String name;
        Long size;
        Long parentId;

        String path;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class FileChildren {
        Integer count;
        List<FileItem> fileList;
        List<FileItem> folderList;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class ListChildrenResponse {
        Integer res_code;
        FileChildren fileListAO;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class LoginResponse {
        Integer result;
        String toUrl;
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class VideoQuality {
        String url;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class VideoPlaybackInfo {
        VideoQuality normal;
    }

    public String login(String username, String password) {
        var url = "https://cloud.189.cn/api/portal/loginUrl.action?redirectURL=https%3A%2F%2Fcloud.189.cn%2Fmain.action";
        var request = HTTPModel.builder()
                .headers(Map.of(
                        "User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"
                ))
                .url(url)
                .method("GET")
                .build();
        var loginUrlLatch = new CountDownLatch(1);
        AtomicReference<Map<String, String>> loginUrlResult = new AtomicReference<>();
        HTTPUtil.request(request, response -> {
            if (response instanceof Response) {
                val requestUrl = ((Response) response).request().url();
                log.info("Request URL: {}", requestUrl);
                val lt = requestUrl.queryParameter("lt");
                val reqId = requestUrl.queryParameter("reqId");
                val appId = requestUrl.queryParameter("appId");
                val redirectURL = requestUrl.url().toString();
                loginUrlResult.set(Map.of("lt", lt, "reqId", reqId, "appId", appId, "redirectURL", redirectURL));
            }
            loginUrlLatch.countDown();
        });
        try {
            loginUrlLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        val redirectURL = loginUrlResult.get().get("redirectURL");
        if (redirectURL.equals("https://cloud.189.cn/web/main")) {
            return null;
        }
        val lt = loginUrlResult.get().get("lt");
        val reqId = loginUrlResult.get().get("reqId");
        val appId = loginUrlResult.get().get("appId");

        var headers = Map.of(
                "lt",      lt,
                "reqId",  reqId,
                "referer", redirectURL,
                "origin",  "https://open.e.189.cn",
                "Content-Type", "application/x-www-form-urlencoded; charset=UTF-8",
                "User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36 Edg/126.0.0.0"
        );
        url = "https://open.e.189.cn/api/logbox/oauth2/appConf.do";
        var params = Map.of(
                "version", "2.0",
                "appKey", appId
        );
        request = HTTPModel.builder()
                .url(url)
                .headers(headers)
                .method("POST")
                .params(params)
                .typeReference(new TypeReference<DriveResponse<AppConfig>>() {
                })
                .build();
        var appConfLatch = new CountDownLatch(1);
        AtomicReference<DriveResponse<AppConfig>> appConfResult = new AtomicReference<>();
        HTTPUtil.request(request, response -> {
            if (response instanceof DriveResponse<?> driveResponse) {
                appConfResult.set((DriveResponse<AppConfig>) driveResponse);
            }
            appConfLatch.countDown();
        });
        try {
            appConfLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        url = "https://open.e.189.cn/api/logbox/config/encryptConf.do";
        params = Map.of(
                "appId", appId
        );
        request = HTTPModel.builder()
                .url(url)
                .headers(headers)
                .method("POST")
                .params(params)
                .typeReference(new TypeReference<DriveResponse<EncryptConfig>>() {})
                .build();
        var encryptConfLatch = new CountDownLatch(1);
        AtomicReference<DriveResponse<EncryptConfig>> encryptConfResult = new AtomicReference<>();
        HTTPUtil.request(request, response -> {
            if (response instanceof DriveResponse<?> driveResponse) {
                encryptConfResult.set((DriveResponse<EncryptConfig>) driveResponse);
            }
            encryptConfLatch.countDown();
        });

        try {
            encryptConfLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        url = "https://open.e.189.cn/api/logbox/oauth2/loginSubmit.do";
        val appConfig = appConfResult.get().getData();
        val encryptConfig = encryptConfResult.get().getData();
        params = new HashMap<>();
        params.putAll(Map.of(
                        "version",         "v2.0",
                        "apToken",         "",
                        "appKey",          appId,
                        "accountType",     appConfig.accountType,
                        "userName",        encryptConfig.pre + encode(username, encryptConfig.pubKey),
                        "epd",             encryptConfig.pre + encode(password, encryptConfig.pubKey)));
        params.putAll(Map.of(
                "captchaType",     "",
                "validateCode",    "",
                "smsValidateCode", "",
                "captchaToken",    "",
                "returnUrl",       appConfig.returnUrl,
                "mailSuffix",      appConfig.mailSuffix,
                "dynamicCheck",    "FALSE"));
        params.putAll(Map.of(
                "clientType",      appConfig.clientType.toString(),
                "cb_SaveName",     "3",
                "isOauth2",        appConfig.isOauth2 ? "1" : "0",
                "state",           "",
                "paramId",         appConfig.paramId));
        request = HTTPModel.builder()
                .url(url)
                .headers(headers)
                .method("POST")
                .params(params)
                .build();
        var loginSubmitLatch = new CountDownLatch(1);
        AtomicReference<LoginResponse> loginSubmitResult = new AtomicReference<>();
        HTTPUtil.request(request, response -> {
            if (response instanceof Response res) {
                String body = null;
                try {
                    body = res.body().string();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                val adapter = XGET(IJSONAdapter.class);
                LoginResponse obj = adapter.fromJSON(body, new TypeReference<LoginResponse>() { });
                loginSubmitResult.set(obj);
            }
            loginSubmitLatch.countDown();
        });

        try {
            loginSubmitLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        val finalUrl = loginSubmitResult.get().toUrl;
        headers = Map.of(
                "Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
                "User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36 Edg/126.0.0.0"
        );
        request = HTTPModel.builder()
                .url(finalUrl)
                .headers(headers)
                .method("GET")
                .disableRedirect(true)
                .build();
        val finalLatch = new CountDownLatch(1);
        HTTPUtil.request(request, response -> {
            if (response instanceof Response res) {
                cookie = String.join("; ", res.headers("Set-Cookie"));
                String body = null;
                try {
                    body = res.body().string();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            finalLatch.countDown();
        });

        try {
            finalLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return cookie;
    }

    public void listFiles(String path, Consumer<ListChildrenResponse> completion) {
        if (cookie == null) {
            login(username, password);
        }

        val id = ids.getOrDefault(path, -11L);
        val url = "https://cloud.189.cn/api/open/file/listFiles.action?noCache=0.9764056784249009&pageSize=60&pageNum=1&mediaType=0&folderId=" + id + "&iconOption=5&orderBy=lastOpTime&descending=true";
        val request = HTTPModel.<ListChildrenResponse>builder()
                .url(url)
                .headers(Map.of("Cookie", cookie, "Accept", "application/json;charset=UTF-8"))
                .method("GET")
                .typeReference(new TypeReference<ListChildrenResponse>() {
                })
                .build();
        HTTPUtil.request(request, result -> {
            if (result != null && result.fileListAO != null) {
                result.fileListAO.fileList.forEach(item -> {
                    item.path = PathUtil.of(path, item.name);
                    ids.put(item.path, item.id);
                });
                result.fileListAO.folderList.forEach(item -> {
                    item.path = PathUtil.of(path, item.name);
                    ids.put(item.path, item.id);
                });
                completion.accept(result);
            } else {
                completion.accept(null);
            }
        });
    }

    public void link(String path, Consumer<String> completion) {
        var id = ids.get(path);
        if (id == null) {
            completion.accept(null);
        }

        if (cookie == null) {
            login(username, password);
        }
        val url = "https://cloud.189.cn/api/portal/getNewVlcVideoPlayUrl.action?fileId=" + id + "&type=2";
        var request = HTTPModel.<VideoPlaybackInfo>builder()
                .url(url)
                .headers(Map.of("Cookie", cookie, "Accept", "application/json;charset=UTF-8"))
                .method("GET")
                .typeReference(new TypeReference<VideoPlaybackInfo>() {
                })
                .build();
        HTTPUtil.request(request, result -> {
            if (result != null && result.normal != null) {
                completion.accept(result.normal.url);
            } else {
                completion.accept(null);
            }
        });
    }

    private String encode(String str, String pubKey) {
        return RSAUtil.rsaEncode(str.getBytes(), pubKey, true);
    }
}
