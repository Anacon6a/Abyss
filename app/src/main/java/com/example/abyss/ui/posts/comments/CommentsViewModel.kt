package com.example.abyss.ui.posts.comments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import com.example.abyss.adapters.comment.CommentPagingAdapter
import com.example.abyss.model.data.PostData
import com.example.abyss.model.repository.comment.CommentRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CommentsViewModel(
    private val externalScope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher,
    private val commentRepository: CommentRepository,
    val commentPagingAdapter: CommentPagingAdapter
) : ViewModel() {

    private val postData = MutableLiveData<PostData>()

    val textComment = MutableLiveData<String>()

    private val _progressBarComments = MutableLiveData<Boolean>()
    val progressBarComments: LiveData<Boolean>
        get() = _progressBarComments

    init {
        statusLoading()
    }

    fun insertData(post: PostData, text: String?) {
        if (postData.value == null) {
            postData.value = post
            if (!text.isNullOrEmpty()) {
                textComment.value = text!!
            }
            getAllComment()
            editOrDeleteListener()
        }
    }

    private fun getAllComment() {
        viewModelScope.launch {
            postData.value?.let {
                commentRepository.getAllComment(postData.value!!.id!!, postData.value!!.uid!!)
                    .collect {
                        commentPagingAdapter.submitData(it)
                    }
            }
        }
    }

    private fun editOrDeleteListener(){
        externalScope.launch(ioDispatcher) {
            commentRepository.editOrDeleteListener(postData.value!!.id!!, postData.value!!.uid!!).collect {
                getAllComment()
            }
        }
    }

    fun addComment() {
        viewModelScope.launch {
            postData.value?.let {
                val text = textComment.value
                textComment.postValue("")
                commentRepository.createComment(
                    text!!,
                    postData.value!!.id!!,
                    postData.value!!.uid!!
                )
                getAllComment()
            }
        }
    }

    private fun statusLoading() {
        externalScope.launch()
        {
            commentPagingAdapter.loadStateFlow.collectLatest { loadState ->
                loadingComments(loadState.source.refresh is LoadState.Loading)
            }
        }
    }

    fun loadingComments(boolean: Boolean) {
        _progressBarComments.postValue(boolean)
    }

    fun refresh() {
        viewModelScope.launch {
            getAllComment()
        }
    }
}