package com.example.abyss.ui.posts.post

import android.os.Bundle
import android.transition.*
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
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
                findNavController().navigate(PostFragmentDirections.actionPostFragmentToEditPostModalBottomSheetFragment())
            }
        }
    }

}