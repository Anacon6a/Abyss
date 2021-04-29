package com.example.abyss.model.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.abyss.model.data.PostData
import com.example.abyss.model.repository.auth.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import timber.log.Timber


class PostForProfileFirestorePagingSource(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : PagingSource<QuerySnapshot, PostData>() {

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, PostData> {
        return try {

            val uid = firebaseAuth.uid.toString()
            val query = firestore.collection("posts").document("uid").collection(uid)
                .orderBy("date", Query.Direction.DESCENDING)

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

//    override fun getRefreshKey(state: PagingState<Int, User>): Int? {
//        // Try to find the page key of the closest page to anchorPosition, from
//        // either the prevKey or the nextKey, but you need to handle nullability
//        // here:
//        //  * prevKey == null -> anchorPage is the first page.
//        //  * nextKey == null -> anchorPage is the last page.
//        //  * both prevKey and nextKey null -> anchorPage is the initial page, so
//        //    just return null.
//        return state.anchorPosition?.let { anchorPosition ->
//            val anchorPage = state.closestPageToPosition(anchorPosition)
//            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
//        }
//    }
//}
}