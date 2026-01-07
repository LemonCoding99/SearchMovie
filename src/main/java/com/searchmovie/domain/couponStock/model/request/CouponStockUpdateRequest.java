package com.searchmovie.domain.couponStock.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CouponStockUpdateRequest {
    @NotBlank(message = "수정할 값이 입력되지 않았습니다")
    private int totalQuantity;
}
