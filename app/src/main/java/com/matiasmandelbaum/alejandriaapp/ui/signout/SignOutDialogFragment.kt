package com.matiasmandelbaum.alejandriaapp.ui.signout

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.matiasmandelbaum.alejandriaapp.R
import com.matiasmandelbaum.alejandriaapp.common.auth.AuthManager
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "SignoutDialogFragment"

@AndroidEntryPoint
class SignOutDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.titulo_logout))
            .setMessage(getString(R.string.confirmacion))
            .setNegativeButton(getString(R.string.no)) { _, _ ->
                // User clicked the negative button
                Log.d(TAG, "Negative button clicked")
                dismiss()
              //  findNavController().navigate(SignoutDialogFragmentDirections.actionSignoutDialogFragmentToHomeListFragment())

            }
            .setPositiveButton(getString(R.string.si)) { _, _ ->
                // User clicked the positive button
                AuthManager.signOut()
                Log.d(TAG, "Positive button clicked")
            }
            .create()
    }
}