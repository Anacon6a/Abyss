package com.example.abyss

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import bindViewModel
import com.example.abyss.adapters.PostPagingAdapter
import com.example.abyss.model.pagingsource.PostForProfileFirestorePagingSource
import com.example.abyss.model.repository.auth.AuthRepositoryFirebase
import com.example.abyss.model.repository.auth.AuthRepository
import com.example.abyss.model.repository.post.PostRepository
import com.example.abyss.model.repository.post.PostRepositoryFirestore
import com.example.abyss.model.repository.user.UserRepository
import com.example.abyss.model.repository.user.UserRepositoryFirestore
import com.example.abyss.ui.GeneralViewModelFactory
import com.example.abyss.ui.first.FirstViewModelFactory
import com.example.abyss.ui.auth.login.LoginViewModelFactory
import com.example.abyss.ui.auth.registration.RegistrationViewModelFactory
import com.example.abyss.ui.posts.addpost.AddPostViewModel
import com.example.abyss.ui.profile.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.direct
import org.kodein.di.generic.*
import timber.log.Timber

class AbyssApplication : Application(), KodeinAware {

    override val kodein = Kodein.lazy {
        import(androidXModule(this@AbyssApplication))

// Firebase
        bind<FirebaseAuth>() with provider { FirebaseAuth.getInstance() }
        bind<FirebaseDatabase>() with provider { FirebaseDatabase.getInstance() }
        bind<FirebaseStorage>() with provider { FirebaseStorage.getInstance() }
        bind<FirebaseFirestore>() with provider { FirebaseFirestore.getInstance() }
// Репозитории
        bind<AuthRepository>() with singleton {
            AuthRepositoryFirebase(instance(), instance(), instance())
        }
        bind<PostRepository>() with singleton {
            PostRepositoryFirestore(instance(), instance(), instance(), instance(), instance())
        }
        bind<UserRepository>() with provider {
            UserRepositoryFirestore(instance(), instance(), instance(), instance(), instance())
        }

// ViewModelFactory
        bind() from provider { FirstViewModelFactory(instance(), instance()) }
        bind() from provider { LoginViewModelFactory(instance(), instance()) }
        bind() from provider { RegistrationViewModelFactory(instance(), instance(), instance()) }

        bind<ViewModelProvider.Factory>() with singleton { GeneralViewModelFactory(kodein.direct) }

// Корутины
        bind<CoroutineScope>() with singleton { MainScope() }
        bind<CoroutineDispatcher>() with singleton { Dispatchers.IO }
        bind<CoroutineDispatcher>(tag = "default") with singleton { Dispatchers.Default }

// ViewModel
        bindViewModel<ProfileViewModel>() with singleton {
            ProfileViewModel(instance(), instance(), instance(), instance(), instance(), instance())
        }
        bindViewModel<AddPostViewModel>() with provider {
            AddPostViewModel(instance(), instance(), instance())
        }
// Adapter
        bind<PostPagingAdapter>() with provider { PostPagingAdapter() }
// PagingSource
       bind() from provider { PostForProfileFirestorePagingSource(instance(), instance()) }

    }

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
    }
}