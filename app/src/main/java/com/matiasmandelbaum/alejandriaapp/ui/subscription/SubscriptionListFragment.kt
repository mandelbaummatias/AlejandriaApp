package com.matiasmandelbaum.alejandriaapp.ui.subscription

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import com.matiasmandelbaum.alejandriaapp.R
import com.matiasmandelbaum.alejandriaapp.common.auth.AuthManager
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.data.util.subscriptionstatus.SubscriptionStatus
import com.matiasmandelbaum.alejandriaapp.databinding.FragmentSubscriptionListBinding
import com.matiasmandelbaum.alejandriaapp.domain.model.subscription.Subscription
import com.matiasmandelbaum.alejandriaapp.ui.subscription.model.User
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "SubscriptionListFragment"

@AndroidEntryPoint
class SubscriptionListFragment : Fragment() {

    private lateinit var binding: FragmentSubscriptionListBinding
    //private val viewModel: SubscriptionListViewModel by activityViewModels()

    //tampoco influye que sea shared vm

    private val viewModel: SubscriptionListViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
     //   viewModel.resetUser()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSubscriptionListBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // initObservers()
        setupObservers()
        setupListeners()
        AuthManager.authStateLiveData.observe(viewLifecycleOwner) { handleAuthState(it) }
        Log.d(TAG, "ViewModel hash code: ${viewModel.hashCode()}")
    }


    private fun setupObservers() {
        Log.d(TAG, "setupObservers()")
        with(viewModel) {
            subscription.observe(viewLifecycleOwner) { handleSubscriptionResult(it) }
            subscriptionExists.observe(viewLifecycleOwner) {
                when (it) {
                    is Result.Success -> {
                        Log.d(TAG, "subExists SUCC: $it")
                        handleSubscriptionExistsResult(it)
                    }

                    is Result.Error -> Log.d(TAG, "subExists ERR $it")
                    is Result.Loading -> Log.d(TAG, "subExists LOAD $it")
                    is Result.Finished -> Log.d(TAG, "subExists FINI $it")
                }
            }
            user.observe(viewLifecycleOwner) {
                it?.let { handleUserResult(it) }
            }

            //data class sloo para json
            // -> luego otra capa VM . Listener que recibo de la data class -> info del back pura (NetworkModel)
            //instancia de vm con nueva información. ESE objeto / ref. file ES la info que consume
            //el componente de la v.

            /* caso crítico: fetch
            medio -> inter usuario - app. EVENTOS de adopcion de perro, sobre un objeto mutando

            a nivel padre -> modificación -> observer . Reduzco la cantidad de llamados
            en vez de ir obj por obj, "disparar" obs -> activity parte de observadores de ese evento
            cuestión de reducir interacción -> POR EVENTO, por ciclo de vida

            ... Otro caso: cambia mi obj. Otra parte de la aplicacion pre fetch tenga otro comportamiento,
            a nivel general... (ojo).

            delay -> pegarle a la api -> cambio la info?    ||  quien sabe . O no devuelve callback...pero si hago una llamada?
            repeat() -> llega -> qué pasa? || Cambiar estado al botón . Suscribirme...rechazo...confirmación


            Si espero la llamada..camb ia el evento final ...concurrencia

            * */


        }

    }

    private fun setupListeners() {
        binding.basicPlanSubscribeBtn.setOnClickListener {
            AuthManager.getCurrentUser()?.email?.let { viewModel.createSubscription(it) }
        }
    }

    private fun handleSubscriptionResult(result: Result<Subscription>) {
        with(binding) {
            progressBar.visibility = if (result is Result.Loading) View.VISIBLE else View.GONE
            basicPlanSubscribeBtn.visibility =
                if (result is Result.Success) View.VISIBLE else View.GONE
        }
    }

    private fun handleSubscriptionExistsResult(result: Result<Subscription>) {
        Log.d(TAG, "handleSubscriptionExistsResult")
        with(binding) {
            (result as? Result.Success)?.let { successResult ->
                when (successResult.data.status) {
                    SubscriptionStatus.Pending.statusString -> {
                        basicPlanSubscribeBtn.setOnClickListener {
                            viewModel.continueSubscription(successResult.data.id)
                        }
                    }

                    SubscriptionStatus.Authorized.statusString -> handleAuthorizedSubscription()
                }
            }
        }
    }

    private fun handleAuthorizedSubscription() {
        Log.d(TAG, "handleAuthorizedSubscription")
        with(binding) {
            basicPlanSubscribeBtn.visibility = View.GONE
            showSuccessfulSubscriptionSnackbar()
        }
    }

    private fun showSuccessfulSubscriptionSnackbar() {
        Log.d(TAG, "showSuccessfulSubscriptionSnackbar")
        Snackbar.make(
            requireView(),
            getString(R.string.suscripcion_exitosa),
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun handleUserResult(result: Result<User>) {
        Log.d(TAG, "handleUserResult")
        when (result) {
            is Result.Success -> {
                Log.d(TAG, "handleUser Success")
                result.data.takeIf { it.subscriptionId.isNotBlank() }?.let {
                    viewModel.fetchSubscription(it.subscriptionId)
                }
            } // ?: Unit
            is Result.Error -> Log.d(TAG, "error ${result.message}")
            is Result.Loading -> Log.d(TAG, "handleUser Loading")
            else -> Unit
        }
    }

    private fun handleAuthState(user: FirebaseUser?) {
        Log.d(TAG, "handleAuthState")
        with(binding.basicPlanSubscribeBtn) {
            user?.let {
                viewModel.getUserById(it.uid)
            } ?: run {
                Log.d(TAG, "user not logged: button visible")
                visibility = View.VISIBLE
                setOnClickListener {
                    Snackbar.make(
                        requireView(),
                        getString(R.string.solicitud_iniciar_sesion),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //  viewModel.resetUser() //esto no es
    }

    private fun handleLoading(isLoading: Boolean) {
        Log.d(TAG, "HANDLE loading")
        with(binding) {
            if (isLoading) {
                basicPlanSubscribeBtn.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
            } else {
                basicPlanSubscribeBtn.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun initObservers() {
        viewModel.subscription.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Success -> {
                    Log.d(TAG, "subscription is Result.Success")
                    handleLoading(false) //autorizado reserva
                }

                is Result.Loading -> handleLoading(true)
                is Result.Error -> handleLoading(false)
                else -> {}
            }
        }

        viewModel.subscriptionExists.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    Log.d(TAG, "subscriptionExists is Result.Success")
                    if (result.data.status == "pending") {
                        Log.d(TAG, "consegui usuario (sub pending)")
                        //si consiguió usuario al princi
                        binding.basicPlanSubscribeBtn.setOnClickListener {
                            viewModel.continueSubscription(result.data.id)

                        }
                    } else if (result.data.status == "authorized") {
                        Log.d(TAG, "authorized from subscriptionExists")
                        binding.basicPlanSubscribeBtn.visibility = View.GONE
                        showSuccessfulSubscriptionSnackbar()
                    }
                }

                is Result.Error -> {
                    Unit
                }

                else -> {
                    Unit
                }
            }
        }

        viewModel.user.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    Log.d(TAG, "user is Result.Success")
                    if (result.data.subscriptionId.isNotBlank()) {
                        Log.d(TAG, "is not blank ${result.data.subscriptionId}")
                        viewModel.fetchSubscription(result.data.subscriptionId)
                    } else {
                        Log.d(TAG, "is blank ${result.data.subscriptionId}")
                    }
                }

                is Result.Error -> {
                    Log.d(TAG, "error ${result.message}")
                }

                is Result.Finished -> Unit
                Result.Loading -> Unit
                else -> {}
            }
        }

        AuthManager.authStateLiveData.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                Log.d(TAG, "user logged $user")
                val userUid = AuthManager.getCurrentUser()?.uid
                userUid?.let {
                    viewModel.getUserById(it)
                }
            } else {
                Log.d(TAG, "user not logged: button visible")
                binding.basicPlanSubscribeBtn.visibility = View.VISIBLE
                binding.basicPlanSubscribeBtn.setOnClickListener {
                    Snackbar.make(
                        requireView(),
                        getString(R.string.solicitud_iniciar_sesion), Snackbar.LENGTH_SHORT
                    ).show()

                }
            }
        }

    }
}
