package top.ourfor.app.iplay.util;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
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
