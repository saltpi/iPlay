package top.ourfor.app.iplay.page.setting.common;

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
import java.util.concurrent.CopyOnWriteArrayList;

import lombok.val;
import top.ourfor.app.iplay.R;
import top.ourfor.app.iplay.action.UpdateModelAction;
import top.ourfor.app.iplay.databinding.SettingCellBinding;
import top.ourfor.app.iplay.util.DeviceUtil;
import top.ourfor.app.iplay.view.TagView;

public class SettingViewCell extends ConstraintLayout implements UpdateModelAction {
    SettingCellBinding binding = null;
    SettingModel model = null;

    public SettingViewCell(@NonNull Context context) {
        super(context);
        binding = SettingCellBinding.inflate(LayoutInflater.from(context), this, true);
        val layout = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(layout);
        bind();
    }

    private void bind() {
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


    @Override
    public <T> void updateModel(T other) {
        if (!(other instanceof SettingModel)) {
            return;
        }
        model = (SettingModel) other;
        if (model.type == null) return;
        resetLayoutForReuse();
        switch (model.type) {
            case SWITCH:
                binding.settingSwitch.setVisibility(VISIBLE);
                binding.settingButton.setVisibility(GONE);
                binding.nameLabel.setText(model.title);
                setupSwitch();
                break;
            case SELECT:
                binding.settingSwitch.setVisibility(GONE);
                binding.settingButton.setVisibility(GONE);
                binding.nameLabel.setText(model.title);
                setupSelection();
                break;
            case TEXTAREA:
                ConstraintLayout.LayoutParams titleLayout = (ConstraintLayout.LayoutParams) binding.nameLabel.getLayoutParams();
                titleLayout.bottomToBottom = -1;
                binding.nameLabel.setLayoutParams(titleLayout);
                binding.settingSwitch.setVisibility(GONE);
                binding.settingButton.setVisibility(GONE);
                binding.nameLabel.setText(model.title);
                ViewGroup.LayoutParams params = binding.textArea.getLayoutParams();
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                binding.textArea.setLayoutParams(params);
                setupTextArea();
                break;
            case SPINNER:
                binding.settingSwitch.setVisibility(GONE);
                binding.settingButton.setVisibility(GONE);
                binding.nameLabel.setText(model.title);
                setupSpinner();
                break;
            case ACTION:
                binding.settingSwitch.setVisibility(GONE);
                binding.settingButton.setVisibility(VISIBLE);
                binding.nameLabel.setText(model.title);
                setupAction();
                break;
            default:
                binding.settingSwitch.setVisibility(GONE);
                binding.settingButton.setVisibility(GONE);
                binding.nameLabel.setText(model.title);
                break;
        }
    }

    private void resetLayoutForReuse() {
        binding.textArea.setVisibility(GONE);
        binding.settingContainer.removeAllViews();
        binding.settingButton.setVisibility(GONE);
        binding.settingSwitch.setVisibility(GONE);
    }

    private void setupSpinner() {
        binding.settingContainer.removeAllViews();
        val spinner = new Spinner(getContext());
        val options = new CopyOnWriteArrayList<>();
        if (model.options.indexOf(model.value) == -1) {
            options.add("default");
        }
        options.addAll(model.options);
        val adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(model.options.indexOf(model.value), true);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    return;
                }
                if (model.onClick == null) return;
                model.onClick.accept(model.options.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.settingContainer.addView(spinner);
    }

    void setupTextArea() {
        binding.settingButton.setText(R.string.save);
        binding.textArea.setText(model.value instanceof String ? (String) model.value : "");
        binding.textArea.setVisibility(VISIBLE);
        binding.textArea.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (model.onClick == null) return;
                model.onClick.accept(binding.textArea.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    void setupSwitch() {
        if (!(model.value instanceof Boolean)) return;
        binding.settingSwitch.setChecked((Boolean) model.value);
        binding.settingSwitch.setOnCheckedChangeListener((button, flag) -> {
            if (model.onClick == null) return;
            model.onClick.accept(flag);
        });
    }

    void setupAction() {
        binding.settingButton.setVisibility(VISIBLE);
        binding.settingButton.setText(model.value.toString());
        binding.settingButton.setOnClickListener(v -> {
            if (model.onClick == null) return;
            model.onClick.accept(null);
        });
    }

    void setupSelection() {
        val buttons = new ArrayList<TagView>();
        binding.settingContainer.removeAllViews();
        model.options.forEach(option -> {
            val button = new TagView(getContext());
            button.setText(option.toString());
            button.setTextSize(DeviceUtil.spToPx(13));
            if (option.equals(model.value)) {
                button.setColor("green");
            } else {
                button.setColor("blue");
            }
            FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(DeviceUtil.dpToPx(5), 0, DeviceUtil.dpToPx(5), 0);
            params.setFlexGrow(FlexboxLayout.LayoutParams.FLEX_GROW_DEFAULT);
            button.setLayoutParams(params);
            button.setOnClickListener(v -> {
                for (val b : buttons) {
                    b.setColor("blue");
                }
                button.setColor("green");
                if (model.onClick == null) return;
                model.onClick.accept(option);
            });
            binding.settingContainer.addView(button);
            buttons.add(button);
        });
    }

    public void onItemClick() {
        val context = getContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.SettingItemDialog);

        builder.setTitle(model.title);

        List<String> buttonTitles = new ArrayList<>();
        if (model.type == SettingType.SWITCH) {
            buttonTitles.add(context.getString(R.string.turn_on));
            buttonTitles.add(context.getString(R.string.turn_off));
        } else if (model.type == SettingType.SELECT ||
                   model.type == SettingType.SPINNER) {
            for (val option : model.options) {
                buttonTitles.add(option.toString());
            }
        }
        buttonTitles.add(context.getString(R.string.cancel));
        final String[] titles = buttonTitles.toArray(new String[0]);
        builder.setItems(titles, (dialog, which) -> {
            if (model.type == SettingType.SWITCH) {
                if (which == 0) {
                    binding.settingSwitch.setChecked(true);
                    model.onClick.accept(true);
                } else if (which == 1) {
                    binding.settingSwitch.setChecked(false);
                    model.onClick.accept(false);
                } else {
                    dialog.dismiss();
                    return;
                }
            } else if (model.type == SettingType.SELECT) {
                if (which == model.options.size()) {
                    dialog.dismiss();
                    return;
                }
                // get i-th subview
                binding.settingContainer.getChildAt(which).performClick();
            }
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
