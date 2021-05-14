package com.example.abyss.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.abyss.databinding.PostProfileDataBinding
import com.example.abyss.extensions.onClick
import com.example.abyss.model.data.PostData

class PostProfilePagingAdapter : PagingDataAdapter<PostData, PostProfilePagingAdapter.PostViewHolder>(Companion) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val binding = PostProfileDataBinding.inflate(
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

    private var onItemClickListener: ((PostData, ImageView, ConstraintLayout) -> Unit)? = null

    fun setOnItemClickListener(listener: (PostData, ImageView, ConstraintLayout) -> Unit) {
        onItemClickListener = listener
    }

    inner class PostViewHolder(
        private val binding: PostProfileDataBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindPost(post: PostData) {
            binding.post = post

            binding.postContainer.onClick {
                onItemClickListener?.let {

                    it(post,  binding.iconsImage, binding.postContainer)
                }
            }
        }
    }

}
