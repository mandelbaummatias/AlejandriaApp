package com.matiasmandelbaum.alejandriaapp.domain.repository

import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.domain.model.subscription.Subscription

interface MercadoPagoRepository {
    suspend fun createSubscription(payerEmail: String): Result<Subscription>

    suspend fun fetchSubscription(id: String): Result<Subscription>
}