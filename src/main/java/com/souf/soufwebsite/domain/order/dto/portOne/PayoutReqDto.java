package com.souf.soufwebsite.domain.order.dto.portOne;

import java.util.List;

public record PayoutReqDto(
        String partnerId,
        String contractId,
        String paymentId,
        CreatePlatformOrderTransferBodyOrderDetail orderDetail,
        List<CreatePlatformOrderTransferBodyDiscount> discounts,
        List<CreatePlatformOrderTransferBodyAdditionalFee> additionalFees
) {
    public record CreatePlatformOrderTransferBodyOrderDetail(
            Integer orderAmount
    ){}

    public record CreatePlatformOrderTransferBodyDiscount(
            String sharePolicyId,
            Long amount,
            Long taxFreeAmount
    ){}

    public record CreatePlatformOrderTransferBodyAdditionalFee(
            String policyId
    ){}
}
