package com.example.collabdrawingfe

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

import kotlinx.android.synthetic.main.activity_registration.*
import java.io.IOException
import java.util.*


class RegistrationActivity : AppCompatActivity(), View.OnClickListener {

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()


    private val PICK_IMAGE_REQUEST = 1234

    private fun showFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Photo"), PICK_IMAGE_REQUEST)
    }



    private var filePath: Uri? = null

    internal var storage : FirebaseStorage?=null
    internal var storageReference:StorageReference?=null

    override fun onClick(p0: View?) {
        if (p0 === select_photo_button_registration)
            showFileChooser()
        else if (p0 === register_button_registration)
            uploadFile()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK &&
            data != null && data.data != null)
        {
            filePath = data.data

            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                val bitmapDrawable = BitmapDrawable(bitmap)
                select_photo_button_registration.setBackgroundDrawable(bitmapDrawable)

            } catch (e: IOException)
            {
                e.printStackTrace()
            }
        }
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
        select_photo_button_registration.setOnClickListener(this)

        already_have_an_account_text_view.setOnClickListener {
            Log.d("RegistrationActivity", "Try to show Log in Activity")


            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

//        select_photo_button_registration.setOnClickListener {
//            Log.d("RegistrationActivity", "select photo button clicked")
//
//            val intent = Intent(Intent.ACTION_PICK)
//            intent.type = "image/*"
//            startActivityForResult(intent, 0)
//        }
    }

//    var selectedPhotoURI: Uri? = null

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
//            //check what the image was....
//            Log.d("RegistrationActivity", "Photo was selected")
//
//            selectedPhotoURI = data.data
//            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoURI)
//            val bitmapDrawable = BitmapDrawable(bitmap)
//            select_photo_button_registration.setBackgroundDrawable(bitmapDrawable)
//        }
//    }

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
                    val intent = Intent(this, GalleryActivity::class.java)
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




