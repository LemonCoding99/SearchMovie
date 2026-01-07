package com.searchmovie.domain.couponStock;

import com.searchmovie.domain.coupon.entity.Coupon;
import com.searchmovie.domain.coupon.repository.CouponRepository;
import com.searchmovie.domain.couponStock.service.CouponCoreService;
import com.searchmovie.domain.couponStock.entity.CouponStock;
import com.searchmovie.domain.couponStock.repository.CouponStockRepository;
import com.searchmovie.domain.couponStock.service.CouponStockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
public class CouponStockServiceTest {

    @Autowired
    private CouponStockRepository couponStockRepository;

    @Autowired
    private CouponStockService couponStockService;

    @Autowired
    private CouponRepository couponRepository;


    @Test
    void 낙관적락_쿠폰_정상발급_테스트() throws InterruptedException {

        // Test할 쿠폰 수량 생성
        Coupon coupon = couponRepository.save(new Coupon("TestCoupon", 20, 10000, LocalDateTime.parse("2026-01-01T00:00:00"), LocalDateTime.parse("2026-02-01T23:59:59"), 30, null, null));

        // Test할 couponStock 생성
        CouponStock couponStock = couponStockRepository.save(new CouponStock(coupon.getId(), 1000, 0L));

        // 동시에 몇 명이 시도할건지?
        ExecutorService executor = Executors.newFixedThreadPool(10);

        // 어떤 작업을 수행할건지?
        Runnable task = () -> {
            couponStockService.decreaseStock(coupon.getId(), 1);
        };

        // 반복수행
        for (int i = 0; i < 10; i++) {
            executor.submit(task);  // task를 10번 반복
        }

        // 그 환경을 이제 동시에 시작하도록 준비 땅
        executor.shutdown();

        // 그 환경이 실행될 때까지 잠깐 기다리기
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // 다 기다렸으면 최종적으로 어떤 결과가 나왔는지 출력
        CouponStock result = couponStockRepository.findById(couponStock.getId()).orElseThrow();
        System.out.println("남은 쿠폰 수량: " + result.getPresentQuantity());

        assertEquals(990, result.getPresentQuantity());
    }
}
