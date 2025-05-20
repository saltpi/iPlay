package top.ourfor.app.iplay.view.video;

import static top.ourfor.app.iplay.module.Bean.XGET;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import top.ourfor.app.iplay.util.DeviceUtil;
import top.ourfor.app.iplay.view.player.Player;
import top.ourfor.lib.mpv.SeekableRange;

@Slf4j
public class PlayerSlider extends androidx.appcompat.widget.AppCompatSeekBar {

    private Paint paint;
    private SeekableRange[] ranges;
    private double maxValue;
    private RectF cacheLine;

    public PlayerSlider(Context context) {
        super(context);
        init();
    }

    public PlayerSlider(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PlayerSlider(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        cacheLine = new RectF(0, 0, 0, 0 );
        if (DeviceUtil.isTV) {
            setFocusable(true);
            setOnFocusChangeListener((v, hasFocus) -> {
                double scale = hasFocus ? 1.1 : 1.0;
                setScaleX((float) scale);
                setScaleY((float) scale);
            });
        }
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        if (ranges != null && maxValue >= 0.001) {
            int width = getWidth();
            int height = getHeight();
            int offsetX = getPaddingLeft();
            int trackWidth = width - getPaddingLeft() - getPaddingRight();
            int trackHeight = DeviceUtil.dpToPx(2);
            int offsetY = (height - trackHeight) / 2;
            for (val range : ranges) {
                if (range.end <= 0) continue;
                paint.setColor(Color.GREEN);
                cacheLine.set(
                        (float) (offsetX + range.start * trackWidth / maxValue),
                        (float) offsetY,
                        (float) (offsetX + range.end * trackWidth / maxValue),
                        (float) (offsetY + trackHeight)
                );
                canvas.drawRect(cacheLine, paint);
            }
        }

        super.onDraw(canvas);
    }

    public void setRanges(SeekableRange[] ranges, double maxValue) {
        if (ranges == null || maxValue <= 0.001f) return;
        this.ranges = ranges;
        this.maxValue = maxValue;
        invalidate();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            val player = XGET(Player.class);
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (player != null) {
                        player.jumpBackward(10);
                    }
                    return true;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if (player != null) {
                        player.jumpForward(10);
                    }
                    return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
