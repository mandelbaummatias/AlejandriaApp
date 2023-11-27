package com.matiasmandelbaum.alejandriaapp.di

import com.matiasmandelbaum.alejandriaapp.core.googlebooks.GoogleBooksConfig
import com.matiasmandelbaum.alejandriaapp.core.mercadopago.InterceptorCustom
import com.matiasmandelbaum.alejandriaapp.core.mercadopago.MercadoPagoConfig
import com.matiasmandelbaum.alejandriaapp.data.googlebooks.remote.GoogleBooksApiClient
import com.matiasmandelbaum.alejandriaapp.data.mercadopago.remote.MercadoPagoApiClient
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private var client: OkHttpClient = OkHttpClient.Builder().apply {
        addInterceptor(interceptor)
            .addInterceptor(InterceptorCustom)

    }.build()

    @Singleton
    @Provides
    @Named("GBooks")
    fun provideRetrofit(): Retrofit {
        val baseUrl = GoogleBooksConfig.baseUrl

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .build()
                chain.proceed(request)
            }
            .build()

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Singleton
    @Provides
    @Named("MercadoPago")
    fun provideRetrofitMercadoPago(): Retrofit {
        val apiKey = MercadoPagoConfig.apiKey
        val baseUrl = MercadoPagoConfig.baseUrl

        client.newBuilder().addNetworkInterceptor(Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $apiKey")
                .build()
            chain.proceed(request)
        })

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Singleton
    @Provides
    fun provideGoogleBooksApiClient(@Named("GBooks") retrofit: Retrofit): GoogleBooksApiClient {
        return retrofit.create(GoogleBooksApiClient::class.java)
    }

    @Singleton
    @Provides
    fun provideMercadoPagoApiClient(@Named("MercadoPago") retrofit: Retrofit): MercadoPagoApiClient {
        return retrofit.create(MercadoPagoApiClient::class.java)
    }


}