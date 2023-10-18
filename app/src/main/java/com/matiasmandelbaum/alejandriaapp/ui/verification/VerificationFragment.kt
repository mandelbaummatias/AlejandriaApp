package com.matiasmandelbaum.alejandriaapp.ui.verification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.matiasmandelbaum.alejandriaapp.databinding.FragmentVerificationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VerificationFragment : Fragment() {

    private lateinit var binding: FragmentVerificationBinding
    private val viewModel: VerificationViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVerificationBinding.inflate(inflater, container, false)
        initUI()

        return binding.root
    }

    private fun initUI() {
        initListeners()
        initObservers()
    }

    private fun initListeners() {
        binding.startButton.setOnClickListener { viewModel.onGoToDetailSelected() }
    }

    private fun initObservers() {
        viewModel.navigateToLoginSuccessful.observe(this) {
            it.getContentIfNotHandled()?.let {
                val message = "Cuenta verificada! Bienvenido a AlejandriaApp"
                Snackbar.make(
                    requireView(), // Replace with the view to attach the Snackbar to
                    message,
                    Snackbar.LENGTH_SHORT // You can use LENGTH_LONG for a longer display
                ).show()
                goToHome()
                //LoginSuccessDialog.create().show(dialogLauncher, this)
            }
        }

        viewModel.showContinueButton.observe(this) {
            it.getContentIfNotHandled()?.let {
                binding.startButton.isVisible = true
            }
        }
    }

    private fun goToHome() {
        val action = VerificationFragmentDirections.actionVerificationFragmentToHomeListFragment()
        findNavController().navigate(action)
    }

}