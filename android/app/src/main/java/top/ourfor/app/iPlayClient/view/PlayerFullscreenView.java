package top.ourfor.app.iPlayClient.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import java.util.function.Function;

public class PlayerFullscreenView extends Dialog {
    private ConstraintLayout containerView;
    private View contentView;
    public ViewGroup controlView;
    public ViewGroup eventView;
    private ViewGroup superview;
    public PlayerFullscreenView(
            Context context,
            View contentView,
            ViewGroup controlView,
            ViewGroup eventView) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        ConstraintLayout layout = new ConstraintLayout(context);
        setContentView(layout);
        this.controlView = controlView;
        this.contentView = contentView;
        this.eventView = eventView;
        this.containerView = layout;
    }

    @Override
    protected void onStart() {
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
        superview = (ViewGroup) contentView.getParent();
        if (contentView != null) {
            superview.removeView(contentView);
            containerView.addView(contentView, layoutParams);
        }
        if (controlView != null) {
            superview.removeView(controlView);
            containerView.addView(controlView, layoutParams);
        }

        if (eventView != null) {
            superview.removeView(eventView);
            containerView.addView(eventView, layoutParams);
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
        if (controlView != null) {
            containerView.removeView(contentView);
            superview.addView(contentView, layoutParams);
        }
        if (contentView != null) {
            containerView.removeView(controlView);
            superview.addView(controlView, layoutParams);
        }
        if (eventView != null) {
            containerView.removeView(eventView);
            superview.addView(eventView, layoutParams);
        }
        superview = null;
        super.onStop();
    }
}
