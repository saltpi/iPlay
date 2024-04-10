package top.ourfor.app.iPlayClient.helper;

import lombok.AllArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
public class IntervalCaller {
    // default 500ms
    @Setter
    private long interval = 500;
    private long lastCallTime = 0;

    public void invoke(Runnable runnable) {
        long now = System.currentTimeMillis();
        if (now - lastCallTime >= interval) {
            lastCallTime = now;
            runnable.run();
        }
    }
}
