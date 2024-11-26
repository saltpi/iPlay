package top.ourfor.app.iplayx.view.video;

import android.graphics.Color;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PlayerSelectAdapter<T> extends RecyclerView.Adapter<PlayerSelectAdapter.ViewHolder> {
    private static String TAG = "PlayerSelectAdapter";
    private List<PlayerSelectModel<T>> localDataSet;
    private PlayerSelectDelegate<PlayerSelectModel<T>> delegate;
    private boolean multiSelectSupport;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PlayerSelectItemView view = new PlayerSelectItemView(parent.getContext());
        view.getTextView().setTextColor(Color.WHITE);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.topMargin = 5;
        layoutParams.bottomMargin = 5;
        view.setLayoutParams(layoutParams);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlayerSelectModel<T> model = localDataSet.get(position);
        T text = localDataSet.get(position).item;
        holder.getTextView()
                .setText(text.toString());
        holder.rootView.setOnClickListener(v -> {
            PlayerSelectModel<T> item = localDataSet.get(position);
            if (!multiSelectSupport) {
                localDataSet.forEach(i -> i.isSelected = false);
            }
            item.isSelected = !item.isSelected;
            if (delegate != null) {
                if (item.isSelected) delegate.onSelect(item);
                else delegate.onDeselect(item);
            }
            notifyDataSetChanged();
            Log.d(TAG, "position: " + position + " data: " + item);
        });
        holder.rootView.setIsSelected(model.isSelected);
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }


    @Getter
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final PlayerSelectItemView rootView;
        private final TextView textView;

        public ViewHolder(PlayerSelectItemView view) {
            super(view);
            rootView = view;
            textView = view.getTextView();
        }
    }
}
