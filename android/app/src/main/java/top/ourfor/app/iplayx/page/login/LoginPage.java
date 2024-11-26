package top.ourfor.app.iplayx.page.login;

import static top.ourfor.app.iplayx.module.Bean.XGET;
import static top.ourfor.app.iplayx.module.Bean.XWATCH;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import top.ourfor.app.iplayx.R;
import top.ourfor.app.iplayx.action.SiteUpdateAction;
import top.ourfor.app.iplayx.api.alist.AlistApi;
import top.ourfor.app.iplayx.api.cloud189.Cloud189Api;
import top.ourfor.app.iplayx.api.emby.EmbyApi;
import top.ourfor.app.iplayx.api.jellyfin.JellyfinApi;
import top.ourfor.app.iplayx.api.onedrive.OneDriveAction;
import top.ourfor.app.iplayx.api.onedrive.OneDriveApi;
import top.ourfor.app.iplayx.api.onedrive.OneDriveAuth;
import top.ourfor.app.iplayx.api.webdav.WebDavFileApi;
import top.ourfor.app.iplayx.bean.KVStorage;
import top.ourfor.app.iplayx.action.NavigationTitleBar;
import top.ourfor.app.iplayx.bean.Navigator;
import top.ourfor.app.iplayx.common.type.ServerType;
import top.ourfor.app.iplayx.databinding.LoginPageBinding;
import top.ourfor.app.iplayx.model.drive.AlistDriveModel;
import top.ourfor.app.iplayx.model.drive.Cloud189Model;
import top.ourfor.app.iplayx.model.drive.OneDriveModel;
import top.ourfor.app.iplayx.model.drive.WebDAVModel;
import top.ourfor.app.iplayx.page.Page;
import top.ourfor.app.iplayx.util.DeviceUtil;
import top.ourfor.app.iplayx.util.LayoutUtil;
import top.ourfor.app.iplayx.model.SiteModel;
import top.ourfor.app.iplayx.store.GlobalStore;
import top.ourfor.app.iplayx.util.WindowUtil;
import top.ourfor.app.iplayx.view.TagView;

@Slf4j
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
            ServerType.Cloud189, R.string.cloud189
    );

    LoginPageBinding binding = null;
    boolean isFragment = false;
    @Setter
    public boolean isDialogModel = false;
    @Setter
    public ServerType serverType = ServerType.Emby;
    @Setter
    private SiteModel siteModel = null;
    @Setter
    private List<ServerType> allowServerType = List.of(ServerType.Emby, ServerType.Jellyfin, ServerType.Cloud189, ServerType.OneDrive);


    Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = LoginPageBinding.inflate(inflater, container, false);
        isFragment = container != null;
        val actionBar = XGET(ActionBar.class);
        if (!isDialogModel) {
            XGET(NavigationTitleBar.class).setNavTitle(R.string.login);
            actionBar.setDisplayHomeAsUpEnabled(true);
            XGET(BottomNavigationView.class).setVisibility(View.GONE);
        }
        val context = container == null ? XGET(Activity.class) : container.getContext();
        setupUI(context);
        refreshUI();
        XWATCH(OneDriveAction.class, this);
        binding.getRoot().setPadding(0, isDialogModel ? 0 : WindowUtil.defaultToolbarBottom, 0, 0);
        return binding.getRoot();
    }

    void setup(LayoutInflater inflater, ViewGroup container) {
        binding = LoginPageBinding.inflate(inflater, container, false);
        isFragment = container != null;
        val actionBar = XGET(ActionBar.class);
        if (!isDialogModel) {
            XGET(NavigationTitleBar.class).setNavTitle(R.string.login);
            actionBar.setDisplayHomeAsUpEnabled(true);
            XGET(BottomNavigationView.class).setVisibility(View.GONE);
        }
        val context = container == null ? XGET(Activity.class) : container.getContext();
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
        KVStorage kv = XGET(KVStorage.class);
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
                EmbyApi.login(server, username, password, (response) -> {
                    log.info("login response: {}", response);
                    if (response != null) {
                        kv.set(remarkKey, remake);
                        kv.set(serverKey, server);
                        kv.set(usernameKey, username);
                        kv.set(passwordKey, password);

                        val site = (SiteModel) response;
                        site.setSync(binding.allowSyncSwitch.isChecked());
                        site.setShowSensitive(binding.showSensitiveSwitch.isChecked());
                        site.setRemark(remake);
                        site.setServerType(ServerType.Emby);
                        XGET(GlobalStore.class).addNewSite(site);
                        getActivity().runOnUiThread(() -> {
                            val action = XGET(SiteUpdateAction.class);
                            action.onSiteUpdate();
                            Toast.makeText(context, "Login success", Toast.LENGTH_SHORT).show();
                            if (isFragment) {
                                XGET(Navigator.class).popPage();
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
            } else if (serverType == ServerType.Jellyfin) {
                JellyfinApi.login(server, username, password, (response) -> {
                    log.info("login response: {}", response);
                    if (response != null) {
                        kv.set(remarkKey, remake);
                        kv.set(serverKey, server);
                        kv.set(usernameKey, username);
                        kv.set(passwordKey, password);

                        val site = (SiteModel) response;
                        site.setSync(binding.allowSyncSwitch.isChecked());
                        site.setShowSensitive(binding.showSensitiveSwitch.isChecked());
                        site.setRemark(remake);
                        site.setServerType(ServerType.Jellyfin);
                        XGET(GlobalStore.class).addNewSite(site);
                        getActivity().runOnUiThread(() -> {
                            val action = XGET(SiteUpdateAction.class);
                            action.onSiteUpdate();
                            Toast.makeText(context, "Login success", Toast.LENGTH_SHORT).show();
                            if (isFragment) {
                                XGET(Navigator.class).popPage();
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
            } else if (serverType == ServerType.Cloud189) {
                loginTo189(remake, username, password);
            } else if (serverType == ServerType.OneDrive) {
                loginToOneDrive();
            } else if (serverType == ServerType.WebDAV) {
                loginToWebDAV(remake, server, username, password);
            } else if (serverType == ServerType.Alist) {
                loginToAlist(remake, server, username, password);
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
                    type != ServerType.OneDrive) {
                    Toast.makeText(context, R.string.not_implementation, Toast.LENGTH_SHORT).show();
                    return;
                }
                serverType = type;
                for (int i = 0; i < binding.serverTypeContainer.getChildCount(); i++) {
                    val child = binding.serverTypeContainer.getChildAt(i);
                    if (child instanceof TagView) {
                        val tag = (TagView) child;
                        tag.setColor(tag.equals(context.getString(value)) ? "purple" : "green");
                    }
                }
                switch (type) {
                    case Cloud189 -> setup189UI();
                    case Emby, Jellyfin -> setupEmbyUI();
                    case WebDAV -> setupWebDAVUI();
                    case OneDrive -> setupOneDriveUI();
                    case Alist -> setupAlistUI();
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

    @Override
    public void onedriveReadyUpdate(OneDriveAuth auth) {
        val drive = OneDriveModel.builder()
                .remark(binding.remarkInput.getText().toString())
                .auth(auth)
                .build();

        val store = XGET(GlobalStore.class);
        store.addDrive(drive);
        store.switchDrive(drive);
        XGET(Activity.class).runOnUiThread(() -> {
            Toast.makeText(getContext(), R.string.login_success, Toast.LENGTH_SHORT).show();
            binding.loginButton.setEnabled(true);
            if (isDialogModel) {
                dismiss();
            }
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

                val store = XGET(GlobalStore.class);
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

                val store = XGET(GlobalStore.class);
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

                val store = XGET(GlobalStore.class);
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
    }

    @Override
    public View view() {
        return binding.getRoot();
    }

    @Override
    public void create(Context context, Map<String, Object> params) {
        this.context = context;
        setup(LayoutInflater.from(context), null);
    }
}
