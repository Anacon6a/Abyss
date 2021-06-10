package com.example.abyss.ui.posts.comments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.abyss.model.data.PostData
import com.example.abyss.model.data.UserCommentData
import com.example.abyss.model.repository.comment.CommentRepository
import com.example.abyss.model.repository.post.PostRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ModalBottomSheetForCommentViewModel(
    private val externalScope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher,
    private val commentRepository: CommentRepository,
): ViewModel() {

    fun deleteComment(comment: UserCommentData, contentMakerId: String) {
        externalScope.launch(ioDispatcher) {
            commentRepository.deleteComment(comment, contentMakerId)
        }
    }
}