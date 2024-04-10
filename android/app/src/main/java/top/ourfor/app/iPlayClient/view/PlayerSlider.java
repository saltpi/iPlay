package top.ourfor.app.iPlayClient.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
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

    private void init() {
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        cacheLine = new RectF(0, 0, 0, 0 );
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        if (ranges != null && maxValue >= 0.001) {
            int width = getWidth();
            int height = getHeight();
            int offsetX = getPaddingLeft();
            int offsetY = getPaddingTop() + 2;
            int trackWidth = width - getPaddingLeft() - getPaddingRight();
            int trackHeight = height - getPaddingTop() - getPaddingBottom() - 4;
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
}
