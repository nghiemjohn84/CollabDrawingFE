package com.example.collabdrawingfe

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.google.firebase.database.*
import java.util.*


class PaintView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    private val paintColor = Color.BLACK
    private var mX: Float = 0.toFloat()
    private var mY: Float = 0.toFloat()
    private var mPath: Path? = null
    private val mPaint: Paint
    private val paths = ArrayList<InputPath>()
    private var currentColour: Int = 0
    private var previousColour: Int = 0
    private var backgroundColour = DEFAULT_BG_COLOUR
    private var strokeWidth: Int = 0
    private var mBitmap: Bitmap? = null
    private var mCanvas: Canvas? = null
    private val mBitmapPaint = Paint(Paint.DITHER_FLAG)

    private val dbFirebase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var drawnInstruction: DatabaseReference? = null
    val drawInstruction: DrawInstruction = DrawInstruction()

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

    fun init(metrics: DisplayMetrics, doodle: String) {
        val height = metrics.heightPixels
        val width = metrics.widthPixels

        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        mCanvas = Canvas(mBitmap!!)

        currentColour = DEFAULT_COLOUR
        strokeWidth = BRUSH_SIZE

        drawnInstruction = dbFirebase.getReference(doodle).child("drawingInstruction")

        drawInstruction.x = 0F
        drawInstruction.y = 0F
        drawInstruction.command = "init"
        drawnInstruction!!.setValue(drawInstruction)

        drawnInstruction!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.w("error", "Failed to read value.", error.toException())
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue(DrawInstruction::class.java)
                Log.d("command", value.toString())


                when (value!!.command) {
                    "init" -> {
                    }
                    "inputStart" -> {
                        inputStart(value.x!!, value.y!!)
                        invalidate()

                    }
                    "touching" -> {
                        touching(value.x!!, value.y!!)
                        invalidate()

                    }
                    "notTouching" -> {
                        notTouching()
                        drawInstruction.command = "init"
                        drawnInstruction?.setValue(drawInstruction)
                        invalidate()
                    }
                    "changeColour" -> {
                        currentColour = value!!.colour!!
                    }
                    "changeStrokeWidth" -> {
                        strokeWidth = value!!.strokeWidth!!
                    }
                    "clearCanvas" -> {
                        paths.clear()
                        invalidate()
                    }
                }
            }
        })
    }

    // Drawing functions

    override fun onDraw(canvas: Canvas) {
        canvas.save()
        mCanvas!!.drawColor(backgroundColour)
        for (ip in paths) {
            mPaint.color = ip.color
            mPaint.strokeWidth = ip.strokeWidth.toFloat()
            mCanvas!!.drawPath(ip.path, mPaint)

        }

        canvas.drawBitmap(mBitmap!!, 0f, 0f, mBitmapPaint)
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

        drawInstruction.x = x
        drawInstruction.y = y

        when (event.action) {

            MotionEvent.ACTION_DOWN -> {
                drawInstruction.command = "inputStart"
                drawInstruction.colour = currentColour
                drawInstruction.strokeWidth = strokeWidth
                drawnInstruction!!.setValue(drawInstruction)

                inputStart(x, y)
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                drawInstruction.command = "touching"
                drawnInstruction!!.setValue(drawInstruction)

                touching(x, y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                drawInstruction.command = "notTouching"
                drawnInstruction!!.setValue(drawInstruction)
                notTouching()

                invalidate()
                drawInstruction.command = "init"
                drawnInstruction!!.setValue(drawInstruction)

            }
        }

        return true
    }

    fun changeColour(colour: String) {
        currentColour = Color.parseColor(colour)
        drawInstruction.colour = currentColour
        drawInstruction.command = "changeColour"
        drawnInstruction!!.setValue(drawInstruction)
    }

    fun changeStrokeWidth(stroke: Int) {
        drawInstruction.strokeWidth = stroke
        drawInstruction.command = "changeStrokeWidth"
        drawnInstruction!!.setValue(drawInstruction)
    }

    fun clearCanvas(){
        drawInstruction.command = "clearCanvas"
        drawnInstruction!!.setValue(drawInstruction)
    }

    companion object {

        const val BRUSH_SIZE = 5
        const val DEFAULT_COLOUR = Color.BLACK
        const val DEFAULT_BG_COLOUR = Color.WHITE
        private const val TOUCH_TOLERANCE = 4f
        const val TAG = "PaintActivity"
    }
}
