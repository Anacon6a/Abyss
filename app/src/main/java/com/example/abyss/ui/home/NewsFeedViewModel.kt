package com.example.abyss.ui.home

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.example.abyss.model.repository.post.PostRepository

class NewsFeedViewModel(
    private val postRepository: PostRepository,
): ViewModel() {

    private val _progressBarloadingAllPosts = MutableLiveData<Boolean>()
    val progressBarloadingAllPosts: LiveData<Boolean>
        get() = _progressBarloadingAllPosts

    val getPosts = postRepository.GetPostForProfile()?.cachedIn(viewModelScope)?.asLiveData()

    fun LoadingPosts(boolean: Boolean) {
        _progressBarloadingAllPosts.postValue(boolean)
    }

}