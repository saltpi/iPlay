package top.ourfor.app.iplay.page.setting.picture;

import android.content.Context;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.val;
import top.ourfor.app.iplay.R;
import top.ourfor.app.iplay.common.annotation.ViewController;
import top.ourfor.app.iplay.common.type.PictureQuality;
import top.ourfor.app.iplay.config.AppSetting;
import top.ourfor.app.iplay.page.Page;
import top.ourfor.app.iplay.page.setting.common.OptionModel;
import top.ourfor.app.iplay.page.setting.common.SettingModel;
import top.ourfor.app.iplay.page.setting.common.SettingType;
import top.ourfor.app.iplay.page.setting.common.SettingViewCell;
import top.ourfor.app.iplay.util.LayoutUtil;
import top.ourfor.app.iplay.util.WindowUtil;
import top.ourfor.app.iplay.view.ListView;

@ViewController(name = "picture_page")
public class PicturePage implements Page {
    private ConstraintLayout contentView = null;
    private List<SettingModel> settingModels = null;
    private ListView<SettingModel> listView = null;

    @Getter
    Context context;

    public void init() {
        val pictureOptions = List.of(
                new OptionModel<>(PictureQuality.Auto, getContext().getString(R.string.pic_quality_auto)),
                new OptionModel<>(PictureQuality.High, getContext().getString(R.string.pic_quality_high)),
                new OptionModel<>(PictureQuality.Medium, getContext().getString(R.string.pic_quality_medium)),
                new OptionModel<>(PictureQuality.Low, getContext().getString(R.string.pic_quality_low))
        );
        var defaultPictureOption = pictureOptions.get(0);
        for (val option : pictureOptions) {
            if (option.value == AppSetting.shared.pictureQuality) {
                defaultPictureOption = option;
                break;
            }
        }
        settingModels = List.of(
                SettingModel.builder()
                        .title(getContext().getString(R.string.picture_quality))
                        .type(SettingType.SELECT)
                        .value(defaultPictureOption)
                        .options(pictureOptions)
                        .onClick(object -> {
                            if (!(object instanceof OptionModel)) return;
                            val option = (OptionModel<PictureQuality>) object;
                            AppSetting.shared.pictureQuality = option.value;
                            AppSetting.shared.save();
                        })
                        .build(),
                SettingModel.builder()
                        .title(getContext().getString(R.string.picture_use_multi_thread))
                        .type(SettingType.SWITCH)
                        .value(AppSetting.shared.isUsePictureMultiThread())
                        .onClick(object -> {
                            if (!(object instanceof Boolean)) return;
                            val value = (Boolean) object;
                            AppSetting.shared.setUsePictureMultiThread(value);
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
        return R.id.picturePage;
    }
}
