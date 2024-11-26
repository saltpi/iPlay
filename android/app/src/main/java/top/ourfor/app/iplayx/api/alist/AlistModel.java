package top.ourfor.app.iplayx.api.alist;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AlistModel {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlistResponse<T> {
        int code;
        String msg;
        T data;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlistAuth {
        String token;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlistFile {
        String name;
        @JsonAlias("is_dir")
        boolean isDir;
        long size;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlistFileList {
        long total;
        List<AlistFile> content;
    }
}
