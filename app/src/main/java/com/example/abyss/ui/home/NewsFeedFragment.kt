package com.example.abyss.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.abyss.adapters.PostNewsFeedPagingAdapter
import com.example.abyss.adapters.PostNewsFeedViewPagerAdapter
import com.example.abyss.adapters.PostProfilePagingAdapter
import com.example.abyss.databinding.FragmentNewsFeedBinding
import com.example.abyss.databinding.PostNewsFeedRecyclerDataBinding
import com.example.abyss.ui.profile.ProfileFragmentDirections
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import kodeinViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class NewsFeedFragment() : Fragment(), KodeinAware {

    override val kodein by kodein()
    private val viewModel: NewsFeedViewModel by kodeinViewModel()
    private lateinit var binding: FragmentNewsFeedBinding
    private lateinit var bindingRecycler: PostNewsFeedRecyclerDataBinding

    //    private val postNewsFeedPagingAdapter: PostNewsFeedPagingAdapter by instance()
    private val postNewsFeedViewPagerAdapter: PostNewsFeedViewPagerAdapter by instance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = FragmentNewsFeedBinding.inflate(inflater, container, false)
        binding.newsFeedViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner


        Subscription()
//        GetPostsForRecyclerview()
        setAdaptersForViewPager()
        setTabLayout()

        return binding.root
    }

    private fun Subscription() {
        viewModel.postNewsFeedPagingAdapter.setOnItemClickListener { post, imageView, postContainer ->
            val action = NewsFeedFragmentDirections.actionNewsFeedFragmentToPostFragment(post)
            findNavController().navigate(
                action, FragmentNavigator.Extras.Builder().addSharedElements(
                    mapOf(
//                                imageView to imageView.transitionName,
                        postContainer to postContainer.transitionName
                    )
                ).build()
            )
        }
        postNewsFeedViewPagerAdapter.setOnCreatePostViewHolder {
            bindingRecycler = it
            setAdaptersForRecycler()
        }
        binding.viewPagerPosts.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewModel.SetPosition(position)
            }
        })
    }

    private fun setAdaptersForViewPager() {
        lifecycleScope.launch {
            binding.viewPagerPosts.apply {
                adapter = postNewsFeedViewPagerAdapter
            }
        }
    }

    private fun setAdaptersForRecycler() {
        lifecycleScope.launch {
            bindingRecycler.postRecyclerView.apply {
                layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
                setHasFixedSize(false)
                itemAnimator = null
                adapter = viewModel.postNewsFeedPagingAdapter
//                    .withLoadStateFooter(
//                    footer = PostLoadStateAdapter { postAdapter.retry()}
//                )

//                viewTreeObserver
//                    .addOnPreDrawListener {
//                        startPostponedEnterTransition()
//                        true
//                    }
            }
        }
//        lifecycleScope.launch {
//            postNewsFeedPagingAdapter.loadStateFlow.collectLatest { loadState ->
//                viewModel.LoadingPosts(loadState.source.refresh is LoadState.Loading)
//            }
//        }
    }


    private fun setTabLayout() {
        TabLayoutMediator(binding.newsFeedTabLayout, binding.viewPagerPosts) { tablayout, position ->
            tablayout.text = when(position) {
                0 -> "Подписки"
                else -> "Тренды"
            }
        }.attach()
    }

//    private fun GetPostsForRecyclerview() {
//        viewModel.getPosts?.observe(viewLifecycleOwner, {
//            lifecycleScope.launch {
//                postNewsFeedPagingAdapter.submitData(it)
//            }
//        })
//    }


}