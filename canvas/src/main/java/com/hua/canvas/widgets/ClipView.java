package com.hua.canvas.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by hzw on 2016/10/25.
 */
public class ClipView extends View {

    private Paint mPaint;
    private Path mPath;

    public ClipView(Context context) {
        this(context, null);
    }

    public ClipView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setFocusable(true);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(5);
        mPaint.setTextSize(25);
        mPaint.setTextAlign(Paint.Align.CENTER);

        mPath = new Path();
    }

    private void drawScene(Canvas canvas, String str) {
        canvas.clipRect(0, 0, 300, 300);
        canvas.drawColor(Color.WHITE);

        mPaint.setColor(Color.RED);
        canvas.drawLine(0, 0, 300, 300, mPaint);

        mPaint.setColor(Color.GREEN);
        canvas.drawCircle(90, 210, 90, mPaint);

        mPaint.setColor(Color.BLUE);
        str = str == null ? "Clipping" : str;
        canvas.drawText(str, 150, 150, mPaint);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawColor(Color.GRAY);

        canvas.save();
        canvas.translate(30, 30);
        drawScene(canvas, null);
        canvas.restore();

        canvas.save();
        canvas.translate(30, 400);
        canvas.clipRect(0, 0, 180, 180);
        canvas.clipRect(120, 120, 300, 300, Region.Op.DIFFERENCE);
        drawScene(canvas, "Difference");
        canvas.restore();

        canvas.save();
        canvas.translate(30, 800);
        mPath.addCircle(150, 150, 50, Path.Direction.CCW);
        canvas.clipPath(mPath);
        mPath.addCircle(150, 150, 150, Path.Direction.CCW);
        canvas.clipPath(mPath, Region.Op.REPLACE);
        drawScene(canvas, "Replace");
        canvas.restore();

        canvas.save();
        canvas.translate(400, 400);
        canvas.clipRect(0, 0, 180, 180);
        canvas.clipRect(120, 120, 300, 300, Region.Op.XOR);
        drawScene(canvas, "Xor");
        canvas.restore();

        canvas.save();
        canvas.translate(400, 800);
        canvas.clipRect(0, 0, 180, 180);
        canvas.clipRect(120, 120, 300, 300, Region.Op.REVERSE_DIFFERENCE);
        drawScene(canvas, "Reverse_Difference");
        canvas.restore();

        canvas.save();
        canvas.translate(800, 400);
        canvas.clipRect(0, 0, 180, 180);
        canvas.clipRect(120, 120, 300, 300, Region.Op.UNION);
        drawScene(canvas, "Union");
        canvas.restore();

        canvas.save();
        canvas.translate(800, 800);
        canvas.clipRect(0, 0, 180, 180);
        canvas.clipRect(120, 120, 300, 300, Region.Op.INTERSECT);
        drawScene(canvas, "Intersect");
        canvas.restore();
    }
}
