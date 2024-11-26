package top.ourfor.app.iplayx.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.With;
import top.ourfor.app.iplayx.common.model.SiteEndpointModel;
import top.ourfor.app.iplayx.common.type.ServerType;

@Data
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SiteLineModel {
    @JsonProperty("endpoint")
    SiteEndpointModel endpoint;
    @JsonProperty("remark")
    String remark;

    @JsonIgnore
    boolean showDelay;
    @JsonIgnore
    boolean showUrl;
}
