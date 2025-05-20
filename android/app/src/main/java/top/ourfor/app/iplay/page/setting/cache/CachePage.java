package top.ourfor.app.iplay.page.setting.cache;

import android.content.Context;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import top.ourfor.app.iplay.R;
import top.ourfor.app.iplay.common.annotation.ViewController;
import top.ourfor.app.iplay.config.AppSetting;
import top.ourfor.app.iplay.page.Page;
import top.ourfor.app.iplay.page.setting.common.SettingModel;
import top.ourfor.app.iplay.page.setting.common.SettingType;
import top.ourfor.app.iplay.page.setting.common.SettingViewCell;
import top.ourfor.app.iplay.util.LayoutUtil;
import top.ourfor.app.iplay.util.WindowUtil;
import top.ourfor.app.iplay.view.ListView;

@ViewController(name = "cache_page")
public class CachePage implements Page {
    private ConstraintLayout contentView = null;
    private List<SettingModel> settingModels = null;
    private ListView<SettingModel> listView = null;

    @Getter
    Context context;

    public void init() {
        settingModels = List.of(
                SettingModel.builder()
                        .title(getContext().getString(R.string.exit_after_crash))
                        .type(SettingType.SWITCH)
                        .value(AppSetting.shared.exitAfterCrash)
                        .onClick(object -> {
                            if (!(object instanceof Boolean)) return;
                            AppSetting.shared.exitAfterCrash = (Boolean) object;
                            AppSetting.shared.save();
                        })
                        .build(),
                SettingModel.builder()
                        .title(getContext().getString(R.string.autoupgrade_turn_off))
                        .type(SettingType.SWITCH)
                        .value(AppSetting.shared.turnOffAutoUpgrade)
                        .onClick(object -> {
                            if (!(object instanceof Boolean)) return;
                            AppSetting.shared.turnOffAutoUpgrade = (Boolean) object;
                            AppSetting.shared.save();
                        })
                        .build(),
                SettingModel.builder()
                        .title(getContext().getString(R.string.web_homepage))
                        .type(SettingType.TEXTAREA)
                        .value(AppSetting.shared.webHomePage)
                        .onClick(object -> {
                            if (!(object instanceof String)) return;
                            AppSetting.shared.webHomePage = (String) object;
                            AppSetting.shared.save();
                        })
                        .build()
        );
    }

    public void setup() {
        contentView = new ConstraintLayout(context);
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
        return R.id.cachePage;
    }
}
