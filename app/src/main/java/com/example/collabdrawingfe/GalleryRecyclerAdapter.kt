package com.example.collabdrawingfe

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class GalleryRecyclerAdapter(private val context: Context, private val drawings: List<String>) :

    RecyclerView.Adapter<GalleryRecyclerAdapter.ViewHolder>() {

    private val layoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = layoutInflater.inflate(R.layout.item_gallery_list, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount() = drawings.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val drawing = drawings[position]
        Picasso.get().load(drawing).into(holder.drawingURL)
        holder.drawingPosition = position
    }



    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val drawingURL = itemView?.findViewById<ImageView>(R.id.image_item_gallery)
        var drawingPosition = 0
        init {
            itemView.setOnClickListener {
                val intent = Intent(context, PaintActivity::class.java)
                context.startActivity(intent)
            }
        }
    }
}