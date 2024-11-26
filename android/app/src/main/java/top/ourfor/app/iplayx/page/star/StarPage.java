package top.ourfor.app.iplayx.page.star;

import static top.ourfor.app.iplayx.module.Bean.XGET;
import static top.ourfor.app.iplayx.module.Bean.XWATCH;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import dagger.hilt.android.AndroidEntryPoint;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import top.ourfor.app.iplayx.R;
import top.ourfor.app.iplayx.action.AnimationAction;
import top.ourfor.app.iplayx.action.SiteUpdateAction;
import top.ourfor.app.iplayx.action.ThemeUpdateAction;
import top.ourfor.app.iplayx.databinding.StarPageBinding;
import top.ourfor.app.iplayx.page.Page;
import top.ourfor.app.iplayx.util.DeviceUtil;
import top.ourfor.app.iplayx.store.GlobalStore;
import top.ourfor.app.iplayx.util.WindowUtil;
import top.ourfor.app.iplayx.view.ListView;
import top.ourfor.app.iplayx.view.infra.Toolbar;
import top.ourfor.app.iplayx.view.infra.ToolbarAction;

@Slf4j
public class StarPage implements SiteUpdateAction, ThemeUpdateAction, Page {
    private ViewGroup contentView = null;
    private ListView<MediaStarModel> listView = null;
    private SwipeRefreshLayout swipeRefreshLayout = null;

    @Getter
    Context context;
    GlobalStore store;
    StarViewModel viewModel;
    StarPageBinding binding;
    AnimationAction activityIndicator;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        binding = StarPageBinding.inflate(LayoutInflater.from(context));
        val view = binding.getRoot();
        store = XGET(GlobalStore.class);
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

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentView = binding.getRoot();
        setupUI(getContext());
        bind(savedInstanceState);
        contentView.setPadding(0, WindowUtil.defaultToolbarBottom, 0, 0);
        return contentView;
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

    void bind(Bundle savedInstanceState) {
        XWATCH(ThemeUpdateAction.class, this);
        XWATCH(SiteUpdateAction.class, this);
        if (savedInstanceState == null) {
            XGET(ThreadPoolExecutor.class).execute(viewModel::fetchStarData);
        }
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
        onCreate(null);
        onCreateView(LayoutInflater.from(context), null, null);
    }

    @Override
    public View view() {
        return this.binding.getRoot();
    }
}
