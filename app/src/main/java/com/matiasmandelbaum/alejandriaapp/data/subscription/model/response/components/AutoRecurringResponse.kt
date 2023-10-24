package com.matiasmandelbaum.alejandriaapp.data.subscription.model.response.components

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AutoRecurringResponse(
    @Json(name = "currency_id")
    val currencyIdResponse: String,

    @Json(name = "free_trial")
    val freeTrialResponse: Any?,

    @Json(name = "frequency")
    val frequencyResponse: Int,

    @Json(name = "frequency_type")
    val frequencyTypeResponse: String,

    @Json(name = "transaction_amount")
    val transactionAmountResponse: Int?
)