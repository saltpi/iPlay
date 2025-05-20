package top.ourfor.app.iplay.page.home;

import static top.ourfor.app.iplay.module.Bean.XGET;

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
import top.ourfor.app.iplay.R;
import top.ourfor.app.iplay.api.emby.EmbyModel;
import top.ourfor.app.iplay.bean.INavigator;
import top.ourfor.app.iplay.common.model.IMediaModel;
import top.ourfor.app.iplay.action.UpdateModelAction;
import top.ourfor.app.iplay.common.type.MediaType;
import top.ourfor.app.iplay.model.AlbumModel;
import top.ourfor.app.iplay.model.MediaModel;
import top.ourfor.app.iplay.module.FontModule;
import top.ourfor.app.iplay.util.DeviceUtil;
import top.ourfor.app.iplay.view.ListItemClickEvent;
import top.ourfor.app.iplay.view.ListView;
import top.ourfor.app.iplay.view.infra.TextView;

@Slf4j
@SuppressWarnings({"rawtypes", "unchecked"})
public class MediaListViewCell extends ConstraintLayout implements UpdateModelAction {
    ListView<Object> listView = new ListView(getContext());
    private TextView titleLabel = null;
    private TextView viewMoreLabel = null;
    private LayoutParams titleLayout = null;
    private LayoutParams listLayout = null;
    private static Typeface themeFont = null;
    public Consumer<ListItemClickEvent<IMediaModel>> onClick;
    public MediaListViewCell(@NonNull Context context) {
        super(context);
        setupUI(context);
        if (themeFont == null) {
            themeFont = XGET(FontModule.class).getThemeFont();
        }
    }

    private void setupUI(Context context) {
        LayoutParams layout = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        setLayoutParams(layout);

        titleLabel = new TextView(context);
        titleLabel.setId(View.generateViewId());
        titleLabel.setSingleLine(true);
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
        if (!(model instanceof EmbyModel.EmbyAlbumMediaModel<?> data)) {
            return;
        }

        if (data.getAlbum() != null) {
            titleLabel.setText(data.getAlbum().getTitle());
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
            val route = XGET(INavigator.class);
            val args = new HashMap<String, Object>();
            val m = event.getModel();
            String id = null;
            String name = null;
            if (m instanceof AlbumModel album) {
                id = album.getId();
                name = album.getTitle();
            } else if (m instanceof MediaModel media) {
                id = media.getId();
                name = media.getName();
            }
            args.put("id", id);
            args.put("title", name);
            boolean isMedia = m instanceof MediaModel;
            var media = isMedia ? (MediaModel) m : null;
            int dstId = m instanceof MediaModel ? R.id.mediaPage : R.id.albumPage;
            if (media != null) {
                if (media.isMusicAlbum()) {
                    dstId = R.id.musicPage;
                } else if (media.isAudio()) {
                    dstId = R.id.musicPlayerPage;
                }
            }
            if (m instanceof AlbumModel album && album.isMusic()) {
                args.put("type", MediaType.MusicAlbum.name());
            }
            route.pushPage(dstId, args);
        };

        val medias = (List)data.getMedias();
        listView.resetItems(medias);

        viewMoreLabel.setOnClickListener(v -> {
            val route = XGET(INavigator.class);
            val args = new HashMap<String, Object>();
            args.put("id", data.getAlbum().getId());
            args.put("title", data.getAlbum().getTitle());
            if (data.getAlbum().isMusic()) {
                args.put("type", MediaType.MusicAlbum.name());
            }
            route.pushPage(R.id.albumPage, args);
        });
    }
}