package top.ourfor.app.iplayx.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@Builder
@With
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SiteEndpointModel {
    @JsonProperty("remark")
    String remark;
    @JsonProperty("host")
    String host;
    @JsonProperty("protocol")
    String protocol;
    @JsonProperty("port")
    Integer port;
    @JsonProperty("path")
    String path;

    @JsonIgnore
    public String getBaseUrl() {
        return protocol + "://" + host + ":" + (port < 0 ? 443 : port) + (path.isEmpty() ? "/" : path);
    }
}
