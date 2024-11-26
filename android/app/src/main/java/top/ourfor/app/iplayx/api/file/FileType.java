package top.ourfor.app.iplayx.api.file;

public enum FileType {
    DIRECTORY("directory"),
    FILE("file"),
    LINK("link"),
    UNKNOWN("unknown");

    private String value;

    FileType(String value) {
        this.value = value;
    }

    public static FileType fromString(String type) {
        switch (type) {
            case "directory":
                return DIRECTORY;
            case "file":
                return FILE;
            default:
                return UNKNOWN;
        }
    }
}
