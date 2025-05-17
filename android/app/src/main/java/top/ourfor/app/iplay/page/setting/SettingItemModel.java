package top.ourfor.app.iplay.page.setting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettingItemModel {
    Type type;
    int title;
    int icon;

    enum Type {
        Theme,
        Video,
        Cloud,
        Audio,
        Site,
        Picture,
        About,
        Cache,
    }
}
