package com.example.abyss.adapters.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.abyss.databinding.PostProfileRecyclerDataBinding
import com.example.abyss.databinding.SearchRecyclerDataBinding

class ProfileViewPagerAdapter: RecyclerView.Adapter<ProfileViewPagerAdapter.PostsViewHolder>() {
    override fun getItemCount(): Int {
        return 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = PostProfileRecyclerDataBinding.inflate(
            layoutInflater,
            parent,
            false
        )
        return PostsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostsViewHolder, position: Int) {
        holder.bindRecycler(position)
    }


    inner class PostsViewHolder(
        private val binding: PostProfileRecyclerDataBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindRecycler(position: Int) {

            onCreateSearchViewHolder?.let {
                it(binding, position)
            }
        }
    }
    private var onCreateSearchViewHolder: ((PostProfileRecyclerDataBinding, Int) -> Unit)? = null

    fun setOnCreateSearchViewHolder(listener: (PostProfileRecyclerDataBinding, Int) -> Unit) {
        onCreateSearchViewHolder = listener
    }
}