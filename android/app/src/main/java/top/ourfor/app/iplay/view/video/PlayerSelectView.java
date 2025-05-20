package top.ourfor.app.iplay.view.video;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import lombok.Setter;

@SuppressLint("ViewConstructor")
public class PlayerSelectView<T> extends ConstraintLayout implements PlayerSelectDelegate<PlayerSelectModel<T>> {
    RecyclerView listView;
    PlayerSelectAdapter listViewModel;

    @Setter
    private List<PlayerSelectModel<T>> datasource;
    @Setter
    private boolean multiSelectSupport = false;

    @Setter
    private PlayerSelectDelegate<PlayerSelectModel<T>> delegate;
    public PlayerSelectView(@NonNull Context context, List<PlayerSelectModel<T>> dataSource) {
        super(context);
        datasource = dataSource;
        listView = new RecyclerView(context);
        listView.setId(View.generateViewId());
        listViewModel = new PlayerSelectAdapter(datasource, this, multiSelectSupport);
        listView.setLayoutParams(listViewLayout());
        bind();
        setupUI(context);
    }

    private void setupUI(Context context) {
        listView.setLayoutManager(new LinearLayoutManager(context));
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(Color.argb(50, 0, 0, 0));
        gradientDrawable.setCornerRadius(10.f);
        setBackground(gradientDrawable);
        addView(listView);
    }

    private void bind() {
        listView.setAdapter(listViewModel);
    }

    @Override
    public void onSelect(PlayerSelectModel<T> data) {
        if (delegate == null) return;
        delegate.onSelect(data);
        if (!multiSelectSupport) {
            delegate.onClose();
        }
    }

    @Override
    public void onDeselect(PlayerSelectModel<T> data) {
        if (delegate == null) return;
        delegate.onSelect(data);
    }

    private LayoutParams listViewLayout() {
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
        params.topToTop = LayoutParams.PARENT_ID;
        params.leftToLeft = LayoutParams.PARENT_ID;
        params.rightToRight = LayoutParams.PARENT_ID;
        params.bottomToBottom = LayoutParams.PARENT_ID;
        params.leftMargin = 8;
        params.rightMargin = 8;
        params.topMargin = 10;
        params.bottomMargin = 10;
        return params;
    }

}
