package com.example.abyss.ui.posts.addpost

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.abyss.databinding.DialogTagsSearchBinding
import com.example.abyss.databinding.FragmentAddPostBinding
import com.example.abyss.databinding.PostNewsFeedDataBinding
import com.example.abyss.extensions.onClick
import com.example.abyss.utils.HidingNavigationBar
import com.github.dhaval2404.imagepicker.ImagePicker
import kodeinViewModel
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import timber.log.Timber


class AddPostFragment : Fragment(), KodeinAware {

    override val kodein by kodein()

    private val viewModel: AddPostViewModel by kodeinViewModel()

    private lateinit var binding: FragmentAddPostBinding

    private lateinit var dialogTagsSearchBinding: DialogTagsSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = FragmentAddPostBinding.inflate(inflater, container, false)
        binding.addPostViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        (activity as HidingNavigationBar).hideNavigationBar(true)

        dialogTagsSearchBinding = DialogTagsSearchBinding.inflate(layoutInflater, container,false)

        subscription()

        return binding.root
    }

    private fun subscription() {
        viewModel.eventOnAddPost.observe(viewLifecycleOwner, { event ->
            if (event) {
                viewModel.widthImage.set(binding.imageViewNewPost.width)
                viewModel.heightImage.set(binding.imageViewNewPost.height)
                Timber.i("размеры добавлены")
                viewModel.addPost()
            }
        })
        viewModel.eventPostAdded.observe(viewLifecycleOwner, { event ->
            if (event) {
                goToProfile()
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
        binding.buttonSearchTags.onClick {
            tagsSearchDialog()
        }
    }

    private fun photoSelection() {
        ImagePicker.with(this)
            .crop().compress(1024)
            .start()
    }

    private fun goToProfile() {
        findNavController().navigate(AddPostFragmentDirections.actionAddPostFragmentToProfileFragment())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {

            val fileUri = data?.data!!

            viewModel.onActivityResult(requestCode, fileUri)
        }
    }

    private fun tagsSearchDialog(){
       val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setView(dialogTagsSearchBinding.root)
        builder.create()
        builder.show()
    }
}