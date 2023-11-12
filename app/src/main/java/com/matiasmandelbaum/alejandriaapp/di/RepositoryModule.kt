package com.matiasmandelbaum.alejandriaapp.di


import com.matiasmandelbaum.alejandriaapp.data.repository.BooksRepositoryImpl
import com.matiasmandelbaum.alejandriaapp.data.repository.MercadoPagoRepositoryImpl
import com.matiasmandelbaum.alejandriaapp.data.repository.ReservationsRepositoryImpl
import com.matiasmandelbaum.alejandriaapp.data.repository.UsersRepositoryImpl
import com.matiasmandelbaum.alejandriaapp.domain.repository.BooksRepository
import com.matiasmandelbaum.alejandriaapp.domain.repository.MercadoPagoRepository
import com.matiasmandelbaum.alejandriaapp.domain.repository.ReservationsRepository
import com.matiasmandelbaum.alejandriaapp.domain.repository.UsersRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindBooksRepository(booksRepository: BooksRepositoryImpl): BooksRepository
    @Binds
    abstract fun bindMercadoPagoRepository(mercadoPagoRepository: MercadoPagoRepositoryImpl): MercadoPagoRepository

    @Binds
    abstract fun bindUserRepository(usersRepositoryImpl: UsersRepositoryImpl): UsersRepository

    @Binds
    abstract fun bindReservationRepository(reservationsRepositoryImpl: ReservationsRepositoryImpl): ReservationsRepository
}