package top.ourfor.app.iplay.api.onedrive;

import static top.ourfor.app.iplay.module.Bean.XGET;

import com.fasterxml.jackson.core.type.TypeReference;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.With;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import top.ourfor.app.iplay.store.IAppStore;
import top.ourfor.app.iplay.util.HTTPModel;
import top.ourfor.app.iplay.util.HTTPUtil;
import top.ourfor.app.iplay.util.PathUtil;
import top.ourfor.app.iplay.model.drive.OneDriveModel;

@Slf4j
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OneDriveApi {
    static String API_AUTH_URL = "https://login.microsoftonline.com/common/oauth2/v2.0/authorize";
    static String API_TOKEN_URL = "https://login.microsoftonline.com/common/oauth2/v2.0/token";
    static String API_URL = "https://graph.microsoft.com/v1.0";
    static final Map<String, String> authBody = Map.of(
        "client_id", "8be7695d-474a-4f6a-a564-f3f6a349978f",
        "response_type", "code",
        "scope", "Files.ReadWrite offline_access",
        "redirect_uri", "iplay://drive/onedrive"
    );

    static final Map<String, String> redeemBody = Map.of(
        "client_id", "8be7695d-474a-4f6a-a564-f3f6a349978f",
        "redirect_uri", "iplay://drive/onedrive",
        "grant_type", "authorization_code"
//        "code", ""
    );

    static final Map<String, String> refreshBody = Map.of(
        "client_id", "8be7695d-474a-4f6a-a564-f3f6a349978f",
        "redirect_uri", "iplay://drive/onedrive",
        "grant_type", "refresh_token"
//        "refresh_token", ""
    );

    OneDriveModel drive;
    OneDriveAuth auth;

    public OneDriveApi(OneDriveModel drive) {
        this.drive = drive;
        auth = drive.getAuth();
    }

    public static String auth() {
        // https://login.microsoftonline.com/common/oauth2/v2.0/authorize?client_id=8be7695d-474a-4f6a-a564-f3f6a349978f&scope=Files.ReadWrite,Files.ReadWrite.AppFolder,offline_access&response_type=code&redirect_uri=iplay://drive/onedrive
        StringBuilder url = new StringBuilder(API_AUTH_URL);
        url.append("?");
        authBody.forEach((key, value) -> {
            try {
                value = URLEncoder.encode(value, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            url.append(key).append("=").append(value).append("&");
        });
        url.deleteCharAt(url.length() - 1);
        return url.toString();
    }

    public void redeem(String code) {
        StringBuilder params = new StringBuilder();
        redeemBody.forEach((key, value) -> {
            try {
                value = URLEncoder.encode(value, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            params.append(key).append("=").append(value).append("&");
        });
        params.append("code").append("=").append(code);
        var reqModel = HTTPModel.<OneDriveAuth>builder()
                .url(API_TOKEN_URL)
                .headers(Map.of("Content-Type", "application/x-www-form-urlencoded"))
                .method("POST")
                .body(params.toString())
                .typeReference(new TypeReference<OneDriveAuth>() {
                })
                .build();
        HTTPUtil.request(reqModel, result -> {
            log.info("redeem {}", result);
            if (result != null) {
                result.expires_at = (int) TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + result.expires_in;
                val action = XGET(OneDriveAction.class);
                if (action != null) action.onedriveReadyUpdate(result);
            }
        });
    }

    public void refresh() {
        if (auth == null || auth.refresh_token == null) return;
        StringBuilder params = new StringBuilder();
        refreshBody.forEach((key, value) -> {
            try {
                value = URLEncoder.encode(value, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            params.append(key).append("=").append(value).append("&");
        });
        params.append("refresh_token").append("=").append(auth.refresh_token);
        var reqModel = HTTPModel.<OneDriveAuth>builder()
                .url(API_TOKEN_URL)
                .headers(Map.of("Content-Type", "application/x-www-form-urlencoded"))
                .method("POST")
                .body(params.toString())
                .typeReference(new TypeReference<OneDriveAuth>() {
                })
                .build();
        CountDownLatch latch = new CountDownLatch(1);
        HTTPUtil.request(reqModel, result -> {
            log.info("refresh {}", result);
            if (result != null) {
                result.expires_at = (int) TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + result.expires_in;
                auth = result;
                drive.setAuth(result);
                XGET(ThreadPoolExecutor.class).submit(() -> {
                    XGET(IAppStore.class).save();
                });
            }
            latch.countDown();
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String token() {
        if (auth != null) {
            val now = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
            if (now > auth.expires_at) {
                refresh();
            }
            return auth.token_type + " " + auth.access_token;
        }
        return null;
    }

    public void read(String path, Consumer<Object> completion) {
        val token = token();
        val url = API_URL + "/me/drive/root:" + path;
        val request = HTTPModel.builder()
                .url(url)
                .headers(Map.of("Authorization", token))
                .method("GET")
                .modelClass(OneDriveFileResponse.class)
                .build();
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> downloadUrl = new AtomicReference<>(null);
        HTTPUtil.request(request, result -> {
            if (result instanceof OneDriveFileResponse file) {
                downloadUrl.set(file.downloadUrl);
            }
            latch.countDown();
        });

        try {
            latch.await();
            val download = HTTPModel.<String>builder()
                    .url(downloadUrl.get())
                    .headers(Map.of("Authorization", token))
                    .method("GET")
                    .typeReference(new TypeReference<String>() {})
                    .build();
            if (downloadUrl.get() == null) {
                completion.accept(null);
                return;
            }
            HTTPUtil.request(download, completion::accept);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void write(String path, String content, Consumer<Boolean> completion) {
        val token = token();
        val url = API_URL + "/me/drive/root:" + path + ":/createUploadSession";
        val request = HTTPModel.<OneDriveUploadResponse>builder()
                .url(url)
                .headers(Map.of("Authorization", token))
                .body("")
                .method("POST")
                .typeReference(new TypeReference<OneDriveUploadResponse>() {})
                .build();
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> uploadUrl = new AtomicReference<>(null);
        HTTPUtil.request(request, result -> {
            if (result != null) {
                uploadUrl.set(result.uploadUrl);
            }
            latch.countDown();
        });
        try {
            latch.await();
            if (uploadUrl.get() == null) {
                completion.accept(false);
                return;
            }
            val bytes = content.getBytes(StandardCharsets.UTF_8);
            val upload = HTTPModel.<String>builder()
                    .url(uploadUrl.get())
                    .headers(Map.of(
                            "Content-Length", String.valueOf(bytes.length),
                            "Content-Range", "Bytes " + "0-" + String.valueOf(bytes.length-1) + "/" + bytes.length
                    ))
                    .method("PUT")
                    .body(content)
                    .typeReference(new TypeReference<String>() {
                    })
                    .build();
            HTTPUtil.request(upload, result -> {
                if (result != null) {
                    completion.accept(true);
                } else {
                    completion.accept(false);
                }
            });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // @link https://learn.microsoft.com/en-us/onedrive/developer/rest-api/api/driveitem_get?view=odsp-graph-online
    public void listFiles(String path, Consumer<OneDriveListChildrenResponse> completion) {
        val token = token();
        val url = path == "/" ? API_URL + "/me/drive/root/children" : API_URL + "/me/drive/root:" + path + ":/children";
        var request = HTTPModel.<OneDriveListChildrenResponse>builder()
                .url(url)
                .headers(Map.of("Authorization", token))
                .method("GET")
                .typeReference(new TypeReference<OneDriveListChildrenResponse>() {
                })
                .build();
        HTTPUtil.request(request, result -> {
            if (result != null && result.value != null) {
                result.value.forEach(item -> {
                    item.path = PathUtil.of(path, item.name);
                });
                completion.accept(result);
            } else {
                completion.accept(null);
            }
        });
    }
}
