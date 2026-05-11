package kr.co.fitzcode.admin.service;

import kr.co.fitzcode.common.dto.CouponDTO;

import java.sql.Timestamp;
import java.util.List;

public interface CouponService {

    // 새로운 쿠폰 추가
    void addCoupon(CouponDTO couponDTO);

    // 전체 쿠폰 목록을 페이지네이션과 함께 조회
    List<CouponDTO> getAllCoupons(int page, int pageSize);

    // 특정 쿠폰 ID로 쿠폰을 조회
    CouponDTO getCouponById(Integer couponId);

    // 쿠폰 정보를 수정
    void updateCoupon(CouponDTO couponDTO);

    // 특정 쿠폰 ID로 쿠폰을 삭제
    void deleteCoupon(Integer couponId);

    // 전체 쿠폰 개수를 반환
    int countAllCoupons();

    // 특정 사용자에게 쿠폰을 발급
    void issueCouponToUser(Integer couponId, Integer userId, Timestamp validUntil);

    // 모든 사용자에게 쿠폰을 일괄 발급
    void issueCouponToAllUsers(Integer couponId, Timestamp validUntil);

}