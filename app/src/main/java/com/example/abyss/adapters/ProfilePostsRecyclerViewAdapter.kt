package com.example.abyss.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.abyss.R
import com.example.abyss.model.data.entity.PostData

class ProfilePostsRecyclerViewAdapter(var context: Context, var arrayList: ArrayList<PostData>) :
    RecyclerView.Adapter<ProfilePostsRecyclerViewAdapter.ItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val itemHolder = LayoutInflater.from(parent.context)
            .inflate(R.layout.grid_layout_list_item, parent, false)
        return ItemHolder(itemHolder)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        var post: PostData = arrayList.get(position)
        holder.icons.setImageURI((post.ImageUrl).toUri())

    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var icons = itemView.findViewById<ImageView>(R.id.icons_image)
    }



}