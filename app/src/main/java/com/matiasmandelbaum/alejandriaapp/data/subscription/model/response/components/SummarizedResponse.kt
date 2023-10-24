package com.matiasmandelbaum.alejandriaapp.data.subscription.model.response.components

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SummarizedResponse(
    @Json(name = "charged_amount")
    val chargedAmount: Any?,

    @Json(name = "charged_quantity")
    val chargedQuantity: Any?,

    @Json(name = "last_charged_amount")
    val lastChargedAmount: Any?,

    @Json(name = "last_charged_date")
    val lastChargedDate: Any?,

    @Json(name = "pending_charge_amount")
    val pendingChargeAmount: Any?,

    @Json(name = "pending_charge_quantity")
    val pendingChargeQuantity: Any?,

    @Json(name = "quotas")
    val quotas: Any?,

    @Json(name = "semaphore")
    val semaphore: Any?
)