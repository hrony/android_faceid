/* -*- Mode: C; indent-tabs-mode:t ; c-basic-offset:8 -*- */
/*
 * libusb example program for hotplug API
 * Copyright © 2012-2013 Nathan Hjelm <hjelmn@mac.ccom>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

#include <stdlib.h>
#include <stdio.h>

#include "libusb.h"
#include <pthread.h>
#include "common.h"

static libusb_device_handle *handle;
static pthread_t thread_id;
static int handle_exit = 0;
uint8_t endpoint_in = 0, endpoint_out = 0;	// default IN and OUT endpoints
int nrinterface = 0;
pthread_mutex_t request_mutex;

#define LOG_TAG "libusbwraper"

static void *thread_handle_events(void *arg)
{
	int rc = 0;
	LOG("poll thread running\n");
	handle_exit = 0;
	while (!handle_exit) {
		rc = libusb_handle_events(NULL);
		if (rc < 0)
			LOG("libusb_handle_events() failed: %s\n", libusb_error_name(rc));
	}
	pthread_exit(NULL);
	LOG(" thread_handle_events shutting down\n");
	return NULL;
}
//req请求设备端，然后同步等待设备端replay
int algo_device_request(void *req, int req_size, void *reply, int replay_size){

	int tran_size = 0;
	int ret = 0;
	struct usb_comm_protocol *p = reply;

	if (!handle)
		return -1;
	
	pthread_mutex_lock(&request_mutex);
	
	ret = libusb_bulk_transfer(handle, endpoint_out, req, req_size, &tran_size,1000);
	if (ret != 0) {
		LOG ("libusb_bulk_transfer endpoint_out:%d, ret:%d, tran_size:%d\n", endpoint_out,ret, tran_size);
	}
	
	if (p && p->param[0] == DEVICE_GET_FACES_INFO){
		ret = libusb_bulk_transfer(handle, endpoint_in, reply, replay_size, &tran_size, 30);
	} else {
		ret = libusb_bulk_transfer(handle, endpoint_in, reply, replay_size, &tran_size, 1000);
	}
	
	if (ret != 0){
		LOG ("libusb_bulk_transfer endpoint_in:%d, ret:%d, tran_size:%d\n", endpoint_in, ret, tran_size);
	}
	
	pthread_mutex_unlock(&request_mutex);
	
	return ret;
}

static libusb_device_handle * libusb_open_device_with_vid_pid_class_subclass(
		libusb_context *ctx, uint16_t vendor_id, uint16_t product_id, 
		uint16_t class, uint16_t subclass) {

	struct libusb_device **devs;
	struct libusb_device *found = NULL;
	struct libusb_device *dev;
	size_t i = 0;
	int r;

	if (libusb_get_device_list(ctx, &devs) < 0)
		return NULL;

	while ((dev = devs[i++]) != NULL) {
		struct libusb_device_descriptor desc;
		r = libusb_get_device_descriptor(dev, &desc);
		if ((r < 0))
			goto out;
		LOG ("desc.bDeviceClass :%d, bDeviceSubClass:%d\n", desc.bDeviceClass, desc.bDeviceSubClass);
		if (desc.bDeviceClass == class 
			&& desc.bDeviceSubClass == subclass) {
			found = dev;
			break;
		}
	}

	if (found) {
		r = libusb_open(found, &handle);
		if ((r < 0))
			handle = NULL;
	}

out:
	libusb_free_device_list(devs, 1);
	return handle;
}

libusb_device_handle * LIBUSB_CALL _open_device_with_vid_pid(
       libusb_context *ctx, uint16_t vendor_id, uint16_t product_id){
	struct libusb_device **devs;
	struct libusb_device *found = NULL;
	struct libusb_device *dev;
	struct libusb_device_handle *dev_handle = NULL;
	size_t i = 0;
	int r;

	if (libusb_get_device_list(ctx, &devs) < 0)
		return NULL;

	while ((dev = devs[i++]) != NULL) {
		struct libusb_device_descriptor desc;
		r = libusb_get_device_descriptor(dev, &desc);
		if (r < 0)
			goto out;
		LOG ("desc.idVendor = 0x%x desc.idProduct= 0x%x", desc.idVendor, desc.idProduct);
		if (desc.idVendor == vendor_id && desc.idProduct == product_id) {
			found = dev;
			break;
		}
	}

	if (found) {
		LOG ("find pid vid  success");
		r = libusb_open(found, &dev_handle);
		if (r < 0){
			dev_handle = NULL;
			LOG ("libusb_open faild");
		} else {
			LOG("libusb_open success");
		}
	} else {
		LOG("no find pid:%d vid:%d device", product_id, vendor_id);
	}

out:
	libusb_free_device_list(devs, 1);
	return dev_handle;
}

libusb_device_handle *  _get_device_with_fd(void*ctx, int vid, int pid, const char *serial, int fd, int busnum, int devaddr){
	struct libusb_device *dev;
	struct libusb_device_handle *dev_handle = NULL;
	int r;

	fd = dup(fd);
	dev = libusb_get_device_with_fd(ctx, vid, pid, serial, fd, busnum, devaddr);
	if (dev) {
		LOG ("libusb_get_device_with_fd success");
		r = libusb_open(dev, &dev_handle);
		if (r < 0){
			dev_handle = NULL;
			LOG ("libusb_open faild");
		} else {
			LOG("libusb_open success");
		}
	} else {
		LOG("libusb_get_device_with_fd fail, vid:%d, pid:%d, fd:%d, busnum:%d, devaddr:%d\n",
			vid, pid, fd, busnum, devaddr);
	}

out:
	return dev_handle;
}

int algo_device_init_with_fd(int pid, int vid, int fd, char *serial, int busnum, int devaddr){
	int product_id, vendor_id;
	int rc, i, j, k;

	libusb_device *dev;
	struct libusb_config_descriptor *conf_desc;
	struct libusb_context *ctx = NULL;
	const struct libusb_endpoint_descriptor *endpoint;
	int nb_ifaces = 0;
	
	vendor_id  = RV1108_TV_VID;
	product_id = RV1108_TV_PID;

	rc = libusb_init2(&ctx, "/dev/bus/usb");
	if (rc < 0)
	{
		LOG("failed to initialise libusb: %s\n", libusb_error_name(rc));
		return -1;
	}
	//hyh nativeConnect vid=8711,pid=54,fb=38,busNum=2,devAddr=9,usbfs=/dev/bus/usb,result=0

#ifndef LIBUSB_OPEN_WITH_FD
	handle = _open_device_with_vid_pid(ctx, vendor_id, product_id);
#else
	handle = _get_device_with_fd(ctx, vendor_id, product_id, serial, fd, busnum, devaddr);
#endif
	if (!handle){
		LOG("could not find device pid:0x%x, vid:0x%x\n", product_id, vendor_id);
		libusb_exit(NULL);
		return -1;
	}
	
	dev = libusb_get_device(handle);
	libusb_get_config_descriptor(dev, 0, &conf_desc);
	
	nb_ifaces = conf_desc->bNumInterfaces;
	LOG("nb interfaces: %d\n", nb_ifaces);

	for (i = 0; i< nb_ifaces; i++) {
		for (j = 0; j < conf_desc->interface[i].num_altsetting; j++) {
			LOG("num endpoints = %d\n",conf_desc->interface[i].altsetting[j].bNumEndpoints);
			LOG("bInterfaceClass:%02X, bInterfaceSubClass:%02X\n",conf_desc->interface[i].altsetting[j].bInterfaceClass,
				conf_desc->interface[i].altsetting[j].bInterfaceSubClass);
			if(conf_desc->interface[i].altsetting[j].bInterfaceClass != 0xFF &&
				conf_desc->interface[i].altsetting[j].bInterfaceSubClass != 0x84) {
				continue;
			}
			for (k = 0; k < conf_desc->interface[i].altsetting[j].bNumEndpoints; k++) {

				endpoint = &conf_desc->interface[i].altsetting[j].endpoint[k];
				LOG(" endpoint[%d].address: %02X\n", k, endpoint->bEndpointAddress);

				if ((endpoint->bmAttributes & LIBUSB_TRANSFER_TYPE_MASK) & LIBUSB_TRANSFER_TYPE_BULK) {
					if (endpoint->bEndpointAddress & LIBUSB_ENDPOINT_IN) {
						endpoint_in = endpoint->bEndpointAddress;
					} else {
						endpoint_out = endpoint->bEndpointAddress;
					}
					nrinterface = i;
				}
			}
		}
	}
	if (nb_ifaces)
		libusb_free_config_descriptor(conf_desc);

	LOG("Claiming interface %d...\n", nrinterface);
	if (endpoint_in && endpoint_out) {
		libusb_set_auto_detach_kernel_driver(handle, 1);
		rc = libusb_claim_interface(handle, nrinterface);
		if (rc != LIBUSB_SUCCESS) {
			LOG("  libusb_claim_interface Failed.\n");
		}
	}
	pthread_mutex_init(&request_mutex, NULL);

	rc = 0;
	return rc;

}

int algo_device_release() {

	pthread_mutex_lock(&request_mutex);
	
	handle_exit = 1;
	//pthread_join(thread_id, NULL);
	
	libusb_release_interface(handle, nrinterface);
	if (handle) {
		libusb_close(handle);
	}
	handle = 0;
	libusb_exit(NULL);
	
	pthread_mutex_unlock(&request_mutex);
	pthread_mutex_destroy(&request_mutex);

	return 0;
}
