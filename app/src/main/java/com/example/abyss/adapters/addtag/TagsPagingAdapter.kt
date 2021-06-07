package com.example.abyss.adapters.addtag

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.abyss.databinding.TagsSearchDataBinding
import com.example.abyss.extensions.onClick
import com.example.abyss.model.data.TagData

class TagsPagingAdapter: PagingDataAdapter<TagData, TagsPagingAdapter.TagViewHolder>(
    Companion
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val binding = TagsSearchDataBinding.inflate(
            layoutInflater,
            parent,
            false
        )
        return TagViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        val tag = getItem(position) ?: return
        holder.bindTag(tag)
    }

    companion object : DiffUtil.ItemCallback<TagData>() {
        override fun areItemsTheSame(oldItem: TagData, newItem: TagData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TagData, newItem: TagData): Boolean {
            return oldItem == newItem
        }
    }

    private var onItemClickListener: ((TagData) -> Unit)? = null

    fun setOnItemClickListener(listener: (TagData) -> Unit) {
        onItemClickListener = listener
    }

    inner class TagViewHolder(
        private val binding: TagsSearchDataBinding,
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
