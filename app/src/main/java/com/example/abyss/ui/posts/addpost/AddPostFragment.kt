package com.example.abyss.ui.posts.addpost

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.abyss.databinding.DialogTagsSearchAddPostBinding
import com.example.abyss.databinding.FragmentAddPostBinding
import com.example.abyss.extensions.onClick
import com.example.abyss.utils.HidingNavigationBar
import com.github.dhaval2404.imagepicker.ImagePicker
import kodeinViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import timber.log.Timber
import java.util.*


class AddPostFragment : Fragment(), KodeinAware {

    override val kodein by kodein()
    private val viewModel: AddPostViewModel by kodeinViewModel()
    private lateinit var binding: FragmentAddPostBinding
    private val mainDispatcher: CoroutineDispatcher by instance("main")
    private lateinit var dialogTagsSearchBinding: DialogTagsSearchAddPostBinding
    private var dialog: AlertDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = FragmentAddPostBinding.inflate(inflater, container, false)
        binding.addPostViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        (activity as HidingNavigationBar).hideNavigationBar(true)

        dialogTagsSearchBinding =
            DialogTagsSearchAddPostBinding.inflate(layoutInflater, container, false)
        dialogTagsSearchBinding.addPostViewModel = viewModel

        subscription()

        return binding.root
    }

    private fun subscription() {
        viewModel.eventOnAddPost.observe(viewLifecycleOwner, { event ->
            if (event) {
                viewModel.widthImage.set(binding.imageViewNewPost.width)
                viewModel.heightImage.set(binding.imageViewNewPost.height)
                Timber.i("размеры добавлены")
                viewModel.addPost()
            }
        })
        viewModel.eventPostAdded.observe(viewLifecycleOwner, { event ->
            if (event) {
                goToProfile()
            }
        })
        viewModel.eventImageSelection.observe(viewLifecycleOwner, { event ->
            if (event) {
                photoSelection()
                viewModel.endEventImageSelection()
            }
        })
        binding.close.onClick {
            findNavController().popBackStack()
        }
        binding.buttonSearchTags.onClick {
            tagsSearchDialog()
        }
        dialogTagsSearchBinding.backBtn.onClick {
            dialog?.let { dialog!!.dismiss() }
        }
    }

    private fun photoSelection() {
        ImagePicker.with(this)
            .crop().compress(1024)
            .start()
    }

    private fun goToProfile() {
        findNavController().navigate(AddPostFragmentDirections.actionAddPostFragmentToProfileFragment())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {

            val fileUri = data?.data!!

            viewModel.onActivityResult(requestCode, fileUri)
        }
    }

    private fun tagsSearchDialog() {
        if (dialogTagsSearchBinding.root.parent != null) {
            (dialogTagsSearchBinding.root.parent as? ViewGroup)?.removeView(dialogTagsSearchBinding.root)
        }
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setView(dialogTagsSearchBinding.root)
        dialog = builder.create()
        dialog!!.show()

        getAllTags()
        searchTags()
        setAdaptersForTagsRecyclerView()
    }

    private fun getAllTags() {
        viewModel.getAllTags()
    }

    private fun searchTags() {
        lifecycleScope.launch(mainDispatcher) {
            dialogTagsSearchBinding.searchView.setOnQueryTextListener(object :
                SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText != null) {
                        viewModel.getSearchResults(newText.toLowerCase(Locale.ROOT))
                    }
                    return true
                }
            })
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setAdaptersForTagsRecyclerView() {
        lifecycleScope.launch {
            dialogTagsSearchBinding.tagsRecyclerView.apply {
                layoutManager = StaggeredGridLayoutManager(1, RecyclerView.VERTICAL)
                setHasFixedSize(false)
                itemAnimator = null
                adapter = viewModel.tagsPagingAdapter
            }
            viewModel.tagsPagingAdapter.setOnItemClickListener { tag ->
                if (binding.tagsText.text.isNullOrEmpty()) {
                    binding.tagsText.setText(tag.tagName)
                } else {
                    binding.tagsText.setText("${binding.tagsText.text}, ${tag.tagName}")
                }
                Toast.makeText(context, "Тег \"${tag.tagName}\" добавлен", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}