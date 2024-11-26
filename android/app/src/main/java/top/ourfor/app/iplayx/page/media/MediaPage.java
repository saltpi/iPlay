package top.ourfor.app.iplayx.page.media;

import static top.ourfor.app.iplayx.module.Bean.XGET;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams;

import com.google.android.flexbox.FlexboxLayout;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import top.ourfor.app.iplayx.R;
import top.ourfor.app.iplayx.action.DispatchAction;
import top.ourfor.app.iplayx.action.NavigationTitleBar;
import top.ourfor.app.iplayx.bean.Navigator;
import top.ourfor.app.iplayx.common.model.ColorScheme;
import top.ourfor.app.iplayx.common.model.MediaModel;
import top.ourfor.app.iplayx.common.type.MediaType;
import top.ourfor.app.iplayx.databinding.MediaPageBinding;
import top.ourfor.app.iplayx.model.EmbyActorModel;
import top.ourfor.app.iplayx.model.EmbyMediaModel;
import top.ourfor.app.iplayx.model.EmbyUserData;
import top.ourfor.app.iplayx.module.GlideApp;
import top.ourfor.app.iplayx.page.Page;
import top.ourfor.app.iplayx.page.home.MediaViewCell;
import top.ourfor.app.iplayx.store.GlobalStore;
import top.ourfor.app.iplayx.util.DeviceUtil;
import top.ourfor.app.iplayx.util.LayoutUtil;
import top.ourfor.app.iplayx.util.NavigationUtil;
import top.ourfor.app.iplayx.util.WindowUtil;
import top.ourfor.app.iplayx.view.ActorCellView;
import top.ourfor.app.iplayx.view.LinearLayoutManager;
import top.ourfor.app.iplayx.view.ListView;
import top.ourfor.app.iplayx.view.TagView;
import top.ourfor.app.iplayx.view.infra.Toolbar;
import top.ourfor.app.iplayx.view.infra.ToolbarAction;

@Slf4j
public class MediaPage implements Page {
    private String title = null;
    private String id = null;
    private MediaModel model = null;
    private ListView<EmbyActorModel> actorList = null;
    private ListView<EmbyMediaModel> similarList = null;

    @Getter
    Context context;
    Map<String, Object> params;
    MediaPageBinding binding;
    GlobalStore store;
    MediaViewModel viewModel;


    public void onCreate(@Nullable Bundle savedInstanceState) {
        binding = MediaPageBinding.inflate(LayoutInflater.from(context), null, false);
        store = XGET(GlobalStore.class);
        viewModel = new MediaViewModel(store);
        val view = binding.getRoot();
        viewModel.getSeasons().observe(view, seasons -> {
            if (binding == null) return;
            val adapter = new SeasonPageAdapter();
            adapter.setContext(context);
            adapter.setSeasons(seasons);
            binding.seasonPager.setAdapter(adapter);
            binding.seasonTab.setupWithViewPager(binding.seasonPager);
            val firstTab = binding.seasonTab.getTabAt(0);
            firstTab.setText(R.string.season_title);
            for (int i = 0; i < seasons.size(); i++) {
                val tab = binding.seasonTab.getTabAt(i+1);
                tab.setText(seasons.get(i).getName());
            }
        });

        viewModel.getSimilar().observe(view, similar -> {
            if (similarList == null) return;
            similarList.setItems(similar);
        });
    }


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        title = params.getOrDefault("title", "").toString();
        id = params.getOrDefault("id", "").toString();
        model = store.getDataSource().getMediaMap().get(id);
        if (model instanceof EmbyMediaModel episode && episode.isEpisode()) {
            title = episode.getSeriesName();
        }
        XGET(NavigationTitleBar.class).setNavTitle(title);
        setupUI(getContext());
        bind();
        return binding.getRoot();
    }

    @Override
    public void viewDidAppear() {
        if (DeviceUtil.isTV) {
            return;
        }

        val toolbar = XGET(Toolbar.class);
        toolbar.inflateMenu(R.menu.media_menu, ToolbarAction.Position.Right);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val menu = toolbar.getRightMenu();
            val size = menu.size();
            for (int i = 0; i < size; i++) {
                menu.getItem(i).setIcon(R.drawable.favorite_off);
            }
        }
        toolbar.setOnMenuItemClickListener(item -> {
            val itemId = item.getItemId();
            if (itemId == R.id.toggle_favorite) {
                if (model instanceof EmbyMediaModel media) {
                    val favorite = media.getUserData().getIsFavorite();
                    store.markFavorite(media.getId(), !favorite, obj -> {
                        if (!(obj instanceof EmbyUserData)) {
                            return;
                        }
                        media.setUserData((EmbyUserData) obj);
                        updateFavoriteState();
                    });
                }
                updateFavoriteState();
            }
            return true;
        });

        updateFavoriteState();

        val seasons = viewModel.getSeasons().getValue();
        if (seasons != null && !seasons.isEmpty()) {
            binding.seasonTab.setupWithViewPager(binding.seasonPager);
            val firstTab = binding.seasonTab.getTabAt(0);
            firstTab.setText(R.string.season_title);
            for (int i = 0; i < seasons.size(); i++) {
                val tab = binding.seasonTab.getTabAt(i+1);
                tab.setText(seasons.get(i).getName());
            }
        }
    }

    void setupUI(Context context) {

    }

    private void updateFavoriteState() {
        if (model instanceof EmbyMediaModel media) {
            if (media.getUserData() != null) {
                val isFavorite = media.getUserData().getIsFavorite();
                int resId = isFavorite ? R.drawable.favorite_off : R.drawable.favorite_on;
                if (DeviceUtil.isTV) {
                    return;
                }
                val toolbar = XGET(Toolbar.class);
                XGET(DispatchAction.class).runOnUiThread(() -> {
                    val item = toolbar.getRightMenu().getItem(0);
                    if (item == null) return;
                    item.setIcon(resId);
                });
            }
        }
    }

    @SneakyThrows
    void bind() {
        if (model == null) return;

        var backdrop = model.getImage().getBackdrop();
        if (model instanceof EmbyMediaModel) {
            val media = (EmbyMediaModel)model;
            backdrop = media.isEpisode() ? media.getImage().getPrimary() : media.getImage().getBackdrop();
        }
        GlideApp.with(context)
                .load(backdrop)
                .into(binding.posterImage);

        if (model instanceof EmbyMediaModel &&
                ((EmbyMediaModel)model).getType().equals("Movie") ||
                ((EmbyMediaModel)model).getType().equals("Episode")) {
            binding.watchButton.setVisibility(View.VISIBLE);
            binding.watchButton.setOnClickListener(v -> {
                val args = new HashMap<String, Object>();
                args.put("id", model.getId());
                XGET(Navigator.class).pushPage(R.id.playerPage, args);
            });
            if (!DeviceUtil.isTV) {
                val layout = (ConstraintLayout.LayoutParams)binding.watchButton.getLayoutParams();
                layout.topMargin = WindowUtil.defaultToolbarBottom / 2;
                binding.watchButton.setLayoutParams(layout);
            }
        } else {
            binding.watchButton.setVisibility(View.GONE);
        }

        binding.overviewLabel.setText(model.getOverview());

        if (model instanceof EmbyMediaModel media &&
                ((EmbyMediaModel) model).getGenres() != null &&
                !((EmbyMediaModel) model).getGenres().isEmpty()) {
            val padding = DeviceUtil.dpToPx(5);
            val keys = ColorScheme.shared.getScheme().keySet().toArray();
            var idx = (int)(Math.random() * keys.length);
            for (val genre : media.getGenres()) {
                val textView = new TagView(getContext());
                val color = ColorScheme.shared.getScheme().get(keys[idx++%keys.length]);
                textView.setId(View.generateViewId());
                textView.setText(genre);
                textView.setColor(color);
                val layout = new FlexboxLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                layout.setFlexShrink(0);
                layout.setFlexGrow(0);
                layout.leftMargin = padding;
                layout.topMargin = padding;
                textView.setLayoutParams(layout);
                binding.tagList.addView(textView);
            }
        }

        if (model instanceof EmbyMediaModel media && ( media.isSeries() || media.isEpisode())) {
            store.getSeasons(media.isSeries() ? media.getId() : media.getSeriesId() , seasons -> {
                if (seasons == null) return;
                viewModel.getSeasons().postValue(seasons);
            });
            if (media.isEpisode()) {
                binding.episodeLabel.setVisibility(View.VISIBLE);
                binding.episodeLabel.setText(media.episodeName());
            } else {
                binding.episodeLabel.setVisibility(View.GONE);
            }
            binding.similarLabel.setVisibility(View.GONE);
            binding.similarList.setVisibility(View.GONE);
        } else {
            binding.episodeLabel.setVisibility(View.GONE);
            binding.seasonPager.setVisibility(View.GONE);
            binding.seasonTab.setVisibility(View.GONE);
            if (!DeviceUtil.isTV) {
                showSimilarList();
            } else {
                binding.similarLabel.setVisibility(View.GONE);
                binding.similarList.setVisibility(View.GONE);
            }
        }
        if (model instanceof EmbyMediaModel media && media.getActors() != null && media.getActors().size() > 0) {
            val layout = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            actorList = new ListView<>(getContext());
            actorList.viewModel.viewCell = ActorCellView.class;
            actorList.setItems(media.getActors());
            actorList.setLayoutParams(layout);
            actorList.listView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            actorList.viewModel.onClick = event -> {
                val actor = event.getModel();
                val actorId = actor.getId();
                val args = new HashMap<String, Object>();
                args.put("id", actorId);
                args.put("title", actor.getName());
                args.put("type", MediaType.Actor.name());
                XGET(Navigator.class).pushPage(R.id.albumPage, args);
            };
            binding.actorList.addView(actorList);
            binding.actorList.requestLayout();
        } else {
            binding.actorList.setVisibility(View.GONE);
            binding.actorLabel.setVisibility(View.GONE);
        }
    }

    void showSimilarList() {
        similarList = new ListView<>(getContext());
        similarList.viewModel.viewCell = MediaViewCell.class;
        similarList.listView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        similarList.viewModel.onClick = event -> {
            val model = event.getModel();
            val args = new HashMap<String, Object>();
            args.put("id", model.getId());
            args.put("title", model.getName());
            val isSeason = model.getType().equals("Season");
            var dstId = R.id.mediaPage;
            if (isSeason) {
                args.put("seriesId", model.getSeriesId());
                args.put("seasonId", model.getId());
                dstId = R.id.episodePage;
            }
            store.getDataSource().getMediaMap().put(model.getId(), model);
            XGET(Navigator.class).pushPage(dstId, args);
        };
        binding.similarList.addView(similarList, LayoutUtil.fit());
        viewModel.fetchSimilar(id);
    }

    @Override
    public void create(Context context, Map<String, Object> params) {
        this.context = context;
        this.params = params;
        onCreate(null);
        onCreateView(LayoutInflater.from(context), null, null);
    }

    @Override
    public View view() {
        return binding.getRoot();
    }
}
