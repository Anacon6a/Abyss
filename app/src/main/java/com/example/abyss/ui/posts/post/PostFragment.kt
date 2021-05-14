package com.example.abyss.ui.posts.post

import android.os.Bundle
import android.transition.*
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.abyss.adapters.loadImageStatusTracking
import com.example.abyss.databinding.FragmentPostBinding
import com.example.abyss.utils.HidingNavigationBar
import kodeinViewModel
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein

class PostFragment : Fragment(), KodeinAware {

    override val kodein by kodein()

    private val viewModel: PostViewModel by kodeinViewModel()

    private lateinit var binding: FragmentPostBinding

    private val args: PostFragmentArgs by navArgs()

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

        viewModel.insertPost(args.post)
        // отслеживаем загрузку изображения
        binding.postImage.loadImageStatusTracking(viewModel.postImage.value) {
            //переход
            startPostponedEnterTransition()
        }

        binding.executePendingBindings()

        viewModel.ininitialization()

        return binding.root
    }

}