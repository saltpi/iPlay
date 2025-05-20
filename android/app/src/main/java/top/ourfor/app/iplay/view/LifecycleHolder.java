package top.ourfor.app.iplay.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

import top.ourfor.app.iplay.bean.IPageLifecycle;

public class LifecycleHolder extends ConstraintLayout implements LifecycleOwner, ViewModelStoreOwner, IPageLifecycle {
    private final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

    public LifecycleHolder(Context context) {
        super(context);
    }

    public LifecycleHolder(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LifecycleHolder(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LifecycleHolder(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onAttach() {
        lifecycleRegistry.setCurrentState(Lifecycle.State.STARTED);
    }

    @Override
    public void onDetach() {
        lifecycleRegistry.setCurrentState(Lifecycle.State.DESTROYED);
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }

    @NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        return null;
    }
}
