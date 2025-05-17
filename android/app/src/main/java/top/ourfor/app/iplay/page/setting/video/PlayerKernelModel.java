package top.ourfor.app.iplay.page.setting.video;

import androidx.annotation.NonNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import top.ourfor.app.iplay.common.type.PlayerKernelType;

@Builder
@With
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerKernelModel {

    @Builder.Default
    PlayerKernelType type = PlayerKernelType.MPV;
    String title;

    @NonNull
    public String toString() {
        return title != null ? title : super.toString();
    }
}
