package top.ourfor.app.iplayx.page.home;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;

import lombok.val;
import top.ourfor.app.iplayx.R;
import top.ourfor.app.iplayx.action.UpdateModelAction;
import top.ourfor.app.iplayx.module.GlideApp;
import top.ourfor.app.iplayx.util.DeviceUtil;
import top.ourfor.app.iplayx.model.EmbyAlbumModel;
import top.ourfor.app.iplayx.view.infra.TextView;

public class AlbumViewCell extends ConstraintLayout implements UpdateModelAction {
    private EmbyAlbumModel model;
    private TextView nameLabel = new TextView(getContext());
    private ImageView coverImage = new ImageView(getContext());

    public AlbumViewCell(@NonNull Context context) {
        super(context);
        setupUI(context);
    }

    @Override
    public <T> void updateModel(T model) {
        if (!(model instanceof EmbyAlbumModel)) {
            return;
        }
        this.model = (EmbyAlbumModel) model;
        nameLabel.setText(this.model.getName());
        GlideApp.with(this)
                .load(this.model.getImage().getPrimary())
                .placeholder(R.drawable.abstract_3)
                .into(coverImage);
        updateLayout();
    }

    void setupUI(Context context) {
        LayoutParams layout = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        setLayoutParams(layout);

        coverImage.setId(View.generateViewId());
        LayoutParams imageLayout = new LayoutParams((int) (120 * DeviceUtil.density), LayoutParams.MATCH_CONSTRAINT);
        // set height equal to parent width multiple 1.5
        imageLayout.dimensionRatio = "16:9";
        imageLayout.topToTop = LayoutParams.PARENT_ID;
        imageLayout.leftToLeft = LayoutParams.PARENT_ID;
        imageLayout.rightToRight = LayoutParams.PARENT_ID;
        imageLayout.leftMargin = 10;
        imageLayout.rightMargin = 10;
        coverImage.setLayoutParams(imageLayout);
        // set image corner radius 5dp
        coverImage.setClipToOutline(true);

        addView(coverImage);

        val textLayout = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        textLayout.topToBottom = coverImage.getId();
        textLayout.leftToLeft = coverImage.getId();
        textLayout.rightToRight = coverImage.getId();
        textLayout.bottomToBottom = LayoutParams.PARENT_ID;
        textLayout.topMargin = 15;
        nameLabel.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        nameLabel.setLayoutParams(textLayout);
        addView(nameLabel);
    }

    void updateLayout() {
        boolean isAlbum = model instanceof EmbyAlbumModel;
        LayoutParams imageLayout = new LayoutParams((int) ((isAlbum ? 120 : 150) * DeviceUtil.density), LayoutParams.MATCH_CONSTRAINT);
        // set height equal to parent width multiple 1.5
        imageLayout.dimensionRatio = isAlbum ? "16:9" : "2:3";
        imageLayout.topToTop = LayoutParams.PARENT_ID;
        imageLayout.leftToLeft = LayoutParams.PARENT_ID;
        imageLayout.rightToRight = LayoutParams.PARENT_ID;
        imageLayout.leftMargin = 10;
        imageLayout.rightMargin = 10;
        coverImage.setLayoutParams(imageLayout);
    }
}
