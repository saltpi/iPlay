package top.ourfor.app.iplay.view;

import android.view.View;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@Builder
@With
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ListItemClickEvent<T> {
    private int position;
    private T model;
    private View view;
}
