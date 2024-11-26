package top.ourfor.app.iplayx.model.drive;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import top.ourfor.app.iplayx.api.onedrive.OneDriveAuth;
import top.ourfor.app.iplayx.common.type.ServerType;

@With
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OneDriveModel implements Drive {
    @Override
    public ServerType getType() {
        return ServerType.OneDrive;
    }

    String remark;
    OneDriveAuth auth;
}
