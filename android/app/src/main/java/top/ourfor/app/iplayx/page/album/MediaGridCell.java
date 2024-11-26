package top.ourfor.app.iplayx.page.album;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import lombok.val;
import top.ourfor.app.iplayx.R;
import top.ourfor.app.iplayx.action.UpdateModelAction;
import top.ourfor.app.iplayx.common.model.MediaModel;
import top.ourfor.app.iplayx.common.type.MediaLayoutType;
import top.ourfor.app.iplayx.module.GlideApp;
import top.ourfor.app.iplayx.util.DeviceUtil;
import top.ourfor.app.iplayx.model.EmbyAlbumModel;
import top.ourfor.app.iplayx.model.EmbyMediaModel;
import top.ourfor.app.iplayx.view.infra.TextView;

public class MediaGridCell extends ConstraintLayout implements UpdateModelAction {
    private MediaModel model;
    private final TextView nameLabel = new TextView(getContext());
    private final ImageView coverImage = new ImageView(getContext());
    private final TextView countLabel = new TextView(getContext());
    private final TextView airDateLabel = new TextView(getContext());
    private MediaLayoutType layoutType = MediaLayoutType.None;


    public MediaGridCell(@NonNull Context context) {
        super(context);
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
        } else if (layoutType == MediaLayoutType.Backdrop) {
            if (((EmbyMediaModel) model).isEpisode()) imageUrl = model.getImage().getPrimary();
            else imageUrl = model.getImage().getBackdrop();
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
        } else {
            countLabel.setVisibility(GONE);
            airDateLabel.setVisibility(GONE);
        }
    }

    void setupUI(Context context) {
        setClipToOutline(true);
        val margin = DeviceUtil.dpToPx(5);
        LayoutParams layout = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layout.setMargins(margin, margin, margin, margin);
        setLayoutParams(layout);

        coverImage.setId(View.generateViewId());
        LayoutParams imageLayout = new LayoutParams(DeviceUtil.dpToPx(174), LayoutParams.MATCH_CONSTRAINT);
        // set height equal to parent width multiple 1.5
        imageLayout.dimensionRatio = "16:9";
        imageLayout.topToTop = LayoutParams.PARENT_ID;
        imageLayout.leftToLeft = LayoutParams.PARENT_ID;
        imageLayout.rightToRight = LayoutParams.PARENT_ID;
        imageLayout.leftMargin = DeviceUtil.dpToPx(4);
        imageLayout.rightMargin = DeviceUtil.dpToPx(4);
        coverImage.setLayoutParams(imageLayout);
        coverImage.setBackgroundResource(R.drawable.media_border);
        // set image corner radius 5dp
        coverImage.setScaleType(ImageView.ScaleType.FIT_XY);
        coverImage.setClipToOutline(true);

        addView(coverImage);

        val textLayout = new LayoutParams(LayoutParams.MATCH_CONSTRAINT, LayoutParams.WRAP_CONTENT);
        textLayout.topToBottom = coverImage.getId();
        textLayout.leftToLeft = coverImage.getId();
        textLayout.rightToRight = coverImage.getId();
        textLayout.topMargin = DeviceUtil.dpToPx(10);
        textLayout.bottomMargin = DeviceUtil.dpToPx(10);
        nameLabel.setId(View.generateViewId());
        nameLabel.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        nameLabel.setGravity(Gravity.CENTER);
        nameLabel.setLayoutParams(textLayout);
        nameLabel.setEllipsize(TextUtils.TruncateAt.END);
        nameLabel.setMaxLines(1);
        nameLabel.setSingleLine(true);
        nameLabel.setTextSize(DeviceUtil.spToPx(16));
        nameLabel.setTextColor(ContextCompat.getColor(context, R.color.onBackground));
        addView(nameLabel);

        val countLabelLayout = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        countLabelLayout.rightToRight = coverImage.getId();
        countLabelLayout.topToTop = coverImage.getId();
        countLabelLayout.topMargin = DeviceUtil.dpToPx(2);
        countLabelLayout.rightMargin = DeviceUtil.dpToPx(2);
        countLabel.setTextSize(DeviceUtil.spToPx(11));
        countLabel.setBackgroundResource(R.drawable.unplayed_count);
        countLabel.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        countLabel.setGravity(Gravity.CENTER);
        countLabel.setEllipsize(TextUtils.TruncateAt.END);
        countLabel.setMaxLines(1);
        countLabel.setSingleLine(true);
        val padding = DeviceUtil.dpToPx(4);
        countLabel.setMinWidth(padding * 6);
        countLabel.setPadding(padding, padding, padding, padding);
        countLabel.setTextColor(ContextCompat.getColor(context, R.color.white));
        countLabel.setLayoutParams(countLabelLayout);
        addView(countLabel);

        val airDateLayout = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        airDateLayout.topToBottom = nameLabel.getId();
        airDateLayout.leftToLeft = nameLabel.getId();
        airDateLayout.rightToRight = nameLabel.getId();
        airDateLayout.bottomMargin = DeviceUtil.dpToPx(5);
        airDateLayout.bottomToBottom = LayoutParams.PARENT_ID;
        airDateLabel.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        airDateLabel.setLayoutParams(airDateLayout);
        airDateLabel.setTextSize(DeviceUtil.spToPx(11));
        airDateLabel.setTextColor(ContextCompat.getColor(context, R.color.secondary));
        addView(airDateLabel);

        if (DeviceUtil.isTV) {
            imageLayout.width = imageLayout.width - DeviceUtil.dpToPx(8);
            coverImage.setLayoutParams(imageLayout);
            setPadding(DeviceUtil.dpToPx(4), DeviceUtil.dpToPx(2), DeviceUtil.dpToPx(4), DeviceUtil.dpToPx(2));
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
        boolean isMusic = model instanceof EmbyMediaModel && ((EmbyMediaModel) model).isMusicAlbum();
        layoutType = model.getLayoutType();
        int width = DeviceUtil.dpToPx(isAlbum || layoutType == MediaLayoutType.Backdrop ? (isAlbum ? 150 : 174) : 111);
        LayoutParams imageLayout = new LayoutParams(width, LayoutParams.MATCH_CONSTRAINT);
        // set height equal to parent width multiple 1.5
        imageLayout.dimensionRatio = isAlbum || layoutType == MediaLayoutType.Backdrop ? "16:9" : "2:3";
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
        if (DeviceUtil.isTV) {
            imageLayout.width = imageLayout.width - DeviceUtil.dpToPx(8);
            coverImage.setLayoutParams(imageLayout);
        }
    }
}
