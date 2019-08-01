package com.example.collabdrawingfe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_button_login.setOnClickListener {
            val email = email_editText_login.text.toString()
            val password = password_editText_login.text.toString()

//            Log.d("LoginActivity", "username is: $email")
//            Log.d("LoginActivity", "password is: $password")

            signIn(email, password)


        }


    }

    private fun signIn(email: String, password: String) {

        Log.d("LoginActivity", "signIn ran")
        Log.d("LoginActivity", "email is: $email")
        Log.d("LoginActivity", "password is: $password")
        Log.d("LoginActivity", mAuth.toString())

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                when{
                    it.isSuccessful -> {
                        Log.d("LoginActivity", "Login successful")
                        Toast.makeText(this, "User logged in successfully", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, PaintActivity::class.java)
                        startActivity(intent)

                    }
                    else -> {
                        Log.d("LoginActivity", "Login unsuccessful boyyyyyy :(")
                        Toast.makeText(this, "Login failed boyyyyyyyyyyy! :(", Toast.LENGTH_LONG).show()

                        return@addOnCompleteListener
                    }
                }


        }
    }
}
