package top.ourfor.app.iplayx.model.drive;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import top.ourfor.app.iplayx.common.type.ServerType;

@With
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebDAVModel implements Drive {
    @Override
    public ServerType getType() {
        return ServerType.WebDAV;
    }

    String remark;
    String serverUrl;
    String username;
    String password;
}
