package com.example.collabdrawingfe

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

import java.util.ArrayList

class PaintView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    private val paintColor = Color.BLACK
    private var mX: Float = 0.toFloat()
    private var mY: Float = 0.toFloat()
    private var mPath: Path? = null
    private val mPaint: Paint
    private val paths = ArrayList<InputPath>()
    private var currentColour: Int = 0
    private var backgroundColour = DEFAULT_BG_COLOUR
    private var strokeWidth: Int = 0
    private var mBitmap: Bitmap? = null
    private var mCanvas: Canvas? = null
    private val mBitmapPaint = Paint(Paint.DITHER_FLAG)

    private val dbFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    init {
        mPaint = Paint()
        mPaint.isAntiAlias = true
        mPaint.isDither = true
        mPaint.color = paintColor
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeJoin = Paint.Join.ROUND
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.alpha = 255
    }

    private val instructionsRef = dbFirestore.collection("instructions")


    fun init(metrics: DisplayMetrics) {
        val height = metrics.heightPixels
        val width = metrics.widthPixels

        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        mCanvas = Canvas(mBitmap!!)

        Log.d("PaintView-mCanvas-init", mCanvas.toString())


        val pathdata = HashMap<String,ArrayList<out Any>>()

        instructionsRef.document(mCanvas.toString()).set(pathdata)
            .addOnSuccessListener { documentReference ->
                // Toast.makeText(this, "path added to database", Toast.LENGTH_SHORT).show()
                Log.d("PaintView - onSuccess", paths.toString())
            }
            .addOnFailureListener { e ->
                Log.d("PaintView", "Error adding to database: ", e)
            }


        currentColour = DEFAULT_COLOUR
        strokeWidth = BRUSH_SIZE
    }

    fun clear() {
        paths.clear()
        invalidate()

    }

    fun eraser() {
        currentColour = Color.WHITE
    }

    fun brush() {
        currentColour = DEFAULT_COLOUR
    }


    override fun onDraw(canvas: Canvas) {
        canvas.save()
        mCanvas!!.drawColor(backgroundColour)
        for (ip in paths) {
            mPaint.color = ip.color
            mPaint.strokeWidth = ip.strokeWidth.toFloat()
            mCanvas!!.drawPath(ip.path, mPaint)


        }

        canvas.drawBitmap(mBitmap!!, 0f, 0f, mBitmapPaint)

        Log.d("PaintView - paths", mBitmapPaint.toString())

        val pathdata = hashMapOf<String, ArrayList<InputPath>>(
            "pathname" to paths
        )

        instructionsRef.document(mCanvas.toString()).set(pathdata)
            .addOnSuccessListener { documentReference ->
                // Toast.makeText(this, "path added to database", Toast.LENGTH_SHORT).show()
                Log.d("PaintView - onDraw", paths.toString())
            }
            .addOnFailureListener { e ->
                Log.d("PaintView - onDraw", "Error adding to database: ", e)
            }


        canvas.restore()
    }

    private fun inputStart(x: Float, y: Float) {
        mPath = Path()
        val ip = InputPath(currentColour, strokeWidth, mPath!!)
        paths.add(ip)

        mPath!!.reset()
        mPath!!.moveTo(x, y)
        mX = x
        mY = y
    }

    private fun touching(x: Float, y: Float) {
        val dx = Math.abs(x - mX)
        val dy = Math.abs(x - mY)

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath!!.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2)
            mX = x
            mY = y
        }
    }

    private fun notTouching() {
        mPath!!.lineTo(mX, mY)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {

            MotionEvent.ACTION_DOWN -> {
                inputStart(x, y)
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                touching(x, y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                notTouching()
                invalidate()
            }
        }

        return true
    }

    companion object {

        const val BRUSH_SIZE = 20
        const val DEFAULT_COLOUR = Color.BLACK
        const val DEFAULT_BG_COLOUR = Color.WHITE
        private const val TOUCH_TOLERANCE = 4f
    }
}
