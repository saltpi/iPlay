package top.ourfor.app.iplayx.page.setting;

import static top.ourfor.app.iplayx.module.Bean.XGET;
import static top.ourfor.app.iplayx.module.Bean.XWATCH;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.val;
import top.ourfor.app.iplayx.R;
import top.ourfor.app.iplayx.action.ThemeUpdateAction;
import top.ourfor.app.iplayx.bean.Navigator;
import top.ourfor.app.iplayx.databinding.SettingPageBinding;
import top.ourfor.app.iplayx.page.Page;
import top.ourfor.app.iplayx.page.setting.SettingItemModel.Type;
import top.ourfor.app.iplayx.util.WindowUtil;
import top.ourfor.app.iplayx.view.ListItemClickEvent;
import top.ourfor.app.iplayx.view.ListView;

public class SettingPage implements ThemeUpdateAction, Page {
    @Getter
    Context context;
    SettingPageBinding binding;

    private ListView<SettingItemModel> listView = null;
    private final List<SettingItemModel> settingItems = List.of(
            new SettingItemModel(Type.Theme, R.string.setting_item_theme, com.microsoft.fluent.mobile.icons.R.drawable.ic_fluent_color_24_filled),
            new SettingItemModel(Type.Site, R.string.setting_item_site, com.microsoft.fluent.mobile.icons.R.drawable.ic_fluent_hard_drive_24_filled),
            new SettingItemModel(Type.Picture, R.string.setting_item_picture, com.microsoft.fluent.mobile.icons.R.drawable.ic_fluent_image_24_filled),
            new SettingItemModel(Type.Video, R.string.setting_item_video, com.microsoft.fluent.mobile.icons.R.drawable.ic_fluent_movies_and_tv_24_filled),
            new SettingItemModel(Type.Audio, R.string.setting_item_audio, com.microsoft.fluent.mobile.icons.R.drawable.ic_fluent_headphones_24_filled),
            new SettingItemModel(Type.Cloud, R.string.setting_item_cloud, com.microsoft.fluent.mobile.icons.R.drawable.ic_fluent_cloud_24_filled),
            new SettingItemModel(Type.Cache, R.string.setting_item_cache, com.microsoft.fluent.mobile.icons.R.drawable.ic_fluent_delete_24_filled),
            new SettingItemModel(Type.About, R.string.setting_item_about, com.microsoft.fluent.mobile.icons.R.drawable.ic_fluent_megaphone_24_filled)
    );

    public void onCreate(@Nullable Bundle savedInstanceState) {
        binding = SettingPageBinding.inflate(LayoutInflater.from(getContext()));
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        listView = binding.settingList;
        setupUI(context);
        bind();
        return binding.getRoot();
    }

    void setupUI(Context context) {
        listView.viewModel.viewCell = SettingItemViewCell.class;
        listView.viewModel.onClick = (ListItemClickEvent<SettingItemModel> event) -> {
            val navController = XGET(Navigator.class);
            val type = event.getModel().type;
            switch (type) {
                case Theme -> navController.pushPage(R.id.themePage, null);
                case Video -> navController.pushPage(R.id.videoPage, null);
                case Audio -> navController.pushPage(R.id.audioPage, null);
                case Site -> navController.pushPage(R.id.sitePage, null);
                case About -> navController.pushPage(R.id.aboutPage, null);
                case Cloud -> navController.pushPage(R.id.cloudPage, null);
                case Cache -> navController.pushPage(R.id.cachePage, null);
                case Picture -> navController.pushPage(R.id.picturePage, null);
                default -> Toast.makeText(context, R.string.not_implementation, Toast.LENGTH_SHORT).show();
            }
        };
        binding.getRoot().setPadding(0, WindowUtil.defaultToolbarBottom, 0, 0);
    }

    void bind() {
        XWATCH(ThemeUpdateAction.class, this);
        listView.setItems(settingItems);
    }

    @Override
    public void create(Context context, Map<String, Object> params) {
        this.context = context;
        onCreate(null);
        onCreateView(LayoutInflater.from(context), null, null);
    }

    @Override
    public View view() {
        return binding.getRoot();
    }
}
