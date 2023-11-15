package com.matiasmandelbaum.alejandriaapp.data.util.subscriptionstatus

sealed class SubscriptionStatus(val statusString: String) {
    object Pending : SubscriptionStatus("pending")
    object Authorized : SubscriptionStatus("authorized")
}