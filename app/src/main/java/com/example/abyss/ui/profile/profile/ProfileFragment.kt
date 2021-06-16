package com.example.abyss.ui.profile.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.abyss.adapters.profile.ProfileViewPagerAdapter
import com.example.abyss.databinding.FragmentProfileBinding
import com.example.abyss.databinding.PostProfileRecyclerDataBinding
import com.example.abyss.extensions.ignorePullToRefresh
import com.example.abyss.extensions.onClick
import com.example.abyss.ui.profile.profile.ProfileFragmentDirections
import com.example.abyss.utils.HidingNavigationBar
import com.google.android.material.tabs.TabLayoutMediator
import kodeinViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class ProfileFragment : Fragment(), KodeinAware {

    private lateinit var binding: FragmentProfileBinding
    override val kodein by kodein()
    private val viewModel: ProfileViewModel by kodeinViewModel()
    private val mainDispatcher: CoroutineDispatcher by instance("main")
    private val viewPagerAdapter: ProfileViewPagerAdapter by instance()
    private lateinit var bindingRecycler: PostProfileRecyclerDataBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.profileViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        (activity as HidingNavigationBar).hideNavigationBar(false)
        binding.viewPagerPosts.ignorePullToRefresh(binding.swipeRefreshLayout)

        setTabLayout()
        subscription()
        setAdaptersForViewPager()

        return binding.root
    }

    private fun subscription() {
        viewPagerAdapter.setOnCreateSearchViewHolder { bind, pos ->
            bindingRecycler = bind
            when (pos) {
                0 -> viewModel.postsPagingAdapter.let {
                    it.setOnItemClickListener { post, imageView, postContainer ->
                        val action =
                            ProfileFragmentDirections.actionProfileFragmentToPostFragment(post)
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
                1 -> viewModel.savedPostsPagingAdapter.let {
                    it.setOnItemClickListener { post, imageView, postContainer ->
                        val action =
                            ProfileFragmentDirections.actionProfileFragmentToPostFragment(post)
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
            }
            setAdaptersForRecycler(pos)
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            refresh()
        }
        binding.btnMore.onClick {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToModalBottomSheetFragment())
        }
    }

    private fun setAdaptersForRecycler(pos: Int) {
        when (pos) {
            0 -> lifecycleScope.launch {
                bindingRecycler.profilePostsRecyclerView.apply {
                    layoutManager = GridLayoutManager(context, 3)
                    setHasFixedSize(false)
                    itemAnimator = null
                    adapter = viewModel.postsPagingAdapter

//                viewTreeObserver
//                    .addOnPreDrawListener {
//                        startPostponedEnterTransition()
//                        true
//                    }
                }
            }
            1 -> lifecycleScope.launch {
                bindingRecycler.profilePostsRecyclerView.apply {
                    layoutManager = GridLayoutManager(context, 3)
                    setHasFixedSize(false)
                    itemAnimator = null
                    adapter = viewModel.savedPostsPagingAdapter
                }
            }
        }
    }
    private fun setAdaptersForViewPager() {
        lifecycleScope.launch {
            binding.viewPagerPosts.apply {
                adapter = viewPagerAdapter
            }
        }
    }

    private fun setTabLayout() {
        lifecycleScope.launch(mainDispatcher) {
            TabLayoutMediator(
                binding.profileTabLayout,
                binding.viewPagerPosts
            ) { tablayout, position ->
                when (position) {
                    0 -> tablayout.text = "Мои публикации"
                    1 -> tablayout.text = "Избранное"
                }
            }.attach()
        }
    }

    private fun refresh() {
        lifecycleScope.launch {
            viewModel.refresh()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }
}





