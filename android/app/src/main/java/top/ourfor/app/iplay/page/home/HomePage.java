package top.ourfor.app.iplay.page.home;

import static top.ourfor.app.iplay.module.Bean.XGET;
import static top.ourfor.app.iplay.module.Bean.XWATCH;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.MenuItemCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import top.ourfor.app.iplay.R;
import top.ourfor.app.iplay.action.AnimationAction;
import top.ourfor.app.iplay.action.DispatchAction;
import top.ourfor.app.iplay.action.SiteListUpdateAction;
import top.ourfor.app.iplay.action.SiteUpdateAction;
import top.ourfor.app.iplay.action.ThemeUpdateAction;
import top.ourfor.app.iplay.bean.INavigator;
import top.ourfor.app.iplay.common.annotation.ViewController;
import top.ourfor.app.iplay.databinding.HomePageBinding;
import top.ourfor.app.iplay.model.SiteModel;
import top.ourfor.app.iplay.page.Page;
import top.ourfor.app.iplay.page.login.SiteViewCell;
import top.ourfor.app.iplay.page.setting.site.SiteLineManageView;
import top.ourfor.app.iplay.util.DeviceUtil;
import top.ourfor.app.iplay.store.IAppStore;
import top.ourfor.app.iplay.util.WindowUtil;
import top.ourfor.app.iplay.view.ListView;
import top.ourfor.app.iplay.view.infra.Toolbar;
import top.ourfor.app.iplay.view.infra.ToolbarAction;

@Slf4j
@ViewController(name = "home_page")
public class HomePage implements SiteUpdateAction, ThemeUpdateAction, SiteListUpdateAction, Page {
    private ListView<SiteModel> siteListView = null;
    private BottomSheetDialog dialog = null;
    private AnimationAction activityIndicator = null;
    private SiteLineManageView siteLineListView = null;

    @Getter
    Context context;
    HomePageBinding binding;
    IAppStore store;
    HomeViewModel viewModel;

    public void init() {
        val inflater = LayoutInflater.from(context);
        binding = HomePageBinding.inflate(inflater, null, false);
        val view = binding.getRoot();
        store = XGET(IAppStore.class);
        viewModel = new HomeViewModel(store);
        viewModel.getAlbumCollection().observe(view, albums -> {
            if (binding != null) {
                binding.albumListView.setItems(albums);
            }
        });
        viewModel.getIsLoading().observe(view, isLoading -> {
            if (activityIndicator == null) return;
            if (isLoading) {
                activityIndicator.playAnimation();
            } else {
                activityIndicator.cancelAnimation();
                binding.swipeRefresh.setRefreshing(false);
            }
        });
        viewModel.getHasValidSite().observe(view, hasValidSite -> {
            val visible = hasValidSite ? View.VISIBLE : View.GONE;
            val invisible = hasValidSite ? View.GONE : View.VISIBLE;
            binding.homeActionBar.setVisibility(invisible);
            binding.albumListView.setVisibility(visible);
            binding.swipeRefresh.setVisibility(visible);
        });
    }

    @Override
    public void viewDidAppear() {
        val toolbar = XGET(Toolbar.class);
        toolbar.inflateMenu(R.menu.home_menu, ToolbarAction.Position.Right);
        toolbar.inflateMenu(R.menu.home_left_menu, ToolbarAction.Position.Left);
        val animationItem = toolbar.getLeftMenu().findItem(R.id.loading);
        val actionProvider = MenuItemCompat.getActionProvider(animationItem);
        activityIndicator = (AnimationAction)actionProvider;
        activityIndicator.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val color = getContext().getColor(R.color.onBackground);
            val menu = toolbar.getMenu();
            val size = menu.size();
            val tint = ColorStateList.valueOf(color);
            for (int i = 0; i < size; i++) {
                menu.getItem(i).setIconTintList(tint);
            }
        }
        toolbar.setOnMenuItemClickListener(item -> {
            val itemId = item.getItemId();
            if (itemId == R.id.switch_site) {
                showSiteSelectPanel();
            } else if (itemId == R.id.line_manage) {
                showSiteLineSelectPanel();
            } else if (itemId == R.id.web_page) {
                val route = XGET(INavigator.class);
                val args = new HashMap<String, Object>();
                assert route != null;
                route.pushPage("web_page", args);
            }
            return true;
        });
        binding.getRoot().setPadding(0, WindowUtil.defaultToolbarBottom, 0, 0);
    }

    public void setup() {
        setupUI(context);
        bind();
        binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void showSiteSelectPanel() {
        val site = store.getSite();
        siteListView.viewModel.isSelected = (model) -> model.getUser().equals(site.getUser()) && model.getId().equals(site.getId());
        siteListView.setItems(store.getSites());
        siteListView.setOnTouchListener((v1, event) -> {
            if (!siteListView.listView.canScrollVertically(-1)) {
                siteListView.listView.requestDisallowInterceptTouchEvent(false);
            }else{
                siteListView.listView.requestDisallowInterceptTouchEvent(true);
            }
            return false;
        });
        val context = getContext();
        dialog = new BottomSheetDialog(context, R.style.SiteBottomSheetDialog);
        dialog.setOnDismissListener(dlg -> {
            ViewGroup parent = (ViewGroup) siteListView.getParent();
            if (parent != null) {
                parent.removeView(siteListView);
            }
        });
        val parent = (ViewGroup)siteListView.getParent();
        if (parent != null) {
            parent.removeView(siteListView);
        }
        dialog.setContentView(siteListView);
        BottomSheetBehavior behavior = BottomSheetBehavior.from((View) siteListView.getParent());
        val height = (int) (DeviceUtil.screenSize(context).getHeight() * 0.6);
        behavior.setPeekHeight(height);
        dialog.show();
    }

    void setupUI(Context context) {
        val listView = binding.albumListView;
        listView.setCacheSize(10);
        listView.setEmptyTipVisible(View.VISIBLE);
        listView.viewModel.viewCell = MediaListViewCell.class;

        siteListView = new ListView<>(context);
        siteListView.viewModel.viewCell = SiteViewCell.class;
        val listViewLayout = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        listViewLayout.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
        listViewLayout.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
        siteListView.setLayoutParams(listViewLayout);
        siteListView.viewModel.onClick = e -> {
            XGET(DispatchAction.class).runOnUiThread(() -> {
                if (dialog != null) {
                    dialog.dismiss();
                }
            });
            store.switchSite(e.getModel());
        };

        binding.addSiteButton.setOnClickListener(v -> {
            XGET(INavigator.class).pushPage(R.id.loginPage, null);
        });
        binding.syncWebdavButton.setOnClickListener(v -> {
            XGET(INavigator.class).pushPage(R.id.cloudPage, null);
        });
        if (DeviceUtil.isTV) {
            binding.addSiteButton.setFocusable(true);
            binding.syncWebdavButton.setFocusable(true);
            View.OnFocusChangeListener onFocusChangeListener = (v, hasFocus) -> {
                v.setBackgroundResource(hasFocus ? R.drawable.button_focus : R.drawable.button_normal);
            };
            binding.syncWebdavButton.setOnFocusChangeListener(onFocusChangeListener);
            binding.addSiteButton.setOnFocusChangeListener(onFocusChangeListener);
        }
        binding.getRoot().setPadding(0, WindowUtil.defaultToolbarBottom, 0, 0);
    }

    void bind() {
        XWATCH(ThemeUpdateAction.class, this);
        XWATCH(SiteUpdateAction.class, this);
        XWATCH(SiteListUpdateAction.class, this);
        viewModel.getHasValidSite().setValue(store.hasValidSite());
        viewModel.loadFromLocalCache();
        binding.getRoot().setPadding(0, WindowUtil.defaultToolbarBottom, 0, 0);

        if (viewModel.getHasValidSite().getValue()) {
            viewModel.fetchAlbumsIfNeeded();
        }

        binding.swipeRefresh.setOnRefreshListener(() -> {
            viewModel.fetchAlbums();
        });
    }


    @Override
    public void onSiteUpdate() {
        viewModel.getHasValidSite().postValue(store.hasValidSite());
        viewModel.fetchAlbums();
        XGET(Toolbar.class).setTitle(store.getSiteName());
    }

    public void updateSiteList() {
        siteListView.setItems(store.getSites());
    }

    private void showSiteLineSelectPanel() {
        val context = getContext();
        if (siteLineListView == null) {
            siteLineListView = new SiteLineManageView(context);
            siteLineListView.setShowUrl(false);
            siteLineListView.setShowDelay(true);
        }

        val dialog = new BottomSheetDialog(context, R.style.SiteBottomSheetDialog);
        dialog.setOnDismissListener(dlg -> {
            ViewGroup parent = (ViewGroup) siteLineListView.getParent();
            if (parent != null) {
                parent.removeView(siteLineListView);
            }
        });
        val parent = (ViewGroup)siteLineListView.getParent();
        if (parent != null) {
            parent.removeView(siteLineListView);
        }
        dialog.setContentView(siteLineListView);
        val behavior = BottomSheetBehavior.from((View) siteLineListView.getParent());
        val height = (int) (DeviceUtil.screenSize(context).getHeight() * 0.6);
        behavior.setPeekHeight(height);
        siteLineListView.loadData();
        dialog.show();
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
        return binding.getRoot();
    }
}
