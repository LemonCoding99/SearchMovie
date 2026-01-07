package com.searchmovie.domain.couponStock.entity;

import com.searchmovie.common.entity.BaseEntity;
import com.searchmovie.common.enums.ExceptionCode;
import com.searchmovie.common.exception.CustomException;
import com.searchmovie.domain.couponStock.model.request.CouponStockUpdateRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

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
    @Check(constraints = "present_quantity >= 0")
    private int presentQuantity;

    public CouponStock(Long couponId, int totalQuantity) {
        this.couponId = couponId;
        this.totalQuantity = totalQuantity;
        this.presentQuantity = totalQuantity;   // 초기값은 최대수량과 동일하게
    }

    public CouponStock update(CouponStockUpdateRequest request) {

        this.totalQuantity = request.getTotalQuantity();
        return this;
    }

    public void decrease(int amount) {
        if((this.presentQuantity - amount) < 0) {
            throw new CustomException(ExceptionCode.COUPON_OUT_OF_STOCK);
        }
        this.presentQuantity -= amount;
    }
}
