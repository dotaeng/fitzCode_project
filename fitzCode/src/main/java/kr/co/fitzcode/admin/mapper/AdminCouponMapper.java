package kr.co.fitzcode.admin.mapper;

import kr.co.fitzcode.common.dto.CouponDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface AdminCouponMapper {
    void insertCoupon(CouponDTO couponDTO); // 쿠폰 추가
    List<CouponDTO> getAllCoupons(@Param("offset") int offset, @Param("pageSize") int pageSize); // 전체 쿠폰 조회
    CouponDTO getCouponById(@Param("couponId") Integer couponId); // 특정 쿠폰 조회
    void updateCoupon(CouponDTO couponDTO); // 쿠폰 수정
    void deleteCoupon(@Param("couponId") Integer couponId); // 쿠폰 삭제
    int countAllCoupons(); // 전체 쿠폰 개수 조회
    void issueCouponToUser(@Param("couponId") Integer couponId, @Param("userId") Integer userId, @Param("validUntil") Timestamp validUntil); // 사용자에게 쿠폰 발급
    List<Integer> getAllUserIds(); // 전체 사용자 ID 조회
}