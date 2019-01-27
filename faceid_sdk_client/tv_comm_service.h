#ifndef DEV_API_H
#define DEV_API_H

#ifdef __cplusplus
extern "C" {
#endif /* __cplusplus */

#include <unistd.h>
#include <fcntl.h>
#include <sched.h>
#include <stdlib.h>
#include <stdio.h>
#include <stdint.h>
#include <string.h>
#include <pthread.h>

#include "common.h"

#ifdef __cplusplus
extern "C" {
#endif

int comm_device_sendOtaFile(char *OtaPath);
int comm_device_set_signature(char *sig);
char *comm_device_get_cpuid();
int comm_device_reboot_device();
int comm_device_reset_facdiddb();
char* comm_device_system_verison();
int comm_device_start_update();
int comm_device_switch_usb_mode(int mode);
int comm_device_get_algo_status(int algo);
int comm_device_switch_algo(int algo, int enable);
int comm_device_set_callback_freq(int algo, int freq);
int comm_device_set_detect_fps(int algo, int fps);
int comm_device_set_record_mode(int second);
int comm_device_stop_record();
int comm_device_start_record();
int comm_device_take_picture();
int comm_device_get_body_status(int index);
int* comm_device_get_body_rect(int index);
int comm_device_get_body_num();
int comm_device_face_get_attention(int index);
int comm_device_face_get_emotion(int index);
int comm_device_face_get_age(int index);
int comm_device_face_get_gender(int index);
int comm_device_get_faceid(int index);
int* comm_device_get_face_rect(int index);
int comm_device_get_face_num();
int comm_device_unregister_faceid(int id);
int comm_device_register_faceid(int index);
int comm_device_exit_register_mode();
int comm_device_enter_register_mode();
int comm_device_isalive();
int comm_device_isactivate();
int comm_device_release();
int comm_device_init(void *face_detect_callback, void *body_detect_callback,
			int pid, int vid, int fd, char *serial, int busnum, int devaddr);
int comm_device_isIDReg(int id);

#ifdef __cplusplus
}
#endif


#ifdef __cplusplus
}
#endif /* __cplusplus */

#endif //DEV_API_H