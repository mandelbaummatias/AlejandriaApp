package com.matiasmandelbaum.alejandriaapp.ui.subscription

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import com.matiasmandelbaum.alejandriaapp.R
import com.matiasmandelbaum.alejandriaapp.common.auth.AuthManager
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.databinding.FragmentSubscriptionListBinding
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "SubscriptionListFragment"

@AndroidEntryPoint
class SubscriptionListFragment : Fragment() {

    private lateinit var binding: FragmentSubscriptionListBinding
    private val viewModel: SubscriptionViewModel by activityViewModels()

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




        AuthManager.authStateLiveData.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                Log.d(TAG, "user logged $user")
                val userUid = AuthManager.getCurrentUser()?.uid
                userUid?.let {
                    viewModel.getUserById(it)
                }
            } else {
                binding.basicPlanSubscribeBtn.visibility = View.VISIBLE
                binding.basicPlanSubscribeBtn.setOnClickListener {
                    Snackbar.make(
                        requireView(),
                        getString(R.string.solicitud_iniciar_sesion), Snackbar.LENGTH_SHORT
                    ).show()

                }
            }
        }


        initObservers()
        initListeners()
    }

    private fun initObservers() {
        viewModel.subscription.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Success -> handleLoading(false)
                is Result.Loading -> handleLoading(true)
                is Result.Error -> handleLoading(false)
                else -> {}
            }
        }
    }

    private fun initListeners() {
        binding.basicPlanSubscribeBtn.setOnClickListener {
            Log.d(TAG, "click basic")
            AuthManager.getCurrentUser()?.email?.let {viewModel.createSubscription(it) }
        }

        viewModel.subscriptionExists.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    if (result.data.status == "pending") {
                        Log.d(TAG, "consegui usuario")
                        //si consiguió usuario al princi
                        binding.basicPlanSubscribeBtn.setOnClickListener {
                            viewModel.continueSubscription(result.data.id)

                        }
                    } else if (result.data.status == "authorized") {
                        binding.basicPlanSubscribeBtn.visibility = View.GONE
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
            }
        }


    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
    }

    private fun handleLoading(isLoading: Boolean) {
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
}


