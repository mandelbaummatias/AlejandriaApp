package com.matiasmandelbaum.alejandriaapp.data.mercadopago.remote

import android.util.Log
import com.matiasmandelbaum.alejandriaapp.core.mercadopago.CustomResponseInterceptor
import com.matiasmandelbaum.alejandriaapp.data.subscription.model.request.SubscriptionRequest
import com.matiasmandelbaum.alejandriaapp.data.subscription.model.response.SubscriptionResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "MercadoPagoService"
class MercadoPagoService @Inject constructor(private val service: MercadoPagoApiClient, private val customResponseInterceptor: CustomResponseInterceptor) {
    suspend fun createSubscription(subscriptionRequest: SubscriptionRequest): SubscriptionResponse? {
        return withContext(Dispatchers.IO) {
            val response = service.createSubscription(subscriptionRequest)
            response.body()
        }
    }

    suspend fun fetchSubscription(id: String): SubscriptionResponse? {
        return withContext(Dispatchers.IO) {
            val response = service.fetchSubscription(id)
            response.body()
        }
    }

}