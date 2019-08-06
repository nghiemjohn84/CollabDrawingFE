package com.example.collabdrawingfe

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.activity_gallery.*
import kotlinx.android.synthetic.main.content_gallery.*

class GalleryActivity : AppCompatActivity() {

    private val dbFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    val drawings = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        setSupportActionBar(toolbar)
        fetchAllImages()
//        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/kotlinstorage-31352.appspot.com/o/images%2Fbart.jpg?alt=media&token=9fe1f1e0-4bae-444f-a2f7-55dc33487553").into(tempImage)

        newCanvas_button_gallery.setOnClickListener { view ->
            val intentActivity = Intent(this, chooseDoodleActivity::class.java)
            startActivity(intentActivity)
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    private fun fetchAllImages() {
        val userRef =  dbFirestore.collection("testImages")
        userRef.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    var drawingURL = document.get("url")
                    drawings.add("$drawingURL")
                    image_list_gallery.layoutManager = LinearLayoutManager(this)
                    image_list_gallery.adapter = GalleryRecyclerAdapter(this, drawings)
                }
            }
            .addOnFailureListener { exception ->
                Log.d("gallery", "Error getting drawing URLs", exception)
            }
    }

}
