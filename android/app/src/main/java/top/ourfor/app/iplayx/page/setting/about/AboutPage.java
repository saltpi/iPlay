package top.ourfor.app.iplayx.page.setting.about;

import static android.text.Html.FROM_HTML_MODE_LEGACY;
import static top.ourfor.app.iplayx.module.Bean.XGET;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import com.azhon.appupdate.manager.DownloadManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import top.ourfor.app.iplayx.R;
import top.ourfor.app.iplayx.action.NavigationTitleBar;
import top.ourfor.app.iplayx.databinding.AboutPageBinding;
import top.ourfor.app.iplayx.page.Page;
import top.ourfor.app.iplayx.util.DateTimeUtil;
import top.ourfor.app.iplayx.util.DeviceUtil;
import top.ourfor.app.iplayx.util.PackageUtil;
import top.ourfor.app.iplayx.util.PathUtil;
import top.ourfor.app.iplayx.util.WindowUtil;

@Slf4j
public class AboutPage implements Page {
    AboutPageBinding binding = null;

    @Getter
    Context context;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = AboutPageBinding.inflate(inflater, container, false);
        binding.getRoot().setPadding(0, WindowUtil.defaultToolbarBottom, 0, 0);
        setupUI(context);
        return binding.getRoot();
    }

    @Override
    public void viewDidAppear() {
        val actionBar = XGET(ActionBar.class);
        XGET(NavigationTitleBar.class).setNavTitle(R.string.page_about);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    void setupUI(Context context) {
        binding.checkUpdateButton.setOnClickListener(v -> {
            checkUpdate();
        });

        binding.appNote.setMovementMethod(LinkMovementMethod.getInstance());
        binding.appNote.setText(Html.fromHtml(context.getString(R.string.app_note_html), FROM_HTML_MODE_LEGACY));
        binding.version.setText(DeviceUtil.getVersionName(context) + " - " + DeviceUtil.getVersionCode(context));
    }

    void checkUpdate() {
        XGET(ThreadPoolExecutor.class).execute(() -> {
            PackageUtil.checkUpdate(info -> {
                if (info == null) return;
                log.info("update info: {}", info);
                DownloadManager manager = new DownloadManager.Builder(XGET(Activity.class))
                        .apkUrl(info.getOrDefault("url", ""))
                        .apkName(info.getOrDefault("packageName", "iPlay.apk"))
                        .smallIcon(R.mipmap.ic_launcher)
                        .showNewerToast(true)
                        .apkVersionCode(Integer.valueOf(info.getOrDefault("versionCode", "0")))
                        .apkVersionName(info.getOrDefault("version", "0.0.0"))
                        .apkSize(PathUtil.formatSize(info.getOrDefault("size", "0")))
                        .apkDescription(info.getOrDefault("content", ""))
                        .build();
                manager.download();
            });
        });
    }

    void exportLog() {
        val datetime = DateTimeUtil.formatDateTime(new Date());
        String fileName = "iplayx-" + datetime + ".txt";
        File logFile = new File(getContext().getFilesDir(), "trace.log");
        File exportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        File exportFile = new File(exportDir, fileName);

        try (InputStream in = new FileInputStream(logFile);
             OutputStream out = new FileOutputStream(exportFile)) {

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            Toast.makeText(getContext(), getContext().getString(R.string.export_log_success) + exportFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch(IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), getContext().getString(R.string.export_log_failed) + exportFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public View view() {
        return binding.getRoot();
    }

    @Override
    public void create(Context context, Map<String, Object> params) {
        this.context = context;
        onCreateView(LayoutInflater.from(context), null, null);
    }
}
