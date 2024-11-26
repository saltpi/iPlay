package top.ourfor.app.iplayx.page.setting.cloud;

import static android.app.Activity.RESULT_OK;
import static top.ourfor.app.iplayx.module.Bean.XGET;
import static top.ourfor.app.iplayx.module.Bean.XWATCH;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.documentfile.provider.DocumentFile;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import top.ourfor.app.iplayx.R;
import top.ourfor.app.iplayx.action.DispatchAction;
import top.ourfor.app.iplayx.action.DriveUpdateAction;
import top.ourfor.app.iplayx.api.file.FileProvider;
import top.ourfor.app.iplayx.api.file.FileProviderFactory;
import top.ourfor.app.iplayx.action.NavigationTitleBar;
import top.ourfor.app.iplayx.common.type.ServerType;
import top.ourfor.app.iplayx.databinding.CloudPageBinding;
import top.ourfor.app.iplayx.model.drive.Drive;
import top.ourfor.app.iplayx.page.Activity;
import top.ourfor.app.iplayx.page.ActivityEvent;
import top.ourfor.app.iplayx.page.Page;
import top.ourfor.app.iplayx.page.login.LoginPage;
import top.ourfor.app.iplayx.util.DeviceUtil;
import top.ourfor.app.iplayx.store.GlobalStore;
import top.ourfor.app.iplayx.util.PathUtil;
import top.ourfor.app.iplayx.util.WindowUtil;

@Slf4j
public class CloudPage implements DriveUpdateAction, Page {

    CloudPageBinding binding = null;
    FileProvider fileProvider;

    @Getter
    Context context;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        binding = CloudPageBinding.inflate(LayoutInflater.from(context), null, false);
        XWATCH(DriveUpdateAction.class, this);
    }

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        val actionBar = XGET(ActionBar.class);
        XGET(NavigationTitleBar.class).setNavTitle(R.string.page_cloud);
        actionBar.setDisplayHomeAsUpEnabled(true);
        XGET(BottomNavigationView.class).setVisibility(View.GONE);
        setupUI(getContext());
        bind();
        binding.getRoot().setPadding(0, WindowUtil.defaultToolbarBottom, 0, 0);
        return binding.getRoot();
    }

    void setupUI(Context context) {
        if (DeviceUtil.isTV) {
            binding.fromButton.setFocusable(true);
            binding.toButton.setFocusable(true);
            View.OnFocusChangeListener onFocusChangeListener = (v, hasFocus) -> {
                v.setBackgroundResource(hasFocus ? R.drawable.button_focus : R.drawable.button_normal);
            };
            binding.fromButton.setOnFocusChangeListener(onFocusChangeListener);
            binding.toButton.setOnFocusChangeListener(onFocusChangeListener);
        }
    }

    void bind() {

        binding.fromButton.setOnClickListener(v -> {
            XGET(DispatchAction.class).runOnUiThread(() -> {
                binding.fromButton.setEnabled(false);
                binding.toButton.setEnabled(false);
            });
            fileProvider.read("iPlay/sites.json", result -> {
                XGET(DispatchAction.class).runOnUiThread(() -> {
                    binding.fromButton.setEnabled(true);
                    binding.toButton.setEnabled(true);
                });
                if (result == null) {
                    syncFailed();
                    return;
                }
                log.info("file content {}", result);
                if (result instanceof String) {
                    val store = XGET(GlobalStore.class);
                    store.fromSiteJSON((String) result);
                    syncSuccess();
                }
            });
        });

        binding.toButton.setOnClickListener(v -> {
            XGET(DispatchAction.class).runOnUiThread(() -> {
                binding.fromButton.setEnabled(false);
                binding.toButton.setEnabled(false);
            });
            fileProvider.write("iPlay/sites.json", XGET(GlobalStore.class).toSiteJSON(true), result -> {
                XGET(DispatchAction.class).runOnUiThread(() -> {
                    binding.fromButton.setEnabled(true);
                    binding.toButton.setEnabled(true);
                });
                if (result == null) {
                    syncFailed();
                    return;
                }
                if (result instanceof Boolean) {
                    val success = (Boolean) result;
                    if (success) {
                        syncSuccess();
                        return;
                    }
                }
                syncFailed();
            });
        });

        binding.syncTutorial.setMovementMethod(LinkMovementMethod.getInstance());
        binding.syncTutorial.setText(Html.fromHtml(context.getString(R.string.webdav_sync_tutorial_html), Html.FROM_HTML_MODE_LEGACY));

        binding.fromLocalButton.setOnClickListener(v -> {
            showFilePicker();
        });

        binding.toLocalButton.setOnClickListener(v -> {
            showFolderPicker();
        });

        showDriveSelection();

        binding.loginButton.setOnClickListener(v -> {
            LoginPage page = new LoginPage();
            page.setAllowServerType(List.of(ServerType.OneDrive, ServerType.WebDAV));
            page.setServerType(ServerType.OneDrive);
            page.show(XGET(Activity.class).getSupportFragmentManager(), "addDrive");
        });
    }

    @Override
    public void onDriveAdded(Drive drive) {
        XGET(DispatchAction.class).runOnUiThread(() -> {
            showDriveSelection();
        });
    }

    void showDriveSelection() {
        val store = XGET(GlobalStore.class);
        if (store.hasValidDrive()) {
            binding.loginButton.setVisibility(View.GONE);
            binding.driveSpinner.setVisibility(View.VISIBLE);
            val spinner = binding.driveSpinner;
            val drives = store.getDrives();
            val drive = drives.get(0);
            fileProvider = FileProviderFactory.create(drive);
            val options = drives.stream().map(d -> d.getRemark() != null ? d.getRemark() : d.getType().toString()).collect(Collectors.toList());
            val adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, options);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setSelection(0);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    val drive = drives.get(position);
                    fileProvider = FileProviderFactory.create(drive);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } else {
            binding.loginButton.setVisibility(View.VISIBLE);
            binding.driveSpinner.setVisibility(View.GONE);
        }
    }

    void showFilePicker() {
        val intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/json");
        val launcher = XGET(ActivityEvent.class);
        launcher.setRequestFile(true);
        launcher.getLauncher().launch(intent);
    }

    void showFolderPicker() {
        val intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        val launcher = XGET(ActivityEvent.class);
        launcher.setRequestFile(false);
        launcher.getLauncher().launch(intent);
    }

    void syncFailed() {
        XGET(DispatchAction.class).runOnUiThread(() -> {
            Toast.makeText(getContext(), R.string.webdav_sync_failed, Toast.LENGTH_SHORT).show();
        });
    }

    void syncSuccess() {
        XGET(DispatchAction.class).runOnUiThread(() -> {
            Toast.makeText(getContext(), R.string.webdav_sync_success, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void viewDidAppear() {
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
