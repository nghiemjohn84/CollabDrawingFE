package com.example.collabdrawingfe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

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
                for(document in result){
                    Log.d("invite","${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("invite","Error getting documents", exception)
            }
    }
}
