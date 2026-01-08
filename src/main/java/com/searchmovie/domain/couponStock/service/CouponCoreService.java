package com.searchmovie.domain.couponStock.service;


import com.searchmovie.common.enums.ExceptionCode;
import com.searchmovie.common.exception.CustomException;
import com.searchmovie.domain.couponStock.entity.CouponStock;
import com.searchmovie.domain.couponStock.repository.CouponStockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponCoreService {

    private final CouponStockRepository couponStockRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void withdraw(Long couponId, int quantity) {
        CouponStock stock = couponStockRepository.findByCouponIdAndDeletedAtIsNull(couponId)
                .orElseThrow(() -> new CustomException(ExceptionCode.COUPON_STOCK_NOT_FOUND));

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        stock.decrease(quantity);
    }
}
