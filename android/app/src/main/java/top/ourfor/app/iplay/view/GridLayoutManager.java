package top.ourfor.app.iplay.view;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GridLayoutManager extends androidx.recyclerview.widget.GridLayoutManager {
    public GridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (Exception e) {
            log.error("onLayoutChildren: ", e);
        }
    }


    @Override
    public void measureChildWithMargins(View child, int widthUsed, int heightUsed) {
        final RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
        final Rect decorInsets = new Rect();
        calculateItemDecorationsForChild(child, decorInsets);
        final int widthSpec = getChildMeasureSpec(getWidth(), getWidthMode(),
                getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin + widthUsed + decorInsets.left + decorInsets.right,
                lp.width, canScrollHorizontally());
        final int heightSpec = getChildMeasureSpec(getHeight(), getHeightMode(),
                getPaddingTop() + getPaddingBottom() + lp.topMargin + lp.bottomMargin + heightUsed + decorInsets.top + decorInsets.bottom,
                lp.height, canScrollVertically());
        child.measure(widthSpec, heightSpec);
    }

    @Override
    public void layoutDecoratedWithMargins(View child, int left, int top, int right, int bottom) {
        final Rect decorInsets = new Rect();
        calculateItemDecorationsForChild(child, decorInsets);
        super.layoutDecoratedWithMargins(child, left + decorInsets.left, top + decorInsets.top, right - decorInsets.right, bottom - decorInsets.bottom);
    }
}
