package top.ourfor.app.iplay.action;

import top.ourfor.app.iplay.model.drive.Drive;

public interface DriveUpdateAction {
    default void onSelectedDriveChanged(Drive drive) {}
    default void onDriveAdded(Drive drive) {}
    default void onDriveRemoved(Drive drive) {}
    default void onDriveUpdate(Drive drive) {};
}
