package top.ourfor.app.iplay.page.file;

import static top.ourfor.app.iplay.module.Bean.XGET;
import static top.ourfor.app.iplay.module.Bean.XSET;
import static top.ourfor.app.iplay.module.Bean.XWATCH;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import top.ourfor.app.iplay.R;
import top.ourfor.app.iplay.action.DispatchAction;
import top.ourfor.app.iplay.action.DriveUpdateAction;
import top.ourfor.app.iplay.action.ThemeUpdateAction;
import top.ourfor.app.iplay.api.file.File;
import top.ourfor.app.iplay.api.file.FileProviderFactory;
import top.ourfor.app.iplay.api.file.FileType;
import top.ourfor.app.iplay.bean.INavigator;
import top.ourfor.app.iplay.common.annotation.ViewController;
import top.ourfor.app.iplay.common.type.ServerType;
import top.ourfor.app.iplay.databinding.FilePageBinding;
import top.ourfor.app.iplay.model.drive.Drive;
import top.ourfor.app.iplay.page.Activity;
import top.ourfor.app.iplay.page.Page;
import top.ourfor.app.iplay.page.login.LoginPage;
import top.ourfor.app.iplay.store.IAppStore;
import top.ourfor.app.iplay.util.AnimationUtil;
import top.ourfor.app.iplay.util.DeviceUtil;
import top.ourfor.app.iplay.util.PathUtil;
import top.ourfor.app.iplay.util.WindowUtil;
import top.ourfor.app.iplay.view.ListView;
import top.ourfor.app.iplay.view.infra.Toolbar;
import top.ourfor.app.iplay.view.infra.ToolbarAction;

@Slf4j
@ViewController(name = "file_page")
public class FilePage implements DriveUpdateAction, ThemeUpdateAction, Page {
    IAppStore store;
    @Getter
    Context context;
    FileViewModel viewModel;
    FilePageBinding binding;

    private ListView<File> listView = null;
    private ListView<Drive> driveListView = null;
    private BottomSheetDialog dialog = null;
    private LottieAnimationView activityIndicator = null;
    private SwipeRefreshLayout swipeRefreshLayout = null;

    public void init() {
        binding = FilePageBinding.inflate(LayoutInflater.from(context), null, false);
        store = XGET(IAppStore.class);
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

    public void setup() {
        setupUI(context);
        bind();
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
                        XGET(INavigator.class).pushPage(R.id.playerPage, bundle);
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
            viewModel.getPath().postValue(path);
            viewModel.getFileProvider().postValue(FileProviderFactory.create(drive));
            viewModel.listFiles(path);
        }
    }

    @Override
    public void onDriveRemoved(Drive drive) {
        driveListView.setItems(store.getDrives());
    }

    @SuppressLint("ClickableViewAccessibility")
    void showDriveSelectPanel() {
        driveListView.viewModel.isSelected = (model) -> model != null && model.equals(store.getDrive());
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
        page.setAllowServerType(List.of(ServerType.OneDrive, ServerType.Cloud189, ServerType.WebDAV, ServerType.Alist, ServerType.Local));
        page.setServerType(ServerType.OneDrive);
        page.show(XGET(Activity.class).getSupportFragmentManager(), "addDrive");
    }

    @Override
    public void create(Context context, Map<String, Object> params) {
        this.context = context;
        init();
        setup();
    }

    @Override
    public void destroy() {
        binding = null;
    }

    @Override
    public View view() {
        return this.binding.getRoot();
    }
}

