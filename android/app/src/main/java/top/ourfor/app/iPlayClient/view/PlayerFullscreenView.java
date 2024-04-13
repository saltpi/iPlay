package top.ourfor.app.iPlayClient.view;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Constraints;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import java.util.function.Function;

import top.ourfor.app.iPlayClient.R;

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
        super(context, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        setFullscreenStyle();
        ConstraintLayout rootView = new ConstraintLayout(context);
        Constraints.LayoutParams layout = new Constraints.LayoutParams(Constraints.LayoutParams.MATCH_PARENT, Constraints.LayoutParams.MATCH_PARENT);
        rootView.setLayoutParams(layout);
        setContentView(rootView);
        this.controlView = controlView;
        this.contentView = contentView;
        this.eventView = eventView;
        this.containerView = rootView;
    }

    private void setFullscreenStyle() {
        Window window = getWindow();
        if (window != null) {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                WindowManager.LayoutParams lp = window.getAttributes();
                lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
                window.setAttributes(lp);
                final View decorView = window.getDecorView();
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            }
            window.setAttributes(layoutParams);
        }
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
