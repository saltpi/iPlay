package top.ourfor.app.iplay.common.datetime;

import android.icu.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class DateTimeMonitor {
    public Consumer<String> onStatusUpdate;

    public void startMonitoring() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Date now = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("EEEE a hh:mm", Locale.CHINA);
                String formattedTime = sdf.format(now);
                if (onStatusUpdate != null) {
                    onStatusUpdate.accept(formattedTime);
                }
            }
        }, 1000, 45 * 1000);
    }
}
