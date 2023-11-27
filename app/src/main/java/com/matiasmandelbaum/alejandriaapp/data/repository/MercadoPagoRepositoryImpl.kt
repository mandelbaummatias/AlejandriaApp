package com.matiasmandelbaum.alejandriaapp.data.repository

import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.data.MercadoPagoSubscription
import com.matiasmandelbaum.alejandriaapp.data.mercadopago.remote.MercadoPagoService
import com.matiasmandelbaum.alejandriaapp.data.subscription.mapper.SubscriptionResponseMapperToDomain
import com.matiasmandelbaum.alejandriaapp.domain.model.subscription.Subscription
import com.matiasmandelbaum.alejandriaapp.domain.repository.MercadoPagoRepository
import javax.inject.Inject

class MercadoPagoRepositoryImpl @Inject constructor(
    private val remote: MercadoPagoService,
    private val subscriptionResponseMapperToDomain: SubscriptionResponseMapperToDomain
) : MercadoPagoRepository {
    override suspend fun createSubscription(payerEmail: String): Result<Subscription> {
        return try {
            val response =
                remote.createSubscription(MercadoPagoSubscription.createSubscription(payerEmail))
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
