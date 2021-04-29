package com.example.abyss.ui.posts.post

import androidx.lifecycle.ViewModel
import com.example.abyss.model.repository.post.PostRepositoryFirestore

class PostViewModel(
    val repositoryFirestore: PostRepositoryFirestore
): ViewModel() {

    init {

    }
}