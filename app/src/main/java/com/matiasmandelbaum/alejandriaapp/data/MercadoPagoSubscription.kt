package com.matiasmandelbaum.alejandriaapp.data

import com.matiasmandelbaum.alejandriaapp.data.subscription.model.request.SubscriptionRequest
import com.matiasmandelbaum.alejandriaapp.data.subscription.model.request.components.AutoRecurringRequest

class MercadoPagoSubscription {
    companion object {
        fun createSubscription(payerEmail: String): SubscriptionRequest {
            return SubscriptionRequest(
                autoRecurringRequest = AutoRecurringRequest(
                    currencyIdRequest = "ARS",
                    frequencyRequest = 1,
                    frequencyTypeRequest = "months",
                    transactionAmountRequest = 500
                ),
                backUrlRequest = "https://mercadopago.com.ar",
                payerEmailRequest = payerEmail,
                reasonRequest = "Plan básico"
            )
        }
    }
}