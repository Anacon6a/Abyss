package com.example.abyss.ui.home.newsfeed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import com.example.abyss.adapters.home.newsfeed.NewsFeedAllTagsPagingAdapter
import com.example.abyss.model.data.TagData
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

    private val _userTagsList = MutableLiveData<List<TagData>>().apply { value = emptyList() }
    val userTagsList: LiveData<List<TagData>>
    get() = _userTagsList

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
            tagRepository.getUserTags().collect {
                _userTagsList.value = it
                checkForEmptyUserTagsList()
                _progressBarUserTags.postValue(false)
            }
        }
    }

     fun removeUserTag(tagId: String){
        viewModelScope.launch {
            tagRepository.removeUserTag(tagId)
        }
    }

    private fun checkForEmptyUserTagsList(){
        viewModelScope.launch {
            if (userTagsList.value.isNullOrEmpty()) {
                _userTagsRecyclerVisible.postValue(false)
                _userTagsTextListEmptyVisible.postValue( true)
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
                    val tag = TagData(id = it.id, tagName = it.tagName, tagTextInsensitive = it.tagTextInsensitive, numberOfUses = it.numberOfUses)
                    tagRepository.addTagForUser(tag)
                } else {
                    removeUserTag(it.id!!)
                }
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