package top.ourfor.app.iplayx.page;

import static android.app.Activity.RESULT_OK;
import static top.ourfor.app.iplayx.module.Bean.XGET;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.documentfile.provider.DocumentFile;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import top.ourfor.app.iplayx.R;
import top.ourfor.app.iplayx.store.GlobalStore;
import top.ourfor.app.iplayx.util.PathUtil;

@Getter
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class ActivityEvent {
    Context context;
    ActivityResultLauncher<Intent> launcher;

    @Setter
    boolean isRequestFile = false;

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
                    val isDirectory = dir.isDirectory();
                    log.info("isDirectory: {}", isDirectory);
                    if (isDirectory) {
                        Arrays.stream(dir.listFiles()).forEach(file -> {
                            if (file.getName().endsWith("sites.json")) {
                                file.delete();
                            }
                        });
                        val file = dir.createFile("application/json", "sites.json");
                        try {
                            val os = getContext().getContentResolver().openOutputStream(file.getUri());
                            BufferedOutputStream bos = new BufferedOutputStream(os);
                            bos.write(GlobalStore.shared.toSiteJSON().getBytes());
                            bos.close();
                            os.close();
                            Toast.makeText(getContext(), R.string.local_sync_success, Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else if (dir.isFile()) {
                        try {
                            val is = getContext().getContentResolver().openInputStream(dir.getUri());
                            val store = XGET(GlobalStore.class);
                            final var content = PathUtil.getContent(is);
                            is.close();
                            store.fromSiteJSON(content);
                            Toast.makeText(getContext(), R.string.local_sync_success, Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        });
    }
}
