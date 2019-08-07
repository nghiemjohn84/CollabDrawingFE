package com.example.collabdrawingfe

import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.util.*

class PaintActivity : AppCompatActivity() {

    companion object {
        const val DOODLE_NAME = "DOODLE_NAME"
    }

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

    override fun onDestroy() {
        super.onDestroy()
        activeUsers.removeEventListener(newUserEventListener)
        key.removeValue()
    }

//    fun takeScreenShot(view: PaintView): Bitmap? {
//        val snapshot = Bitmap.createBitmap(view.getBitmap())
//        return snapshot
//    }
//
//    private fun save(finalBitmap: Bitmap) {
//        val root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
//        val myDir = File("$root/CollaborativeDoodling")
//
//        if (!myDir.exists()) {
//            myDir.mkdirs()
//        }
//
//        val generator = Random()
//        var n = 10000
//        n = generator.nextInt(n)
//        val iname = "doodle$n.jpg"
//        val file = File(myDir, iname)
//        if (file.exists())
//            file.delete()
//        try {
//            val out = FileOutputStream(file)
//            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
//            out.flush()
//            out.close()
//            Toast.makeText(
//                applicationContext,
//                "Saved Sucessfully",
//                Toast.LENGTH_LONG
//            ).show()
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//        MediaScannerConnection.scanFile(this, arrayOf(file.toString()), null,
//            object : MediaScannerConnection.OnScanCompletedListener {
//                override fun onScanCompleted(path: String, uri: Uri) {
//                    Log.i("ExternalStorage", "Scanned $path:")
//                    Log.i("ExternalStorage", "-> uri=$uri")
//                }
//            })
//
//    }



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
                paintView!!.eraser()
                return true
            }
            R.id.brush -> {
                paintView!!.brush()
                return true
            }
            R.id.red ->{
                paintView!!.red()
                return true
            }
            R.id.black ->{
                paintView!!.black()
                return true
            }
            R.id.blue ->{
                paintView!!.blue()
                return true
            }
            R.id.green ->{
                paintView!!.green()
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
                val activityIntent = Intent(this, GalleryActivity::class.java)
                //call itemtobesaved method here?
                startActivity(activityIntent)

            }

        }

        return super.onOptionsItemSelected(item)
    }
}