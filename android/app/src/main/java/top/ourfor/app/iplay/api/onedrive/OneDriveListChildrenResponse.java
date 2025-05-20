package top.ourfor.app.iplay.api.onedrive;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OneDriveListChildrenResponse {
    List<OneDriveFileItem> value;
    @JsonAlias("@odata.nextLink")
    String nextLink;
}
