package com.matiasmandelbaum.alejandriaapp.data

import com.matiasmandelbaum.alejandriaapp.data.subscription.model.request.SubscriptionRequest
import com.matiasmandelbaum.alejandriaapp.data.subscription.model.request.components.AutoRecurringRequest

class MercadoPagoDataSource {

    companion object {
        val BODY_SUBSCRIPTION = SubscriptionRequest(
            autoRecurringRequest = AutoRecurringRequest(
                currencyIdRequest = "ARS",
                frequencyRequest = 1,
                frequencyTypeRequest = "months",
                transactionAmountRequest = 500
            ),
            backUrlRequest = "https://mercadopago.com.ar",
            payerEmailRequest = "", //Your buyer user
            reasonRequest = "Plan básico"
        )
    }
}
