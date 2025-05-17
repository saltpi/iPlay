package top.ourfor.app.iplay.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

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
public class GitHubReleaseModel {
    @JsonProperty("published_at")
    Date publishedAt;
    String body;
    @JsonProperty("tag_name")
    String tagName;
    String name;
    List<GitHubAsset> assets;
}
