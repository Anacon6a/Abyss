package com.example.abyss.ui.posts.editpost

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.abyss.adapters.loadImageStatusTracking
import com.example.abyss.databinding.DialogTagsSearchEditPostBinding
import com.example.abyss.databinding.FragmentAddPostBinding
import com.example.abyss.databinding.FragmentEditPostBinding
import com.example.abyss.extensions.onClick
import com.example.abyss.ui.posts.addpost.AddPostFragmentDirections
import com.example.abyss.ui.posts.post.ModalBottomSheetForPostFragmentArgs
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

class EditPostFragment : Fragment(), KodeinAware {
    override val kodein by kodein()
    private val viewModel: EditPostViewModel by kodeinViewModel()
    private lateinit var binding: FragmentEditPostBinding
    private val args: ModalBottomSheetForPostFragmentArgs by navArgs()
    private lateinit var dialogTagsSearchBinding: DialogTagsSearchEditPostBinding
    private val mainDispatcher: CoroutineDispatcher by instance("main")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = FragmentEditPostBinding.inflate(inflater, container, false)
        binding.editPostViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        (activity as HidingNavigationBar).hideNavigationBar(true)

        dialogTagsSearchBinding = DialogTagsSearchEditPostBinding.inflate(layoutInflater, container, false)

        viewModel.insertPost(args.post)
        if (viewModel.postImageUrl.value == null){
            binding.imageViewNewPost.loadImageStatusTracking(args.post.imageUrl)
        } else {
            binding.imageViewNewPost.setImageURI(viewModel.postImageUrl.value)
        }
        subscription()

        return binding.root
    }

    private fun subscription() {
        viewModel.eventOnEditPost.observe(viewLifecycleOwner, { event ->
            if (event) {
                viewModel.widthImage.set(binding.imageViewNewPost.width)
                viewModel.heightImage.set(binding.imageViewNewPost.height)
                Timber.i("размеры добавлены")
                viewModel.editPost()
            }
        })
        viewModel.eventPostEdited.observe(viewLifecycleOwner, { event ->
            if (event) {
             findNavController().navigate(EditPostFragmentDirections.actionEditPostFragmentToPostFragment(viewModel.postData.value!!))
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
    }

    private fun photoSelection() {
        ImagePicker.with(this)
            .crop().compress(1024)
            .start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {

            val fileUri = data?.data!!
            binding.imageViewNewPost.setImageURI(fileUri)

            viewModel.onActivityResult(requestCode, fileUri)
        }
    }
    private fun tagsSearchDialog() {

        if (dialogTagsSearchBinding.root.parent !=null){
            (dialogTagsSearchBinding.root.parent as? ViewGroup)?.removeView(dialogTagsSearchBinding.root)
        }
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setView(dialogTagsSearchBinding.root)
        builder.create()
        builder.show()

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

    private fun setAdaptersForTagsRecyclerView() {
        lifecycleScope.launch {
            dialogTagsSearchBinding.tagsRecyclerView .apply {
                layoutManager = StaggeredGridLayoutManager(1, RecyclerView.VERTICAL)
                setHasFixedSize(false)
                itemAnimator = null
                adapter = viewModel.tagsPagingAdapter
            }
            viewModel.tagsPagingAdapter.setOnItemClickListener { tag ->
                if (binding.tagsText.text.isNullOrEmpty()){
                    binding.tagsText.setText(tag.tagName)
                } else {
                    binding.tagsText.setText("${binding.tagsText.text}, ${tag.tagName}")
                }
                Toast.makeText(context, "Тег \"${tag.tagName}\" добавлен", Toast.LENGTH_SHORT).show()
            }
        }
    }
}