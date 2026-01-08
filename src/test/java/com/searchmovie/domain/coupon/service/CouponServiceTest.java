package com.searchmovie.domain.coupon.service;

import com.searchmovie.domain.couponStock.entity.CouponStock;
import com.searchmovie.domain.couponStock.repository.CouponStockRepository;
import com.searchmovie.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
class CouponServiceTest {

    @Autowired
    CouponService couponService;

    @Autowired
    CouponStockRepository couponStockRepository;

    @Autowired
    private com.searchmovie.domain.user.repository.UserRepository userRepository;

    @Test
    @DisplayName("비관락_동시_쿠폰발급_성공")
    void issueMyCoupon_pessimistic_lock() throws Exception {
        // given DB 데이터는 이미 SQL로 세팅 완료 (쿠폰 1번, 재고 10개)
        int threadCount = 10;
        Long couponId = 1L;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when 10명이 동시에 발급 요청
        for (int i = 0; i < threadCount; i++) {
            final long userId = (long) i + 1; // 유저 1~10번 사용
            executor.submit(() -> {
                try {
                    couponService.issueMyCoupon(userId, couponId);
                } catch (Exception e) {
                    System.err.println("실패: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        CouponStock stock = couponStockRepository.findByCouponId(couponId).orElseThrow();
        System.out.println("남은 최종 재고 = " + stock.getPresentQuantity());

        assertThat(stock.getPresentQuantity()).isEqualTo(10);
    }
}
