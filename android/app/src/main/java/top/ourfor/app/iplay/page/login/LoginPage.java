package top.ourfor.app.iplay.page.login;

import static top.ourfor.app.iplay.module.Bean.XGET;
import static top.ourfor.app.iplay.module.Bean.XWATCH;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import top.ourfor.app.iplay.R;
import top.ourfor.app.iplay.action.SiteUpdateAction;
import top.ourfor.app.iplay.api.alist.AlistApi;
import top.ourfor.app.iplay.api.cloud189.Cloud189Api;
import top.ourfor.app.iplay.api.emby.EmbyApi;
import top.ourfor.app.iplay.api.iplay.iPlayApi;
import top.ourfor.app.iplay.api.jellyfin.JellyfinApi;
import top.ourfor.app.iplay.api.onedrive.OneDriveAction;
import top.ourfor.app.iplay.api.onedrive.OneDriveApi;
import top.ourfor.app.iplay.api.onedrive.OneDriveAuth;
import top.ourfor.app.iplay.api.webdav.WebDavFileApi;
import top.ourfor.app.iplay.bean.IKVStorage;
import top.ourfor.app.iplay.action.NavigationTitleBar;
import top.ourfor.app.iplay.bean.INavigator;
import top.ourfor.app.iplay.common.annotation.ViewController;
import top.ourfor.app.iplay.common.type.ServerType;
import top.ourfor.app.iplay.databinding.LoginPageBinding;
import top.ourfor.app.iplay.model.drive.AlistDriveModel;
import top.ourfor.app.iplay.model.drive.Cloud189Model;
import top.ourfor.app.iplay.model.drive.LocalDriveModel;
import top.ourfor.app.iplay.model.drive.OneDriveModel;
import top.ourfor.app.iplay.model.drive.WebDAVModel;
import top.ourfor.app.iplay.page.Page;
import top.ourfor.app.iplay.util.DeviceUtil;
import top.ourfor.app.iplay.util.LayoutUtil;
import top.ourfor.app.iplay.model.SiteModel;
import top.ourfor.app.iplay.store.IAppStore;
import top.ourfor.app.iplay.util.WindowUtil;
import top.ourfor.app.iplay.view.TagView;

@Slf4j
@ViewController(name = "login_page")
public class LoginPage extends BottomSheetDialogFragment implements OneDriveAction, Page {
    private static final String remarkKey = "@site/remark";
    private static final String usernameKey = "@site/username";
    private static final String passwordKey = "@site/password";
    private static final String serverKey = "@site/server";
    private static final Map<ServerType, Integer> serverTypeMap = Map.of(
            ServerType.Emby, R.string.emby,
            ServerType.Jellyfin, R.string.jellyfin,
            ServerType.Plex, R.string.plex,
            ServerType.OneDrive, R.string.onedrive,
            ServerType.WebDAV, R.string.webdav,
            ServerType.IPTV, R.string.iptv,
            ServerType.Alist, R.string.alist,
            ServerType.Cloud189, R.string.cloud189,
            ServerType.iPlay, R.string.iplay,
            ServerType.Local, R.string.local_file
    );

    LoginPageBinding binding = null;
    boolean isPage = false;
    @Setter
    public boolean isDialogModel = false;
    @Setter
    public ServerType serverType = ServerType.Emby;
    @Setter
    private SiteModel siteModel = null;
    @Setter
    private List<ServerType> allowServerType = List.of(
            ServerType.Emby,
            ServerType.Jellyfin,
            ServerType.iPlay,
            ServerType.Cloud189,
            ServerType.OneDrive
    );


    Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = LoginPageBinding.inflate(inflater, container, false);
        isPage = false;
        val actionBar = XGET(ActionBar.class);
        if (!isDialogModel) {
            XGET(NavigationTitleBar.class).setNavTitle(R.string.login);
            actionBar.setDisplayHomeAsUpEnabled(true);
            XGET(BottomNavigationView.class).setVisibility(View.GONE);
        }
        if (context == null) {
            context = container == null ? XGET(Activity.class) : container.getContext();
        }
        setupUI(context);
        refreshUI();
        XWATCH(OneDriveAction.class, this);
        binding.getRoot().setPadding(0, isDialogModel ? 0 : WindowUtil.defaultToolbarBottom, 0, 0);
        return binding.getRoot();
    }

    void setup(LayoutInflater inflater, ViewGroup container) {
        binding = LoginPageBinding.inflate(inflater, container, false);
        val actionBar = XGET(ActionBar.class);
        if (!isDialogModel) {
            XGET(NavigationTitleBar.class).setNavTitle(R.string.login);
            actionBar.setDisplayHomeAsUpEnabled(true);
            XGET(BottomNavigationView.class).setVisibility(View.GONE);
        }
        if (context == null) {
            context = container == null ? XGET(Activity.class) : container.getContext();
        }
        setupUI(context);
        refreshUI();
        XWATCH(OneDriveAction.class, this);
        binding.getRoot().setPadding(0, isDialogModel ? 0 : WindowUtil.defaultToolbarBottom, 0, 0);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.BottomSheetDialog);
        val dialog = super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(dialogInterface -> {
            val view = (BottomSheetDialog) dialogInterface;
            view.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        });
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!isDialogModel) return;
        View view = getView();
        if (view != null) {
            View parent = (View) view.getParent();
            BottomSheetBehavior behavior = BottomSheetBehavior.from(parent);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    @SuppressLint("NewApi")
    void setupUI(Context context) {
        if (siteModel != null) {
            binding.remarkInput.setText(siteModel.getEndpoint().getRemark());
            binding.serverInput.setText(siteModel.getEndpoint().getBaseUrl());
            binding.usernameInput.setText(siteModel.getUserName());
            binding.passwordInput.setText("");
            binding.allowSyncSwitch.setChecked(siteModel.isSync());
            binding.showSensitiveSwitch.setChecked(siteModel.isShowSensitive());
            serverType = siteModel.getServerType();
        }

        binding.passwordInput.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        binding.passwordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
        // add login on click listener
        binding.loginButton.setOnClickListener(v -> {
            binding.loginButton.setEnabled(false);
            binding.loginButton.setText(R.string.login_ing);
            // get remake, server, username, password
            var remake = binding.remarkInput.getText().toString();
            var server = binding.serverInput.getText().toString();
            var username = binding.usernameInput.getText().toString();
            var password = binding.passwordInput.getText().toString();
            // do login
            log.info("remake: {}, server: {}, username: {}, password: {}", remake, server, username, password);
            if (serverType == ServerType.Emby) {
                loginToEmby(remake, server, username, password);
            } else if (serverType == ServerType.Jellyfin) {
                loginToJellyfin(remake, server, username, password);
            } else if (serverType == ServerType.Cloud189) {
                loginTo189(remake, username, password);
            } else if (serverType == ServerType.OneDrive) {
                loginToOneDrive();
            } else if (serverType == ServerType.WebDAV) {
                loginToWebDAV(remake, server, username, password);
            } else if (serverType == ServerType.Alist) {
                loginToAlist(remake, server, username, password);
            } else if (serverType == ServerType.Local) {
                loginToLocalDrive();
            } else if (serverType == ServerType.iPlay) {
                loginToiPlay(remake, server, username, password);
            }
        });

        for (var type : allowServerType) {
            val value = serverTypeMap.get(type);
            val tagView = new TagView(context);
            tagView.setText(context.getString(value));
            tagView.setColor(serverType == type ? "purple" : "green");
            val fit = LayoutUtil.fit();
            fit.setMargins(DeviceUtil.dpToPx(5), DeviceUtil.dpToPx(5), DeviceUtil.dpToPx(5), DeviceUtil.dpToPx(5));
            tagView.setLayoutParams(fit);
            tagView.setOnClickListener(v -> {
                if (type != ServerType.Emby &&
                    type != ServerType.Jellyfin &&
                    type != ServerType.Cloud189 &&
                    type != ServerType.WebDAV &&
                    type != ServerType.Alist &&
                    type != ServerType.iPlay &&
                    type != ServerType.Local &&
                    type != ServerType.OneDrive) {
                    Toast.makeText(context, R.string.not_implementation, Toast.LENGTH_SHORT).show();
                    return;
                }
                serverType = type;
                for (int i = 0; i < binding.serverTypeContainer.getChildCount(); i++) {
                    val child = binding.serverTypeContainer.getChildAt(i);
                    if (child instanceof TagView tag) {
                        tag.setColor(tag.equals(context.getString(value)) ? "purple" : "green");
                    }
                }
                switch (type) {
                    case Cloud189 -> setup189UI();
                    case Emby, Jellyfin -> setupEmbyUI();
                    case WebDAV -> setupWebDAVUI();
                    case OneDrive -> setupOneDriveUI();
                    case Local -> setupLocalDriveUI();
                    case Alist -> setupAlistUI();
                    case iPlay -> setupiPlayUI();
                    default -> { }
                }
                tagView.setColor("purple");
            });

            if (DeviceUtil.isTV) {
                tagView.setFocusable(true);
                View.OnFocusChangeListener onFocusChangeListener = (v, hasFocus) -> {
                    v.setScaleX(hasFocus ? 1.15f : 1.0f);
                    v.setScaleY(hasFocus ? 1.15f : 1.0f);
                };
                tagView.setOnFocusChangeListener(onFocusChangeListener);
            }
            binding.serverTypeContainer.addView(tagView);
        }

        if (DeviceUtil.isTV) {
            binding.loginButton.setFocusable(true);
            View.OnFocusChangeListener onFocusChangeListener = (v, hasFocus) -> {
                v.setBackgroundResource(hasFocus ? R.drawable.button_focus : R.drawable.button_normal);
            };
            binding.loginButton.setOnFocusChangeListener(onFocusChangeListener);
        }
    }

    private void loginToOneDrive() {
        val intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(OneDriveApi.auth()));
        startActivity(intent);
    }

    private void loginToLocalDrive() {
        var canAccessAllFile = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] perms = {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE
            };
            for (String p : perms) {
                int ret = ContextCompat.checkSelfPermission(XGET(Application.class), p);
                if (ret != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(perms, 0XCF);
                    break;
                } else {
                    canAccessAllFile = true;
                }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + XGET(Application.class).getPackageName()));
                startActivityForResult(intent, 1024);
            } else {
                canAccessAllFile = true;
            }
        }

        if (!canAccessAllFile) {
            return;
        }

        val remark = binding.remarkInput.getText().toString();
        val drive = LocalDriveModel.builder()
                .remark(remark.isBlank() || remark.isEmpty() ? "/" : remark)
                .path("/")
                .build();
        val store = XGET(IAppStore.class);
        store.addDrive(drive);
        store.switchDrive(drive);
        XGET(Activity.class).runOnUiThread(() -> {
            val resId = R.string.add_folder_success;
            Toast.makeText(getContext(), resId, Toast.LENGTH_SHORT).show();
            binding.loginButton.setEnabled(true);
            if (!isPage) {
                dismiss();
            }
        });
    }

    @Override
    public void onedriveReadyUpdate(OneDriveAuth auth) {
        val drive = OneDriveModel.builder()
                .remark(binding.remarkInput.getText().toString())
                .auth(auth)
                .build();

        val store = XGET(IAppStore.class);
        store.addDrive(drive);
        store.switchDrive(drive);
        XGET(Activity.class).runOnUiThread(() -> {
            Toast.makeText(getContext(), R.string.login_success, Toast.LENGTH_SHORT).show();
            binding.loginButton.setEnabled(true);
            if (!isPage) {
                dismiss();
            }
        });
    }

    private void loginToEmby(String remake, String server, String username, String password) {
        EmbyApi.login(server, username, password, (response) -> {
            log.info("login response: {}", response);
            if (response != null) {
                var kv = XGET(IKVStorage.class);
                assert kv != null;
                kv.set(remarkKey, remake);
                kv.set(serverKey, server);
                kv.set(usernameKey, username);
                kv.set(passwordKey, password);

                val site = (SiteModel) response;
                site.setSync(binding.allowSyncSwitch.isChecked());
                site.setShowSensitive(binding.showSensitiveSwitch.isChecked());
                site.setRemark(remake);
                site.setServerType(ServerType.Emby);
                XGET(IAppStore.class).addNewSite(site);
                XGET(Activity.class).runOnUiThread(() -> {
                    val action = XGET(SiteUpdateAction.class);
                    action.onSiteUpdate();
                    Toast.makeText(context, "Login success", Toast.LENGTH_SHORT).show();
                    if (isPage) {
                        XGET(INavigator.class).popPage();
                    } else {
                        dismiss();
                    }
                });
            } else {
                XGET(Activity.class).runOnUiThread(() ->
                        Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show()
                );
            }

            binding.loginButton.post(() -> {
                binding.loginButton.setEnabled(true);
                binding.loginButton.setText(R.string.login);
            });
        });
    }

    private void loginToJellyfin(String remake, String server, String username, String password) {
        JellyfinApi.login(server, username, password, (response) -> {
            log.info("login response: {}", response);
            if (response != null) {
                var kv = XGET(IKVStorage.class);
                assert kv != null;
                kv.set(remarkKey, remake);
                kv.set(serverKey, server);
                kv.set(usernameKey, username);
                kv.set(passwordKey, password);

                val site = (SiteModel) response;
                site.setSync(binding.allowSyncSwitch.isChecked());
                site.setShowSensitive(binding.showSensitiveSwitch.isChecked());
                site.setRemark(remake);
                site.setServerType(ServerType.Jellyfin);
                XGET(IAppStore.class).addNewSite(site);
                getActivity().runOnUiThread(() -> {
                    val action = XGET(SiteUpdateAction.class);
                    action.onSiteUpdate();
                    Toast.makeText(context, "Login success", Toast.LENGTH_SHORT).show();
                    if (isPage) {
                        XGET(INavigator.class).popPage();
                    } else {
                        dismiss();
                    }
                });
            } else {
                getActivity().runOnUiThread(() ->
                        Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show()
                );
            }

            binding.loginButton.post(() -> {
                binding.loginButton.setEnabled(true);
                binding.loginButton.setText(R.string.login);
            });
        });
    }

    private void loginToWebDAV(String remake, String server, String username, String password) {
        binding.loginButton.setEnabled(false);
        new WebDavFileApi(server, username, password).login(success -> {
            int resId;
            if (success) {
                val drive = WebDAVModel.builder()
                        .remark(binding.remarkInput.getText().toString())
                        .username(binding.usernameInput.getText().toString())
                        .password(binding.passwordInput.getText().toString())
                        .serverUrl(binding.serverInput.getText().toString())
                        .build();

                val store = XGET(IAppStore.class);
                store.addDrive(drive);
                store.switchDrive(drive);
                resId = R.string.login_success;
            } else {
                resId = R.string.login_failed;
            }
            XGET(Activity.class).runOnUiThread(() -> {
                Toast.makeText(getContext(), resId, Toast.LENGTH_SHORT).show();
                binding.loginButton.setEnabled(true);
                if (success && isDialogModel) {
                    dismiss();
                }
            });
        });
    }

    private void loginToAlist(String remake, String server, String username, String password) {
        binding.loginButton.setEnabled(false);
        // guest login for alist, if username and password are empty
        if (username.isEmpty() && password.isEmpty()) {
            int resId;
            val drive = AlistDriveModel.builder()
                    .remark(binding.remarkInput.getText().toString())
                    .username(binding.usernameInput.getText().toString())
                    .password(binding.passwordInput.getText().toString())
                    .server(binding.serverInput.getText().toString())
                    .token("")
                    .build();

            val store = XGET(IAppStore.class);
            store.addDrive(drive);
            store.switchDrive(drive);
            resId = R.string.login_success;
            XGET(Activity.class).runOnUiThread(() -> {
                Toast.makeText(getContext(), resId, Toast.LENGTH_SHORT).show();
                binding.loginButton.setEnabled(true);
                if (isDialogModel) {
                    dismiss();
                }
            });
            return;
        }

        AlistApi.login(server, username, password, token -> {
            boolean success = token != null;
            int resId;
            if (success) {
                val drive = AlistDriveModel.builder()
                        .remark(binding.remarkInput.getText().toString())
                        .username(binding.usernameInput.getText().toString())
                        .password(binding.passwordInput.getText().toString())
                        .server(binding.serverInput.getText().toString())
                        .token(token)
                        .build();

                val store = XGET(IAppStore.class);
                store.addDrive(drive);
                store.switchDrive(drive);
                resId = R.string.login_success;
            } else {
                resId = R.string.login_failed;
            }
            XGET(Activity.class).runOnUiThread(() -> {
                Toast.makeText(getContext(), resId, Toast.LENGTH_SHORT).show();
                binding.loginButton.setEnabled(true);
                if (success && isDialogModel) {
                    dismiss();
                }
            });
        });
    }

    private void loginToiPlay(String remake, String server, String username, String password) {
        binding.loginButton.setEnabled(false);
        iPlayApi.login(server, username, password, response -> {
            boolean success = response != null;
            int resId;
            if (success && response instanceof SiteModel newSite) {
                val store = XGET(IAppStore.class);
                newSite.setRemark(remake);
                newSite.setServerType(ServerType.iPlay);
                store.addNewSite(newSite);
                resId = R.string.login_success;
            } else {
                resId = R.string.login_failed;
            }
            XGET(Activity.class).runOnUiThread(() -> {
                Toast.makeText(getContext(), resId, Toast.LENGTH_SHORT).show();
                binding.loginButton.setEnabled(true);
                if (success && isDialogModel) {
                    dismiss();
                }
            });
        });
    }

    private void loginTo189(String remake, String username, String password) {
        binding.loginButton.setEnabled(false);
        val api = new Cloud189Api();
        api.setUsername(username);
        api.setPassword(password);
        XGET(ThreadPoolExecutor.class).submit(() -> {
            val token = api.login(username, password);
            boolean success;
            if (token != null) {
                val drive = Cloud189Model.builder()
                        .remark(binding.remarkInput.getText().toString())
                        .username(username)
                        .password(password)
                        .cookie(token)
                        .build();

                val store = XGET(IAppStore.class);
                store.addDrive(drive);
                store.switchDrive(drive);
                success = true;
            } else {
                success = false;
            }

            XGET(Activity.class).runOnUiThread(() -> {
                val resId = success ? R.string.login_success : R.string.login_failed;
                Toast.makeText(getContext(), resId, Toast.LENGTH_SHORT).show();
                binding.loginButton.setEnabled(true);
                if (success && isDialogModel) {
                    dismiss();
                }
            });
        });
    }


    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
        isDialogModel = true;
        super.show(manager, tag);
    }

    void refreshUI() {
        switch (serverType) {
            case Emby, Jellyfin -> setupEmbyUI();
            case WebDAV -> setupWebDAVUI();
            case Cloud189 -> setup189UI();
            case OneDrive -> setupOneDriveUI();
            case Local -> setupLocalDriveUI();
            case Alist -> setupAlistUI();
            default -> { }
        }
    }

    private void setupEmbyUI() {
        val visible = List.of(
                binding.remarkLabel,
                binding.remarkInput,
                binding.usernameLabel,
                binding.usernameInput,
                binding.passwordLabel,
                binding.passwordInput,
                binding.loginButton,
                binding.serverLabel,
                binding.serverInput,
                binding.allowSyncLabel,
                binding.allowSyncSwitch,
                binding.showSensitiveLabel,
                binding.showSensitiveSwitch
        );
        visible.forEach(view -> view.setVisibility(View.VISIBLE));
        binding.loginButton.setText(R.string.login);
    }

    private void setupiPlayUI() {
        val visible = List.of(
                binding.remarkLabel,
                binding.remarkInput,
                binding.usernameLabel,
                binding.usernameInput,
                binding.passwordLabel,
                binding.passwordInput,
                binding.loginButton,
                binding.serverLabel,
                binding.serverInput,
                binding.allowSyncLabel,
                binding.allowSyncSwitch,
                binding.showSensitiveLabel,
                binding.showSensitiveSwitch
        );
        visible.forEach(view -> view.setVisibility(View.VISIBLE));
        binding.loginButton.setText(R.string.login);
    }

    void setupAlistUI() {
        val visible = List.of(
                binding.remarkLabel,
                binding.remarkInput,
                binding.usernameLabel,
                binding.usernameInput,
                binding.passwordLabel,
                binding.passwordInput,
                binding.loginButton,
                binding.serverLabel,
                binding.serverInput,
                binding.allowSyncLabel,
                binding.allowSyncSwitch,
                binding.showSensitiveLabel,
                binding.showSensitiveSwitch
        );
        visible.forEach(view -> view.setVisibility(View.VISIBLE));
        binding.loginButton.setText(R.string.login);
    }

    void setup189UI() {
        val visible = List.of(
                binding.remarkLabel,
                binding.remarkInput,
                binding.usernameLabel,
                binding.usernameInput,
                binding.passwordLabel,
                binding.passwordInput,
                binding.loginButton
        );
        val invisible = List.of(
                binding.serverInput,
                binding.allowSyncLabel,
                binding.allowSyncSwitch,
                binding.showSensitiveLabel,
                binding.showSensitiveSwitch,
                binding.serverLabel
        );
        visible.forEach(view -> view.setVisibility(View.VISIBLE));
        invisible.forEach(view -> view.setVisibility(View.GONE));
        binding.loginButton.setText(R.string.login);
    }

    void setupWebDAVUI() {
        val visible = List.of(
                binding.remarkLabel,
                binding.remarkInput,
                binding.usernameLabel,
                binding.usernameInput,
                binding.passwordLabel,
                binding.passwordInput,
                binding.serverLabel,
                binding.serverInput,
                binding.loginButton
        );
        val invisible = List.of(
                binding.allowSyncLabel,
                binding.allowSyncSwitch,
                binding.showSensitiveLabel,
                binding.showSensitiveSwitch
        );
        visible.forEach(view -> view.setVisibility(View.VISIBLE));
        invisible.forEach(view -> view.setVisibility(View.GONE));
        binding.loginButton.setText(R.string.login);
    }

    void setupOneDriveUI() {
        val visible = List.of(
                binding.remarkLabel,
                binding.remarkInput,
                binding.loginButton
        );
        val invisible = List.of(
                binding.usernameLabel,
                binding.usernameInput,
                binding.passwordLabel,
                binding.passwordInput,
                binding.serverLabel,
                binding.serverInput,
                binding.allowSyncLabel,
                binding.allowSyncSwitch,
                binding.showSensitiveLabel,
                binding.showSensitiveSwitch
        );
        visible.forEach(view -> view.setVisibility(View.VISIBLE));
        invisible.forEach(view -> view.setVisibility(View.GONE));
        binding.loginButton.setText(R.string.login);
    }

    void setupLocalDriveUI() {
        val visible = List.of(
                binding.remarkLabel,
                binding.remarkInput,
                binding.loginButton
        );
        val invisible = List.of(
                binding.usernameLabel,
                binding.usernameInput,
                binding.passwordLabel,
                binding.passwordInput,
                binding.serverLabel,
                binding.serverInput,
                binding.allowSyncLabel,
                binding.allowSyncSwitch,
                binding.showSensitiveLabel,
                binding.showSensitiveSwitch
        );
        visible.forEach(view -> view.setVisibility(View.VISIBLE));
        invisible.forEach(view -> view.setVisibility(View.GONE));
        binding.loginButton.setText(R.string.add_folder);
    }

    @Override
    public View view() {
        return binding.getRoot();
    }

    @Override
    public void create(Context context, Map<String, Object> params) {
        this.context = context;
        this.isPage = true;
        setup(LayoutInflater.from(context), null);
    }
}
