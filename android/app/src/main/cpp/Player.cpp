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
mpv_handle *g_mpv;
static jobject surface;

static void prepare_environment(JNIEnv *env, jobject appctx) {
    setlocale(LC_NUMERIC, "C");
    if (!env->GetJavaVM(&g_vm) && g_vm)
        av_jni_set_java_vm(g_vm, NULL);
}

extern "C"
JNIEXPORT void JNICALL
Java_top_ourfor_lib_mpv_MPV_create(JNIEnv *env, jclass clazz, jobject appctx) {
    prepare_environment(env, appctx);
    g_mpv = mpv_create();
    mpv_request_log_messages(g_mpv, "terminal-default");
    mpv_set_option_string(g_mpv, "msg-level", "all=v");
}

extern "C"
JNIEXPORT void JNICALL
Java_top_ourfor_lib_mpv_MPV_init(JNIEnv *env, jclass clazz) {
    mpv_initialize(g_mpv);
}

extern "C"
JNIEXPORT void JNICALL
Java_top_ourfor_lib_mpv_MPV_destroy(JNIEnv *env, jclass clazz) {

}

extern "C"
JNIEXPORT void JNICALL
Java_top_ourfor_lib_mpv_MPV_setDrawable(JNIEnv *env, jclass clazz, jobject surface_) {
    surface = env->NewGlobalRef(surface_);
    int64_t wid = (int64_t)(intptr_t) surface;
    mpv_set_option(g_mpv, "wid", MPV_FORMAT_INT64, (void*) &wid);
}

extern "C"
JNIEXPORT void JNICALL
Java_top_ourfor_lib_mpv_MPV_command(JNIEnv *env, jclass clazz, jobjectArray cmd) {
    const char *arguments[128] = { 0 };
    int len = env->GetArrayLength(cmd);
    for (int i = 0; i < len; ++i)
        arguments[i] = env->GetStringUTFChars((jstring)env->GetObjectArrayElement(cmd, i), NULL);
    mpv_command(g_mpv, arguments);

    for (int i = 0; i < len; ++i)
        env->ReleaseStringUTFChars((jstring)env->GetObjectArrayElement(cmd, i), arguments[i]);
}

extern "C"
JNIEXPORT jint JNICALL
Java_top_ourfor_lib_mpv_MPV_setOptionString(JNIEnv *env, jclass clazz, jstring jname,
                                            jstring jvalue) {
    const char *option = env->GetStringUTFChars(jname, NULL);
    const char *value = env->GetStringUTFChars(jvalue, NULL);
    int result = mpv_set_option_string(g_mpv, option, value);
    env->ReleaseStringUTFChars(jname, option);
    env->ReleaseStringUTFChars(jvalue, value);
    return result;

}