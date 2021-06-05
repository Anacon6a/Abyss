package com.example.abyss.model.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.abyss.model.data.TagData
import com.example.abyss.model.data.UserData
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class TagsPagingSource(
    private val query: com.google.firebase.firestore.Query,
) : PagingSource<QuerySnapshot, TagData>() {

    override fun getRefreshKey(state: PagingState<QuerySnapshot, TagData>): QuerySnapshot? {
        return null
    }

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, TagData> {
        return try {

            //ссылка на первые 10 элементов
            val currentPage = params.key ?: query.limit(30).get().await()
            if (!currentPage.isEmpty) {
                //ссылка на последний элемент
                val lastVisiblePost = currentPage.documents[currentPage.size() - 1]
                //сылка на следующие 10
                val nextPage = query.startAfter(lastVisiblePost).limit(30).get().await()

                LoadResult.Page(
                    data = currentPage.toObjects(TagData::class.java),
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
            LoadResult.Error(e)
        }
    }
}
