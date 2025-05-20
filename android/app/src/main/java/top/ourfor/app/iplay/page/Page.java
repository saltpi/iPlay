package top.ourfor.app.iplay.page;

import android.content.Context;
import android.view.View;

import java.util.Map;

public interface Page {
    default int id() { return -1; }
    default String title() { return null; }
    default void create(Context context, Map<String, Object> params) {}
    default void destroy() {}

    default int layoutId() { return 0; }
    default View view() { return null; }

    default void viewWillAppear() {}
    default void viewWillDisappear() {}
    default void viewDidAppear() {}
    default void viewDidDisappear() {}

}
