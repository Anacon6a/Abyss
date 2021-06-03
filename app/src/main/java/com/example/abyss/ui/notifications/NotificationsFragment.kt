package com.example.abyss.ui.notifications

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.abyss.databinding.FragmentNotificationsBinding
import com.example.abyss.utils.HidingNavigationBar
import kodeinViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class NotificationsFragment : Fragment(), KodeinAware {

    override val kodein by kodein()
    private val viewModel: NotificationsViewModel by kodeinViewModel()
    private val mainDispatcher: CoroutineDispatcher by instance("main")
    private lateinit var binding: FragmentNotificationsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        binding.notificationsViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        (activity as HidingNavigationBar).hideNavigationBar(false)

        subscription()
        setAdaptersForRecycler()

        return binding.root
    }

    private fun subscription() {
        viewModel.newNotificationsPagingAdapter.setOnPostClickListener { postData ->

            val action = NotificationsFragmentDirections.actionNotificationsFragmentToPostFragment(postData)
            findNavController().navigate(action)
//            findNavController().navigate(
//                action, FragmentNavigator.Extras.Builder().addSharedElements(
//                    mapOf(
////                                imageView to imageView.transitionName,
//                        postContainer to postContainer.transitionName
//                    )
//                ).build()
//            )
        }
        viewModel.viewedNotificationsPagingAdapter.setOnPostClickListener { postData ->

            val action = NotificationsFragmentDirections.actionNotificationsFragmentToPostFragment(postData)
            findNavController().navigate(action)
//            findNavController().navigate(
//                action, FragmentNavigator.Extras.Builder().addSharedElements(
//                    mapOf(
////                                imageView to imageView.transitionName,
//                        postContainer to postContainer.transitionName
//                    )
//                ).build()
//            )
        }
        viewModel.newNotificationsPagingAdapter.setOnUserClickListener {  }
        viewModel.viewedNotificationsPagingAdapter.setOnUserClickListener {  }

        binding.swipeRefreshLayout.setOnRefreshListener {
            refresh()
        }
    }

    private fun setAdaptersForRecycler() {
        lifecycleScope.launch {
            binding.newNotificationsRecyclerView.apply {
                layoutManager = StaggeredGridLayoutManager(1, RecyclerView.VERTICAL)
                setHasFixedSize(false)
                itemAnimator = null
                adapter = viewModel.newNotificationsPagingAdapter

//                viewTreeObserver
//                    .addOnPreDrawListener {
//                        startPostponedEnterTransition()
//                        true
//                    }
            }
        }
        lifecycleScope.launch {
            binding.viewedNotificationsRecyclerView.apply {
                layoutManager = StaggeredGridLayoutManager(1, RecyclerView.VERTICAL)
                setHasFixedSize(false)
                itemAnimator = null
                adapter = viewModel.viewedNotificationsPagingAdapter

//                viewTreeObserver
//                    .addOnPreDrawListener {
//                        startPostponedEnterTransition()
//                        true
//                    }
            }
        }
    }

    private fun refresh() {
        viewModel.onRefresh()
        binding.swipeRefreshLayout.isRefreshing = false
    }

}