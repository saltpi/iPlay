package top.ourfor.app.iplayx.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

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
public class SiteModel {
    @JsonProperty("endpoint")
    SiteEndpointModel endpoint;
    @JsonProperty("endpoints")
    List<SiteLineModel> endpoints;
    @JsonProperty("user")
    EmbyUserModel user;

    @JsonProperty("sync")
    boolean sync;

    @JsonProperty("show_sensitive")
    boolean showSensitive;

    @JsonProperty("server_type")
    ServerType serverType;

    @JsonIgnore
    public String getAccessToken() {
        try {
            return user.accessToken;
        } catch (Exception e) {
            return null;
        }
    }

    @JsonIgnore
    public String getUserId() {
        try {
            return user.getUser().getId();
        } catch (Exception e) {
            return null;
        }
    }

    @JsonIgnore
    public void setRemark(String remake) {
        endpoint.setRemark(remake);
    }

    @JsonIgnore
    public String getRemark() {
        return endpoint.getRemark();
    }

    @JsonIgnore
    public String getUserName() {
        return user.getUser().getName();
    }

    @JsonIgnore
    public String getId() { return user.getServerId(); }

    @JsonIgnore
    public String avatarUrl() {
        return endpoint.getBaseUrl() + "emby/Users/" + getUserId() + "/Images/Primary";
    }
}
