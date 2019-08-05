package com.example.collabdrawingfe

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.google.android.gms.tasks.OnCompleteListener
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

        writeToFirestore()
        createSnapshot()
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

        when (event.action) {

            MotionEvent.ACTION_DOWN -> {
                inputStart(x, y)

//  PMD 03/08/19
                Log.d("PaintView-ontouch-X", "${event.getX()}")
                Log.d("PaintView-ontouch-Y", "${event.getY()}")
                Log.d("PaintView-ontouch-event", "${event}")
                updateSnapshot()
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                touching(x, y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                notTouching()
                bitmapToString(mBitmap!!)
                writeToFirestore()
                invalidate()
            }
        }

        return true
    }

    private var bitmapBase64: String = ""

    private fun bitmapToString(bitmap: Bitmap):String {

        Base64Image.instance.encode(bitmap) { base64 ->
//            bitmapBase64 = base64!! }
            base64?.let {
               bitmapBase64 = base64          }
//                Log.d(TAG, "${base64}")
        }
        return bitmapBase64
    }

    private fun writeToFirestore() {

        val pathdetails = hashMapOf<String, Any>(
            "bitmap" to bitmapBase64
        )


        val canvasRef = dbFirestore.collection("canvii").document(mCanvas.toString())
        canvasRef
//        canvasRef.collection(mPath.toString())
//            .add(pathdetails)
            .set(pathdetails)
            .addOnSuccessListener { documentReference ->
                Log.d("PaintView - onSuccess", "Database Updated: ${mPath.toString()} ${documentReference}")
            }
            .addOnFailureListener { e ->
                Log.d("PaintView", "Error adding to database: ", e)
            }


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
    }


    private fun createSnapshot() {
        Log.d(TAG,"Entered create snapshot")
        var canvasCollection = dbFirestore.collection(mCanvas.toString())
            .addSnapshotListener(EventListener<QuerySnapshot> {documentSnapshots, err ->
                if(err != null) {
                    Log.e(TAG, "Listen Failed", err)
                    return@EventListener
                } else {
                    Log.d(TAG + "- snapshot", "Snapshot Created")
                }

                val pathList = mutableListOf<CanvasPath>()

                Log.d(TAG, "${documentSnapshots}")

                if(documentSnapshots!!.isEmpty()) {
                    Log.d(TAG, "${documentSnapshots}")
//                    val canvas_path = doc.toObject(CanvasPath::class.java)
//                    canvas_path.id = doc.id
//                    canvas_path.name = mPath.toString()
//                    pathList.add(canvas_path)
                }
                for(doc in documentSnapshots!!) {
                    val canvas_path = doc.toObject(CanvasPath::class.java)
                    canvas_path.id = doc.id
                    canvas_path.name = mPath.toString()
                    pathList.add(canvas_path)
                }
            })
    }

    private fun updateSnapshot() {
        var doc_collection = dbFirestore.collection(mCanvas.toString())
            .get()
            .addOnCompleteListener (OnCompleteListener<QuerySnapshot> {task ->
                if(task.isSuccessful) {
                    for (document in task.result!!) {
                        Log.d(TAG, document.id + " => " + document.data)
                    }
                } else {
                    Log.d(TAG, "Error getting documents", task.exception)
                }

            })
    }


//    private fun setupSnapShot() {
//
//        val snapshotRef = dbFirestore.collection(mCanvas.toString()).document(mPath.toString())
//        snapshotRef.addSnapshotListener{snapshot, e ->
//            if(e != null) {
//                Log.d("PaintActivity", "Listen Failed.", e)
//                return@addSnapshotListener
//            }
//
//            if(snapshot != null && !snapshot.exists()) {
//                Log.d("PaintActivity", "Current data: ${snapshot.data}")
//
//            } else {
//                Log.d("PaintActivity","Current data: null")
//            }
//        }
//    }
//
//    private fun listenToDocumentLocal() {
//        // [START listen_document_local]
//        val snapshotRef = dbFirestore.collection(mCanvas.toString()).document(mPath.toString())
//        snapshotRef.addSnapshotListener { snapshot, e ->
//            if (e != null) {
//                Log.d("PaintActivity", "Listen failed.", e)
//                return@addSnapshotListener
//            }
//
//            val source = if (snapshot != null && snapshot.metadata.hasPendingWrites())
//                "Local"
//            else
//                "Server"
//
//            if (snapshot != null && snapshot.exists()) {
//                Log.d("PaintActivity", "$source data: ${snapshot.data}")
//            } else {
//                Log.d("PaintActivity", "$source data: null")
//            }
//        }
//        // [END listen_document_local]
//    }


    companion object {

        const val BRUSH_SIZE = 5
        const val DEFAULT_COLOUR = Color.BLACK
        const val DEFAULT_BG_COLOUR = Color.WHITE
        private const val TOUCH_TOLERANCE = 4f
        const val TAG = "PaintActivity"
    }
}
