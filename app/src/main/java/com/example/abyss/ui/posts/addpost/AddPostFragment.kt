package com.example.abyss.ui.posts.addpost

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.abyss.databinding.FragmentAddPostBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import kodeinViewModel
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import java.io.File


class AddPostFragment : Fragment(), KodeinAware {

    override val kodein by kodein()

    private val viewModel: AddPostViewModel by kodeinViewModel()

    private lateinit var binding: FragmentAddPostBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = FragmentAddPostBinding.inflate(inflater, container, false)
        binding.addPostViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        Subscription()

        return binding.root
    }

    private fun Subscription() {

        viewModel.eventPhotoAdded.observe(viewLifecycleOwner, { event ->
            if (event) {
                goToProfile()
            }
        })
//        viewModel.eventCansel.observe(viewLifecycleOwner, { event ->
//
//        })
        viewModel.eventImageSelection.observe(viewLifecycleOwner, {event ->
            if (event) {
                photoSelection()
                viewModel.endEventImageSelection()
            }
        })
    }

    private fun photoSelection() {
        ImagePicker.with(this)
            .crop()
            .saveDir ( File ( Environment .getExternalStorageDirectory (), "Abyss" ))
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
}