package com.example.abyss.ui.posts.addpost

import android.content.DialogInterface
import android.net.Uri
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.example.abyss.model.data.PostData
import com.example.abyss.model.repository.post.PostRepository
import kotlinx.coroutines.*
import timber.log.Timber


class AddPostViewModel(
    private val ioDispatcher: CoroutineDispatcher,
    private val postRepository: PostRepository,
    private val externalScope: CoroutineScope,
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
            postImageUrl.value?.let {
                val post = PostData(
                    text = signature.value,
                    widthImage = widthImage.get(),
                    heightImage = heightImage.get()
                )
                postRepository.createPost(post, it)
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

}


