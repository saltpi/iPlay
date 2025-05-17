package top.ourfor.app.iplay.module;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HttpConfig {

    public static class UserAgentInterceptor implements Interceptor {
        private final String userAgent;

        public UserAgentInterceptor(String userAgent) {
            this.userAgent = userAgent;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            Request.Builder requestBuilder = originalRequest.newBuilder()
                    .header("User-Agent", userAgent);
            Request modifiedRequest = requestBuilder.build();
            return chain.proceed(modifiedRequest);
        }
    }
}
