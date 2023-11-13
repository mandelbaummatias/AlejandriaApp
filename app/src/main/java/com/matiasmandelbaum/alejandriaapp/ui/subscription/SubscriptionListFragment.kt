package com.matiasmandelbaum.alejandriaapp.ui.subscription

import android.os.Bundle
import android.util.Log
import android.view.View
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
import com.matiasmandelbaum.alejandriaapp.ui.subscription.model.User
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "SubscriptionListFragment"

@AndroidEntryPoint
class SubscriptionListFragment : Fragment(R.layout.fragment_subscription_list) {

    private lateinit var binding: FragmentSubscriptionListBinding
    private val viewModel: SubscriptionListViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSubscriptionListBinding.bind(view)

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        with(viewModel) {
            subscription.observe(viewLifecycleOwner) { handleSubscriptionResult(it) }
            subscriptionExists.observe(viewLifecycleOwner) { handleSubscriptionExistsResult(it) }
            user.observe(viewLifecycleOwner) { it?.let { handleUserResult(it) } }
        }
        AuthManager.authStateLiveData.observe(viewLifecycleOwner) { handleAuthState(it) }
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
        with(binding) {
            result.takeIf { it is Result.Success }?.let { successResult ->
                when ((successResult as Result.Success).data.status) {
                    SubscriptionStatus.Pending.statusString -> {
                        basicPlanSubscribeBtn.setOnClickListener {
                            viewModel.continueSubscription(successResult.data.id)
                        }
                    }

                    SubscriptionStatus.Authorized.statusString -> handleAuthorizedSubscription()
                }
            } ?: Unit
        }
    }

    private fun handleAuthorizedSubscription() {
        with(binding) {
            basicPlanSubscribeBtn.visibility = View.GONE
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

    private fun handleUserResult(result: Result<User>) {
        when (result) {
            is Result.Success -> {
                result.data.takeIf { it.subscriptionId.isNotBlank() }?.let {
                    viewModel.fetchSubscription(it.subscriptionId)
                }
            } // ?: Unit
            is Result.Error -> Log.d(TAG, "error ${result.message}")
            else -> Unit
        }
    }

    private fun handleAuthState(user: FirebaseUser?) {
        with(binding.basicPlanSubscribeBtn) {
            user?.let { viewModel.getUserById(it.uid) } ?: run {
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
        viewModel.resetUser()
    }
}