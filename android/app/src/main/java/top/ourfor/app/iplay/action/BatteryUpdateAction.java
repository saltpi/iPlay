package top.ourfor.app.iplay.action;

public interface BatteryUpdateAction {
    default void onBatteryUpdate(float percent) {}
}
