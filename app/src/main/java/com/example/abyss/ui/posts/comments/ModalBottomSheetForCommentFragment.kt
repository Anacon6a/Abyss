package com.example.abyss.ui.posts.comments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.abyss.databinding.FragmentModalBottomSheetForCommentBinding
import com.example.abyss.extensions.onClick
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kodeinViewModel
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein


class ModalBottomSheetForCommentFragment : BottomSheetDialogFragment(), KodeinAware {

    override val kodein by kodein()
    private lateinit var binding: FragmentModalBottomSheetForCommentBinding
    private val viewModel: ModalBottomSheetForCommentViewModel by kodeinViewModel()
    private val args: ModalBottomSheetForCommentFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentModalBottomSheetForCommentBinding.inflate(inflater, container, false)
        binding.modalBottomSheetForCommentViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        subscription()

        return binding.root
    }

    private fun subscription() {
        binding.editBtn.onClick {
            findNavController().navigate(
                ModalBottomSheetForCommentFragmentDirections.actionModalBottomSheetForCommentFragmentToDialogForEditCommentFragment(
                    args.comment, args.contentMakerId
                )
            )
        }
        binding.deleteBtn.onClick {
            viewModel.deleteComment(args.comment, args.contentMakerId)
//            findNavController().navigate(ModalBottomSheetForCommentFragmentDirections.actionModalBottomSheetForCommentFragmentToCommentsModalBottomSheetFragment2(
//                PostData(id = args.comment.postId, uid = args.contentMakerId, imageUrl = args.comment.profileImageUrl), null
//            ))
            findNavController().popBackStack()
//            supportFragmentManager.popBackStackImmediate ( "theFragment" , 0)
        }
    }

}