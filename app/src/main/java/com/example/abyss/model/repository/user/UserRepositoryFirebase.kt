package com.example.abyss.model.repository.user


import com.example.abyss.model.data.entity.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception

class UserRepositoryFirebase(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDatabase: FirebaseDatabase,
    private val firebaseStorage: FirebaseStorage,
    private val ioDispatcher: CoroutineDispatcher,
    private val externalScope: CoroutineScope,

    ) : UserRepository {

    override suspend fun CreateUser(user: UserData) {
        externalScope.launch(ioDispatcher) {
            try {

                val uid = firebaseAuth.uid ?: ""
//        создает с указанным id
                val ref = firebaseDatabase.getReference("users").child(uid);
                if (user.profileImageUrl != "") {

                }
                ref.setValue(user)
                Timber.i("Пользователь добавлен")
            } catch (e: Exception) {

                Timber.i("Ошибка добавления пользователя")
            }
        }
    }

}