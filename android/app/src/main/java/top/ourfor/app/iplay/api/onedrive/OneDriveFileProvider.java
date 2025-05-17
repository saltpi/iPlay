package top.ourfor.app.iplay.api.onedrive;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.val;
import top.ourfor.app.iplay.api.file.File;
import top.ourfor.app.iplay.api.file.FileProvider;
import top.ourfor.app.iplay.api.file.FileType;
import top.ourfor.app.iplay.model.drive.OneDriveModel;

@NoArgsConstructor
@AllArgsConstructor
public class OneDriveFileProvider implements FileProvider {
    OneDriveApi api;

    public OneDriveFileProvider(OneDriveModel drive) {
        api = new OneDriveApi(drive);
    }

    @Override
    public void listFiles(String path, Consumer<List<File>> completion) {
        api.listFiles(path, response -> {
            if (response == null) {
                completion.accept(List.of());
                return;
            }
            val children = response.value.stream().map(item -> new File(item.name, item.path, item.folder == null ? FileType.FILE : FileType.DIRECTORY, item.size, item, null)).collect(Collectors.toList());
            if (!path.equals("/")) {
                var parentPath = path.substring(0, path.lastIndexOf('/'));
                if (parentPath.isEmpty()) {
                    parentPath = "/";
                }
                val parent = new File("..", parentPath, FileType.LINK, null, null, null);
                children.add(0, parent);
            }
            completion.accept(children);
        });
    }

    @Override
    public void link(File file, Consumer<String> completion) {
        if (file.getExtra() instanceof OneDriveFileItem item) {
            val url = item.getDownloadUrl();
            completion.accept(url);
        } else {
            completion.accept(null);
        }
    }

    @Override
    public void read(String path, Consumer<Object> completion) {
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        api.read(path, completion);
    }

    @Override
    public void write(String path, String content, Consumer<Boolean> completion) {
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        api.write(path, content, completion);
    }
}
