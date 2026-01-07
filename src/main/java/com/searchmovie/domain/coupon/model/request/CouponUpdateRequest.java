package com.searchmovie.domain.coupon.model.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CouponUpdateRequest {

    private String name;
    private Integer discountRate;
    private Integer maxDiscountPrice;
    private LocalDateTime issueStartAt;
    private LocalDateTime issueEndAt;
    private Integer usePeriodDays;
    private LocalDateTime useStartAt;
    private LocalDateTime useEndAt;
}
