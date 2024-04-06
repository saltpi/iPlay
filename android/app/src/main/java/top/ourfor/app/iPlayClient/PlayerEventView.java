package top.ourfor.app.iPlayClient;

import static java.lang.Math.abs;

import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.List;

public class PlayerEventView extends ConstraintLayout implements GestureDetector.OnGestureListener {
    protected static final String TAG = "PlayerEventView";
    private GestureDetector detector;
    public List<View> ignoreAreas;
    private long lastSeekTime = 0;
    private PlayerGestureType gestureType;
    public PlayerEventDelegate delegate;
    public PlayerNumberValueView numberValueView;

    public PlayerEventView(@NonNull Context context) {
        super(context);
        setupUI(context);
    }

    private void setupUI(Context context) {
        detector = new GestureDetector(context, this);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();

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
        Log.d(TAG, "press");
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
            case Volume -> {
                if (delegate != null) {
                    delegate.onEvent(gestureType, deltaY);
                }
            }
            case Brightness -> {
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
        Log.d(TAG, "Long press");

    }

    @Override
    public boolean onFling(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            Log.d(TAG, "action up");
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

}
