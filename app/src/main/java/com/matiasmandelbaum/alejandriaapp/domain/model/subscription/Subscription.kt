package com.matiasmandelbaum.alejandriaapp.domain.model.subscription

import com.matiasmandelbaum.alejandriaapp.domain.model.subscription.components.AutoRecurring
import com.matiasmandelbaum.alejandriaapp.domain.model.subscription.components.Summarized

data class Subscription(
    val applicationId: Long,
    val autoRecurring: AutoRecurring,
    val backUrl: String,
    val collectorId: Int,
    val dateCreated: String,
    val firstInvoiceOffset: Any?,
    val id: String,
    val initPoint: String,
    val lastModified: String,
    val payerEmail: String,
    val payerId: Int,
    val paymentMethodId: Any?,
    val reason: String,
    val status: String,
    val summarized: Summarized
)