package com.example.abyss.model.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import io.reactivex.rxjava3.core.Completable
import timber.log.Timber

class FirebaseSource {

    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firebaseDatabase: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }

    fun login(email: String, password: String) = Completable.create { emitter ->
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (!emitter.isDisposed) {
                if (it.isSuccessful)
                    emitter.onComplete()
                else
                    emitter.onError(it.exception!!)
            }
        }
    }

    fun register(email: String, password: String) = Completable.create { emitter ->
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (!emitter.isDisposed) {
                if (it.isSuccessful)
                    emitter.onComplete()
                else
                    emitter.onError(it.exception!!)
            }
        }
    }

    fun logout() = firebaseAuth.signOut()

    fun currentUser() = firebaseAuth.currentUser

    fun saveUserToFirebaseDatabase(
        username: String,
        email: String,
        password: String,
        profileImageUrl: String
    ) {
        val uid = firebaseAuth.uid ?: ""
//        создает с указанным id
        val ref = FirebaseDatabase.getInstance().getReference("users").child(uid);
        Timber.i("id должно быть "+uid)
        val user = UserData2( username, email)

        ref.setValue(user)
//            .addOnSuccessListener {
//                Log.d(TAG, "Finally we saved the user to Firebase Database")
//            }
//            .addOnFailureListener {
//                Log.d(TAG, "Failed to set value to database: ${it.message}")
//            }
    }

}

class UserData2(
//    var userID: String,
    var userName: String,
    var email: String,
)