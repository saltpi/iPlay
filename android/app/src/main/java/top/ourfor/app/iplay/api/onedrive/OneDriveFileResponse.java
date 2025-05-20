package top.ourfor.app.iplay.api.onedrive;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OneDriveFileResponse {
    @JsonAlias("@microsoft.graph.downloadUrl")
    String downloadUrl;
}
