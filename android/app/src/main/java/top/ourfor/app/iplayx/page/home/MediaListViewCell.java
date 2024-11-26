package top.ourfor.app.iplayx.page.home;

import static top.ourfor.app.iplayx.module.Bean.XGET;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import top.ourfor.app.iplayx.R;
import top.ourfor.app.iplayx.bean.Navigator;
import top.ourfor.app.iplayx.common.model.MediaModel;
import top.ourfor.app.iplayx.action.UpdateModelAction;
import top.ourfor.app.iplayx.common.type.MediaType;
import top.ourfor.app.iplayx.model.EmbyAlbumModel;
import top.ourfor.app.iplayx.module.FontModule;
import top.ourfor.app.iplayx.util.DeviceUtil;
import top.ourfor.app.iplayx.model.EmbyAlbumMediaModel;
import top.ourfor.app.iplayx.model.EmbyMediaModel;
import top.ourfor.app.iplayx.view.ListItemClickEvent;
import top.ourfor.app.iplayx.view.ListView;
import top.ourfor.app.iplayx.view.infra.TextView;

@Slf4j
public class MediaListViewCell extends ConstraintLayout implements UpdateModelAction {
    private ListView<MediaModel> listView = new ListView(getContext());
    private TextView titleLabel = null;
    private TextView viewMoreLabel = null;
    private LayoutParams titleLayout = null;
    private LayoutParams listLayout = null;
    private static Typeface themeFont = XGET(FontModule.class).getThemeFont();
    public Consumer<ListItemClickEvent<MediaModel>> onClick;
    public MediaListViewCell(@NonNull Context context) {
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
        titleLabel.setTypeface(themeFont, Typeface.BOLD);
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
        LayoutParams viewMoreLayout = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        viewMoreLayout.topToTop = titleLabel.getId();
        viewMoreLayout.rightToRight = LayoutParams.PARENT_ID;
        viewMoreLayout.topMargin = DeviceUtil.dpToPx(5);
        viewMoreLayout.rightMargin = DeviceUtil.dpToPx(10);
        viewMoreLabel.setLayoutParams(viewMoreLayout);
        addView(viewMoreLabel);

        listView.viewModel.viewCell = MediaViewCell.class;
        listView.setHasFixedSize(true);
        listView.setCacheSize(16);
        listView.listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
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
        if (model instanceof EmbyAlbumMediaModel<?>) {
            EmbyAlbumMediaModel<?> data = (EmbyAlbumMediaModel<?>) model;
            if (data.getAlbum() != null) {
                titleLabel.setText(data.getAlbum().getName());
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
            val title = data.getTitle();
            if (title != null) {
                titleLabel.setText(title);
                titleLabel.setVisibility(VISIBLE);
            }
            listView.viewModel.onClick = (event) -> {
                if (event == null) return;
                log.info("event: {}", event);
                val route = XGET(Navigator.class);
                val args = new HashMap<String, Object>();
                val m = event.getModel();
                args.put("id", m.getId());
                args.put("title", m.getName());
                boolean isMedia = m instanceof EmbyMediaModel;
                EmbyMediaModel media = isMedia ? (EmbyMediaModel) m : null;
                int dstId = m instanceof EmbyMediaModel ? R.id.mediaPage : R.id.albumPage;
                if (media != null) {
                    if (media.isMusicAlbum()) {
                        dstId = R.id.musicPage;
                    } else if (media.isAudio()) {
                        dstId = R.id.musicPlayerPage;
                    }
                }
                if (m instanceof EmbyAlbumModel album && album.isMusic()) {
                    args.put("type", MediaType.MusicAlbum.name());
                }
                route.pushPage(dstId, args);
            };

            listView.resetItems((List<MediaModel>)data.getMedias());

            viewMoreLabel.setOnClickListener(v -> {
                val route = XGET(Navigator.class);
                val args = new HashMap<String, Object>();
                args.put("id", data.getAlbum().getId());
                args.put("title", data.getAlbum().getName());
                if (data.getAlbum().isMusic()) {
                    args.put("type", MediaType.MusicAlbum.name());
                }
                route.pushPage(R.id.albumPage, args);
            });
        }
    }
}