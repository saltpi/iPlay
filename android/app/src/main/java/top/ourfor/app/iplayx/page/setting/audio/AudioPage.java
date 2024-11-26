package top.ourfor.app.iplayx.page.setting.audio;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.val;
import top.ourfor.app.iplayx.R;
import top.ourfor.app.iplayx.config.AppSetting;
import top.ourfor.app.iplayx.page.Page;
import top.ourfor.app.iplayx.util.LayoutUtil;
import top.ourfor.app.iplayx.page.setting.common.SettingModel;
import top.ourfor.app.iplayx.page.setting.common.SettingType;
import top.ourfor.app.iplayx.page.setting.common.SettingViewCell;
import top.ourfor.app.iplayx.util.WindowUtil;
import top.ourfor.app.iplayx.view.ListView;

public class AudioPage implements Page {
    private ConstraintLayout contentView = null;
    private List<SettingModel> settingModels = null;
    private ListView<SettingModel> listView = null;

    @Getter
    Context context;

    public void onCreate(@Nullable Bundle savedInstanceState) {
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

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentView = new ConstraintLayout(context);
        setupUI(context);
        bind();
        contentView.setPadding(0, WindowUtil.defaultToolbarBottom, 0, 0);
        return contentView;
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
        onCreate(null);
        onCreateView(LayoutInflater.from(context), null, null);
    }
}
