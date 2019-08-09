package com.example.collabdrawingfe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_full_screen_image.*

class FullScreenImageActivity : AppCompatActivity() {



    companion object {
        const val IMAGE_URL = "IMAGE_URL"
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_image)
//        Log.d("screenshot", "${intent.getStringExtra(IMAGE_URL)}")
        Picasso.get().load(intent.getStringExtra(IMAGE_URL)).into(findViewById<ImageView>(R.id.fullScreen_imageView))
    }
}
