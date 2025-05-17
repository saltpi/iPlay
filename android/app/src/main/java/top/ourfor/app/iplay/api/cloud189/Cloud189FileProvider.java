package top.ourfor.app.iplay.api.cloud189;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.val;
import top.ourfor.app.iplay.api.file.File;
import top.ourfor.app.iplay.api.file.FileProvider;
import top.ourfor.app.iplay.api.file.FileType;
import top.ourfor.app.iplay.model.drive.Cloud189Model;

@NoArgsConstructor
@AllArgsConstructor
public class Cloud189FileProvider implements FileProvider {
    @Getter
    Cloud189Api api = null;

    public Cloud189FileProvider(Cloud189Model drive) {
        api = new Cloud189Api();
        api.cookie = drive.getCookie();
    }

    @Override
    public void listFiles(String path, Consumer<List<File>> completion) {
        api.listFiles(path, response -> {
            if (response == null) {
                completion.accept(List.of());
                return;
            }
            val folders = response.fileListAO.folderList.stream().map(item -> new File(item.name, item.path, FileType.DIRECTORY, item.size, item, null)).collect(Collectors.toList());
            if (!path.equals("/")) {
                var parentPath = path.substring(0, path.lastIndexOf('/'));
                if (parentPath.isEmpty()) {
                    parentPath = "/";
                }
                val parent = new File("..", parentPath, FileType.LINK, null, null, null);
                folders.add(0, parent);
            }
            val files = response.fileListAO.fileList.stream().map(item -> new File(item.name, item.path, FileType.FILE, item.size, item, null)).collect(Collectors.toList());
            folders.addAll(files);
            completion.accept(folders);
        });
    }

    @Override
    public void link(File file, Consumer<String> completion) {
        api.link(file.getPath(), response -> {
            if (response == null) {
                completion.accept(null);
                return;
            }
            completion.accept(response);
        });
    }
}
