package com.vyagoo.faceid.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;

/**
 * Created by Administrator on 2018/7/26/026.
 */

public class MySufaceView extends SurfaceView {
    public MySufaceView(Context context) {
        super(context);
    }

    public MySufaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MySufaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.e("","ondraw...");
    }


}
