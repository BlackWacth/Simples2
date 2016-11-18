package com.hua.canvas.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.hua.canvas.R;

/**
 * 放大镜效果
 * Created by hzw on 2016/10/26.
 */
public class MafyGlassView extends View {

    public static final String tag = "MafyGlassView";
    private Path mPath;
    private Matrix mMatrix;
    private Bitmap mBitmap;

    private float mFactor = 3;
    private float mRadius = 300;
    private float mCurrentX = mRadius;
    private float mCurrentY = mRadius;

    public MafyGlassView(Context context) {
        this(context, null);
    }

    public MafyGlassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mMatrix = new Matrix();
        mMatrix.setScale(mFactor, mFactor);

        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.img03);

        mPath = new Path();
        mPath.addCircle(mCurrentX, mCurrentY, mRadius, Path.Direction.CW);
//        mPath.addCircle(700, 700, mRadius, Path.Direction.CW);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmap, 0, 0, null);

//        canvas.save();
//        canvas.clipPath(mPath);
//        canvas.drawBitmap(mBitmap, mMatrix, null); //绘制放大3倍的底图
//        canvas.restore();

        canvas.save();
        canvas.translate(mCurrentX - mRadius, mCurrentY - mRadius); //决定剪切圆的移动
        canvas.clipPath(mPath);  //剪切一个圆
        canvas.translate(mRadius - mCurrentX * mFactor, mRadius - mCurrentY * mFactor); //决定放大图片的移动，与剪切圆的移动的刚好形成放大镜效果
        canvas.drawBitmap(mBitmap, mMatrix, null); //把图片整体放大mFactor倍
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_DOWN) {
            mCurrentX = event.getX();
            mCurrentY = event.getY();;
            Log.i(tag, "x = " + mCurrentX + ", y = " + mCurrentY);
            invalidate();
        }
        return true;
    }
}

