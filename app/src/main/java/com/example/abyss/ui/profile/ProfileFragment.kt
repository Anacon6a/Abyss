package com.example.abyss.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.abyss.databinding.FragmentProfileBinding
import com.example.abyss.model.data.entity.PostData
import com.example.abyss.model.repository.post.PostRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kodeinViewModel
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import timber.log.Timber


class ProfileFragment : Fragment(), KodeinAware {

    private lateinit var binding: FragmentProfileBinding

    override val kodein by kodein()

    private val viewModel: ProfileViewModel by kodeinViewModel()

    var userId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.profileViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

}




