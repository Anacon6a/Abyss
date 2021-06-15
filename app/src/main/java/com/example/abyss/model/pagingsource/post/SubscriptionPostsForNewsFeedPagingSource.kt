package com.example.abyss.model.pagingsource.post

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.abyss.model.data.PostData
import com.example.abyss.model.data.SubscriptionData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import kotlin.collections.ArrayList

class SubscriptionPostsForNewsFeedPagingSource(
    private val querySubscription: Query,
    private val queryPosts: FirebaseFirestore,
    private val ioDispatcher: CoroutineDispatcher,
    private val externalScope: CoroutineScope,
) : PagingSource<Int, PostData>() {

    private var posts: MutableList<PostData> = ArrayList<PostData>()
    private val sizePage = 10

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PostData> {
        return try {
            if (posts.size == 0) {
                GetPosts()
            }
            val currentPage = params.key ?: 0
            var nextPage = currentPage + sizePage

            val visiblePosts =
                when {
                    posts.size < nextPage -> {
                        nextPage = posts.size
                        posts.subList(currentPage, nextPage)
                    }
                    else -> {
                        posts.subList(currentPage, nextPage)
                    }
                }

            LoadResult.Page(
                data = visiblePosts,
                prevKey = null,
                nextKey = if  (nextPage == posts.size) null else nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, PostData>): Int? {
        return null
    }

   private suspend fun GetPosts() {

        val subSnap = querySubscription.get().await()

        externalScope.launch(ioDispatcher) {
            val d = ArrayList<Deferred<Unit?>>()
            for (sub in subSnap) {
                d.add(async {
                    val postsSubscription = queryPosts.collection("users")
                        .document(sub.toObject<SubscriptionData>().uidSubscription!!)
                        .collection("posts").get().await()
                    postsSubscription?.let {

                        for (post in it) {
                            val p = post.toObject<PostData>()
                            posts.add(p)
                        }
                    }
                })
            }
            d.awaitAll()
            posts.sortByDescending { it.date }
        }.join()
    }
}
