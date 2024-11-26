package top.ourfor.app.iplayx.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import lombok.Getter;
import lombok.val;
import top.ourfor.app.iplayx.action.UpdateModelAction;
import top.ourfor.app.iplayx.util.DeviceUtil;

public class ListViewModel<T, V extends View> extends RecyclerView.Adapter {
    public List<T> items;
    public Class<? extends V> viewCell;
    public Consumer<ListItemClickEvent<T>> onClick;
    public Consumer<SwipeRefreshLayout> onRefresh;
    public Predicate<T> isSelected;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try {
            val view = viewCell.getConstructor(Context.class).newInstance(parent.getContext());
            return new ViewHolder(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (items == null || items.size() <= position || !(holder instanceof ViewHolder viewHolder)) return;
        T model = items.get(position);
        viewHolder.onClick = (event) -> {
            if (onClick == null) {
                if (viewHolder.view instanceof UpdateModelAction view) {
                    view.onItemClick();
                }
                return;
            };
            val data = this.items.get(event.getPosition());
            onClick.accept((ListItemClickEvent<T>) event.withModel(data));
            if (isSelected == null) return;
            notifyDataSetChanged();
        };
        if (viewHolder.view instanceof UpdateModelAction view) {
            view.updateModel(model);
            if (isSelected != null) {
                view.updateSelectionState(model, isSelected.test(model));
            }
        }
        // Make the item focusable
        if (DeviceUtil.isTV) {
            holder.itemView.setFocusable(true);
        }
    }


    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }


    @Getter
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private View view;
        public Consumer<ListItemClickEvent<Object>> onClick;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(v -> {
                if (getBindingAdapterPosition() == RecyclerView.NO_POSITION) return;
                ListItemClickEvent event = ListItemClickEvent.builder()
                        .position(getBindingAdapterPosition())
                        .view(v)
                        .build();
                onClick.accept(event);
            });
            this.view = view;
        }
    }
}
