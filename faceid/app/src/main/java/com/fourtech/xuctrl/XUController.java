package com.fourtech.xuctrl;

import android.hardware.usb.UsbDevice;
import android.util.Log;

import com.serenegiant.usb.USBMonitor.UsbControlBlock;
import com.serenegiant.usb.UVCCamera;

import java.util.Calendar;


public class XUController {
	private static final String TAG = "XUController";
	private static final boolean DEBUG = false;

	private UVCCamera mUVCCamera;

	public XUController(UVCCamera camera) {
		super();
		nativeEsInit();
		setUVCCamera(camera);
	}

	public void setUVCCamera(UVCCamera camera) {
		mUVCCamera = camera;
	}

	public int startRecord() {
		Log.e("tomyx", "startRecord");
		if (mUVCCamera != null && mUVCCamera.mCtrlBlock != null) {
			int ret = nativeEsStartRecord(mUVCCamera.mNativePtr);
			return ret;
		}
		return -1;
	}

	public int stopRecord() {
		Log.e("tomyx", "stopRecord");
		if (mUVCCamera != null && mUVCCamera.mCtrlBlock != null) {
			int ret = nativeEsStopRecord(mUVCCamera.mNativePtr);
			Log.e(TAG, "stopRecord() ret=" + ret);
			return ret;
		}
		return -1;
	}

	/**
	 * when recording return 2
	 *
	 * @return
	 */
	public boolean isRecording() {
		Log.e("tomyx", "isRecording");
		if (mUVCCamera != null && mUVCCamera.mCtrlBlock != null) {
			int ret = nativeEsIsRecording(mUVCCamera.mNativePtr);
			Log.e("callIsStopCar", "isRecording ret=" + ret);
			return ret  == 2;//
		}
		return false;
	}

	public boolean setResolution(int width, int height) {
		Log.e("tomyx", "setResolution");
		if (mUVCCamera != null && mUVCCamera.mCtrlBlock != null) {
			int ret = nativeEsSetResolution(mUVCCamera.mNativePtr, width, height);
			Log.e(TAG, "setResolution() ret=" + ret);
			return ret >= 0;
		}
		return false;
	}

	public int[] getResolution(int[] resolution) {
		Log.e("tomyx", "getResolution");
		if (mUVCCamera != null && mUVCCamera.mCtrlBlock != null) {
			if (resolution == null || resolution.length < 2) {
				throw new IllegalArgumentException("Array resolution cannot be null or length < 2");
			}
			int ret = nativeEsGetResolution(mUVCCamera.mNativePtr, resolution);
			// Log.e(TAG, "getResolution() ret=" + ret + " width=" +
			// resolution[0] + ", height=" + resolution[1]);
			return resolution;
		}
		return null;
	}

	public boolean setBitRate(int bitRate) {
		Log.e("tomyx", "setBitRate");
		if (mUVCCamera != null && mUVCCamera.mCtrlBlock != null) {
			int ret = nativeEsSetBitRate(mUVCCamera.mNativePtr, bitRate);
			Log.e(TAG, "setBitRate() ret=" + ret);
			return ret >= 0;
		}
		return false;
	}

	public int getBitRate() {
		Log.e("tomyx", "getBitRate");
		if (mUVCCamera != null && mUVCCamera.mCtrlBlock != null) {
			int bitrate = nativeEsGetBitRate(mUVCCamera.mNativePtr);
			Log.e(TAG, "getBitRate() bitrate=" + bitrate);
			return bitrate;
		}
		return 0;
	}

	public boolean setFPS(int fps) {
		Log.e("tomyx", "setFPS");
		if (mUVCCamera != null && mUVCCamera.mCtrlBlock != null) {
			int ret = nativeEsSetFPS(mUVCCamera.mNativePtr, fps);
			Log.e(TAG, "setFPS() ret=" + ret);
			return ret >= 0;
		}
		return false;
	}

	public int getFPS() {
		Log.e("tomyx", "getFPS");
		if (mUVCCamera != null && mUVCCamera.mCtrlBlock != null) {
			int fps = nativeEsGetFPS(mUVCCamera.mNativePtr);
			Log.e(TAG, "getFPS() fps=" + fps);
			return fps;
		}
		return 0;
	}

	public boolean takeSnapshot() {
		Log.e("tomyx", "takeSnapshot");
		if (mUVCCamera != null && mUVCCamera.mCtrlBlock != null) {
			int ret = nativeEsTakeSnapshot(mUVCCamera.mNativePtr);
			Log.e(TAG, "takeSnapshot() ret=" + ret);
			return ret >= 0;
		}
		return false;
	}

	public boolean isSnapshotFull() {
		Log.e("tomyx", "isSnapshotFull");
		if (mUVCCamera != null && mUVCCamera.mCtrlBlock != null) {
			int ret = nativeEsIsSnapshotFull(mUVCCamera.mNativePtr);
			Log.e(TAG, "isSnapshotFull() ret=" + ret);
			return ret > 0;
		}
		return false;
	}

	public boolean lockRecord() {
		Log.e("tomyx", "lockRecord");
		if (mUVCCamera != null && mUVCCamera.mCtrlBlock != null) {
			int ret = nativeEsLockRecord(mUVCCamera.mNativePtr);
			Log.e(TAG, "lockRecord() ret=" + ret);
			return ret >= 0;
		}
		return false;
	}

	/**
	 *
	 * @return when return 0 is lock, return 256 is unlock.
	 */
	public int getLockRecordState() {
		Log.e("tomyx", "getLockRecordState");
		if (mUVCCamera != null && mUVCCamera.mCtrlBlock != null) {
			//返回值为 0代表不加锁  1代表加锁
			int state = nativeEsGetLockRecordState(mUVCCamera.mNativePtr);
			Log.e(TAG, "getLockRecordState() state=" + state);
			return state;
		}
		return 0;
	}

	// public boolean isLockRecordFull() {
	// return ((getLockRecordState() & 0x00FF) != 0);
	// }

	public boolean isLockRecordFull() {
		Log.e("tomyx", "isLockRecordFull");
		if (getLockRecordState() == 0) {
			return false;
		} else {
			return true;
		}
	}

	public boolean isLockRecordEnable() {
		return ((getLockRecordState() & 0xFF00) != 0);
	}

	public boolean setPreviewH264BitRate(int bitrate) {
		Log.e("tomyx", "setPreviewH264BitRate");
		if (mUVCCamera != null && mUVCCamera.mCtrlBlock != null) {
			int ret = nativeEsSetPreviewH264BitRate(mUVCCamera.mNativePtr, bitrate);
			Log.e(TAG, "setPreviewH264BitRate() ret=" + ret);
			return ret >= 0;
		}
		return false;
	}

	public int getPreviewH264BitRate() {
		Log.e("tomyx", "getPreviewH264BitRate");
		if (mUVCCamera != null && mUVCCamera.mCtrlBlock != null) {
			int bitrate = nativeEsGetPreviewH264BitRate(mUVCCamera.mNativePtr);
			Log.e(TAG, "getPreviewH264BitRate() bitrate=" + bitrate);
			return bitrate;
		}
		return 0;
	}

	public boolean setPreviewMJPGQp(int qp) {
		Log.e("tomyx", "setPreviewMJPGQp");
		if (mUVCCamera != null && mUVCCamera.mCtrlBlock != null) {
			int ret = nativeEsSetPreviewMJPGQp(mUVCCamera.mNativePtr, qp);
			Log.e(TAG, "setPreviewMJPGQp() ret=" + ret);
			return ret >= 0;
		}
		return false;
	}

	public int getPreviewMJPGQp() {
		Log.e("tomyx", "getPreviewMJPGQp");
		if (mUVCCamera != null && mUVCCamera.mCtrlBlock != null) {
			int qp = nativeEsGetPreviewMJPGQp(mUVCCamera.mNativePtr);
			Log.e(TAG, "getPreviewMJPGQp() qp=" + qp);
			return qp;
		}
		return 0;
	}

	private boolean setTimeStatus(int status) {
		Log.e("tomyx", "setTimeStatus");
		if (mUVCCamera != null && mUVCCamera.mCtrlBlock != null) {
			int ret = nativeEsSetTimeStatus(mUVCCamera.mNativePtr, status);
			Log.e(TAG, "setTimeStatus() ret=" + ret);
			return ret >= 0;
		}
		return false;
	}

	/**
	 * 设置停车监控 0代表开 1 代表关
	 * @param status
	 * @return
     */
	public boolean setStopCarListener(int status) {
		Log.e("tomyx", "setStopCarListener");
		if (mUVCCamera != null && mUVCCamera.mCtrlBlock != null) {
			int ret = nativeEsSetStopCarListener(mUVCCamera.mNativePtr, status);
			Log.e("tomyx1", "录音开关 setStopCarListener() status=" + status+"---ret = " +ret);
			return ret >= 0;
		}
		return false;
	}



	/**
	 * 获取停车监控的状态
	 * @return
     */
	public int getStopCarListener() {
		Log.e("tomyx", "getStopCarListener");
		if (mUVCCamera != null && mUVCCamera.mCtrlBlock != null) {
			int status = nativeEsGetStopCarListener(mUVCCamera.mNativePtr);
			if(status == 255){
				status = nativeEsGetStopCarListener(mUVCCamera.mNativePtr);
			}
			Log.e("tomyx1", "录音开关 getStopCarListener() status=" + status);
			return status;
		}
		return -1;
	}

	public boolean showRecordTime() {
		Log.e("tomyx", "showRecordTime");
		// 0x0001 = (0x00 << 8 | 0x01) = (record channel | show)
		return setTimeStatus(0x0001);
	}

	public boolean hideRecordTime() {
		Log.e("tomyx", "hideRecordTime");

		// 0x0000 = (0x00 << 8 | 0x00) = (record channel | hide)
		return setTimeStatus(0x0000);
	}

	public boolean showPreviewTime() {
		Log.e("tomyx", "showPreviewTime");
		// 0x0101 = (0x01 << 8 | 0x01) = (preview channel | show)
		return setTimeStatus(0x0101);
	}

	public boolean hidePreviewTime() {
		Log.e("tomyx", "hidePreviewTime");
		// 0x0100 = (0x01 << 8 | 0x00) = (preview channel | hide)
		return setTimeStatus(0x0100);
	}

	public int getTimeStatus() {
		Log.e("tomyx", "getTimeStatus");
		if (mUVCCamera != null && mUVCCamera.mCtrlBlock != null) {
			int status = nativeEsGetTimeStatus(mUVCCamera.mNativePtr);
			Log.e(TAG, "getTimeStatus() status=" + status);
			return status;
		}
		return 0;
	}

	public boolean isRecordTimeShown() {
		Log.e("tomyx", "isRecordTimeShown");
		return (getTimeStatus() & 0xFF00) != 0;
	}

	public boolean isPreviewTimeShown() {
		Log.e("tomyx", "isPreviewTimeShown");
		return (getTimeStatus() & 0x00FF) != 0;
	}

	public boolean setTimeToUvc(int[] dates) {
		Log.e("tomyx", "setTimeToUvc");
		Log.e(TAG, "setTimeToUvc dates=" + dates);
		if (dates != null && dates.length == 6) {
			return setTimeToUvc(dates[0] - 2000, dates[1], dates[2], dates[3], dates[4], dates[5]);
		}
		return false;
	}

	public boolean setTimeToUvc(int year, int month, int day, int hour, int minute, int second) {
		Log.e("tomyx", "setTimeToUvc");
		if (mUVCCamera != null) {
			Log.e(TAG, "setTime()year" + year + month + day + hour + minute + second);
			int ret = nativeEsSetTime(mUVCCamera.mNativePtr, second, minute, hour, day, month, year);
			Log.e(TAG, "setTime() ret=" + ret);
			return ret >= 0;
		}
		return false;
	}

	public Calendar getTime() {
		Log.e("tomyx", "getTime");
		if (mUVCCamera != null && mUVCCamera.mCtrlBlock != null) {
			int[] timeArray = new int[6];
			int ret = nativeEsGetTime(mUVCCamera.mNativePtr, timeArray);
			Log.e(TAG, "getTime() ret=" + ret);
			if (ret >= 0) {
				Calendar c = Calendar.getInstance();
				c.set(timeArray[0], timeArray[1], timeArray[2], timeArray[3], timeArray[4], timeArray[5]);
				Log.e(TAG, "getTime() c=" + c);
				return c;
			}
		}
		return null;
	}

	public boolean setImageStatus(int status) {
		Log.e("tomyx", "setImageStatus");
		if (mUVCCamera != null && mUVCCamera.mCtrlBlock != null) {
			int ret = nativeEsSetImageStatus(mUVCCamera.mNativePtr, status);
			Log.e(TAG, "setImageStatus() ret=" + ret);
			return ret >= 0;
		}
		return false;
	}

	public boolean setImageStatus(boolean mirror, boolean flip, boolean wdr) {
		int status = 0x07000000; // set Normal/Mirror/Flip/WDR state same time
		if (mirror)
			status |= 0x00000001; // Mirror
		if (flip)
			status |= 0x00000100; // Flip
		if (wdr)
			status |= 0x00010000; // WDR
		return setImageStatus(status);
	}

	public boolean setImageMirror(boolean mirror) {
		int status = 0x01000000; // set Mirror state
		if (mirror)
			status |= 0x00000001; // Mirror
		return setImageStatus(status);
	}

	public boolean setImageFlip(boolean flip) {
		int status = 0x02000000; // set Flip state
		if (flip)
			status |= 0x00000100; // Flip
		return setImageStatus(status);
	}

	public boolean setImageWDR(boolean wdr) {
		int status = 0x04000000; // set WDR state
		if (wdr)
			status |= 0x00010000; // WDR
		return setImageStatus(status);
	}

	public int getImageStatus(int marks) {
		Log.e("tomyx", "getImageStatus");
		if (mUVCCamera != null && mUVCCamera.mCtrlBlock != null) {
			int status = nativeEsGetImageStatus(mUVCCamera.mNativePtr, marks);
			Log.e(TAG, "getImageStatus() status=" + status);
			return status;
		}
		return 0;
	}

	public int getGSensor() {
		Log.e("tomyx", "getGSensor");
		if (mUVCCamera != null && mUVCCamera.mCtrlBlock != null) {
			int status = nativeEsGetGsoner(mUVCCamera.mNativePtr);
			Log.e("xuGetGSensor", "nativeEsGetGsoner() status=" + status);
			return status;
		}
		return -1;
	}



	public static final class ImageStatus {
		public boolean isMirror;
		public boolean isFlip;
		public boolean isWDR;
	}

	public ImageStatus getImageStatusAll() {
		Log.e("tomyx", "getImageStatusAll");
		int marks = 0x07000000; // get Normal/Mirror/Flip/WDR state same time
		int status = getImageStatus(marks);
		ImageStatus imageStatus = new ImageStatus();
		imageStatus.isMirror = ((status & 0x000000FF) != 0);
		imageStatus.isFlip = ((status & 0x0000FF00) != 0);
		imageStatus.isWDR = ((status & 0x00FF0000) != 0);
		return imageStatus;
	}

	public boolean isImageMirror() {
		int marks = 0x01000000; // get Mirror state
		int status = getImageStatus(marks);
		return ((status & 0x000000FF) != 0);
	}

	public boolean isImageFlip() {
		int marks = 0x02000000; // get Flip state
		int status = getImageStatus(marks);
		return ((status & 0x0000FF00) != 0);
	}

	public boolean isImageWDR() {
		int marks = 0x04000000; // get WDR state
		int status = getImageStatus(marks);
		return ((status & 0x00FF0000) != 0);
	}

	public boolean formatSDCard() {
		Log.e("tomyx", "formatSDCard");
		if (mUVCCamera != null && mUVCCamera.mCtrlBlock != null) {
			int ret = nativeEsFormatSDCard(mUVCCamera.mNativePtr);
			Log.e(TAG, "formatSDCard() ret=" + ret);
			return ret >= 0;
		}
		return false;
	}

	public boolean isSDCardFormating() {
		Log.e("tomyx", "isSDCardFormating");
		if (mUVCCamera != null && mUVCCamera.mCtrlBlock != null) {
			int isFormating = nativeEsIsSDCardFormating(mUVCCamera.mNativePtr);
			Log.e(TAG, "isSDCardFormating() isFormating=" + isFormating);
			return isFormating > 0;
		}
		return false;
	}

	public static final class SDCardStatus {
		public boolean exists;
		public boolean isFileSystemSupported;
		public boolean isRecording;
		public boolean isTooSlow;
		public boolean isSpaceNotEnough;
		public boolean isFolderSpaceNotEnough;
	}

	public int getSDCardStatus() {
		Log.e("tomyx", "getSDCardStatus");
		int sdcard_status=-1;
		if (mUVCCamera != null && mUVCCamera.mCtrlBlock != null) {
			 sdcard_status = nativeEsGetSDCardStatus(mUVCCamera.mNativePtr);
			//0 没卡 1有卡 2锁卡写保护
			Log.i("SDCard_Statue","1111msg.sdcard_status ="+sdcard_status);
		}
		return sdcard_status;
	}

	public int setRecordDuration(int duration) {
		Log.e("tomyx", "setRecordDuration");
		if (mUVCCamera != null && mUVCCamera.mCtrlBlock != null) {
			int ret = nativeEsSetRecordDuration(mUVCCamera.mNativePtr, duration);
			Log.e(TAG, "setRecordDuration() ret=" + ret + ",,duration=" + duration);
			return ret;
		}
		return -1;
	}

	public int getRecordDuration() {
		Log.e("tomyx", "getRecordDuration");
		if (mUVCCamera != null && mUVCCamera.mCtrlBlock != null) {
			int duration = nativeEsGetRecordDuration(mUVCCamera.mNativePtr);
			if(duration == 255){
				duration = nativeEsGetRecordDuration(mUVCCamera.mNativePtr);
			}
			Log.e("tomyx1", "getRecordDuration() duration=" + duration);
			return duration;
		}
		return 0;
	}

	public int setGsensor(int i) {
		Log.e("tomyx", "setGsensor");
		if (mUVCCamera != null && mUVCCamera.mCtrlBlock != null) {
			int ret = nativeEsSetGsoner(mUVCCamera.mNativePtr,i);
			Log.e("tomyx1", "setGsensor() ret=" + ret);
			if(ret>0){
				return i;
			}else{
				return -1;
			}
		}
		return  -1;
	}

	@Deprecated
	public boolean resetToDefault() {
		Log.e("tomyx", "resetToDefault");
		if (mUVCCamera != null && mUVCCamera.mCtrlBlock != null) {
			int ret = nativeEsResetToDefault(mUVCCamera.mNativePtr);
			Log.e("tomyx1", "nativeEsResetToDefault() ret=" + ret);
			return ret >= 0;
		}
		return false;
	}

	public boolean switchToMassStorage() {
		Log.e("tomyx", "switchToMassStorage");
		if (mUVCCamera != null && mUVCCamera.mCtrlBlock != null) {
			int ret = nativeEsSwitchToMassStorage(mUVCCamera.mNativePtr);
			Log.e("tomy", "switchToMassStorage() ret=" + ret);
			return ret >= 0;
		}
		return false;
	}

	public static boolean switchToUVCCamera(UsbControlBlock ctrlBlock) {
		Log.e("tomyx", "switchToUVCCamera");
		UsbDevice device = null;
		if (ctrlBlock != null && ctrlBlock.getDevice() != null) {
			device = ctrlBlock.getDevice();
			Log.d(TAG, "EsSwitchToUVCCamera fd=" + ctrlBlock.getFileDescriptor());
			Log.d(TAG, "device.getVendorId()=" + device.getVendorId());
			Log.d(TAG, "device.getProductId()=" + device.getProductId());
			int ret = nativeEsSwitchToUVCCamera(device.getVendorId(), device.getProductId(), ctrlBlock.getFileDescriptor(), UVCCamera.getUSBFSName(ctrlBlock));
			Log.d(TAG, "switchToUVCCamera() ret=" + ret);
			return ret >= 0;
		}
		return false;
	}

	public boolean setRecordMute(boolean isMute) {
		Log.e("tomyx", "setRecordMute");
		if (mUVCCamera != null && mUVCCamera.mCtrlBlock != null) {
			int ret = nativeEsSetRecordMute(mUVCCamera.mNativePtr, isMute ? 1 : 0);
			Log.e(TAG, "setRecordMute() ret=" + ret+"--isMute="+isMute);
			Log.e("tomyx1", "setRecordMute() isMute=" + isMute +"--ret="+ret);
			if(ret >0){
				return isMute;
			};
		}
		return false;
	}
	//录音开关
	public boolean isRecordMute() {
		Log.e("tomyx", "isRecordMute");
		if (mUVCCamera != null && mUVCCamera.mCtrlBlock != null) {
			int isMute = nativeEsIsRecordMute(mUVCCamera.mNativePtr);
			Log.e("tomyx1", "录音开关 isRecordMute() isMute=" + isMute );
			return isMute > 0;
		}
		return false;
	}

	public String getFWVersion() {
		Log.e("tomyx", "getFWVersion");
		if (mUVCCamera != null && mUVCCamera.mCtrlBlock != null) {
			String version = nativeEsGetFWVersion(mUVCCamera.mNativePtr);
			Log.e("tomyx1", "getFWVersion=" + version );
			return version;
		}
		return "FOURTECH.XU.1.0.0";
	}

	private static final native long nativeEsInit();

	private static final native int nativeEsStartRecord(final long id_camera);

	private static final native int nativeEsStopRecord(final long id_camera);

	private static final native int nativeEsIsRecording(final long id_camera);

	private static final native int nativeEsSetResolution(final long id_camera, int width, int height);

	private static final native int nativeEsGetResolution(final long id_camera, int[] resolution);

	private static final native int nativeEsSetBitRate(final long id_camera, int bitRate);

	private static final native int nativeEsGetBitRate(final long id_camera);

	private static final native int nativeEsSetFPS(final long id_camera, int fps);

	private static final native int nativeEsGetFPS(final long id_camera);

	private static final native int nativeEsTakeSnapshot(final long id_camera);

	private static final native int nativeEsIsSnapshotFull(final long id_camera);

	private static final native int nativeEsLockRecord(final long id_camera);

	private static final native int nativeEsGetLockRecordState(final long id_camera);

	private static final native int nativeEsSetPreviewH264BitRate(final long id_camera, int bitrate);

	private static final native int nativeEsGetPreviewH264BitRate(final long id_camera);

	private static final native int nativeEsSetPreviewMJPGQp(final long id_camera, int qp);

	private static final native int nativeEsGetPreviewMJPGQp(final long id_camera);

	private static final native int nativeEsSetTimeStatus(final long id_camera, int status);

	private static final native int nativeEsGetTimeStatus(final long id_camera);

	private static final native int nativeEsSetTime(final long id_camera, int second, int minute, int hour, int day, int month, int year);

	private static final native int nativeEsGetTime(final long id_camera, int[] time_array);

	private static final native int nativeEsSetImageStatus(final long id_camera, int status);

	private static final native int nativeEsGetImageStatus(final long id_camera, int marks);

	private static final native int nativeEsFormatSDCard(final long id_camera);

	private static final native int nativeEsIsSDCardFormating(final long id_camera);

	private static final native int nativeEsGetSDCardStatus(final long id_camera);

	private static final native int nativeEsSetRecordDuration(final long id_camera, int duration);

	private static final native int nativeEsGetRecordDuration(final long id_camera);

	private static final native int nativeEsResetToDefault(final long id_camera);

	private static final native int nativeEsSwitchToMassStorage(final long id_camera);

	private static final native int nativeEsSwitchToUVCCamera(int vid, int pid, int fd, String usbfs_str);

	private static final native int nativeEsSetRecordMute(final long id_camera, int mute);

	private static final native int nativeEsIsRecordMute(final long id_camera);

	private static final native String nativeEsGetFWVersion(final long id_camera);
	//停车监控
	private static final native int nativeEsSetStopCarListener(final long id_camera, int status);
	private static final native int nativeEsGetStopCarListener(final long id_camera);

	private static final native int nativeEsSetGsoner(final long id_camera, int status);
	private static final native int nativeEsGetGsoner(final long id_camera);
}
