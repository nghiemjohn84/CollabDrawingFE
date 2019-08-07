package com.example.collabdrawingfe

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

import kotlinx.android.synthetic.main.activity_registration.*



class RegistrationActivity : AppCompatActivity() {

//    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
//    private val dbFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)



        register_button_registration.setOnClickListener {
            registerUser()
        }

        already_have_an_account_text_view.setOnClickListener {
            Log.d("RegistrationActivity", "Try to show Log in Activity")


            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        select_photo_button_registration.setOnClickListener {
            Log.d("RegistrationActivity", "select photo button clicked")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    var selectedPhotoURI: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            //check what the image was....
            Log.d("RegistrationActivity", "Photo was selected")

            selectedPhotoURI = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoURI)
            val bitmapDrawable = BitmapDrawable(bitmap)
            select_photo_button_registration.setBackgroundDrawable(bitmapDrawable)
        }
    }

    private fun registerUser() {
        val userName = username_editText_registration.text.toString()
        val email = email_editText_registration.text.toString()
        val password = password_editText_registration.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter text in email/pw", Toast.LENGTH_SHORT).show()
            return
        }
        Log.d("RegistrationActivity", "Username is: $userName")
        Log.d("RegistrationActivity", "Email is: $email")
        Log.d("RegistrationActivity", "Password is: $password")

//        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
//            if (task.isSuccessful) {
//                Log.d("RegisterUser", "Successfully created user with uid ${task.result?.user?.uid}")
////                    insertUser(email, userName, it.result?.user?.uid!!)
//                var userId = mAuth!!.currentUser!!.uid
//                task.result?.user?.displayName
////                    clearInputs()
//            } else {
//                Log.e("RegisterUser", "Failed")
//
//                Toast.makeText(this, "User Registration has failed", Toast.LENGTH_LONG).show()
//            }
//        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this)
        { task ->
            if(task.isSuccessful) {
                mAuth.currentUser?.updateProfile(UserProfileChangeRequest
                    .Builder()
                    .setDisplayName(userName)
                    .build())
            }
            var userId = mAuth!!.currentUser!!.uid
            var userName = mAuth!!.currentUser!!.displayName
        }

        signIn(email, password)
    }
//        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
//            when {
//                it.isSuccessful -> {
//                    Toast.makeText(this, "User Registered", Toast.LENGTH_LONG).show()
//                    Log.d("RegisterUser", "Successfully created user with uid ${it.result?.user?.uid}")
////                    insertUser(email, userName, it.result?.user?.uid!!)
//                    clearInputs()
//                }
//                else -> {
//                    Toast.makeText(this, "User Registration has failed", Toast.LENGTH_LONG).show()
//                    return@addOnCompleteListener
//                }
//            }
//
//        }




    private fun signIn(email: String, password: String) {

        Log.d("LoginActivity", "signIn ran")
        Log.d("LoginActivity", "email is: $email")
        Log.d("LoginActivity", "password is: $password")

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if(task.isSuccessful) {
                    Log.d("LoginActivity", "Login successful")
                    Toast.makeText(this, "User logged in successfully", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, PaintActivity::class.java)
                    startActivity(intent)
                }
                else {
                    Log.d("LoginActivity", "Login unsuccessful boyyyyyy :(")
                    Toast.makeText(this, "Login failed boyyyyyyyyyyy! :(", Toast.LENGTH_LONG).show()
                }
            }



    }

    private fun clearInputs() {
        email_editText_registration.text.clear()
        password_editText_registration.text.clear()
        username_editText_registration.text.clear()
    }
}
//    private fun insertUser(email: String, username: String, uid: String) {
//        val userRef = dbFirestore.collection("users")
//
//        Log.d("RegistrationActivity", userRef.toString())
//
//        val user = hashMapOf<String, Any?>(
//            "email" to email,
//            "username" to username,
//            "uid" to uid
//        )
//
//        userRef.add(user)
//            .addOnSuccessListener { documentReference ->
//                Toast.makeText(this, "User created in database", Toast.LENGTH_SHORT).show()
//                Log.d("RegistrationActivity", "user created in database")
//            }
//            .addOnFailureListener { e ->
//                Log.d("RegistrationActivity", "Error adding document to database")
//            }
//
//    }




