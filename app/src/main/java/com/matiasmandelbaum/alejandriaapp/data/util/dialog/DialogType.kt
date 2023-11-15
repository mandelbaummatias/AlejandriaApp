package com.matiasmandelbaum.alejandriaapp.data.util.dialog

sealed class DialogType(val tag: String) {
    object RESERVATION : DialogType("ConfirmBookReservationDialogFragment")
    object SUBSCRIPTION : DialogType("SubscriptionRequiredDialogFragment")
}