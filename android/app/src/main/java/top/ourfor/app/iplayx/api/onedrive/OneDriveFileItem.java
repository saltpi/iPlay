package top.ourfor.app.iplayx.api.onedrive;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OneDriveFileItem {
    String id;
    String name;
    Long size;
    OneDriveFolderProp folder;
    Date createdDateTime;
    Date lastModifiedDateTime;
    String webUrl;
    String eTag;
    String cTag;

    @JsonAlias("@microsoft.graph.downloadUrl")
    String downloadUrl;

    String path;
}
