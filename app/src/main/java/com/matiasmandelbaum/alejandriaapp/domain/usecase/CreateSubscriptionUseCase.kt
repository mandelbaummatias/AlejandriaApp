package com.matiasmandelbaum.alejandriaapp.domain.usecase

import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.domain.model.subscription.Subscription
import com.matiasmandelbaum.alejandriaapp.domain.repository.MercadoPagoRepository
import javax.inject.Inject

class CreateSubscriptionUseCase @Inject constructor(private val mercadoPagoRepository: MercadoPagoRepository) {
    suspend operator fun invoke(payerEmail: String): Result<Subscription> {
        return mercadoPagoRepository.createSubscription(payerEmail)
    }
}