package com.matiasmandelbaum.alejandriaapp.domain.repository

import com.matiasmandelbaum.alejandriaapp.domain.model.subscription.Subscription
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import kotlinx.coroutines.flow.Flow

interface MercadoPagoRepository {
    suspend fun createSubscription(): Result<Subscription>

//    fun createSubscription(): Flow<Result<Subscription>>

   // suspend fun fetchSubscription(id: String): Result<String>

     suspend fun fetchSubscription(id: String): Result<Subscription>
}