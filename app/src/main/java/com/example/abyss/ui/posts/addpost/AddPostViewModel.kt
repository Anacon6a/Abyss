package com.example.abyss.ui.posts.addpost

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.abyss.model.repository.post.PostRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import java.util.*

class AddPostViewModel(
    private val ioDispatcher: CoroutineDispatcher,
    private val postRepository: PostRepository,
    private val externalScope: CoroutineScope,
) : ViewModel() {

    private val _postImageUrl = MutableLiveData<Uri>()
    val postImageUrl: LiveData<Uri>
        get() = _postImageUrl

    private val postImageUrlInStorage = MutableLiveData<Uri>()

    val signature = MutableLiveData<String>()

    private val _buttonEnabled = MutableLiveData<Boolean>()
    val buttonEnabled: LiveData<Boolean>
        get() = _buttonEnabled

    private val _eventPhotoAdded = MutableLiveData<Boolean>()
    val eventPhotoAdded: LiveData<Boolean>
        get() = _eventPhotoAdded

    private val _eventImageSelection = MutableLiveData<Boolean>()
    val eventImageSelection: LiveData<Boolean>
        get() = _eventImageSelection


//    private val _eventCansel = MutableLiveData<Boolean>()
//    val eventCansel: LiveData<Boolean>
//        get() = _eventCansel


    init {
        _buttonEnabled.value = false
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
                _postImageUrl.value = uri!!
                _buttonEnabled.value = true
            }
        }
    }

    fun addPost() {
        externalScope.launch(ioDispatcher) {
            postImageUrl.value?.let {

                    val url = postRepository.AddPostImageInStorage(it).collect { url ->

                        val date = Date(System.currentTimeMillis())
//                        val post = PostData(url, signature.value, date)

//                        postRepository.CreatePost(post)

                    }
            }
            cancel()
        }
        _eventPhotoAdded.value = true
    }

}


