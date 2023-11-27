package com.matiasmandelbaum.alejandriaapp.ui.subscription

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import com.matiasmandelbaum.alejandriaapp.R
import com.matiasmandelbaum.alejandriaapp.common.auth.AuthManager
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.data.util.subscriptionstatus.SubscriptionStatus
import com.matiasmandelbaum.alejandriaapp.databinding.FragmentSubscriptionListBinding
import com.matiasmandelbaum.alejandriaapp.domain.model.subscription.Subscription
import com.matiasmandelbaum.alejandriaapp.ui.subscription.model.SubscriptionUser
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SubscriptionListFragment : Fragment() {

    private lateinit var binding: FragmentSubscriptionListBinding

    private val viewModel: SubscriptionListViewModel by activityViewModels()

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
        initUI()
    }

    private fun initUI() {
        setupObservers()
        setupListeners()
    }


    private fun setupObservers() {
        AuthManager.authStateLiveData.observe(viewLifecycleOwner) { handleAuthState(it) }

        with(viewModel) {
            subscription.observe(viewLifecycleOwner) { handleSubscriptionResult(it) }
            subscriptionExists.observe(viewLifecycleOwner) {
                when (it) {
                    is Result.Success -> {
                        handleSubscriptionExistsResult(it)
                    }

                    is Result.Error -> showErrorOnGettingSubscriptionMessage()
                    is Result.Loading -> Unit

                }
            }
            subscriptionUser.observe(viewLifecycleOwner) {
                it?.let { handleUserResult(it) }
            }
        }

    }

    private fun setupListeners() {
        binding.basicPlanButton.setOnClickListener {
            AuthManager.getCurrentUser()?.email?.let { viewModel.createSubscription(it) }
        }
    }

    private fun handleSubscriptionResult(result: Result<Subscription>) {
        with(binding) {
            progressBar.visibility = if (result is Result.Loading) View.VISIBLE else View.GONE
            basicPlanButton.visibility =
                if (result is Result.Success) View.VISIBLE else View.GONE
        }
    }

    private fun handleSubscriptionExistsResult(result: Result<Subscription>) {
        with(binding) {
            (result as? Result.Success)?.let { successResult ->
                when (successResult.data.status) {
                    SubscriptionStatus.Pending.statusString -> {
                        basicPlanButton.setOnClickListener {
                            viewModel.continueSubscription(successResult.data.id)
                        }
                    }

                    SubscriptionStatus.Authorized.statusString -> handleAuthorizedSubscription()
                }
            }
        }
    }

    private fun handleAuthorizedSubscription() {
        with(binding) {
            basicPlanButton.visibility = View.GONE
            showSuccessfulSubscriptionSnackbar()
        }
    }

    private fun showSuccessfulSubscriptionSnackbar() {
        Snackbar.make(
            requireView(),
            getString(R.string.suscripcion_exitosa),
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun handleUserResult(result: Result<SubscriptionUser>) {
        when (result) {
            is Result.Success -> {
                result.data.takeIf { it.subscriptionId.isNotBlank() }?.let {
                    viewModel.fetchSubscription(it.subscriptionId)
                }
            }

            is Result.Error -> showErrorOnGettingUserMessage()
            is Result.Loading -> Unit
        }
    }

    private fun handleAuthState(user: FirebaseUser?) {
        with(binding.basicPlanButton) {
            user?.let {
                viewModel.getUserById(it.uid)
            } ?: run {
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

    private fun showErrorOnGettingSubscriptionMessage() {
        Snackbar.make(
            requireView(),
            getString(R.string.error_al_traer_suscription), Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun showErrorOnGettingUserMessage() {
        Snackbar.make(
            requireView(),
            getString(R.string.error_al_traer_usuario), Snackbar.LENGTH_SHORT
        ).show()
    }

}
