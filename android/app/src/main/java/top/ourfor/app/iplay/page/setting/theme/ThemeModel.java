package top.ourfor.app.iplay.page.setting.theme;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Builder
@With
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThemeModel {
    public enum ThemeType {
        FOLLOW_SYSTEM,
        DARK_MODE,
        LIGHT_MODE
    }

    @Builder.Default
    ThemeType type = ThemeType.FOLLOW_SYSTEM;
    String title;

    public String toString() {
        return title != null ? title : super.toString();
    }

}
