package top.ourfor.app.iplay.page.test;

import static top.ourfor.app.iplay.module.Bean.XGET;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import lombok.val;
import top.ourfor.app.iplay.R;
import top.ourfor.app.iplay.action.NavigationTitleBar;
import top.ourfor.app.iplay.common.annotation.ViewController;
import top.ourfor.app.iplay.databinding.PlayerControlBinding;

@ViewController(name = "test_page")
public class TestPage extends Fragment {
    PlayerControlBinding binding = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        val actionBar = XGET(ActionBar.class);
        XGET(NavigationTitleBar.class).setNavTitle(R.string.page_test);
        actionBar.setDisplayHomeAsUpEnabled(true);
        XGET(BottomNavigationView.class).setVisibility(View.GONE);
        binding = PlayerControlBinding.inflate(inflater, container, false);
        setupUI(container.getContext());
        return binding.getRoot();
    }

    void setupUI(Context context) {

    }
}
