package top.ourfor.app.iplay.view.video;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import top.ourfor.app.iplay.databinding.PlayerAdvanceConfigBinding;
import top.ourfor.app.iplay.view.player.Player;

public class PlayerAdvanceConfigView extends ConstraintLayout {
    PlayerAdvanceConfigBinding binding;
    Player player;

    public PlayerAdvanceConfigView(@NonNull Context context) {
        super(context);
        binding = PlayerAdvanceConfigBinding.inflate(LayoutInflater.from(context), this, true);
        setup();
        bind();
    }

    void setup() {

    }

    void bind() {
        binding.subDelay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (player != null) {
                    double delay = Double.parseDouble(editable.toString());
                    player.setSubtitleDelay(delay);
                }
            }
        });

        binding.subPos.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (player != null) {
                    double position = Double.parseDouble(editable.toString());
                    player.setSubtitlePosition(position);
                }
            }
        });
    }
}
