package top.ourfor.app.iplayx.page.setting.common;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.function.Consumer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.val;
import top.ourfor.app.iplayx.R;
import top.ourfor.app.iplayx.action.UpdateModelAction;
import top.ourfor.app.iplayx.databinding.ScriptManageBinding;
import top.ourfor.app.iplayx.util.DeviceUtil;

@NoArgsConstructor
@AllArgsConstructor
public class ScriptManageViewModel  {
    @Getter @Setter
    String value;

    @Getter @Setter
    Consumer<String> onClick;

}
