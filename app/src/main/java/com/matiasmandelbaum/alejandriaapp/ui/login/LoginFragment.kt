package com.matiasmandelbaum.alejandriaapp.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.matiasmandelbaum.alejandriaapp.R
import com.matiasmandelbaum.alejandriaapp.common.dialogclicklistener.DialogClickListener
import com.matiasmandelbaum.alejandriaapp.common.ex.dismissKeyboard
import com.matiasmandelbaum.alejandriaapp.common.ex.loseFocusAfterAction
import com.matiasmandelbaum.alejandriaapp.common.ex.onTextChanged
import com.matiasmandelbaum.alejandriaapp.databinding.FragmentLoginBinding
import com.matiasmandelbaum.alejandriaapp.ui.passwordrecovery.PasswordRecoveryFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LoginFragment : Fragment(), DialogClickListener {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        initUI()
        return binding.root
    }

    private fun initUI() {
        initCollectors()
        initListeners()
        initObservers()
    }

    private fun initCollectors() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.viewState.collect {
                    updateUI(it)
                }
            }
        }
    }

    private fun initListeners() {
        binding.editTextEmail.apply {
            loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
            onTextChanged { onFieldChanged() }
        }.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)


        binding.editTextContraseniaLogin.apply {
            loseFocusAfterAction(EditorInfo.IME_ACTION_DONE)
            setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
            onTextChanged { onFieldChanged() }
        }

        binding.textViewRegistro.setOnClickListener {
            viewModel.onSignInSelected()
        }

        with(binding) {
            btnIngreso.setOnClickListener {
                it.dismissKeyboard()
                viewModel.onLoginSelected(
                    binding.editTextEmail.text.toString(),
                    binding.editTextContraseniaLogin.text.toString()
                )
            }
            textViewRecuperarCuenta.setOnClickListener {
                showPasswordRecovery()
            }
        }
    }

    private fun initObservers() {
        viewModel.navigateToHome.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let {
                goToHome()
                showWelcomeMessage()
            }
        }

        viewModel.navigateToSignIn.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let {
                goToSignIn()
            }
        }

        viewModel.showErrorDialog.observe(viewLifecycleOwner) { userLogin ->
            if (userLogin.showErrorDialog) {
                showLoginError()
            }
        }
    }

    private fun showLoginError() {
        val snackbar = Snackbar.make(
            requireView(),
            getString(R.string.error_login), Snackbar.LENGTH_LONG
        )
        snackbar.show()
    }

    private fun updateUI(viewState: LoginViewState) {
        with(binding) {
            textInputEmail.error =
                if (viewState.isValidEmail) null else getString(R.string.login_error_mail)
            textInputLayoutContraseniaLogin.error =
                if (viewState.isValidPassword) null else getString(R.string.login_error_password)
        }
    }

    private fun onFieldChanged(hasFocus: Boolean = false) {
        if (!hasFocus) {
            viewModel.onFieldsChanged(
                email = binding.editTextEmail.text.toString(),
                password = binding.editTextContraseniaLogin.text.toString()
            )
        }
    }

    private fun goToSignIn() {
        val action = LoginFragmentDirections.actionLoginFragmentToSignInFragment()
        findNavController().navigate(action)
    }

    private fun goToHome() {
        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeListFragment())

    }

    private fun showWelcomeMessage() {
        Snackbar.make(
            requireView(),
            getString(R.string.bienvenido_a_alejandriaapp), Snackbar.LENGTH_SHORT
        ).show()
    }


    private fun showPasswordRecovery() {
        val passwordRecoveryFragment = PasswordRecoveryFragment()
        passwordRecoveryFragment.setDialogClickListener(this@LoginFragment)
        passwordRecoveryFragment.show(childFragmentManager, passwordRecoveryFragment.tag)
    }

    private fun showLinkForPasswordRecoverySentMessage() {
        Snackbar.make(
            requireView(),
            getString(R.string.enlace_reestablecer_contrasenia),
            Snackbar.LENGTH_LONG
        ).show();
    }

    private fun showOnErrorLinkSentMessage() {
        Snackbar.make(
            requireView(),
            getString(R.string.error_correo_recuperacion),
            Snackbar.LENGTH_LONG
        ).show();
    }

    override fun onFinishClickDialog(clickValue: Boolean) {
        if (clickValue) {
            showLinkForPasswordRecoverySentMessage()
        } else {
            showOnErrorLinkSentMessage()
        }
    }
}




