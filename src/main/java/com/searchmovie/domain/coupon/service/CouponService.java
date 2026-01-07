package com.searchmovie.domain.coupon.service;

import com.searchmovie.common.enums.ExceptionCode;
import com.searchmovie.common.exception.CustomException;
import com.searchmovie.common.model.PageResponse;
import com.searchmovie.domain.coupon.entity.Coupon;
import com.searchmovie.domain.coupon.entity.IssuedCouponHistory;
import com.searchmovie.domain.coupon.entity.IssuedCouponStatus;
import com.searchmovie.domain.coupon.model.response.IssuedCouponHistoryGetDetailResponse;
import com.searchmovie.domain.coupon.model.response.IssuedCouponHistoryIssueResponse;
import com.searchmovie.domain.coupon.model.response.IssuedCouponHistoryUseResponse;
import com.searchmovie.domain.coupon.repository.CouponRepository;
import com.searchmovie.domain.coupon.repository.IssuedCouponHistoryRepository;
import com.searchmovie.domain.user.entity.User;
import com.searchmovie.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final IssuedCouponHistoryRepository issuedCouponHistoryRepository;
    private final CouponStockRepository couponStockRepository;
    private final UserRepository userRepository;

    @Transactional
    public IssuedCouponHistoryIssueResponse issueMyCoupon(Long userId, Long couponId) {

        User founduser = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        Coupon foundCoupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new CustomException(ExceptionCode.COUPON_NOT_FOUND));

        LocalDateTime issueStartAt = foundCoupon.getIssueStartAt();
        LocalDateTime issueEndAt = foundCoupon.getIssueEndAt();
        LocalDateTime now = LocalDateTime.now();

        // 발급 기간 확인
        if (now.isBefore(issueStartAt) || now.isAfter(issueEndAt)) {
           throw new CustomException(ExceptionCode.COUPON_ISSUE_PERIOD_INVALID);
        }

        // 중복 발급 확인
        if (issuedCouponHistoryRepository.existsByUser_IdAndCoupon_Id(userId, couponId)) {
            throw new CustomException(ExceptionCode.COUPON_ALREADY_ISSUED);
        }

        // 재고 조회 + 소진 체크 + 차감
        CouponStock stock = couponStockRepository.findByCouponId(couponId)
               .orElseThrow(() -> new CustomException(ExceptionCode.COUPON_STOCK_NOT_FOUND));

        if (stock.getPresentQuantity() <= 0) {
            throw new CustomException(ExceptionCode.COUPON_OUT_OF_STOCK);
        }
        stock.decrease(1);

        // 만료일 계산
        LocalDateTime issuedAt = now;
        LocalDateTime expiredAt;

        Integer periodDays = foundCoupon.getUsePeriodDays();
        if (periodDays != null) {
            expiredAt = issuedAt.plusDays(periodDays);
        } else {
            // 기간 고정 쿠폰
            if (foundCoupon.getUseEndAt() == null) {
                throw new CustomException(ExceptionCode.COUPON_POLICY_INVALID);
            }
            expiredAt = foundCoupon.getUseEndAt();
        }
        IssuedCouponHistory history = new IssuedCouponHistory(founduser, foundCoupon, now, expiredAt);

        IssuedCouponHistory saved = issuedCouponHistoryRepository.save(history);

        return IssuedCouponHistoryIssueResponse.from(saved);
    }


    @Transactional(readOnly = true)
    public PageResponse<IssuedCouponHistoryGetDetailResponse> getMyCouponsDetail(
            Long userId,
            IssuedCouponStatus status,
            Pageable pageable) {

        // 유저 존재 검증용
        userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        Sort sortObj = Sort.by(Sort.Direction.DESC, "issuedAt");
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortObj);

        Page<IssuedCouponHistory> historyPage;
        if (status == null) {
            historyPage = issuedCouponHistoryRepository.findByUser_Id(userId, sortedPageable);

        } else {
            historyPage = issuedCouponHistoryRepository.findByUser_IdAndStatus(userId, status, sortedPageable);
        }

        Page<IssuedCouponHistoryGetDetailResponse> dtoPage = historyPage.map(IssuedCouponHistoryGetDetailResponse::from);

        return PageResponse.from(dtoPage);
    }

    @Transactional
    public IssuedCouponHistoryUseResponse useMyCoupon(Long userId, Long issuedCouponId) {

        // 유저 존재 검증용
        userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        // 내 쿠폰인지 조회
        IssuedCouponHistory foundIssuedCoupon = issuedCouponHistoryRepository
                .findByIdAndUser_Id(issuedCouponId, userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ISSUED_COUPON_NOT_FOUND));

        // 이미 사용한 쿠폰인지 확인
        if (foundIssuedCoupon.getStatus() == IssuedCouponStatus.USED) {
            throw new CustomException(ExceptionCode.COUPON_ALREADY_USED);
        }

        LocalDateTime now = LocalDateTime.now();

        // 만료된 쿠폰
        if (foundIssuedCoupon.getExpiredAt().isBefore(now)) {
            throw new CustomException(ExceptionCode.COUPON_EXPIRED);
        }

        // 사용 시작 전 쿠폰
        LocalDateTime useStartAt = foundIssuedCoupon.getCoupon().getUseStartAt();
        if (useStartAt != null && useStartAt.isAfter(now)) {
            throw new CustomException(ExceptionCode.COUPON_NOT_STARTED);
        }

        // 사용 처리
        foundIssuedCoupon.use(now);

        return IssuedCouponHistoryUseResponse.from(foundIssuedCoupon);
    }
}
