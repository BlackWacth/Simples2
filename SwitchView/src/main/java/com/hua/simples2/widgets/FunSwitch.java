package com.hua.simples2.widgets;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * Created by hzw on 2016/10/12.
 */
public class FunSwitch extends View implements ValueAnimator.AnimatorUpdateListener, ValueAnimator.AnimatorListener{

    public static final String tag = "FunSwitch";
    public static final float DEFAULT_WIDTH_HEIGHT_PERCENT = 0.65f;
    private final static float FACE_ANIM_MAX_FRACTION = 1.4f;
    private final static float NORMAL_ANIM_MAX_FRACTION = 1.0f;

    private Paint mPaint;
    private Path mBgPath, mFacePath;

    private int mWidth, mHeight;

    private float mWidthAndHeightPercent;
    private boolean mIsOpen;
    private boolean mIsDuringAnimation = false;

    private int mBgColorOn = 0xff33ccff;
    private int mBgColorOff = 0xffcccccc;
    private int mFaceColor = 0xffffffff;
    private int mCurrentColor = mBgColorOff;
    private float mCenterX, mCenterY;
    private float mFaceRadius;
    private float mTransitionLength;

    private ValueAnimator mValueAnimator;
    private Interpolator mInterpolator = new AccelerateDecelerateInterpolator();
    private float mAnimationFraction = 0.0f;
    private long mOnAnimationDuration = 850L;
    private long mOffAnimationDuration = (long)(mOnAnimationDuration * NORMAL_ANIM_MAX_FRACTION / FACE_ANIM_MAX_FRACTION);

    public FunSwitch(Context context) {
        this(context, null);
    }

    public FunSwitch(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FunSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mWidthAndHeightPercent = DEFAULT_WIDTH_HEIGHT_PERCENT;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgPath = new Path();
        mFacePath = new Path();
        setState(false);
        setSaveEnabled(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.i(tag, "onMeasure");
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = (int) (width * mWidthAndHeightPercent);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.i(tag, "onSizeChanged");
        mWidth = w;
        mHeight = h;

        float left = 0;
        float top = 0;
        float right = mWidth;
        float bottom = mHeight * 0.8f;

        RectF bgRectF = new RectF(left, top, bottom, bottom);
        mBgPath.arcTo(bgRectF, 90, 180);
        bgRectF.left = right - bottom;
        bgRectF.right = right;
        mBgPath.arcTo(bgRectF, 270, 180);
        mBgPath.close();

        mFaceRadius = bottom * 0.98f / 2.0f;
        mCenterX = (left + bottom) / 2;
        mCenterY = (top + bottom) / 2;
        mTransitionLength = right - bottom;
        mFacePath.addCircle(mCenterX, mCenterY, mFaceRadius, Path.Direction.CW);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.i(tag, "onDraw");
        drawBackground(canvas);
        drawForeground(canvas);
    }

    private void drawBackground(Canvas canvas) {
        mPaint.setColor(mCurrentColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(mBgPath, mPaint);
    }

    private void drawForeground(Canvas canvas) {
        canvas.save();
        canvas.translate(getForgroundTransitionValue(), 0);
        drawFace(canvas, mAnimationFraction);
        canvas.restore();
    }

    private float getForgroundTransitionValue() {
        float result;
        if(mIsOpen) {
            if(mIsDuringAnimation) {
                result = mAnimationFraction > NORMAL_ANIM_MAX_FRACTION ? mTransitionLength : mTransitionLength * mAnimationFraction;
            } else {
                result = mTransitionLength;
            }
        } else {
            if(mIsDuringAnimation) {
                result = mTransitionLength * mAnimationFraction;
            } else {
                result = 0;
            }
        }
        return result;
    }

    private void drawFace(Canvas canvas, float fraction) {
        mPaint.setColor(mFaceColor);
        canvas.drawPath(mFacePath, mPaint);

        translateAndClipFace(canvas, fraction);
        drawEye(canvas, fraction);
        drawMouth(canvas, fraction);
    }

    private void translateAndClipFace(Canvas canvas, float fraction) {
        canvas.clipPath(mFacePath); //截掉超出face的部分

        float faceTranstion;
        if(fraction >= 0.0f && fraction < 0.5f) {
            faceTranstion = fraction * mFaceRadius * 4;
        } else if(fraction <= NORMAL_ANIM_MAX_FRACTION) {
            faceTranstion = -(NORMAL_ANIM_MAX_FRACTION - fraction) * mFaceRadius * 4;
        } else if(fraction <= (NORMAL_ANIM_MAX_FRACTION + FACE_ANIM_MAX_FRACTION) / 2) {
            faceTranstion = (fraction - NORMAL_ANIM_MAX_FRACTION) * mFaceRadius * 2;
        } else {
            faceTranstion = (FACE_ANIM_MAX_FRACTION - fraction) * mFaceRadius * 2;
        }
        canvas.translate(faceTranstion, 0);
    }

    private void drawEye(Canvas canvas, float fraction) {
        float scale;
        float startValue = 1.2f;
        float middleValue = (startValue + FACE_ANIM_MAX_FRACTION) / 2;

        if(fraction >= startValue && fraction <= middleValue) {
            scale = (middleValue - fraction) * 10;
        } else if(fraction > middleValue && fraction <= FACE_ANIM_MAX_FRACTION) {
            scale = (fraction - middleValue) * 10;
        } else {
            scale = 1.0f;
        }
        Log.i(tag, "scale = " + scale);

        float eyeRectWidth = mFaceRadius * 0.25f;
        float eyeRectHeight = mFaceRadius * 0.35f;
        float eyeXOffSet = mFaceRadius * 0.14f;
        float eyeYOffSet = mFaceRadius * 0.12f;
        float leftEyeCenterX = mCenterX - eyeXOffSet - eyeRectWidth/2;
        float leftEyeCenterY = mCenterY - eyeYOffSet - eyeRectHeight/2;
        float rightEyeCenterX = mCenterX + eyeXOffSet + eyeRectWidth/2;

        eyeRectHeight *= scale; //眨眼缩放
        float eyeLeft = leftEyeCenterX - eyeRectWidth/2 ;
        float eyeTop = leftEyeCenterY - eyeRectHeight/2;
        float eyeRight = leftEyeCenterX + eyeRectWidth/2;
        float eyeBottom = leftEyeCenterY + eyeRectHeight/2;
        RectF leftEye = new RectF(eyeLeft,eyeTop,eyeRight,eyeBottom);

        eyeLeft = rightEyeCenterX - eyeRectWidth/2;
        eyeRight = rightEyeCenterX + eyeRectWidth/2;
        RectF rightEye = new RectF(eyeLeft,eyeTop,eyeRight,eyeBottom);

        mPaint.setColor(mCurrentColor);

        canvas.drawOval(leftEye, mPaint);
        canvas.drawOval(rightEye, mPaint);
    }

    private void drawMouth(Canvas canvas, float fraction) {
        float eyeRectWidth = mFaceRadius * 0.2f;
        float eyeXOffSet = mFaceRadius * 0.14f;
        float eyeYOffSet = mFaceRadius * 0.30f;
        float mouthWidth = (eyeRectWidth + eyeXOffSet) * 2; //嘴的长度正好和双眼之间的距离一样
        float mouthHeight = (mFaceRadius * 0.05f);
        float mouthLeft = mCenterX - mouthWidth / 2;
        float mouthTop = mCenterY + eyeYOffSet; // mCenterY是face的原点
        float mouthRight = mouthLeft + mouthWidth;
        float mouthBottom = mouthTop + mouthHeight;

        mPaint.setColor(mCurrentColor);
        if(fraction <= 0.75) {
            Path path = new Path();
            path.moveTo(mouthLeft, mouthTop);
            path.quadTo(mCenterX, mCenterY, mouthRight, mouthTop);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(3);
            canvas.drawPath(path, mPaint);
        } else {
            Path path = new Path();
            path.moveTo(mouthLeft, mouthTop);
            float controlX = (mouthLeft + mouthRight) / 2;
            float controlY = mouthTop + mouthHeight + mouthHeight * 15f * (fraction - 0.75f);
            path.quadTo(controlX, controlY, mouthRight, mouthTop);
            path.close();
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawPath(path, mPaint);
        }
    }

    public void setState(boolean isOpen) {
        mIsOpen = isOpen;
        refreshState();
    }

    public void refreshState() {
        mCurrentColor = mIsOpen ? mBgColorOn : mBgColorOff;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN :
                return true;

            case MotionEvent.ACTION_CANCEL:
                break;

            case MotionEvent.ACTION_UP:
                if(mIsDuringAnimation) {
                    return true;
                }
                if(mIsOpen) {
                    startCloseAnimation();
                    mIsOpen = false;
                } else {
                    startOpenAnimation();
                    mIsOpen = true;
                }
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void startCloseAnimation() {
        mValueAnimator = ValueAnimator.ofFloat(NORMAL_ANIM_MAX_FRACTION, 0);
        mValueAnimator.setDuration(mOffAnimationDuration);
        mValueAnimator.addUpdateListener(this);
        mValueAnimator.addListener(this);
        mValueAnimator.setInterpolator(mInterpolator);
        mValueAnimator.start();
        startColorAnimation();
    }

    private void startOpenAnimation() {
        mValueAnimator = ValueAnimator.ofFloat(0, FACE_ANIM_MAX_FRACTION);
        mValueAnimator.setDuration(mOnAnimationDuration);
        mValueAnimator.addUpdateListener(this);
        mValueAnimator.addListener(this);
        mValueAnimator.setInterpolator(mInterpolator);
        mValueAnimator.start();
        startColorAnimation();
    }

    private void startColorAnimation() {
        int colorFrom = mIsOpen ? mBgColorOn : mBgColorOff;
        int colorTo = mIsOpen ? mBgColorOff : mBgColorOn;
        long duration = mIsOpen ? mOffAnimationDuration : mOnAnimationDuration;
        ValueAnimator colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimator.setInterpolator(mInterpolator);
        colorAnimator.setDuration(duration);
        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentColor = (int) animation.getAnimatedValue();
            }
        });
        colorAnimator.start();
    }

    @Override
    public void onAnimationStart(Animator animation) {
        mIsDuringAnimation = true;
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        mIsDuringAnimation = false;
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        mIsDuringAnimation = false;
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
        mIsDuringAnimation = true;
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        mAnimationFraction = (float) animation.getAnimatedValue();
        invalidate();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Log.i(tag, "onSaveInstanceState");
        Parcelable state = super.onSaveInstanceState();
        SavedState savedState = new SavedState(state);
        savedState.isOpen = mIsOpen ? 1 : 0;
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        Log.i(tag, "onRestoreInstanceState");
        SavedState savedState = (SavedState) state;
        boolean result = savedState.isOpen == 1;
        setState(result);
    }

    static class SavedState extends BaseSavedState {

        int isOpen;

        public SavedState(Parcel source) {
            super(source);
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(isOpen);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {

            @Override
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[0];
            }
        };
    }
}
