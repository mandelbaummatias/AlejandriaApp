package com.matiasmandelbaum.alejandriaapp.data.util.dialog.factory

import com.matiasmandelbaum.alejandriaapp.common.dialogclicklistener.DialogClickListenerProvider
import com.matiasmandelbaum.alejandriaapp.data.util.dialog.DialogType

interface DialogFragmentFactory {
    fun createDialogFragment(type: DialogType, subscriptionId: String? = null): DialogClickListenerProvider
}
