package top.ourfor.app.iplayx.common.device;

import static top.ourfor.app.iplayx.module.Bean.XGET;

import android.app.Application;
import android.os.BatteryManager;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

import lombok.val;

public class BatteryMonitor {
    public Consumer<String> onStatusUpdate;
    public void startMonitoring() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (onStatusUpdate != null) {
                    onStatusUpdate.accept(getBatteryCurrent() + "%");
                }
            }
        }, 1000, 30 * 1000);
    }

    public static int getBatteryCurrent() {
        int capacity = 0;
        try {
            val context = XGET(Application.class);
            BatteryManager manager = (BatteryManager) context.getSystemService(context.BATTERY_SERVICE);
            capacity = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        } catch (Exception e) {

        }
        return capacity;
    }
}
