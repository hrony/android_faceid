#define LOG_TAG1 "FACEID_API"
#include "utils/Log.h"
#include "utils/misc.h"
#include "cutils/properties.h"
#include "android/asset_manager.h"
#include "android/asset_manager_jni.h"
#include <jni.h>
#include "JNIHelp.h"
#include <assert.h>
#include <string.h>
#include <pthread.h>

#include <sys/stat.h>
#include <fcntl.h>
#include "tv_sdk_api.h"
#include "locadefines.h"

#define LOG_FLAG 1
#ifdef LOG_FLAG

#define LOGV(fmt, arg...)   do { \
		__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG1, "<line[%04d] %s()> " fmt, __LINE__, __FUNCTION__, ##arg); \
	} while(0)
#define LOGD(fmt, arg...)   do { \
		__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG1, "<line[%04d] %s()> " fmt, __LINE__, __FUNCTION__, ##arg); \
	} while(0)

#define LOGI(fmt, arg...)   do { \
		__android_log_print(ANDROID_LOG_INFO, LOG_TAG1, "<line[%04d] %s()> " fmt, __LINE__, __FUNCTION__, ##arg); \
	} while(0)
#define LOGW(fmt, arg...)   do { \
		__android_log_print(ANDROID_LOG_WARN, LOG_TAG1, "<line[%04d] %s()> " fmt, __LINE__, __FUNCTION__, ##arg); \
	} while(0)
#define LOGE(fmt, arg...)   do { \
		__android_log_print(ANDROID_LOG_ERROR, LOG_TAG1, "<line[%04d] %s()> "fmt, __LINE__, __FUNCTION__, ##arg); \
	} while(0)

#else
	
#define LOGV(...)
#define LOGD(...)
#define LOGI(...)
#define LOGW(...)
#define LOGE(...)

#endif

#define ClassName "com/faceid/api"

using namespace android;

struct fields_t {
	jfieldID context;
};
static fields_t gFields;

static jclass gObjClass;
static jobject gObj;
static JavaVM *gJavaVm;
static jmethodID gfaceDetect_callback;
static jmethodID gbodyDetect_callback;


static JNIEnv* getJNIEnv(int* needsDetach) {
	JNIEnv* env = NULL;
	jint result = -1;

	if (gJavaVm->GetEnv((void**) &env, JNI_VERSION_1_6)) {
		int status = gJavaVm->AttachCurrentThread(&env, 0);
		if (status < 0) {
			LOGD("failed to attach current thread");
			return NULL;
		}
		*needsDetach = 1;
	}

	return env;
}

/***
 * call apk event listener
 */
static void global_faceDetect_callback(int event) {

	int needsDetach = 0;

	JNIEnv *env = getJNIEnv(&needsDetach);

    env->CallIntMethod(gObj, gfaceDetect_callback,gObj, event);

	if (needsDetach) {
		gJavaVm->DetachCurrentThread();
	}
	return;
}

static void global_bodyDetect_callback(int event) {

    int needsDetach = 0;

	JNIEnv *env = getJNIEnv(&needsDetach);

	env->CallIntMethod(gObj, gbodyDetect_callback,gObj, event);


	if (needsDetach) {
		gJavaVm->DetachCurrentThread();
	}
	return;
}

static jint com_faceid_isIDReg(JNIEnv *env, jobject thiz, jint id){
	return dev_isIDReg(id);
}

static jint com_faceid_reset_transfer(JNIEnv *env, jobject thiz){
	return dev_reset_transfer();
}

static int com_faceid_sendOtaFile(JNIEnv *env, jobject thiz, jstring OtaPath){
	LOGV("com_faceid_sendOtaFile");
	char* file = (char*) env->GetStringUTFChars(OtaPath, false);
	int ret = dev_sendOtaFile(file);
    if (file)
	    env->ReleaseStringUTFChars(OtaPath, file);
	return ret;
}

static int com_faceid_receiveFaceidDBFile(JNIEnv *env, jobject thiz, jstring FaceDBPath){
	LOGV("com_faceid_sendOtaFile");
	char* file = (char*) env->GetStringUTFChars(FaceDBPath, false);
	int ret = dev_receiveFaceidDBFile(file);
    if (file)
	    env->ReleaseStringUTFChars(FaceDBPath, file);
	return ret;
}

static int com_faceid_sendFaceidDBFile(JNIEnv *env, jobject thiz, jstring FaceDBPath){
	LOGV("com_faceid_sendOtaFile");
	char* file = (char*) env->GetStringUTFChars(FaceDBPath, false);
	int ret = dev_sendFaceidDBFile(file);
    if (file)
	    env->ReleaseStringUTFChars(FaceDBPath, file);
	return ret;
}

static jint com_faceid_activeDevice(JNIEnv *env, jobject thiz){
	return 1;
}


static jint com_faceid_rebootDevice(JNIEnv *env, jobject thiz) {
	LOGV("com_faceid_rebootDevice");
	return dev_rebootDevice();
}

static jint com_faceid_resetFaceIdDB(JNIEnv *env, jobject thiz) {
	LOGV("com_faceid_resetFaceIdDB");
	return dev_resetFaceIdDB();
}

static jstring com_faceid_getSysVersion(JNIEnv *env, jobject thiz) {
	LOGV("com_faceid_getSysVersion");
	const char* str = dev_getSysVersion();
	jstring data = env->NewStringUTF(str);
	return data;
}

static jint com_faceid_startUpdate(JNIEnv *env, jobject thiz) {
	LOGV("com_faceid_startUpdate");
	return dev_startUpdate();
}

static jint com_faceid_switchUsbMode(JNIEnv *env, jobject thiz, jint mode) {
	LOGV("com_faceid_switchUsbMode");
	return dev_switchUsbMode(mode);
}

static jint com_faceid_getAlgoStatus(JNIEnv *env, jobject thiz, jint id) {
	LOGV("com_faceid_getAlgoStatus");
    return dev_getAlgoStatus(id);
}

static jint com_faceid_switchAlgo(JNIEnv *env, jobject thiz, jint algo, jint enable) {
	LOGV("com_faceid_switchAlgo");
	return dev_switchAlgo(algo, enable);
}

static jint com_faceid_setDetectFps(JNIEnv *env, jobject thiz, jint algo, jint fps){
	LOGV("com_faceid_setDetectFps");
	return dev_setDetectFps(algo, fps);
}

static jint com_faceid_setCallBackFreq(JNIEnv *env, jobject thiz, jint algo, jint freq) {
	LOGV("com_faceid_setCallBackFreq");
	return dev_setCallBackFreq(algo, freq);
}

static jint com_faceid_setRecordMode(JNIEnv *env, jobject thiz, jint second) {
	LOGV("com_faceid_setRecordMode");
	return dev_setRecordMode(second);
}

static jint com_faceid_stopRecord(JNIEnv *env, jobject thiz) {
	LOGV("com_faceid_stopRecord");
	return dev_stopRecord();
}

static jint com_faceid_startRecord(JNIEnv *env, jobject thiz) {
	LOGV("com_faceid_startRecord");
	return dev_startRecord();
}

static jint com_faceid_takePicture(JNIEnv *env, jobject thiz) {
	LOGV("com_faceid_takePicture");
	return dev_takePicture();
}

static jint com_faceid_getBodyStatus(JNIEnv *env, jobject thiz, jint id) {
	LOGV("com_faceid_getBodyStatus");
	return dev_getBodyStatus(id);
}

static jintArray com_faceid_getBodyRect(JNIEnv *env, jobject thiz, jint id) {
	LOGV("com_faceid_getBodyRect");
	int *pdata = dev_getBodyRect(id);
    jintArray data = env->NewIntArray(4);
    env->SetIntArrayRegion(data, 0, 4, pdata);
	return data;
}

static jint com_faceid_getBodyNum(JNIEnv *env, jobject thiz) {
	LOGV("com_faceid_getBodyNum");
	return dev_getBodyNum();
}

static jint com_faceid_faceGetEmotion(JNIEnv *env, jobject thiz, jint id) {
	LOGV("com_faceid_faceGetEmotion");
	return dev_faceGetEmotion(id);
}

static jint com_faceid_faceGetAttention(JNIEnv *env, jobject thiz, jint id) {
	LOGV("com_faceid_faceGetAttention");
    return dev_faceGetAttention(id);
}

static jint com_faceid_faceGetAge(JNIEnv *env, jobject thiz, jint id) {
	LOGV("com_faceid_faceGetAge");
	return dev_faceGetAge(id);
}

static jint com_faceid_faceGetGender(JNIEnv *env, jobject thiz, jint id) {
	LOGV("com_faceid_faceGetGender");
	return dev_faceGetGender(id);
}

static jint com_faceid_getFaceID(JNIEnv *env, jobject thiz, jint id) {
	LOGV("com_faceid_getFaceID");
	return dev_getFaceID(id);
}

static jintArray com_faceid_getFaceRect(JNIEnv *env, jobject thiz, jint id) {
	LOGV("com_faceid_getFaceRect");
	int *pdata = dev_getFaceRect(id);
	jintArray data = env->NewIntArray(4);
	if (pdata != NULL){
		env->SetIntArrayRegion(data, 0, 4, pdata);
	}

	return data;
}

static jint com_faceid_getFaceNum(JNIEnv *env, jobject thiz) {
	LOGV("com_faceid_getFaceNum");
	return dev_getFaceNum();
}

static jint com_faceid_unregisterFaceID(JNIEnv *env, jobject thiz, jint id) {
	LOGV("com_faceid_unregisterFaceID");
	return dev_unregisterFaceID(id);
}

static jint com_faceid_registerFaceID(JNIEnv *env, jobject thiz, jint index) {
	LOGV("com_faceid_registerFaceID");
	return dev_registerFaceID(index);
}
static jint com_faceid_exitRegisterMode(JNIEnv *env, jobject thiz) {
	LOGV("com_faceid_exitRegisterMode");
	return dev_exitRegisterMode();
}

static jint com_faceid_enterRegisterMode(JNIEnv *env, jobject thiz) {
	LOGV("com_faceid_enterRegisterMode");
    return dev_enterRegisterMode();
}

static jint com_faceid_isAlive(JNIEnv *env, jobject thiz) {
	LOGV("com_faceid_isAlive");
	return dev_isAlive();
}

static jint com_faceid_isActivate(JNIEnv *env, jobject thiz) {
	LOGV("com_faceid_isAlive");
	return dev_isActivate();
}

static jint com_faceid_release(JNIEnv *env, jobject thiz) {
	LOGV("com_faceid_release");
	return dev_release();
}

static jint com_faceid_init(JNIEnv *env, jobject thiz, jobject obj,
	jint vid, jint pid, jint fd, jint busnum, jint devaddr, jstring serial) {
	LOGV("com_faceid_init");
	//char* s = (char*) env->GetStringUTFChars(serial, NULL);
	jclass clazz;
	int ret = 0;
/*
	clazz = env->FindClass(ClassName);
	if (clazz == NULL) {
		return 0;
	}
*/
	
	gObjClass =env->GetObjectClass(thiz);
	gfaceDetect_callback = env->GetMethodID(gObjClass, "faceDetect_callback", "(Ljava/lang/Object;I)I");

    gbodyDetect_callback = env->GetMethodID(gObjClass, "bodyDetect_callback", "(Ljava/lang/Object;I)I");
	gObj = env->NewGlobalRef(thiz);
	
	
	LOGV("com_faceid_init gObjClass:%p ,gfaceDetect_callback:%p, gbodyDetect_callback:%p, gObj:%p", gObjClass, \
			gfaceDetect_callback, gbodyDetect_callback, gObj);

	ret = dev_init(global_faceDetect_callback, global_bodyDetect_callback,
		pid, vid, fd, NULL, busnum, devaddr);
	//env->ReleaseStringUTFChars(serial, NULL);
	return ret;
}

// ----------------------------------------------------------------------------
static JNINativeMethod gMethods[] = {

	{ "native_init", "(Ljava/lang/Object;IIIIILjava/lang/String;)I", (void *)com_faceid_init },
	{ "native_release", "()I", (void *) com_faceid_release },
	{ "native_isAlive", "()I", (void *) com_faceid_isAlive },
	{ "native_isActivate", "()I", (void *) com_faceid_isActivate },
	{ "native_enterRegisterMode", "()I", (void *) com_faceid_enterRegisterMode },
	{ "native_exitRegisterMode", "()I", (void *) com_faceid_exitRegisterMode },
	{ "native_registerFaceID", "(I)I",(void *) com_faceid_registerFaceID },
	{ "native_unregisterFaceID", "(I)I", (void *) com_faceid_unregisterFaceID },
	{ "native_getFaceNum", "()I", (void *) com_faceid_getFaceNum },
	{ "native_getFaceRect", "(I)[I",(void *) com_faceid_getFaceRect },
	{ "native_getFaceID", "(I)I", (void *) com_faceid_getFaceID },
	{ "native_faceGetGender", "(I)I", (void *) com_faceid_faceGetGender },
	{ "native_faceGetAge", "(I)I", (void *) com_faceid_faceGetAge },
	{ "native_faceGetEmotion", "(I)I", (void *) com_faceid_faceGetEmotion },
    { "native_faceGetAttention", "(I)I", (void *)com_faceid_faceGetAttention },
	{ "native_getBodyNum", "()I", (void *)com_faceid_getBodyNum },
	{ "native_getBodyRect", "(I)[I", (void *)com_faceid_getBodyRect },
	{ "native_getBodyStatus", "(I)I", (void *) com_faceid_getBodyStatus },
	{ "native_takePicture", "()I", (void *) com_faceid_takePicture },
	{ "native_startRecord", "()I",(void *) com_faceid_startRecord },
	{ "native_stopRecord", "()I",(void *) com_faceid_stopRecord },
	{ "native_setRecordMode", "(I)I",	(void *) com_faceid_setRecordMode },
	{ "native_setDetectFps", "(II)I", (void *) com_faceid_setDetectFps },
	{ "native_setCallBackFreq", "(II)I", (void *) com_faceid_setCallBackFreq },
	{ "native_switchAlgo", "(II)I",	(void *) com_faceid_switchAlgo },
	{ "native_getAlgoStatus", "(I)I", (void *)com_faceid_getAlgoStatus },
    { "native_switchUsbMode", "(I)I"	, (void *)com_faceid_switchUsbMode },
    { "native_startUpdate", "()I", (void *)com_faceid_startUpdate },
    { "native_getSysVersion", "()Ljava/lang/String;", (void *)com_faceid_getSysVersion },
	{ "native_resetFaceIdDB", "()I", (void *)com_faceid_resetFaceIdDB },
	{ "native_rebootDevice", "()I", (void *)com_faceid_rebootDevice },
	{ "native_activeDevice", "()I", (void *)com_faceid_activeDevice },
	{ "native_sendOtaFile", "(Ljava/lang/String;)I", (void *)com_faceid_sendOtaFile },
	{ "native_receiveFaceidDBFile", "(Ljava/lang/String;)I", (void *)com_faceid_receiveFaceidDBFile },
	{ "native_sendFaceidDBFile", "(Ljava/lang/String;)I", (void *)com_faceid_sendFaceidDBFile },
	{ "native_isIDReg", "(I)I", (void *)com_faceid_isIDReg },
	{ "native_reset_transfer", "()I", (void *)com_faceid_reset_transfer },
};

jint registerNativeMethods(JNIEnv* env, const char *class_name, JNINativeMethod *methods, int num_methods) {
	int result = 0;

	jclass clazz = env->FindClass(class_name);
	if (LIKELY(clazz)) {
		int result = env->RegisterNatives(clazz, methods, num_methods);
		if (UNLIKELY(result < 0)) {
			//LOGE("registerNativeMethods failed(class=%s)", class_name);
		}
	} else {
		//LOGE("registerNativeMethods: class'%s' not found", class_name);
	}
	return result;
}


// This function only registers the native methods
static int register_faceid_methods(JNIEnv *env) {

	return registerNativeMethods(env, ClassName, gMethods, NELEM(gMethods));
	

}

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
	LOGV("xxx  JNI_OnLoad reserved:%p", reserved);
	JNIEnv* env = NULL;

	gJavaVm = vm;

	if (vm->GetEnv((void**) &env, JNI_VERSION_1_6)) {
		LOGV("GetEnv failed");
		return JNI_ERR;
	}

	assert(env != NULL);

	if (register_faceid_methods(env) < 0) {
		LOGV("register_faceid_methods native registration failed");
		return JNI_ERR;
	}
	LOGV("++++++ JNI_OnLoad");
	/* success -- return valid version number */
	return JNI_VERSION_1_6;
}
