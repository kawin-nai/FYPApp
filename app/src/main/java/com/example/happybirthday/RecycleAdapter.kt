package com.example.happybirthday

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text

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
        bindImage(holder.imgView, images[position])
//        holder.imgView.setImageResource(images[position])
    }
}