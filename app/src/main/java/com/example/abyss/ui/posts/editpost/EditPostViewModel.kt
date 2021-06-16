package com.example.abyss.ui.posts.editpost

import android.net.Uri
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import com.example.abyss.adapters.addtag.TagsPagingAdapter
import com.example.abyss.model.data.PostData
import com.example.abyss.model.repository.post.PostRepository
import com.example.abyss.model.repository.tag.TagRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

class EditPostViewModel(
    private val ioDispatcher: CoroutineDispatcher,
    private val postRepository: PostRepository,
    private val tagRepository: TagRepository,
    private val externalScope: CoroutineScope,
    val tagsPagingAdapter: TagsPagingAdapter
) : ViewModel() {

    private val _postImageUrl = MutableLiveData<Uri>()
    val postImageUrl: LiveData<Uri>
        get() = _postImageUrl

    val textPost = MutableLiveData<String>()

    val tagsText = MutableLiveData<String>()

    private val _eventOnEditPost = MutableLiveData<Boolean>()
    val eventOnEditPost: LiveData<Boolean>
        get() = _eventOnEditPost

    private val _eventPostEdited = MutableLiveData<Boolean>()
    val eventPostEdited: LiveData<Boolean>
        get() = _eventPostEdited

    private val _eventImageSelection = MutableLiveData<Boolean>()
    val eventImageSelection: LiveData<Boolean>
        get() = _eventImageSelection

    private val _loadingEdit = MutableLiveData<Boolean>()
    val loadingEdit: LiveData<Boolean>
        get() = _loadingEdit

    private val _viewEnabled = MutableLiveData<Boolean>()
    val viewEnabled: LiveData<Boolean>
        get() = _viewEnabled

    private val _progressBarTags = MutableLiveData<Boolean>()
    val progressBarTags: LiveData<Boolean>
        get() = _progressBarTags

    val postData = MutableLiveData<PostData>()

    init {
        _viewEnabled.value = true
    }

    fun insertPost(post: PostData) {
        viewModelScope.launch {
            if (postData.value == null) {
                postData.value = post
                post.text?.let {
                    textPost.postValue(it)
                }
                if (!post.tags.isNullOrEmpty()) {
                    tagsText.postValue(post.tags!!.joinToString(separator = ", "))
                }
            }
        }
    }

    fun imageSelection() {
        _eventImageSelection.value = true
    }

    fun endEventImageSelection() {
        _eventImageSelection.value = false
    }

    fun onActivityResult(requestCode: Int, uri: Uri?) {
        if (uri != null) {
            viewModelScope.launch {
                _postImageUrl.postValue(uri!!)
            }
        }
    }

    val widthImage = ObservableField<Int>()
    val heightImage = ObservableField<Int>()

    fun onEditPost() {
        loading(true)
        _eventOnEditPost.value = true
    }

    fun editPost() {
        externalScope.launch(ioDispatcher) {
            val url = postRepository.updatePost(
                postData.value!!,
                postImageUrl.value,
                widthImage.get(),
                heightImage.get(),
                textPost.value,
                tagTextToString()
            )
            if (!url.isNullOrEmpty()) {
                postData.value!!.imageUrl = url
            }
            Timber.i("пост отредактирован")
            _eventPostEdited.postValue(true)
        }
    }

    private suspend fun tagTextToString(): ArrayList<String>{
        return if (!tagsText.value.isNullOrEmpty()) {
            val tL = tagsText.value!!.toLowerCase(Locale.ROOT).split(",")
            val tagsList: ArrayList<String> = arrayListOf()
            tL.forEach { tagsList.add(it.trim().capitalize(Locale.ROOT)) }
            tagsList
        } else {
            arrayListOf()
        }
    }

   private fun loading(b: Boolean){
       _viewEnabled.value = !b
       _loadingEdit.value = b
    }

    fun getAllTags() {
        statusLoading()
        viewModelScope.launch {
            tagRepository.getAllTags().collect {
                tagsPagingAdapter.submitData(it)
            }
        }
    }

    private fun statusLoading() {
        externalScope.launch {
            tagsPagingAdapter.loadStateFlow.collectLatest { loadState ->
                loadingTags(loadState.source.refresh is LoadState.Loading)
            }
        }
    }

    private fun loadingTags(boolean: Boolean) {
        _progressBarTags.postValue(boolean)
    }

    fun getSearchResults(text: String?) {
        viewModelScope.launch {
            if (!text.isNullOrEmpty()) {
                tagRepository.getFoundTags(text).collect {
                    tagsPagingAdapter.submitData(it)
                }
            } else {
                getAllTags()
            }
        }
    }
}