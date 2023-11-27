package com.matiasmandelbaum.alejandriaapp.ui.subscriptionrequired

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.matiasmandelbaum.alejandriaapp.R
import com.matiasmandelbaum.alejandriaapp.common.auth.AuthManager
import com.matiasmandelbaum.alejandriaapp.common.dialogclicklistener.DialogClickListener
import com.matiasmandelbaum.alejandriaapp.common.dialogclicklistener.DialogClickListenerProvider
import com.matiasmandelbaum.alejandriaapp.ui.subscription.SubscriptionListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SubscriptionRequiredDialogFragment : DialogFragment(), DialogClickListenerProvider {
    private var listener: DialogClickListener? = null

    private val viewModel: SubscriptionListViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val subscriptionId = arguments?.getString(getString(R.string.sub_id))
        return AlertDialog.Builder(requireContext())

            .setTitle(getString(R.string.suscripcion_requerida))
            .setMessage(getString(R.string.para_reservar_primero_debe_suscribirse))
            .setNegativeButton(getString(R.string.quiza_despues)) { _, _ ->
                dismiss()
            }
            .setPositiveButton(getString(R.string.si)) { _, _ ->
                dismiss()
                if (subscriptionId != null) {
                    viewModel.continueSubscription(subscriptionId)
                } else {
                    viewModel.createSubscription(AuthManager.getCurrentUser()?.email!!)
                }
            }
            .create()
    }

    override fun setDialogClickListener(listener: DialogClickListener) {
        this.listener = listener
    }
}