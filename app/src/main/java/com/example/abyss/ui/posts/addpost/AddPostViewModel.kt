package com.example.abyss.ui.posts.addpost

import android.content.DialogInterface
import android.net.Uri
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.example.abyss.adapters.TagsPagingAdapter
import com.example.abyss.model.data.PostData
import com.example.abyss.model.repository.post.PostRepository
import com.example.abyss.model.repository.tag.TagRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList


class AddPostViewModel(
    private val ioDispatcher: CoroutineDispatcher,
    private val postRepository: PostRepository,
    private val tagRepository: TagRepository,
    private val externalScope: CoroutineScope,
    val tagsPagingAdapter: TagsPagingAdapter
) : ViewModel() {

    private val _postImageUrl = MutableLiveData<Uri>()
    val postImageUrl: LiveData<Uri>
        get() = _postImageUrl

    val signature = MutableLiveData<String>()

    private val _buttonEnabled = MutableLiveData<Boolean>()
    val buttonEnabled: LiveData<Boolean>
        get() = _buttonEnabled

    private val _eventOnAddPost = MutableLiveData<Boolean>()
    val eventOnAddPost: LiveData<Boolean>
        get() = _eventOnAddPost

    private val _eventPostAdded = MutableLiveData<Boolean>()
    val eventPostAdded: LiveData<Boolean>
        get() = _eventPostAdded

    private val _eventImageSelection = MutableLiveData<Boolean>()
    val eventImageSelection: LiveData<Boolean>
        get() = _eventImageSelection

    private val _loadingAdd = MutableLiveData<Boolean>()
    val loadingAdd: LiveData<Boolean>
        get() = _loadingAdd

    private val _viewEnabled = MutableLiveData<Boolean>()
    val viewEnabled: LiveData<Boolean>
        get() = _viewEnabled

    private val _progressBarTags = MutableLiveData<Boolean>()
    val progressBarTags: LiveData<Boolean>
        get() = _progressBarTags

    var tagsText: String? = null

    init {
        _buttonEnabled.value = false
        _viewEnabled.value = true
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
                _buttonEnabled.postValue(true)
            }
        }
    }

    val widthImage = ObservableField<Int>()
    val heightImage = ObservableField<Int>()

    fun onAddPost() {
        loading(true)
        _eventOnAddPost.value = true
    }

    fun addPost() {
        externalScope.launch(ioDispatcher) {
            postImageUrl.value?.let { imgUri ->

                val post = PostData(
                    text = signature.value,
                    widthImage = widthImage.get(),
                    heightImage = heightImage.get(),
                )
                if (!tagsText.isNullOrEmpty()) {
                    val tL = tagsText!!.toLowerCase(Locale.ROOT).split(",")
                    val tagsList: ArrayList<String> = arrayListOf()
                    tL.forEach { tagsList.add(it.trim().capitalize(Locale.ROOT)) }
                    post.tags = tagsList
                    tagRepository.createTag(tagsList)
                }

                postRepository.createPost(post, imgUri)
                Timber.i("пост создан")
                _eventPostAdded.postValue(true)
            }
        }
    }

    private fun loading(b: Boolean) {
        _viewEnabled.value = !b
        _buttonEnabled.value = !b
        _loadingAdd.value = b
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


