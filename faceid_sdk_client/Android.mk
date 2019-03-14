# Android build config for libusb examples
# Copyright © 2012-2013 RealVNC Ltd. <toby.gray@realvnc.com>
#
# This library is free software; you can redistribute it and/or
# modify it under the terms of the GNU Lesser General Public
# License as published by the Free Software Foundation; either
# version 2.1 of the License, or (at your option) any later version.
#
# This library is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public
# License along with this library; if not, write to the Free Software
# Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
#

LOCAL_PATH:= $(call my-dir)

LIBUSB_ROOT_ABS:= $(LOCAL_PATH)


# libfaceid

include $(CLEAR_VARS)
LOCAL_PREBUILT_LIBS:= \
	libusb:./libusb100.so \

include $(BUILD_MULTI_PREBUILT)


include $(CLEAR_VARS)
include $(LOCAL_PATH)/Config.mk


LOCAL_SRC_FILES := \
	libusbwrapper.c \
	tv_comm_service.c \
	tv_sdk_api.c \



LOCAL_C_INCLUDES += \
  $(LIBUSB_ROOT_ABS)
  
LOCAL_MODULE_TAGS := optional

LOCAL_SHARED_LIBRARIES += libusb liblog  

LOCAL_MODULE:= libfaceid

include $(BUILD_SHARED_LIBRARY)


###############################################
##########  libfaceid_jni  ############
###############################################

include $(CLEAR_VARS)
include $(LOCAL_PATH)/Config.mk

LOCAL_SRC_FILES:= \
    	com_faceid_api.cpp

LOCAL_SHARED_LIBRARIES := \
    liblog \
	libfaceid \


LOCAL_C_INCLUDES += \
		

LOCAL_C_INCLUDES += \
    $(JNI_H_INCLUDE) \
    
LOCAL_CFLAGS += -D__ANDROID

LOCAL_CPPFLAGS := -Wno-error=non-virtual-dtor -fexceptions
LOCAL_CFLAGS := -std=c++11
LOCAL_NDK_STL_VARIANT := c++_static
LOCAL_LDFLAGS   := -llog -ldl -landroid -Wl,--no-fatal-warnings
LOCAL_LDFLAGS   += \
        $(NDK)/sources/cxx-stl/gnu-libstdc++/libs/armeabi-v7a/libgnustl_static.a  \
        
LOCAL_CFLAGS    += -ffast-math -O3 -funroll-loops
LOCAL_ARM_MODE := arm	
LOCAL_MODULE_TAGS := optional

LOCAL_MODULE:= libfaceid_jni

include $(BUILD_SHARED_LIBRARY)

