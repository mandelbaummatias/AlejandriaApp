package com.matiasmandelbaum.alejandriaapp

import android.app.Application
import com.matiasmandelbaum.alejandriaapp.core.googlebooks.GoogleBooksConfig
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AlejandriaApp : Application() {

    override fun onCreate() {
        super.onCreate()
        GoogleBooksConfig.baseUrl= resources.getString(R.string.google_books_api_base_url)
        GoogleBooksConfig.apiKey = resources.getString(R.string.google_books_api_key)
    }
}