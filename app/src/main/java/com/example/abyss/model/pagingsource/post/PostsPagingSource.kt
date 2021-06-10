package com.example.abyss.model.pagingsource.post

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.abyss.model.data.PostData
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import timber.log.Timber


class
PostsPagingSource(
    private val query: Query,
) : PagingSource<QuerySnapshot, PostData>() {

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, PostData> {
        return try {

            //ссылка на первые 10 элементов
            val currentPage = params.key ?: query.limit(20).get().await()
            if (!currentPage.isEmpty) {
                //ссылка на последний элемент
                val lastVisiblePost = currentPage.documents[currentPage.size() - 1]
                //сылка на следующие 10
                val nextPage = query.startAfter(lastVisiblePost).limit(20).get().await()

                LoadResult.Page(
                    data = currentPage.toObjects(PostData::class.java),
                    prevKey = null,
                    nextKey = nextPage
                )
            } else {
                LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null
                )
            }


        } catch (e: Exception) {
            Timber.e(e)
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<QuerySnapshot, PostData>): QuerySnapshot? {
        return null
    }
}