package top.ourfor.app.iplay.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy;

import com.airbnb.lottie.LottieAnimationView;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import lombok.val;
import top.ourfor.app.iplay.databinding.ListViewBinding;
import top.ourfor.app.iplay.util.DeviceUtil;

public class ListView<T> extends ConstraintLayout {
    private ListViewBinding binding;
    public ListViewModel<T, View> viewModel;
    public RecyclerView listView;
    public LottieAnimationView emptyTipView;

    public ListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setupUI(context);
        bind();
    }

    public ListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupUI(context);
        bind();
    }

    public ListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setupUI(context);
        bind();
    }

    public ListView(@NonNull Context context) {
        super(context);
        setupUI(context);
        bind();
    }

    public void setItems(List<T> newItems) {
        emptyTipView.post(() -> {
            emptyTipView.setVisibility(newItems == null || newItems.isEmpty() ? VISIBLE : GONE);
        });
        if (viewModel.items == null) {
            viewModel.items = newItems == null ? new CopyOnWriteArrayList<>() : new CopyOnWriteArrayList<>(newItems);
            post(() -> viewModel.notifyDataSetChanged());
            return;
        }
        val oldItems = viewModel.items;
        val diffResult = DiffUtil.calculateDiff(new ListDiff(oldItems, newItems), true);
        viewModel.items.clear();
        if (newItems != null) viewModel.items.addAll(newItems);
        post(() -> {
            diffResult.dispatchUpdatesTo(viewModel);
        });
    }

    public void resetItems(List<T> newItems) {
        viewModel.items = newItems;
        post(() -> {
            try {
                viewModel.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void setHasFixedSize(boolean hasFixedSize) {
        listView.setHasFixedSize(hasFixedSize);
    }

    public void setCacheSize(int cacheSize) {
        listView.setItemViewCacheSize(cacheSize);
    }

    @Deprecated
    public void reloadData() {
        post(() -> viewModel.notifyDataSetChanged());
    }

    private void setupUI(Context context) {
        binding = ListViewBinding.inflate(LayoutInflater.from(context), this);
        listView = binding.listView;
        emptyTipView = binding.emptyTip;
        emptyTipView.setVisibility(GONE);
        viewModel = new ListViewModel<>();
        LayoutParams layout = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        listView.setLayoutParams(layout);
        listView.setLayoutManager(new LinearLayoutManager(context));
        if (DeviceUtil.isTV) {
            listView.setFocusable(true);
        }
    }

    private void bind() {
        viewModel.setStateRestorationPolicy(StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        listView.setAdapter(viewModel);
    }

    public void setEmptyTipVisible(int visibility) {
        post(() -> emptyTipView.setVisibility(visibility));
    }

    public class ListDiff extends DiffUtil.Callback {
        private final List<T> oldItems;
        private final List<T> newItems;

        ListDiff(List<T> oldItems, List<T> newItems) {
            this.oldItems = oldItems;
            this.newItems = newItems;
        }

        @Override
        public int getOldListSize() {
            return oldItems == null ? 0 : oldItems.size();
        }

        @Override
        public int getNewListSize() {
            return newItems == null ? 0 : newItems.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            if (oldItems == null || newItems == null) {
                return false;
            }
            if (oldItems.size() <= oldItemPosition || newItems.size() <= newItemPosition) {
                return false;
            }
            val oldItem = oldItems.get(oldItemPosition);
            val newItem = newItems.get(newItemPosition);
            return (oldItem != null && oldItem.equals(newItem)) || (oldItem == null && newItem == null);
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return areItemsTheSame(oldItemPosition, newItemPosition);
        }
    }

    @Override
    public boolean canScrollVertically(int direction) {
        return listView.canScrollVertically(direction);
    }
}
