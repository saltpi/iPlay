package top.ourfor.app.iplayx.page.setting.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@Builder
@With
@NoArgsConstructor
@AllArgsConstructor
public class OptionModel<T> {
    public T value;
    public String title;

    public String toString() {
        return title != null ? title : super.toString();
    }
}
