package com.example.abyss.ui.posts.editpost

import android.net.Uri
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.abyss.model.data.PostData
import com.example.abyss.model.repository.post.PostRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

class EditPostViewModel(
    private val ioDispatcher: CoroutineDispatcher,
    private val postRepository: PostRepository,
    private val externalScope: CoroutineScope,
) : ViewModel() {

    private val _postImageUrl = MutableLiveData<Uri>()
    val postImageUrl: LiveData<Uri>
        get() = _postImageUrl

    val textPost = MutableLiveData<String>()

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

    val postData = MutableLiveData<PostData>()

    init {
        _viewEnabled.value = true
    }

    fun insertPost(post: PostData) {
        if (postData.value == null) {
            postData.value = post
            post.text?.let {
                textPost.postValue(it)
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
            val url = postRepository.editPost(
                postData.value!!,
                postImageUrl.value,
                widthImage.get(),
                heightImage.get(),
                textPost.value
            )
            if (!url.isNullOrEmpty()) {
                postData.value!!.imageUrl = url
            }
            Timber.i("пост отредактирован")
            _eventPostEdited.postValue(true)
        }
    }

   private fun loading(b: Boolean){
       _viewEnabled.value = !b
       _loadingEdit.value = b
    }
}