package com.searchmovie.domain.coupon.model.response;

import com.searchmovie.domain.coupon.entity.Coupon;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CouponCreateResponse {

    private Long id;

    private String name;

    private Integer discountRate;

    private Integer maxDiscountPrice;

    private LocalDateTime issueStartAt;

    private LocalDateTime issueEndAt;

    private Integer usePeriodDays;

    private LocalDateTime useStartAt;

    private LocalDateTime useEndAt;

    private LocalDateTime createdAt;

    public static CouponCreateResponse of(Coupon coupon) {
        return new CouponCreateResponse(
                coupon.getId(),
                coupon.getName(),
                coupon.getDiscountRate(),
                coupon.getMaxDiscountPrice(),
                coupon.getIssueStartAt(),
                coupon.getIssueEndAt(),
                coupon.getUsePeriodDays(),
                coupon.getUseStartAt(),
                coupon.getUseEndAt(),
                coupon.getCreatedAt()
        );
    }
}
