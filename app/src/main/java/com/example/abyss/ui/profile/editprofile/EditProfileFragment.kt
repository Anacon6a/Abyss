package com.example.abyss.ui.profile.editprofile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.abyss.R
import com.example.abyss.adapters.loadImageStatusTracking
import com.example.abyss.databinding.DialogTagsSearchEditPostBinding
import com.example.abyss.databinding.FragmentEditPostBinding
import com.example.abyss.databinding.FragmentEditProfileBinding
import com.example.abyss.extensions.onClick
import com.example.abyss.ui.posts.editpost.EditPostViewModel
import com.example.abyss.ui.posts.post.ModalBottomSheetForPostFragmentArgs
import com.example.abyss.utils.HidingNavigationBar
import com.github.dhaval2404.imagepicker.ImagePicker
import kodeinViewModel
import kotlinx.coroutines.CoroutineDispatcher
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class EditProfileFragment : Fragment(), KodeinAware {

    override val kodein by kodein()
    private val viewModel: EditProfileViewModel by kodeinViewModel()
    private lateinit var binding: FragmentEditProfileBinding
    private val mainDispatcher: CoroutineDispatcher by instance("main")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        binding.editProfileViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        (activity as HidingNavigationBar).hideNavigationBar(true)

        if (viewModel.userImageUri.value != null){
            binding.shapeableImageView.setImageURI(viewModel.userImageUri.value)
        } else if (!viewModel.userImageUrl.value.isNullOrEmpty()) {
            binding.shapeableImageView.loadImageStatusTracking(viewModel.userImageUrl.value)
        }

        subscription()

        return binding.root
    }

    private fun subscription() {
        binding.back.onClick {
            findNavController().popBackStack()
        }
        binding.shapeableImageView.onClick {
            photoSelection()
        }
        binding.selectImage.onClick {
            photoSelection()
        }
        viewModel.userImageUrl.observe(viewLifecycleOwner, { url ->
            if (!url.isNullOrEmpty()) {
                binding.shapeableImageView.loadImageStatusTracking(url)
            }
        })
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
            binding.shapeableImageView.setImageURI(fileUri)

            viewModel.onActivityResult(requestCode, fileUri)
        }
    }
}