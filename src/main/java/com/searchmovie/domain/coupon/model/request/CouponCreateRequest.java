package com.searchmovie.domain.coupon.model.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CouponCreateRequest {

    private String name;

    private Integer discountRate;

    private Integer maxDiscountPrice;

    private LocalDateTime issueStartAt;

    private LocalDateTime issueEndAt;

    private Integer usePeriodDays;

    private LocalDateTime useStartAt;

    private LocalDateTime useEndAt;
}
