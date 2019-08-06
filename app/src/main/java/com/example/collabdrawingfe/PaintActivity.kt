package com.example.collabdrawingfe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class PaintActivity : AppCompatActivity() {

    companion object {
        const val DOODLE_NAME = "DOODLE_NAME"
    }

    private var paintView: PaintView? = null

    // Database connection details
    private val dbFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var doodlesCollection = dbFirestore.collection("doodles")
    private var user: FirebaseUser? = null
    // private var activeUsers = dbFirestore.collection("doodles")
    private var key = dbFirestore.collection("doodles")
    var activeUsers = ArrayList<Map<Any, String>>()
    var fields = ArrayList<String>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        paintView = findViewById(R.id.paintView) as PaintView

        // Doodling code 06/08/19
        user = FirebaseAuth.getInstance().currentUser
        val doodleRef = doodlesCollection.document(intent.getStringExtra(DOODLE_NAME))
        doodleRef.get()
            .addOnCompleteListener(OnCompleteListener<DocumentSnapshot> {
                if(it.isSuccessful) {
                    val list = ArrayList<String>()
                    val map = it.result!!.data
                    for((key) in map!!) {
                        list.add(key)
                        Log.d("PaintActivity", key)
                    }
                    Log.d("PaintActivity", "${map.entries}")
                    for(entry in map.entries) {
                        Log.d("PaintActivity", "${entry.value}")
                        var entrydetails = entry.value
                        Log.d("PaintActivity", "Here")
                    }
                }
            })




        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        paintView!!.init(metrics, intent.getStringExtra(DOODLE_NAME))
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
            R.id.white ->{
                paintView!!.white()
                return true
            }
            R.id.bgRed ->{
                paintView!!.bgRed()
                return true
            }
            R.id.bgBlack ->{
                paintView!!.bgBlack()
                return true
            }
            R.id.bgBlue ->{
                paintView!!.bgBlue()
                return true
            }
            R.id.bgGreen ->{
                paintView!!.bgGreen()
                return true
            }
            R.id.bgWhite ->{
                paintView!!.bgWhite()
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