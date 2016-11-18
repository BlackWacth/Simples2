package com.hua.simples2.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

/**
 * Created by Bruce on 2016/10/9.
 */

public class SwitchView extends View {

    /** 宽高比 */
    public static final float DEFAULT_WIDTH_HEIGHT_PERCENT = 0.65f;
    public static final String tag = "SwitchView";

    private final Paint paint = new Paint();
    private final Path sPath = new Path();

    private int mWidth, mHeight;
    private float sWidth, sHeight;
    private float sLeft, sTop, sRight, sBottom;
    private float sCenterX, sCenterY;

    private float sAnim = 1;
    private float bAnim = 1;
    private boolean isOn;

    private float bRadius, bStrokWidth;
    private float bWidth;
    private float bLeft, bTop, bRight, bBottom;
    private float sScaleCenterX;
    private float sScale;

    private float bTranslateX;
    private final AccelerateInterpolator aInterpolator = new AccelerateInterpolator(2);

    public SwitchView(Context context) {
        this(context, null);
    }

    public SwitchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = (int) (widthSize * DEFAULT_WIDTH_HEIGHT_PERCENT);
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w; //控件宽
        mHeight = h; //控件高
        sLeft = sTop = 0; //田径场，左、上点
        sRight = mWidth; //田径场，占整个控件宽， 右点
        sBottom = mHeight * 0.8f; //田径场占整个控件80%，预留为阴影，下点

        sWidth = sRight - sLeft; //田径场宽
        sHeight = sBottom - sTop; //田径场高
        sCenterX = (sRight + sLeft) / 2;  //田径场X轴中心坐标
        sCenterY = (sBottom + sTop) / 2;  //田径场Y轴中心坐标

        RectF sRectF = new RectF(sLeft, sTop, sBottom, sBottom); //跑道左半部分，构成正方形，绘制内切半圆
        sPath.arcTo(sRectF, 90, 180);

        //改变矩形坐标，形成跑道右半部分，构成正方形，绘制内切半圆
        sRectF.left = sRight - sBottom;
        sRectF.right = sRight;
        sPath.arcTo(sRectF, 270, 180);

        sPath.close();  //通过以上两个步骤，仅绘制了左右两个半圆和跑道上面一条直线，下面一条直线需要改方法连接。

        bLeft = bTop = 0;
        bRight = bBottom = sBottom;
        bWidth = bRight - bLeft;
        final float halfHeightOfS = (sBottom - sTop) / 2;
        bRadius = halfHeightOfS * 0.9f;
        bStrokWidth = 2 * (halfHeightOfS - bRadius); //按钮的边框
        sScale = 1 - bStrokWidth / sHeight;
        sScaleCenterX = sWidth - halfHeightOfS;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xffcccccc);
        canvas.drawPath(sPath, paint);

        sAnim = sAnim - 0.1f > 0 ? sAnim - 0.1f : 0;
        final float scale = 0.98f * (isOn ? sAnim : 1 - sAnim);
        canvas.save();
        canvas.scale(scale, scale, sScaleCenterX, sCenterY); //缩放画布
        paint.setColor(0xffffffff);
        canvas.drawPath(sPath, paint);
        canvas.restore();
        if(sAnim > 0) {
            invalidate();
        }

//        paint.reset();

        canvas.save();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xffffffff);
//        Log.i(tag, "bWidth = " + bWidth);
//        Log.i(tag, "bRadius = " + bRadius);
        canvas.drawCircle(bWidth / 2, bWidth / 2, bRadius, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(0xffdddddd);
        paint.setStrokeWidth(bStrokWidth);
        canvas.drawCircle(bWidth / 2, bWidth / 2, bRadius, paint);
        canvas.restore();

        bTranslateX = sWidth - bWidth;
        Log.i(tag, "bTranslateX = " + bTranslateX);
        final float translate = bTranslateX * (isOn ? 1 - sAnim : sAnim);
        Log.i(tag, "translate = " + translate);
        canvas.save();
        canvas.translate(translate, 0);
        canvas.restore();


        sAnim = sAnim - 0.1f > 0 ? sAnim - 0.1f : 0;
        bAnim = bAnim - 0.1f > 0 ? bAnim - 0.1f : 0;

        final float asAnim = aInterpolator.getInterpolation(sAnim);
        final float abAnim = aInterpolator.getInterpolation(bAnim);
//        Log.i(tag, "sAnim:" +sAnim + " ,dsAnim:" + dsAnim);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return true;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                sAnim = 1; //动画标示
                isOn = !isOn; //状态标示, 开关
                invalidate();
                break;
        }
        return super.onTouchEvent(event);
    }
}
