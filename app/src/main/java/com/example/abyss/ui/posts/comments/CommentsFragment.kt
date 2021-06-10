package com.example.abyss.ui.posts.comments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.abyss.databinding.FragmentCommentsBinding
import com.example.abyss.extensions.onClick
import kodeinViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class CommentsFragment : Fragment(), KodeinAware {

    override val kodein by kodein()
    private val viewModel: CommentsViewModel by kodeinViewModel()
    private lateinit var binding: FragmentCommentsBinding
    private val args: CommentsFragmentArgs by navArgs()
    private val mainDispatcher: CoroutineDispatcher by instance("main")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCommentsBinding.inflate(inflater, container, false)
        binding.commentsViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.insertData(args.post, args.text)
        subscription()
        setAdaptersForCommentRecyclerView()

        return binding.root
    }

    private fun subscription() {
        binding.backBtn.onClick {
            findNavController().popBackStack()
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            refresh()
        }
        viewModel.commentPagingAdapter.setOnItemClickListener {
            findNavController().navigate(
                CommentsFragmentDirections.actionCommentsModalBottomSheetFragmentToModalBottomSheetForCommentFragment(
                    it,
                    args.post.uid!!
                )
            )
        }
    }

    private fun refresh() {
        viewModel.refresh()
        binding.swipeRefreshLayout.isRefreshing = false
    }

    private fun setAdaptersForCommentRecyclerView() {
        lifecycleScope.launch {
            binding.commentsRecyclerView.apply {
                layoutManager = StaggeredGridLayoutManager(1, RecyclerView.VERTICAL)
                setHasFixedSize(false)
                itemAnimator = null
                adapter = viewModel.commentPagingAdapter
            }
        }
    }
}