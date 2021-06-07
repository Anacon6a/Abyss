package com.example.abyss.adapters.home.newsfeed

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.abyss.databinding.PostNewsFeedRecyclerDataBinding

class NewsFeedViewPagerAdapter : RecyclerView.Adapter<NewsFeedViewPagerAdapter.PostViewHolder>() {

    var cout = 2

    override fun getItemCount(): Int {
        return cout
    }

    @SuppressLint("ResourceType")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = PostNewsFeedRecyclerDataBinding.inflate(
            layoutInflater,
            parent,
            false
        )
        return PostViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bindRecycler(position)
    }


    inner class PostViewHolder(
        private val binding: PostNewsFeedRecyclerDataBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bindRecycler(position: Int) {

            onCreatePostViewHolder?.let {
                it(binding, position)
            }
        }
    }
    private var onCreatePostViewHolder: ((PostNewsFeedRecyclerDataBinding, Int) -> Unit)? = null

    fun setOnCreatePostViewHolder(listener: (PostNewsFeedRecyclerDataBinding, Int) -> Unit) {
        onCreatePostViewHolder = listener
    }
}
