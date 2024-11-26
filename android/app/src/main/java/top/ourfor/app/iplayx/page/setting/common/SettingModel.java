package top.ourfor.app.iplayx.page.setting.common;

import java.util.List;
import java.util.function.Consumer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.With;

@Builder
@With
@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class SettingModel {
    SettingType type;
    String title;
    Object value;

    List<?> options;
    Consumer<Object> onClick;
}
