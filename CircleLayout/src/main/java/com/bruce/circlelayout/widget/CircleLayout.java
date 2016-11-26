package com.bruce.circlelayout.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by Bruce on 2016/11/24.
 */
public class CircleLayout extends ViewGroup implements View.OnClickListener{

    public static final String tag = "CircleLayout";

    /**默认间隔角度 */
    private final static float DEFAULT_INTERVAL_ANGLE = 45f;

    private int mLayoutWidth, mLayoutHeight;
    private int mChildWidth, mChildHeight;
    private int mCenterChildWidth, mCenterChildHeight;
    private int mBixRadius;
    private int mCenterX, mCenterY;
    private long mDuration = 500;
    private State mState = State.HIDE;
    private boolean isCanRotatd = true;
    private boolean isRotating = true;
    private int mSpeed = 25;
    private float mAngle = 0;
    /**两图标间隔角度 */
    private float mIntervalAngle = DEFAULT_INTERVAL_ANGLE;
    private ObjectAnimator mRotateAnimation;

    public CircleLayout(Context context) {
        this(context, null);
    }

    public CircleLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        for(int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            if(child.getVisibility() == GONE) {
                continue;
            }
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(mState == State.SHOW) {
            return ;
        }
        mLayoutWidth = r - l;
        mLayoutHeight = b - t;
        Log.i(tag, "onLayout --> mLayoutWidth = " + mLayoutWidth);
        Log.i(tag, "onLayout --> mLayoutHeight = " + mLayoutHeight);

        mChildWidth = getChildAt(0).getMeasuredWidth();
        mChildHeight = getChildAt(0).getMeasuredHeight();
        mBixRadius = (mLayoutWidth - mChildWidth) / 2;
        Log.i(tag, "onLayout --> mChildWidth = " + mChildWidth);
        Log.i(tag, "onLayout --> mChildHeight = " + mChildHeight);
        Log.i(tag, "onLayout --> mBixRadius = " + mBixRadius);

        View centerChild = getChildAt(getChildCount() - 1);
        mCenterChildWidth = centerChild.getMeasuredWidth();
        mCenterChildHeight = centerChild.getMeasuredHeight();
        centerChild.setOnClickListener(this);

        int left, top, right, bottom;
        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            if(child.getVisibility() == GONE) {
                continue;
            }

            left = mLayoutWidth / 2 - child.getMeasuredWidth() / 2;
            top = mLayoutHeight / 2 - child.getMeasuredHeight() / 2;
            right = mLayoutWidth / 2 + child.getMeasuredWidth() / 2;
            bottom = mLayoutHeight / 2 + child.getMeasuredHeight() / 2;
            child.layout(left, top, right, bottom);

            if (i != getChildCount() - 1) {
                CircleImageView circleImageView = (CircleImageView) child;
                circleImageView.setAnagle(i * mIntervalAngle);
                circleImageView.setPosition(i);
            }
        }
    }

    private int getStartLeft() {
        return mLayoutWidth / 2 - mChildWidth / 2;
//        return 1050 / 2 - 175 / 2;
    }

    private int getStartTop() {
        return mLayoutHeight / 2 - mChildHeight / 2;
//        return 1050 / 2 - 175 / 2;
    }

    public float getAngle() {
        return mAngle;
    }

    public void setAngle(float angle) {
        mAngle = angle;
        chanageChildLayout();
    }

//    private float filterAngle(float angle) {
//        if(angle > 360) {
//            return filterAngle(angle - 360);
//        } else if(angle < 0) {
//            return filterAngle(angle + 360);
//        } else {
//            return angle;
//        }
//    }

    private int getEndLeft(int position, float angle){
        CircleImageView child = (CircleImageView) getChildAt(position);
        return (int) (mLayoutWidth / 2 + mBixRadius * Math.cos(Math.toRadians(child.getAnagle() + angle))) - mChildWidth / 2;
    }

    private int getEndTop(int position, float angle){
        CircleImageView child = (CircleImageView) getChildAt(position);
        Log.i(tag, "getEndTop >>>> mLayoutWidth = " + mLayoutWidth);
        Log.i(tag, "getEndTop >>>> mBixRadius = " + mBixRadius);
        Log.i(tag, "getEndTop >>>> mChildHeight = " + mChildHeight);
        int top = (int) (mLayoutWidth / 2 + mBixRadius * Math.sin(Math.toRadians(child.getAnagle() + angle))) - mChildHeight / 2;
        Log.i(tag, "getEndTop >>>> top = " + top);
        return top;
    }

    /**
     * 该方法才是实现旋转的本质方法
     */
    private void chanageChildLayout() {
        int left, top;
        final int childCount = getChildCount() - 1;

        for(int i = 0; i < childCount; i++) {
            final CircleImageView child = (CircleImageView) getChildAt(i);
            if(child.getVisibility() == GONE) {
                continue;
            }
            left = getEndLeft(i, mAngle);
            top = getEndTop(i, mAngle);
            child.layout(left, top, left + mChildWidth, top + mChildHeight);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    /**
     * 移动动画， 展开与隐藏
     * @param view
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     * @param isShow 是否展开，标记每块的状态改变
     */
    private void translationAnimation(View view, float startX, float startY, float endX, float endY, final boolean isShow) {
        final AnimatorSet set = new AnimatorSet();
        ObjectAnimator viewXAnimator = ObjectAnimator.ofFloat(view, View.X, startX, endX);
        ObjectAnimator viewYAnimator = ObjectAnimator.ofFloat(view, View.Y, startY, endY);
        set.play(viewXAnimator).with(viewYAnimator);
        set.setDuration(mDuration);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mState = isShow ? State.SHOW : State.HIDE;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                mState = State.SHOWING;
            }
        });
        set.start();
    }

    /**
     * 旋转动画
     * @param endAngle
     * @param duration
     */
    private void rotateAnimation(final float endAngle, long duration) {
        if(mRotateAnimation != null && mRotateAnimation.isRunning() || Math.abs(mAngle - endAngle) < 1) {
            return ;
        }

        mRotateAnimation = ObjectAnimator.ofFloat(CircleLayout.this, "angle", mAngle, endAngle);
        mRotateAnimation.setDuration(duration);
        mRotateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        mRotateAnimation.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                isRotating = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isRotating = false;
//                mAngle = filterAngle(endAngle);
                Log.i(tag, "mAngle = " + mAngle);
            }
        });
        mRotateAnimation.start();
    }

    /**
     * 停止旋转动画
     */
    public void stopRotateAnimation() {
        if(mRotateAnimation != null && mRotateAnimation.isRunning()) {
            mRotateAnimation.cancel();
            mRotateAnimation = null;
        }
    }

    private void show() {
        for(int i = 0; i < getChildCount() - 1; i++) {
            translationAnimation(getChildAt(i), getStartLeft(), getStartTop(), getEndLeft(i, mAngle), getEndTop(i, mAngle), true);
        }
    }

    private void hide() {
        for(int i = 0; i < getChildCount() - 1; i++) {
            CircleImageView child = (CircleImageView) getChildAt(i);
            child.setChecked(false);
            translationAnimation(child, getEndLeft(i, mAngle), getEndTop(i, mAngle), getStartLeft(), getStartTop(),  false);
        }
    }

    @Override
    public void onClick(View v) {
        if(mState == State.SHOWING) {
            return ;
        }
        if(mState == State.SHOW) {
            hide();
        } else if(mState == State.HIDE) {
            show();
        }
    }

    private float mTouchStartAngle;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled() || !isCanRotatd) {
            return false;
        }
        if(mState != State.SHOW) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchStartAngle = getPositionAngle(event.getX(), event.getY());
                Log.i(tag, "mTouchStartAngle = " + mTouchStartAngle);
                break;

            case MotionEvent.ACTION_MOVE:
                float currentAngle = getPositionAngle(event.getX(), event.getY());
                Log.i(tag, "currentAngle = " + currentAngle);
                mAngle += (mTouchStartAngle - currentAngle);
                Log.i(tag, "mAngle = " + mAngle);
                chanageChildLayout();
                mTouchStartAngle = currentAngle;
                isRotating = true;
                break;

            case MotionEvent.ACTION_UP:
                isRotating = false;
                break;
        }
        return true;
    }


    private float getPositionAngle(float xTouch, float yTouch) {
        float x = xTouch - mLayoutWidth / 2f;
        float y = mLayoutHeight / 2f - yTouch;

        switch (getPositionQuadrant(x, y)) {
            case 1:
                return (float) (Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
            case 2:
            case 3:
                return (float) (180 - (Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI));
            case 4:
                return (float) (360 + Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
            default:
                return 0;

        }
    }
    /**
     * 根据坐标值获取坐标在几象限
     * @param x
     * @param y
     * @return
     */
    private int getPositionQuadrant(double x, double y) {
        if (x >= 0) {
            return y >= 0 ? 1 : 4;
        } else {
            return y >= 0 ? 2 : 3;
        }
    }

    enum State {
        HIDE, SHOWING, SHOW
    }
}
