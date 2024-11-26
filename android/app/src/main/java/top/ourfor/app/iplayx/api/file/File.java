package top.ourfor.app.iplayx.api.file;

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
public class File {
    String name;
    String path;
    FileType type;
    Long size;

    Object extra;
}
