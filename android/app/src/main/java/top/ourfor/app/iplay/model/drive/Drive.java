package top.ourfor.app.iplay.model.drive;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import top.ourfor.app.iplay.common.type.ServerType;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = OneDriveModel.class, name = "onedrive"),
        @JsonSubTypes.Type(value = Cloud189Model.class, name = "cloud189"),
        @JsonSubTypes.Type(value = WebDAVModel.class, name = "webdav"),
        @JsonSubTypes.Type(value = AlistDriveModel.class, name = "alist"),
        @JsonSubTypes.Type(value = LocalDriveModel.class, name = "local"),
})
public interface Drive {
    default ServerType getType() {
        return ServerType.None;
    };

    default String getRemark() {
        return "";
    };
}
