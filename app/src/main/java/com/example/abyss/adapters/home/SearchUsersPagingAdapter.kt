package com.example.abyss.adapters.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.abyss.adapters.loadImage2
import com.example.abyss.databinding.PostNewsFeedDataBinding
import com.example.abyss.databinding.UserSearchDataBinding
import com.example.abyss.extensions.onClick
import com.example.abyss.model.data.PostData
import com.example.abyss.model.data.UserData

class SearchUsersPagingAdapter: PagingDataAdapter<UserData, SearchUsersPagingAdapter.UserViewHolder>(Companion){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val binding = UserSearchDataBinding.inflate(
            layoutInflater,
            parent,
            false
        )
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = getItem(position) ?: return
        holder.bindUser(user)
    }

    companion object : DiffUtil.ItemCallback<UserData>() {
        override fun areItemsTheSame(oldItem: UserData, newItem: UserData): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: UserData, newItem: UserData): Boolean {
            return oldItem == newItem
        }
    }

    private var onItemClickListener: ((UserData) -> Unit)? = null

    fun setOnItemClickListener(listener: (UserData) -> Unit) {
        onItemClickListener = listener
    }

    inner class UserViewHolder(
        private val binding: UserSearchDataBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindUser(user: UserData) {
            binding.user = user
            binding.userConstraintLayout.onClick {
                onItemClickListener?.let {
                    it(user)
                }
            }
        }
    }
}