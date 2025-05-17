package top.ourfor.app.iplay.view.infra;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.SupportMenuInflater;
import androidx.appcompat.widget.ActionMenuView;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import top.ourfor.app.iplay.R;

@Slf4j
public class Toolbar extends androidx.appcompat.widget.Toolbar implements ToolbarAction {
    private ActionMenuView leftMenu;
    private ActionMenuView rightMenu;
    private OnMenuItemClickListener onMenuItemClickListener;

    public Toolbar(@NonNull Context context) {
        super(context);
        setupUI();
    }

    public Toolbar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setupUI();
    }

    public Toolbar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupUI();
    }

    public void setupUI() {
        leftMenu = new ActionMenuView(getContext());
        rightMenu = new ActionMenuView(getContext());
        setupMenu(leftMenu, Position.Left);
        addView(leftMenu);
        setupMenu(rightMenu, Position.Right);
        addView(rightMenu);
    }

    @Override
    public void inflateMenu(int resId) {
        super.inflateMenu(resId);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void inflateMenu(int resId, Position position) {
        val tintColor = getContext().getColor(R.color.onBackground);
        var supportMenuInflater = new SupportMenuInflater(getContext());
        if (position == Position.Left) {
            leftMenu.getMenu().clear();
            supportMenuInflater.inflate(resId, leftMenu.getMenu());
            for (int i = 0; i < leftMenu.getMenu().size(); i++) {
                val icon = leftMenu.getMenu().getItem(i).getIcon();
                if (icon == null) continue;
                icon.setTint(tintColor);
            }
        } else if (position == Position.Right) {
            rightMenu.getMenu().clear();
            supportMenuInflater.inflate(resId, rightMenu.getMenu());
            for (int i = 0; i < rightMenu.getMenu().size(); i++) {
                rightMenu.getMenu().getItem(i).getIcon().setTint(tintColor);
            }
        } else {
            super.inflateMenu(resId);
        }
    }

    @Override
    public void clear() {
        super.getMenu().clear();
        leftMenu.getMenu().clear();
        rightMenu.getMenu().clear();
    }

    @Override
    public Menu getLeftMenu() {
        return leftMenu.getMenu();
    }

    @Override
    public Menu getRightMenu() {
        return rightMenu.getMenu();
    }

    @Override
    public void setTitle(String title) {
        try {
            val view = (TextView)findViewById(R.id.toolbar_title);
            if (view == null) return;
            view.setText(title);
        } catch (Exception e) {
            log.error("Error", e);
        }
    }

    @Override
    public void invalidateMenu() {
        super.invalidateMenu();
        for (int i = 0; i < leftMenu.getChildCount(); i++) {
            leftMenu.getMenu().removeItem(i);
        }
        for (int i = 0; i < rightMenu.getChildCount(); i++) {
            rightMenu.getMenu().removeItem(i);
        }
    }

    @Override
    public void setOnMenuItemClickListener(OnMenuItemClickListener listener) {
        super.setOnMenuItemClickListener(listener);
        onMenuItemClickListener = listener;
    }

    @SuppressLint({"RestrictedApi", "RtlHardcoded"})
    void setupMenu(ActionMenuView menuView, Position position) {
        menuView.setOnMenuItemClickListener(item -> {
            if (onMenuItemClickListener != null) {
                return onMenuItemClickListener.onMenuItemClick(item);
            }
            return false;
        });
        final LayoutParams lp = generateDefaultLayoutParams();
        lp.gravity = (position == Position.Left ? Gravity.LEFT : Gravity.RIGHT) | (Gravity.TOP & Gravity.VERTICAL_GRAVITY_MASK);
        menuView.setLayoutParams(lp);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        leftMenu.invalidate();
        rightMenu.invalidate();
    }
}
