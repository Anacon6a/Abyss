package com.example.abyss.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.abyss.adapters.PostNewsFeedPagingAdapter
import com.example.abyss.adapters.PostNewsFeedViewPagerAdapter
import com.example.abyss.databinding.FragmentNewsFeedBinding
import com.example.abyss.databinding.PostNewsFeedRecyclerDataBinding
import com.example.abyss.extensions.ignorePullToRefresh
import com.example.abyss.utils.HidingNavigationBar
import com.google.android.material.tabs.TabLayoutMediator
import kodeinViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class NewsFeedFragment() : Fragment(), KodeinAware {

    override val kodein by kodein()
    private val viewModel: NewsFeedViewModel by kodeinViewModel()
    private val mainDispatcher: CoroutineDispatcher by instance("main")

    private lateinit var binding: FragmentNewsFeedBinding

    private lateinit var bindingRecycler: PostNewsFeedRecyclerDataBinding

    private val postNewsFeedViewPagerAdapter: PostNewsFeedViewPagerAdapter by instance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = FragmentNewsFeedBinding.inflate(inflater, container, false)
        binding.newsFeedViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        (activity as HidingNavigationBar).hideNavigationBar(false)

        binding.viewPagerPosts.ignorePullToRefresh(binding.swipeRefreshLayout)

        lifecycleScope.launch(mainDispatcher) {
            viewModel.initial()
            Subscription()
            setTabLayout()
            setAdaptersForViewPager()
        }

        return binding.root
    }

    private fun Subscription() {
        viewModel.listTitles.observe(viewLifecycleOwner, { listTitles ->
            listTitles?.let {
                if (postNewsFeedViewPagerAdapter.cout != listTitles.size) {
                    postNewsFeedViewPagerAdapter.cout = listTitles.size
                    postNewsFeedViewPagerAdapter.notifyDataSetChanged()
                }
                setTabLayout()
            }
        })
        binding.viewPagerPosts.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewModel.getPosts(position)
            }
        })

        postNewsFeedViewPagerAdapter.setOnCreatePostViewHolder { bind, pos ->
            bindingRecycler = bind
            viewModel.listPostPagingAdapters.value?.get(pos)?.let {
                it.setOnItemClickListener { post, imageView, postContainer ->
                    val action =
                        NewsFeedFragmentDirections.actionNewsFeedFragmentToPostFragment(post)
                    findNavController().navigate(
                        action, FragmentNavigator.Extras.Builder().addSharedElements(
                            mapOf(
//                                imageView to imageView.transitionName,
                                postContainer to postContainer.transitionName
                            )
                        ).build()
                    )
                }
                setAdaptersForRecycler(it)
            }
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            refresh()
        }
    }


    private fun setAdaptersForViewPager() {
        lifecycleScope.launch {
            binding.viewPagerPosts.apply {
                adapter = postNewsFeedViewPagerAdapter
            }
        }
    }

    private fun setAdaptersForRecycler(postNewsFeedPagingAdapter: PostNewsFeedPagingAdapter) {
        lifecycleScope.launch {
            bindingRecycler.postRecyclerView.apply {
                layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
                setHasFixedSize(false)
                itemAnimator = null
                adapter = postNewsFeedPagingAdapter

//                viewTreeObserver
//                    .addOnPreDrawListener {
//                        startPostponedEnterTransition()
//                        true
//                    }
            }
        }
    }

    private fun setTabLayout() {
        lifecycleScope.launch(mainDispatcher) {
            TabLayoutMediator(
                binding.newsFeedTabLayout,
                binding.viewPagerPosts
            ) { tablayout, position ->
                tablayout.text = viewModel.listTitles.value?.get(position)
            }.attach()
        }
    }

    private fun refresh() {
        lifecycleScope.launch {
            viewModel.onRefresh()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }
}