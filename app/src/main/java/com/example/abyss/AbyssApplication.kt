package com.example.abyss

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.abyss.adapters.ProfilePostsRecyclerViewAdapter
import com.example.abyss.model.repository.auth.AuthRepositoryFirebase
import com.example.abyss.model.repository.auth.AuthRepository
import com.example.abyss.model.repository.user.UserRepository
import com.example.abyss.model.repository.user.UserRepositoryFirebase
import com.example.abyss.ui.first.FirstViewModelFactory
import com.example.abyss.ui.auth.login.LoginViewModelFactory
import com.example.abyss.ui.auth.registration.RegistrationViewModelFactory
import com.example.abyss.ui.posts.AddPostViewModel
import com.example.abyss.ui.profile.ProfileViewModel
import com.example.abyss.ui.profile.ProfileViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.*
import timber.log.Timber

class AbyssApplication : Application(), KodeinAware {

    override val kodein = Kodein.lazy {
        import(androidXModule(this@AbyssApplication))
// firebase, каждый раз новая
        bind<FirebaseAuth>() with provider { FirebaseAuth.getInstance() }
        bind<FirebaseDatabase>() with provider { FirebaseDatabase.getInstance() }
        bind<FirebaseStorage>() with provider { FirebaseStorage.getInstance() }
// репозитории, зависимости будет переданы единожды
        bind<AuthRepository>() with singleton {
            AuthRepositoryFirebase(instance(), instance(), instance())
        }
        bind<UserRepository>() with singleton {
            UserRepositoryFirebase(instance(), instance(), instance(), instance(), instance())
        }
// фабрики
        bind() from provider { FirstViewModelFactory(instance(), instance()) }
        bind() from provider { LoginViewModelFactory(instance(), instance()) }
        bind() from provider { RegistrationViewModelFactory(instance(), instance(), instance()) }
        bind() from  provider { ProfileViewModelFactory(instance()) }
//        bind<ViewModelProvider.Factory>() with singleton { ProfileViewModelFactory(instance()) }
// корутины
        bind<CoroutineScope>() with singleton { MainScope() }
        bind<CoroutineDispatcher>() with singleton { Dispatchers.IO }
        bind<CoroutineDispatcher>(tag = "default") with singleton { Dispatchers.Default }
// ViewModel
        bind<ViewModel>(tag = ProfileViewModel::class.java) with singleton { ProfileViewModel(instance()) }
    }

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
    }
}