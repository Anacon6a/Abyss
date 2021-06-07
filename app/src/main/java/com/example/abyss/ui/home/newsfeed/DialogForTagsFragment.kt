package com.example.abyss.ui.home.newsfeed

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.abyss.R
import com.example.abyss.adapters.home.newsfeed.NewsFeedUserTagsRecyclerAdapter
import com.example.abyss.databinding.FragmentDialogForTagsBinding
import com.example.abyss.databinding.FragmentModalBottomSheetForPostBinding
import com.example.abyss.model.data.TagData
import com.example.abyss.ui.posts.post.ModalBottomSheetForPostViewModel
import kodeinViewModel
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein


class DialogForTagsFragment : DialogFragment(), KodeinAware {

    override val kodein by kodein()
    private lateinit var binding: FragmentDialogForTagsBinding
    private val viewModel: DialogForTagsViewModel by kodeinViewModel()
    private lateinit var newsFeedUserTagsRecyclerAdapter: NewsFeedUserTagsRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDialogForTagsBinding.inflate(inflater, container, false)
        binding.dialogForTagsViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        newsFeedUserTagsRecyclerAdapter =
            NewsFeedUserTagsRecyclerAdapter(viewModel.userTagsList.value!!)
        subscription()
        setAdaptersForUserTagsRecycler()
        setAdaptersForUsedTagsRecycler()

        return binding.root
    }

    private fun subscription() {
        viewModel.userTagsList.observe(viewLifecycleOwner, {
            newsFeedUserTagsRecyclerAdapter.notifyDataSetChanged()
        })
        newsFeedUserTagsRecyclerAdapter.setOnItemClickListener {
            viewModel.removeUserTag(it.id!!)
        }
    }

    private fun setAdaptersForUserTagsRecycler() {
        lifecycleScope.launch {
            binding.allTagsRecyclerView.apply {
                layoutManager = StaggeredGridLayoutManager(1, RecyclerView.VERTICAL)
                setHasFixedSize(false)
                itemAnimator = null
                adapter = newsFeedUserTagsRecyclerAdapter
            }
        }
    }

    private fun setAdaptersForUsedTagsRecycler() {
        lifecycleScope.launch {
            binding.allTagsRecyclerView.apply {
                layoutManager = StaggeredGridLayoutManager(1, RecyclerView.VERTICAL)
                setHasFixedSize(false)
                itemAnimator = null
                adapter = viewModel.allTagsPagingAdapter
            }
        }
    }

}