package top.ourfor.app.iPlayClient.view;


import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.Image;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import lombok.Setter;
import top.ourfor.app.iPlayClient.R;

public class PlayerSelectView<T> extends ConstraintLayout implements PlayerSelectDelegate<PlayerSelectModel<T>> {
    private static int CLOSE_ICON_SIZE = 56;

    private RecyclerView listView;
    private PlayerSelectAdapter listViewModel;
    private ImageView closeButton;
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
        listViewModel = new PlayerSelectAdapter(datasource, this, multiSelectSupport);
        closeButton = new ImageView(context);
        closeButton.setImageTintList(ColorStateList.valueOf(Color.WHITE));
        closeButton.setImageResource(R.drawable.xmark);
        bind();
        setupUI(context);
    }

    private void setupUI(Context context) {
        listView.setLayoutManager(new LinearLayoutManager(context));
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(Color.argb(50, 0, 0, 0));
        gradientDrawable.setCornerRadius(10.f);
        setBackground(gradientDrawable);
        addView(listView, listViewLayout());
        addView(closeButton, closeButtonLayout());
    }

    private void bind() {
        listView.setAdapter(listViewModel);
        closeButton.setOnClickListener((v) -> {
            if (delegate == null) return;
            delegate.onClose();
        });
    }

    @Override
    public void onSelect(PlayerSelectModel<T> data) {
        if (delegate == null) return;
        delegate.onSelect(data);
    }

    @Override
    public void onDeselect(PlayerSelectModel<T> data) {
        if (delegate == null) return;
        delegate.onSelect(data);
    }

    private LayoutParams listViewLayout() {
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.topToTop = LayoutParams.PARENT_ID;
        params.leftToLeft = LayoutParams.PARENT_ID;
        params.bottomToBottom = LayoutParams.PARENT_ID;
        params.rightToRight = LayoutParams.PARENT_ID;
        params.leftMargin = 8;
        params.rightMargin = CLOSE_ICON_SIZE + 8;
        params.topMargin = CLOSE_ICON_SIZE + 10;
        params.bottomMargin = 10;
        return params;
    }

    private LayoutParams closeButtonLayout() {
        LayoutParams params = new LayoutParams(CLOSE_ICON_SIZE, CLOSE_ICON_SIZE);
        params.topToTop = LayoutParams.PARENT_ID;
        params.rightToRight = LayoutParams.PARENT_ID;
        params.rightMargin = 12;
        params.topMargin = 12;
        return params;
    }

}
