package top.ourfor.app.iplay.page.setting.video;

import android.content.Context;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.val;
import top.ourfor.app.iplay.R;
import top.ourfor.app.iplay.common.annotation.ViewController;
import top.ourfor.app.iplay.common.type.PlayerKernelType;
import top.ourfor.app.iplay.config.AppSetting;
import top.ourfor.app.iplay.common.type.VideoDecodeType;
import top.ourfor.app.iplay.page.Page;
import top.ourfor.app.iplay.util.LayoutUtil;
import top.ourfor.app.iplay.page.setting.common.OptionModel;
import top.ourfor.app.iplay.page.setting.common.SettingModel;
import top.ourfor.app.iplay.page.setting.common.SettingType;
import top.ourfor.app.iplay.page.setting.common.SettingViewCell;
import top.ourfor.app.iplay.util.WindowUtil;
import top.ourfor.app.iplay.view.ListView;

@ViewController(name = "video_page")
public class VideoPage implements Page {
    private ConstraintLayout contentView = null;
    private List<SettingModel> settingModels = null;
    private ListView<SettingModel> listView = null;

    @Getter
    Context context;

    public void init() {
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
        var kernelOptions = List.of(
                new OptionModel<>(PlayerKernelType.MPV, getContext().getString(R.string.player_mpv)),
                new OptionModel<>(PlayerKernelType.EXO, getContext().getString(R.string.player_exo)),
                new OptionModel<>(PlayerKernelType.VLC, getContext().getString(R.string.player_vlc))
        );
        var defaultPlayerKernelOption = kernelOptions.get(0);
        for (val option : kernelOptions) {
            if (option.value == AppSetting.shared.playerKernel) {
                defaultPlayerKernelOption = option;
                break;
            }
        }
        settingModels = List.of(
                SettingModel.builder()
                        .title(getContext().getString(R.string.select_player_kernel))
                        .type(SettingType.SELECT)
                        .value(defaultPlayerKernelOption)
                        .options(kernelOptions)
                        .onClick(object -> {
                            if (!(object instanceof OptionModel<?>)) return;
                            val value = (OptionModel<PlayerKernelType>) object;
                            AppSetting.shared.playerKernel = value.value;
                            AppSetting.shared.save();
                        })
                        .build(),
                SettingModel.builder()
                        .title(getContext().getString(R.string.video_player_fullscreen))
                        .type(SettingType.SWITCH)
                        .value(AppSetting.shared.useFullScreenPlayer)
                        .onClick(object -> {
                            if (!(object instanceof Boolean value)) return;
                            AppSetting.shared.setUseFullScreenPlayer(value);
                            AppSetting.shared.save();
                        })
                        .build(),
                SettingModel.builder()
                        .title(getContext().getString(R.string.auto_play_next_episode))
                        .type(SettingType.SWITCH)
                        .value(AppSetting.shared.isAutoPlayNextEpisode())
                        .onClick(object -> {
                            if (!(object instanceof Boolean value)) return;
                            AppSetting.shared.setAutoPlayNextEpisode(value);
                            AppSetting.shared.save();
                        })
                        .build(),
                SettingModel.builder()
                        .title(getContext().getString(R.string.video_use_strm_first))
                        .type(SettingType.SWITCH)
                        .value(AppSetting.shared.isUseStrmFirst())
                        .onClick(object -> {
                            if (!(object instanceof Boolean value)) return;
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
                            AppSetting.shared.mpvConfig = (String) object;
                            AppSetting.shared.save();
                        })
                        .type(SettingType.TEXTAREA)
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
        return R.id.videoPage;
    }
}
