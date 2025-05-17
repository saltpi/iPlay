package top.ourfor.app.iplay.api.onedrive;

public interface OneDriveAction {
    default void onedriveReadyUpdate(OneDriveAuth auth) {}
}
