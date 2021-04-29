package com.example.abyss.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.abyss.databinding.PostDataBinding
import com.example.abyss.model.data.PostData

class PostPagingAdapter : PagingDataAdapter<PostData, PostPagingAdapter.PostViewHolder>(Companion) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val binding = PostDataBinding.inflate(
            layoutInflater,
            parent,
            false
        )
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position) ?: return
        holder.bindPost(post)

    }

    companion object : DiffUtil.ItemCallback<PostData>() {
        override fun areItemsTheSame(oldItem: PostData, newItem: PostData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PostData, newItem: PostData): Boolean {
            return oldItem == newItem
        }
    }

    private var onItemClickListener: ((PostData) -> Unit)? = null

    fun setOnItemClickListener(listener: (PostData) -> Unit) {
        onItemClickListener = listener
    }

    inner class PostViewHolder(
        private val binding: PostDataBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindPost(post: PostData) {
            binding.post = post
            Glide.with(binding.iconsImage).load(post.imageUrl).centerCrop().into(binding.iconsImage)

            binding.postContainer.setOnClickListener {
                onItemClickListener?.let {
                    it(post)
                }
            }
        }
    }
}
