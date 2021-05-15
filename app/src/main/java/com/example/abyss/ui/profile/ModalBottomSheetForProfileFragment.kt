package com.example.abyss.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.abyss.R
import com.example.abyss.databinding.FragmentModalBottomSheetForProfileBinding
import com.example.abyss.databinding.FragmentProfileBinding
import com.example.abyss.extensions.onClick
import com.example.abyss.utils.HidingNavigationBar
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kodeinViewModel
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein

class ModalBottomSheetForProfileFragment : BottomSheetDialogFragment(), KodeinAware {

    override val kodein by kodein()
    private lateinit var binding: FragmentModalBottomSheetForProfileBinding
    private val viewModel: ModalBottomSheetForProfileViewModel by kodeinViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentModalBottomSheetForProfileBinding.inflate(inflater, container, false)
        binding.modalBottomSheetForProfileViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        subscription()

        return binding.root
    }

    private fun subscription() {
        binding.statisticsBtn.onClick { }
        binding.editProfileBtn.onClick { }
        binding.logoutBtn.onClick {
            viewModel.logout()
        }
        viewModel.eventLogout.observe(viewLifecycleOwner, { event ->
            if (event) {
                findNavController().navigate(ModalBottomSheetForProfileFragmentDirections.actionModalBottomSheetFragmentToLoginFragment())
            }
        })
    }

}