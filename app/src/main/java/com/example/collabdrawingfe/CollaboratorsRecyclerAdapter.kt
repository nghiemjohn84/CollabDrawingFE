package com.example.collabdrawingfe

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class CollaboratorsRecyclerAdapter(private val context: Context, private val users: List<String>) :

    RecyclerView.Adapter<CollaboratorsRecyclerAdapter.ViewHolder>(){

    private val layoutInflater = LayoutInflater.from(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = layoutInflater.inflate(R.layout.item_user_list, parent, false)
        return ViewHolder(itemView)

    }

    override fun getItemCount() = users.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.username?.text = user
        holder.userPosition = position
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username = itemView?.findViewById<TextView?>(R.id.user_textview_item_user_list)
        var userPosition = 0
        init {
            itemView.setOnClickListener {
                val intent = Intent(context, PaintActivity::class.java)
                context.startActivity(intent)
            }
        }
    }
}