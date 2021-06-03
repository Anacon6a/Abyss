package com.example.abyss.ui.home.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.abyss.R
import com.example.abyss.adapters.home.SearchViewPagerAdapter
import com.example.abyss.databinding.FragmentNewsFeedBinding
import com.example.abyss.databinding.FragmentSearchBinding
import com.example.abyss.databinding.SearchRecyclerDataBinding
import com.example.abyss.extensions.hideKeyboard
import com.example.abyss.extensions.ignorePullToRefresh
import com.example.abyss.ui.home.newsfeed.NewsFeedFragmentDirections
import com.example.abyss.utils.HidingNavigationBar
import com.google.android.material.tabs.TabLayoutMediator
import kodeinViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class SearchFragment : Fragment(), KodeinAware {

    override val kodein by kodein()
    private val viewModel: SearchViewModel by kodeinViewModel()
    private val mainDispatcher: CoroutineDispatcher by instance("main")
    private lateinit var binding: FragmentSearchBinding
    private lateinit var bindingRecycler: SearchRecyclerDataBinding
    private val searchViewPagerAdapter: SearchViewPagerAdapter by instance()
    private val args: SearchFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSearchBinding.inflate(inflater, container, false)
        binding.searchViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        (activity as HidingNavigationBar).hideNavigationBar(false)
        binding.searchViewPager.ignorePullToRefresh(binding.swipeRefreshLayout)

        if (viewModel.firstSearch.value == null){
            viewModel.initial(args.searchQuery)
        }
        setTabLayout()
        subscription()
        setAdaptersForViewPager()

        return binding.root
    }

    private fun subscription() {
        searchViewPagerAdapter.setOnCreateSearchViewHolder { bind, pos ->
            bindingRecycler = bind
            when (pos) {
                1 -> viewModel.searchPostsPagingAdapter.let {
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
                }
                2 -> viewModel.searchUsersPagingAdapter.let {
                    it.setOnItemClickListener { user ->

                    }
                }
            }
            setAdaptersForRecycler(pos)
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            refresh()
        }
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.getSearchResults(newText)
                return true
            }
        })
    }

    private fun setAdaptersForViewPager() {
        lifecycleScope.launch {
            binding.searchViewPager.apply {
                adapter = searchViewPagerAdapter
            }
        }
    }

    private fun setAdaptersForRecycler(pos: Int) {
        when (pos) {
            0 -> lifecycleScope.launch {
                bindingRecycler.searchRecyclerView.apply {
                    layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
                    setHasFixedSize(false)
                    itemAnimator = null
                    adapter = viewModel.searchPostsPagingAdapter

//                viewTreeObserver
//                    .addOnPreDrawListener {
//                        startPostponedEnterTransition()
//                        true
//                    }
                }
            }
            1 -> lifecycleScope.launch {
                bindingRecycler.searchRecyclerView.apply {
                    layoutManager = StaggeredGridLayoutManager(1, RecyclerView.VERTICAL)
                    setHasFixedSize(false)
                    itemAnimator = null
                    adapter = viewModel.searchUsersPagingAdapter
                }
            }
        }
    }

    private fun setTabLayout() {
        lifecycleScope.launch(mainDispatcher) {
            TabLayoutMediator(
                binding.searchTabLayout,
                binding.searchViewPager
            ) { tablayout, position ->
                when (position) {
                    0 -> tablayout.text = "Публикации"
                    1 -> tablayout.text = "Пользователи"
                }
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