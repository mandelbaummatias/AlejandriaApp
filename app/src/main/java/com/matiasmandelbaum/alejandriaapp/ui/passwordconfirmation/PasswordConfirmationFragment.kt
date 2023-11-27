package com.matiasmandelbaum.alejandriaapp.ui.passwordconfirmation

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.matiasmandelbaum.alejandriaapp.R
import com.matiasmandelbaum.alejandriaapp.common.dialogclicklistener.DialogClickListener
import com.matiasmandelbaum.alejandriaapp.common.dialogclicklistener.DialogClickListenerProvider
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.databinding.FragmentPwConfirmationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PasswordConfirmationFragment : BottomSheetDialogFragment(), DialogClickListenerProvider {

    private lateinit var binding: FragmentPwConfirmationBinding
    private var listener: DialogClickListener? = null

    private val viewModel: PasswordConfirmationViewModel by viewModels()

    companion object {
        private const val ARG_NEW_EMAIL = "arg_new_email"
        private const val ARG_PREVIOUS_EMAIL = "arg_previous_email"

        fun newInstance(newEmail: String, previousEmail: String): PasswordConfirmationFragment {
            val fragment = PasswordConfirmationFragment()
            val args = Bundle()
            args.putString(ARG_NEW_EMAIL, newEmail)
            args.putString(ARG_PREVIOUS_EMAIL, previousEmail)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPwConfirmationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        initObservers()
    }

    private fun initObservers() {
        viewModel.onChangeUserEmailSuccess.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Success -> {
                    handleLoading(true)
                    this.dismiss()
                    listener?.onFinishClickDialog(true)
                }

                is Result.Error -> {
                    binding.pwConfirmPasswordLabel.error = "La contraseña es incorrecta"
                }

                else -> {
                    Unit
                }
            }
        }
    }

    private fun initListeners() {
        val newEmail = arguments?.getString(ARG_NEW_EMAIL)
        val previousEmail = arguments?.getString(ARG_PREVIOUS_EMAIL)

        with(binding) {
            pwConfirmButton.setOnClickListener {
                val pass = binding.pwConfirmPassword.text.toString()
                if (pass.isNotEmpty()) {
                    try {
                        if (newEmail != null) {
                            if (previousEmail != null) {
                                viewModel.changeUserEmail(newEmail, previousEmail, pass)
                            }
                        }
                    } catch (e: Exception) {
                       showOnChangeEmailErrorMessage()
                    }
                } else {
                    pwConfirmPasswordLabel.error = "La contraseña no puede estar vacía"

                }

            }
        }


    }

    private fun handleLoading(isLoading: Boolean) {
        with(binding) {
            if (isLoading) {
                userProfileFragment.visibility = View.GONE
                pwConfirmProgressbar.visibility = View.VISIBLE
            } else {
                userProfileFragment.visibility = View.VISIBLE
                pwConfirmProgressbar.visibility = View.GONE
            }
        }
    }

    private fun showOnChangeEmailErrorMessage() {
        Snackbar.make(
            requireView(),
            getString(R.string.error_en_cambiar_el_mail_del_usuario), Snackbar.LENGTH_LONG
        ).show()
    }

    override fun setDialogClickListener(listener: DialogClickListener) {
        this.listener = listener
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener?.onFinishClickDialog(false)
    }
}