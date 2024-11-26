package top.ourfor.app.iplayx.page.test;

import static top.ourfor.app.iplayx.module.Bean.XGET;

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
import top.ourfor.app.iplayx.R;
import top.ourfor.app.iplayx.action.NavigationTitleBar;
import top.ourfor.app.iplayx.databinding.PlayerControlBinding;

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
