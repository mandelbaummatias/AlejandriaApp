package com.matiasmandelbaum.alejandriaapp.domain.usecase

import com.matiasmandelbaum.alejandriaapp.domain.model.subscription.Subscription
import com.matiasmandelbaum.alejandriaapp.domain.repository.MercadoPagoRepository
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CreateSubscriptionUseCase @Inject constructor(private val mercadoPagoRepository: MercadoPagoRepository) {
    suspend operator fun invoke(): Result<Subscription> {
        return mercadoPagoRepository.createSubscription()
    }
}

//    suspend operator fun invoke(): Flow<Result<Subscription>> = flow {
//        emit(Result.Loading)
//
//        val networkRequest = mercadoPagoRepository.createSubscription()
//
//        when(networkRequest) {
//            is Result.Success -> emit(networkRequest)
//            is Result.Error -> emit(Result.Error(networkRequest.message))
//            else -> Result.Finished
//        }
//    }

//    operator fun invoke(): Flow<Result<Subscription>> =
//        mercadoPagoRepository.createSubscription()
//}