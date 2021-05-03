package com.example.abyss.ui.posts.post

import android.os.Bundle
import android.transition.*
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.abyss.adapters.loadImageStatusTracking
import com.example.abyss.databinding.FragmentPostBinding
import kodeinViewModel
import kotlinx.coroutines.launch
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

        //откладывает переход
        postponeEnterTransition()
        //переход перемещение
        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        // отслеживаем загрузку изображения
        binding.postImage.loadImageStatusTracking(args.post.imageUrl) {
            //переход
            startPostponedEnterTransition()
        }

        binding.executePendingBindings()

        viewModel.postData.set(args.post)
        viewModel.Insert()
//        InsertPost()

        return binding.root
    }


    fun InsertPost() {
        lifecycleScope.launch {
//            if (viewModel.postImageUrl.value == null) {
//                viewModel.InsertPost(args.post)
//            }
//            Glide.with(requireContext()).load(args.post.imageUrl).into(binding.postImage)
        }
    }

    fun InsertImage() {

    }
}