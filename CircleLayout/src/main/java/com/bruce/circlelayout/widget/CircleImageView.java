package com.bruce.circlelayout.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.ImageView;

import com.bruce.circlelayout.R;

/**
 *
 * Created by Bruce on 2016/11/23.
 */
public class CircleImageView extends ImageView implements Checkable, View.OnClickListener{

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
    private Drawable mCheckDrawable;
    private Rect mSrcRect, mDstRect;


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

        mCheckBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.check_animated_vector);

        mSrcRect = new Rect(0, 0, mCheckBitmap.getWidth(), mCheckBitmap.getHeight());
        mDstRect = new Rect();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);

        int minWidth = Math.min(mWidth, mHeight);
        int center = minWidth / 2;
        int halfCheckWidth = mCheckBitmap.getWidth() / 2;
        int halfCheckHeight = mCheckBitmap.getHeight() / 2;

        mDstRect.left = center - halfCheckWidth;
        mDstRect.right = center + halfCheckWidth;
        mDstRect.top = center - halfCheckHeight;
        mDstRect.bottom = center + halfCheckHeight;

        setMeasuredDimension(minWidth, minWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(isChecked) {
            onDrawChecked(canvas);
        }
    }

    private void onDrawChecked(Canvas canvas) {
        canvas.drawBitmap(mCheckBitmap, mSrcRect, mDstRect, mPaint);
    }

    @Override
    public void setChecked(boolean checked) {
        isChecked = checked;
        if(mOnCheckedChangeListener != null) {
            mOnCheckedChangeListener.onCheckedChanged(this, isChecked);
        }
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
    }

    public static interface OnCheckedChangeListener {
        void onCheckedChanged(View view, boolean isChecked);
    }
}
