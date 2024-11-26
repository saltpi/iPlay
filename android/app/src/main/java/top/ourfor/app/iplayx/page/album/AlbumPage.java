package top.ourfor.app.iplayx.page.album;

import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE;
import static top.ourfor.app.iplayx.module.Bean.XGET;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadPoolExecutor;

import lombok.Getter;
import lombok.val;
import top.ourfor.app.iplayx.R;
import top.ourfor.app.iplayx.action.NavigationTitleBar;
import top.ourfor.app.iplayx.bean.Navigator;
import top.ourfor.app.iplayx.common.type.MediaLayoutType;
import top.ourfor.app.iplayx.common.type.MediaType;
import top.ourfor.app.iplayx.common.type.SortType;
import top.ourfor.app.iplayx.databinding.AlbumPageBinding;
import top.ourfor.app.iplayx.page.Page;
import top.ourfor.app.iplayx.util.AnimationUtil;
import top.ourfor.app.iplayx.util.DeviceUtil;
import top.ourfor.app.iplayx.model.EmbyMediaModel;
import top.ourfor.app.iplayx.store.GlobalStore;
import top.ourfor.app.iplayx.util.WindowUtil;
import top.ourfor.app.iplayx.view.GridLayoutManager;
import top.ourfor.app.iplayx.view.ListView;
import top.ourfor.app.iplayx.view.infra.Toolbar;
import top.ourfor.app.iplayx.view.infra.ToolbarAction;

public class AlbumPage implements Page {
    private AlbumPageBinding binding = null;
    private ListView<EmbyMediaModel> listView = null;
    private LottieAnimationView activityIndicator = null;
    private SwipeRefreshLayout swipeRefreshLayout = null;
    private String id = null;
    private String title = null;
    private MediaType type = MediaType.None;
    private SortType sortType = SortType.Name;
    private boolean isStale = true;

    @Getter
    Context context;
    GlobalStore store;
    AlbumViewModel viewModel;
    HashMap<String, Object> params;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        binding = AlbumPageBinding.inflate(LayoutInflater.from(context), null, false);
        val args = params;
        id = args.getOrDefault("id", "").toString();
        title = args.getOrDefault("title", "").toString();
        type = MediaType.valueOf(args.getOrDefault("type", MediaType.None.name()).toString());
        isStale = true;
        val view = binding.getRoot();
        viewModel = new AlbumViewModel();
        viewModel.getMedias().observe(view, medias -> {
            if (listView == null) return;
            listView.setItems(medias);
        });
    }

    @Override
    public void viewDidAppear() {
        val toolbar = XGET(Toolbar.class);
        toolbar.inflateMenu(R.menu.album_menu, ToolbarAction.Position.Right);
        toolbar.setOnMenuItemClickListener(item -> {
            val itemId = item.getItemId();
            if (itemId == R.id.refresh) {
                activityIndicator.post(() -> {
                    activityIndicator.setVisibility(View.VISIBLE);
                });
                XGET(ThreadPoolExecutor.class).submit(this::onRefresh);
            } else if (itemId == R.id.sort_by_date_add) {
                val items = viewModel.getMedias().getValue();
                Comparator<EmbyMediaModel> comparator = Comparator.comparing(EmbyMediaModel::getDateCreated);
                if (sortType == SortType.DateAdded) {
                    sortType = SortType.DateAddedReverse;
                    items.sort(comparator.reversed());
                } else {
                    sortType = SortType.DateAdded;
                    items.sort(comparator);
                }
                viewModel.getMedias().postValue(items);
            } else if (itemId == R.id.sort_by_date_release) {
                val items = viewModel.getMedias().getValue();
                Comparator<EmbyMediaModel> comparator = Comparator.comparing(EmbyMediaModel::getProductionYear);
                if (sortType == SortType.DateReleased) {
                    sortType = SortType.DateReleasedReverse;
                    items.sort(comparator.reversed());
                } else {
                    sortType = SortType.DateReleased;
                    items.sort(comparator);
                }
                viewModel.getMedias().postValue(items);
            } else if (itemId == R.id.sort_by_name) {
                val items = viewModel.getMedias().getValue();
                Comparator<EmbyMediaModel> comparator = Comparator.comparing(EmbyMediaModel::getName);
                if (sortType == SortType.Name) {
                    sortType = SortType.NameReverse;
                    items.sort(comparator.reversed());
                } else {
                    sortType = SortType.Name;
                    items.sort(comparator);
                }
                viewModel.getMedias().postValue(items);
            }
            return true;
        });
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setupUI(getContext());
        bind();
        return binding.getRoot();
    }

    public void onDestroyView() {
        binding = null;
    }

    void setupUI(Context context) {
        listView = binding.mediaList;
        swipeRefreshLayout = binding.swipeRefresh;
        listView.viewModel.viewCell = MediaGridCell.class;
        if (DeviceUtil.isTV) {
        } else {
            swipeRefreshLayout.setOnRefreshListener(() -> {
                XGET(ThreadPoolExecutor.class).submit(this::onRefresh);
            });
        }
        activityIndicator = AnimationUtil.createActivityIndicate(context);
        binding.getRoot().addView(activityIndicator);
        binding.getRoot().setPadding(0, WindowUtil.defaultToolbarBottom, 0, 0);
        listView.listView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == SCROLL_STATE_IDLE) {
                    var verticalOffset = recyclerView.computeVerticalScrollOffset();
                    binding.scrollTop.setVisibility(verticalOffset > 0 ? View.VISIBLE : View.GONE);
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        binding.scrollTop.setVisibility(View.GONE);
        binding.scrollTop.setOnClickListener(v -> {
            listView.listView.smoothScrollToPosition(0);
        });
    }


    void bind() {
        XGET(NavigationTitleBar.class).setNavTitle(title);
        listView.setHasFixedSize(true);
        listView.setCacheSize(15);
        int spanCount = DeviceUtil.screenSize(getContext()).getWidth() / DeviceUtil.dpToPx(120);
        listView.listView.setLayoutManager(new GridLayoutManager(getContext(), spanCount));
        listView.viewModel.onClick = e -> {
            val model = e.getModel();
            val bundle = new HashMap<String, Object>();
            bundle.put("id", model.getId());
            bundle.put("title", model.getName());
            val isSeason = model.getType().equals("Season");
            val isMusic = model.getType().equals("MusicAlbum");
            var dstId = R.id.mediaPage;
            if (isSeason) {
                bundle.put("seriesId", model.getSeriesId());
                bundle.put("seasonId", model.getId());
                dstId = R.id.episodePage;
            } else if (isMusic) {
                dstId = R.id.musicPage;
            }
            XGET(Navigator.class).pushPage(dstId, bundle);
        };


        if (isStale) {
            activityIndicator.post(() -> {
                activityIndicator.setVisibility(View.VISIBLE);
            });
        }
        XGET(ThreadPoolExecutor.class).submit(this::onRefresh);
    }

    void onRefresh() {
        assert store != null;
        if (type == MediaType.None) {
            val cached = store.getDataSource().getAlbumMedias().get(id);
            viewModel.getMedias().postValue(cached);
            if (!isStale) {
                stopRefresh();
                return;
            } else {
                isStale = false;
            }
            store.getAlbumAllMedias(id, medias -> {
                if (medias == null) {
                    stopRefresh();
                    return;
                }
                medias.forEach(media -> media.setLayoutType(MediaLayoutType.Poster));
                viewModel.getMedias().postValue(medias);
                stopRefresh();
            });
        } else if (type == MediaType.Episode ||
                type == MediaType.Movie ||
                type == MediaType.Series) {
            store.getAllFavoriteMedias(type, medias -> {
                if (medias == null) {
                    stopRefresh();
                    return;
                }
                medias.forEach(media -> {
                    media.setLayoutType(type == MediaType.Episode ? MediaLayoutType.Backdrop : MediaLayoutType.Poster);
                });
                viewModel.getMedias().postValue(medias);
                stopRefresh();
            });
        } else if (type == MediaType.Actor) {
            val cached = store.getDataSource().getAlbumMedias().get("Actor-" + id);
            viewModel.getMedias().postValue(cached);
            if (!isStale) {
                stopRefresh();
                return;
            } else {
                isStale = false;
            }
            store.getItems(Map.of("PersonIds", id, "IncludeItemTypes", "Movie,Series"), medias -> {
                if (medias == null) {
                    stopRefresh();
                    return;
                }
                medias.forEach(media -> media.setLayoutType(MediaLayoutType.Poster));
                viewModel.getMedias().postValue(medias);
                store.getDataSource().getAlbumMedias().put("Actor-" + id, new CopyOnWriteArrayList<>(medias));
                stopRefresh();
            });
        } else if (type == MediaType.MusicAlbum) {
            store.getItems(Map.of("ParentId", id, "IncludeItemTypes", "MusicAlbum"), medias -> {
                if (medias == null) {
                    stopRefresh();
                    return;
                }
                medias.forEach(media -> media.setLayoutType(MediaLayoutType.Poster));
                viewModel.getMedias().postValue(medias);
                stopRefresh();
            });
        }

    }

    private void stopRefresh() {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.post(() -> {
                swipeRefreshLayout.setRefreshing(false);
            });
        }
        activityIndicator.post(() -> {
            activityIndicator.setVisibility(View.GONE);
        });
    }

    @Override
    public void create(Context context, Map<String, Object> params) {
        this.context = context;
        this.params = new HashMap<>(params);
        store = XGET(GlobalStore.class);
        onCreate(null);
        onCreateView(LayoutInflater.from(context), null, null);
    }

    @Override
    public View view() {
        return binding.getRoot();
    }
}
