package com.example.collabdrawingfe

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.activity_gallery.*
import kotlinx.android.synthetic.main.content_gallery.*

class GalleryActivity : AppCompatActivity() {

    lateinit var imageRef : DatabaseReference

    val drawings = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        setSupportActionBar(toolbar)
        fetchAllImages()

        newCanvas_button_gallery.setOnClickListener { view ->
            val intentActivity = Intent(this, chooseDoodleActivity::class.java)
            startActivity(intentActivity)
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    private fun fetchAllImages() {
        imageRef = FirebaseDatabase.getInstance().getReference("testImages")
        imageRef.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0!!.exists()){
                    for (drawing in p0.children) {
                        drawings.add("${drawing.value}")
                    }
                    image_list_gallery.layoutManager = LinearLayoutManager(applicationContext)
                    image_list_gallery.adapter = GalleryRecyclerAdapter(applicationContext, drawings)
                    Log.d("images", "$drawings")

                }
            }
        })


//        val userRef =  dbFirestore.collection("testImages")
//        userRef.get()
//            .addOnSuccessListener { result ->
//                for (document in result) {
//                    var drawingURL = document.get("url")
//                    drawings.add("$drawingURL")
//                    image_list_gallery.layoutManager = LinearLayoutManager(this)
//                    image_list_gallery.adapter = GalleryRecyclerAdapter(this, drawings)
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.d("gallery", "Error getting drawing URLs", exception)
//            }
    }

}
