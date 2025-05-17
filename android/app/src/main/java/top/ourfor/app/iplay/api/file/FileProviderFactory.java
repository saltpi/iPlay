package top.ourfor.app.iplay.api.file;

import top.ourfor.app.iplay.api.alist.AlistFileProvider;
import top.ourfor.app.iplay.api.cloud189.Cloud189FileProvider;
import top.ourfor.app.iplay.api.local.LocalFileProvider;
import top.ourfor.app.iplay.api.onedrive.OneDriveFileProvider;
import top.ourfor.app.iplay.api.webdav.WebDavFileProvider;
import top.ourfor.app.iplay.model.drive.AlistDriveModel;
import top.ourfor.app.iplay.model.drive.Cloud189Model;
import top.ourfor.app.iplay.model.drive.Drive;
import top.ourfor.app.iplay.model.drive.LocalDriveModel;
import top.ourfor.app.iplay.model.drive.OneDriveModel;
import top.ourfor.app.iplay.model.drive.WebDAVModel;

public class FileProviderFactory {
    public static FileProvider create(Drive drive) {
        return switch (drive.getType()) {
            case OneDrive -> new OneDriveFileProvider((OneDriveModel) drive);
            case Cloud189 -> new Cloud189FileProvider((Cloud189Model) drive);
            case WebDAV -> new WebDavFileProvider((WebDAVModel) drive);
            case Alist -> new AlistFileProvider((AlistDriveModel) drive);
            case Local -> new LocalFileProvider((LocalDriveModel) drive);
            default -> null;
        };
    }
}
