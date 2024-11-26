package top.ourfor.app.iplayx.common.network;

import static top.ourfor.app.iplayx.common.network.NetworkMonitor.NetworkType.CELLULAR;
import static top.ourfor.app.iplayx.common.network.NetworkMonitor.NetworkType.INVALID;
import static top.ourfor.app.iplayx.common.network.NetworkMonitor.NetworkType.WIFI;
import static top.ourfor.app.iplayx.module.Bean.XGET;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

import lombok.val;
import top.ourfor.app.iplayx.App;

public class NetworkMonitor {
    private int uid;
    private long lastTotalRxBytes = 0;
    private long lastTimeStamp = 0;
    private static long KB = 1 << 10;
    private static long MB = 1 << 20;
    private static long GB = 1 << 30;

    public Consumer<String> onStatusUpdate;

    public NetworkMonitor() {
        uid = XGET(Application.class).getApplicationInfo().uid;
    }

    public void startMonitoring() {
        lastTotalRxBytes = getTotalRxBytes();
        lastTimeStamp = System.currentTimeMillis();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                long currentTotalRxBytes = getTotalRxBytes();
                long currentTimeStamp = System.currentTimeMillis();
                long speed = ((currentTotalRxBytes - lastTotalRxBytes) * 1000 / (currentTimeStamp - lastTimeStamp));
                lastTotalRxBytes = currentTotalRxBytes;
                lastTimeStamp = currentTimeStamp;
                String unit = "B/s";
                if (speed > GB) {
                    speed /= GB;
                    unit = "GB/s";
                } else if (speed > MB) {
                    speed /= MB;
                    unit = "MB/s";
                } else if (speed > KB) {
                    speed /= KB;
                    unit = "KB/s";
                }

                if (onStatusUpdate != null) {
                    onStatusUpdate.accept(speed + " " + unit);
                }
            }
        }, 1000, 1000);
    }

    private long getTotalRxBytes() {
        return TrafficStats.getUidRxBytes(uid) + TrafficStats.getUidTxBytes(uid);
    }

    public enum NetworkType {
        INVALID,
        WIFI,
        CELLULAR
    }

    public static NetworkType getNetWorkType(Context context) {
        NetworkType networkType = INVALID;
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String type = networkInfo.getTypeName();
            if (type.equalsIgnoreCase("WIFI")) {
                networkType = WIFI;
            } else if (type.equalsIgnoreCase("MOBILE")) {
                networkType = CELLULAR;
            }
        } else {
            networkType = INVALID;
        }
        return networkType;
    }

}
