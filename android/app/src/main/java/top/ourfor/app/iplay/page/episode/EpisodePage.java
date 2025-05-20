package top.ourfor.app.iplay.page.episode;

import static top.ourfor.app.iplay.module.Bean.XGET;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.val;
import top.ourfor.app.iplay.R;
import top.ourfor.app.iplay.action.DispatchAction;
import top.ourfor.app.iplay.action.NavigationTitleBar;
import top.ourfor.app.iplay.api.emby.EmbyModel;
import top.ourfor.app.iplay.bean.INavigator;
import top.ourfor.app.iplay.common.annotation.ViewController;
import top.ourfor.app.iplay.databinding.EpisodePageBinding;
import top.ourfor.app.iplay.model.MediaModel;
import top.ourfor.app.iplay.page.Page;
import top.ourfor.app.iplay.util.AnimationUtil;
import top.ourfor.app.iplay.util.DeviceUtil;
import top.ourfor.app.iplay.store.IAppStore;
import top.ourfor.app.iplay.util.WindowUtil;
import top.ourfor.app.iplay.view.EpisodeCellView;
import top.ourfor.app.iplay.view.ListView;
import top.ourfor.app.iplay.view.infra.Toolbar;
import top.ourfor.app.iplay.view.infra.ToolbarAction;

@ViewController(name = "episode_page")
public class EpisodePage implements Page {
    private EpisodePageBinding binding = null;
    private ListView<MediaModel> episodeList = null;
    private LottieAnimationView activityIndicator = null;
    private MediaModel model = null;
    private String title = null;
    private String seriesId = null;
    private String seasonId = null;

    IAppStore store;
    EpisodeViewModel viewModel;
    HashMap<String, Object> params;
    @Getter
    Context context;

    
    public void init() {
        val args = params;
        title = args.getOrDefault("title", "").toString();
        seriesId = args.getOrDefault("seriesId", "").toString();
        seasonId = args.getOrDefault("seasonId", "").toString();
        binding = EpisodePageBinding.inflate(LayoutInflater.from(context));
        store = XGET(IAppStore.class);
        val view = binding.getRoot();
        viewModel = new EpisodeViewModel();
        if (seriesId == null) return;
        val seasons = store.getDataSource().getSeriesSeasons().get(seriesId);
        if (seasons == null) return;
        seasons.forEach(e -> {
            if (e.getId().equals(seasonId)) {
                model = e;
            }
        });

        viewModel.getEpisodes().observe(view, items -> {
            if (episodeList != null) {
                episodeList.setItems(items);
            }
        });
    }

    public void setup() {
        XGET(NavigationTitleBar.class).setNavTitle(title);
        setupUI(context);
        bind();
        binding.getRoot().setPadding(0, WindowUtil.defaultToolbarBottom, 0, 0);
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
                val media = model;
                val favorite = media.getUserData().isFavorite();
                store.markFavorite(media.getId(), !favorite, obj -> {
                    if (!(obj instanceof EmbyModel.EmbyUserData)) {
                        return;
                    }
                    val userData = (EmbyModel.EmbyUserData) obj;
                    media.setUserData(userData.toUserDataModel());
                    updateFavoriteState();
                });
                updateFavoriteState();
            }
            return true;
        });

        updateFavoriteState();
    }

    void setupUI(Context context) {
        episodeList = binding.episodeList;
        episodeList.viewModel.viewCell = EpisodeCellView.class;
        activityIndicator = AnimationUtil.createActivityIndicate(getContext());
        binding.getRoot().addView(activityIndicator);
    }

    private void updateFavoriteState() {
        val media = model;
        if (media != null && media.getUserData() != null) {
            val isFavorite = media.getUserData().isFavorite();
            int resId = isFavorite ? R.drawable.favorite_off : R.drawable.favorite_on;
            if (DeviceUtil.isTV) {
                return;
            }
            val toolbar = XGET(Toolbar.class);
            XGET(DispatchAction.class).runOnUiThread(() -> {
                toolbar.getRightMenu().getItem(0).setIcon(resId);
            });
        }
    }

    void bind() {
        episodeList.viewModel.onClick = e -> {
            val model = e.getModel();
            val args = new HashMap<String, Object>();
            args.put("id", model.getId());
            XGET(INavigator.class).pushPage(R.id.playerPage, args);
        };

        val seasonEpisodes = store.getDataSource().getSeasonEpisodes();
        if (seasonEpisodes != null && seasonEpisodes.get(seasonId) != null) {
            val newItems = seasonEpisodes.get(seasonId);
            viewModel.getEpisodes().postValue(newItems);
        }

        activityIndicator.post(() -> {
            episodeList.emptyTipView.setVisibility(View.GONE);
            activityIndicator.setVisibility(View.VISIBLE);
        });

        store.getEpisodes(seriesId, seasonId, result -> {
            activityIndicator.post(() -> {
                activityIndicator.setVisibility(View.GONE);
            });
            if (result == null) {
                return;
            }
            viewModel.getEpisodes().postValue(result);
        });
    }


    @Override
    public View view() {
        return binding.getRoot();
    }

    @Override
    public void create(Context context, Map<String, Object> params) {
        this.context = context;
        this.params = (HashMap<String, Object>) params;
        init();
        setup();
    }

    @Override
    public void destroy() {
        binding = null;
    }
}
