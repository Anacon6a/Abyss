package com.example.abyss.adapters.comment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.abyss.adapters.addtag.TagsPagingAdapter
import com.example.abyss.databinding.TagsSearchDataBinding
import com.example.abyss.databinding.UserCommentDataDataBinding
import com.example.abyss.extensions.onClick
import com.example.abyss.model.data.TagData
import com.example.abyss.model.data.UserCommentData

class CommentPagingAdapter: PagingDataAdapter<UserCommentData, CommentPagingAdapter.CommentViewHolder>(Companion) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentPagingAdapter.CommentViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val binding = UserCommentDataDataBinding.inflate(
            layoutInflater,
            parent,
            false
        )
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentPagingAdapter.CommentViewHolder, position: Int) {
        val comment = getItem(position) ?: return
        holder.bindComment(comment)
    }

    companion object : DiffUtil.ItemCallback<UserCommentData>() {
        override fun areItemsTheSame(oldItem: UserCommentData, newItem: UserCommentData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: UserCommentData, newItem: UserCommentData): Boolean {
            return oldItem == newItem
        }
    }


    private var onItemClickListener: ((UserCommentData) -> Unit)? = null

    fun setOnItemClickListener(listener: (UserCommentData) -> Unit) {
        onItemClickListener = listener
    }
    private var onUserClickListener: ((String) -> Unit)? = null

    fun setOnUserClickListener(listener: (String) -> Unit) {
        onUserClickListener = listener
    }

    inner class CommentViewHolder(
        private val binding: UserCommentDataDataBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindComment(comment: UserCommentData) {
            binding.comment = comment

            binding.moreBtn.onClick {
                onItemClickListener?.let {
                    it(comment)
                }
            }
            binding.profileImage.onClick {
                onUserClickListener?.let {
                    it(comment.uid)
                }
            }
        }
    }
}