package top.ourfor.app.iplayx.util;
import static top.ourfor.app.iplayx.module.Bean.XGET;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import top.ourfor.app.iplayx.bean.JSONAdapter;

public class NetworkUtil {
    public static class SiteInfo {
        public String country;
        public String countryFlag;
        public String delay;

        public SiteInfo(String country, String countryFlag, String delay) {
            this.country = country;
            this.countryFlag = countryFlag;
            this.delay = delay;
        }
    }

    public static String emojiFlagForCountryCode(String countryCode) {
        if (countryCode == null || countryCode.length() != 2) {
            return "\uD83C\uDF0D";
        }

        // Convert country code to uppercase
        countryCode = countryCode.toUpperCase();

        // Calculate the regional indicator symbols' Unicode values
        char firstChar = countryCode.charAt(0);
        char secondChar = countryCode.charAt(1);

        // Regional indicator symbols' Unicode values start from 0x1F1E6, corresponding to 'A'
        int firstFlagChar = 0x1F1E6 + (firstChar - 'A');
        int secondFlagChar = 0x1F1E6 + (secondChar - 'A');

        // Combine the two regional indicator symbols to form the flag emoji
        return new String(Character.toChars(firstFlagChar)) + new String(Character.toChars(secondFlagChar));
    }

    public static void getSiteLineInfo(String url, SiteInfoCallback callback) {
        new AsyncTask<String, Void, SiteInfo>() {
            @Override
            protected SiteInfo doInBackground(String... params) {
                try {
                    URI uri = new URI(params[0]);
                    String host = uri.getHost();
                    String ipAddress = getIpAddress(host);
                    String countryCode = getCountryCodeFromIp(ipAddress);
                    long delay = getHttpDelay(params[0]);
                    return new SiteInfo(countryCode, emojiFlagForCountryCode(countryCode), delay + "ms");
                } catch (Exception e) {
                    Log.e("NetworkUtil", "Error: " + e.getMessage());
                    return null;
                }
            }

            @Override
            protected void onPostExecute(SiteInfo siteInfo) {
                callback.onResult(siteInfo);
            }
        }.execute(url);
    }

    private static String getIpAddress(String host) throws IOException {
        InetAddress address = InetAddress.getByName(host);
        return address.getHostAddress();
    }

    private static String getCountryCodeFromIp(String ipAddress) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://ipinfo.io/" + ipAddress + "/json")
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            String responseBody = response.body().string();
            var json = XGET(JSONAdapter.class).fromJSON(responseBody, Map.class);
            return json.get("country").toString();
        }
    }

    private static long getHttpDelay(String url) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .build();

        long startTime = System.nanoTime();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        }
        long endTime = System.nanoTime();
        return TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
    }

    public interface SiteInfoCallback {
        void onResult(SiteInfo siteInfo);
    }
}
