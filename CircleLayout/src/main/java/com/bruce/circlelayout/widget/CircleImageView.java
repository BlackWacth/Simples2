package com.bruce.circlelayout.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Checkable;
import android.widget.ImageView;

import com.bruce.circlelayout.R;

/**
 *
 * Created by Bruce on 2016/11/23.
 */
public class CircleImageView extends ImageView implements Checkable, View.OnClickListener{

    public static final String tag = "CircleImageView";

    private float mAnagle = 0;
    private int mPosition = 0;
    private String mName;
    private Drawable mDrawable;
    private boolean isChecked = false;
    private OnCheckedChangeListener mOnCheckedChangeListener;
    private Paint mPaint;
    private Path mPath;
    private int mWidth, mHeight;
    private Bitmap mCheckBitmap;
    private AnimatedVectorDrawable mCheckDrawable;
    private Rect mCheckBounds;
    private int halfCheckWidth, halfCheckHeight;

    public CircleImageView(Context context) {
        this(context, null);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.RED);

        mPath = new Path();
        setOnClickListener(this);

        mCheckDrawable = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.check_animated_vector, null);
        Log.i(tag, "mCheckDrawable = " + mCheckDrawable);
        mCheckBounds = mCheckDrawable.getBounds();
        halfCheckWidth = mCheckBounds.width() / 2;
        halfCheckHeight = mCheckBounds.height() / 2;
        Log.i(tag, "bounds = " + mCheckBounds);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);

        int minWidth = Math.min(mWidth, mHeight);
        int center = minWidth / 2;
        mCheckBounds.left = center - halfCheckWidth;
        mCheckBounds.right = center + halfCheckWidth;
        mCheckBounds.top = center - halfCheckHeight;
        mCheckBounds.bottom = center + halfCheckHeight;
//        mCheckDrawable.setBounds(mCheckBounds);

        setMeasuredDimension(minWidth, minWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(isChecked) {
            onDrawChecked(canvas);
//            mCheckDrawable.start();
        }
    }

    private void onDrawChecked(Canvas canvas) {
        mCheckDrawable.draw(canvas);
    }

    @Override
    public void setChecked(boolean checked) {
        isChecked = checked;
        if(mOnCheckedChangeListener != null) {
            mOnCheckedChangeListener.onCheckedChanged(this, isChecked);
        }
        postInvalidate();
    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public void toggle() {
        setChecked(!isChecked);
    }

    public OnCheckedChangeListener getOnCheckedChangeListener() {
        return mOnCheckedChangeListener;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        mOnCheckedChangeListener = onCheckedChangeListener;
    }

    public float getAnagle() {
        return mAnagle;
    }

    public void setAnagle(float anagle) {
        mAnagle = anagle;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    @Override
    public Drawable getDrawable() {
        return mDrawable;
    }

    public void setDrawable(Drawable drawable) {
        mDrawable = drawable;
    }

    @Override
    public void onClick(View v) {
        toggle();
        Log.i(tag, "isChecked = " + isChecked);
    }

    public static interface OnCheckedChangeListener {
        void onCheckedChanged(View view, boolean isChecked);
    }
}
