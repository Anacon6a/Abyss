package com.example.abyss.ui.posts.comments.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.abyss.model.data.UserCommentData
import com.example.abyss.model.repository.comment.CommentRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DialogForEditCommentViewModel(
    private val externalScope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher,
    private val commentRepository: CommentRepository,
) : ViewModel() {

    private val _eventCommentEdited = MutableLiveData<Boolean>()
    val eventCommentEdited: LiveData<Boolean>
        get() = _eventCommentEdited

    val textComment = MutableLiveData<String>()

    private val userCommentData = MutableLiveData<UserCommentData>()
    private val contentMakerId = MutableLiveData<String>()

    fun insertData(comment: UserCommentData, contentMakerId: String) {
        if (userCommentData.value == null) {
            userCommentData.value = comment
            textComment.value = comment.commentText
            this.contentMakerId.value = contentMakerId
        }
    }

     fun editComment() {
        externalScope.launch(ioDispatcher) {
            commentRepository.editComment(
                textComment.value!!,
                userCommentData.value!!,
                contentMakerId.value!!
            )
            _eventCommentEdited.postValue(true)
        }
    }
}