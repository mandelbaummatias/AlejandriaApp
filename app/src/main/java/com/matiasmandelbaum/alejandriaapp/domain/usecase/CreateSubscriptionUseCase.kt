package com.matiasmandelbaum.alejandriaapp.domain.usecase

import com.matiasmandelbaum.alejandriaapp.domain.model.subscription.Subscription
import com.matiasmandelbaum.alejandriaapp.domain.repository.MercadoPagoRepository
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CreateSubscriptionUseCase @Inject constructor(private val mercadoPagoRepository: MercadoPagoRepository) {
    suspend operator fun invoke(payerEmail: String): Result<Subscription> {
        return mercadoPagoRepository.createSubscription(payerEmail)
    }
}