package com.matiasmandelbaum.alejandriaapp.data.repository

import android.util.Log
import com.bumptech.glide.load.engine.executor.GlideExecutor.UncaughtThrowableStrategy.LOG
import com.matiasmandelbaum.alejandriaapp.data.MercadoPagoDataSource
import com.matiasmandelbaum.alejandriaapp.data.subscription.mapper.SubscriptionResponseMapperToDomain
import com.matiasmandelbaum.alejandriaapp.domain.model.subscription.Subscription
import com.matiasmandelbaum.alejandriaapp.domain.repository.MercadoPagoRepository
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.data.mercadopago.remote.MercadoPagoService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

private const val TAG = "MercadoPagoRepositoryImpl"

class MercadoPagoRepositoryImpl @Inject constructor(
    private val remote: MercadoPagoService,
    private val subscriptionResponseMapperToDomain: SubscriptionResponseMapperToDomain
) : MercadoPagoRepository {
    override suspend fun createSubscription(): Result<Subscription> {
        return try {
            val response = remote.createSubscription(MercadoPagoDataSource.BODY_SUBSCRIPTION)
            val subscriptionResponse =
                response ?: return Result.Error("Response is null")


            Result.Success(subscriptionResponseMapperToDomain.mapFrom(subscriptionResponse))
        } catch (e: Exception) {
            Result.Error(e.message.toString())
        }
    }

        override suspend fun fetchSubscription(id: String): Result<Subscription> {
        return try {
            val response = remote.fetchSubscription(id)
            val subscriptionResponse = response ?: return Result.Error("Response is null")

            Result.Success(subscriptionResponseMapperToDomain.mapFrom(subscriptionResponse))
        } catch (e: Exception) {
            Result.Error(e.message.toString())
        }
    }


}
