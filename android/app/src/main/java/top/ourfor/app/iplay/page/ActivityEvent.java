package top.ourfor.app.iplay.page;

import static android.app.Activity.RESULT_OK;
import static top.ourfor.app.iplay.module.Bean.XGET;

import android.content.Context;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.documentfile.provider.DocumentFile;

import java.util.function.Consumer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import top.ourfor.app.iplay.R;

@Getter
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class ActivityEvent {
    Context context;
    ActivityResultLauncher<Intent> launcher;

    @Setter
    boolean isRequestFile = false;

    @Setter
    Consumer<DocumentFile> confirmCallback;

    public void register() {
        launcher = XGET(Activity.class).registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            log.info("result: {}", result);
            if (result.getResultCode() == RESULT_OK) {
                val uri = result.getData().getData();
                log.info("uri: {}", uri);
                val path = uri.getPath();
                log.info("path: {}", path);
                DocumentFile dir = null;
                try {
                    dir = isRequestFile ? DocumentFile.fromSingleUri(getContext(), uri) : DocumentFile.fromTreeUri(getContext(), uri);
                } catch (Exception e) {
                    log.error("error: ", e);
                }

                if (dir != null && dir.exists()) {
                    if (confirmCallback != null) {
                        confirmCallback.accept(dir);
                        confirmCallback = null;
                    }
                }
            }
        });
    }
}
