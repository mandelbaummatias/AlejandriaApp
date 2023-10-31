package com.matiasmandelbaum.alejandriaapp.data.subscription.mapper

import com.matiasmandelbaum.alejandriaapp.common.mapper.Mapper
import com.matiasmandelbaum.alejandriaapp.data.subscription.model.response.SubscriptionResponse
import com.matiasmandelbaum.alejandriaapp.domain.model.subscription.Subscription
import com.matiasmandelbaum.alejandriaapp.domain.model.subscription.components.AutoRecurring
import com.matiasmandelbaum.alejandriaapp.domain.model.subscription.components.Summarized
import javax.inject.Inject

class SubscriptionResponseMapperToDomain @Inject constructor() :
    Mapper<SubscriptionResponse, Subscription> {
    override fun mapFrom(from: SubscriptionResponse): Subscription {
        return Subscription(
            applicationId = from.applicationIdResponse,
            autoRecurring = AutoRecurring(
                currencyId = from.autoRecurringResponse.currencyIdResponse,
                freeTrial = from.autoRecurringResponse.freeTrialResponse,
                frequency = from.autoRecurringResponse.frequencyResponse,
                frequencyType = from.autoRecurringResponse.frequencyTypeResponse,
                transactionAmount = from.autoRecurringResponse.transactionAmountResponse
            ),
            backUrl = from.backUrlResponse,
            collectorId = from.collectorIdResponse,
            dateCreated = from.dateCreatedResponse,
            firstInvoiceOffset = from.firstInvoiceOffsetResponse,
            id = from.idResponse,
            initPoint = from.initPointResponse,
            lastModified = from.lastModifiedResponse,
            payerEmail = from.payerEmailResponse,
            payerId = from.payerIdResponse,
            paymentMethodId = from.paymentMethodIdResponse,
            reason = from.reasonResponse,
            status = from.statusResponse,
            summarized = Summarized(
                chargedAmount = from.summarizedResponse.chargedAmount,
                chargedQuantity = from.summarizedResponse.chargedQuantity,
                lastChargedAmount = from.summarizedResponse.lastChargedAmount,
                lastChargedDate = from.summarizedResponse.lastChargedDate,
                pendingChargeAmount = from.summarizedResponse.pendingChargeAmount,
                pendingChargeQuantity = from.summarizedResponse.pendingChargeQuantity,
                quotas = from.summarizedResponse.quotas,
                semaphore = from.summarizedResponse.semaphore
            )
        )
    }
}