package com.example.collabdrawingfe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_onboarding__two.*

class OnboardingActivity_Two : AppCompatActivity() {

    val onboardingTwo = "https://firebasestorage.googleapis.com/v0/b/toucandoodle.appspot.com/o/onboarding_images%2Fonboarding_02.png?alt=media&token=4bcfca71-dd32-4262-be09-1f9ebd8148ac"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding__two)

        Picasso.get().load(onboardingTwo).into(findViewById<ImageView>(R.id.onboarding_imageview))

        button_skip.setOnClickListener {
            val intentActivity = Intent(this, GalleryActivity::class.java)
            startActivity(intentActivity)
        }

        button_next.setOnClickListener {
            val intentActivity = Intent(this, OnboardingActivity_Three::class.java)
            startActivity(intentActivity)
        }
    }
}
