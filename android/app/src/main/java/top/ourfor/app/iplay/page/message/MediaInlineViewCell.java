package top.ourfor.app.iplay.page.message;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import lombok.val;
import top.ourfor.app.iplay.R;
import top.ourfor.app.iplay.api.emby.EmbyModel;
import top.ourfor.app.iplay.common.model.IMediaModel;
import top.ourfor.app.iplay.action.UpdateModelAction;
import top.ourfor.app.iplay.module.GlideApp;
import top.ourfor.app.iplay.util.DeviceUtil;
import top.ourfor.app.iplay.view.infra.TextView;

public class MediaInlineViewCell extends ConstraintLayout implements UpdateModelAction {
    private IMediaModel model;
    private TextView nameLabel = new TextView(getContext());
    private ImageView coverImage = new ImageView(getContext());
    private TextView overviewLabel = new TextView(getContext());
    private RequestOptions options = new RequestOptions().transform(new RoundedCorners(10));


    public MediaInlineViewCell(@NonNull Context context) {
        super(context);
        setupUI(context);
        bind();
    }

    @Override
    public <T> void updateModel(T model) {
        if (!(model instanceof EmbyModel.EmbyAlbumModel) &&
            !(model instanceof EmbyModel.EmbyMediaModel)) {
            return;
        }
        this.model = (IMediaModel) model;
        boolean isEpisode = model instanceof EmbyModel.EmbyMediaModel && ((EmbyModel.EmbyMediaModel) model).getType().equals("Episode");
        nameLabel.setText(this.model.getName());
        overviewLabel.setText(this.model.getOverview());
        boolean isAlbum = model instanceof EmbyModel.EmbyAlbumModel;
        GlideApp.with(this)
                .load(isEpisode ? this.model.getImage().getPrimary() : this.model.getImage().getBackdrop())
                .placeholder(isAlbum ? R.drawable.hand_drawn_3 : R.drawable.abstract_3)
                .apply(options)
                .into(coverImage);
    }

    void setupUI(Context context) {
        LayoutParams layout = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layout.topMargin = (int) (15 * DeviceUtil.density);
        layout.bottomMargin = (int) (15 * DeviceUtil.density);
        setLayoutParams(layout);

        coverImage.setId(View.generateViewId());
        LayoutParams imageLayout = new LayoutParams((int) (160 * DeviceUtil.density), LayoutParams.MATCH_CONSTRAINT);
        // set height equal to parent width multiple 1.5
        imageLayout.dimensionRatio = "16:9";
        imageLayout.topToTop = LayoutParams.PARENT_ID;
        imageLayout.leftToLeft = LayoutParams.PARENT_ID;
        imageLayout.topMargin = DeviceUtil.dpToPx(5);
        imageLayout.leftMargin = DeviceUtil.dpToPx(5);
        imageLayout.rightMargin = DeviceUtil.dpToPx(5);
        imageLayout.bottomMargin = DeviceUtil.dpToPx(5);
        coverImage.setId(View.generateViewId());
        coverImage.setLayoutParams(imageLayout);
        coverImage.setScaleType(ImageView.ScaleType.FIT_XY);
        coverImage.setClipToOutline(true);
        addView(coverImage);

        val textLayout = new LayoutParams(LayoutParams.MATCH_CONSTRAINT, LayoutParams.WRAP_CONTENT);
        textLayout.topToTop = LayoutParams.PARENT_ID;
        textLayout.leftToRight = coverImage.getId();
        textLayout.rightToRight = LayoutParams.PARENT_ID;
        textLayout.topMargin = 15;
        nameLabel.setId(View.generateViewId());
        nameLabel.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        nameLabel.setGravity(Gravity.CENTER);
        nameLabel.setLayoutParams(textLayout);
        nameLabel.setEllipsize(TextUtils.TruncateAt.END);
        nameLabel.setMaxLines(1);
        nameLabel.setSingleLine(true);
        nameLabel.setTextColor(context.getColor(R.color.onBackground_highContrast));
        addView(nameLabel);

        val overviewLayout = new LayoutParams(LayoutParams.MATCH_CONSTRAINT, LayoutParams.WRAP_CONTENT);
        overviewLayout.topToBottom = nameLabel.getId();
        overviewLayout.leftToRight = coverImage.getId();
        overviewLayout.rightToRight = LayoutParams.PARENT_ID;
        overviewLayout.bottomToBottom = LayoutParams.PARENT_ID;
        overviewLayout.topMargin = 5;
        overviewLayout.bottomMargin = 15;
        overviewLayout.leftMargin = 10;
        overviewLabel.setId(View.generateViewId());
        overviewLabel.setLayoutParams(overviewLayout);
        overviewLabel.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
        overviewLabel.setGravity(Gravity.START);
        overviewLabel.setEllipsize(TextUtils.TruncateAt.END);
        overviewLabel.setMaxLines(5);
        overviewLabel.setTextColor(context.getColor(R.color.onBackground_mediumContrast));
        // set font size 12sp
        overviewLabel.setTextSize(12);
        addView(overviewLabel);
    }

    void bind() {
        if (!DeviceUtil.isTV) return;
        setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                setBackgroundResource(R.drawable.card_focus);
            } else {
                setBackgroundResource(R.drawable.card_normal);
            }
        });
    }
}
