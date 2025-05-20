package top.ourfor.app.iplay.page.album;

import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE;
import static top.ourfor.app.iplay.module.Bean.XGET;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadPoolExecutor;

import lombok.Getter;
import lombok.val;
import top.ourfor.app.iplay.R;
import top.ourfor.app.iplay.action.NavigationTitleBar;
import top.ourfor.app.iplay.bean.INavigator;
import top.ourfor.app.iplay.common.annotation.ViewController;
import top.ourfor.app.iplay.common.type.MediaLayoutType;
import top.ourfor.app.iplay.common.type.MediaType;
import top.ourfor.app.iplay.common.type.SortType;
import top.ourfor.app.iplay.databinding.AlbumPageBinding;
import top.ourfor.app.iplay.model.MediaModel;
import top.ourfor.app.iplay.page.Page;
import top.ourfor.app.iplay.util.AnimationUtil;
import top.ourfor.app.iplay.util.DeviceUtil;
import top.ourfor.app.iplay.store.IAppStore;
import top.ourfor.app.iplay.util.WindowUtil;
import top.ourfor.app.iplay.view.GridLayoutManager;
import top.ourfor.app.iplay.view.ListView;
import top.ourfor.app.iplay.view.infra.Toolbar;
import top.ourfor.app.iplay.view.infra.ToolbarAction;

@ViewController(name = "album_page")
public class AlbumPage implements Page {
    AlbumPageBinding binding;
    ListView<MediaModel> listView;
    private LottieAnimationView activityIndicator = null;
    private SwipeRefreshLayout swipeRefreshLayout = null;
    private String id = null;
    private String title = null;
    private MediaType type = MediaType.None;
    private SortType sortType = SortType.Name;
    private boolean isStale = true;

    @Getter
    Context context;
    IAppStore store;
    AlbumViewModel viewModel;
    HashMap<String, Object> params;

    public void init() {
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
            setupListLayout(medias);
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
                    listView.emptyTipView.setVisibility(View.GONE);
                    activityIndicator.setVisibility(View.VISIBLE);
                });
                XGET(ThreadPoolExecutor.class).submit(this::onRefresh);
            } else if (itemId == R.id.sort_by_date_add) {
                val items = viewModel.getMedias().getValue();
                var comparator = Comparator.comparing(MediaModel::getDateCreated);
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
                var comparator = Comparator.comparing(MediaModel::getProductionYear);
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
                var comparator = Comparator.comparing(MediaModel::getName);
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

    public void setup() {
        setupUI(context);
        bind();
    }

    public void onDestroyView() {
        binding = null;
    }

    @SuppressWarnings("unchecked")
    void setupUI(Context context) {
        listView = binding.mediaList;
        swipeRefreshLayout = binding.swipeRefresh;
        listView.viewModel.viewCell = MediaGridCell.class;
        if (!DeviceUtil.isTV) {
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
        listView.viewModel.onClick = e -> {
            val model = e.getModel();
            val bundle = new HashMap<String, Object>();
            bundle.put("id", model.getId());
            bundle.put("title", model.getName());
            val isSeason = model.getType().equals("Season");
            val isMusic = model.getType().equals("MusicAlbum");
            val isAlbum = model.isAlbum();
            var dstId = R.id.mediaPage;
            if (isSeason) {
                bundle.put("seriesId", model.getSeriesId());
                bundle.put("seasonId", model.getId());
                dstId = R.id.episodePage;
            } else if (isMusic) {
                dstId = R.id.musicPage;
            } else if (isAlbum) {
                dstId = R.id.albumPage;
            }
            XGET(INavigator.class).pushPage(dstId, bundle);
        };


        if (isStale) {
            activityIndicator.post(() -> {
                listView.emptyTipView.setVisibility(View.GONE);
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
                medias.forEach(media -> media.setLayoutType(media.getLayoutType() == MediaLayoutType.None ? MediaLayoutType.Poster : media.getLayoutType()));
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
                medias.forEach(media -> media.setLayoutType(media.getLayoutType() == MediaLayoutType.None ? MediaLayoutType.Poster : media.getLayoutType()));
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

    void setupListLayout(List<MediaModel> medias) {
        if (medias == null || medias.isEmpty()) return;
        listView.post(() -> {
            val media = medias.get(0);
            if (media == null) return;
            int spanCount = DeviceUtil.screenSize(getContext()).getWidth() / DeviceUtil.dpToPx(media.getLayoutType() == MediaLayoutType.Backdrop ? 160 : 120);
            listView.listView.setLayoutManager(new GridLayoutManager(getContext(), spanCount));
        });
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
        store = XGET(IAppStore.class);
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
