package com.searchmovie.domain.couponStock.model.response;

import com.searchmovie.domain.couponStock.entity.CouponStock;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CouponStockUpdateResponse {
    private final long id;
    private final long couponId;
    private final int totalQuantity;
    private final int presentQuantity;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public CouponStockUpdateResponse(long id, long couponId, int totalQuantity, int presentQuantity, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.couponId = couponId;
        this.totalQuantity = totalQuantity;
        this.presentQuantity = presentQuantity;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static CouponStockUpdateResponse from(CouponStock couponStock) {
        return new CouponStockUpdateResponse(
                couponStock.getId(),
                couponStock.getCouponId(),
                couponStock.getTotalQuantity(),
                couponStock.getPresentQuantity(),
                couponStock.getCreatedAt(),
                couponStock.getUpdatedAt()
        );
    }
}
