package com.example.collabdrawingfe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_invite_collaborator.*
import kotlin.collections.ArrayList


class InviteCollaboratorActivity : AppCompatActivity() {

    private val dbFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    val users = ArrayList<String>()


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
                    users.add("$username")
                    userlist_recyclerview.layoutManager = LinearLayoutManager(this)
                    userlist_recyclerview.adapter = CollaboratorsRecyclerAdapter(this, users)

//                    contacts_list.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, users)

//                    userlist_recyclerview.setOnItemClickListener{parent, view, position, id ->
//                        Log.d("Invite", "${users[position]} selected")
//                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.d("invite","Error getting documents", exception)
            }


    }
}
