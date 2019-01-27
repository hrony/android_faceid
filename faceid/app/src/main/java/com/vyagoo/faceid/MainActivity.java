/*
 *  UVCCamera
 *  library and sample to access to UVC web camera on non-rooted Android device
 *
 * Copyright (c) 2014-2017 saki t_saki@serenegiant.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *  All files in the folder are under this Apache License, Version 2.0.
 *  Files in the libjpeg-turbo, libusb, libuvc, rapidjson folder
 *  may have a different license, see the respective files.
 */

package com.vyagoo.faceid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.faceid.FaceInfoCallBack;
import com.faceid.api;
import com.bean.Person;
import com.serenegiant.common.BaseActivity;
import com.serenegiant.usb.CameraDialog;
import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usb.USBMonitor.OnDeviceConnectListener;
import com.serenegiant.usb.USBMonitor.UsbControlBlock;
import com.serenegiant.usb.UVCCamera;
import com.vyagoo.faceid.util.CrashHandler;
import com.vyagoo.faceid.util.PermissionUtils;
import com.vyagoo.faceid.util.SDCardUtils;
import com.vyagoo.faceid.util.ScreenUtil;
import com.vyagoo.faceid.util.SharedPrefManager;
import com.vyagoo.faceid.widget.MySufaceView;
import com.vyagoo.faceid.widget.SimpleUVCCameraTextureView;


import java.util.ArrayList;
import java.util.List;

public final class MainActivity extends BaseActivity implements CameraDialog.CameraDialogParent, FaceInfoCallBack {

    private final Object mSync = new Object();
    // for accessing USB and USB camera
    private USBMonitor mUSBMonitor;
    private UVCCamera mUVCCamera;
    private SimpleUVCCameraTextureView mUVCCameraView;
    // for open&start / stop&close camera preview
    private ImageButton mCameraButton;
    private Surface mPreviewSurface;


    private api a;
    private MySufaceView sf_kuang;
    private TextView tv_version;
    private TextView entertextview;
    private TextView entertextviewtip;
    private EditText et_name;

    private Button btn_entereg;
    private Button btn_exitreg;
    private Button btn_reg;
    private Button btn_cleandata;
    private Button btn_active;
    private ImageView facerect;
    private int windowHeigh;
    private int windowWidth;
    private float disW;
    private float disH;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        mCameraButton = (ImageButton)findViewById(R.id.camera_button);
        mCameraButton.setOnClickListener(mOnClickListener);

        mUVCCameraView = (SimpleUVCCameraTextureView)findViewById(R.id.UVCCameraTextureView1);
        mUVCCameraView.setAspectRatio(UVCCamera.DEFAULT_PREVIEW_WIDTH / (float)UVCCamera.DEFAULT_PREVIEW_HEIGHT);

        mUSBMonitor = new USBMonitor(this, mOnDeviceConnectListener);

        if (!SDCardUtils.isFileExist("slam")) {
            SDCardUtils.createFile("slam/LOG");
        }
        //错误信息抓取
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());

        initView();

    }

    private void initView() {
        sf_kuang = (MySufaceView) findViewById(R.id.sf_kuang);
        sf_kuang.setZOrderOnTop(true);
        sf_kuang.getHolder().setFormat(PixelFormat.TRANSLUCENT);//设置为透明
        sf_kuang.setVisibility(View.VISIBLE);


        tv_version = (TextView) findViewById(R.id.tv_version);
        entertextview = (TextView) findViewById(R.id.entertextview);
        entertextviewtip = (TextView) findViewById(R.id.entertextviewtip);
        et_name = (EditText) findViewById(R.id.et_name);
        entertextviewtip.setTag(0);
        btn_entereg = (Button) findViewById(R.id.btn_entereg);
        btn_exitreg = (Button) findViewById(R.id.btn_exitreg);
        btn_reg = (Button) findViewById(R.id.btn_reg);
        btn_cleandata = (Button) findViewById(R.id.btn_cleandata);
        btn_active = (Button) findViewById(R.id.btn_active);
        facerect = (ImageView) findViewById(R.id.facerect);

        a = new api();

        a.setMain(this);
        a.setCallBack(this);

        //mContentView是整个页面，tv_ps_username是edittext
        mUVCCameraView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mUVCCameraView.setFocusable(true);
                mUVCCameraView.setFocusableInTouchMode(true);
                mUVCCameraView.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mUVCCameraView.getWindowToken(), 0);
                return false;
            }
        });

        PermissionUtils.verifyStoragePermissions(MainActivity.this);

        windowHeigh = ScreenUtil.getWindowHeigh(MainActivity.this);
        windowWidth = ScreenUtil.getWindowWidth(MainActivity.this);
        Log.e("xxxx","windowHeigh ="+ windowHeigh +"--"+ windowWidth);
        disW = (windowWidth/1920.0f);
        disH = (windowHeigh/1080.0f);

        Log.e("xxxx","disW ="+ disW +"--"+ disH);
    }

    public void selectCamera(View view){
        synchronized (mSync) {
            if (mUVCCamera == null) {
                CameraDialog.showDialog(MainActivity.this);
            } else {
                releaseCamera();
            }
        }
    }

    public void quit(View view) {
        System.exit(0);
    }

    public boolean isRegesterMode = false;
    Person registerPerson;


    /**
     * 进入注册模式
     *
     * @param view
     */
    public void register(View view) {
        isRegesterMode = true;
        if(a.enterRegisterMode() == 0){
            btn_exitreg.setVisibility(View.VISIBLE);
            btn_reg.setVisibility(View.VISIBLE);
            btn_cleandata.setVisibility(View.VISIBLE);
            et_name.setVisibility(View.VISIBLE);
            facerect.setVisibility(View.VISIBLE);
            entertextview.setVisibility(View.VISIBLE);
            entertextviewtip.setVisibility(View.VISIBLE);
//            sf_kuang.setVisibility(View.INVISIBLE);
        }else{
            Toast.makeText(MainActivity.this, R.string.enter_fail,Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 退出注册模式
     *
     * @param view
     */
    public void unregister(View view) {
        isRegesterMode = false;
        if(a.exitRegisterMode() == 0){
            btn_exitreg.setVisibility(View.GONE);
            btn_reg.setVisibility(View.GONE);
            et_name.setVisibility(View.GONE);
            facerect.setVisibility(View.GONE);
            btn_cleandata.setVisibility(View.GONE);
            entertextview.setVisibility(View.GONE);
            entertextviewtip.setVisibility(View.GONE);
//            sf_kuang.setVisibility(View.VISIBLE);
        }else{
            Toast.makeText(MainActivity.this, R.string.quit_fail,Toast.LENGTH_SHORT).show();
        }
    }


    public static List<Person> registerPersionList = new ArrayList<>();
    /**
     * 注册
     *
     * @param view
     */
    public void registering(View view) {
        if(registerPerson == null){
            Toast.makeText(MainActivity.this, R.string.no_face_pleace,Toast.LENGTH_SHORT).show();
            return;
        }
        int[] te_rect = registerPerson.getRect();
        double a1 = (int) te_rect[0] ;
        double a2 = (int) te_rect[1] ;

        double a3 = (int) te_rect[2] ;
        double a4 = (int) te_rect[3];
        Rect rect = new Rect((int) a1, (int) a2, (int) a3, (int) a4);
//                        if(a1>452&&a2>190&&a3<573&&a4<310){
        if (a1 > 700&& a4<730) {
            String name = et_name.getText().toString();
            if(TextUtils.isEmpty(name)){
                Toast.makeText(MainActivity.this, R.string.input_name,Toast.LENGTH_SHORT).show();
            }else{
                registerPerson.setName(name);

                registerPersionList.add(registerPerson);
                int faceId = a.registerFaceID(registerPerson.getIndex());
                if(faceId >0){
                    SharedPrefManager.getInstance(MainActivity.this).put(String.valueOf(faceId),name);
                    Toast.makeText(MainActivity.this,getString(R.string.reg_success)+ faceId,Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this,"注册失败 faceId ="+faceId,Toast.LENGTH_SHORT).show();
                }

            }
        }else {
            Toast.makeText(MainActivity.this,R.string.no_face_pleace,Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 清除数据
     *
     * @param view
     */
    public void deleteDate(View view) {
//        SharedPrefManager.getInstance(MainActivity.this).put(String.valueOf(faceId),name);
        SharedPrefManager.getInstance(MainActivity.this).clearAll();
        if(a.resetFaceIdDB() == 0){
            Toast.makeText(MainActivity.this,getString(R.string.clean_data_sucess),Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(MainActivity.this,"清除数据失败",Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 激活
     *
     * @param view
     */
    public void active(View view) {
        Log.e("xxxxx","a.activeDevice() ="+ a.activeDevice());
        int res = a.activeDevice();
        if(res == 0){
            Toast.makeText(MainActivity.this,getString(R.string.active_success),Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(MainActivity.this,"激活失败，请检查是否连接网络",Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 激活
     *
     * @param view
     */
    public void otasendfile(View view) {
        new Thread(){
            @Override
            public void run() {
//               a.sendOtaFile("/mnt/sdcard/DCIM/Camera/xiongrui_normal_f.jpg");
                a.sendOtaFile("/mnt/sdcard/helloar.apk");
            }
        }.start();
    }


    @Override
    protected void onStart() {
        super.onStart();
        mUSBMonitor.register();
        synchronized (mSync) {
            if (mUVCCamera != null) {
                mUVCCamera.startPreview();
            }
        }
    }

    @Override
    protected void onStop() {
        synchronized (mSync) {
            if (mUVCCamera != null) {
                mUVCCamera.stopPreview();
            }
            if (mUSBMonitor != null) {
                mUSBMonitor.unregister();
            }
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        synchronized (mSync) {
            releaseCamera();
            if (mToast != null) {
                mToast.cancel();
                mToast = null;
            }
            if (mUSBMonitor != null) {
                mUSBMonitor.destroy();
                mUSBMonitor = null;
            }
        }
        mUVCCameraView = null;
        mCameraButton = null;
        super.onDestroy();
    }

    private final OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(final View view) {
            synchronized (mSync) {
                if (mUVCCamera == null) {
                    CameraDialog.showDialog(MainActivity.this);
                } else {
                    releaseCamera();
                }
            }
        }
    };

    private Toast mToast;
    UsbControlBlock mSensorCtrlBlock;
    private final OnDeviceConnectListener mOnDeviceConnectListener = new OnDeviceConnectListener() {
        @Override
        public void onAttach(final UsbDevice device) {
            Toast.makeText(MainActivity.this, "USB_DEVICE_ATTACHED", Toast.LENGTH_SHORT).show();
            mUSBMonitor.requestPermission(device);
        }

        @Override
        public void onConnect(final UsbDevice device, final UsbControlBlock ctrlBlock, final boolean createNew) {
            if(ctrlBlock.getVenderId()==8711 &&ctrlBlock.getProductId()==54 ) {
                releaseCamera();
                queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        final UVCCamera camera = new UVCCamera();
                        camera.open(ctrlBlock);

//					camera.setPreviewTexture(camera.getSurfaceTexture());
                        if (mPreviewSurface != null) {
                            mPreviewSurface.release();
                            mPreviewSurface = null;
                        }
                        try {
                            camera.setPreviewSize(UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT, UVCCamera.FRAME_FORMAT_MJPEG);
                        } catch (final IllegalArgumentException e) {
                            // fallback to YUV mode
                            try {
                                camera.setPreviewSize(UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT, UVCCamera.DEFAULT_PREVIEW_MODE);
                            } catch (final IllegalArgumentException e1) {
                                camera.destroy();
                                return;
                            }
                        }
                        final SurfaceTexture st = mUVCCameraView.getSurfaceTexture();
                        if (st != null) {
                            mPreviewSurface = new Surface(st);
                            camera.setPreviewDisplay(mPreviewSurface);
//						camera.setFrameCallback(mIFrameCallback, UVCCamera.PIXEL_FORMAT_RGB565/*UVCCamera.PIXEL_FORMAT_NV21*/);
                            camera.startPreview();
                        }
                        synchronized (mSync) {
                            mUVCCamera = camera;
                            Log.e("xxxxx","检测到设备连接 onConnect");
                            mSensorCtrlBlock = ctrlBlock;
                            a.setIsInit(false);
                            int res = a.api_init(new Object(), mSensorCtrlBlock.getVenderId(), mSensorCtrlBlock.getProductId(),
                                    mSensorCtrlBlock.getFileDescriptor(),
                                    mSensorCtrlBlock.getBusNum(),
                                    mSensorCtrlBlock.getDevNum(), "");

                            if (res == 0) {
                                a.setIsInit(true);
                            }
                            Log.e("xxxxx", "a.enterRegisterMode()=" + a.enterRegisterMode());
                            a.setFre();
                            Log.e("xxxxx", "a.api_getVersion() =" + a.api_getVersion());

                        }
                    }
                }, 0);
            }

        }

        @Override
        public void onDisconnect(final UsbDevice device, final UsbControlBlock ctrlBlock) {
            // XXX you should check whether the coming device equal to camera device that currently using

            releaseCamera();
        }

        @Override
        public void onDettach(final UsbDevice device) {
            if(mSensorCtrlBlock != null){
                if(mSensorCtrlBlock.getVenderId()==8711 &&mSensorCtrlBlock.getProductId()==54 ) {
                    if (a != null) {
                        Log.e("xxxxx","检测到断开设备连接 onDettach");
                        a.release();
                    }
                }
            }
            Toast.makeText(MainActivity.this, "USB_DEVICE_DETACHED", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(final UsbDevice device) {
        }
    };

    private synchronized void releaseCamera() {
        synchronized (mSync) {
            if (mUVCCamera != null) {
                try {
                    mUVCCamera.setStatusCallback(null);
                    mUVCCamera.setButtonCallback(null);
                    mUVCCamera.close();
                    mUVCCamera.destroy();
                } catch (final Exception e) {
                    //
                }
                mUVCCamera = null;
            }
            if (mPreviewSurface != null) {
                mPreviewSurface.release();
                mPreviewSurface = null;
            }
        }
    }

    /**
     * to access from CameraDialog
     * @return
     */
    @Override
    public USBMonitor getUSBMonitor() {
        return mUSBMonitor;
    }

    @Override
    public void onDialogResult(boolean canceled) {
        if (canceled) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // FIXME
                }
            }, 0);
        }
    }
    private static Paint paint;
    Paint mPaint1;
    private static Bitmap bitmap;
    private Canvas mCanvas;
    private static final int NO_FACE = 1;
    private static final int DECTECT_FACE = 2;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case NO_FACE:
                    entertextviewtip.setTag(0);
                    entertextviewtip.setText(R.string.no_face);
                    break;
                case DECTECT_FACE:
                    entertextviewtip.setTag(1);
                    entertextviewtip.setText(R.string.have_face);
                    break;
            }
            super.handleMessage(msg);
        }
    };




    private boolean isregister = false;
    @Override
    public void onFaceInfoCallBack(List<Person> list) {
        if(paint == null){
            paint = new Paint();
            paint.setAntiAlias(true);
            mPaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint1.setColor(Color.RED);
            mPaint1.setTextSize(50);//设置字体大小
            mPaint1.setStrokeWidth(1.0f);
            mPaint1.setStyle(Paint.Style.FILL);

            mPaint1.setStrokeWidth(3);
            mPaint1.setTextSize(50);

        }
        mCanvas = sf_kuang.getHolder().lockCanvas();
        if(mCanvas == null){
            return;
        }
        if(bitmap == null){
            bitmap = BitmapFactory.decodeResource(MainActivity.this.getResources(), R.mipmap.recognize_blue);
        }
        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        if(!isRegesterMode){
            if(list != null && list.size()>0){
                paint.setColor(Color.GREEN);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(4.0f);
//                for(int i = 0;i<5;i++){
//                    Log.e("xxqqq","isIDReg = "+a.isIDReg(i));
//                }
                for (Person person : list) {
//                    Log.e("xxqqq","list="+list.size()+ " faceID:" + person.getFaceId() );
//                    synchronized (this) {
//                        if (person.getFaceId() == 0) {
//                            if (a.isIDReg(person.getFaceId()) == 0) {
//                                int faceId = a.registerFaceID(person.getIndex());
//                                Log.e("api111", "注册之后的faceID:" + faceId);
//                            }
//                        }
//                    }


                    int[] te_rect = person.getRect();
                    double a1 = (int) te_rect[0] * disW;
                    double a2 = (int) te_rect[1] * disH;

                    double a3 = (int) te_rect[2] * disW;
                    double a4 = (int) te_rect[3] * disH;

                    float paramFloat=1.0f;
                    float f2 = paramFloat * te_rect[0]* disW;
                    float f3 = paramFloat * te_rect[1]* disH;
                    float f4 = paramFloat * (te_rect[2]* disW - te_rect[0]*disW);

                    float f5 = f4 * 1.2F;
                    float f6 = f5 - f4;
                    float f7 = f2 - f6 / 2.0F;
                    float f8 = f3 - f6 / 2.0F;

                    RectF localRectF = new RectF(f7, f8, f2 + f5, f3 + f5);
                    new NinePatch(bitmap, bitmap.getNinePatchChunk(), null).draw(mCanvas, localRectF);

                    //显示信息

                    Rect targetRect = new Rect((int)(a1+30), (int)(a2 - 80), (int)(a3 ), (int)(a2 ));
                    mPaint1.setColor(Color.CYAN);
                    mCanvas.drawRect(targetRect, mPaint1);
                    mPaint1.setColor(Color.RED);
                    Paint.FontMetricsInt fontMetrics = mPaint1.getFontMetricsInt();
                    int baseline = (targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
                    mPaint1.setTextAlign(Paint.Align.CENTER);
                    mCanvas.drawText(person.toString(), targetRect.centerX(), baseline, mPaint1);

                    if((!TextUtils.isEmpty(person.getName()))&&!person.getName().equals("没注册")) {
                        Rect targetRect1 = new Rect((int)(a1+30), (int)(a4), (int)(a3 ), (int)(a4+80 ));
                        mPaint1.setColor(Color.RED);
                        mCanvas.drawRect(targetRect1, mPaint1);
                        mPaint1.setColor(Color.WHITE);
                        Paint.FontMetricsInt fontMetrics1 = mPaint1.getFontMetricsInt();
                        int baseline1 = (targetRect1.bottom + targetRect1.top - fontMetrics1.bottom - fontMetrics1.top) / 2;
                        mPaint1.setTextAlign(Paint.Align.CENTER);
                        mCanvas.drawText(person.getName(), targetRect1.centerX(), baseline1, mPaint1);
                    }

                }
            }
            sf_kuang.getHolder().unlockCanvasAndPost(mCanvas);
        }else{
//            Log.e("face_hyh","检测到人脸在框中="+list);
            paint.setColor(Color.GREEN);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(4.0f);
            if(list != null && list.size()>0) {
                for (Person person : list) {
                    int[] te_rect = person.getRect();
                    double a1 = (int) te_rect[0];
                    double a2 = (int) te_rect[1];

                    double a3 = (int) te_rect[2] ;
                    double a4 = (int) te_rect[3];

                    float paramFloat = 1.0f;
                    float f2 = paramFloat * te_rect[0] * disW;
                    float f3 = paramFloat * te_rect[1] * disH;
                    float f4 = paramFloat * (te_rect[2] * disW - te_rect[0] * disW);

                    float f5 = f4 * 1.2F;
                    float f6 = f5 - f4;
                    float f7 = f2 - f6 / 2.0F;
                    float f8 = f3 - f6 / 2.0F;
                    int tag = (int)entertextviewtip.getTag();

                    Log.e("face_hyh","检测到人脸在框中="+a1+"--"+a2+"--"+a3+"--"+a4);
                    if (a1 > 700&& a4<830) {
                        Log.e("face_hyh","检测到人脸在框中="+list);
                        mHandler.sendEmptyMessage(DECTECT_FACE);
                        registerPerson = person;

                    }else{
//						Log.e("face_hyh","检测不在="+list);
                        registerPerson = null;
                        if(tag == 1){
                            mHandler.sendEmptyMessage(NO_FACE);
                        }
                    }
                    RectF localRectF = new RectF(f7, f8, f2 + f5, f3 + f5);
//                    new NinePatch(bitmap, bitmap.getNinePatchChunk(), null).draw(mCanvas, localRectF);
                }
            }else{
//                mHandler.sendEmptyMessage(NO_FACE);
            }
            sf_kuang.getHolder().unlockCanvasAndPost(mCanvas);
        }
    }

    private long currentTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if ((System.currentTimeMillis() - currentTime) > 2000) {
                Toast.makeText(MainActivity.this, R.string.quit_app,Toast.LENGTH_SHORT).show();
                currentTime = System.currentTimeMillis();
            } else {
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
    // if you need frame data as byte array on Java side, you can use this callback method with UVCCamera#setFrameCallback
    // if you need to create Bitmap in IFrameCallback, please refer following snippet.
/*	final Bitmap bitmap = Bitmap.createBitmap(UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT, Bitmap.Config.RGB_565);
	private final IFrameCallback mIFrameCallback = new IFrameCallback() {
		@Override
		public void onFrame(final ByteBuffer frame) {
			frame.clear();
			synchronized (bitmap) {
				bitmap.copyPixelsFromBuffer(frame);
			}
			mImageView.post(mUpdateImageTask);
		}
	};

	private final Runnable mUpdateImageTask = new Runnable() {
		@Override
		public void run() {
			synchronized (bitmap) {
				mImageView.setImageBitmap(bitmap);
			}
		}
	}; */
}
