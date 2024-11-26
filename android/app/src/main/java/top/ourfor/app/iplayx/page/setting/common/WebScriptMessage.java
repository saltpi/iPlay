package top.ourfor.app.iplayx.page.setting.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@With
public class WebScriptMessage<T> {
    String type;
    T data;
}
