package com.matiasmandelbaum.alejandriaapp.ui.confirmbookreservation

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.matiasmandelbaum.alejandriaapp.R
import com.matiasmandelbaum.alejandriaapp.common.auth.AuthManager
import com.matiasmandelbaum.alejandriaapp.common.dialogclicklistener.DialogClickListener
import com.matiasmandelbaum.alejandriaapp.ui.booksdetails.BooksDetailViewModel
import com.matiasmandelbaum.alejandriaapp.ui.subscriptionrequired.SubscriptionRequiredDialogFragment

import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "ConfirmBookReservationDialogFragment"
@AndroidEntryPoint
class ConfirmBookReservationDialogFragment: DialogFragment() {
    private var listener: DialogClickListener? = null

    //private val booksDetailViewModel: BooksDetailViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle("Reserva de libro")
            .setMessage("Confirma la reserva de este libro?")
            .setNegativeButton(getString(R.string.no)) { _, _ ->
                listener?.onFinishClickDialog(false)
                // User clicked the negative button
               // booksDetailViewModel.onDialogResult(false) // Notify the ViewModel
                dismiss()
            }
            .setPositiveButton(getString(R.string.si)) { _, _ ->
                listener?.onFinishClickDialog(true)
                // User clicked the positive button
              //  booksDetailViewModel.onDialogResult(true) // Notify the ViewModel
                dismiss()
            }
            .create()
    }

    fun setDialogClickListener(listener: DialogClickListener) {
        this.listener = listener
    }



}