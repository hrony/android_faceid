package com.faceid;

import android.util.Log;

import com.bean.Person;
import com.vyagoo.faceid.MainActivity;
import com.vyagoo.faceid.util.SharedPrefManager;


import java.util.ArrayList;

public class api {
	private static final String TAG ="api" ;
	public static ArrayList<Person> list ;
	public boolean isInit =false;

	static {
        try {    
            System.loadLibrary("faceid_jni");
//            native_init(new Object());
//			Log.e(TAG,"init="+native_init(new Object()));
        } catch (Exception e) {
            e.printStackTrace();  
            Log.e(TAG,e.getMessage());
        } 
    }


    public boolean getIsInit(){
		return isInit;
	}

	public void setIsInit(boolean isInit){
		this.isInit = isInit;
	}
    int count=0;
	private int bodyDetect_callback(Object object, int event){
		return 0;
	}
	//do not block in this thread
	int faceDetect_callback(Object object, int event){
		Log.e("api_face","event ="+event);
		if(isInit){
			if (event == 1){
				//detect face
				int num = native_getFaceNum();
	//			num = 4;
				list = new ArrayList<>();
				for(int i = 0; i < num; i++) {
					final int[] rect = native_getFaceRect(i);
					final int faceid = native_getFaceID(i);
					final int age	= native_faceGetAge(i);
					final int emotion	= native_faceGetEmotion(i);
					final int gender	= native_faceGetGender(i);
					final int attention = native_faceGetAttention(i);

					Person person = new Person(i,rect,faceid,age,emotion,gender,attention);
                    person.setContext(mainActivity);
					String name = SharedPrefManager.getInstance(mainActivity).get(String.valueOf(faceid),"没注册");
					person.setName(name);
					list.add(person);

//					if(faceid == 0){
//						final int finalI = i;
//						new Thread(){
//							@Override
//							public void run() {
//								super.run();
//								int faceId = registerFaceID(finalI);
//								Log.e("api111","registerFaceID faceid="+faceId );
//							}
//						}.start();
//
//					}



				}
	//			mainActivity.disPlayInformation(list);
				mCallBack.onFaceInfoCallBack(list);
			}else if(event == 0){
				mCallBack.onFaceInfoCallBack(null);
			}
		}
		return 0;
	}
	public MainActivity mainActivity;
	public void setMain(MainActivity main){
		this.mainActivity = main;
	}

	private FaceInfoCallBack mCallBack;
	public void setCallBack(FaceInfoCallBack callBack){
		this.mCallBack = callBack;
	}

	public int api_init(Object obj, int vid, int pid, int fd, int busnum, int devaddr, String serial) {
			return native_init(obj,vid,pid,fd,busnum,devaddr,serial);
	}

	public String api_getVersion() {
		return native_getSysVersion();
	}

	public void setFre() {
		 native_setCallBackFreq(0,20);
	}

	public int enterRegisterMode() {
		return native_enterRegisterMode();
	}

	public int exitRegisterMode() {
		return native_exitRegisterMode();
	}

	public int registerFaceID(int index) {
		return native_registerFaceID(index);
	}

	public int unregisterFaceID(int id) {
		return native_unregisterFaceID(id);
	}

	public int resetFaceIdDB() {
		return native_resetFaceIdDB();
	}
//	static jint com_baofeng_tv_init(JNIEnv *env, jobject thiz, jobject obj,
//									jint vid, jint pid, jint fd, jint busnum, jint devaddr, jstring serial)

	native int native_init(Object obj, int vid, int pid, int fd, int busnum, int devaddr, String serial);
	static native int native_release();
	static native int native_isAlive();
	static native int native_enterRegisterMode();
	static native int native_exitRegisterMode();
	static native int native_registerFaceID(int id);
	static native int native_unregisterFaceID(int id);
	static native int native_getFaceNum();
	static native int[] native_getFaceRect(int index);
	static native int native_getFaceID(int index);
	static native int native_faceGetGender(int index);
	static native int native_faceGetAge(int index);
	static native int native_faceGetEmotion(int index);
	static native int native_faceGetAttention(int index);
	static native int native_getBodyNum();
	static native int[] native_getBodyRect(int index);
	static native int native_getBodyStatus(int index);
	static native int native_takePicture();
	static native int native_startRecord();
	static native int native_stopRecord();
	static native int native_setRecordMode(int second);
	static native int native_setDetectFps(int algo, int fps);
	static native int native_setCallBackFreq(int algo, int freq);
	static native int native_switchAlgo(int algo, int enable);
	static native int native_getAlgoStatus(int algo);
	static native int native_switchUsbMode(int mode);
	static native int native_startUpdate();
	static native String native_getSysVersion();

	 native int native_resetFaceIdDB();

	static native int native_rebootDevice();

	static native int native_activeDevice();

	static native int native_sendOtaFile(String path);
	static native int native_sendFaceidDBFile(String path);
	static native int native_receiveFaceidDBFile(String path);
	static native int native_isIDReg(int id);

	public int activeDevice(){
		return native_activeDevice();
	}
	public int rebootDevice() {
		native_rebootDevice();
	}
	public int sendOtaFile(String path){
		Log.e("xxxx","path = "+path);
		return native_sendOtaFile(path);
	}
	/*faceid.db*/
	public int sendFaceidDBFile(String path){
		Log.e("xxxx","path = "+path);
		return native_sendFaceidDBFile(path);
	}
	/*faceid.db*/
	public int receiveFaceidDBFile(String path){
		Log.e("xxxx","path = "+path);
		return native_receiveFaceidDBFile(path);
	}

	public int isIDReg(int id){
		return native_isIDReg(id);
	}

	public int release(){
		Log.e("xxxx","release = ");
		return native_release();
	}
}