package com.searchmovie.domain.couponStock.model.response;

import com.searchmovie.domain.couponStock.entity.CouponStock;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GetCouponStockResponse {
    private final long id;
    private final long couponId;
    private final int totalQuantity;
    private final int presentQuantity;
    private final LocalDateTime updatedAt;

    public GetCouponStockResponse(long id, long couponId, int totalQuantity, int presentQuantity, LocalDateTime updatedAt) {
        this.id = id;
        this.couponId = couponId;
        this.totalQuantity = totalQuantity;
        this.presentQuantity = presentQuantity;
        this.updatedAt = updatedAt;
    }

    public static GetCouponStockResponse from(CouponStock couponStock) {
        return new GetCouponStockResponse(
                couponStock.getId(),
                couponStock.getCouponId(),
                couponStock.getTotalQuantity(),
                couponStock.getPresentQuantity(),
                couponStock.getUpdatedAt()
        );
    }
}
