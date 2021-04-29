package com.example.abyss.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.abyss.adapters.PostLoadStateAdapter
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
        binding.progressBarLoadingAllPosts.visibility = View.GONE
        setAdapters()
        getPosts()
        return binding.root
    }

    // настройка RecyclerView и подключение к адаптерам
    private fun setAdapters() {
        lifecycleScope.launch {
            binding.profilePostsRecyclerView.apply {
                layoutManager = GridLayoutManager(context, 3)
                setHasFixedSize(false)
                itemAnimator = null
                adapter = postAdapter
//                    .withLoadStateFooter(
//                    footer = PostLoadStateAdapter { postAdapter.retry()}
//                )
                lifecycleScope.launch {
                    postAdapter.loadStateFlow.collectLatest { loadState ->

                        viewModel.LoadingPosts(loadState.source.refresh is LoadState.Loading)
                        // ничего не найдено - is LoadState.NotLoading
                        //    is LoadState.Error
                    }
                }
            }
        }
//        postAdapter.setOnItemClickListener {
//            val action = HomeFragmentDirections.actionHomeFragmentToDetailsFragment(it)
//            findNavController().navigate(action)
//        }
    }

    // получение данных из viewmodel и передача адаптеру
    private fun getPosts() {
        lifecycleScope.launch {
            viewModel.flow.collectLatest {
                postAdapter.submitData(it)
            }
        }
    }

}




