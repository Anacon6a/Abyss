package com.example.abyss.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.abyss.databinding.PostNewsFeedDataBinding
import com.example.abyss.extensions.onClick
import com.example.abyss.model.data.PostData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class PostNewsFeedPagingAdapter(
    private val externalScope: CoroutineScope,
    private val mainDispatcher: CoroutineDispatcher,

) : PagingDataAdapter<PostData, PostNewsFeedPagingAdapter.PostViewHolder>(Companion) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val binding = PostNewsFeedDataBinding.inflate(
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

    private val set = ConstraintSet()

    inner class PostViewHolder(
        private val binding: PostNewsFeedDataBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindPost(post: PostData) {
            externalScope.launch(mainDispatcher) {
                binding.post = post
                binding.postContainer.onClick {
                    onItemClickListener?.let {

                        it(post, binding.iconsImage, binding.postContainer)
                    }
                }
            }

                val ratio = String.format("%d:%d", post.widthImage, post.heightImage)
                set.clone(binding.postContainer)
                set.setDimensionRatio(binding.iconsImage.id, ratio)
                set.applyTo(binding.postContainer)

                binding.iconsImage.loadImage2(post.imageUrl)

        }
    }


    private var onItemClickListener: ((PostData, ImageView, ConstraintLayout) -> Unit)? = null

    fun setOnItemClickListener(listener: (PostData, ImageView, ConstraintLayout) -> Unit) {
        onItemClickListener = listener
    }
}