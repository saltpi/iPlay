package top.ourfor.app.iplay.page.music;

import android.content.Context;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import lombok.val;
import top.ourfor.app.iplay.R;
import top.ourfor.app.iplay.action.UpdateModelAction;
import top.ourfor.app.iplay.api.emby.EmbyModel;
import top.ourfor.app.iplay.databinding.MusicItemCellBinding;
import top.ourfor.app.iplay.module.GlideApp;
import top.ourfor.app.iplay.util.DeviceUtil;

public class MusicItemCellView extends ConstraintLayout implements UpdateModelAction {
    private static RequestOptions options = new RequestOptions().transform(new RoundedCorners(DeviceUtil.dpToPx(5)));
    MusicItemCellBinding binding = null;

    public MusicItemCellView(@NonNull Context context) {
        super(context);
        val layout = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        setLayoutParams(layout);
        binding = MusicItemCellBinding.inflate(LayoutInflater.from(context), this, true);

        if (!DeviceUtil.isTV) return;
        setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                binding.getRoot().setBackgroundResource(R.drawable.card_focus);
            } else {
                binding.getRoot().setBackgroundResource(R.drawable.card_normal);
            }
        });
    }

    @Override
    public <T> void updateModel(T model) {
        if (!(model instanceof EmbyModel.EmbyMediaModel)) {
            return;
        }

        val episode = (EmbyModel.EmbyMediaModel) model;
        binding.titleLabel.setText(episode.getIndexNumber() + ": " + episode.getName());
        binding.overviewLabel.setText(episode.getOverview());
        GlideApp.with(this)
                .load(episode.getImage().getPrimary())
                .placeholder(R.drawable.hand_drawn_3)
                .apply(options)
                .into(binding.posterImage);
    }
}
