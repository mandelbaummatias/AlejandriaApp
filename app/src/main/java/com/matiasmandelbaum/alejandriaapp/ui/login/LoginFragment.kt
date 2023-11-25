package com.matiasmandelbaum.alejandriaapp.ui.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.matiasmandelbaum.alejandriaapp.R
import com.matiasmandelbaum.alejandriaapp.common.auth.AuthManager.addAuthStateListener
import com.matiasmandelbaum.alejandriaapp.common.auth.AuthManager.removeAuthStateListener
import com.matiasmandelbaum.alejandriaapp.common.ex.dismissKeyboard
import com.matiasmandelbaum.alejandriaapp.common.ex.loseFocusAfterAction
import com.matiasmandelbaum.alejandriaapp.common.ex.onTextChanged
import com.matiasmandelbaum.alejandriaapp.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "LoginFragment"

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        binding.textViewRecuperarCuenta.setOnClickListener {
            showPasswordRecovery()
        }



        initUI()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.viewState.collect {
                    updateUI(it)
                }
            }
        }

        return binding.root
    }

    // private val authManager = AuthManager.instance // Use the AuthManager instance

    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        val user = auth.currentUser
        if (user != null) {
            Log.d(TAG, "Mi user logueado $user")
        } else {
            Log.d(TAG, "user is null")
        }
    }

    override fun onStart() {
        super.onStart()
        addAuthStateListener(authStateListener)
        //  authManager.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        removeAuthStateListener(authStateListener)
        // authManager.removeAuthStateListener(authStateListener)
    }

//    override fun onResume() {
//        super.onResume()
//        val user = FirebaseAuth.getInstance().currentUser
//        if (user != null) {
//            Log.d(TAG, "Mi usuario está logueado $user")
//        } else {
//            Log.d(TAG, "No hay usuario loguedo")
//        }
//    }

    private fun initUI() {
        initListeners()
        initObservers()
//        binding.viewBottom.tvFooter.text = span(
//            getString(R.string.login_footer_unselected),
//            getString(R.string.login_footer_selected)
//        )
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

//        binding.tvForgotPassword.setOnClickListener { viewModel.onForgotPasswordSelected() }
//
//        binding.viewBottom.tvFooter.setOnClickListener { viewModel.onSignInSelected() }

        with(binding) {
            btnIngreso.setOnClickListener {
                it.dismissKeyboard()
                viewModel.onLoginSelected(
                    binding.editTextEmail.text.toString(),
                    binding.editTextContraseniaLogin.text.toString()
                )
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
//
        viewModel.navigateToSignIn.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let {
                goToSignIn()
            }
        }
//
//        viewModel.navigateToForgotPassword.observe(this) {
//            it.getContentIfNotHandled()?.let {
//                goToForgotPassword()
//            }
//        }
//
//        viewModel.navigateToVerifyAccount.observe(this) {
//            it.getContentIfNotHandled()?.let {
//                goToVerify()
//            }
//        }

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
            //pbLoading.isVisible = viewState.isLoading
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


    //    private fun goToForgotPassword() {
//        toast(getString(R.string.feature_not_allowed))
//    }
//
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
            "Bienvenido a AlejandriaApp!", Snackbar.LENGTH_SHORT
        ).show()
    }
//
//    private fun goToDetail() {
//        LoginSuccessDialog.create().show(dialogLauncher, this)
//    }
//
//    private fun goToVerify() {
//        startActivity(VerificationActivity.create(this))
//    }

//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val view = inflater.inflate(R.layout.fragment_login, container, false)
//
//        passwordRecovery = view.findViewById(R.id.textViewRecuperarCuenta)
//        passwordRecovery.setOnClickListener {
//            showPasswordRecovery()
//        }
//        return view
//    }

    @SuppressLint("MissingInflatedId")
    private fun showPasswordRecovery() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.fragment_password_recovery, null)
        val btnPasswordRecovery = view.findViewById<Button>(R.id.pw_recovery_send_button)
        val email = view.findViewById<TextInputEditText>(R.id.pw_recovery_email)

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
        btnPasswordRecovery.setOnClickListener {
            val emailText = email.text.toString()

            if (emailText.isNotEmpty()) {
                try {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(emailText)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                bottomSheetDialog.dismiss()
                                showLinkForPasswordRecoverySentMessage()
                            } else {
                                showOnErrorLinkSentMessage()
                            }
                        }
                } catch (e: Exception) {
                    Log.d(TAG, "exception $e")
                }

            }
        }
    }

    private fun showLinkForPasswordRecoverySentMessage() {
        Snackbar.make(
            requireView(),  // Use requireView() to get the root view of the fragment/activity
            getString(R.string.enlace_reestablecer_contrasenia),
            Snackbar.LENGTH_LONG
        ).show();
    }

    private fun showOnErrorLinkSentMessage() {
        Snackbar.make(
            requireView(),  // Use requireView() to get the root view of the fragment/activity
            getString(R.string.error_correo_recuperacion),
            Snackbar.LENGTH_LONG
        ).show();
    }
}




