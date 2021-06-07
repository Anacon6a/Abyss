package com.example.abyss.adapters.home.newsfeed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.abyss.databinding.UsedTagsForNewsFeedDataBinding
import com.example.abyss.databinding.UserTagsForNewsFeedDataBinding

import com.example.abyss.extensions.onClick
import com.example.abyss.model.data.TagData
import com.example.abyss.model.data.UsedTagData

class NewsFeedUserTagsRecyclerAdapter(
    private val tags: List<TagData>
) : RecyclerView.Adapter<NewsFeedUserTagsRecyclerAdapter.UserTagViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsFeedUserTagsRecyclerAdapter.UserTagViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val binding = UserTagsForNewsFeedDataBinding.inflate(
            layoutInflater,
            parent,
            false
        )
        return UserTagViewHolder(binding)
    }


    override fun onBindViewHolder(holder: UserTagViewHolder, position: Int) {
        val tag = tags[position]
        holder.bindTag(tag)
    }

    override fun getItemCount(): Int {
        return tags.size
    }

    private var onItemClickListener: ((TagData) -> Unit)? = null

    fun setOnItemClickListener(listener: (TagData) -> Unit) {
        onItemClickListener = listener
    }

    inner class UserTagViewHolder(
        private val binding: UserTagsForNewsFeedDataBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTag(tag: TagData) {
            binding.tag = tag

            binding.buttonAddTag.onClick {
                onItemClickListener?.let {
                    it(tag)
                }
            }
        }
    }
}