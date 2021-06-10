package com.example.abyss.ui.home.newsfeed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import com.example.abyss.adapters.home.newsfeed.NewsFeedAllTagsPagingAdapter
import com.example.abyss.model.data.UserTagData
import com.example.abyss.model.repository.tag.TagRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DialogForTagsViewModel(
    private val tagRepository: TagRepository,
    private val externalScope: CoroutineScope,
    val allTagsPagingAdapter: NewsFeedAllTagsPagingAdapter,
) : ViewModel() {
    private val _progressBarUserTags = MutableLiveData<Boolean>()
    val progressBarUserTags: LiveData<Boolean>
        get() = _progressBarUserTags

    private val _progressBarAllTags = MutableLiveData<Boolean>()
    val progressBarAllTags: LiveData<Boolean>
        get() = _progressBarAllTags

    private val _userTagsList =
        MutableLiveData<ArrayList<UserTagData>>().apply { value = arrayListOf() }
    val userTagsList: LiveData<ArrayList<UserTagData>>
        get() = _userTagsList

    private val _eventUserTagsListUpdate = MutableLiveData<Boolean>()
    val eventUserTagsListUpdate: LiveData<Boolean>
        get() = _eventUserTagsListUpdate

    private val _userTagsRecyclerVisible = MutableLiveData<Boolean>()
    val userTagsRecyclerVisible: LiveData<Boolean>
        get() = _userTagsRecyclerVisible

    private val _userTagsTextListEmptyVisible = MutableLiveData<Boolean>()
    val userTagsTextListEmptyVisible: LiveData<Boolean>
        get() = _userTagsTextListEmptyVisible

    init {
        statusLoading()
        getUserTags()
        getAllTags()
        addAllTagsListener()
    }

    private fun getUserTags() {
        viewModelScope.launch {
            _progressBarUserTags.postValue(true)
            tagRepository.getUserTags().collect { tags ->
                getAllTags()
                _userTagsList.value?.clear()
                tags.forEach { _userTagsList.value?.add(it) }
                checkForEmptyUserTagsList()
                _eventUserTagsListUpdate.postValue(true)
                _progressBarUserTags.postValue(false)
            }
        }
    }

    fun removeUserTag(tagId: String) {
        viewModelScope.launch {
            tagRepository.removeUserTag(tagId)
        }
    }

    private fun checkForEmptyUserTagsList() {
        viewModelScope.launch {
            if (userTagsList.value.isNullOrEmpty()) {
                _userTagsRecyclerVisible.postValue(false)
                _userTagsTextListEmptyVisible.postValue(true)
            } else {
                _userTagsRecyclerVisible.postValue(true)
                _userTagsTextListEmptyVisible.postValue(false)
            }
        }
    }

    private fun getAllTags() {
        externalScope.launch {
            tagRepository.getAllUsedTags().collect {
                allTagsPagingAdapter.submitData(it)
            }
        }
    }

    private fun addAllTagsListener() {
        allTagsPagingAdapter.setOnItemClickListener {
            externalScope.launch {
                if (it.used!!) {
                    val tag = UserTagData(
                        id = it.id,
                        tagName = it.tagName,
                    )
                    tagRepository.addTagForUser(tag)
                } else {
                    removeUserTag(it.id!!)
                }
            }
        }
    }

    fun getFoundTags(text: String?) {
        viewModelScope.launch {
            if (!text.isNullOrEmpty()) {
                tagRepository.getFoundUsedTags(text).collect {
                    allTagsPagingAdapter.submitData(it)
                }
            } else {
                getAllTags()
            }
        }
    }

    private fun statusLoading() {
        externalScope.launch()
        {
            allTagsPagingAdapter.loadStateFlow.collectLatest { loadState ->
                loadingPosts(loadState.source.refresh is LoadState.Loading)
            }
        }
    }

    fun loadingPosts(boolean: Boolean) {
        _progressBarAllTags.postValue(boolean)
    }

}