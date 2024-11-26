package top.ourfor.app.iplayx.util;

import java.util.Timer;
import java.util.TimerTask;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.With;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdleCaller {

    @Setter
    @Builder.Default
    private long interval = 5000; // default 5000ms
    @Builder.Default
    private long lastCallTime = 0;

    @With
    @Setter
    private Runnable runnable;

    private Timer timer;

    public void schedule() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        // disable repeated execution

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (runnable == null) {
                    return;
                }
                lastCallTime = System.currentTimeMillis();
                runnable.run();
            }
        }, interval);
    }

}
