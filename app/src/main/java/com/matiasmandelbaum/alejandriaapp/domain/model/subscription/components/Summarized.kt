package com.matiasmandelbaum.alejandriaapp.domain.model.subscription.components

data class Summarized(
    val chargedAmount: Any?,
    val chargedQuantity: Any?,
    val lastChargedAmount: Any?,
    val lastChargedDate: Any?,
    val pendingChargeAmount: Any?,
    val pendingChargeQuantity: Any?,
    val quotas: Any?,
    val semaphore: Any?
)