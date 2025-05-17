package top.ourfor.app.iplay.page.setting.audio;

import android.content.Context;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.val;
import top.ourfor.app.iplay.R;
import top.ourfor.app.iplay.common.annotation.ViewController;
import top.ourfor.app.iplay.config.AppSetting;
import top.ourfor.app.iplay.page.Page;
import top.ourfor.app.iplay.util.LayoutUtil;
import top.ourfor.app.iplay.page.setting.common.SettingModel;
import top.ourfor.app.iplay.page.setting.common.SettingType;
import top.ourfor.app.iplay.page.setting.common.SettingViewCell;
import top.ourfor.app.iplay.util.WindowUtil;
import top.ourfor.app.iplay.view.ListView;

@ViewController(name = "audio_page")
public class AudioPage implements Page {
    private ConstraintLayout contentView = null;
    private List<SettingModel> settingModels = null;
    private ListView<SettingModel> listView = null;

    @Getter
    Context context;

    public void init() {
        settingModels = List.of(
                SettingModel.builder()
                        .title(getContext().getString(R.string.audio_turn_off))
                        .type(SettingType.SWITCH)
                        .value(AppSetting.shared.turnOffAudio)
                        .onClick(object -> {
                            if (!(object instanceof Boolean)) return;
                            val value = (Boolean) object;
                            AppSetting.shared.turnOffAudio = value;
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
    public View view() {
        return contentView;
    }

    @Override
    public void create(Context context, Map<String, Object> params) {
        this.context = context;
        init();
        setup();
    }

    @Override
    public int id() {
        return R.id.audioPage;
    }
}
