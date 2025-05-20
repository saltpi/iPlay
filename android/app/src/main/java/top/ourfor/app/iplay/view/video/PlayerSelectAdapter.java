package top.ourfor.app.iplay.view.video;

import android.annotation.SuppressLint;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import top.ourfor.app.iplay.R;
import top.ourfor.app.iplay.util.FontUtil;

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
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.topMargin = 5;
        layoutParams.bottomMargin = 5;
        view.setLayoutParams(layoutParams);
        return new ViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlayerSelectModel<T> model = localDataSet.get(position);
        T text = localDataSet.get(position).item;
        val textView = holder.getTextView();
        val spannableString = FontUtil.applyEmojiFont(textView.getContext(), text.toString(), R.font.twemoji_mozilla);
        textView.setText(spannableString);
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
