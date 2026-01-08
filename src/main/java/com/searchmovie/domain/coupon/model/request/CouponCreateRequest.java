package com.searchmovie.domain.coupon.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CouponCreateRequest {

    @NotBlank
    private String name;

    @NotNull
    private Integer discountRate;

    private Integer maxDiscountPrice;

    @NotNull
    private LocalDateTime issueStartAt;

    @NotNull
    private LocalDateTime issueEndAt;

    private Integer usePeriodDays;

    private LocalDateTime useStartAt;

    private LocalDateTime useEndAt;
}
