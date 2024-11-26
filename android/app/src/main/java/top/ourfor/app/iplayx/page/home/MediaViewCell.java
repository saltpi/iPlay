package top.ourfor.app.iplayx.page.home;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import lombok.val;
import top.ourfor.app.iplayx.R;
import top.ourfor.app.iplayx.action.UpdateModelAction;
import top.ourfor.app.iplayx.common.model.MediaModel;
import top.ourfor.app.iplayx.common.type.MediaLayoutType;
import top.ourfor.app.iplayx.databinding.MediaCellBinding;
import top.ourfor.app.iplayx.model.EmbyAlbumModel;
import top.ourfor.app.iplayx.model.EmbyMediaModel;
import top.ourfor.app.iplayx.module.GlideApp;
import top.ourfor.app.iplayx.util.DeviceUtil;
import top.ourfor.app.iplayx.view.infra.TextView;

public class MediaViewCell extends ConstraintLayout implements UpdateModelAction {
    private MediaModel model;
    private MediaCellBinding binding;
    private TextView nameLabel;
    private ImageView coverImage;
    private TextView countLabel;
    private TextView airDateLabel;
    private MediaLayoutType layoutType = MediaLayoutType.None;


    public MediaViewCell(@NonNull Context context) {
        super(context);
        binding = MediaCellBinding.inflate(LayoutInflater.from(context), this, true);
        setupUI(context);
    }

    @Override
    public <T> void updateModel(T object) {
        if (!(object instanceof EmbyAlbumModel) &&
            !(object instanceof EmbyMediaModel)) {
            return;
        }
        model = (MediaModel) object;
        updateLayout();
        nameLabel.setText(model.getName());
        boolean isAlbum = object instanceof EmbyAlbumModel;
        String imageUrl;
        if (isAlbum) {
            imageUrl = model.getImage().getPrimary();
        } else if (layoutType == MediaLayoutType.Backdrop || layoutType == MediaLayoutType.EpisodeDetail) {
            if (((EmbyMediaModel) model).isEpisode()) imageUrl = model.getImage().getPrimary();
            else imageUrl = model.getImage().getThumb();
        } else {
            imageUrl = model.getImage().getPrimary();
        }
        GlideApp.with(this)
                .load(imageUrl)
                .placeholder(isAlbum ? R.drawable.hand_drawn_3 : R.drawable.abstract_3)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(coverImage);
        if (model instanceof EmbyMediaModel media) {
            if (media.getUserData() != null && media.getUserData().getUnplayedItemCount() != null) {
                countLabel.setText(media.getUserData().getUnplayedItemCount().toString());
                countLabel.setVisibility(VISIBLE);
            } else {
                countLabel.setVisibility(GONE);
            }
            if (media.getDateTime() != null) {
                airDateLabel.setText(media.getDateTime());
                airDateLabel.setVisibility(VISIBLE);
            } else {
                airDateLabel.setVisibility(GONE);
            }

            if (media.getLayoutType() == MediaLayoutType.EpisodeDetail) {
                nameLabel.setText(media.getName());
                airDateLabel.setText(media.episodeShortName());
            } else if (media.isEpisode()) {
                nameLabel.setText(media.getSeriesName());
                airDateLabel.setText(media.episodeName());
            }
        } else {
            countLabel.setVisibility(GONE);
            airDateLabel.setVisibility(GONE);
        }
    }

    void setupUI(Context context) {
        nameLabel = binding.nameLabel;
        coverImage = binding.coverImage;
        countLabel = binding.countLabel;
        airDateLabel = binding.airDateLabel;

        if (DeviceUtil.isTV) {
            setFocusable(true);
            setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    v.setBackground(ContextCompat.getDrawable(context, R.drawable.card_focus));
                } else {
                    v.setBackground(ContextCompat.getDrawable(context, R.drawable.card_normal));
                }
            });
        }
    }

    void updateLayout() {
        boolean isAlbum = model instanceof EmbyAlbumModel;
        boolean isMedia = model instanceof EmbyMediaModel;
        EmbyMediaModel media = isMedia ? (EmbyMediaModel) model : null;
        boolean isMusic = isMedia && (media.isMusicAlbum() || media.isAudio());
        layoutType = model.getLayoutType();
        int width = DeviceUtil.dpToPx(isAlbum || layoutType == MediaLayoutType.Backdrop || layoutType == MediaLayoutType.EpisodeDetail ? (isAlbum ? 150 : 174) : 111);
        LayoutParams imageLayout = new LayoutParams(width, LayoutParams.MATCH_CONSTRAINT);
        // set height equal to parent width multiple 1.5
        imageLayout.dimensionRatio = isAlbum || layoutType == MediaLayoutType.Backdrop || layoutType == MediaLayoutType.EpisodeDetail ? "16:9" : "2:3";
        if (isMusic) {
            imageLayout.dimensionRatio = "1:1";
        }
        imageLayout.topToTop = LayoutParams.PARENT_ID;
        imageLayout.leftToLeft = LayoutParams.PARENT_ID;
        imageLayout.rightToRight = LayoutParams.PARENT_ID;
        imageLayout.leftMargin = DeviceUtil.dpToPx(3);
        imageLayout.rightMargin = DeviceUtil.dpToPx(3);
        if (DeviceUtil.isTV) {
            imageLayout.topMargin = DeviceUtil.dpToPx(3);
        }
        coverImage.setLayoutParams(imageLayout);
    }

    @Override
    public <T> void updateSelectionState(T model, boolean selected) {
        setSelected(selected);
    }
}
