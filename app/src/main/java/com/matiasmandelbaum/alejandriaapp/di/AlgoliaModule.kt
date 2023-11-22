package com.matiasmandelbaum.alejandriaapp.di

import com.algolia.search.client.ClientSearch
import com.algolia.search.model.APIKey
import com.algolia.search.model.ApplicationID
import com.matiasmandelbaum.alejandriaapp.core.algolia.AlgoliaConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AlgoliaModule {

    @Provides
    @Singleton
    fun provideAlgoliaClient(): ClientSearch {
        return ClientSearch(
            applicationID = ApplicationID(AlgoliaConfig.applicationID),
            apiKey = APIKey(AlgoliaConfig.apiKey)
        )
    }
}