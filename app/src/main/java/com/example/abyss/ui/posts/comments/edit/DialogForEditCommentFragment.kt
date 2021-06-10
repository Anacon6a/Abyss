package com.example.abyss.ui.posts.comments.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.abyss.databinding.FragmentDialogForEditCommentBinding
import com.example.abyss.extensions.onClick
import kodeinViewModel
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein


class DialogForEditCommentFragment : DialogFragment(), KodeinAware {

    override val kodein by kodein()
    private lateinit var binding: FragmentDialogForEditCommentBinding
    private val viewModel: DialogForEditCommentViewModel by kodeinViewModel()
    private val args: DialogForEditCommentFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDialogForEditCommentBinding.inflate(layoutInflater, container, false)
        binding.commentsModalBottomSheetViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.insertData(args.comment, args.contentMakerId)
        subscription()

        return binding.root
    }

    private fun subscription() {
        binding.backBtn.onClick {
            findNavController().popBackStack()
        }
       viewModel.eventCommentEdited.observe(viewLifecycleOwner, {
           if (it == true){
               findNavController().popBackStack()
           }
       })
    }

}