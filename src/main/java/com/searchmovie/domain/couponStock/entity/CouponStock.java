package com.searchmovie.domain.couponStock.entity;

import com.searchmovie.common.entity.BaseEntity;
import com.searchmovie.domain.couponStock.model.request.CouponStockUpdateRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "coupon_inventories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponStock extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "coupon_id", nullable = false)
    private Long couponId;

    @Column(name = "total_quantity", nullable = false)
    private int totalQuantity;

    @Column(name = "present_quantity")
    private int presentQuantity;

    @Column(name = "version")
    private long version;

    public CouponStock(Long couponId, int totalQuantity, long version) {
        this.couponId = couponId;
        this.totalQuantity = totalQuantity;
        this.presentQuantity = totalQuantity;   // 초기값은 최대수량과 동일하게
        this.version = version;
    }

    public CouponStock update(CouponStockUpdateRequest request) {

        this.totalQuantity = request.getTotalQuantity();
        return this;
    }
    public void decrease(int i) { this.presentQuantity -= i; }
}
