package com.example.abyss.ui.home.newsfeed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.abyss.adapters.home.newsfeed.NewsFeedUserTagsRecyclerAdapter
import com.example.abyss.databinding.FragmentDialogForTagsBinding
import com.example.abyss.extensions.hideKeyboard
import com.example.abyss.extensions.onClick
import kodeinViewModel
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import java.util.*


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

        hideKeyboard()

        return binding.root
    }

    private fun subscription() {
        viewModel.eventUserTagsListUpdate.observe(viewLifecycleOwner, {

            newsFeedUserTagsRecyclerAdapter.notifyDataSetChanged()
        })
        newsFeedUserTagsRecyclerAdapter.setOnItemClickListener {
            viewModel.removeUserTag(it.id!!)
        }
        binding.backBtn.onClick {
            findNavController().popBackStack()
        }
        binding.searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    viewModel.getFoundTags(newText.toLowerCase(Locale.ROOT))
                }
                return true
            }
        })
    }

    private fun setAdaptersForUserTagsRecycler() {
        lifecycleScope.launch {
            binding.userTagsRecyclerView.apply {
                layoutManager = StaggeredGridLayoutManager(1, RecyclerView.HORIZONTAL)
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