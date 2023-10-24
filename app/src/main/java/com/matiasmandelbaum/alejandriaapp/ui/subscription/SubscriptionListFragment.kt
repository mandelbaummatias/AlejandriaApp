package com.matiasmandelbaum.alejandriaapp.ui.subscription

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.matiasmandelbaum.alejandriaapp.R
import com.matiasmandelbaum.alejandriaapp.databinding.FragmentSubscriptionListBinding
import com.matiasmandelbaum.alejandriaapp.domain.model.subscription.Subscription
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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

//        viewLifecycleOwner.lifecycleScope.launch {
//            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                viewModel.subscription.collect {
//                    when(it){
//                        is Result.Success -> {
//                            val data: Subscription = it.data// Get the data from the Success resource
//                            // Now you can work with the 'data' list
//                            // For example, if you want to access the first item:
//                            Log.d(TAG, "first item $data")
//                            // Do something with 'firstItem'
//                        }
//                        Result.Loading -> Log.d(TAG,"Loading")
//                        is Result.Error -> {
//                            val errorMessage = it.message
//                            Log.d(TAG, "error! : $errorMessage")// Get the error message from the Error resource
//                            // Handle the error message as needed
//                        }
//                        Result.Finished -> Log.d(TAG, "Finished")
//                        else -> {}
//                    }
//                }
//            }
//        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

    private fun initListeners(){
        binding.basicPlanSubscribeBtn.setOnClickListener {
            viewModel.createSubscription()
        }
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