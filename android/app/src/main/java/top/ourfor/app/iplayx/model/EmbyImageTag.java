package top.ourfor.app.iplayx.model;

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
public class EmbyImageTag {
    @JsonProperty("Primary")
    private String primary;
    @JsonProperty("Thumb")
    private String thumb;
}
