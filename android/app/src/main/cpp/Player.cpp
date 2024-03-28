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

JavaVM *jvm;
static jobject surface;

static void prepare_environment(JNIEnv *env) {
    setlocale(LC_NUMERIC, "C");
    if (!env->GetJavaVM(&jvm) && jvm)
        av_jni_set_java_vm(jvm, NULL);
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
        mpv_terminate_destroy(ctx);
        set_attached_mpv(env, self, nullptr);
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_top_ourfor_lib_mpv_MPV_setDrawable(JNIEnv *env, jobject self, jobject surface_) {
    mpv_handle *ctx = get_attached_mpv(env, self);
    if (ctx == nullptr) return;
    surface = env->NewGlobalRef(surface_);
    int64_t wid = (int64_t)(intptr_t) surface;
    mpv_set_option(ctx, "wid", MPV_FORMAT_INT64, (void*) &wid);
}

extern "C"
JNIEXPORT void JNICALL
Java_top_ourfor_lib_mpv_MPV_command(JNIEnv *env, jobject self, jobjectArray cmd) {
    mpv_handle *ctx = get_attached_mpv(env, self);
    if (ctx == nullptr) return;
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
    if (ctx == nullptr) return -1;
    const char *option = env->GetStringUTFChars(jname, NULL);
    const char *value = env->GetStringUTFChars(jvalue, NULL);
    int result = mpv_set_option_string(ctx, option, value);
    env->ReleaseStringUTFChars(jname, option);
    env->ReleaseStringUTFChars(jvalue, value);
    return result;

}

extern "C"
JNIEXPORT jboolean JNICALL
Java_top_ourfor_lib_mpv_MPV_getBoolProperty(JNIEnv *env, jobject thiz, jstring key) {
    mpv_handle *ctx = get_attached_mpv(env, thiz);
    if (ctx == nullptr) return false;
    const mpv_format format = MPV_FORMAT_FLAG;
    int data;
    const char *prop = env->GetStringUTFChars(key, NULL);
    mpv_get_property(ctx, prop, format, &data);
    env->ReleaseStringUTFChars(key, prop);
    return data == 1;
}

extern "C"
JNIEXPORT jint JNICALL
Java_top_ourfor_lib_mpv_MPV_setBoolProperty(JNIEnv *env, jobject thiz, jstring key, jboolean flag) {
    mpv_handle *ctx = get_attached_mpv(env, thiz);
    if (ctx == nullptr) return -1;
    const mpv_format format = MPV_FORMAT_FLAG;
    int data = flag;
    const char *prop = env->GetStringUTFChars(key, NULL);
    int state = mpv_set_property(ctx, prop, format, &data);
    env->ReleaseStringUTFChars(key, prop);
    return state;
}

extern "C"
JNIEXPORT jlong JNICALL
Java_top_ourfor_lib_mpv_MPV_getLongProperty(JNIEnv *env, jobject thiz, jstring key) {
    mpv_handle *ctx = get_attached_mpv(env, thiz);
    if (ctx == nullptr) return -1;
    const mpv_format format = MPV_FORMAT_INT64;
    long data;
    const char *prop = env->GetStringUTFChars(key, NULL);
    mpv_get_property(ctx, prop, format, &data);
    env->ReleaseStringUTFChars(key, prop);
    return data;
}

extern "C"
JNIEXPORT jint JNICALL
Java_top_ourfor_lib_mpv_MPV_setLongProperty(JNIEnv *env, jobject thiz, jstring key, jlong value) {
    mpv_handle *ctx = get_attached_mpv(env, thiz);
    if (ctx == nullptr) return -1;
    const mpv_format format = MPV_FORMAT_INT64;
    long data = value;
    const char *prop = env->GetStringUTFChars(key, NULL);
    int state = mpv_set_property(ctx, prop, format, &data);
    env->ReleaseStringUTFChars(key, prop);
    return state;
}

extern "C"
JNIEXPORT jdouble JNICALL
Java_top_ourfor_lib_mpv_MPV_getDoubleProperty(JNIEnv *env, jobject thiz, jstring key) {
    mpv_handle *ctx = get_attached_mpv(env, thiz);
    if (ctx == nullptr) return -1;
    const mpv_format format = MPV_FORMAT_DOUBLE;
    double data;
    const char *prop = env->GetStringUTFChars(key, NULL);
    mpv_get_property(ctx, prop, format, &data);
    env->ReleaseStringUTFChars(key, prop);
    return data;
}

extern "C"
JNIEXPORT jint JNICALL
Java_top_ourfor_lib_mpv_MPV_setDoubleProperty(JNIEnv *env, jobject thiz, jstring key,
                                              jdouble value) {
    mpv_handle *ctx = get_attached_mpv(env, thiz);
    if (ctx == nullptr) return -1;
    const mpv_format format = MPV_FORMAT_DOUBLE;
    const char *prop = env->GetStringUTFChars(key, NULL);
    int state = mpv_set_property(ctx, prop, format, &value);
    env->ReleaseStringUTFChars(key, prop);
    return state;
}

extern "C"
JNIEXPORT jint JNICALL
Java_top_ourfor_lib_mpv_MPV_observeProperty(JNIEnv *env, jobject thiz, jlong reply_userdata,
                                            jstring name, jint format) {
    mpv_handle *ctx = get_attached_mpv(env, thiz);
    if (ctx == nullptr) return -1;
    const char *prop = env->GetStringUTFChars(name, NULL);
    int state = mpv_observe_property(ctx, reply_userdata, prop, static_cast<mpv_format>(format));
    env->ReleaseStringUTFChars(name, prop);
    return state;
}

extern "C"
JNIEXPORT jobject JNICALL
Java_top_ourfor_lib_mpv_MPV_waitEvent(JNIEnv *env, jobject thiz, jdouble timeout) {
    mpv_handle *ctx = get_attached_mpv(env, thiz);
    if (ctx == nullptr) return nullptr;
    mpv_event *event = mpv_wait_event(ctx, timeout);

    jclass cls = env->FindClass("top/ourfor/lib/mpv/MPV$Event");
    if (cls == nullptr) {
        return nullptr; // class not found
    }

    jfieldID typeFieldID = env->GetFieldID(cls, "type", "I");
    jfieldID propFieldID = env->GetFieldID(cls, "prop", "Ljava/lang/String;");
    jfieldID formatFieldID = env->GetFieldID(cls, "format", "I");
    if (typeFieldID == nullptr ||
        propFieldID == nullptr ||
        formatFieldID == nullptr) {
        return nullptr; // field not found
    }

    jobject obj = env->NewObject(cls, env->GetMethodID(cls, "<init>", "()V"));
    if (obj == nullptr) {
        return nullptr; // object not created
    }

    env->SetIntField(obj, typeFieldID, reinterpret_cast<int>(event->event_id));
    if (event->event_id == MPV_EVENT_PROPERTY_CHANGE) {
        mpv_event_property *data = static_cast<mpv_event_property *>(event->data);
        env->SetIntField(obj, formatFieldID, reinterpret_cast<int>(data->format));
        env->SetObjectField(obj, propFieldID, env->NewStringUTF(data->name));
    }
    return obj;
}

extern "C"
JNIEXPORT jint JNICALL
Java_top_ourfor_lib_mpv_MPV_setStringProperty(JNIEnv *env, jobject thiz, jstring key,
                                              jstring value) {
    mpv_handle *ctx = get_attached_mpv(env, thiz);
    if (ctx == nullptr) return -1;
    const mpv_format format = MPV_FORMAT_STRING;
    const char *prop = env->GetStringUTFChars(key, NULL);
    const char *data = env->GetStringUTFChars(value, NULL);
    int state = mpv_set_property(ctx, prop, format, &data);
    env->ReleaseStringUTFChars(key, prop);
    env->ReleaseStringUTFChars(value, data);
    return state;
}