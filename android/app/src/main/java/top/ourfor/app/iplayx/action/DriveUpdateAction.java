package top.ourfor.app.iplayx.action;

import top.ourfor.app.iplayx.model.drive.Drive;

public interface DriveUpdateAction {
    default void onSelectedDriveChanged(Drive drive) {}
    default void onDriveAdded(Drive drive) {}
    default void onDriveRemoved(Drive drive) {}
    default void onDriveUpdate(Drive drive) {};
}
