package top.ourfor.app.iplay.page.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import top.ourfor.app.iplay.R;
import top.ourfor.app.iplay.action.UpdateModelAction;
import top.ourfor.app.iplay.common.type.MediaLayoutType;
import top.ourfor.app.iplay.databinding.MediaCellBinding;
import top.ourfor.app.iplay.model.AlbumModel;
import top.ourfor.app.iplay.model.MediaModel;
import top.ourfor.app.iplay.module.GlideApp;
import top.ourfor.app.iplay.util.DeviceUtil;
import top.ourfor.app.iplay.view.infra.TextView;

public class MediaViewCell extends ConstraintLayout implements UpdateModelAction {
    private static final RequestOptions options = new RequestOptions().transform(new RoundedCorners(DeviceUtil.dpToPx(8)));
    private Object model;
    MediaCellBinding binding;
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
        if (object instanceof AlbumModel album) {
            model = object;
            updateLayout();
            nameLabel.setText(album.getTitle());
            GlideApp.with(this)
                    .load(album.getBackdrop())
                    .placeholder(R.drawable.hand_drawn_3)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(options)
                    .into(coverImage);
            countLabel.setVisibility(GONE);
            airDateLabel.setVisibility(GONE);
            countLabel.setVisibility(GONE);
            airDateLabel.setVisibility(GONE);
        } else if (object instanceof MediaModel media) {
            model = object;
            updateLayout();
            nameLabel.setText(media.getName());
            String imageUrl;
            if (layoutType == MediaLayoutType.Backdrop || layoutType == MediaLayoutType.EpisodeDetail) {
                if (media.isEpisode()) imageUrl = media.getImage().getPrimary();
                else imageUrl = media.getImage().getBackdrop();
            } else {
                imageUrl = media.getImage().getPrimary();
            }
            GlideApp.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.abstract_3)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(options)
                    .into(coverImage);
            if (media.getUserData() != null && media.getUserData().getUnplayedItemCount() != null) {
                countLabel.setText(media.getUserData() != null ? media.getUserData().getUnplayedItemCount().toString() : null);
                countLabel.setVisibility(VISIBLE);
            } else {
                countLabel.setVisibility(GONE);
            }
            if (media.getAirDate() != null) {
                airDateLabel.setText(media.getAirDate());
                airDateLabel.setVisibility(VISIBLE);
            } else {
                airDateLabel.setVisibility(GONE);
            }

            if (media.getLayoutType() == MediaLayoutType.EpisodeDetail) {
                nameLabel.setText(media.getName());
                airDateLabel.setText(media.getName());
            } else if (media.isEpisode()) {
                nameLabel.setText(media.getSeriesName());
                airDateLabel.setText(media.getName());
            }
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
        boolean isAlbum = model instanceof AlbumModel;
        boolean isMedia = model instanceof MediaModel;
        var media = isMedia ? (MediaModel) model : null;
        boolean isMusic = isMedia && (media.isMusicAlbum() || media.isAudio());
        layoutType = isAlbum ? MediaLayoutType.Backdrop : media.getLayoutType();
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
