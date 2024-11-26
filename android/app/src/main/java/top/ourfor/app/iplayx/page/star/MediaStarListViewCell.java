package top.ourfor.app.iplayx.page.star;

import static top.ourfor.app.iplayx.module.Bean.XGET;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import java.util.HashMap;
import java.util.function.Consumer;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import top.ourfor.app.iplayx.R;
import top.ourfor.app.iplayx.action.UpdateModelAction;
import top.ourfor.app.iplayx.bean.Navigator;
import top.ourfor.app.iplayx.common.model.MediaModel;
import top.ourfor.app.iplayx.common.type.MediaType;
import top.ourfor.app.iplayx.model.EmbyMediaModel;
import top.ourfor.app.iplayx.page.home.MediaViewCell;
import top.ourfor.app.iplayx.util.DeviceUtil;
import top.ourfor.app.iplayx.util.NavigationUtil;
import top.ourfor.app.iplayx.view.GridLayoutManager;
import top.ourfor.app.iplayx.view.ListItemClickEvent;
import top.ourfor.app.iplayx.view.ListView;
import top.ourfor.app.iplayx.view.infra.TextView;

@Slf4j
public class MediaStarListViewCell extends ConstraintLayout implements UpdateModelAction {
    private ListView<EmbyMediaModel> listView = new ListView<>(getContext());
    private TextView titleLabel = null;
    private TextView viewMoreLabel = null;
    private LayoutParams titleLayout = null;
    private LayoutParams listLayout = null;
    public Consumer<ListItemClickEvent<MediaModel>> onClick;
    public MediaStarListViewCell(@NonNull Context context) {
        super(context);
        setupUI(context);
    }

    private void setupUI(Context context) {
        LayoutParams layout = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        setLayoutParams(layout);


        titleLabel = new TextView(context);
        titleLabel.setId(View.generateViewId());
        titleLabel.setTextSize(18);
        titleLabel.setGravity(Gravity.LEFT);
        titleLabel.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
        titleLabel.setTextColor(ContextCompat.getColor(context, R.color.onBackground_mediumContrast));
        titleLayout = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        titleLayout.topToTop = LayoutParams.PARENT_ID;
        titleLayout.leftToLeft = LayoutParams.PARENT_ID;
        titleLayout.rightToRight = LayoutParams.PARENT_ID;
        titleLayout.topMargin = 10;
        titleLayout.leftMargin = (int) (10 * DeviceUtil.density);
        titleLabel.setLayoutParams(titleLayout);
        addView(titleLabel);

        viewMoreLabel = new TextView(context);
        viewMoreLabel.setText(R.string.view_mode);
        viewMoreLabel.setId(View.generateViewId());
        viewMoreLabel.setTextSize(14);
        viewMoreLabel.setGravity(Gravity.RIGHT);
        viewMoreLabel.setTextAlignment(TEXT_ALIGNMENT_VIEW_END);
        viewMoreLabel.setTextColor(ContextCompat.getColor(context, R.color.onBackground_mediumContrast));
        LayoutParams viewMoreLayout = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        viewMoreLayout.topToTop = titleLabel.getId();
        viewMoreLayout.rightToRight = LayoutParams.PARENT_ID;
        viewMoreLayout.topMargin = DeviceUtil.dpToPx(5);
        viewMoreLayout.rightMargin = DeviceUtil.dpToPx(10);
        viewMoreLabel.setLayoutParams(viewMoreLayout);
        addView(viewMoreLabel);
        if (DeviceUtil.isTV) {
            viewMoreLabel.setFocusable(true);
            viewMoreLabel.setOnFocusChangeListener((v, hasFocus) -> {
                v.setScaleX(hasFocus ? 1.2f : 1.0f);
                v.setScaleY(hasFocus ? 1.2f : 1.0f);
                if (v instanceof TextView text) {
                    text.setTextColor(hasFocus ? ContextCompat.getColor(context, R.color.primaryContainer) : ContextCompat.getColor(context, R.color.onBackground));
                }
            });
        }

        listView.viewModel.viewCell = MediaViewCell.class;
        listView.setHasFixedSize(true);
        listView.setCacheSize(15);
        listLayout = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        listLayout.topToBottom = titleLabel.getId();
        listLayout.leftToLeft = LayoutParams.PARENT_ID;
        listLayout.rightToRight = LayoutParams.PARENT_ID;
        listLayout.bottomToBottom = LayoutParams.PARENT_ID;
        listLayout.topMargin = 20;
        listLayout.bottomMargin = 30;
        addView(listView, listLayout);

        if (!DeviceUtil.isTV) {
            return;
        }
        setOnFocusChangeListener((v, hasFocus) -> {
            listView.requestFocus();
        });
    }

    @Override
    public <T> void updateModel(T model) {
        if (model instanceof MediaStarModel starModel) {
            MediaStarModel data = (MediaStarModel) model;
            if (data.getName() != null) {
                titleLabel.setText(data.getName());
                titleLayout.topMargin = 10;
                titleLabel.setVisibility(VISIBLE);
                viewMoreLabel.setVisibility(VISIBLE);
                titleLabel.setLayoutParams(titleLayout);
                listLayout.topMargin = 20;
                listView.setLayoutParams(listLayout);
            } else {
                titleLayout.topMargin = 0;
                titleLabel.setVisibility(GONE);
                viewMoreLabel.setVisibility(GONE);
                titleLabel.setLayoutParams(titleLayout);
                listLayout.topMargin = 0;
                listView.setLayoutParams(listLayout);
            }
            listView.viewModel.items = data.getMedias();
            int width = DeviceUtil.dpToPx( starModel.getType() == MediaType.Episode ? 174 : 111);
            int spanCount = DeviceUtil.screenSize(getContext()).getWidth() / width;
            listView.listView.setLayoutManager(new GridLayoutManager(getContext(), spanCount));
            listView.viewModel.onClick = (event) -> {
                if (event == null) return;
                log.info("event: {}", event);
                val args = new HashMap<String, Object>();
                val m = event.getModel();
                args.put("id", m.getId());
                args.put("title", m.getName());
                final var options = NavigationUtil.getNavOptions();
                int dstId = m instanceof EmbyMediaModel ? R.id.mediaPage : R.id.albumPage;
                if (m instanceof EmbyMediaModel) {
                    val media = (EmbyMediaModel) m;
                    val isSeason = media.getType().equals("Season");
                    if (isSeason) {
                        dstId = R.id.episodePage;
                        args.put("seriesId", media.getSeriesId());
                        args.put("seasonId", media.getId());
                    }
                }
                XGET(Navigator.class).pushPage(dstId, args);
            };
            post(() -> listView.reloadData());

            viewMoreLabel.setOnClickListener(v -> {
                val args = new HashMap<String, Object>();
                args.put("type", data.getType().name());
                args.put("title", data.getName());
                XGET(Navigator.class).pushPage(R.id.albumPage, args);
            });
        }
    }
}
