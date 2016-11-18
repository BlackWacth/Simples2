package com.hua.canvas.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.RegionIterator;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by hzw on 2016/10/25.
 */

public class RegionView extends View {

    private final Paint mPaint = new Paint();
    private final Rect mRect1 = new Rect();
    private final Rect mRect2 = new Rect();

    public RegionView(Context context) {
        this(context, null);
    }

    public RegionView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setFocusable(true);

        mPaint.setAntiAlias(true);
        mPaint.setTextAlign(Paint.Align.CENTER);

        mRect1.set(50, 50, 350, 300);
        mRect2.set(150, 100, 400, 400);
    }

    private void drawOriginalRects(Canvas canvas, int alpha) {
        mPaint.setStrokeWidth(5);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.RED);
        mPaint.setAlpha(alpha);
        drawCentered(canvas, mRect1, mPaint);

        mPaint.setColor(Color.BLUE);
        mPaint.setAlpha(alpha);
        drawCentered(canvas, mRect2, mPaint);

        mPaint.setStyle(Paint.Style.FILL);
    }

    private void drawCentered(Canvas canvas, Rect rect, Paint paint) {
        float inset = paint.getStrokeWidth() * 0.5f;
        if (inset == 0) {
            inset = 0.5f;
        }
        canvas.drawRect(rect.left + inset, rect.top + inset, rect.right - inset, rect.bottom - inset, paint);
    }

    private void drawRgn(Canvas canvas, int color, String str, Region.Op op) {
        if (str != null) {
            mPaint.setColor(Color.BLACK);
            mPaint.setTextSize(40);
            canvas.drawText(str, 180, 50, mPaint);
        }

        Region rgn = new Region();
        rgn.set(mRect1);
        rgn.op(mRect2, op);

        mPaint.setColor(color);
        RegionIterator iter = new RegionIterator(rgn);
        Rect r = new Rect();

        canvas.translate(0, 30);
        mPaint.setColor(color);
        while (iter.next(r)) {
            canvas.drawRect(r, mPaint);
        }
        drawOriginalRects(canvas, 0x80);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.GRAY);

        canvas.save();
        canvas.translate(200, 30);
        drawOriginalRects(canvas, 0xff);
        canvas.restore();

        mPaint.setStyle(Paint.Style.FILL);

        canvas.save();
        canvas.translate(30, 450);
        drawRgn(canvas, Color.RED, "Union", Region.Op.UNION);
        canvas.restore();

        canvas.save();
        canvas.translate(30, 900);
        drawRgn(canvas, Color.BLUE, "Xor", Region.Op.XOR);
        canvas.restore();

        canvas.save();
        canvas.translate(450, 450);
        drawRgn(canvas, Color.GREEN, "Difference", Region.Op.DIFFERENCE);
        canvas.restore();


        canvas.save();
        canvas.translate(450, 900);
        drawRgn(canvas, Color.WHITE, "Intersect", Region.Op.INTERSECT);
        canvas.restore();

        canvas.save();
        canvas.translate(900, 450);
        drawRgn(canvas, Color.YELLOW, "Replace", Region.Op.REPLACE);
        canvas.restore();


        canvas.save();
        canvas.translate(900, 900);
        drawRgn(canvas, Color.CYAN, "Reverse_Difference", Region.Op.REVERSE_DIFFERENCE);
        canvas.restore();
    }
}