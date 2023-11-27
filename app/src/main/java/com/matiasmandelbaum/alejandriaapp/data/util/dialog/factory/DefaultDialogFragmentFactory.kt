package com.matiasmandelbaum.alejandriaapp.data.util.dialog.factory

import android.os.Bundle
import com.matiasmandelbaum.alejandriaapp.common.dialogclicklistener.DialogClickListenerProvider
import com.matiasmandelbaum.alejandriaapp.data.util.dialog.DialogType
import com.matiasmandelbaum.alejandriaapp.ui.confirmbookreservation.ConfirmBookReservationDialogFragment
import com.matiasmandelbaum.alejandriaapp.ui.subscriptionrequired.SubscriptionRequiredDialogFragment

class DefaultDialogFragmentFactory : DialogFragmentFactory {
    override fun createDialogFragment(
        type: DialogType,
        subscriptionId: String?
    ): DialogClickListenerProvider {
        return when (type) {
            is DialogType.RESERVATION -> ConfirmBookReservationDialogFragment()
            is DialogType.SUBSCRIPTION -> SubscriptionRequiredDialogFragment().apply {
                arguments = Bundle().apply {
                    putString("subscription_id", subscriptionId)
                }
            }
        }
    }
}