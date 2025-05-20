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
public class ThemeColorModel {
    public enum ThemeColor {
        GRADIENT,
        PURE,
    }

    @Builder.Default
    ThemeColorModel.ThemeColor type = ThemeColor.GRADIENT;
    String title;


    public String toString() {
        return title != null ? title : super.toString();
    }
}
