package com.example.collabdrawingfe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_gallery.*

class InviteCollaboratorActivity : AppCompatActivity() {

    private val dbFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invite_collaborator)
        fetchAllUsers()
    }

    private fun fetchAllUsers() {
        val userRef = dbFirestore.collection("users")
        userRef.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    var username = document.get("username")
                    Log.d("invite", "$username")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("invite","Error getting documents", exception)
            }

    }
}
