package com.example.collabdrawingfe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_onboarding.*

class OnboardingActivity_One : AppCompatActivity() {

    val onboardingOne = "https://firebasestorage.googleapis.com/v0/b/toucandoodle.appspot.com/o/onboarding_images%2Fonboarding_01.png?alt=media&token=fd8b09b0-068b-4556-8636-915352b31296"




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)


        Picasso.get().load(onboardingOne).into(findViewById<ImageView>(R.id.onboarding_imageview))

        button_skip.setOnClickListener {
            val intentActivity = Intent(this, GalleryActivity::class.java)
            startActivity(intentActivity)
        }

        button_next.setOnClickListener {
            val intentActivity = Intent(this, OnboardingActivity_Two::class.java)
            startActivity(intentActivity)
        }

    }
}
