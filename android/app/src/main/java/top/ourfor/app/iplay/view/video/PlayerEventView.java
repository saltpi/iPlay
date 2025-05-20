package top.ourfor.app.iplay.view.video;

import static java.lang.Math.abs;

import android.content.Context;
import android.graphics.Rect;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.airbnb.lottie.LottieAnimationView;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import top.ourfor.app.iplay.R;
import top.ourfor.app.iplay.util.DeviceUtil;
import top.ourfor.app.iplay.util.LayoutUtil;
import top.ourfor.app.iplay.view.infra.RotateGestureDetector;
import top.ourfor.lib.mpv.TrackItem;
import top.ourfor.app.iplay.databinding.PlayerEventBinding;

@Slf4j
@Setter
public class PlayerEventView extends ConstraintLayout implements GestureDetector.OnGestureListener, PlayerSelectDelegate<PlayerSelectModel<Object>> {
    static int ICON_SMALL_SIZE = 72;
    private PlayerEventBinding binding;
    private GestureDetector detector;
    public List<View> ignoreAreas;
    public WeakReference<PlayerControlView> controlView;
    private long lastSeekTime = 0;
    private PlayerGestureType gestureType;
    public PlayerEventDelegate delegate;
    public PlayerSelectDelegate trackSelectDelegate;
    public PlayerNumberValueView numberValueView;
    private PlayerSelectView selectView;
    public LottieAnimationView activityIndicator;

    private float scale = 1f;
    private float rotation = 0f;

    private ScaleGestureDetector scaleDetector;
//    private RotateGestureDetector rotateDetector;

    public PlayerEventView(@NonNull Context context) {
        super(context);
        binding = PlayerEventBinding.inflate(LayoutInflater.from(context), this, true);
        binding.getRoot().setLayoutParams(LayoutUtil.fill());
        setupUI(context);
    }

    private void setupUI(Context context) {
        detector = new GestureDetector(context, this);

        numberValueView = new PlayerNumberValueView(context);
        numberValueView.setAlpha(0);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.leftToLeft = LayoutParams.PARENT_ID;
        params.topToTop = LayoutParams.PARENT_ID;
        params.rightToRight = LayoutParams.PARENT_ID;
        params.topMargin = 100;

        addView(numberValueView, params);
        activityIndicator = binding.loading;

        scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
//        rotateDetector = new RotateGestureDetector(context, new RotateListener());
    }

    @Override
    public boolean onDown(@NonNull MotionEvent e) {
        gestureType = PlayerGestureType.None;
        return !isInIgnoredArea(e);
    }

    public void showLoadIndicator(boolean visible) {
        if (activityIndicator == null) return;
        if (visible) {
            if (!activityIndicator.isAnimating()) {
                activityIndicator.playAnimation();
            }
        } else {
            if (activityIndicator.isAnimating()) {
                activityIndicator.cancelAnimation();
            }
        }
        activityIndicator.setVisibility(visible ? VISIBLE : INVISIBLE);
    }

    @Override
    public void onShowPress(@NonNull MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(@NonNull MotionEvent e) {
        boolean inIgnoredArea = isInIgnoredArea(e);
        log.debug("press in ignore area ?" + (inIgnoredArea ? "YES" : "NO"));
        if (!inIgnoredArea) delegate.onEvent(PlayerGestureType.HideControl, 0);
        return !inIgnoredArea;
    }

    @Override
    public boolean onScroll(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
        float x = e1.getX();
        int width = getWidth();
        int height = getHeight();
        float deltaY = (e1.getY() - e2.getY()) / height * 100;
        float deltaX = (e1.getX() - e2.getX()) / width * 100;
        switch (gestureType) {
            case None -> {
                if (abs(deltaY) > abs(deltaX) && x < width / 3) {
                    gestureType = PlayerGestureType.Brightness;
                } else if (abs(deltaY) > abs(deltaX) && x > width * 2 / 3) {
                    gestureType = PlayerGestureType.Volume;
                } else if (abs(deltaX) > abs(deltaY)) {
                    gestureType = PlayerGestureType.Seek;
                }
                if (delegate != null) {
                    delegate.onEvent(PlayerGestureType.None, gestureType);
                }
            }
            case Volume, Brightness -> {
                if (delegate != null) {
                    delegate.onEvent(gestureType, deltaY);
                }
            }
            case Seek -> {
                if (System.currentTimeMillis() - lastSeekTime < 300) break;
                if (delegate != null) {
                    delegate.onEvent(gestureType, -deltaX);
                }
                lastSeekTime = System.currentTimeMillis();
            }
        }
        return true;
    }

    @Override
    public void onLongPress(@NonNull MotionEvent e) {
        log.debug("Long press");

    }

    @Override
    public boolean onFling(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            log.debug("action up");
            numberValueView.hide();
        }
        var oldScale = scale;
        scaleDetector.onTouchEvent(event);
        if (oldScale != scale) {
            return true;
        }
        return detector.onTouchEvent(event);
    }

    boolean isInIgnoredArea(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        Rect area = new Rect();
        for (View view : ignoreAreas) {
            view.getGlobalVisibleRect(area);
            if (area.contains((int) x, (int) y)) {
                val alpha = view.getAlpha();
                val isControlVisible = controlView != null && controlView.get() != null && controlView.get().getAlpha() != 0;
                return alpha != 0 && isControlVisible;
            }
        }
        return false;
    }

    public void showSelectView(List<TrackItem> items) {
        showSelectView(items, "no");
    }

    public void showSelectView(List<TrackItem> items, String currentId) {
        showSelectView(items, currentId, "");
    }

    public void showSelectView(List<TrackItem> items, String currentId, String prefixText) {
        if (selectView != null) return;
        Context context = getContext();
        List<PlayerSelectModel> tracks = items.stream()
                .map(item -> {
                    item.setPrefix(prefixText);
                    return new PlayerSelectModel(item, String.valueOf(item.id).equals(currentId), prefixText);
                })
                .collect(Collectors.toList());
        selectView = new PlayerSelectView(context, tracks);
        selectView.setBackgroundResource(R.drawable.dialog_alpha_bg);
        selectView.setDelegate(this);
        LayoutParams layout = new LayoutParams(0, 0);
        layout.leftToLeft = LayoutParams.PARENT_ID;
        layout.topToTop = LayoutParams.PARENT_ID;
        layout.rightToRight = LayoutParams.PARENT_ID;
        layout.bottomToBottom = LayoutParams.PARENT_ID;
        layout.matchConstraintPercentHeight = 0.75f;
        layout.matchConstraintMaxHeight = (int) (DeviceUtil.screenSize(context).getHeight() * 0.65);
        layout.matchConstraintPercentWidth = 0.5f;
        layout.matchConstraintMaxWidth = (int) (DeviceUtil.screenSize(context).getWidth() * 0.5);
        post(() -> {
            addView(selectView, layout);
            requestLayout();
        });
        log.debug("add select view");
    }

    public boolean isSelectViewPresent() {
        return selectView != null && selectView.getParent() != null;
    }

    public void closeSelectView() {
        if (selectView != null) {
            removeView(selectView);
            selectView = null;
            requestLayout();
            log.debug("remove select view");
        }
    }

    @Override
    public void onSelect(PlayerSelectModel<Object> data) {
        if (trackSelectDelegate == null) return;
        trackSelectDelegate.onSelect(data);
    }

    @Override
    public void onDeselect(PlayerSelectModel<Object> data) {
        if (trackSelectDelegate == null) return;
        trackSelectDelegate.onDeselect(data);
    }

    @Override
    public void onClose() {
        closeSelectView();
    }

    private class RotateListener extends RotateGestureDetector.SimpleOnRotateGestureListener {
        @Override
        public boolean onRotate(RotateGestureDetector detector) {
            rotation -= detector.getRotationDegreesDelta();
            View view = delegate.getContentView();
            if (view != null) {
                view.setRotation(rotation);
            }
            return true;
        }
    }


    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scale *= detector.getScaleFactor();
            scale = Math.max(0.1f, Math.min(scale, 10.0f));
            View view = delegate.getContentView();
            if (view != null) {
                setScale(scale);
                view.setScaleX(scale);
                view.setScaleY(scale);
            }
            return true;
        }
    }
}
