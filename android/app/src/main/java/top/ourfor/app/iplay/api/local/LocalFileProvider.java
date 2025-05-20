package top.ourfor.app.iplay.api.local;

import android.os.Environment;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import top.ourfor.app.iplay.api.file.File;
import top.ourfor.app.iplay.api.file.FileProvider;
import top.ourfor.app.iplay.api.file.FileType;
import top.ourfor.app.iplay.model.drive.LocalDriveModel;

@Slf4j
public class LocalFileProvider implements FileProvider {
    LocalDriveModel drive;
    java.io.File root;

    public LocalFileProvider(LocalDriveModel model) {
        drive = model;
        log.info("local drive: {}", model);
        root = Environment.getExternalStorageDirectory();
    }

    @Override
    public void listFiles(String path, Consumer<List<File>> completion) {
        var hasParent = true;
        if (path.equals("/")) {
            hasParent = false;
            path = root.getAbsolutePath();
        }
        if (root != null && root.isDirectory() && root.canRead()) {
            val baseDir = new java.io.File(path);
            var items = Arrays.stream(baseDir.listFiles()).map(item -> File.builder()
                    .type(item.isFile() ? FileType.FILE : (item.isDirectory() ? FileType.DIRECTORY : FileType.UNKNOWN))
                    .name(item.getName())
                    .path(item.getAbsolutePath())
                    .build()).collect(Collectors.toList());
            if (hasParent) {
                items.add(0, new File("..", baseDir.getParentFile().getAbsolutePath(), FileType.LINK, 0L, null, null));
            }
            completion.accept(items);
        }
    }

    @Override
    public void link(File file, Consumer<String> completion) {
        val path = file.getPath();
        completion.accept(path);
    }
}
