package top.ourfor.app.iplay.page.setting.site;

import static top.ourfor.app.iplay.module.Bean.XGET;
import static top.ourfor.app.iplay.module.Bean.XWATCH;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Map;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import top.ourfor.app.iplay.R;
import top.ourfor.app.iplay.action.NavigationTitleBar;
import top.ourfor.app.iplay.action.SiteListUpdateAction;
import top.ourfor.app.iplay.action.SiteUpdateAction;
import top.ourfor.app.iplay.api.emby.EmbyModel;
import top.ourfor.app.iplay.common.annotation.ViewController;
import top.ourfor.app.iplay.databinding.SitePageBinding;
import top.ourfor.app.iplay.model.SiteModel;
import top.ourfor.app.iplay.page.Activity;
import top.ourfor.app.iplay.page.Page;
import top.ourfor.app.iplay.page.login.LoginPage;
import top.ourfor.app.iplay.page.login.SiteViewCell;
import top.ourfor.app.iplay.store.IAppStore;
import top.ourfor.app.iplay.util.DeviceUtil;
import top.ourfor.app.iplay.util.WindowUtil;
import top.ourfor.app.iplay.view.ListView;
import top.ourfor.app.iplay.view.infra.Toolbar;
import top.ourfor.app.iplay.view.infra.ToolbarAction;

@Slf4j
@ViewController(name = "site_page")
public class SitePage implements Page, SiteListUpdateAction, SiteUpdateAction {
    private SitePageBinding binding = null;
    private ListView<SiteModel> listView = null;
    private SiteLineManageView siteLineListView = null;

    @Getter
    Context context;

    @Override
    public void viewDidAppear() {
        val toolbar = XGET(Toolbar.class);
        toolbar.inflateMenu(R.menu.site_menu, ToolbarAction.Position.Right);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val color = getContext().getColor(R.color.onBackground);
            toolbar.getRightMenu().getItem(0).setIconTintList(ColorStateList.valueOf(color));
        }
        toolbar.setOnMenuItemClickListener(item -> {
            val itemId = item.getItemId();
            if (itemId == R.id.add) {
                addSite();
            } else if (itemId == R.id.line_manage) {
                showSiteLineSelectPanel();
            }
            return true;
        });
        XGET(BottomNavigationView.class).setVisibility(View.GONE);
        val actionBar = XGET(ActionBar.class);
        XGET(NavigationTitleBar.class).setNavTitle(R.string.setting_item_site);
        XWATCH(SiteListUpdateAction.class, this);
        XWATCH(SiteUpdateAction.class, this);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = SitePageBinding.inflate(inflater, null, false);
        setupUI(context);
        bind(context);
        binding.getRoot().setPadding(0, WindowUtil.defaultToolbarBottom, 0, 0);
        return binding.getRoot();
    }

    void setupUI(Context context) {
        listView = binding.siteList;
    }

    void bind(Context context) {
        listView.viewModel.viewCell = SiteViewCell.class;
        listView.viewModel.onClick = (event) -> {
            log.info("site event clicked: {}", event);
            IAppStore store = XGET(IAppStore.class);
            store.switchSite(event.getModel());
            Toast.makeText(context, "Switch site success", Toast.LENGTH_SHORT).show();
        };
        val store = XGET(IAppStore.class);
        listView.viewModel.isSelected = (model) -> model.getUser().equals(store.getSite().getUser()) && model.getId().equals(store.getSite().getId());
        listView.setItems(store.getSites());

    }

    void addSite() {
        LoginPage page = new LoginPage();
        page.show(XGET(Activity.class).getSupportFragmentManager(), "addSite");
    }

    @Override
    public void updateSiteList() {
        val store = XGET(IAppStore.class);
        assert store != null;
        val currentSite = store.getSite();
        if (currentSite.getRemark() == null || currentSite.getRemark().isEmpty()) {
            val api = store.getApi();
            if (api == null) return;
            api.getSiteInfo((info) -> {
                if (!(info instanceof EmbyModel.EmbySiteInfo embyInfo)) return;
                currentSite.setRemark(embyInfo.getServerName());
                store.save();
                updateSiteList();
            });
        }
        listView.viewModel.isSelected = (model) -> model.getUser().equals(currentSite.getUser()) && model.getId().equals(currentSite.getId());
        listView.setItems(store.getSites());
    }

    @Override
    public void onSiteModify(SiteModel model) {
        LoginPage page = new LoginPage();
        page.setSiteModel(model);
        page.show(XGET(Activity.class).getSupportFragmentManager(), "editSite");
    }

    private void showSiteLineSelectPanel() {
        val context = getContext();
        if (siteLineListView == null) {
            siteLineListView = new SiteLineManageView(context);
            siteLineListView.setShowUrl(true);
            siteLineListView.setShowDelay(false);
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
        onCreateView(LayoutInflater.from(context), null, null);
    }

    @Override
    public View view() {
        return binding.getRoot();
    }

    @Override
    public int id() {
        return R.id.sitePage;
    }
}
