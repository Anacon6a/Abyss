package com.example.abyss.model.pagingsource.post

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.abyss.model.data.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class SavedPostsPagingSource(
    private val query: Query,
    private val ioDispatcher: CoroutineDispatcher,
    private val externalScope: CoroutineScope,
    private val firestore: FirebaseFirestore,
) : PagingSource<QuerySnapshot, PostData>() {

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, PostData> {
        return try {
            var savedPostDataList = emptyList<PostData>()
            var nextPage: QuerySnapshot? = null

            val currentPage = params.key ?: query.limit(30).get().await()
            if (!currentPage.isEmpty) {
                savedPostDataList = getPosts(currentPage)
                val lastVisiblePost = currentPage.documents[currentPage.size() - 1]
                nextPage = query.startAfter(lastVisiblePost).limit(30).get().await()
            }
            LoadResult.Page(
                data = savedPostDataList,
                prevKey = null,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            Timber.e(e)
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<QuerySnapshot, PostData>): QuerySnapshot? {
        return null
    }

    private suspend fun getPosts(savedPostsSnap: QuerySnapshot): List<PostData> {
        val savedPosts = savedPostsSnap.toObjects(SavedPostData::class.java)
        val posts = arrayListOf<PostData>()
        externalScope.launch(ioDispatcher) {
            for (savedPost in savedPosts) {
                try {
                    val postSnap =
                        firestore.collection("users").document(savedPost.uid!!).collection("posts")
                            .document(savedPost.postId!!).get().await()
                                if (postSnap != null) {
                                   val post = postSnap.toObject(PostData::class.java)
                                    posts.add(post!!)
                                }
                } catch (e: java.lang.Exception) {
                    Timber.e(e)
                }
            }
        }.join()
        return posts
    }
}