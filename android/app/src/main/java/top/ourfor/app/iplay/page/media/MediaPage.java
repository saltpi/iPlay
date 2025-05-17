package top.ourfor.app.iplay.page.media;

import static top.ourfor.app.iplay.module.Bean.XGET;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams;

import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import top.ourfor.app.iplay.R;
import top.ourfor.app.iplay.action.DispatchAction;
import top.ourfor.app.iplay.action.NavigationTitleBar;
import top.ourfor.app.iplay.bean.INavigator;
import top.ourfor.app.iplay.common.annotation.ViewController;
import top.ourfor.app.iplay.common.model.ColorScheme;
import top.ourfor.app.iplay.common.model.IMediaModel;
import top.ourfor.app.iplay.common.type.MediaType;
import top.ourfor.app.iplay.databinding.MediaPageBinding;
import top.ourfor.app.iplay.model.ActorModel;
import top.ourfor.app.iplay.model.MediaModel;
import top.ourfor.app.iplay.module.GlideApp;
import top.ourfor.app.iplay.page.Page;
import top.ourfor.app.iplay.page.home.MediaViewCell;
import top.ourfor.app.iplay.store.IAppStore;
import top.ourfor.app.iplay.util.DeviceUtil;
import top.ourfor.app.iplay.util.LayoutUtil;
import top.ourfor.app.iplay.util.WindowUtil;
import top.ourfor.app.iplay.view.ActorCellView;
import top.ourfor.app.iplay.view.LinearLayoutManager;
import top.ourfor.app.iplay.view.ListView;
import top.ourfor.app.iplay.view.TagView;
import top.ourfor.app.iplay.view.infra.Toolbar;
import top.ourfor.app.iplay.view.infra.ToolbarAction;

@Slf4j
@ViewController(name = "media_page")
public class MediaPage implements Page {
    String title;
    String id;
    IMediaModel model;
    ListView<ActorModel> actorList;
    ListView<MediaModel> similarList;

    @Getter
    Context context;
    Map<String, Object> params;
    MediaPageBinding binding;
    IAppStore store;
    MediaViewModel viewModel;


    public void init() {
        binding = MediaPageBinding.inflate(LayoutInflater.from(context), null, false);
        store = XGET(IAppStore.class);
        viewModel = new MediaViewModel(store);
        val view = binding.getRoot();
        viewModel.getMedia().observe(view, detail -> {
            if (binding == null || detail == null) return;
            model = detail;
            binding.overviewLabel.setText(detail.getOverview());
            view.post(this::showTagList);
            view.post(this::showActorList);
        });
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


    public void setup() {
        title = params.getOrDefault("title", "").toString();
        id = params.getOrDefault("id", "").toString();
        model = store.getDataSource().getMediaMap().get(id);
        if (model instanceof MediaModel episode && episode.isEpisode()) {
            title = episode.getSeriesName();
        }
        XGET(NavigationTitleBar.class).setNavTitle(title);
        setupUI(getContext());
        bind();
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
                if (model instanceof MediaModel media) {
                    val favorite = media.getUserData().isFavorite();
                    store.markFavorite(media.getId(), !favorite, obj -> {
                        if (obj == null) {
                            return;
                        }
                        media.setUserData(obj.toUserDataModel());
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
        if (model instanceof MediaModel media) {
            if (media.getUserData() != null) {
                val isFavorite = media.getUserData().isFavorite();
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
        if (model instanceof MediaModel media) {
            backdrop = media.isEpisode() ? media.getImage().getPrimary() : media.getImage().getBackdrop();
        }
        GlideApp.with(context)
                .load(backdrop)
                .into(binding.posterImage);

        if (model instanceof MediaModel media &&
                (media.getType().equals("Movie") ||
                media.getType().equals("Episode"))) {
            binding.watchButton.setVisibility(View.VISIBLE);
            binding.watchButton.setOnClickListener(v -> {
                val args = new HashMap<String, Object>();
                args.put("id", model.getId());
                XGET(INavigator.class).pushPage(R.id.playerPage, args);
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

        showTagList();

        if (model instanceof MediaModel media && ( media.isSeries() || media.isEpisode())) {
            store.getSeasons(media.isSeries() ? media.getId() : media.getSeriesId() , seasons -> {
                if (seasons == null) return;
                viewModel.getSeasons().postValue(seasons);
            });
            if (media.isEpisode()) {
                binding.episodeLabel.setVisibility(View.VISIBLE);
                binding.episodeLabel.setText(media.getName());
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

        showActorList();

        var isPlayable = model instanceof MediaModel media && (media.isEpisode() || media.isMovie());
        binding.playerConfig.setVisibility(isPlayable ? View.VISIBLE : View.GONE);
        binding.playerConfig.setOnClickListener(v -> {
            showPlayConfigPanel();
        });
        viewModel.fetchDetail(model.getId());
    }

    void showPlayConfigPanel() {
        var dialog = new BottomSheetDialog(getContext(), R.style.SiteBottomSheetDialog);
        dialog.setOnDismissListener(dlg -> { });
        var view = new PlayerConfigPanelView(context, (MediaModel) model);
        view.setOnPlayButtonClick(v -> dialog.dismiss());
        dialog.setContentView(view);
        var behavior = BottomSheetBehavior.from((View) view.getParent());
        val height = (int) (DeviceUtil.screenSize(getContext()).getHeight() * 0.6);
        behavior.setPeekHeight(height);
        dialog.show();
    }

    void showTagList() {
        binding.tagList.removeAllViews();
        if (model instanceof MediaModel media &&
                media.getGenres() != null &&
                !media.getGenres().isEmpty()) {
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
    }

    void showSimilarList() {
        binding.similarList.removeAllViews();
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
            XGET(INavigator.class).pushPage(dstId, args);
        };
        binding.similarList.addView(similarList, LayoutUtil.fit());
        viewModel.fetchSimilar(id);
    }

    void showActorList() {
        binding.actorList.removeAllViews();
        if (model instanceof MediaModel media && media.getActors() != null && !media.getActors().isEmpty()) {
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
                XGET(INavigator.class).pushPage(R.id.albumPage, args);
            };
            binding.actorList.addView(actorList);
            binding.actorList.requestLayout();
        } else {
            binding.actorList.setVisibility(View.GONE);
            binding.actorLabel.setVisibility(View.GONE);
        }
    }

    @Override
    public void create(Context context, Map<String, Object> params) {
        this.context = context;
        this.params = params;
        init();
        setup();
    }

    @Override
    public void destroy() {
        binding = null;
    }

    @Override
    public View view() {
        return binding.getRoot();
    }
}
