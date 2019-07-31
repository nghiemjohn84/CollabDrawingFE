package com.example.collabdrawingfe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_button_login.setOnClickListener {
            val userName = username_editText_login.text.toString()
            val password = password_editText_login.text.toString()

            Log.d("LoginActivity", "username is: $userName")
            Log.d("LoginActivity", "password is: $password")
        }
    }
}
