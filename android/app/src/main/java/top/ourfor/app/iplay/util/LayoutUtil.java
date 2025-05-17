package top.ourfor.app.iplay.util;

import androidx.constraintlayout.widget.ConstraintLayout;

public class LayoutUtil {
    public static ConstraintLayout.LayoutParams center() {
        ConstraintLayout.LayoutParams centerParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        centerParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        centerParams.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
        centerParams.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
        centerParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        return centerParams;
    }

    public static ConstraintLayout.LayoutParams fill() {
        ConstraintLayout.LayoutParams centerParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
        centerParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        centerParams.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
        centerParams.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
        centerParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        return centerParams;
    }

    public static ConstraintLayout.LayoutParams fit() {
        ConstraintLayout.LayoutParams centerParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        return centerParams;
    }
}
