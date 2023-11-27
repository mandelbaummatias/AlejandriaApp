package com.matiasmandelbaum.alejandriaapp.ui.passwordrecovery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.matiasmandelbaum.alejandriaapp.R
import com.matiasmandelbaum.alejandriaapp.common.dialogclicklistener.DialogClickListener
import com.matiasmandelbaum.alejandriaapp.common.dialogclicklistener.DialogClickListenerProvider
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.databinding.FragmentPasswordRecoveryBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PasswordRecoveryFragment : BottomSheetDialogFragment(), DialogClickListenerProvider {

    private lateinit var binding: FragmentPasswordRecoveryBinding
    private var listener: DialogClickListener? = null
    private val viewModel: PasswordRecoveryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPasswordRecoveryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        initObservers()
    }

    private fun initObservers() {
        viewModel.onPasswordResetEmailSent.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Success -> {
                    this.dismiss()
                    listener?.onFinishClickDialog(true)
                }

                is Result.Error -> {
                    this.dismiss()
                    listener?.onFinishClickDialog(false)
                }

                else -> {
                    Unit
                }
            }
        }
    }

    private fun initListeners() {
        with(binding) {
            pwRecoverySendButton.setOnClickListener {
                pwRecoveryEmail.text.toString().let { emailText ->
                    if (emailText.isNotEmpty()) {
                        viewModel.sendPasswordResetEmail(emailText)
                    } else {
                        val error = getString(R.string.el_mail_no_puede_estar_vacio)
                        binding.pwRecoveryEmailLabel.error = error
                    }
                }
            }
        }
    }

    override fun setDialogClickListener(listener: DialogClickListener) {
        this.listener = listener
    }
}