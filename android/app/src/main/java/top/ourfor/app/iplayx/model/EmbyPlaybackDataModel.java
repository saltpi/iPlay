package top.ourfor.app.iplayx.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class EmbyPlaybackDataModel {
    @JsonProperty("MediaSourceId")
    String mediaSourceId;
    @JsonProperty("PlaySessionId")
    String playSessionId;
    @JsonProperty("PlayMethod")
    String playMethod;
    @JsonProperty("ItemId")
    String itemId;
}
