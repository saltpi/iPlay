package top.ourfor.app.iplayx.page.file;

import static top.ourfor.app.iplayx.module.Bean.XGET;
import static top.ourfor.app.iplayx.module.Bean.XSET;
import static top.ourfor.app.iplayx.module.Bean.XWATCH;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import top.ourfor.app.iplayx.R;
import top.ourfor.app.iplayx.action.DispatchAction;
import top.ourfor.app.iplayx.action.DriveUpdateAction;
import top.ourfor.app.iplayx.action.ThemeUpdateAction;
import top.ourfor.app.iplayx.api.file.File;
import top.ourfor.app.iplayx.api.file.FileProviderFactory;
import top.ourfor.app.iplayx.api.file.FileType;
import top.ourfor.app.iplayx.bean.Navigator;
import top.ourfor.app.iplayx.common.type.ServerType;
import top.ourfor.app.iplayx.databinding.FilePageBinding;
import top.ourfor.app.iplayx.model.drive.Drive;
import top.ourfor.app.iplayx.page.Activity;
import top.ourfor.app.iplayx.page.Page;
import top.ourfor.app.iplayx.page.login.LoginPage;
import top.ourfor.app.iplayx.store.GlobalStore;
import top.ourfor.app.iplayx.util.AnimationUtil;
import top.ourfor.app.iplayx.util.DeviceUtil;
import top.ourfor.app.iplayx.util.NavigationUtil;
import top.ourfor.app.iplayx.util.PathUtil;
import top.ourfor.app.iplayx.util.WindowUtil;
import top.ourfor.app.iplayx.view.ListView;
import top.ourfor.app.iplayx.view.infra.Toolbar;
import top.ourfor.app.iplayx.view.infra.ToolbarAction;

@Slf4j
public class FilePage implements DriveUpdateAction, ThemeUpdateAction, Page {
    GlobalStore store;
    @Getter
    Context context;
    FileViewModel viewModel;
    FilePageBinding binding;

    private ListView<File> listView = null;
    private ListView<Drive> driveListView = null;
    private BottomSheetDialog dialog = null;
    private LottieAnimationView activityIndicator = null;
    private SwipeRefreshLayout swipeRefreshLayout = null;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        binding = FilePageBinding.inflate(LayoutInflater.from(context), null, false);
        store = XGET(GlobalStore.class);
        val view = binding.getRoot();
        viewModel = new FileViewModel(store);
        viewModel.getIsLoading().observe(view, isLoading -> {
            if (activityIndicator == null) return;
            if (isLoading) {
                activityIndicator.setVisibility(View.VISIBLE);
                activityIndicator.playAnimation();
            } else {
                activityIndicator.setVisibility(View.GONE);
                activityIndicator.cancelAnimation();
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        viewModel.getCacheFiles().observe(view, files -> {
            if (listView == null) return;
            listView.setItems(files);
        });
    }

    @Override
    public void viewDidAppear() {
        val toolbar = XGET(Toolbar.class);
        toolbar.inflateMenu(R.menu.drive_menu, ToolbarAction.Position.Right);
        toolbar.inflateMenu(R.menu.drive_left_menu, ToolbarAction.Position.Left);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val color = getContext().getColor(R.color.onBackground);
            val menu = toolbar.getMenu();
            val size = menu.size();
            val tint = ColorStateList.valueOf(color);
            for (int i = 0; i < size; i++) {
                menu.getItem(i).setIconTintList(tint);
            }
        }
        toolbar.setOnMenuItemClickListener(item -> {
            val itemId = item.getItemId();
            if (itemId == R.id.switch_drive) {
                showDriveSelectPanel();
            } else if (itemId == R.id.add_drive) {
                addDrive();
            }
            return true;
        });
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setupUI(getContext());
        bind();
        return binding.getRoot();
    }

    void setupUI(Context context) {
        listView = binding.fileList;
        listView.viewModel.viewCell = FileItemCellView.class;
        listView.setEmptyTipVisible(View.VISIBLE);
        activityIndicator = AnimationUtil.createActivityIndicate(context);
        activityIndicator.setVisibility(View.GONE);
        binding.getRoot().addView(activityIndicator);
        binding.getRoot().setPadding(0, WindowUtil.defaultToolbarBottom, 0, 0);
        if (!DeviceUtil.isTV) {
            swipeRefreshLayout = binding.swipeRefresh;
            swipeRefreshLayout.setOnRefreshListener(() -> {
                viewModel.listFiles(viewModel.getPath().getValue());
            });
        }

        driveListView = new ListView<>(context);
        driveListView.viewModel.viewCell = DriveViewCell.class;
        val listViewLayout = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        listViewLayout.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
        listViewLayout.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
        driveListView.setLayoutParams(listViewLayout);
        driveListView.viewModel.onClick = e -> {
            XGET(DispatchAction.class).runOnUiThread(() -> {
                if (dialog != null) {
                    dialog.dismiss();
                }
            });
            store.switchDrive(e.getModel());
        };
    }

    void bind() {
        XWATCH(ThemeUpdateAction.class, this);
        XWATCH(DriveUpdateAction.class, this);
        listView.viewModel.onClick = e -> {
            val file = e.getModel();
            if (file.getType() == FileType.DIRECTORY ||
                file.getType() == FileType.LINK) {
                val path = file.getPath();
                viewModel.getPath().postValue(path);
                viewModel.listFiles(path);
            } else if (file.getType() == FileType.FILE && PathUtil.isMedia(file.getName())) {
                log.info("file: {}", file);
                viewModel.link(file, url -> {
                    if (url == null) return;
                    val bundle = new HashMap<String, Object>();
                    bundle.put("url", url);
                    bundle.put("title", file.getName());
                    XGET(DispatchAction.class).runOnUiThread(() -> {
                        XGET(Navigator.class).pushPage(R.id.playerPage, bundle);
                    });
                });
            }
        };

        viewModel.updateIfNeed();
    }

    @Override
    public void onSelectedDriveChanged(Drive drive) {
        if (dialog != null) dialog.dismiss();
        if (drive != null) {
            val path = "/";
            viewModel.getPath().setValue(path);
            viewModel.getFileProvider().setValue(FileProviderFactory.create(drive));
            viewModel.listFiles(path);
        }
    }

    @Override
    public void onDriveRemoved(Drive drive) {
        driveListView.setItems(store.getDrives());
    }

    void showDriveSelectPanel() {
        val drive = store.getDrive();
        driveListView.viewModel.isSelected = (model) -> drive != null && model != null && model.equals(drive);
        driveListView.setItems(store.getDrives());
        driveListView.setOnTouchListener((v1, event) -> {
            if (!driveListView.listView.canScrollVertically(-1)) {
                driveListView.listView.requestDisallowInterceptTouchEvent(false);
            }else{
                driveListView.listView.requestDisallowInterceptTouchEvent(true);
            }
            return false;
        });
        dialog = new BottomSheetDialog(getContext(), R.style.SiteBottomSheetDialog);
        dialog.setOnDismissListener(dlg -> {
            ViewGroup parent = (ViewGroup) listView.getParent();
            if (parent != null) {
                parent.removeView(driveListView);
            }
        });
        val parent = (ViewGroup)driveListView.getParent();
        if (parent != null) {
            parent.removeView(driveListView);
        }
        dialog.setContentView(driveListView);
        BottomSheetBehavior behavior = BottomSheetBehavior.from((View) driveListView.getParent());
        val height = (int) (DeviceUtil.screenSize(getContext()).getHeight() * 0.6);
        behavior.setPeekHeight(height);
        dialog.show();
    }

    void addDrive() {
        LoginPage page = new LoginPage();
        page.setAllowServerType(List.of(ServerType.OneDrive, ServerType.Cloud189, ServerType.WebDAV, ServerType.Alist));
        page.setServerType(ServerType.OneDrive);
        page.show(XGET(Activity.class).getSupportFragmentManager(), "addDrive");
    }

    public void onDestroyView() {
        binding = null;
    }

    @Override
    public void create(Context context, Map<String, Object> params) {
        this.context = context;
        onCreate(null);
        onCreateView(LayoutInflater.from(context), null, null);
    }

    @Override
    public View view() {
        return this.binding.getRoot();
    }
}

