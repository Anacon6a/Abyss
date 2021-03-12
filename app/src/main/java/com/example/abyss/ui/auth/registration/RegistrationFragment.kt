package com.example.abyss.ui.auth.registration

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.abyss.databinding.FragmentRegistrationBinding
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class RegistrationFragment : Fragment(), KodeinAware {

    override val kodein by kodein()
    private val factory : RegistrationViewModelFactory by instance()

    private lateinit var viewModel: RegistrationViewModel

    private lateinit var binding: FragmentRegistrationBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        viewModel = ViewModelProvider(this, factory).get(RegistrationViewModel::class.java)
        binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        binding.registrationViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        Subscription()

        return binding.root

    }

    private fun Subscription() {
        viewModel.eventGoToLogin.observe(viewLifecycleOwner, Observer<Boolean> { event ->
            if (event) onLogin()
        })
        viewModel.eventRegistrationCompleted.observe(viewLifecycleOwner, Observer<Boolean> { event ->
            if (event) onRegistdration()
        })
    }

    private fun onLogin() {
        findNavController().navigate(RegistrationFragmentDirections.actionRegistrationFragmentToLoginFragment())
    }

    private fun onRegistdration() {
        findNavController().navigate(RegistrationFragmentDirections.actionRegistrationFragmentToNewsFeedFragment())
    }

}