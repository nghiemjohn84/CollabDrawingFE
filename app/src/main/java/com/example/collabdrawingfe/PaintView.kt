package com.example.collabdrawingfe

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.justinnguyenme.base64image.Base64Image
import java.io.ByteArrayOutputStream
import java.io.OutputStream
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

//    private val dbFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
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

 //   private val instructionsRef = dbFirestore.collection("instructions")


    fun init(metrics: DisplayMetrics, doodle: String) {
        val height = metrics.heightPixels
        val width = metrics.widthPixels

        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        mCanvas = Canvas(mBitmap!!)

        Log.d("PaintView-mCanvas-init", mCanvas.toString())

        currentColour = DEFAULT_COLOUR
        strokeWidth = BRUSH_SIZE

        drawnInstruction = dbFirebase.getReference(doodle).child("drawingInstruction")

        drawInstruction.x = 0F
        drawInstruction.y = 0F
        drawInstruction.command = "initialise"
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
                        invalidate()
                        drawInstruction.command = "init"
                        drawnInstruction!!.setValue(drawInstruction)
                    }
                }
            }
        })
//        writeToFirestore()
//        createSnapshot()
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

//        setupSnapShot()
//        listenToDocumentLocal()
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
                drawInstruction.command = "screen-touched"
                drawnInstruction!!.setValue(drawInstruction)

                inputStart(x, y)

////  PMD 03/08/19
//                Log.d("PaintView-ontouch-X", "${event.getX()}")
//                Log.d("PaintView-ontouch-Y", "${event.getY()}")
//                Log.d("PaintView-ontouch-event", "${event}")
//                updateSnapshot()
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                drawInstruction.command = "finger-moving"
                drawnInstruction!!.setValue(drawInstruction)

                touching(x, y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                drawInstruction.command = "finger-removed"
                drawnInstruction!!.setValue(drawInstruction)
                notTouching()
//                bitmapToString(mBitmap!!)
//                writeToFirestore()
                invalidate()
                drawInstruction.command = "initialise"
                drawnInstruction!!.setValue(drawInstruction)

            }
        }

        return true
    }

//    private var bitmapBase64: String = ""

//    private fun bitmapToString(bitmap: Bitmap):String {
//
//        Base64Image.instance.encode(bitmap) { base64 ->
////            bitmapBase64 = base64!! }
//            base64?.let {
//               bitmapBase64 = base64          }
////                Log.d(TAG, "${base64}")
//        }
//        return bitmapBase64
//    }
//
//    private fun writeToFirestore() {
//
//        val pathdetails = hashMapOf<String, Any>(
//            "bitmap" to bitmapBase64
//        )
//
//
//        val canvasRef = dbFirestore.collection("canvii").document(mCanvas.toString())
//        canvasRef
////        canvasRef.collection(mPath.toString())
////            .add(pathdetails)
//            .set(pathdetails)
//            .addOnSuccessListener { documentReference ->
//                Log.d("PaintView - onSuccess", "Database Updated: ${mPath.toString()} ${documentReference}")
//            }
//            .addOnFailureListener { e ->
//                Log.d("PaintView", "Error adding to database: ", e)
//            }


//        val canvasRef = dbFirestore.collection(mCanvas.toString())
//        canvasRef.document(mPath.toString())
//            .collection(mPath.toString())
//            .add(pathdetails)
//            .addOnSuccessListener { documentReference ->
//                Log.d("PaintView - onSuccess", "Database Updated: ${mPath.toString()} ${documentReference}")
//            }
//            .addOnFailureListener { e ->
//                Log.d("PaintView", "Error adding to database: ", e)
//            }
//    }



    companion object {

        const val BRUSH_SIZE = 5
        const val DEFAULT_COLOUR = Color.BLACK
        const val DEFAULT_BG_COLOUR = Color.WHITE
        private const val TOUCH_TOLERANCE = 4f
        const val TAG = "PaintActivity"
    }
}
