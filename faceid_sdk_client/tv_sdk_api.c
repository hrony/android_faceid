#include "tv_comm_service.h"

int dev_init(void (*face_detect_callback)(int event), void (*body_detect_callback)(int event),
	int pid, int vid, int fd, char *serial, int busnum, int devaddr) {
	
	return comm_device_init(face_detect_callback, body_detect_callback,
					pid, vid, fd, serial, busnum, devaddr);
}

int dev_release(){
	
	return comm_device_release();
}

int dev_isAlive(){
	
	return comm_device_isalive();
}

int dev_isActivate(){
	
	return comm_device_isactivate();
}

int dev_decoder_facetrack_jpeg(){
	return comm_device_dec_facetrack_jpeg();
}

int dev_set_facetrack_state(int mode_state){
	return comm_device_set_facetrack_state(mode_state);
}

int dev_enterRegisterMode(){
	
	return comm_device_enter_register_mode();
}

int dev_exitRegisterMode(){
	
	return comm_device_exit_register_mode();
}

int dev_registerFaceID(int index){
	
	return comm_device_register_faceid(index);
}

int dev_unregisterFaceID(int id){
	
	return comm_device_unregister_faceid(id);
}

int dev_getFaceNum(){
	
	return comm_device_get_face_num();
}

int* dev_getFaceRect(int index){
	
	return comm_device_get_face_rect(index);
}

int dev_getFaceID(int index){
	
	return comm_device_get_faceid(index);
}

int dev_faceGetGender(int index){
	
	return comm_device_face_get_gender(index);
}

int dev_faceGetFRFeature(int index, float *feature){

	return comm_device_face_get_fr_feature(index, feature);
}

int dev_faceGetAge(int index){
	
	return comm_device_face_get_age(index);
}

int dev_faceGetEmotion(int index){
	
	return comm_device_face_get_emotion(index);
}

int dev_faceGetAttention(int index){
	
	return comm_device_face_get_attention(index);
}

int dev_getBodyNum(){
	
	return comm_device_get_body_num();
}

int* dev_getBodyRect(int index){
	
	return comm_device_get_body_rect(index);
}

int dev_getBodyStatus(int index){
	
	return comm_device_get_body_status(index);
}

int dev_takePicture(){
	
	return comm_device_take_picture();
}

int dev_startRecord(){
	
	return comm_device_start_record();
}

int dev_stopRecord(){
	
	return comm_device_stop_record();
}

int dev_setRecordMode(int second){
	
	return comm_device_set_record_mode(second);
}

int dev_setDetectFps(int algo, int fps) {
	
	return comm_device_set_detect_fps(algo, fps);
}

int dev_setCallBackFreq(int algo, int freq){
	
	return comm_device_set_callback_freq(algo, freq);
}

int dev_switchAlgo(int algo, int enable){
	
	return comm_device_switch_algo(algo, enable);
}

int dev_getAlgoStatus(int algo){
	
	return comm_device_get_algo_status(algo);
}

int dev_switchUsbMode(int index){
	
	return comm_device_switch_usb_mode(index);
}

int dev_startUpdate(){
	
	return comm_device_start_update();
}

char* dev_getSysVersion(){
	
	return comm_device_system_verison();
}

int dev_resetFaceIdDB(){
	return comm_device_reset_facdiddb();
}

int dev_rebootDevice(){
	return comm_device_reboot_device();
}

char *dev_getCPUID(){
	return comm_device_get_cpuid();
}

int dev_setSignature(char *sig){
	return comm_device_set_signature(sig);
}

int dev_sendOtaFile(char *OtaPath){
	return comm_device_sendOtaFile(OtaPath);
}

int dev_sendPhotoFile(char *PhotoPath){
	return comm_device_sendPhotoFile(PhotoPath);
}

int dev_receiveFaceidDBFile(char *FaceidDBPath){
	return  comm_device_receiveFaceidDBFile(FaceidDBPath);
}
int dev_sendFaceidDBFile(char *FaceidDBPath){
	return  comm_device_sendFacedbFile(FaceidDBPath);
}

int dev_isIDReg(int id){
	return comm_device_isIDReg(id);
}