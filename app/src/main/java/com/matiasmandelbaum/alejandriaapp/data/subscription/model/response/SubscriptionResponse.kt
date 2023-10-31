package com.matiasmandelbaum.alejandriaapp.data.subscription.model.response

import com.matiasmandelbaum.alejandriaapp.data.subscription.model.response.components.AutoRecurringResponse
import com.matiasmandelbaum.alejandriaapp.data.subscription.model.response.components.SummarizedResponse
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SubscriptionResponse(
    @Json(name = "application_id")
    val applicationIdResponse: Long,

    @Json(name = "auto_recurring")
    val autoRecurringResponse: AutoRecurringResponse,

    @Json(name = "back_url")
    val backUrlResponse: String,

    @Json(name = "collector_id")
    val collectorIdResponse: Int,

    @Json(name = "date_created")
    val dateCreatedResponse: String,

    @Json(name = "first_invoice_offset")
    val firstInvoiceOffsetResponse: Any?,

    @Json(name = "id")
    val idResponse: String,

    @Json(name = "init_point")
    val initPointResponse: String,

    @Json(name = "last_modified")
    val lastModifiedResponse: String,

    @Json(name = "payer_email")
    val payerEmailResponse: String,

    @Json(name = "payer_id")
    val payerIdResponse: Int,

    @Json(name = "payment_method_id")
    val paymentMethodIdResponse: Any?,

    @Json(name = "reason")
    val reasonResponse: String,

    @Json(name = "status")
    val statusResponse: String,

    @Json(name = "summarized")
    val summarizedResponse: SummarizedResponse
)