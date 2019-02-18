#ifndef DEV_API_H
#define DEV_API_H

#ifdef __cplusplus
extern "C" {
#endif /* __cplusplus */

int dev_init(void (*face_detect_callback)(int event), void (*body_detect_callback)(int event),
	int pid, int vid, int fd, char *serial, int busnum, int devaddr);
int dev_release();
int dev_isAlive();
int dev_enterRegisterMode();
int dev_exitRegisterMode();
int dev_registerFaceID(int index);
int dev_unregisterFaceID(int id);
int dev_getFaceNum();
int* dev_getFaceRect(int index);
int dev_getFaceID(int index);
int dev_faceGetGender(int index);
int dev_faceGetFRFeature(int index, float *feature);
int dev_faceGetAge(int index);
int dev_faceGetEmotion(int index);
int dev_faceGetAttention(int index);
int dev_getBodyNum();
int* dev_getBodyRect(int index);
int dev_getBodyStatus(int index);
int dev_takePicture();
int dev_startRecord();
int dev_stopRecord();
int dev_setRecordMode(int second);
int dev_setDetectFps(int algo, int fps);
int dev_setCallBackFreq(int algo, int freq);
int dev_switchAlgo(int algo, int enable);
int dev_getAlgoStatus(int algo);
int dev_switchUsbMode(int index);
int dev_startUpdate();
char* dev_getSysVersion();
int dev_resetFaceIdDB();
int dev_rebootDevice();
char *dev_getCPUID();
int dev_setSignature(char *sig);
int dev_sendOtaFile(char *OtaPath);
int dev_sendPhotoFile(char *PhotoPath);
int dev_receiveFaceidDBFile(char *FaceidDBPath);
int dev_sendFaceidDBFile(char *FaceidDBPath);
int dev_isIDReg(int id);
int dev_isActivate();
/*
	mode_state: 
		0:camera stream facetrack;
		1:photo facetrack;
*/
int dev_set_facetrack_state(int mode_state);
int dev_decoder_facetrack_jpeg();
#ifdef __cplusplus
}
#endif /* __cplusplus */

#endif //DEV_API_H