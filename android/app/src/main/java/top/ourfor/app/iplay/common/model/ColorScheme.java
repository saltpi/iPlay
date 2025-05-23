package top.ourfor.app.iplay.common.model;

import static top.ourfor.app.iplay.module.Bean.XGET;

import android.app.Application;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import lombok.val;
import top.ourfor.app.iplay.bean.IJSONAdapter;
import top.ourfor.app.iplay.util.PathUtil;

@Data
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ColorScheme {
    Map<String, Color> scheme;
    List<Gradient> gradient;

    @JsonIgnore
    public static ColorScheme shared = getShared();

    static ColorScheme getShared() {
        val application = XGET(Application.class);
        // read color.json from assets
        try {
            val is = application.getAssets().open("color.json");
            val content = PathUtil.getContent(is);
            is.close();
            return XGET(IJSONAdapter.class).fromJSON(content, ColorScheme.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Color randomColor() {
        val array = scheme.values().toArray();
        val index = (int) (Math.random() * array.length);
        return (Color) array[index];
    }
}
