package top.ourfor.app.iplayx.page.episode;

import static top.ourfor.app.iplayx.module.Bean.XGET;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.airbnb.lottie.LottieAnimationView;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.val;
import top.ourfor.app.iplayx.R;
import top.ourfor.app.iplayx.action.DispatchAction;
import top.ourfor.app.iplayx.action.NavigationTitleBar;
import top.ourfor.app.iplayx.bean.Navigator;
import top.ourfor.app.iplayx.databinding.EpisodePageBinding;
import top.ourfor.app.iplayx.page.Page;
import top.ourfor.app.iplayx.util.AnimationUtil;
import top.ourfor.app.iplayx.util.DeviceUtil;
import top.ourfor.app.iplayx.model.EmbyMediaModel;
import top.ourfor.app.iplayx.model.EmbyUserData;
import top.ourfor.app.iplayx.store.GlobalStore;
import top.ourfor.app.iplayx.util.WindowUtil;
import top.ourfor.app.iplayx.view.EpisodeCellView;
import top.ourfor.app.iplayx.view.ListView;
import top.ourfor.app.iplayx.view.infra.Toolbar;
import top.ourfor.app.iplayx.view.infra.ToolbarAction;

public class EpisodePage implements Page {
    private EpisodePageBinding binding = null;
    private ListView<EmbyMediaModel> episodeList = null;
    private LottieAnimationView activityIndicator = null;
    private EmbyMediaModel model = null;
    private String title = null;
    private String seriesId = null;
    private String seasonId = null;

    GlobalStore store;
    EpisodeViewModel viewModel;
    HashMap<String, Object> params;
    @Getter
    Context context;

    
    public void onCreate(@Nullable Bundle savedInstanceState) {
        val args = params;
        title = args.getOrDefault("title", "").toString();
        seriesId = args.getOrDefault("seriesId", "").toString();
        seasonId = args.getOrDefault("seasonId", "").toString();
        binding = EpisodePageBinding.inflate(LayoutInflater.from(context));
        store = XGET(GlobalStore.class);
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

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        XGET(NavigationTitleBar.class).setNavTitle(title);
        setupUI(getContext());
        bind();
        binding.getRoot().setPadding(0, WindowUtil.defaultToolbarBottom, 0, 0);
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
                val media = model;
                val favorite = media.getUserData().getIsFavorite();
                store.markFavorite(media.getId(), !favorite, obj -> {
                    if (!(obj instanceof EmbyUserData)) {
                        return;
                    }
                    val userData = (EmbyUserData) obj;
                    media.setUserData(userData);
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
            val isFavorite = media.getUserData().getIsFavorite();
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
            XGET(Navigator.class).pushPage(R.id.playerPage, args);
        };

        val seasonEpisodes = store.getDataSource().getSeasonEpisodes();
        if (seasonEpisodes != null && seasonEpisodes.get(seasonId) != null) {
            val newItems = seasonEpisodes.get(seasonId);
            viewModel.getEpisodes().postValue(newItems);
        }

        activityIndicator.post(() -> {
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
        onCreate(null);
        onCreateView(LayoutInflater.from(context), null, null);
    }
}
