package top.ourfor.app.iplayx.common.model;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebMediaMessage {
    String video;
    String audio;
    String subtitle;

    Map<String, String> extra;
}
