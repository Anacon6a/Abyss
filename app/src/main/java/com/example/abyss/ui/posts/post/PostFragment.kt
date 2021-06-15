package com.example.abyss.ui.posts.post

import android.app.AlertDialog
import android.os.Bundle
import android.transition.*
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.abyss.adapters.loadImageStatusTracking
import com.example.abyss.databinding.FragmentPostBinding
import com.example.abyss.extensions.onClick
import com.example.abyss.utils.HidingNavigationBar
import kodeinViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class PostFragment : Fragment(), KodeinAware {

    override val kodein by kodein()
    private val viewModel: PostViewModel by kodeinViewModel()
    private lateinit var binding: FragmentPostBinding
    private val args: PostFragmentArgs by navArgs()
    private val mainDispatcher: CoroutineDispatcher by instance("main")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = FragmentPostBinding.inflate(inflater, container, false)
        binding.postViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        (activity as HidingNavigationBar).hideNavigationBar(false)

        //откладывает переход
        postponeEnterTransition()
        //переход перемещение
        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)

        lifecycleScope.launch(mainDispatcher) {
            viewModel.insertPost(args.post)

            subscription()

            binding.executePendingBindings()

            viewModel.ininitialization()
        }

        return binding.root
    }

    private fun subscription() {
        lifecycleScope.launch(mainDispatcher) {
            // отслеживаем загрузку изображения
            binding.postImage.loadImageStatusTracking(viewModel.postImage.value) {
                //переход
                startPostponedEnterTransition()
            }
            binding.backBtn.onClick {
                findNavController().popBackStack()
            }
            binding.moreBtn.onClick {
                val post = viewModel.postData.value
                if (post != null) {
                    findNavController().navigate(
                        PostFragmentDirections.actionPostFragmentToEditPostModalBottomSheetFragment(
                            post
                        )
                    )
                }
            }
            binding.swipeRefreshLayout.setOnRefreshListener {
                refresh()
            }
            binding.buttonComments.onClick {
                findNavController().navigate(
                    PostFragmentDirections.actionPostFragmentToCommentsModalBottomSheetFragment(
                        args.post,
                        binding.textCommentInputText.text.toString()
                    )
                )
                binding.textCommentInputText.setText("")
            }
            binding.profileImage.onClick {
                goToProfile()
            }
            binding.userProfileLinearLayout.onClick {
                goToProfile()
            }
        }
    }

    private fun goToProfile() {
        lifecycleScope.launch {
            val b = viewModel.trueIfMyUid()
            if (b) {
                findNavController().navigate(PostFragmentDirections.actionPostFragmentToProfileFragment())
            } else {
                findNavController().navigate(
                    PostFragmentDirections.actionPostFragmentToAnotherUserProfileFragment(
                        viewModel.postData.value?.uid!!
                    )
                )
            }
        }
    }

    private fun refresh() {
        viewModel.refresh()
        binding.swipeRefreshLayout.isRefreshing = false
    }

}