package com.example.collabdrawingfe

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ShareActionProvider
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import kotlin.random.Random
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class PaintActivity : AppCompatActivity() {

    private var filePath: Uri? = null

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
            Log.d("screenshot", "$path")
            Log.d("screenshot", "$myDir")


            val n = Random.nextInt(10000)
            val fileName = "image$n.jpg"
            val file = File(myDir, fileName)
            filePath = Uri.fromFile(file)
            Log.d("screenshot", "$filePath")
            Log.d("screenshot", "$file")
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
            Log.d("screenshot", "file created")
            return bit
        }
    //}

    private var paintView: PaintView? = null

    // Database connection details
    private val dbFirebase = FirebaseDatabase.getInstance()
    private lateinit var doodle: DatabaseReference
    private var user: FirebaseUser? = null
    private lateinit var activeUsers: DatabaseReference
    private lateinit var key: DatabaseReference

    // private val dbFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    // private var doodlesCollection = dbFirestore.collection("doodles")
    // private var user: FirebaseUser? = null
    // private var activeUsers = dbFirestore.collection("doodles")
    // private var key = dbFirestore.collection("doodles")
    // var activeUsers = ArrayList<Map<Any, String>>()
    // var fields = ArrayList<String>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference

        // Doodling code 06/08/19
        doodle = dbFirebase.getReference(intent.getStringExtra(DOODLE_NAME))
        user = FirebaseAuth.getInstance().currentUser
        activeUsers = doodle.child("activeUsers")
        key = activeUsers.child(user!!.uid)
        key.setValue(user!!.displayName)

        paintView = findViewById(R.id.paintView) as PaintView


//        val doodleRef = doodlesCollection.document(intent.getStringExtra(DOODLE_NAME))
//        doodleRef.get()
//            .addOnCompleteListener(OnCompleteListener<DocumentSnapshot> {
//                if(it.isSuccessful) {
//                    val list = ArrayList<String>()
//                    val map = it.result!!.data
//                    for((key) in map!!) {
//                        list.add(key)
//                        Log.d("PaintActivity", key)
//                    }
//                    Log.d("PaintActivity", "${map.entries}")
//                    for(entry in map.entries) {
//                        Log.d("PaintActivity", "${entry.value}")
//                        var entrydetails = entry.value
//                        Log.d("PaintActivity", "Here")
//                    }
//                }
//            })




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
            imageRef.putFile(filePath!!)
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    Toast.makeText(applicationContext, "file uploaded", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    progressDialog.dismiss()
                    Toast.makeText(applicationContext, "file failed", Toast.LENGTH_SHORT).show()
                }
                .addOnProgressListener { taskSnapshot ->
                    val progress = 100.0 * taskSnapshot.bytesTransferred/taskSnapshot.totalByteCount
                    progressDialog.setMessage("uploaded" + progress.toInt() + "%...")
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
                paintView!!.clear()
                return true
            }
            R.id.eraser -> {
                paintView!!.changeColour("white")
                return true
            }
            R.id.brush -> {
                paintView!!.brush()
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
                paintView!!.bsSmall()
                return true
            }
            R.id.bsMedium ->{
                paintView!!.bsMedium()
                return true
            }
            R.id.bsLarge ->{
                paintView!!.bsLarge()
                return true
            }
            R.id.bsChunky ->{
                paintView!!.bsChunky()
                return true
            }
            R.id.invite -> {
                val activityIntent = Intent(this, InviteCollaboratorActivity::class.java)
                startActivity(activityIntent)
            }

            R.id.gallery -> {
                val activityIntent = Intent(this, GalleryActivity::class.java)
                startActivity(activityIntent)

            }
            R.id.save -> {
                val bitmap = loadScreenshot(findViewById(R.id.paintView), 750, 1000)
                saveScreenshot(bitmap!!)
                Handler().postDelayed({
                    uploadFile()
                }, 1000)

//
            }
            }

        return super.onOptionsItemSelected(item)
    }
}