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

//class CustomResponseInterceptor : Interceptor {
//    private var lastResponse: Response? = null
//
//    override fun intercept(chain: Interceptor.Chain): Response {
//        val response = chain.proceed(chain.request())
//        lastResponse = response
//        return response
//    }
//}