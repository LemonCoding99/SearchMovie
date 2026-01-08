package com.searchmovie.domain.coupon.entity;

import com.searchmovie.common.entity.BaseEntity;
import com.searchmovie.common.enums.ExceptionCode;
import com.searchmovie.common.exception.CustomException;
import com.searchmovie.domain.coupon.model.request.CouponUpdateRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "coupons")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id")
    private Long id;

    // 쿠폰이름
    @Column(nullable = false, length = 100)
    private String name;

    // 할인율 (퍼센트)
    @Column(name = "discount_rate", nullable = false)
    private Integer discountRate;

    // 최대 할인금액
    @Column(name = "max_discount_price")
    private Integer maxDiscountPrice;

    // 발급 시작 날짜
    @Column(name = "issue_start_at", nullable = false)
    private LocalDateTime issueStartAt;

    // 발급 종료 날짜
    @Column(name = "issue_end_at", nullable = false)
    private LocalDateTime issueEndAt;

    // 이 밑에는 3개는 사용기간 정책??
    // 발급한 날짜로부터 몇일
    @Column(name = "use_period_days")
    private Integer usePeriodDays;
    // 아래세트랑 위에꺼랑 둘 중에 하나만 값이 존재해야함
    // 사용가능시작날짜
    @Column(name = "use_start_at")
    private LocalDateTime useStartAt;
    // 이거 위아래 세트
    // 사용가능 종료날짜
    @Column(name = "use_end_at")
    private LocalDateTime useEndAt;

    public Coupon(String name, Integer discountRate, Integer  maxDiscountPrice, LocalDateTime issueStartAt, LocalDateTime issueEndAt, Integer usePeriodDays, LocalDateTime useStartAt, LocalDateTime useEndAt) {
        this.name = name;
        this.discountRate = discountRate;
        this.maxDiscountPrice = maxDiscountPrice;
        this.issueStartAt = issueStartAt;
        this.issueEndAt = issueEndAt;
        this.usePeriodDays = usePeriodDays;
        this.useStartAt = useStartAt;
        this.useEndAt = useEndAt;
    }

    public void update(String name, Integer discountRate, Integer  maxDiscountPrice, LocalDateTime issueStartAt, LocalDateTime issueEndAt, Integer usePeriodDays, LocalDateTime useStartAt, LocalDateTime useEndAt) {
        if (name != null) this.name = name;
        if (discountRate != null) this.discountRate = discountRate;
        if (maxDiscountPrice != null) this.maxDiscountPrice = maxDiscountPrice;
        if (issueStartAt != null) this.issueStartAt = issueStartAt;
        if (issueEndAt != null) this.issueEndAt = issueEndAt;

        if (usePeriodDays != null || useStartAt != null || useEndAt != null) {
            this.usePeriodDays = usePeriodDays;
            this.useStartAt = useStartAt;
            this.useEndAt = useEndAt;
        }
        validate();
    }

    // 쿠폰 정책 검증 (사용가능 기간)
    private void validate() {
        validateIssuePeriod();
        validateUsePolicy();
        validateDiscount();
    }

    private void validateIssuePeriod() {
        if (issueStartAt.isAfter(issueEndAt)) {
            throw new CustomException(ExceptionCode.COUPON_ISSUE_PERIOD_INVALID);
        }
    }

    private void validateUsePolicy() {
        boolean hasPeriodDays = usePeriodDays != null;
        boolean hasUseAt = useStartAt != null || useEndAt != null;

        // 둘 중 하나만 존재해야 함
        if (hasPeriodDays == hasUseAt) {
            throw new CustomException(ExceptionCode.INVALID_COUPON_USE_POLICY);
        }

        if (hasUseAt) {
            if (useStartAt == null || useEndAt == null || useStartAt.isAfter(useEndAt)) {
                throw new CustomException(ExceptionCode.INVALID_COUPON_USE_POLICY);
            }
        }
    }

    private void validateDiscount() {
        if (discountRate < 1 || discountRate > 100) {
            throw new CustomException(ExceptionCode.INVALID_COUPON_DISCOUNT);
        }

        // null = 제한 없음
        if (maxDiscountPrice != null && maxDiscountPrice < 0) {
            throw new CustomException(ExceptionCode.INVALID_COUPON_DISCOUNT);
        }
    }
}