package com.example.abyss.ui.first

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.abyss.databinding.FragmentFirstBinding
import com.example.abyss.ui.HidingNavigationBar
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance



class FirstFragment : Fragment(), KodeinAware {

    override val kodein by kodein()
    private val factory : FirstViewModelFactory by instance()

    private lateinit var viewModel: FirstViewModel

    private lateinit var binding: FragmentFirstBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProvider(this, factory).get(FirstViewModel::class.java)
        binding = FragmentFirstBinding.inflate(inflater, container, false)
        binding.firstViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        (activity as HidingNavigationBar).hideNavigationBar(true)

        Subscription()

        return binding.root
    }

    private fun Subscription() {
        viewModel.eventGoToHome.observe(viewLifecycleOwner, Observer<Boolean> { event ->
            if (event) GoToHome()
        })
        viewModel.eventGoToAuth.observe(viewLifecycleOwner, Observer<Boolean> { event ->
            if (event) GoToAuth()
        })
    }

    private fun GoToHome() {
        findNavController().navigate(FirstFragmentDirections.actionFirstFragmentToNewsFeedFragment())
    }

    private fun GoToAuth() {
        findNavController().navigate(FirstFragmentDirections.actionFirstFragmentToLoginFragment())
    }


}