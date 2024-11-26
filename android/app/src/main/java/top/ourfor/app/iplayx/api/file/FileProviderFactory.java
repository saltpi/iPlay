package top.ourfor.app.iplayx.api.file;

import top.ourfor.app.iplayx.api.alist.AlistFileProvider;
import top.ourfor.app.iplayx.api.cloud189.Cloud189FileProvider;
import top.ourfor.app.iplayx.api.file.FileProvider;
import top.ourfor.app.iplayx.api.onedrive.OneDriveFileProvider;
import top.ourfor.app.iplayx.api.webdav.WebDavFileApi;
import top.ourfor.app.iplayx.api.webdav.WebDavFileProvider;
import top.ourfor.app.iplayx.model.drive.AlistDriveModel;
import top.ourfor.app.iplayx.model.drive.Cloud189Model;
import top.ourfor.app.iplayx.model.drive.Drive;
import top.ourfor.app.iplayx.model.drive.OneDriveModel;
import top.ourfor.app.iplayx.model.drive.WebDAVModel;

public class FileProviderFactory {
    public static FileProvider create(Drive drive) {
        switch (drive.getType()) {
            case OneDrive:
                return new OneDriveFileProvider((OneDriveModel) drive);
            case Cloud189:
                return new Cloud189FileProvider((Cloud189Model) drive);
            case WebDAV:
                return new WebDavFileProvider((WebDAVModel) drive);
            case Alist:
                return new AlistFileProvider((AlistDriveModel) drive);
            default:
                return null;
        }
    }
}
