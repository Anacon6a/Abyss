package com.example.abyss.model.repository

import com.google.firebase.FirebaseException
import com.google.firebase.database.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


//разовое получение данных
suspend fun Query.awaitsSingle(): DataSnapshot =
    suspendCancellableCoroutine { cancellableContinuation ->

        val listener = object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                try {

                    cancellableContinuation.resume(snapshot)
                } catch (exception: Exception) {

                    cancellableContinuation.resumeWithException(exception)
                }
            }

            override fun onCancelled(error: DatabaseError) {

                val exception = when (error.toException()) {
                    is FirebaseException -> error.toException()
                    else -> Exception("Запрос Firebase для $this был отменен")
                }
                cancellableContinuation.resumeWithException(exception)
            }
        }

        cancellableContinuation.invokeOnCancellation { this.removeEventListener(listener) }
        this.addListenerForSingleValueEvent(listener)
    }

// наблюдение за изменениями данных
@ExperimentalCoroutinesApi
fun Query.observeValue(): Flow<DataSnapshot?> =
    callbackFlow {

        val listener = object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                offer(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }

        }
        addValueEventListener(listener)
        awaitClose { removeEventListener(listener) }
    }




