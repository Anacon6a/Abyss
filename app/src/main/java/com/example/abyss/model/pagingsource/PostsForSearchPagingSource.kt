package com.example.abyss.model.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.abyss.model.data.PostData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class PostsForSearchPagingSource(
    private val getTagsQuery: Query,
    private val firestore: FirebaseFirestore,
    private val ioDispatcher: CoroutineDispatcher,
    private val externalScope: CoroutineScope,
) : PagingSource<QuerySnapshot, PostData>() {

    private var current10Tags = arrayListOf<String>()
    private var nextTagPage: QuerySnapshot? =null

    // orderby
    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, PostData> {
        return try {

            var postDataList = emptyList<PostData>()
            var nextPage: QuerySnapshot? = null

            if (nextTagPage == null) {
                get10Tags()
            }

            if (!current10Tags.isNullOrEmpty()) {
                val currentPage = getCurrentPostsSnap(params.key)

                if (!currentPage.isEmpty) {
                    postDataList = currentPage.toObjects(PostData::class.java)

                    val lastVisiblePost = currentPage.documents[currentPage.size() - 1]

                    nextPage =
                        firestore.collectionGroup("posts")
                            .whereArrayContainsAny("tags", current10Tags)
                            .orderBy("date", Query.Direction.DESCENDING).limit(40)
                            .startAfter(lastVisiblePost).get().await()
                }
            }

            LoadResult.Page(
                data = postDataList,
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

    private suspend fun getCurrentPostsSnap(snap: QuerySnapshot?): QuerySnapshot {
        val currentPostsSnap =
            snap ?: firestore.collectionGroup("posts")
                .whereArrayContainsAny("tags", current10Tags)
                .orderBy("date", Query.Direction.DESCENDING).limit(40).get().await()

        if (currentPostsSnap.isEmpty) {
            get10Tags()
            if (current10Tags.isNotEmpty()) {
                return getCurrentPostsSnap(
                    firestore.collectionGroup("posts")
                        .whereArrayContainsAny("tags", current10Tags).limit(40).get().await()
                )
            }
        }
        return currentPostsSnap
    }

    private suspend fun get10Tags() {
        externalScope.launch(ioDispatcher) {
            val current10TagsSnap = nextTagPage?: getTagsQuery.limit(10).get().await()

            current10Tags.clear()

            if (!current10TagsSnap.isEmpty) {
                val lastTag = current10TagsSnap.documents[current10TagsSnap.size() - 1]
                    nextTagPage = getTagsQuery.startAfter(lastTag).limit(10).get().await()

                current10TagsSnap.forEach { t ->
                    t.getString("tagName")?.let { it ->
                        current10Tags.add(it)
                    }
                }
            }
        }.join()
    }
}