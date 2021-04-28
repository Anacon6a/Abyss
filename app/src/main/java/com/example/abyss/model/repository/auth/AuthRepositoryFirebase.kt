package com.example.abyss.model.repository.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import timber.log.Timber


class AuthRepositoryFirebase(
    private val firebaseAuth: FirebaseAuth,
    private val ioDispatcher: CoroutineDispatcher,
    private val externalScope: CoroutineScope

    ) : AuthRepository {


    override suspend fun login(email: String, password: String): String {
        var request = ""
        //Если работа актуальна пока приложение открыто и работа должна пережить жизненный цикл вызывающего
        externalScope.launch(ioDispatcher) {
            try {

                firebaseAuth
                    .signInWithEmailAndPassword(email, password)
                    .await()
            } catch (e: Exception) {

                request = when (e.message.toString()) {
                   "The email address is badly formatted." -> "Адрес электронной почты имеет неправильный формат."
                    "There is no user record corresponding to this identifier. The user may have been deleted." -> "Пользователь с указанным email не найден."
                    "The password is invalid or the user does not have a password." -> "Неверно введен пароль."
                    else -> "Ошибка авторизации."
                }
                Timber.i("Ошибка авторизации: $e")
            }
        }
            .join()

        return request
    }

    override suspend fun register(email: String, password: String): String {
        var request = ""
        externalScope.async(ioDispatcher) {
             try {

                firebaseAuth
                    .createUserWithEmailAndPassword(email, password)
                    .await()
                 Timber.i("Регистрация выполнена")
            } catch (e: Exception) {

             request = when (e.message.toString()) {
                    "The email address is badly formatted." -> "Адрес электронной почты имеет неправильный формат."
                    "The given password is invalid. [ Password should be at least 6 characters ]" -> "Пароль должен содержать не меньше 6 символов."
                    "The email address is already in use by another account." -> "Адрес электронной почты уже используется другим пользователем."
                    else -> "Ошибка регистрации"
                }
                 Timber.i("Ошибка геристрации: $e")
            }
        }
            .join()
        return request
    }

    override suspend fun currentUser(): FirebaseUser? = firebaseAuth.currentUser

    override suspend fun logout() = firebaseAuth.signOut()

}
