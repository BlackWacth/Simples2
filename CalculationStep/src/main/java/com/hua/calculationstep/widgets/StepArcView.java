package com.hua.calculationstep.widgets;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.hua.calculationstep.R;
import com.hua.calculationstep.utils.ScreenUtil;

/**
 * 环形跑步进度条
 * Created by hzw on 2016/10/28.
 */
public class StepArcView extends View {

    /**默认圆弧宽度 */
    public static final float DEFAULT_BORDER_WIDTH = 48f;
    /**默认圆弧开始角度 */
    public static final float DEFAULT_START_ANGLE = 135f;
    /**默认圆弧扫过角度 */
    public static final float DEFAULT_SWEEP_ANGLE = 270f;

    /**弧形线条宽度 */
    private float mBorderWidth = DEFAULT_BORDER_WIDTH;
    /**弧形开始角度 */
    private float mStartAngle = DEFAULT_START_ANGLE;
    /**弧形扫过角度，顺时针为正 */
    private float mSweepAngle = DEFAULT_SWEEP_ANGLE;
    /**总步数，环形容量 */
    private int mTotalStepCount = 50000;
    /**环形背景颜色 */
    private int mArcBgColor;
    /**环形填充颜色 */
    private int mArcColor;
    /**环形内部步数颜色 */
    private int mStepCountColor;
    /**环形下部“步数”文字颜色 */
    private int mTextFontColor;

    /**当前已经扫过弧形角度 */
    private float mCurrentSweepAngle = 0f;
    /**有效弧形角度，用于绘制，实时更改 */
    private float mValidSweepAngle = 0f;
    /**当前步数 */
    private int mCurrentStepCount = 0;
    /**有效步数，用于绘制，实时更改 */
    private int mValidStepCount = 0;
    private String stepText = "步数";

    private Paint mArcBgPaint, mArcPaint;
    private Paint mStepCountPaint, mTextFontPaint;
    private RectF mArcRectF;
    private float mCenterX;
    private Rect mStepCountRect, mStepTextRect;

    public StepArcView(Context context) {
        super(context);
        init();
    }

    public StepArcView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StepArcView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mArcBgPaint = new Paint();
        mArcBgPaint.setAntiAlias(true);
        mArcBgPaint.setStrokeCap(Paint.Cap.ROUND);
        mArcBgPaint.setStrokeJoin(Paint.Join.ROUND);
        mArcBgPaint.setStyle(Paint.Style.STROKE);
        mArcBgPaint.setStrokeWidth(mBorderWidth);

        mArcPaint = new Paint(mArcBgPaint);
        mArcBgColor = getResources().getColor(R.color.teal_400);
        mArcBgPaint.setColor(mArcBgColor);
        mArcColor = getResources().getColor(R.color.orange_700);
        mArcPaint.setColor(mArcColor);

        mArcRectF = new RectF();

        mStepCountPaint = new Paint();
        mStepCountPaint.setAntiAlias(true);
        mStepCountPaint.setTextAlign(Paint.Align.CENTER);

        mTextFontPaint = new Paint(mStepCountPaint);

        mStepCountColor = getResources().getColor(R.color.red_800);
        mStepCountPaint.setColor(mStepCountColor);
        mTextFontColor = getResources().getColor(R.color.grey_400);
        mTextFontPaint.setColor(mTextFontColor);

        mStepCountRect = new Rect();
        mStepTextRect = new Rect();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width;
        if(widthMode == MeasureSpec.EXACTLY || heightMode == MeasureSpec.EXACTLY) {
            width = Math.min(widthSize, heightSize);
        } else {
            width = ScreenUtil.getScreenWidth(getContext()) / 2;
        }
        mCenterX = width / 2;
        mArcRectF.left = mBorderWidth / 2;
        mArcRectF.top = mBorderWidth / 2;
        mArcRectF.right = width - mBorderWidth / 2;
        mArcRectF.bottom = width - mBorderWidth / 2;
        setMeasuredDimension(width, width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawArcBg(canvas);
        drawArc(canvas);
        drawStepCount(canvas);
        drawFont(canvas);
    }

    /**
     * 绘制弧形背景
     * @param canvas
     */
    private void drawArcBg(Canvas canvas) {
        canvas.drawArc(mArcRectF, mStartAngle, mSweepAngle, false, mArcBgPaint);
    }

    /**
     * 绘制弧形填充色
     * @param canvas
     */
    private void drawArc(Canvas canvas) {
        canvas.drawArc(mArcRectF, mStartAngle, mValidSweepAngle, false, mArcPaint);
    }

    /**
     * 绘制步数
     * @param canvas
     */
    private void drawStepCount(Canvas canvas) {
        mStepCountPaint.setTextSize(getStepCountFontSize(mValidStepCount));
        String stepCount = String.valueOf(mValidStepCount);
        mStepCountPaint.getTextBounds(stepCount, 0, stepCount.length(), mStepCountRect);
        canvas.drawText(stepCount, mCenterX, mCenterX + mStepCountRect.height() / 2, mStepCountPaint);
    }

    /**
     * 绘制步数两个文字
     * @param canvas
     */
    private void drawFont(Canvas canvas) {
        mTextFontPaint.setTextSize(ScreenUtil.dp2px(getContext(), 30));
        mTextFontPaint.getTextBounds(stepText, 0, stepText.length(), mStepTextRect);
        canvas.drawText(stepText, mCenterX, mCenterX * 2 - mStepTextRect.height(), mTextFontPaint);
    }

    /**
     * 动态改变步数字体大小
     * @param num
     * @return
     */
    private int getStepCountFontSize(int num) {
        String s = String.valueOf(num);
        int length = s.length();
        int size = 0;
        if (length <= 4) {
            size = 60;
        } else if (length > 4 && length <= 6) {
            size = 50;
        } else if (length > 6 && length <= 8) {
            size = 40;
        } else if (length > 8) {
            size = 30;
        }
        return ScreenUtil.dp2px(getContext(), size);
    }

    /**
     * 动态设置动画时间
     * @param count
     * @return
     */
    private long getAnimDuration(int count){
        long duration = 0;
        if(count < 1000) {
            duration = 500;
        } else if(count >= 1000 && count < 3000) {
            duration = 1500;
        } else if(count >= 3000 && count < 5000) {
            duration = 2000;
        } else if(count >= 5000 && count < 10000) {
            duration = 2500;
        } else if(count >= 10000) {
            duration = 3000;
        }
        return duration;
    }

    /**
     * 设置步数，相当于初始化，从0开始。
     * @param currentStepCount
     */
    public void setCurrentStepCount(int currentStepCount) {
        mCurrentStepCount = currentStepCount >= mTotalStepCount ? mTotalStepCount : currentStepCount;
        mCurrentSweepAngle = (float) ((mCurrentStepCount * 1.0 / mTotalStepCount) * mSweepAngle);
        startStepCountAnimation(0, mCurrentSweepAngle, 0, mCurrentStepCount, getAnimDuration(currentStepCount));
    }

    /**
     * 添加步数，不从开始，从上一次结束为止开始。
     * @param addStepCount
     */
    public void addCurrentStepCount(int addStepCount) {
        int stepCount = (mCurrentStepCount + addStepCount) >= mTotalStepCount ? mTotalStepCount : (mCurrentStepCount + addStepCount);
        float sweepAngle = (float) ((stepCount * 1.0 / mTotalStepCount) * mSweepAngle);
        startStepCountAnimation(mCurrentSweepAngle, sweepAngle, mCurrentStepCount, stepCount, getAnimDuration(addStepCount));
    }

    /**
     * 步数改变动画
     * @param startAngle
     * @param endAngle
     * @param startStepCount
     * @param endStepCount
     * @param duration
     */
    public void startStepCountAnimation(float startAngle, final float endAngle, int startStepCount, final int endStepCount, long duration){
        ValueAnimator sweepAngleAnimator = ValueAnimator.ofFloat(startAngle, endAngle);
        sweepAngleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mValidSweepAngle = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        ValueAnimator textChangeAnimator = ValueAnimator.ofInt(startStepCount, endStepCount);
        textChangeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mValidStepCount = (int) valueAnimator.getAnimatedValue();
            }
        });
        AnimatorSet set = new AnimatorSet();
        set.play(sweepAngleAnimator).with(textChangeAnimator);
        set.setDuration(duration);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentSweepAngle = endAngle;
                mCurrentStepCount = endStepCount;
            }
        });
        set.start();
    }

    public float getBorderWidth() {
        return mBorderWidth;
    }

    public void setBorderWidth(float borderWidth) {
        mBorderWidth = borderWidth;
        mArcBgPaint.setStrokeWidth(mBorderWidth);
        mArcPaint.setStrokeWidth(mBorderWidth);
        invalidate();
    }

    public float getStartAngle() {
        return mStartAngle;
    }

    public void setStartAngle(float startAngle) {
        mStartAngle = startAngle;
        invalidate();
    }

    public float getSweepAngle() {
        return mSweepAngle;
    }

    public void setSweepAngle(float sweepAngle) {
        mSweepAngle = sweepAngle;
        invalidate();
    }

    public int getTotalStepCount() {
        return mTotalStepCount;
    }

    public void setTotalStepCount(int totalStepCount) {
        mTotalStepCount = totalStepCount;
        invalidate();
    }

    public int getArcBgColor() {
        return mArcBgColor;
    }

    public void setArcBgColor(int arcBgColor) {
        mArcBgColor = arcBgColor;
        mArcBgPaint.setColor(mArcBgColor);
        invalidate();
    }

    public int getArcColor() {
        return mArcColor;
    }

    public void setArcColor(int arcColor) {
        mArcColor = arcColor;
        mArcPaint.setColor(mArcColor);
        invalidate();
    }

    public int getStepCountColor() {
        return mStepCountColor;
    }

    public void setStepCountColor(int stepCountColor) {
        mStepCountColor = stepCountColor;
        mStepCountPaint.setColor(mStepCountColor);
        invalidate();
    }

    public int getTextFontColor() {
        return mTextFontColor;
    }

    public void setTextFontColor(int textFontColor) {
        mTextFontColor = textFontColor;
        mTextFontPaint.setColor(mTextFontColor);
        invalidate();
    }
}
