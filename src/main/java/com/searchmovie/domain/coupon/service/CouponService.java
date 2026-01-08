package com.searchmovie.domain.coupon.service;

import com.searchmovie.common.enums.ExceptionCode;
import com.searchmovie.common.exception.CustomException;
import com.searchmovie.common.model.PageResponse;
import com.searchmovie.domain.coupon.entity.Coupon;
import com.searchmovie.domain.coupon.entity.IssuedCouponHistory;
import com.searchmovie.domain.coupon.entity.IssuedCouponStatus;
import com.searchmovie.domain.coupon.model.request.CouponCreateRequest;
import com.searchmovie.domain.coupon.model.request.CouponUpdateRequest;
import com.searchmovie.domain.coupon.model.response.CouponCreateResponse;
import com.searchmovie.domain.coupon.model.response.CouponGetResponse;
import com.searchmovie.domain.coupon.model.response.IssuedCouponHistoryGetDetailResponse;
import com.searchmovie.domain.coupon.model.response.IssuedCouponHistoryIssueResponse;
import com.searchmovie.domain.coupon.model.response.IssuedCouponHistoryUseResponse;
import com.searchmovie.domain.coupon.repository.CouponRepository;
import com.searchmovie.domain.coupon.repository.IssuedCouponHistoryRepository;
import com.searchmovie.domain.couponStock.entity.CouponStock;
import com.searchmovie.domain.couponStock.repository.CouponStockRepository;
import com.searchmovie.domain.couponStock.service.CouponCoreService;
import com.searchmovie.domain.couponStock.service.CouponStockService;
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
    private final CouponStockService couponStockService;

    @Transactional
    public IssuedCouponHistoryIssueResponse issueMyCoupon(Long userId, Long couponId) {

        User foundUser = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        Coupon foundCoupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new CustomException(ExceptionCode.COUPON_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();

        // 발급 기간 확인
        LocalDateTime issueStartAt = foundCoupon.getIssueStartAt();
        LocalDateTime issueEndAt = foundCoupon.getIssueEndAt();
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

        couponStockService.decreaseStock(couponId, 1); // 낙관적 락 적용하면서 바뀐 부분

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

        IssuedCouponHistory history = new IssuedCouponHistory(foundUser, foundCoupon, issuedAt, expiredAt);
        IssuedCouponHistory saved = issuedCouponHistoryRepository.save(history);

        return IssuedCouponHistoryIssueResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public PageResponse<IssuedCouponHistoryGetDetailResponse> getMyCouponsDetail(
            Long userId,
            IssuedCouponStatus status,
            Pageable pageable
    ) {
        // 유저 존재 검증용
        userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        Sort sortObj = Sort.by(Sort.Direction.DESC, "issuedAt");
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortObj);

        Page<IssuedCouponHistory> historyPage = (status == null)
                ? issuedCouponHistoryRepository.findByUser_Id(userId, sortedPageable)
                : issuedCouponHistoryRepository.findByUser_IdAndStatus(userId, status, sortedPageable);

        Page<IssuedCouponHistoryGetDetailResponse> dtoPage =
                historyPage.map(IssuedCouponHistoryGetDetailResponse::from);

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

    @Transactional
    public CouponCreateResponse createCoupon(CouponCreateRequest request) {

        boolean hasPeriodDays = request.getUsePeriodDays() != null;
        boolean hasUseAt = request.getUseStartAt() != null || request.getUseEndAt() != null;

        // 둘 다 없으면 안 됨
        if (!hasPeriodDays && !hasUseAt) {
            throw new CustomException(ExceptionCode.INVALID_COUPON_USE_POLICY);
        }

        // 둘 다 있으면 안 됨
        if (hasPeriodDays && hasUseAt) {
            throw new CustomException(ExceptionCode.INVALID_COUPON_USE_POLICY);
        }

        Coupon coupon = new Coupon(
                request.getName(),
                request.getDiscountRate(),
                request.getMaxDiscountPrice(),
                request.getIssueStartAt(),
                request.getIssueEndAt(),
                request.getUsePeriodDays(),
                request.getUseStartAt(),
                request.getUseEndAt()
        );

        Coupon savedCoupon = couponRepository.save(coupon);
        return CouponCreateResponse.of(savedCoupon);
    }

    @Transactional(readOnly = true)
    public CouponGetResponse getCoupon(Long couponId) {
        Coupon coupon = couponRepository.findByIdAndDeletedAtIsNull(couponId)
                .orElseThrow(() -> new CustomException(ExceptionCode.COUPON_NOT_FOUND));
        return CouponGetResponse.from(coupon);
    }

    @Transactional(readOnly = true)
    public PageResponse<CouponGetResponse> getCoupons(Pageable pageable) {
        Page<Coupon> couponsPage = couponRepository.findAllByDeletedAtIsNull(pageable);
        Page<CouponGetResponse> coupons = couponsPage.map(CouponGetResponse::from);
        return PageResponse.from(coupons);
    }

    @Transactional
    public CouponGetResponse updateCoupon(Long couponId, CouponUpdateRequest request) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new CustomException(ExceptionCode.COUPON_NOT_FOUND));

        coupon.update(
                request.getName(),
                request.getDiscountRate(),
                request.getMaxDiscountPrice(),
                request.getIssueStartAt(),
                request.getIssueEndAt(),
                request.getUsePeriodDays(),
                request.getUseStartAt(),
                request.getUseEndAt()
        );

        return CouponGetResponse.from(coupon);
    }

    @Transactional
    public void deleteCoupon(Long couponId) {
        Coupon coupon = couponRepository.findByIdAndDeletedAtIsNull(couponId)
                .orElseThrow(() -> new CustomException(ExceptionCode.COUPON_NOT_FOUND));
        coupon.softDelete();
    }
}