package com.example.collabdrawingfe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_onboarding__three.*

class OnboardingActivity_Three : AppCompatActivity() {

    val onboardingThree = "https://firebasestorage.googleapis.com/v0/b/toucandoodle.appspot.com/o/onboarding_images%2Fonboarding_03.png?alt=media&token=3ffd1ff1-7694-43b7-86bc-bd59c3929ce9"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding__three)

        Picasso.get().load(onboardingThree).into(findViewById<ImageView>(R.id.onboarding_imageview))

        button_skip.setOnClickListener {
            val intentActivity = Intent(this, GalleryActivity::class.java)
            startActivity(intentActivity)
        }

        button_next.setOnClickListener {
            val intentActivity = Intent(this, GalleryActivity::class.java)
            startActivity(intentActivity)
        }
    }
}
