package com.example.collabdrawingfe

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges

import java.util.ArrayList


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

 //   private val instructionsRef = dbFirestore.collection("instructions")


    fun init(metrics: DisplayMetrics) {
        val height = metrics.heightPixels
        val width = metrics.widthPixels

        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        mCanvas = Canvas(mBitmap!!)

        Log.d("PaintView-mCanvas-init", mCanvas.toString())

        currentColour = DEFAULT_COLOUR
        strokeWidth = BRUSH_SIZE

    }


    fun clear() {
        paths.clear()
        invalidate()

    }

    fun eraser() {
        previousColour = currentColour
        currentColour = backgroundColour
    }


    fun brush() {
        currentColour = previousColour
    }

    //Brush Colours:
    fun red() {
        currentColour = Color.RED
    }
    fun black() {
        currentColour = Color.BLACK
    }
    fun blue() {
        currentColour = Color.BLUE
    }
    fun green() {
        currentColour = Color.GREEN
    }
    fun white() {
        currentColour = Color.WHITE
    }

    //Background Colours:
    fun bgRed() {
        backgroundColour = Color.RED
        currentColour = backgroundColour

    }
    fun bgBlack() {
        backgroundColour = Color.BLACK
        currentColour = backgroundColour

    }
    fun bgBlue() {
        backgroundColour = Color.BLUE
        currentColour = backgroundColour

    }
    fun bgGreen() {
        backgroundColour = Color.GREEN
        currentColour = backgroundColour

    }
    fun bgWhite() {
        backgroundColour = Color.WHITE
        currentColour = backgroundColour

    }

    //Brush Sizes

    fun bsSmall(){
        strokeWidth = 5

    }
    fun bsMedium(){
        strokeWidth = 20
    }
    fun bsLarge(){
        strokeWidth = 40

    }
    fun bsChunky(){
        strokeWidth = 80


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

        setupSnapShot()
        listenToDocumentLocal()
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
//  PMD 03/08/19
                Log.d("PaintView-ontouch-X", "${event.getX()}")
                Log.d("PaintView-ontouch-Y", "${event.getY()}")
                Log.d("PaintView-ontouch-event", "${event}")
                writeToFirestore(event)

                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                touching(x, y)
                writeToFirestore(event)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                notTouching()
                invalidate()
            }
        }

        return true
    }

    private fun writeToFirestore(event: MotionEvent) {

//        val canvasRef = dbFirestore.collection(mCanvas.toString())

        Log.d("PaintView-DBWrite", "X: ${event.getX(0)} Y: ${event.getY(0)}")

        val pathdetails = hashMapOf<String, Any>(
            "colour" to currentColour,
            "X" to event.getX(0),
            "Y" to event.getY(0)
        )

        val canvasRef = dbFirestore.collection(mCanvas.toString())
        canvasRef.document(mPath.toString())
            .collection(mPath.toString())
            .add(pathdetails)
            .addOnSuccessListener { documentReference ->
                Log.d("PaintView - onSuccess", "Database Updated: ${mPath.toString()} ${documentReference}")
            }
            .addOnFailureListener { e ->
                Log.d("PaintView", "Error adding to database: ", e)
            }
    }

    private fun setupSnapShot() {

        val snapshotRef = dbFirestore.collection(mCanvas.toString()).document(mPath.toString())
        snapshotRef.addSnapshotListener{snapshot, e ->
            if(e != null) {
                Log.d("PaintActivity", "Listen Failed.", e)
                return@addSnapshotListener
            }

            if(snapshot != null && !snapshot.exists()) {
                Log.d("PaintActivity", "Current data: ${snapshot.data}")

            } else {
                Log.d("PaintActivity","Current data: null")
            }
        }
    }

    private fun listenToDocumentLocal() {
        // [START listen_document_local]
        val snapshotRef = dbFirestore.collection(mCanvas.toString()).document(mPath.toString())
        snapshotRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.d("PaintActivity", "Listen failed.", e)
                return@addSnapshotListener
            }

            val source = if (snapshot != null && snapshot.metadata.hasPendingWrites())
                "Local"
            else
                "Server"

            if (snapshot != null && snapshot.exists()) {
                Log.d("PaintActivity", "$source data: ${snapshot.data}")
            } else {
                Log.d("PaintActivity", "$source data: null")
            }
        }
        // [END listen_document_local]
    }


    companion object {

        const val BRUSH_SIZE = 5
        const val DEFAULT_COLOUR = Color.BLACK
        const val DEFAULT_BG_COLOUR = Color.WHITE
        private const val TOUCH_TOLERANCE = 4f
    }
}
