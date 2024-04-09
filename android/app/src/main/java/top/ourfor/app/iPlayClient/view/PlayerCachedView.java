package top.ourfor.app.iPlayClient.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.List;

import top.ourfor.lib.mpv.SeekableRange;

public class PlayerCachedView extends View {
    private Paint paint;
    private List<SeekableRange> ranges;
    private double maxValue;
    public PlayerCachedView(Context context) {
        super(context);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        float partWidth = (float) width;

        RectF rect = new RectF(0, 0, width, height);
        paint.setColor(Color.parseColor("#f5f5f5"));
        canvas.drawRoundRect(rect, 8f,8f,paint);

        if (ranges != null ){
            for (int i = 0; i < ranges.size(); i++) {
                SeekableRange bean = ranges.get(i);
                paint.setColor(Color.GREEN);
                RectF redRect = new RectF((float) (bean.start * partWidth / maxValue), (float) 0,
                        (float) (bean.end * partWidth / maxValue), height);
                canvas.drawRect(redRect, paint);
            }
        }


    }


    public void setSegmentPart(List<SeekableRange> ranges, double maxValue) {
        this.ranges = ranges;
        this.maxValue = maxValue;
        invalidate();
    }
}
