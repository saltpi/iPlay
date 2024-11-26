package top.ourfor.app.iplayx.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;

@Data
@EqualsAndHashCode
@ToString
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmbyUserModel {
    @JsonProperty("AccessToken")
    String accessToken;
    @JsonProperty("ServerId")
    String serverId;
    @JsonProperty("User")
    EmbySiteUserModel user;
}
