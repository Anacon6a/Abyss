package com.example.abyss.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.abyss.adapters.PostPagingAdapter
import com.example.abyss.databinding.FragmentProfileBinding
import kodeinViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class ProfileFragment : Fragment(), KodeinAware {

    private lateinit var binding: FragmentProfileBinding

    override val kodein by kodein()

    private val viewModel: ProfileViewModel by kodeinViewModel()

    private val postAdapter: PostPagingAdapter by instance()

    var userId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.profileViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setAdapters()
        getProducts()
        return binding.root
    }

    private fun setAdapters() {
        binding.profilePostsRecyclerView.adapter = postAdapter
    }

    private fun getProducts() {
        lifecycleScope.launch {
            viewModel.flow.collectLatest {
                postAdapter.submitData(it)
            }
        }
    }

    private fun setProgressBarAccordingToLoadState() {
//        lifecycleScope.launch {
//            postAdapter.loadStateFlow.collectLatest {
//                binding.progressBar.isVisible = it.append is Loading
//            }
//        }
    }
}




