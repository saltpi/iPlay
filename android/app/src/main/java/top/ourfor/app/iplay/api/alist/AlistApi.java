package top.ourfor.app.iplay.api.alist;

import static top.ourfor.app.iplay.module.Bean.XGET;

import com.fasterxml.jackson.core.type.TypeReference;

import java.security.MessageDigest;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import top.ourfor.app.iplay.api.file.File;
import top.ourfor.app.iplay.api.file.FileType;
import top.ourfor.app.iplay.bean.IJSONAdapter;
import top.ourfor.app.iplay.model.drive.AlistDriveModel;
import top.ourfor.app.iplay.util.HTTPModel;
import top.ourfor.app.iplay.util.HTTPUtil;

@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlistApi {
    AlistDriveModel drive;

    public static String login(String server, String username, String password, Consumer<String> completion) {
        var request = HTTPModel.<AlistModel.AlistResponse<AlistModel.AlistAuth>>builder()
                .url(server + (server.endsWith("/") ? "" : "/") + "api/auth/login/hash")
                .method("POST")
                .headers(Map.of("Content-Type", "application/json"))
                .body(XGET(IJSONAdapter.class).toJSON(Map.of("username", username, "password", sha256(password+"-https://github.com/alist-org/alist"))))
                .typeReference(new TypeReference<AlistModel.AlistResponse<AlistModel.AlistAuth>>() {})
                .build();
        HTTPUtil.request(request, response -> {
            if (response != null) {
                if (response.getCode() == 200) {
                    var auth = (AlistModel.AlistAuth) response.getData();
                    completion.accept(auth.getToken());
                } else {
                    log.error("login failed: {}", response.getMsg());
                    completion.accept(null);
                }
            } else {
                log.error("login failed: {}", response);
                completion.accept(null);
            }
        });
        return null;
    }


    public static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void listFiles(String path, Consumer<List<File>> completion) {
        var server = drive.getServer();
        var request = HTTPModel.<AlistModel.AlistResponse<AlistModel.AlistFileList>>builder()
                .url(server + (server.endsWith("/") ? "" : "/") + "api/fs/list")
                .method("POST")
                .headers(Map.of("Content-Type", "application/json", "Authorization", drive.getToken()))
                .body(XGET(IJSONAdapter.class).toJSON(Map.of("path", path, "refresh", false, "page", 1, "per_page", 1000, "password", "")))
                .typeReference(new TypeReference<AlistModel.AlistResponse<AlistModel.AlistFileList>>() {})
                .build();
        HTTPUtil.request(request, response -> {
            if (response != null) {
                if (response.getCode() == 200) {
                    var children = (AlistModel.AlistFileList) response.getData();
                    var parentPath = path + (path.endsWith("/") ? "" : "/");
                    List<File> files = children.content.stream().map(file -> File.builder()
                            .name(file.getName())
                            .path(parentPath + file.getName())
                            .type(file.isDir() ? FileType.DIRECTORY : FileType.FILE)
                            .size(file.getSize())
                            .extra(file.sign)
                            .build()).collect(Collectors.toList());
                    completion.accept(files);
                } else {
                    log.error("login failed: {}", response.getMsg());
                    completion.accept(null);
                }
            } else {
                log.error("login failed: {}", response);
                completion.accept(null);
            }
        });
    }
}
