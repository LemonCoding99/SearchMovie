package com.searchmovie.domain.coupon.entity;

import com.searchmovie.common.entity.BaseEntity;
import com.searchmovie.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "issued_coupon_histories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IssuedCouponHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "issued_coupon_id")
    private Long id;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private IssuedCouponStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;

    public IssuedCouponHistory(User user, Coupon coupon, LocalDateTime issuedAt, LocalDateTime expiredAt) {
        this.user = user;
        this.coupon = coupon;
        this.issuedAt = issuedAt;
        this.expiredAt = expiredAt;
        this.status = IssuedCouponStatus.NOT_USED;
        this.usedAt = null;
    }

    public void use(LocalDateTime now) {
        this.status = IssuedCouponStatus.USED;
        this.usedAt = now;
    }
}
