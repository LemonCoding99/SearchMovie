package com.searchmovie.domain.coupon.repository;

import com.searchmovie.domain.coupon.entity.Coupon;
import org.springframework.data.repository.CrudRepository;

public interface CouponRepository extends CrudRepository<Coupon, Long> {
}
