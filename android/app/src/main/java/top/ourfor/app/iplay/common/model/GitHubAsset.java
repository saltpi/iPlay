package top.ourfor.app.iplay.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public class GitHubAsset {
    @JsonProperty("browser_download_url")
    String browserDownloadUrl;
    @JsonProperty("content_type")
    String contentType;
    String name;
    long size;
    String state;
    String url;
}
