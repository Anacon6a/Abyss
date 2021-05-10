package com.example.abyss.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.abyss.R
import com.example.abyss.databinding.PostNewsFeedRecyclerDataBinding
import com.example.abyss.databinding.PostProfileDataBinding
import com.example.abyss.model.data.PostData
import com.example.abyss.model.repository.post.PostRepository
import com.example.abyss.ui.profile.ProfileFragmentDirections
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PostNewsFeedViewPagerAdapter : RecyclerView.Adapter<PostNewsFeedViewPagerAdapter.PostViewHolder>() {

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
