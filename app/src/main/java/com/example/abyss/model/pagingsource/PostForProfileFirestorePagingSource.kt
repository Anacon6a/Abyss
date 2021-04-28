package com.example.abyss.model.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.abyss.model.data.entity.PostData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await


class PostForProfileFirestorePagingSource(
    private val queryPost: Query
    ) : PagingSource<QuerySnapshot, PostData>() {

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, PostData> {
        return try {
            //ссылка на первые 10 элементов
            val currentPage = params.key ?: queryPost.limit(10).get().await()
            //ссылка на последний элемент
            val lastVisiblePost = currentPage.documents[currentPage.size() - 1]
            //сылка на следующие 10
            val nextPage = queryPost.startAfter(lastVisiblePost).limit(10).get().await()
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