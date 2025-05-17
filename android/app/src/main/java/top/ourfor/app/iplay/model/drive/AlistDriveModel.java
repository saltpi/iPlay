package top.ourfor.app.iplay.model.drive;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import top.ourfor.app.iplay.common.type.ServerType;

@Data
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlistDriveModel implements Drive {
    @Override
    public ServerType getType() {
        return ServerType.Alist;
    }


    String remark;
    String server;
    String username;
    String password;
    String token;
}
