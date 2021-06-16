package com.example.abyss.ui.posts.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.abyss.databinding.FragmentModalBottomSheetForPostBinding
import com.example.abyss.extensions.onClick
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kodeinViewModel
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein

class ModalBottomSheetForPostFragment : BottomSheetDialogFragment(), KodeinAware {

    override val kodein by kodein()
    private lateinit var binding: FragmentModalBottomSheetForPostBinding
    private val viewModel: ModalBottomSheetForPostViewModel by kodeinViewModel()
    private val args: ModalBottomSheetForPostFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentModalBottomSheetForPostBinding.inflate(inflater, container, false)
        binding.modalBottomSheetForPostViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        subscription()

        return binding.root
    }

    private fun subscription() {
        binding.editBtn.onClick {
            findNavController().navigate(
                ModalBottomSheetForPostFragmentDirections.actionEditPostModalBottomSheetFragmentToEditPostFragment(
                    args.post
                )
            )
        }
        binding.deleteBtn.onClick {
            viewModel.deletePost(args.post)
            findNavController().navigate(ModalBottomSheetForPostFragmentDirections.actionEditPostModalBottomSheetFragmentToProfileFragment())
        }
//        viewModel.eventDeletePost.observe(viewLifecycleOwner, { event ->
//            if (event) {
//            }
//        })
    }

}