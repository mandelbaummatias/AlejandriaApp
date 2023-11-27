package com.matiasmandelbaum.alejandriaapp

import android.app.Application
import com.matiasmandelbaum.alejandriaapp.core.algolia.AlgoliaConfig
import com.matiasmandelbaum.alejandriaapp.core.googlebooks.GoogleBooksConfig
import com.matiasmandelbaum.alejandriaapp.core.mercadopago.MercadoPagoConfig
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AlejandriaApp : Application() {

    override fun onCreate() {
        super.onCreate()
        GoogleBooksConfig.baseUrl = resources.getString(R.string.google_books_api_base_url)
        MercadoPagoConfig.apiKey = resources.getString(R.string.mercadopago_api_key)
        MercadoPagoConfig.baseUrl = resources.getString(R.string.mercadopago_api_base_url)
        AlgoliaConfig.applicationID = resources.getString(R.string.algolia_application_id)
        AlgoliaConfig.apiKey = resources.getString(R.string.algolia_api_key)
    }
}