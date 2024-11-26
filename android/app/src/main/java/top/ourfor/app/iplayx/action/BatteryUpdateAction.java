package top.ourfor.app.iplayx.action;

public interface BatteryUpdateAction {
    default void onBatteryUpdate(float percent) {}
}
