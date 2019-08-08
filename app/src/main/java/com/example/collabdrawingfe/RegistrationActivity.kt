package com.example.collabdrawingfe

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_registration.*
import java.util.*


class RegistrationActivity : AppCompatActivity(), View.OnClickListener {

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()


    private var filePath: Uri? = null

    internal var storage : FirebaseStorage?=null
    internal var storageReference:StorageReference?=null

    override fun onClick(p0: View?) {
        if (p0 === register_button_registration)
            uploadFile()

    }

    private fun uploadFile() {

        if (filePath != null)
        {
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Uploading...")
            progressDialog.show()

            val imageRef = storageReference!!.child("profilePics/" + UUID.randomUUID().toString())
            imageRef.putFile(filePath!!)
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    Toast.makeText(applicationContext, "File Uploaded", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    progressDialog.dismiss()
                    Toast.makeText(applicationContext, "Failed", Toast.LENGTH_SHORT).show()
                }
                .addOnProgressListener { taskSnapshot ->
                    val progress = 100.0 * taskSnapshot.bytesTransferred/taskSnapshot.totalByteCount
                    progressDialog.setMessage("Uploaded " + progress.toInt() + "%...")

                }
        }
        registerUser()

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference



        register_button_registration.setOnClickListener(this)

        already_have_an_account_text_view.setOnClickListener {
            Log.d("RegistrationActivity", "Try to show Log in Activity")


            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
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




    private fun signIn(email: String, password: String) {

        Log.d("LoginActivity", "signIn ran")
        Log.d("LoginActivity", "email is: $email")
        Log.d("LoginActivity", "password is: $password")

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if(task.isSuccessful) {
                    Log.d("LoginActivity", "Login successful")
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, GalleryActivity::class.java)
                    startActivity(intent)
                }
                else {
                    Log.d("LoginActivity", "Login unsuccessful boyyyyyy :(")
                    Toast.makeText(this, "Login unsuccessful, please try again", Toast.LENGTH_LONG).show()
                }
            }



    }

}




