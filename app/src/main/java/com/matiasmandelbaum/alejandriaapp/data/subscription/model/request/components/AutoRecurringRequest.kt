package com.matiasmandelbaum.alejandriaapp.data.subscription.model.request.components

import com.squareup.moshi.Json

data class AutoRecurringRequest(
    @Json(name = "currency_id")
    val currencyIdRequest: String,

    @Json(name = "frequency")
    val frequencyRequest: Int,

    @Json(name = "frequency_type")
    val frequencyTypeRequest: String,

    @Json(name = "transaction_amount")
    val transactionAmountRequest: Int
)