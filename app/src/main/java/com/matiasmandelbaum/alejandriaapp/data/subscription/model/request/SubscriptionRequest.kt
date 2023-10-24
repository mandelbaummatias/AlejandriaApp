package com.matiasmandelbaum.alejandriaapp.data.subscription.model.request

import com.matiasmandelbaum.alejandriaapp.data.subscription.model.request.components.AutoRecurringRequest
import com.squareup.moshi.Json

data class SubscriptionRequest(
    @Json(name = "auto_recurring")
    val autoRecurringRequest: AutoRecurringRequest,

    @Json(name = "back_url")
    val backUrlRequest: String,

    @Json(name = "payer_email")
    val payerEmailRequest: String,

    @Json(name = "reason")
    val reasonRequest: String,

    )