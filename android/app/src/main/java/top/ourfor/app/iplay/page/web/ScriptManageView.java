package top.ourfor.app.iplay.page.web;

import static top.ourfor.app.iplay.module.Bean.XGET;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.function.Consumer;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import top.ourfor.app.iplay.R;
import top.ourfor.app.iplay.bean.IKVStorage;
import top.ourfor.app.iplay.databinding.ScriptManageBinding;
import top.ourfor.app.iplay.util.DeviceUtil;

public class ScriptManageView extends ConstraintLayout {
    ScriptManageBinding binding = null;
    @Getter
    ScriptManageViewModel viewModel = new ScriptManageViewModel();

    @Getter @Setter
    Consumer<View> onSaveButtonClick;

    public ScriptManageView(@NonNull Context context) {
        super(context);
        binding = ScriptManageBinding.inflate(LayoutInflater.from(context), this, true);
        val layout = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(layout);
        bind();
    }

    private void bind() {
        viewModel.value = XGET(IKVStorage.class).get("@script");
        setupTextArea();

        binding.saveButton.setOnClickListener(v -> {
            val kv = XGET(IKVStorage.class);
            if (kv != null) {
                kv.set("@script", viewModel.value);
            }
            if (onSaveButtonClick != null) {
                onSaveButtonClick.accept(v);
            }
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
