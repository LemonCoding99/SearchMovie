package com.searchmovie.domain.coupon.model.response;

import com.searchmovie.domain.coupon.entity.Coupon;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CouponGetResponse {

    private final Long id;
    private final String name;
    private final Integer discountRate;
    private final Integer maxDiscountPrice;
    private final LocalDateTime issueStartAt;
    private final LocalDateTime issueEndAt;
    private final Integer usePeriodDays;
    private final LocalDateTime useStartAt;
    private final LocalDateTime useEndAt;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public CouponGetResponse(Long id, String name, Integer discountRate, Integer maxDiscountPrice, LocalDateTime issueStartAt, LocalDateTime issueEndAt, Integer usePeriodDays, LocalDateTime useStartAt, LocalDateTime useEndAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.discountRate = discountRate;
        this.maxDiscountPrice = maxDiscountPrice;
        this.issueStartAt = issueStartAt;
        this.issueEndAt = issueEndAt;
        this.usePeriodDays = usePeriodDays;
        this.useStartAt = useStartAt;
        this.useEndAt = useEndAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static CouponGetResponse from(Coupon coupon) {
        return new CouponGetResponse(
                coupon.getId(),
                coupon.getName(),
                coupon.getDiscountRate(),
                coupon.getMaxDiscountPrice(),
                coupon.getIssueStartAt(),
                coupon.getIssueEndAt(),
                coupon.getUsePeriodDays(),
                coupon.getUseStartAt(),
                coupon.getUseEndAt(),
                coupon.getCreatedAt(),
                coupon.getUpdatedAt()
        );
    }
}