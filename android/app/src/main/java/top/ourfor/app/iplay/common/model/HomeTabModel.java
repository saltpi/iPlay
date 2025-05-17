package top.ourfor.app.iplay.common.model;

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
public class HomeTabModel {
    Integer id;
    Integer title;
    Integer icon;
}
