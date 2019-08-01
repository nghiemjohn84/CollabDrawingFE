package com.example.collabdrawingfe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_registration.*

class RegistrationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)



        register_button_registration.setOnClickListener {
            val userName = username_editText_registration.text.toString()
            val email = email_editText_registration.text.toString()
            val password = password_editText_registration.text.toString()


            Log.d("RegistrationActivity", "Email is: $email")
            Log.d("RegistrationActivity", "Username is: $userName")
            Log.d("RegistrationActivity", "Password is: $password")
        }

        already_have_an_account_text_view.setOnClickListener {
            Log.d("RegistrationActivity", "Try to show Log in Activity")

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
