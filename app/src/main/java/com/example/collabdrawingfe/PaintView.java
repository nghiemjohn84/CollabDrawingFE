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
    public static final int DEFAULT_COLOR = Color.BLACK;
    public static final int DEFAULT_BG_COLOR = Color.WHITE;
    private static final float TOUCH_TOLERANCE = 4;
    private float userX, userY;
    private Path userPath;
    private Paint userPaint;
    private ArrayList <InputPath> paths = new ArrayList<>();
    private int currentColor;
    private int backgroundColor = DEFAULT_BG_COLOR;
    private int strokeWidth;
    private Bitmap userBitmap;
    private Canvas userCanvas;
    private Paint userBitmapPaint = new Paint(Paint.DITHER_FLAG);



    public PaintView(Context context) {
        this(context,null)
    }

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        userPaint = new Paint();
        userPaint.setAntiAlias(true);
        userPaint.setDither(true);
        userPaint.setColor(DEFAULT_COLOR);
        userPaint.setStyle(Paint.Style.STROKE);
        userPaint.setStrokeJoin(Paint.Join.ROUND);
        userPaint.setStrokeCap(Paint.Cap.ROUND);
        userPaint.setAlpha(255);
    }
    public void init(DisplayMetrics metrics) {
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        userBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        userCanvas = new Canvas(userBitmap);

        currentColor = DEFAULT_COLOR;
        strokeWidth = BRUSH_SIZE;
    }
    public void clear() {
        backgroundColor = DEFAULT_BG_COLOR;
        paths.clear();
        normal();
        invalidate();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        userCanvas.drawColor(backgroundColor);
        canvas.drawBitmap(userBitmap, 0, 0, userBitmapPaint);
        canvas.restore();
    }

    private void inputStart(float x, float y) {
        userPath = new Path();
        InputPath ip = new InputPath(currentColor, strokeWidth, userPath);
        paths.add(ip);

        userPath.reset();
        userPath.moveTo(x,y);
        userX = x;
        userY = y;
    }

    private void touching(float x, float y){
        float dx = Math.abs(x-userX);
        float dy = Math.abs(x-userY);

        if( dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE){
            userPath.quadTo(userX, userY, (x + userX) / 2, (y + userY) / 2);
            userX = x;
            userY = y;
        }
    }
    private void notTouching() {
        userPath.lineTo(userX, userY);
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
