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

static inline jobjectArray get_cached_ranges(JNIEnv *env, jobject obj) {
    jclass cls = env->GetObjectClass(obj);
    jfieldID fid = env->GetFieldID(cls, "cachedRanges", "[Ltop/ourfor/lib/mpv/SeekableRange;");
    return reinterpret_cast<jobjectArray>(env->GetObjectField(obj, fid));
}

static inline void set_cached_ranges_count(JNIEnv *env, jobject obj, int value) {
    jclass cls = env->GetObjectClass(obj);
    jfieldID fid = env->GetFieldID(cls, "cachedRangeCount", "I");
    env->SetIntField(obj, fid, value);
}

static inline void set_attached_mpv(JNIEnv *env, jobject obj, mpv_handle *ctx) {
    jclass cls = env->GetObjectClass(obj);
    jfieldID fid = env->GetFieldID(cls, "holder", "J");
    jclass eventClass = env->FindClass("top/ourfor/lib/mpv/MPV$Event");
    if (eventClass != nullptr) {
        jclass rangeCls = env->FindClass("top/ourfor/lib/mpv/SeekableRange");
        auto cacheCount = 10;
        auto cachedRanges = env->NewObjectArray(cacheCount, rangeCls, NULL);
        jfieldID cachedRangesFid = env->GetFieldID(cls, "cachedRanges", "[Ltop/ourfor/lib/mpv/SeekableRange;");
        jfieldID startFieldID = env->GetFieldID(rangeCls, "start", "D");
        jfieldID endFieldID = env->GetFieldID(rangeCls, "end", "D");
        for (int i = 0; i < cacheCount; i++) {
            jobject seekable = env->NewObject(rangeCls, env->GetMethodID(cls, "<init>", "()V"));
            env->SetDoubleField(seekable, startFieldID, 0.0);
            env->SetDoubleField(seekable, endFieldID, 0.0);
            env->SetObjectArrayElement(cachedRanges, i, seekable);
        }
        env->SetObjectField(obj, cachedRangesFid, cachedRanges);
    }
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
    jfieldID replyFieldID = env->GetFieldID(cls, "reply", "I");
    jfieldID dataFieldID = env->GetFieldID(cls, "data", "J");
    if (typeFieldID == nullptr ||
        propFieldID == nullptr ||
        formatFieldID == nullptr ||
        replyFieldID == nullptr ||
        dataFieldID == nullptr) {
        return nullptr; // field not found
    }

    jobject obj = env->NewObject(cls, env->GetMethodID(cls, "<init>", "()V"));
    if (obj == nullptr) {
        return nullptr; // object not created
    }

    env->SetIntField(obj, typeFieldID, reinterpret_cast<int>(event->event_id));
    env->SetIntField(obj, replyFieldID, static_cast<int>(event->reply_userdata));
    if (event->event_id == MPV_EVENT_PROPERTY_CHANGE) {
        mpv_event_property *data = static_cast<mpv_event_property *>(event->data);
        env->SetIntField(obj, formatFieldID, reinterpret_cast<int>(data->format));
        env->SetObjectField(obj, propFieldID, env->NewStringUTF(data->name));
        env->SetLongField(obj, dataFieldID, reinterpret_cast<jlong>(data->data));
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
extern "C"
JNIEXPORT jstring JNICALL
Java_top_ourfor_lib_mpv_MPV_getStringProperty(JNIEnv *env, jobject thiz, jstring key) {
    mpv_handle *ctx = get_attached_mpv(env, thiz);
    if (ctx == nullptr) return nullptr;
    const char *prop = env->GetStringUTFChars(key, NULL);
    const char *value = mpv_get_property_string(ctx, prop);
    env->ReleaseStringUTFChars(key, prop);
    return env->NewStringUTF(value);
}
extern "C"
JNIEXPORT jobjectArray JNICALL
Java_top_ourfor_lib_mpv_MPV_seekableRanges(JNIEnv *env, jobject thiz, jlong pointer) {
    jclass cls = env->FindClass("top/ourfor/lib/mpv/SeekableRange");
    if (cls == nullptr) {
        return nullptr; // class not found
    }
    jfieldID startFieldID = env->GetFieldID(cls, "start", "D");
    jfieldID endFieldID = env->GetFieldID(cls, "end", "D");
    if (startFieldID == nullptr ||
        endFieldID == nullptr) {
        return nullptr; // field not found
    }

    mpv_node *data = reinterpret_cast<mpv_node *>(pointer);
    if (data == nullptr) return nullptr;

    mpv_node node = *data;
    for (int i = 0; i < node.u.list->num; i++) {
        if (strcmp(node.u.list->keys[i], "seekable-ranges") != 0) {
            continue;
        }

        if (node.u.list->values[i].format == MPV_FORMAT_NODE_ARRAY) {
            mpv_node_list seekable_ranges = *(node.u.list->values[i].u.list);
            auto array = get_cached_ranges(env, thiz);

            for (int j = 0; j < seekable_ranges.num; j++) {
                mpv_node range = seekable_ranges.values[j];

                auto seekable = env->GetObjectArrayElement(array, j);
                if (seekable == nullptr) {
                    return nullptr; // object not created
                }

                for (int k = 0; k < range.u.list->num; k++) {
                    char *key = range.u.list->keys[k];
                    if (range.u.list->values[k].format != MPV_FORMAT_DOUBLE) continue;
                    double value = range.u.list->values[k].u.double_;
                    if (strcmp(key, "start") == 0) {
                        env->SetDoubleField(seekable, startFieldID, value);
                    } else if (strcmp(key, "end") == 0) {
                        env->SetDoubleField(seekable, endFieldID, value);
                    }
                }

                env->SetObjectArrayElement(array, j, seekable);
            }
            set_cached_ranges_count(env, thiz, seekable_ranges.num);
            return array;
        }
    }
    return nullptr;
}