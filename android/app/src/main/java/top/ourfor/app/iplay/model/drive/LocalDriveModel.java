package top.ourfor.app.iplay.model.drive;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import top.ourfor.app.iplay.common.type.ServerType;

@With
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocalDriveModel implements Drive {
    @Override
    public ServerType getType() {
        return ServerType.Local;
    }

    String remark;
    String path;
}
