package com.example.abyss.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.abyss.R
import com.example.abyss.data.firebase.FirebaseSource
import com.example.abyss.data.repositories.UserRepository
import com.example.abyss.databinding.FragmentFirstBinding
import com.example.abyss.databinding.FragmentNewsFeedBinding
import com.example.abyss.ui.first.FirstViewModel


class NewsFeedFragment : Fragment() {

    var f: FirebaseSource = FirebaseSource()
var rep: UserRepository = UserRepository(f)

    private lateinit var viewModel: NewsFeedViewModel

    private lateinit var binding: FragmentNewsFeedBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProvider(this).get(NewsFeedViewModel::class.java)
        binding = FragmentNewsFeedBinding.inflate(inflater, container, false)
        binding.newsFeedViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.button.setOnClickListener {
            fff()

        }
        return binding.root
    }

    private fun fff() {
        rep.logout()

    }

}