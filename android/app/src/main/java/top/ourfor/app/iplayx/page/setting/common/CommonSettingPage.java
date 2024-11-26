package top.ourfor.app.iplayx.page.setting.common;

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

import top.ourfor.app.iplayx.util.LayoutUtil;
import top.ourfor.app.iplayx.util.WindowUtil;
import top.ourfor.app.iplayx.view.ListView;

public class CommonSettingPage extends Fragment {
    private ConstraintLayout contentView = null;
    static List<SettingModel> settingModels = List.of(
            SettingModel.builder()
                    .title("Setting 1")
                    .type(SettingType.SWITCH)
                    .build(),
            SettingModel.builder()
                    .title("Setting 2")
                    .type(SettingType.SELECT)
                    .build(),
            SettingModel.builder()
                    .title("Setting 3")
                    .build()
    );
    private ListView<SettingModel> listView = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentView = new ConstraintLayout(container.getContext());
        setupUI(container.getContext());
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
}
