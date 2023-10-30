package com.matiasmandelbaum.alejandriaapp.ui.subscriptionrequired

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.matiasmandelbaum.alejandriaapp.R
import com.matiasmandelbaum.alejandriaapp.common.auth.AuthManager
import com.matiasmandelbaum.alejandriaapp.ui.subscription.SubscriptionListViewModel

import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "SubscriptionRequiredDialogFragment"
@AndroidEntryPoint
class SubscriptionRequiredDialogFragment : DialogFragment() {
    private var listener: DialogClickListener? = null

    private val viewModel: SubscriptionListViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val subscriptionId = arguments?.getString("subscriptionId")
        return AlertDialog.Builder(requireContext())

            .setTitle("Suscripción requerida")
            .setMessage("Para reservar, primero debe suscribirse")
            .setNegativeButton("Quizá después") { _, _ ->
                // User clicked the negative button
                Log.d(TAG, "Negative button clicked")
                listener?.onFinishClickDialog("negative")
                dismiss()
            }
            .setPositiveButton(getString(R.string.si)) { _, _ ->
                listener?.onFinishClickDialog("positive")
                dismiss()
                if (subscriptionId != null) {
                    Log.d(TAG, "subscriptionId is not null")
                    // Handle the case where subscriptionId is not null
                    viewModel.continueSubscription(subscriptionId)
                    Log.d(TAG, "Ok!")
                } else {
                    Log.d(TAG, "subscriptionId IS null")
                    // Handle the case where subscriptionId is null
                    viewModel.createSubscription(AuthManager.getCurrentUser()?.email!!)
                    Log.d(TAG, "Ok! (No subscriptionId)")
                }
            }
            .create()
    }

    interface DialogClickListener {
        fun onFinishClickDialog(inputText: String)
    }

    fun setDialogClickListener(listener: DialogClickListener) {
        this.listener = listener
    }
}