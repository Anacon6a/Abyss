package com.example.abyss.adapters.home.newsfeed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.abyss.databinding.UsedTagsForNewsFeedDataBinding
import com.example.abyss.extensions.onClick
import com.example.abyss.model.data.UsedTagData

class NewsFeedAllTagsPagingAdapter : PagingDataAdapter<UsedTagData, NewsFeedAllTagsPagingAdapter.UsedTagViewHolder>(Companion) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsFeedAllTagsPagingAdapter.UsedTagViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val binding = UsedTagsForNewsFeedDataBinding.inflate(
            layoutInflater,
            parent,
            false
        )
        return UsedTagViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsFeedAllTagsPagingAdapter.UsedTagViewHolder, position: Int) {
        val tag = getItem(position) ?: return
        holder.bindTag(tag)
    }

    companion object : DiffUtil.ItemCallback<UsedTagData>() {
        override fun areItemsTheSame(oldItem: UsedTagData, newItem: UsedTagData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: UsedTagData, newItem: UsedTagData): Boolean {
            return oldItem == newItem
        }
    }

    private var onItemClickListener: ((UsedTagData) -> Unit)? = null

    fun setOnItemClickListener(listener: (UsedTagData) -> Unit) {
        onItemClickListener = listener
    }

    inner class UsedTagViewHolder(
        private val binding: UsedTagsForNewsFeedDataBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTag(tag: UsedTagData) {
            binding.tag = tag

            binding.buttonAddTag.onClick {
                tag.used = !tag.used!!
                onItemClickListener?.let {
                    it(tag)
                }
            }
        }
    }
}