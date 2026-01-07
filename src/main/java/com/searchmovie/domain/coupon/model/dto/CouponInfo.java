package com.searchmovie.domain.coupon.model.dto;

import com.searchmovie.domain.coupon.entity.Coupon;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CouponInfo {

    private final String name;
    private final Integer discountRate;
    private final Integer maxDiscountPrice;
    private final LocalDateTime issueStartAt;
    private final LocalDateTime issueEndAt;
    private final Integer usePeriodDays;
    private final LocalDateTime useStartAt;
    private final LocalDateTime useEndAt;

    public static CouponInfo from(Coupon coupon) {
        return new CouponInfo(
                coupon.getName(),
                coupon.getDiscountRate(),
                coupon.getMaxDiscountPrice(),
                coupon.getIssueStartAt(),
                coupon.getIssueEndAt(),
                coupon.getUsePeriodDays(),
                coupon.getUseStartAt(),
                coupon.getUseEndAt()
        );
    }
}
