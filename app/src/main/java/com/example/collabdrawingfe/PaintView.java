package com.example.collabdrawingfe;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class PaintView extends View {

    public static int BRUSH_SIZE = 20;
    public static int DEFAULT_COLOR = Color.BLACK;
    public static final int DEFAULT_BG_COLOR = Color.WHITE;
    private static final float TOUCH_TOLERANCE = 4;
    private float mX, mY;
    private Path mPath;
    private Paint mPaint;
    private ArrayList<InputPath> paths = new ArrayList<>();
    private int currentColor;
    private int backgroundColor = DEFAULT_BG_COLOR;
    private int strokeWidth;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);



    public PaintView(Context context) {
        this(context,null);
    }

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(DEFAULT_COLOR);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAlpha(255);
    }
    public void init(DisplayMetrics metrics) {
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        currentColor = DEFAULT_COLOR;
        strokeWidth = BRUSH_SIZE;
    }

    public void red() {
        currentColor = Color.RED;

    }

    public void clear() {
        backgroundColor = DEFAULT_BG_COLOR;
        paths.clear();
        invalidate();

    }

    public void eraser() {
        currentColor = backgroundColor;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        mCanvas.drawColor(backgroundColor);
        for (InputPath ip : paths) {
            mPaint.setColor(ip.color);
            mPaint.setStrokeWidth(ip.strokeWidth);
            mCanvas.drawPath(ip.path, mPaint);

        }
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.restore();
    }

    private void inputStart(float x, float y) {
        mPath = new Path();
        InputPath ip = new InputPath(currentColor, strokeWidth, mPath);
        paths.add(ip);

        mPath.reset();
        mPath.moveTo(x,y);
        mX = x;
        mY = y;
    }

    private void touching(float x, float y){
        float dx = Math.abs(x-mX);
        float dy = Math.abs(x-mY);

        if( dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE){
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }
    private void notTouching() {
        mPath.lineTo(mX, mY);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN :
                inputStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE :
                touching(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP :
                notTouching();
                invalidate();
                break;
        }

        return true;
    }
}
