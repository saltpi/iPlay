package top.ourfor.app.iplay.api.file;

import java.util.List;
import java.util.function.Consumer;

public interface FileProvider {
    default List<File> listFiles(String path) {
        return null;
    };

    default void listFiles(String path, Consumer<List<File>> completion) {
        completion.accept(List.of());
    };

    default void link(File file, Consumer<String> completion) {
        completion.accept(null);
    }

    default void read(String path, Consumer<Object> completion) {
        completion.accept(null);
    }

    default void write(String path, String content, Consumer<Boolean> completion) {
        completion.accept(false);
    }
}
