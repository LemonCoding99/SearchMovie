package com.searchmovie.domain.couponStock.service;

import com.searchmovie.common.enums.ExceptionCode;
import com.searchmovie.common.exception.CustomException;
import com.searchmovie.domain.coupon.repository.CouponRepository;
import com.searchmovie.domain.couponStock.entity.CouponStock;
import com.searchmovie.domain.couponStock.model.request.CouponStockCreateRequest;
import com.searchmovie.domain.couponStock.model.request.CouponStockUpdateRequest;
import com.searchmovie.domain.couponStock.model.response.CouponStockCreateResponse;
import com.searchmovie.domain.couponStock.model.response.CouponStockGetResponse;
import com.searchmovie.domain.couponStock.model.response.CouponStockUpdateResponse;
import com.searchmovie.domain.couponStock.repository.CouponStockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponStockService {

    private final CouponStockRepository couponStockRepository;
    private final CouponRepository couponRepository;
    private final CouponCoreService couponCoreService;

    @Transactional
    public CouponStockCreateResponse createCouponStock(long couponId, CouponStockCreateRequest request) {

        // 존재하는 쿠폰인지 검사
        if (!couponRepository.existsById(couponId)) {
            throw new CustomException(ExceptionCode.COUPON_NOT_FOUND);
        }

        // 해당 쿠폰의 쿠폰재고가 이미 존재한다면 에러 발생
        if (couponStockRepository.existsByCouponId(couponId)) {
            throw new CustomException(ExceptionCode.ALREADY_EXISTS_COUPONSTOCK);
        }


        CouponStock couponStock = new CouponStock(
                couponId,
                request.getTotalQuantity(),
                request.getVersion()
        );

        CouponStock savedCouponStock = couponStockRepository.save(couponStock);
        return CouponStockCreateResponse.from(savedCouponStock);
    }

    @Transactional(readOnly = true)
    public CouponStockGetResponse getCouponStock(Long couponStockId) {

        CouponStock couponStock = couponStockRepository.findById(couponStockId).orElseThrow(
                () -> new CustomException(ExceptionCode.COUPONSTOCK_NOT_FOUND)
        );

        return CouponStockGetResponse.from(couponStock);
    }

    @Transactional
    public CouponStockUpdateResponse updateCouponStock(Long couponStockId, CouponStockUpdateRequest request) {

        CouponStock couponStock = couponStockRepository.findById(couponStockId).orElseThrow(
                () -> new CustomException(ExceptionCode.COUPONSTOCK_NOT_FOUND)
        );

        CouponStock updatedCouponStock = couponStock.update(request);
        return CouponStockUpdateResponse.from(updatedCouponStock);
    }


    // 쿠폰 발급 로직
    public void decreaseStock(Long couponId, int quantity) {

        int retry = 0;
        while (retry < 10) {  // 10번만 재시도하고 안되면 예외 처리
            try {
                couponCoreService.withdraw(couponId, quantity);
                return;

            } catch (ObjectOptimisticLockingFailureException e) {  // 낙관적 락 예외 잡아서 처리

                retry++;
                log.info("충돌 발생! 락 획득 재시도 로직 수행 재시도 횟수: {}", retry);

                try {
                    Thread.sleep(100);  // 100ms 대기 후 재시도
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        throw new IllegalArgumentException("쿠폰 발급에 실패했습니다.");
    }

}
