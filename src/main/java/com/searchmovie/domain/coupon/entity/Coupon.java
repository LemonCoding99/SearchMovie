package com.searchmovie.domain.coupon.entity;

import com.searchmovie.common.entity.BaseEntity;
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

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "discount_rate", nullable = false)
    private Integer discountRate;

    @Column(name = "issue_start_at", nullable = false)
    private LocalDateTime issueStartAt;

    @Column(name = "issue_end_at", nullable = false)
    private LocalDateTime issueEndAt;

    @Column(name = "use_period_days")
    private Integer usePeriodDays;

    // 사용 가능 시작일 (nullable)
    @Column(name = "use_start_at")
    private LocalDateTime useStartAt;

    // 사용 가능 종료일 (nullable)
    @Column(name = "use_end_at")
    private LocalDateTime useEndAt;

    public Coupon(String name, Integer discountRate, LocalDateTime issueStartAt, LocalDateTime issueEndAt, Integer usePeriodDays, LocalDateTime useStartAt, LocalDateTime useEndAt) {
        this.name = name;
        this.discountRate = discountRate;
        this.issueStartAt = issueStartAt;
        this.issueEndAt = issueEndAt;
        this.usePeriodDays = usePeriodDays;
        this.useStartAt = useStartAt;
        this.useEndAt = useEndAt;
    }
}
