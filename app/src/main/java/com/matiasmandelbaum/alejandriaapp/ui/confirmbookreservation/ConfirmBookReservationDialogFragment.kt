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
import com.matiasmandelbaum.alejandriaapp.ui.booksdetails.BooksDetailViewModel

import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "ConfirmBookReservationDialogFragment"
@AndroidEntryPoint
class ConfirmBookReservationDialogFragment: DialogFragment() {

    private val booksDetailViewModel: BooksDetailViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle("Reserva de libro")
            .setMessage("Confirma la reserva de este libro?")
            .setNegativeButton(getString(R.string.no)) { _, _ ->
                // User clicked the negative button
                booksDetailViewModel.onDialogResult(false) // Notify the ViewModel
                dismiss()
            }
            .setPositiveButton(getString(R.string.si)) { _, _ ->
                // User clicked the positive button
                booksDetailViewModel.onDialogResult(true) // Notify the ViewModel
                dismiss()
            }
            .create()
    }
}