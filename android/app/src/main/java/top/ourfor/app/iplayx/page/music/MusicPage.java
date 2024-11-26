package top.ourfor.app.iplayx.page.music;

import static top.ourfor.app.iplayx.module.Bean.XGET;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams;
import androidx.fragment.app.Fragment;

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
import top.ourfor.app.iplayx.model.EmbyMediaModel;
import top.ourfor.app.iplayx.model.EmbyUserData;
import top.ourfor.app.iplayx.page.Page;
import top.ourfor.app.iplayx.store.GlobalStore;
import top.ourfor.app.iplayx.util.AnimationUtil;
import top.ourfor.app.iplayx.util.DeviceUtil;
import top.ourfor.app.iplayx.util.LayoutUtil;
import top.ourfor.app.iplayx.util.NavigationUtil;
import top.ourfor.app.iplayx.view.ListView;
import top.ourfor.app.iplayx.view.infra.Toolbar;
import top.ourfor.app.iplayx.view.infra.ToolbarAction;

public class MusicPage implements Page {
    private EpisodePageBinding binding = null;
    private ConstraintLayout contentView = null;
    private ListView<EmbyMediaModel> episodeList = null;
    private LottieAnimationView activityIndicator = null;
    private ImageView likeIcon = null;
    private EmbyMediaModel model = null;
    private String title = null;
    private String id = null;

    @Getter
    Context context;
    HashMap<String, Object> params;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        val args = params;
        title = (String) args.getOrDefault("title", "");
        id = (String) args.getOrDefault("id", "");
    }

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = EpisodePageBinding.inflate(inflater, container, false);
        XGET(NavigationTitleBar.class).setNavTitle(title);
        setupUI(getContext());
        bind();
        return binding.getRoot();
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
            val isFavorite = media.getUserData().getIsFavorite();
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

        val store = XGET(GlobalStore.class);
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
                val store = XGET(GlobalStore.class);
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
    }

    @Override
    public View view() {
        return binding.getRoot();
    }

    @Override
    public void create(Context context, Map<String, Object> params) {
        this.context = context;
        this.params = new HashMap<>(params);
        onCreate(null);
        onCreateView(LayoutInflater.from(context), null, null);
    }
}
