package top.ourfor.app.iplayx.page.setting.video;

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
import top.ourfor.app.iplayx.common.type.VideoDecodeType;
import top.ourfor.app.iplayx.page.Page;
import top.ourfor.app.iplayx.util.LayoutUtil;
import top.ourfor.app.iplayx.page.setting.common.OptionModel;
import top.ourfor.app.iplayx.page.setting.common.SettingModel;
import top.ourfor.app.iplayx.page.setting.common.SettingType;
import top.ourfor.app.iplayx.page.setting.common.SettingViewCell;
import top.ourfor.app.iplayx.util.WindowUtil;
import top.ourfor.app.iplayx.view.ListView;

public class VideoPage implements Page {
    private ConstraintLayout contentView = null;
    private List<SettingModel> settingModels = null;
    private ListView<SettingModel> listView = null;

    @Getter
    Context context;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        val videoOptions = List.of(
                new OptionModel<>(VideoDecodeType.Auto, getContext().getString(R.string.video_decode_auto)),
                new OptionModel<>(VideoDecodeType.Hardware, getContext().getString(R.string.video_decode_hw)),
                new OptionModel<>(VideoDecodeType.Software, getContext().getString(R.string.video_decode_sw))
        );
        var defaultVideoOption = videoOptions.get(0);
        for (val option : videoOptions) {
            if (option.value == AppSetting.shared.videoDecodeType) {
                defaultVideoOption = option;
                break;
            }
        }
        settingModels = List.of(
                SettingModel.builder()
                        .title(getContext().getString(R.string.use_exo_player))
                        .type(SettingType.SWITCH)
                        .value(AppSetting.shared.useExoPlayer)
                        .onClick(object -> {
                            if (!(object instanceof Boolean)) return;
                            val value = (Boolean) object;
                            AppSetting.shared.useExoPlayer = value;
                            AppSetting.shared.save();
                        })
                        .build(),
                SettingModel.builder()
                        .title(getContext().getString(R.string.video_player_fullscreen))
                        .type(SettingType.SWITCH)
                        .value(AppSetting.shared.useFullScreenPlayer)
                        .onClick(object -> {
                            if (!(object instanceof Boolean)) return;
                            val value = (Boolean) object;
                            AppSetting.shared.useFullScreenPlayer = value;
                            AppSetting.shared.save();
                        })
                        .build(),
                SettingModel.builder()
                        .title(getContext().getString(R.string.video_use_strm_first))
                        .type(SettingType.SWITCH)
                        .value(AppSetting.shared.isUseStrmFirst())
                        .onClick(object -> {
                            if (!(object instanceof Boolean)) return;
                            val value = (Boolean) object;
                            AppSetting.shared.setUseStrmFirst(value);
                            AppSetting.shared.save();
                        })
                        .build(),
                SettingModel.builder()
                        .title(getContext().getString(R.string.video_decode))
                        .value(defaultVideoOption)
                        .options(videoOptions)
                        .onClick(object -> {
                            if (!(object instanceof OptionModel)) return;
                            val option = (OptionModel<VideoDecodeType>) object;
                            AppSetting.shared.videoDecodeType = option.value;
                            AppSetting.shared.save();
                        })
                        .type(SettingType.SELECT)
                        .build(),
                SettingModel.builder()
                        .title(getContext().getString(R.string.mpv_conf))
                        .value(AppSetting.shared.mpvConfig)
                        .onClick(object -> {
                            if (!(object instanceof String)) return;
                            val value = (String) object;
                            AppSetting.shared.mpvConfig = value;
                            AppSetting.shared.save();
                        })
                        .type(SettingType.TEXTAREA)
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
