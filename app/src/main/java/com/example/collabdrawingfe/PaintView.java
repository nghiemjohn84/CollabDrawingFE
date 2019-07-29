package com.example.collabdrawingfe;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import java.util.ArrayList;

public class PaintView extends View {

    public static int BRUSH_SIZE = 20;
    public static final int DEFAULT_COLOR = Color.BLACK;
    public static final int DEFAULT_BG_COLOR = Color.WHITE;
    private static final float TOUCH_TOLERANCE = 4;
    private float userX, userX;
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
}
