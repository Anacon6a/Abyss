package com.example.abyss.ui.posts.addpost

import android.net.Uri
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.example.abyss.model.data.PostData
import com.example.abyss.model.repository.post.PostRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import timber.log.Timber
import java.util.*

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
                _postImageUrl.postValue(uri!!)
                _buttonEnabled.postValue(true)
            }
        }
    }

    val widthImage = ObservableField<Int>()
    val heightImage = ObservableField<Int>()

    fun onAddPost() {
        _eventOnAddPost.value = true
    }

    fun addPost() {
        externalScope.launch(ioDispatcher) {


            postImageUrl.value?.let {

                val url = postRepository.AddPostImageInStorage(it).collect { url ->

                    val date = Date(System.currentTimeMillis())
                    val post =
                        PostData(url, signature.value, date, widthImage.get(), heightImage.get(), 0, 0)
                    postRepository.CreatePost(post)
                    Timber.i("пост создан")
                }
            }
        }
        _eventPostAdded.value = true
    }

}


