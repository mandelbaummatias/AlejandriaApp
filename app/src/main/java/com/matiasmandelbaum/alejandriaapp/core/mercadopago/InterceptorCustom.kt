package com.matiasmandelbaum.alejandriaapp.core.mercadopago

import okhttp3.Interceptor
import okhttp3.Response

object InterceptorCustom : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val apiKey = MercadoPagoConfig.apiKey

        var request = chain.request()
        request = request.newBuilder()
            .header("Authorization", "Bearer $apiKey")
            .header("Content-Type", "application/json")
            .build()
        return chain.proceed(request)
    }
}