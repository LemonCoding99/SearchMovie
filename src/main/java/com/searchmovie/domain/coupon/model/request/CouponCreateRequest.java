package com.searchmovie.domain.coupon.model.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CouponCreateRequest {

    @NotBlank
    @Size(max = 100, message = "쿠폰명은 100자 이하여야 합니다.")
    private String name;

    @NotNull
    @Min(value = 1, message = "할인율은 1 이상이어야 합니다.")
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