package top.ourfor.app.iplay.page.music;

import static top.ourfor.app.iplay.module.Bean.XGET;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams;

import com.airbnb.lottie.LottieAnimationView;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.val;
import top.ourfor.app.iplay.R;
import top.ourfor.app.iplay.action.DispatchAction;
import top.ourfor.app.iplay.action.NavigationTitleBar;
import top.ourfor.app.iplay.api.emby.EmbyModel;
import top.ourfor.app.iplay.common.annotation.ViewController;
import top.ourfor.app.iplay.databinding.EpisodePageBinding;
import top.ourfor.app.iplay.model.MediaModel;
import top.ourfor.app.iplay.page.Page;
import top.ourfor.app.iplay.store.IAppStore;
import top.ourfor.app.iplay.util.AnimationUtil;
import top.ourfor.app.iplay.util.DeviceUtil;
import top.ourfor.app.iplay.view.ListView;
import top.ourfor.app.iplay.view.infra.Toolbar;
import top.ourfor.app.iplay.view.infra.ToolbarAction;

@ViewController(name = "music_page")
public class MusicPage implements Page {
    private EpisodePageBinding binding = null;
    private ConstraintLayout contentView = null;
    private ListView<MediaModel> episodeList = null;
    private LottieAnimationView activityIndicator = null;
    private ImageView likeIcon = null;
    private MediaModel model = null;
    private String title = null;
    private String id = null;

    @Getter
    Context context;
    HashMap<String, Object> params;

    public void init() {
        val args = params;
        title = (String) args.getOrDefault("title", "");
        id = (String) args.getOrDefault("id", "");
    }

    public void setup() {
        binding = EpisodePageBinding.inflate(LayoutInflater.from(context), null, false);
        XGET(NavigationTitleBar.class).setNavTitle(title);
        setupUI(getContext());
        bind();
    }

    void setupUI(Context context) {
        contentView = binding.getRoot();
        episodeList = binding.episodeList;
        episodeList.viewModel.viewCell = MusicItemCellView.class;
        val layout = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        episodeList.setLayoutParams(layout);

        activityIndicator = AnimationUtil.createActivityIndicate(getContext());
        contentView.addView(activityIndicator);

        if (DeviceUtil.isTV) {
            return;
        }

    }

    private void updateFavoriteState() {
        val media = model;
        if (media.getUserData() != null) {
            val isFavorite = media.getUserData().isFavorite();
            int resId = isFavorite ? R.drawable.favorite_off : R.drawable.favorite_on;
            val toolbar = XGET(Toolbar.class);
            XGET(DispatchAction.class).runOnUiThread(() -> {
                toolbar.getMenu().getItem(0).setIcon(resId);
            });
        }
    }

    void bind() {
        episodeList.viewModel.onClick = e -> {
            val model = e.getModel();
            val args = new HashMap<String, Object>();
            args.put("id", model.getId());
//            XGET(Navigator.class).pushPage(R.id.musicPlayerPage, args);
        };

        val store = XGET(IAppStore.class);
        if (id == null) return;
        activityIndicator.post(() -> {
            activityIndicator.setVisibility(View.VISIBLE);
        });
        store.getItems(Map.of("ParentId", id), (medias) -> {
            episodeList.setItems(medias);
            activityIndicator.post(() -> {
                activityIndicator.setVisibility(View.GONE);
            });
        });



    }

    @Override
    public void viewDidAppear() {
        val toolbar = XGET(Toolbar.class);
        toolbar.inflateMenu(R.menu.media_menu, ToolbarAction.Position.Right);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val menu = toolbar.getMenu();
            val size = menu.size();
            for (int i = 0; i < size; i++) {
                menu.getItem(i).setIcon(R.drawable.favorite_off);
            }
        }
        toolbar.setOnMenuItemClickListener(item -> {
            val itemId = item.getItemId();
            if (itemId == R.id.toggle_favorite) {
                val media = model;
                val store = XGET(IAppStore.class);
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
    }

    @Override
    public View view() {
        return binding.getRoot();
    }

    @Override
    public void create(Context context, Map<String, Object> params) {
        this.context = context;
        this.params = new HashMap<>(params);
        init();
        setup();
    }
}
