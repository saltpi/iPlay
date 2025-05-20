package top.ourfor.app.iplay.util;

import static top.ourfor.app.iplay.module.Bean.XGET;

import android.util.Base64;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import top.ourfor.app.iplay.bean.IJSONAdapter;

@Slf4j
public class HTTPUtil {
    static OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build();
    static OkHttpClient no320Client = new OkHttpClient.Builder()
            .followRedirects(false)
            .build();
    static Map<String, String> deviceInfo = Map.of(
        "X-Emby-Client", "iPlay",
        "X-Emby-Device-Name", "Android",
        "X-Emby-Device-Id", "9999999",
        "X-Emby-Client-Version", "v1.0.0"
    );

    public static String base64Encode(String str) {
        return Base64.encodeToString(str.getBytes(), Base64.URL_SAFE | Base64.NO_WRAP);
    }

    public static void download(String url, String path, Consumer<String> completion) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    completion.accept(null);
                    throw new IOException("Failed to download file: " + response);
                }
                var tempFile = new File(path);
                BufferedSink sink = Okio.buffer(Okio.sink(tempFile));
                sink.writeAll(response.body().source());
                sink.close();
                completion.accept(path);
            }
        });
    }


    public static <T> void request(HTTPModel<T> model, Consumer<T> completion) {
        Request.Builder builder = new Request.Builder();
//        builder.removeHeader("User-Agent");
        builder.addHeader("User-Agent", "iPlay");
        // add query
        if (model.getQuery() != null) {
            StringBuilder query = new StringBuilder();
            model.getQuery().forEach((key, value) -> query.append(key).append("=").append(value).append("&"));
            // remove query last &
            query.deleteCharAt(query.length() - 1);
            try {
                builder.url(model.getUrl() + "?" + query.toString());
            } catch (IllegalArgumentException e) {
                log.error("Invalid URL: {}", model.getUrl());
                completion.accept(null);
                return;
            }
        } else {
            try {
                builder.url(model.getUrl());
            } catch (IllegalArgumentException e) {
                log.error("Invalid URL: {}", model.getUrl());
                completion.accept(null);
                return;
            }
        }
        if (model.getHeaders() != null) {
            model.getHeaders().forEach(builder::addHeader);
        }
        if (deviceInfo != null) {
            deviceInfo.forEach(builder::addHeader);
        }
        if (model.getBody() != null) {
            String contentType = model.getHeaders() != null ? model.getHeaders().getOrDefault("Content-Type", null) : null;
            if (contentType == null) {
                builder.method(model.getMethod(), RequestBody.create(model.getBody(), null));
            } else {
                builder.method(model.getMethod(), RequestBody.create(model.getBody(), MediaType.parse(contentType)));
            }
        } else {
            if (model.getParams() != null) {
                StringBuilder body = new StringBuilder();
                model.getParams().forEach((key, value) -> body.append(key).append("=").append(value).append("&"));
                // remove query last &
                body.deleteCharAt(body.length() - 1);
                String contentType = model.getHeaders() != null ? model.getHeaders().getOrDefault("Content-Type", null) : null;
                if (contentType == null) {
                    builder.method(model.getMethod(), RequestBody.create(body.toString(), null));
                } else {
                    builder.method(model.getMethod(), RequestBody.create(body.toString(), MediaType.parse(contentType)));
                }
            } else {
                builder.method(model.getMethod(), null);
            }
        }
        OkHttpClient http = client;
        if (model.disableRedirect) {
            http = no320Client;
        }
        val req = builder.build();
        http.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                completion.accept(null);
            }

            @Override
            @SuppressWarnings("unchecked")
            public void onResponse(Call call, Response response) throws IOException {
                if (model.typeReference != null) {
                    String body = response.body().string();
                    val adapter = XGET(IJSONAdapter.class);
                    T obj = adapter.fromJSON(body, model.typeReference);
                    completion.accept(obj);
                    return;
                } else if (model.getModelClass() != null) {
                    String body = response.body().string();
                    val adapter = XGET(IJSONAdapter.class);
                    val clazz = model.getModelClass();
                    if (clazz == String.class) {
                        completion.accept((T) body);
                        return;
                    }
                    Object obj = adapter.fromJSON(body, clazz);
                    completion.accept(obj == null ? null : (T) obj);
                    return;
                }
                completion.accept((T) response);
            }
        });
    }

}
