package top.ourfor.app.iPlayClient.view;

import static java.lang.Math.abs;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import top.ourfor.lib.mpv.TrackItem;

@Slf4j
@Setter
public class PlayerEventView extends ConstraintLayout implements GestureDetector.OnGestureListener, PlayerSelectDelegate<PlayerSelectModel<Object>> {
    private GestureDetector detector;
    public List<View> ignoreAreas;
    private long lastSeekTime = 0;
    private PlayerGestureType gestureType;
    public PlayerEventDelegate delegate;
    public PlayerSelectDelegate trackSelectDelegate;
    public PlayerNumberValueView numberValueView;
    private PlayerSelectView selectView;

    public PlayerEventView(@NonNull Context context) {
        super(context);
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
    }

    @Override
    public boolean onDown(@NonNull MotionEvent e) {
        gestureType = PlayerGestureType.None;
        return !isInIgnoredArea(e);
    }

    @Override
    public void onShowPress(@NonNull MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(@NonNull MotionEvent e) {
        log.debug("press");
        boolean inIgnoredArea = isInIgnoredArea(e);
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
        return detector.onTouchEvent(event);
    }

    boolean isInIgnoredArea(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        Rect area = new Rect();
        for (View view : ignoreAreas) {
            view.getHitRect(area);
            if (area.contains((int) x, (int) y)) {
                return view.getAlpha() != 0;
            }
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void showSelectView(List<TrackItem> items) {
        showSelectView(items, "no");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void showSelectView(List<TrackItem> items, String currentId) {
        if (selectView != null) return;
        Context context = getContext();
        List<PlayerSelectModel> subtitles = items.stream()
                .map(item -> new PlayerSelectModel(item, String.valueOf(item.id).equals(currentId)))
                .collect(Collectors.toList());
        selectView = new PlayerSelectView(context, subtitles);
        selectView.setDelegate(this);
        LayoutParams layout = new LayoutParams(0, 0);
        layout.leftToLeft = LayoutParams.PARENT_ID;
        layout.topToTop = LayoutParams.PARENT_ID;
        layout.rightToRight = LayoutParams.PARENT_ID;
        layout.bottomToBottom = LayoutParams.PARENT_ID;
        layout.matchConstraintPercentHeight = 0.75f;
        layout.matchConstraintMaxHeight = 640;
        layout.matchConstraintPercentWidth = 0.5f;
        layout.matchConstraintMaxWidth = 800;
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

}
