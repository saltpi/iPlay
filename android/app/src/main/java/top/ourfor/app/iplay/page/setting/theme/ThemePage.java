package top.ourfor.app.iplay.page.setting.theme;

import static top.ourfor.app.iplay.module.Bean.XGET;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.val;
import top.ourfor.app.iplay.R;
import top.ourfor.app.iplay.action.LayoutUpdateAction;
import top.ourfor.app.iplay.action.NavigationTitleBar;
import top.ourfor.app.iplay.bean.INavigator;
import top.ourfor.app.iplay.common.annotation.ViewController;
import top.ourfor.app.iplay.common.model.HomeTabModel;
import top.ourfor.app.iplay.common.type.LayoutType;
import top.ourfor.app.iplay.config.AppSetting;
import top.ourfor.app.iplay.module.FontModule;
import top.ourfor.app.iplay.page.Page;
import top.ourfor.app.iplay.page.setting.common.OptionModel;
import top.ourfor.app.iplay.util.DeviceUtil;
import top.ourfor.app.iplay.util.LayoutUtil;
import top.ourfor.app.iplay.page.setting.common.SettingModel;
import top.ourfor.app.iplay.page.setting.common.SettingType;
import top.ourfor.app.iplay.page.setting.common.SettingViewCell;
import top.ourfor.app.iplay.util.WindowUtil;
import top.ourfor.app.iplay.view.ListView;

@ViewController(name = "theme_page")
public class ThemePage implements Page {
    private ConstraintLayout contentView = null;
    private List<SettingModel> settingModels = null;
    private ListView<SettingModel> listView = null;
    private ListView<HomeTabModel> homeTabListView = null;
    Dialog dialog = null;

    @Getter
    Context context;

    public void init() {
        val themeOptions = List.of(
                new ThemeModel(ThemeModel.ThemeType.FOLLOW_SYSTEM, getContext().getString(R.string.theme_follow_system)),
                new ThemeModel(ThemeModel.ThemeType.DARK_MODE, getContext().getString(R.string.theme_dark_mode)),
                new ThemeModel(ThemeModel.ThemeType.LIGHT_MODE, getContext().getString(R.string.theme_light_mode))
        );
        var defaultThemeOption = themeOptions.get(0);
        for (val option : themeOptions) {
            if (option.type == AppSetting.shared.appearance) {
                defaultThemeOption = option;
                break;
            }
        }

        val layoutOptions = List.of(
                new OptionModel<>(LayoutType.Auto, getContext().getString(R.string.video_decode_auto)),
                new OptionModel<>(LayoutType.Phone, getContext().getString(R.string.layout_type_phone)),
                new OptionModel<>(LayoutType.TV, getContext().getString(R.string.layout_type_tv))
        );
        var defaultLayoutOption = layoutOptions.get(0);
        for (val option : layoutOptions) {
            if (option.value == AppSetting.shared.layoutType) {
                defaultLayoutOption = option;
                break;
            }
        }

        settingModels = List.of(
                SettingModel.builder()
                        .title(getContext().getString(R.string.theme_appearance))
                        .value(defaultThemeOption)
                        .options(themeOptions)
                        .onClick(object -> {
                            if (!(object instanceof ThemeModel option)) return;
                            val titleBar = XGET(NavigationTitleBar.ThemeManageAction.class);
                            assert titleBar != null;
                            switch (option.type) {
                                case FOLLOW_SYSTEM:
                                    titleBar.switchToAutoModel();
                                    break;
                                case DARK_MODE:
                                    titleBar.switchToDarkMode(true);
                                    break;
                                case LIGHT_MODE:
                                    titleBar.switchToDarkMode(false);
                                    break;
                            }
                            AppSetting.shared.appearance = option.type;
                            AppSetting.shared.save();
                        })
                        .type(SettingType.SELECT)
                        .build(),
                SettingModel.builder()
                        .title(getContext().getString(R.string.home_allow_tabs))
                        .value(getContext().getString(R.string.home_allow_tabs_modify))
                        .onClick(object -> {
                            showTabConfigPanel();
                        })
                        .type(SettingType.ACTION)
                        .build(),
                SettingModel.builder()
                        .title(getContext().getString(R.string.theme_layout_mode))
                        .value(defaultLayoutOption)
                        .options(layoutOptions)
                        .onClick(object -> {
                            if (!(object instanceof OptionModel)) return;
                            val option = (OptionModel<LayoutType>) object;
                            AppSetting.shared.layoutType = option.value;
                            XGET(LayoutUpdateAction.class).switchLayoutMode(option.value);
                            AppSetting.shared.save();
                        })
                        .type(SettingType.SELECT)
                        .build(),
                SettingModel.builder()
                        .title(getContext().getString(R.string.theme_font))
                        .value(AppSetting.shared.fontFamily)
                        .options(FontModule.getFontFamilyList())
                        .onClick(object -> {
                            if (!(object instanceof String)) return;

                            AppSetting.shared.fontFamily = (String) object;
                            AppSetting.shared.save();
                        })
                        .type(SettingType.SPINNER)
                        .build()
        );
    }

    private void showTabConfigPanel() {
        val allTabs = XGET(INavigator.class).getHomeTabs();
        if (homeTabListView == null) {
            homeTabListView = new ListView<>(getContext());
            homeTabListView.viewModel.viewCell = HomeTabEditViewCell.class;
            val itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
                @Override
                public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                    int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                    int swipeFlags = 0;
                    return makeMovementFlags(dragFlags, swipeFlags);
                }

                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    int fromPosition = viewHolder.getAdapterPosition();
                    int toPosition = target.getAdapterPosition();
                    if (fromPosition < toPosition) {
                        for (int i = fromPosition; i < toPosition; i++) {
                            Collections.swap(homeTabListView.viewModel.items, i, i + 1);
                        }
                    } else {
                        for (int i = fromPosition; i > toPosition; i--) {
                            Collections.swap(homeTabListView.viewModel.items, i, i - 1);
                        }
                    }
                    homeTabListView.reloadData();
                    return true;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                }
            });
            itemTouchHelper.attachToRecyclerView(homeTabListView.listView);
        }
        val mapTabToString = new HashMap<HomeTabModel, String>();
        for (val tab : allTabs.entrySet()) {
            mapTabToString.put(tab.getValue(), tab.getKey());
        }
        homeTabListView.viewModel.isSelected = (model) -> {
            val tab = mapTabToString.get(model);
            if (tab != null && AppSetting.shared.allowTabs.contains(tab)) {
                return true;
            }
            return false;
        };
        var allItems = new ArrayList<>(allTabs.values());
        var allowItems = new ArrayList<HomeTabModel>();
        var notAllowItems = new ArrayList<HomeTabModel>();
        for (val tabKey: AppSetting.shared.allowTabs.split(",")) {
            val tab = allTabs.get(tabKey);
            if (tab != null) {
                allowItems.add(tab);
            }
        }
        for (val tabKey: allTabs.keySet()) {
            val tab = allTabs.get(tabKey);
            if (tab != null && !AppSetting.shared.allowTabs.contains(tabKey)) {
                notAllowItems.add(tab);
            }
        }
        allowItems.addAll(notAllowItems);
        homeTabListView.setItems(allowItems);
        homeTabListView.viewModel.onClick = (event) -> {
            val model = event.getModel();
            val key = mapTabToString.get(model);
            var tabs = new ArrayList<>(List.of(AppSetting.shared.allowTabs.split(",")));
            if (tabs.contains(key)) {
                tabs.remove(key);
            } else {
                tabs.add(key);
            }
            AppSetting.shared.allowTabs = String.join(",", tabs);
            AppSetting.shared.save();
            homeTabListView.reloadData();
        };
        val context = getContext();
        dialog = new BottomSheetDialog(context, R.style.SiteBottomSheetDialog);
        dialog.setOnDismissListener(dlg -> {
            ViewGroup parent = (ViewGroup) homeTabListView.getParent();
            if (parent != null) {
                parent.removeView(homeTabListView);
            }
            var orderedTabs = new ArrayList<String>();
            for (val item : homeTabListView.viewModel.items) {
                val key = mapTabToString.get(item);
                if (AppSetting.shared.allowTabs.contains(key)) {
                    orderedTabs.add(key);
                }
            }
            AppSetting.shared.allowTabs = String.join(",", orderedTabs);
            AppSetting.shared.save();
        });
        val parent = (ViewGroup)homeTabListView.getParent();
        if (parent != null) {
            parent.removeView(homeTabListView);
        }
        dialog.setContentView(homeTabListView);
        BottomSheetBehavior behavior = BottomSheetBehavior.from((View) homeTabListView.getParent());
        val height = (int) (DeviceUtil.screenSize(context).getHeight() * 0.6);
        behavior.setPeekHeight(height);
        dialog.show();
    }

    public void setup() {
        contentView = new ConstraintLayout(getContext());
        setupUI(context);
        bind();
        contentView.setPadding(0, WindowUtil.defaultToolbarBottom, 0, 0);
    }

    void setupUI(Context context) {
        listView = new ListView<>(context);
        listView.viewModel.viewCell = SettingViewCell.class;
        contentView.addView(listView, LayoutUtil.fill());
    }

    void bind() {
        listView.setItems(settingModels);
    }

    @Override
    public void create(Context context, Map<String, Object> params) {
        this.context = context;
        init();
        setup();
    }

    @Override
    public View view() {
        return contentView;
    }

    @Override
    public int id() {
        return R.id.themePage;
    }
}
