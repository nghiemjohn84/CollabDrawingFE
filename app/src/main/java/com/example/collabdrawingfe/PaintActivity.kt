package com.example.collabdrawingfe

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.net.Uri
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import kotlin.random.Random
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URI
import java.util.*

class PaintActivity : AppCompatActivity() {

    private var filePath: Uri? = null
    private var fileNameForUpload: String? = null

    internal var storage : FirebaseStorage?= null
    internal var storageReference:StorageReference?= null

    companion object {
        const val DOODLE_NAME = "DOODLE_NAME"
    }

        fun saveScreenshot(bitmap: Bitmap) {
            val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val myDir = File("$path/doodles")
            if(!myDir.exists()) {
                myDir.mkdirs()
            }

            val n = Random.nextInt(10000)
            val fileName = "image$n.jpg"
            fileNameForUpload = "image$n"
            val file = File(myDir, fileName)
            filePath = Uri.fromFile(file)
            if(file.exists())
                file.delete()
            try {
                val out = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out.flush()
                out.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }


        fun loadScreenshot(v: View, width: Int, height: Int): Bitmap {

            val bit = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bit)
            v.layout(0, 0, v.layoutParams.width, v.layoutParams.height)
            v.draw(canvas)
            return bit
        }


    private var paintView: PaintView? = null


    private val dbFirebase = FirebaseDatabase.getInstance()
    private lateinit var doodle: DatabaseReference
    private var user: FirebaseUser? = null
    private lateinit var activeUsers: DatabaseReference
    private lateinit var key: DatabaseReference


    var width: Int = 700
    var height: Int = 1000

    override fun onCreate(savedInstanceState: Bundle?) {

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        width = displayMetrics.widthPixels
        height = displayMetrics.heightPixels

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference

        doodle = dbFirebase.getReference(intent.getStringExtra(DOODLE_NAME))
        user = FirebaseAuth.getInstance().currentUser
        activeUsers = doodle.child("activeUsers")
        key = activeUsers.child(user!!.uid)
        key.setValue(user!!.displayName)

        paintView = findViewById(R.id.paintView) as PaintView


        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        paintView!!.init(metrics, intent.getStringExtra(DOODLE_NAME))

        activeUsers.addValueEventListener(newUserEventListener)
    }

    private val newUserEventListener = object : ValueEventListener {
        override fun onCancelled(error: DatabaseError) {
            Log.w("error", "Failed to read value.", error.toException())
        }

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val td = dataSnapshot.value as HashMap<String, String>


            val listOfActiveUsers = ArrayList(td.values)

            activeUsersListTV.text = ""

            for (user: String in listOfActiveUsers) {
                activeUsersListTV.append("$user\n")
            }

        }
    }

    private fun uploadFile() {
        if(filePath != null) {
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("uploading...")
            progressDialog.show()

            val imageRef = storageReference!!.child("gallery/" + UUID.randomUUID().toString())
            var uploadTask = imageRef.putFile(filePath!!)
                    uploadTask
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    val url = imageRef.downloadUrl
                  
                    Log.d("screenshot", "$url")
                    Toast.makeText(applicationContext, "Image uploaded successfully!", Toast.LENGTH_SHORT).show()

                }
                .addOnFailureListener {
                    progressDialog.dismiss()
                    Toast.makeText(applicationContext, "Upload failed", Toast.LENGTH_SHORT).show()
                }
                .addOnProgressListener { taskSnapshot ->
                    val progress = 100.0 * taskSnapshot.bytesTransferred/taskSnapshot.totalByteCount
                    progressDialog.setMessage("Uploading image " + progress.toInt() + "%...")
                }
            val urlTask = uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> {task ->
                if(!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation imageRef.downloadUrl
            }).addOnCompleteListener{ task ->
                if(task.isSuccessful) {
                    val downloadUri = task.result

                    val galleryUriRef = FirebaseDatabase.getInstance().getReference("galleryURLs")
                    val urlId = galleryUriRef.push().key

                    galleryUriRef.child("$urlId").setValue(downloadUri.toString())



                } else {
                    Toast.makeText(applicationContext, "URL failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        activeUsers.removeEventListener(newUserEventListener)
        key.removeValue()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.canvas, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.clear -> {
                paintView!!.clearCanvas()
                return true
            }
            R.id.eraser -> {
                paintView!!.changeColour("white")
                paintView!!.changeStrokeWidth(40)
                return true
            }
            R.id.brush -> {
                paintView!!.changeColour("black")
                paintView!!.changeStrokeWidth(5)
                return true
            }
            R.id.red ->{
                paintView!!.changeColour("red")
                return true
            }
            R.id.black ->{
                paintView!!.changeColour("black")
                return true
            }
            R.id.blue ->{
                paintView!!.changeColour("blue")
                return true
            }
            R.id.green ->{
                paintView!!.changeColour("green")
                return true
            }

            R.id.bsSmall ->{
                paintView!!.changeStrokeWidth(10)
                return true
            }
            R.id.bsMedium ->{
                paintView!!.changeStrokeWidth(20)
                return true
            }
            R.id.bsLarge ->{
                paintView!!.changeStrokeWidth(40)
                return true
            }
            R.id.bsChunky ->{
                paintView!!.changeStrokeWidth(80)
                return true
            }

            R.id.gallery -> {
                val activityIntent = Intent(this, GalleryActivity::class.java)
                startActivity(activityIntent)

            }
            R.id.save -> {
                val bitmap = loadScreenshot(findViewById(R.id.paintView), width, height)
                saveScreenshot(bitmap!!)
                Handler().postDelayed({
                    uploadFile()
                }, 1000)
                Handler().postDelayed({
                    val activityIntent = Intent(this, GalleryActivity::class.java)
                    startActivity(activityIntent)
                }, 2000)

            }
            }

        return super.onOptionsItemSelected(item)
    }
}