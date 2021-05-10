package com.example.abyss.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.example.abyss.adapters.PostProfilePagingAdapter
import com.example.abyss.databinding.FragmentProfileBinding
import com.example.abyss.extensions.onClick
import com.google.firebase.auth.FirebaseAuth
import kodeinViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class ProfileFragment : Fragment(), KodeinAware {

    private lateinit var binding: FragmentProfileBinding

    override val kodein by kodein()

    private val viewModel: ProfileViewModel by kodeinViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.profileViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        //выход временно
        /**пример как изспользовать onClick, заменить все onClickListener на onClick*/
        binding.button.onClick {
            FirebaseAuth.getInstance().signOut()
        }
        //////////////////
        Subscription()
        setAdaptersForRecycler()

        return binding.root
    }

    private fun Subscription() {
        viewModel.postProfilePagingAdapter.setOnItemClickListener { postImage, imageView, postContainer ->

            val action = ProfileFragmentDirections.actionProfileFragmentToPostFragment(postImage)

            findNavController().navigate(
                action, FragmentNavigator.Extras.Builder().addSharedElements(
                    mapOf(
//                                imageView to imageView.transitionName,
                        postContainer to postContainer.transitionName
                    )
                ).build()
            )
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            refresh()
        }
    }

    private fun setAdaptersForRecycler() {
        lifecycleScope.launch {
            binding.profilePostsRecyclerView.apply {
                layoutManager = GridLayoutManager(context, 3)
                setHasFixedSize(false)
                itemAnimator = null
                adapter = viewModel.postProfilePagingAdapter
//                    .withLoadStateFooter(
//                    footer = PostLoadStateAdapter { postAdapter.retry()}
//                )

//                viewTreeObserver
//                    .addOnPreDrawListener {
//                        startPostponedEnterTransition()
//                        true
//                    }

//                lifecycleScope.launch {
//                    postProfileAdapter.loadStateFlow.collectLatest { loadState ->
//
//                        viewModel.LoadingPosts(loadState.source.refresh is LoadState.Loading)
//
//                    }
//                }
            }
        }
    }

    private fun refresh() {
        lifecycleScope.launch {
            viewModel.Refresh()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }
}





