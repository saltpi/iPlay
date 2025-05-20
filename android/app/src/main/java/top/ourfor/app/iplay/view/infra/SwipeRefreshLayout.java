package top.ourfor.app.iplay.view.infra;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lombok.val;
import top.ourfor.app.iplay.view.ListView;

public class SwipeRefreshLayout extends androidx.swiperefreshlayout.widget.SwipeRefreshLayout {
    public SwipeRefreshLayout(@NonNull Context context) {
        super(context);
    }

    public SwipeRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean canChildScrollUp() {
        if (getChildCount() > 0) {
            val child = getChildAt(0);
            if (child instanceof ListView<?> listView) {
                return listView.listView.canScrollVertically(-1);
            }
            return child.canScrollVertically(-1);
        }
        return super.canChildScrollUp();
    }
}
