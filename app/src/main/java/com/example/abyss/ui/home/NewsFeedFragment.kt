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
import androidx.viewpager2.widget.ViewPager2
import com.example.abyss.adapters.PostNewsFeedPagingAdapter
import com.example.abyss.adapters.PostNewsFeedViewPagerAdapter
import com.example.abyss.adapters.PostProfilePagingAdapter
import com.example.abyss.databinding.FragmentNewsFeedBinding
import com.example.abyss.databinding.PostNewsFeedRecyclerDataBinding
import com.example.abyss.ui.profile.ProfileFragmentDirections
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
    private val postNewsFeedPagingAdapter: PostNewsFeedPagingAdapter by instance()
    private val postNewsFeedViewPagerAdapter: PostNewsFeedViewPagerAdapter by instance()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = FragmentNewsFeedBinding.inflate(inflater, container, false)
        binding.newsFeedViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

//выход временно
        binding.button.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
        }


        Subscription()
        GetPostsForRecyclerview()
        setAdaptersForViewPager()

        return binding.root
    }

    private fun Subscription() {
        postNewsFeedPagingAdapter.setOnItemClickListener { post, imageView, postContainer ->
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
                layoutManager = GridLayoutManager(context, 1)
                setHasFixedSize(false)
                itemAnimator = null
                adapter = postNewsFeedPagingAdapter
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
        lifecycleScope.launch {
            postNewsFeedPagingAdapter.loadStateFlow.collectLatest { loadState ->
                viewModel.LoadingPosts(loadState.source.refresh is LoadState.Loading)
            }
        }
    }

    private fun GetPostsForRecyclerview() {
        viewModel.getPosts?.observe(viewLifecycleOwner, {
            lifecycleScope.launch {
                postNewsFeedPagingAdapter.submitData(it)
            }
        })
    }





}