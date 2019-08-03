package com.example.collabdrawingfe

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.text.method.Touch
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

import java.util.ArrayList
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream


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
    private val mStorage: FirebaseStorage = FirebaseStorage.getInstance()

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
//        Log.d("PaintView-mBitmap-init", mBitmap.toString())


//         val pathdata = HashMap<String,ArrayList<out Any>>()
//
//        instructionsRef.document(mCanvas.toString()).set(pathdata)
//            .addOnSuccessListener { documentReference ->
//                // Toast.makeText(this, "path added to database", Toast.LENGTH_SHORT).show()
//                Log.d("PaintView - onSuccess", paths.toString())
//            }
//            .addOnFailureListener { e ->
//                Log.d("PaintView", "Error adding to database: ", e)
//            }


        currentColour = DEFAULT_COLOUR
        strokeWidth = BRUSH_SIZE

 //       writeToFile(mBitmap)
    }

//    private fun bitmapToDrawable(bitmap:Bitmap):BitmapDrawable{
//        return BitmapDrawable(resources,bitmap)
//    }

//    private fun writeToFile(bitmapFile: Bitmap) {
//
//        var storageRef = mStorage.reference
//        var imagesRef: StorageReference? = storageRef.child("images")
//
//        val boas = ByteArrayOutputStream()
//        val drawableBitmap = bitmapToDrawable(bitmapFile)
//        val bitmapToWrite = drawableBitmap.bitmap
//
//        // TODO
//        //bitmapToWrite.compress(Bitmap.CompressFormat.JPEG, 100, boas)
//
//        val f: File = File(Environment.getExternalStorageDirectory(), "${mCanvas}.png")
//        f.createNewFile()
//        val bos: ByteArrayOutputStream = ByteArrayOutputStream();
//        bitmapFile.compress?(Bitmap.CompressFormat.PNG, 0, bos)
//
//        //write the bytes in file
//        val fos: FileOutputStream = FileOutputStream(f)
//        fos.write(bos.toByteArray());
//        fos.flush();
//        fos.close();
//
//        return f.absolutePath
//    }

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

//  PMD 03/08/19
    lateinit var XYPoints: Array<FloatArray>

    private fun inputStart(x: Float, y: Float) {

        mPath = Path()
        val ip = InputPath(currentColour, strokeWidth, mPath!!)
        paths.add(ip)

        mPath!!.reset()
        mPath!!.moveTo(x, y)
        mX = x
        mY = y

//  PMD 03/08/19
        Log.d("PaintView - X", "${mX}")
        Log.d("PaintView - Y", "${mY}")
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
//  PMD 03/08/19
                Log.d("PaintView-ontouch-event", "${event}")
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

    private val canvasRef = dbFirestore.collection("canvasdetails")

    private fun writeToFirestore(event: MotionEvent) {

        Log.d("PaintView-DBWrite", "PointerId: ${event.getPointerId(0)} X: ${event.getX(0)} Y: ${event.getY(0)}")

        val pathdetails = hashMapOf<String, Any>(
            "pointerId" to event.getPointerId(0),
            "X" to event.getX(0),
            "Y" to event.getY(0)
        )

        canvasRef.document(mCanvas.toString()).collection(mPath.toString()).add(pathdetails)
            .addOnSuccessListener { documentReference ->
                Log.d("PaintView - onSuccess", "Database Updated: ${documentReference}")
            }
            .addOnFailureListener { e ->
                Log.d("PaintView", "Error adding to database: ", e)
            }

    }

    companion object {

        const val BRUSH_SIZE = 20
        const val DEFAULT_COLOUR = Color.BLACK
        const val DEFAULT_BG_COLOUR = Color.WHITE
        private const val TOUCH_TOLERANCE = 4f
    }
}
