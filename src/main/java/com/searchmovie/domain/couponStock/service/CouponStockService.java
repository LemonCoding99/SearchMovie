package com.searchmovie.domain.couponStock.service;

import com.searchmovie.common.enums.ExceptionCode;
import com.searchmovie.common.exception.CustomException;
import com.searchmovie.domain.coupon.repository.CouponRepository;
import com.searchmovie.domain.couponStock.entity.CouponStock;
import com.searchmovie.domain.couponStock.model.request.CreateCouponStockRequest;
import com.searchmovie.domain.couponStock.model.request.UpdateCouponStockRequest;
import com.searchmovie.domain.couponStock.model.response.CreateCouponStockResponse;
import com.searchmovie.domain.couponStock.model.response.GetCouponStockResponse;
import com.searchmovie.domain.couponStock.model.response.UpdateCouponStockResponse;
import com.searchmovie.domain.couponStock.repository.CouponStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponStockService {

    private final CouponStockRepository couponStockRepository;
    private final CouponRepository couponRepository;

    @Transactional
    public CreateCouponStockResponse createCouponStock(long couponId, CreateCouponStockRequest request) {

        // 존재하는 쿠폰인지 검사
        if (couponRepository.existsById(couponId)) {
            throw new CustomException(ExceptionCode.COUPON_NOT_FOUND);
        }

        CouponStock couponStock = new CouponStock(
                couponId,
                request.getTotalQuantity(),
                request.getVersion()
        );

        CouponStock savedCouponStock = couponStockRepository.save(couponStock);
        return CreateCouponStockResponse.from(savedCouponStock);
    }

    @Transactional(readOnly = true)
    public GetCouponStockResponse getCouponStock(Long couponStockId) {

        CouponStock couponStock = couponStockRepository.findById(couponStockId).orElseThrow(
                () -> new CustomException(ExceptionCode.COUPONSTOCK_NOT_FOUND)
        );

        return GetCouponStockResponse.from(couponStock);
    }

    @Transactional
    public UpdateCouponStockResponse updateCouponStock(Long couponStockId, UpdateCouponStockRequest request) {

        CouponStock couponStock = couponStockRepository.findById(couponStockId).orElseThrow(
                () -> new CustomException(ExceptionCode.COUPONSTOCK_NOT_FOUND)
        );

        CouponStock updatedCouponStock = couponStock.update(request);
        return UpdateCouponStockResponse.from(updatedCouponStock);
    }
}
