package top.ourfor.app.iplayx.view.video;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.With;

@AllArgsConstructor
@Builder
@With
@Getter
@ToString
@EqualsAndHashCode
public class PlayerSelectModel<T> {
    T item;
    boolean isSelected;
}
