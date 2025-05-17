package top.ourfor.app.iplay.util;

import static top.ourfor.app.iplay.module.Bean.XGET;

import android.app.Application;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Map;
import java.util.function.Consumer;

import lombok.val;
import top.ourfor.app.iplay.common.model.GitHubAsset;
import top.ourfor.app.iplay.common.model.GitHubReleaseModel;

public class PackageUtil {
    public static void checkUpdate(Consumer<Map<String, String>> completion) {
        val req = HTTPModel.<GitHubReleaseModel>builder()
                .url("https://api.github.com/repos/ourfor/iPlay/releases/latest")
                .method("GET")
                .typeReference(new TypeReference<GitHubReleaseModel>() {})
                .build();
        HTTPUtil.request(req, (res) -> {
            val model = (GitHubReleaseModel) res;
            val versionParts = model.getTagName().split("\\.");
            if (versionParts.length == 0) {
                completion.accept(null);
                return;
            }
            val versionCode = Integer.valueOf(versionParts[versionParts.length - 1]);
            val currentVersion = DeviceUtil.getVersionCode(XGET(Application.class));
            if (versionCode > currentVersion) {
                GitHubAsset resource = null;
                val arch = DeviceUtil.arch();
                for (val asset : model.getAssets()) {
                    if (asset.getName().contains(arch)) {
                        resource = asset;
                        break;
                    }
                }
                completion.accept(Map.of(
                    "url", resource.getBrowserDownloadUrl(),
                    "size", String.valueOf(resource.getSize()),
                    "version", model.getName(),
                    "content", model.getBody(),
                    "name", model.getName(),
                    "packageName", resource.getName(),
                    "publishedAt", model.getPublishedAt().toString(),
                    "versionCode", String.valueOf(versionCode)
                ));
            } else {
                completion.accept(null);
            }
        });

    }
}
