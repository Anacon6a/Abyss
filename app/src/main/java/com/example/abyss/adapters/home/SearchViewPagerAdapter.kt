package com.example.abyss.adapters.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.abyss.databinding.SearchRecyclerDataBinding

class SearchViewPagerAdapter: RecyclerView.Adapter<SearchViewPagerAdapter.SearchViewHolder>() {
    override fun getItemCount(): Int {
        return 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = SearchRecyclerDataBinding.inflate(
            layoutInflater,
            parent,
            false
        )
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bindRecycler(position)
    }


    inner class SearchViewHolder(
        private val binding: SearchRecyclerDataBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindRecycler(position: Int) {

            onCreateSearchViewHolder?.let {
                it(binding, position)
            }
        }
    }
    private var onCreateSearchViewHolder: ((SearchRecyclerDataBinding, Int) -> Unit)? = null

    fun setOnCreateSearchViewHolder(listener: (SearchRecyclerDataBinding, Int) -> Unit) {
        onCreateSearchViewHolder = listener
    }
}