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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.searchmovie.common.enums.ExceptionCode.COUPONSTOCK_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponStockService {

    private final CouponStockRepository couponStockRepository;
    private final CouponRepository couponRepository;

    @Transactional
    public CouponStockCreateResponse createCouponStock(long couponId, CouponStockCreateRequest request) {
        CouponStock stock = couponStockRepository.findByIdForLOCK(couponId)
                .orElseThrow(() -> new CustomException(COUPONSTOCK_NOT_FOUND));

        // 존재하는 쿠폰인지 검사
        if (!couponRepository.existsById(couponId)) {
            throw new CustomException(ExceptionCode.COUPON_NOT_FOUND);
        }

        // 해당 쿠폰의 쿠폰재고가 이미 존재한다면 에러 발생
        if (couponStockRepository.existsByCouponId(couponId)) {
            throw new CustomException(ExceptionCode.ALREADY_EXISTS_COUPONSTOCK);
        }

        CouponStock couponStock = new CouponStock(
                stock.getCouponId(),
                request.getTotalQuantity()
        );

        CouponStock savedCouponStock = couponStockRepository.save(couponStock);
        return CouponStockCreateResponse.from(savedCouponStock);
    }

    @Transactional(readOnly = true)
    public CouponStockGetResponse getCouponStock(Long couponStockId) {

        CouponStock couponStock = couponStockRepository.findById(couponStockId).orElseThrow(
                () -> new CustomException(COUPONSTOCK_NOT_FOUND)
        );

        return CouponStockGetResponse.from(couponStock);
    }

    @Transactional
    public CouponStockUpdateResponse updateCouponStock(Long couponStockId, CouponStockUpdateRequest request) {

        CouponStock couponStock = couponStockRepository.findById(couponStockId).orElseThrow(
                () -> new CustomException(COUPONSTOCK_NOT_FOUND)
        );

        CouponStock updatedCouponStock = couponStock.update(request);
        return CouponStockUpdateResponse.from(updatedCouponStock);
    }
}
