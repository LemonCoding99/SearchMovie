package com.searchmovie.domain.couponStock.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CouponStockCreateRequest {

    @NotBlank(message = "totalQuantity는 필수입니다.")
    int totalQuantity;
    @NotBlank(message = "version은 필수입니다.")
    long version;
}
