package top.ourfor.app.iplay.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@With
public class ImageModel {
    @JsonProperty("primary")
    String primary;
    @JsonProperty("backdrop")
    String backdrop;
    @JsonProperty("logo")
    String logo;
    @JsonProperty("thumb")
    String thumb;

    @JsonProperty("fallback")
    List<String> fallback;
}
