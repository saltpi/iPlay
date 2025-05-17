package top.ourfor.app.iplay.api.alist;

import java.util.List;
import java.util.function.Consumer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import top.ourfor.app.iplay.api.file.File;
import top.ourfor.app.iplay.api.file.FileProvider;
import top.ourfor.app.iplay.api.file.FileType;
import top.ourfor.app.iplay.model.drive.AlistDriveModel;

@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlistFileProvider implements FileProvider {
    AlistApi api;

    public AlistFileProvider(AlistDriveModel drive) {
        api = AlistApi.builder()
                .drive(drive)
                .build();
        if (drive.getServer() == null || drive.getUsername() == null || drive.getPassword() == null) {
            return;
        }
        AlistApi.login(drive.getServer(), drive.getUsername(), drive.getPassword(), (token) -> {
            if (token != null) {
                drive.setToken(token);
            } else {
                log.info("login failed");
            }
        });
    }

    @Override
    public void listFiles(String path, Consumer<List<File>> completion) {
        api.listFiles(path, files -> {
            if (path.equals("/")) {
                completion.accept(files);
            } else {
                var parentPath = path.substring(0, path.lastIndexOf('/'));
                if (parentPath.isEmpty()) {
                    parentPath = "/";
                }
                files.add(0, File.builder()
                        .name("..")
                        .path(parentPath)
                        .size(-1L)
                        .type(FileType.LINK)
                        .build());
                completion.accept(files);
            }
        });
    }

    @Override
    public void link(File file, Consumer<String> completion) {
        var path = file.getPath();
        String server = api.drive.getServer();
        server = server.endsWith("/") ? server : server + "/";
        var url = server + "d" + path;
        if (file.getExtra() instanceof String sign && !sign.isEmpty()) {
            url += "?sign=" + sign;
        }
        completion.accept(url);
    }
}
