package com.example.abyss.ui.posts.editpost

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.abyss.adapters.loadImageStatusTracking
import com.example.abyss.databinding.FragmentAddPostBinding
import com.example.abyss.databinding.FragmentEditPostBinding
import com.example.abyss.extensions.onClick
import com.example.abyss.ui.posts.addpost.AddPostFragmentDirections
import com.example.abyss.ui.posts.post.ModalBottomSheetForPostFragmentArgs
import com.example.abyss.utils.HidingNavigationBar
import com.github.dhaval2404.imagepicker.ImagePicker
import kodeinViewModel
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import timber.log.Timber

class EditPostFragment : Fragment(), KodeinAware {
    override val kodein by kodein()
    private val viewModel: EditPostViewModel by kodeinViewModel()
    private lateinit var binding: FragmentEditPostBinding
    private val args: ModalBottomSheetForPostFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = FragmentEditPostBinding.inflate(inflater, container, false)
        binding.editPostViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        (activity as HidingNavigationBar).hideNavigationBar(true)

        viewModel.insertPost(args.post)
        if (viewModel.postImageUrl.value == null){
            binding.imageViewNewPost.loadImageStatusTracking(args.post.imageUrl)
        } else {
            binding.imageViewNewPost.setImageURI(viewModel.postImageUrl.value)
        }
        subscription()

        return binding.root
    }

    private fun subscription() {
        viewModel.eventOnEditPost.observe(viewLifecycleOwner, { event ->
            if (event) {
                viewModel.widthImage.set(binding.imageViewNewPost.width)
                viewModel.heightImage.set(binding.imageViewNewPost.height)
                Timber.i("размеры добавлены")
                viewModel.editPost()
            }
        })
        viewModel.eventPostEdited.observe(viewLifecycleOwner, { event ->
            if (event) {
             findNavController().navigate(EditPostFragmentDirections.actionEditPostFragmentToPostFragment(viewModel.postData.value!!))
            }
        })
        viewModel.eventImageSelection.observe(viewLifecycleOwner, { event ->
            if (event) {
                photoSelection()
                viewModel.endEventImageSelection()
            }
        })
        binding.close.onClick {
            findNavController().popBackStack()
        }
    }

    private fun photoSelection() {
        ImagePicker.with(this)
            .crop().compress(1024)
            .start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {

            val fileUri = data?.data!!
            binding.imageViewNewPost.setImageURI(fileUri)

            viewModel.onActivityResult(requestCode, fileUri)
        }
    }
}