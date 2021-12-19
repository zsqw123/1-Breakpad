#include <jni.h>
#include "android/log.h"

#include "client/linux/handler/exception_handler.h"
#include "client/linux/handler/minidump_descriptor.h"

using namespace google_breakpad;

#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,"breakpadApp",__VA_ARGS__)

void crash() {
    int *null_int = (int *) nullptr;
    *null_int = 2;
}

bool DumpCallback(const google_breakpad::MinidumpDescriptor &descriptor,
                  void *context,
                  bool succeeded) {
    LOGW("======native crash dump======");
    LOGW("======Dump path: %s", descriptor.path());
    return succeeded;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_breakpadapp_NativeBridge_makeCrash(JNIEnv *env, jobject thiz, jstring store_path) {
    const char *path = env->GetStringUTFChars(store_path, nullptr);
    LOGW("=====%s=====", path);
    MinidumpDescriptor descriptor(path);
    ExceptionHandler eh(descriptor, nullptr, DumpCallback, nullptr, true, -1);
    crash();
}

