package com.example.abyss.model.pagingsource.comment

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.abyss.model.data.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class CommentsPagingSource(
    private val query: Query,
    private val ioDispatcher: CoroutineDispatcher,
    private val externalScope: CoroutineScope,
    private val firestore: FirebaseFirestore,
    private val uid: String
) : PagingSource<QuerySnapshot, UserCommentData>() {

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, UserCommentData> {
        return try {
            var userCommentDataList = emptyList<UserCommentData>()
            var nextPage: QuerySnapshot? = null

            val currentPage = params.key ?: query.limit(30).get().await()
            if (!currentPage.isEmpty) {
                userCommentDataList = getUserComments(currentPage)
                val lastVisiblePost = currentPage.documents[currentPage.size() - 1]
                nextPage = query.startAfter(lastVisiblePost).limit(30).get().await()
            }

            LoadResult.Page(
                data = userCommentDataList,
                prevKey = null,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            Timber.e(e)
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<QuerySnapshot, UserCommentData>): QuerySnapshot? {
        return null
    }

    suspend fun getUserComments(commentsSnap: QuerySnapshot): List<UserCommentData> {
        val comments = commentsSnap.toObjects(CommentData::class.java)
        val userComments = arrayListOf<UserCommentData>()
        externalScope.launch(ioDispatcher) {
            for (comm in comments) {
                try {
                    val user =
                        firestore.collection("users").document(comm.contentMakerUid!!).get().await()
                            .toObject(UserData::class.java)!!
                    val uC = UserCommentData(
                        comm.commentText!!,
                        comm.date!!,
                        comm.id!!,
                        comm.uid!!,
                        comm.postId!!,
                        user.userName!!,
                        user.profileImageUrl,
                        comm.uid == uid
                    )
                    userComments.add(uC)
                } catch (e: java.lang.Exception) {
                    Timber.e(e)
                }
            }
        }.join()
        return userComments
    }
}