package top.ourfor.app.iplayx.module;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.engine.executor.GlideExecutor;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;

import java.io.InputStream;

import lombok.val;
import okhttp3.OkHttpClient;
import top.ourfor.app.iplayx.config.AppSetting;

@GlideModule
public class PictureManager extends AppGlideModule {
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        if (AppSetting.shared.usePictureMultiThread) {
            builder.setSourceExecutor(GlideExecutor.newUnlimitedSourceExecutor());
        }
    }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new HttpConfig.UserAgentInterceptor("iPlay"));
        val build = builder.build();
        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(build));
    }
}
