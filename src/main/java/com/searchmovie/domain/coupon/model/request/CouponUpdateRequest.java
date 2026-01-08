package com.searchmovie.domain.coupon.model.request;

import jakarta.validation.constraints.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CouponUpdateRequest {

    @Size(max = 100, message = "쿠폰명은 100자 이하여야 합니다.")
    private String name;

    @Min(value = 1, message = "할인율은 1 이상이어야 합니다.")
    private Integer discountRate;
    private Integer maxDiscountPrice;
    private LocalDateTime issueStartAt;
    private LocalDateTime issueEndAt;
    private Integer usePeriodDays;
    private LocalDateTime useStartAt;
    private LocalDateTime useEndAt;
}