package com.example.abyss.ui.auth.login

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.abyss.databinding.FragmentLoginBinding
import org.kodein.di.android.x.kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import timber.log.Timber


class LoginFragment : Fragment(), KodeinAware {

    override val kodein by kodein()
    private val factory : LoginViewModelFactory by instance()

    private lateinit var viewModel: LoginViewModel

    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProvider(this, factory).get(LoginViewModel::class.java)
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.loginViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        //задали представление фрагмента в качестве владельца жизненного цикла. Это определяет область действия
        // LiveData объекта выше, позволяя объекту автоматически обновлять виды в fragment_login.xml
        Subscription()


        return binding.root
    }


    private fun Subscription() {
        viewModel.eventGoToRegistration.observe(viewLifecycleOwner, Observer<Boolean> { event ->
            if (event) onRegistdration()
        })
        viewModel.eventLoginCompleted.observe(viewLifecycleOwner, Observer<Boolean> { event ->
            if (event) loginCompleted()
        })
    }

    private fun onRegistdration() {
        parentFragment
        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegistrationFragment())
    }

    private fun loginCompleted() {
        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToNewsFeedFragment())
            //parentFragment?.findNavController()?.navigate(AuthFragmentDirections.actionAuthFragmentToHomeFragment())
    }

}