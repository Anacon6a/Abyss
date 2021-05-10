package com.example.abyss.ui.auth.login

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.abyss.databinding.FragmentLoginBinding
import com.example.abyss.utils.HidingNavigationBar
import org.kodein.di.android.x.kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import timber.log.Timber


class LoginFragment : Fragment(), KodeinAware {

    override val kodein by kodein()
    private val factory : LoginViewModelFactory by instance()

    private lateinit var viewModel: LoginViewModel

    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProvider(this, factory).get(LoginViewModel::class.java)
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.loginViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        (activity as HidingNavigationBar).hideNavigationBar(true)

        Subscription()


        return binding.root
    }


    private fun Subscription() {
        viewModel.eventGoToRegistration.observe(viewLifecycleOwner, { event ->
            if (event) onRegistdration()
        })
        viewModel.eventLoginCompleted.observe(viewLifecycleOwner, { event ->
            if (event) loginCompleted()
        })
    }

    private fun onRegistdration() {
        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegistrationFragment())
    }

    private fun loginCompleted() {
        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToNewsFeedFragment())
            //parentFragment?.findNavController()?.navigate(AuthFragmentDirections.actionAuthFragmentToHomeFragment())
    }



    override fun onAttach(context: Context) {
        super.onAttach(context)
        Timber.i("фрагмент связан с активити")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("создание начального фрагмента")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.i(" сразу после onCreateView()возврата, но до восстановления любого сохраненного состояния в представлении" )
    }

    override fun onStart() {
        super.onStart()
        Timber.i(" фрагмент становится видимым")
    }
    override fun onResume() {
        super.onResume()
        Timber.i("  фрагмент получает фокус пользователя")
    }
    override fun onPause() {
        super.onPause()
        Timber.i("теряет фокус пользователя")
    }
    override fun onStop() {
        super.onStop()
        Timber.i("фрагмент больше не отображается на экране")
    }
    override fun onDestroyView() {
        super.onDestroyView()
        Timber.i("представление фрагмента больше не требуется, для очистки ресурсов, связанных с этим представлением")
    }
    override fun onDetach() {
        super.onDetach()
        Timber.i("onAttach called")
    }
}