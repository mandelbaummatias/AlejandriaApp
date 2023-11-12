package com.matiasmandelbaum.alejandriaapp.ui.confirmbookreservation

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.matiasmandelbaum.alejandriaapp.R
import com.matiasmandelbaum.alejandriaapp.common.dialogclicklistener.DialogClickListener
import com.matiasmandelbaum.alejandriaapp.common.dialogclicklistener.DialogClickListenerProvider
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "ConfirmBookReservationDialogFragment"

@AndroidEntryPoint
class ConfirmBookReservationDialogFragment : DialogFragment(), DialogClickListenerProvider{
    private var listener: DialogClickListener? = null
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

    override fun setDialogClickListener(listener: DialogClickListener) {
        this.listener = listener
    }
}