package com.matiasmandelbaum.alejandriaapp.domain.usecase

import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.domain.model.subscription.Subscription
import com.matiasmandelbaum.alejandriaapp.domain.model.subscription.components.AutoRecurring
import com.matiasmandelbaum.alejandriaapp.domain.model.subscription.components.Summarized
import com.matiasmandelbaum.alejandriaapp.domain.repository.MercadoPagoRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals

class CreateSubscriptionUseCaseTest {

    @RelaxedMockK
    private lateinit var mercadoPagoRepository: MercadoPagoRepository

    lateinit var createSubscriptionUseCase: CreateSubscriptionUseCase

    @Before
    fun onBefore() {
        MockKAnnotations.init(this)
        createSubscriptionUseCase = CreateSubscriptionUseCase(mercadoPagoRepository)
    }

    @Test
    fun `should return success when createSubscription is successful`() = runBlocking {
        // Given
        val payerEmail = "test@example.com"
        val subscription = mockSubscription()

        coEvery { mercadoPagoRepository.createSubscription(payerEmail) } returns Result.Success(
            subscription
        )

        // When
        val response = createSubscriptionUseCase(payerEmail)

        // Then
        assert(response is Result.Success)
        assertEquals(subscription, (response as Result.Success).data)
    }

    @Test
    fun `should return error when createSubscription fails`() = runBlocking {
        // Given
        val payerEmail = "test@example.com"
        coEvery { mercadoPagoRepository.createSubscription(payerEmail) } returns Result.Error("Create subscription failed")

        // When
        val response = createSubscriptionUseCase(payerEmail)

        // Then
        assert(response is Result.Error)
        assertEquals("Create subscription failed", (response as Result.Error).message)
    }

    private fun mockSubscription(): Subscription {
        return Subscription(
            applicationId = 12345,
            autoRecurring = AutoRecurring(
                currencyId = "ARS",
                freeTrial = null,
                frequency = 1,
                frequencyType = "MONTHLY",
                transactionAmount = 100
            ),
            backUrl = "https://example.com",
            collectorId = 6789,
            dateCreated = "2023-01-01T12:00:00",
            firstInvoiceOffset = null,
            id = "subscriptionId123",
            initPoint = "https://initpoint.example.com",
            lastModified = "2023-01-01T12:00:00",
            payerEmail = "test@example.com",
            payerId = 9876,
            paymentMethodId = null,
            reason = "Subscription Reason",
            status = "ACTIVE",
            summarized = Summarized(
                chargedAmount = null,
                chargedQuantity = null,
                lastChargedAmount = null,
                lastChargedDate = null,
                pendingChargeAmount = null,
                pendingChargeQuantity = null,
                quotas = null,
                semaphore = null
            )
        )
    }
}
