package com.matiasmandelbaum.alejandriaapp.data.mercadopago.remote

import com.matiasmandelbaum.alejandriaapp.data.subscription.model.request.SubscriptionRequest
import com.matiasmandelbaum.alejandriaapp.data.subscription.model.response.SubscriptionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MercadoPagoApiClient {
    @POST("preapproval")
    suspend fun createSubscription(@Body request: SubscriptionRequest): Response<SubscriptionResponse>

    @GET("preapproval/{id}")
    suspend fun fetchSubscription(@Path("id") id: String): Response<SubscriptionResponse>

}
