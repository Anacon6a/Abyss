package com.example.abyss.ui.profile.anotheruser

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.abyss.databinding.FragmentAnotherUserProfileBinding
import com.example.abyss.extensions.onClick
import com.example.abyss.ui.profile.profile.ProfileFragmentDirections
import com.example.abyss.utils.HidingNavigationBar
import kodeinViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class AnotherUserProfileFragment : Fragment(), KodeinAware {

    private lateinit var binding: FragmentAnotherUserProfileBinding
    override val kodein by kodein()
    private val viewModel: AnotherUserProfileViewModel by kodeinViewModel()
    private val mainDispatcher: CoroutineDispatcher by instance("main")
    private val args: AnotherUserProfileFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = FragmentAnotherUserProfileBinding.inflate(inflater, container, false)
        binding.anotherUserProfileViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        (activity as HidingNavigationBar).hideNavigationBar(false)

        viewModel.ininitialization(args.uid)
        subscription()
        setAdaptersForRecycler()

        return binding.root
    }

    private fun subscription() {
            viewModel.postsPagingAdapter.setOnItemClickListener { post, imageView, postContainer ->
                    val action =
                        AnotherUserProfileFragmentDirections.actionAnotherUserProfileFragmentToPostFragment(post)
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
        binding.btnBack.onClick {
            findNavController().popBackStack()
        }
    }

    private fun setAdaptersForRecycler() {
        lifecycleScope.launch {
            binding.profilePostsRecyclerView.apply {
                layoutManager = GridLayoutManager(context, 3)
                setHasFixedSize(false)
                itemAnimator = null
                adapter = viewModel.postsPagingAdapter

            }
        }
    }

    private fun refresh() {
        lifecycleScope.launch {
            viewModel.refresh()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }
}