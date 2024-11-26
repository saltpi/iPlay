package top.ourfor.app.iplayx.page.setting.common;

import static top.ourfor.app.iplayx.module.Bean.XGET;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.val;
import top.ourfor.app.iplayx.R;
import top.ourfor.app.iplayx.action.UpdateModelAction;
import top.ourfor.app.iplayx.bean.KVStorage;
import top.ourfor.app.iplayx.databinding.ScriptManageBinding;
import top.ourfor.app.iplayx.databinding.SettingCellBinding;
import top.ourfor.app.iplayx.util.DeviceUtil;
import top.ourfor.app.iplayx.view.TagView;

public class ScriptManageView extends ConstraintLayout {
    ScriptManageBinding binding = null;
    @Getter
    ScriptManageViewModel viewModel = new ScriptManageViewModel();

    public ScriptManageView(@NonNull Context context) {
        super(context);
        binding = ScriptManageBinding.inflate(LayoutInflater.from(context), this, true);
        val layout = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(layout);
        bind();
    }

    private void bind() {
        viewModel.value = XGET(KVStorage.class).get("@script");
        setupTextArea();

        binding.settingButton.setOnClickListener(v -> {
            XGET(KVStorage.class).set("@script", viewModel.value);
        });

        if (!DeviceUtil.isTV) return;
        setFocusable(true);
        setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                setBackgroundResource(R.drawable.card_focus);
            } else {
                setBackgroundResource(R.drawable.card_normal);
            }
        });
    }


    void setupTextArea() {
        binding.textArea.setText(viewModel.value);
        binding.textArea.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setValue(binding.textArea.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}
