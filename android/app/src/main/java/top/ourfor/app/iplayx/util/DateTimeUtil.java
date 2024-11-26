package top.ourfor.app.iplayx.util;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateTimeUtil {
    private static DateTimeFormatter formatter;
    public static String formatTime(long time) {
        long hour = time / 3600;
        long minute = (time % 3600) / 60;
        long second = time % 60;
        return String.format("%02d:%02d:%02d", hour, minute, second);
    }

    public static String formatDateTime(Date time) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd.HHmmss");
        String dateString = formatter.format(time);
        return dateString;
    }
}
