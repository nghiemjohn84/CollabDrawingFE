package com.example.collabdrawingfe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_choose_doodle.*

class chooseDoodleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_doodle)

        joinDoodle_button.setOnClickListener{
            val intent = Intent(this, PaintActivity::class.java)
            intent.putExtra("DOODLE_NAME", doodleName_editText.text.toString())
            startActivity(intent)
        }
    }
}
