package com.matiasmandelbaum.alejandriaapp.ui.signout

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.matiasmandelbaum.alejandriaapp.R
import com.matiasmandelbaum.alejandriaapp.common.auth.AuthManager
import com.matiasmandelbaum.alejandriaapp.common.dialogclicklistener.DialogClickListener
import com.matiasmandelbaum.alejandriaapp.common.dialogclicklistener.DialogClickListenerProvider
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignOutDialogFragment : DialogFragment(), DialogClickListenerProvider {
    private var listener: DialogClickListener? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.titulo_logout))
            .setMessage(getString(R.string.confirmacion))
            .setNegativeButton(getString(R.string.no)) { _, _ ->
                listener?.onFinishClickDialog(false)
                dismiss()
            }
            .setPositiveButton(getString(R.string.si)) { _, _ ->
                listener?.onFinishClickDialog(true)
                AuthManager.signOut()
            }
            .create()
    }

    override fun setDialogClickListener(listener: DialogClickListener) {
        this.listener = listener
    }
}