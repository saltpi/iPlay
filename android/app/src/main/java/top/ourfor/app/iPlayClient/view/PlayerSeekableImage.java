package top.ourfor.app.iPlayClient.view;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;

import top.ourfor.lib.mpv.SeekableRange;

public class PlayerSeekableImage {
    public static LayerDrawable seekableImage(SeekableRange[] ranges, double maxValue, int width) {
// Create a ShapeDrawable for the green part
        ShapeDrawable greenDrawable = new ShapeDrawable(new RectShape());
        greenDrawable.getPaint().setColor(Color.GREEN);
        greenDrawable.setBounds(0, 0, 30, 5); // Set the width to 30% and the height to 5

// Create a ShapeDrawable for the red part
        ShapeDrawable redDrawable = new ShapeDrawable(new RectShape());
        redDrawable.getPaint().setColor(Color.RED);
        redDrawable.setBounds(80, 0, 100, 5); // Set the width to 20% and the height to 5

// Create a ShapeDrawable for the background
        ShapeDrawable background = new ShapeDrawable(new RectShape());
        background.getPaint().setColor(Color.WHITE);
        background.setBounds(0, 0, 100, 5); // Set the width to 100 and the height to 5

// Create a LayerDrawable
        Drawable[] drawables = new Drawable[]{background, greenDrawable, redDrawable};
        LayerDrawable layerDrawable = new LayerDrawable(drawables);
        return layerDrawable;
    }
}
