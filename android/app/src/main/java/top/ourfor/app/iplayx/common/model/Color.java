package top.ourfor.app.iplayx.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
public class Color {
    String border;
    String background;
    String color;

    @JsonIgnore
    public static int of(String hex) {
        return android.graphics.Color.parseColor(hex);
    }

    @JsonIgnore
    public int borderColor() {
        return of(border);
    }

    @JsonIgnore
    public int backgroundColor() {
        return of(background);
    }

    @JsonIgnore
    public int textColor() {
        return of(color);
    }
}
