package top.ourfor.app.iplay.page.star;

import static top.ourfor.app.iplay.module.Bean.XGET;
import static top.ourfor.app.iplay.module.Bean.XWATCH;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.view.MenuItemCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import top.ourfor.app.iplay.R;
import top.ourfor.app.iplay.action.AnimationAction;
import top.ourfor.app.iplay.action.SiteUpdateAction;
import top.ourfor.app.iplay.action.ThemeUpdateAction;
import top.ourfor.app.iplay.common.annotation.ViewController;
import top.ourfor.app.iplay.databinding.StarPageBinding;
import top.ourfor.app.iplay.page.Page;
import top.ourfor.app.iplay.util.DeviceUtil;
import top.ourfor.app.iplay.store.IAppStore;
import top.ourfor.app.iplay.util.WindowUtil;
import top.ourfor.app.iplay.view.ListView;
import top.ourfor.app.iplay.view.infra.Toolbar;
import top.ourfor.app.iplay.view.infra.ToolbarAction;

@Slf4j
@ViewController(name = "star_page")
public class StarPage implements SiteUpdateAction, ThemeUpdateAction, Page {
    private ViewGroup contentView = null;
    private ListView<MediaStarModel> listView = null;
    private SwipeRefreshLayout swipeRefreshLayout = null;

    @Getter
    Context context;
    IAppStore store;
    StarViewModel viewModel;
    StarPageBinding binding;
    AnimationAction activityIndicator;

    public void init() {
        binding = StarPageBinding.inflate(LayoutInflater.from(context));
        val view = binding.getRoot();
        store = XGET(IAppStore.class);
        viewModel = new StarViewModel(store);
        viewModel.getIsLoading().observe(view, isLoading -> {
            if (activityIndicator == null) return;
            if (isLoading) {
                activityIndicator.setVisibility(View.VISIBLE);
                activityIndicator.playAnimation();
            } else {
                activityIndicator.setVisibility(View.GONE);
                activityIndicator.cancelAnimation();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        viewModel.getStarItems().observe(view, starItems -> {
            if (listView == null) return;
            listView.setItems(starItems);
        });
    }

    @Override
    public void viewDidAppear() {
        val toolbar = XGET(Toolbar.class);
        toolbar.inflateMenu(R.menu.star_left_menu, ToolbarAction.Position.Left);
        val animationItem = toolbar.getLeftMenu().findItem(R.id.loading);
        val actionProvider = MenuItemCompat.getActionProvider(animationItem);
        activityIndicator = (AnimationAction)actionProvider;
        activityIndicator.setVisibility(View.GONE);
    }

    public void setup() {
        contentView = binding.getRoot();
        setupUI(context);
        bind();
        contentView.setPadding(0, WindowUtil.defaultToolbarBottom, 0, 0);
    }

    void setupUI(Context context) {
        listView = binding.starListView;
        listView.viewModel.viewCell = MediaStarListViewCell.class;
        listView.setEmptyTipVisible(View.VISIBLE);
        swipeRefreshLayout = binding.swipeRefresh;
        if (!DeviceUtil.isTV) {
            swipeRefreshLayout.setOnRefreshListener(() -> {
                XGET(ThreadPoolExecutor.class).execute(this::onSiteUpdate);
            });
        }
    }

    void bind() {
        XWATCH(ThemeUpdateAction.class, this);
        XWATCH(SiteUpdateAction.class, this);
        XGET(ThreadPoolExecutor.class).execute(viewModel::fetchStarData);
    }

    @Override
    public void onSiteUpdate() {
        if (!store.hasValidSite()) {
            viewModel.getIsLoading().postValue(false);
            return;
        }
        viewModel.getIsLoading().postValue(true);
        val pool = XGET(ThreadPoolExecutor.class);
        pool.submit(viewModel::fetchStarData);
    }

    @Override
    public void themeDidUpdate() {
        contentView = null;
    }

    @Override
    public void create(Context context, Map<String, Object> params) {
        this.context = context;
        init();
        setup();
    }

    @Override
    public void destroy() {
        binding = null;
    }

    @Override
    public View view() {
        return this.binding.getRoot();
    }
}
