package com.matiasmandelbaum.alejandriaapp.di


import com.matiasmandelbaum.alejandriaapp.data.repository.BooksRepositoryImpl
import com.matiasmandelbaum.alejandriaapp.domain.repository.BooksRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindBooksRepository(booksRepository: BooksRepositoryImpl): BooksRepository
}