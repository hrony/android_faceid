//
// Created by Administrator on 2018/4/26/026.
//

#ifndef VYRECORDER_LOCADEFINES_H
#define VYRECORDER_LOCADEFINES_H

#include <jni.h>
typedef jlong ID_TYPE;

#define		LIKELY(x)					((__builtin_expect(!!(x), 1)))	// x is likely true
#define		UNLIKELY(x)					((__builtin_expect(!!(x), 0)))	// x is likely false


#endif //VYRECORDER_LOCADEFINES_H
