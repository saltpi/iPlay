#include <jni.h>
#include <stdlib.h>
#include <stdio.h>
#include <time.h>
#include <locale.h>
#include <atomic>

#include <mpv/client.h>
extern "C" {
#include <libavcodec/jni.h>
}

JavaVM *g_vm;
static jobject surface;
static mpv_handle *mpv_ctx;

static void prepare_environment(JNIEnv *env) {
    setlocale(LC_NUMERIC, "C");
    if (!env->GetJavaVM(&g_vm) && g_vm)
        av_jni_set_java_vm(g_vm, NULL);
}

static inline mpv_handle * get_attached_mpv(JNIEnv *env, jobject obj) {
    jclass cls = env->GetObjectClass(obj);
    jfieldID fid = env->GetFieldID(cls, "holder", "J");
    return reinterpret_cast<mpv_handle *>(env->GetLongField(obj, fid));
}

static inline void set_attached_mpv(JNIEnv *env, jobject obj, mpv_handle *ctx) {
    jclass cls = env->GetObjectClass(obj);
    jfieldID fid = env->GetFieldID(cls, "holder", "J");
    env->SetLongField(obj, fid, reinterpret_cast<jlong>(ctx));
}


extern "C"
JNIEXPORT void JNICALL
Java_top_ourfor_lib_mpv_MPV_create(JNIEnv *env, jobject self) {
    prepare_environment(env);
    mpv_handle *ctx = mpv_create();
    set_attached_mpv(env, self, ctx);
    mpv_request_log_messages(ctx, "terminal-default");
    mpv_set_option_string(ctx, "msg-level", "all=v");
}

extern "C"
JNIEXPORT void JNICALL
Java_top_ourfor_lib_mpv_MPV_init(JNIEnv *env, jobject self) {
    mpv_handle *ctx = get_attached_mpv(env, self);
    mpv_initialize(ctx);
}

extern "C"
JNIEXPORT void JNICALL
Java_top_ourfor_lib_mpv_MPV_destroy(JNIEnv *env, jobject self) {
    mpv_handle *ctx = get_attached_mpv(env, self);
    if (ctx) {
        mpv_destroy(ctx);
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_top_ourfor_lib_mpv_MPV_setDrawable(JNIEnv *env, jobject self, jobject surface_) {
    mpv_handle *ctx = get_attached_mpv(env, self);
    surface = env->NewGlobalRef(surface_);
    int64_t wid = (int64_t)(intptr_t) surface;
    mpv_set_option(ctx, "wid", MPV_FORMAT_INT64, (void*) &wid);
}

extern "C"
JNIEXPORT void JNICALL
Java_top_ourfor_lib_mpv_MPV_command(JNIEnv *env, jobject self, jobjectArray cmd) {
    mpv_handle *ctx = get_attached_mpv(env, self);
    const char *arguments[128] = { 0 };
    int len = env->GetArrayLength(cmd);
    for (int i = 0; i < len; ++i)
        arguments[i] = env->GetStringUTFChars((jstring)env->GetObjectArrayElement(cmd, i), NULL);
    mpv_command(ctx, arguments);

    for (int i = 0; i < len; ++i)
        env->ReleaseStringUTFChars((jstring)env->GetObjectArrayElement(cmd, i), arguments[i]);
}

extern "C"
JNIEXPORT jint JNICALL
Java_top_ourfor_lib_mpv_MPV_setOptionString(JNIEnv *env, jobject self, jstring jname,
                                            jstring jvalue) {
    mpv_handle *ctx = get_attached_mpv(env, self);
    const char *option = env->GetStringUTFChars(jname, NULL);
    const char *value = env->GetStringUTFChars(jvalue, NULL);
    int result = mpv_set_option_string(ctx, option, value);
    env->ReleaseStringUTFChars(jname, option);
    env->ReleaseStringUTFChars(jvalue, value);
    return result;

}