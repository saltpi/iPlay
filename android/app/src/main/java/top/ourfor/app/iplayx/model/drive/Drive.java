package top.ourfor.app.iplayx.model.drive;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Getter;
import top.ourfor.app.iplayx.common.type.ServerType;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = OneDriveModel.class, name = "onedrive"),
        @JsonSubTypes.Type(value = Cloud189Model.class, name = "cloud189"),
        @JsonSubTypes.Type(value = WebDAVModel.class, name = "webdav"),
        @JsonSubTypes.Type(value = AlistDriveModel.class, name = "alist")
})
public interface Drive {
    default ServerType getType() {
        return ServerType.None;
    };

    default String getRemark() {
        return "";
    };
}
