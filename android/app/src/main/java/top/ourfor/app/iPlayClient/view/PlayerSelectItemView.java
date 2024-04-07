package top.ourfor.app.iPlayClient.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import lombok.Getter;
import lombok.Setter;
import top.ourfor.app.iPlayClient.R;

@Getter
public class PlayerSelectItemView extends ConstraintLayout {
    private TextView textView;
    private ImageView iconView;
    private static int iconSize = 36;

    @SuppressLint("ResourceType")
    public PlayerSelectItemView(@NonNull Context context) {
        super(context);
        iconView = new ImageView(context);
        textView = new TextView(context);
        setupUI(context);
    }

    private void setupUI(Context context) {
        addView(iconView, iconViewLayout());
        addView(textView, textViewLayout());
    }

    public void setIsSelected(boolean flag) {
        int icon = NO_ID;
        if (flag) {
            icon = R.drawable.checkmark;
        }
        iconView.setImageResource(icon);
        iconView.setImageTintList(ColorStateList.valueOf(Color.WHITE));
    }

    private LayoutParams textViewLayout() {
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = 5;
        params.bottomMargin = 5;
        params.leftMargin = iconSize + 30;
        params.topToTop = LayoutParams.PARENT_ID;
        params.bottomToBottom = LayoutParams.PARENT_ID;
        return params;
    }

    private LayoutParams iconViewLayout() {
        LayoutParams params = new LayoutParams(iconSize, iconSize);
        params.topMargin = 5;
        params.bottomMargin = 5;
        params.topToTop = LayoutParams.PARENT_ID;
        params.leftMargin = 20;
        params.bottomToBottom = LayoutParams.PARENT_ID;
        return params;
    }

}
