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

class FetchSubscriptionUseCaseTest {

    @RelaxedMockK
    private lateinit var mercadoPagoRepository: MercadoPagoRepository

    lateinit var fetchSubscriptionUseCase: FetchSubscriptionUseCase

    @Before
    fun onBefore() {
        MockKAnnotations.init(this)
        fetchSubscriptionUseCase = FetchSubscriptionUseCase(mercadoPagoRepository)
    }

    @Test
    fun `when fetchSubscription is successful`() = runBlocking {
        // Given
        val subscriptionId = "testSubscriptionId"
        val subscription = Subscription(
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




        coEvery { mercadoPagoRepository.fetchSubscription(subscriptionId) } returns Result.Success(
            subscription
        )

        // When
        val response = fetchSubscriptionUseCase(subscriptionId)

        // Then
        assert(response is Result.Success)
        assertEquals(subscription, (response as Result.Success).data)
    }

    @Test
    fun `when fetchSubscription fails`() = runBlocking {
        // Given
        val subscriptionId = "testSubscriptionId"
        coEvery { mercadoPagoRepository.fetchSubscription(subscriptionId) } returns Result.Error("Fetch subscription failed")

        // When
        val response = fetchSubscriptionUseCase(subscriptionId)

        // Then
        assert(response is Result.Error)
        assertEquals("Fetch subscription failed", (response as Result.Error).message)
    }
}
