package com.example.abyss.model.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.abyss.model.data.PostData
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await


class PostForProfileFirestorePagingSource(
    private val query: Query,
) : PagingSource<QuerySnapshot, PostData>() {

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, PostData> {
        return try {

            //ссылка на первые 10 элементов
            val currentPage = params.key ?: query.limit(40).get().await()
            //ссылка на последний элемент
            val lastVisiblePost = currentPage.documents[currentPage.size() - 1]
            //сылка на следующие 10
            val nextPage = query.startAfter(lastVisiblePost).limit(10).get().await()

            //удачная загрузка
            LoadResult.Page(
                data = currentPage.toObjects(PostData::class.java),
                prevKey = null,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<QuerySnapshot, PostData>): QuerySnapshot? {
        return null
    }
}