package com.example.happybirthday.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.happybirthday.FullScreenImage
import com.example.happybirthday.R

class RecycleAdapter(private val context: Context,
                     private var names: List<String>,
                     private var roles: List<String>,
                     private var distances: List<String>,
                     private var images: List<String>,
                     ) : RecyclerView.Adapter<RecycleAdapter.ViewHolder>() {

    class ViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
        val nameView: TextView = view.findViewById(R.id.person_name)
        val roleView: TextView = view.findViewById(R.id.person_role)
        val distanceView: TextView = view.findViewById(R.id.person_distance)
        val imgView: ImageView = view.findViewById(R.id.iv_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.person_item, parent, false)
        return ViewHolder(adapterLayout)
    }

    override fun getItemCount(): Int {
        return names.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nameView.text = names[position]
        holder.roleView.text = roles[position]
        holder.distanceView.text = distances[position]

        val imgUri = images[position].toUri().buildUpon().scheme("https").build()
        holder.imgView.load(imgUri) {
            placeholder(R.drawable.loading_animation)
            error(R.drawable.ic_broken_image)
        }
        holder.imgView.setOnClickListener {
            val intent = Intent(holder.itemView.context, FullScreenImage::class.java)
            intent.putExtra("image_resource", imgUri.toString())
            holder.itemView.context.startActivity(intent)
        }
    }
}