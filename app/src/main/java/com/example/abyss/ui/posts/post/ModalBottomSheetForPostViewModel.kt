package com.example.abyss.ui.posts.post

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.abyss.model.data.PostData
import com.example.abyss.model.repository.post.PostRepository
import kotlinx.coroutines.launch

class ModalBottomSheetForPostViewModel(
    private val postRepository: PostRepository,
) : ViewModel() {

//    private val _eventDeletePost = MutableLiveData<Boolean>()
//    val eventDeletePost: LiveData<Boolean>
//    get() = _eventDeletePost

    fun deletePost(post: PostData) {
        viewModelScope.launch {
            postRepository.deletePost(post)
//            _eventDeletePost.postValue(true)
        }
    }
}