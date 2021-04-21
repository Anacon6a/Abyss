package com.example.abyss.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.abyss.databinding.FragmentNewsFeedBinding
import com.example.abyss.model.repository.auth.AuthRepository
import com.example.abyss.model.repository.auth.AuthRepositoryFirebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class NewsFeedFragment : Fragment() {


    var f: FirebaseAuth = FirebaseAuth.getInstance()

    private lateinit var viewModel: NewsFeedViewModel

    private lateinit var binding: FragmentNewsFeedBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        viewModel = ViewModelProvider(this).get(NewsFeedViewModel::class.java)
        binding = FragmentNewsFeedBinding.inflate(inflater, container, false)
        binding.newsFeedViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
//выход временно
        binding.button.setOnClickListener {
            fff()

        }
        return binding.root
    }

    private fun fff() {
        f.signOut()
    }

}