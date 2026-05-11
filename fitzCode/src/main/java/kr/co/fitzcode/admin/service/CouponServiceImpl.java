package kr.co.fitzcode.admin.service;

import kr.co.fitzcode.admin.mapper.AdminCouponMapper;
import kr.co.fitzcode.common.dto.CouponDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final AdminCouponMapper couponMapper;

    @Override
    @Transactional
    public void addCoupon(CouponDTO couponDTO) {
        couponMapper.insertCoupon(couponDTO); // 쿠폰 추가
    }

    @Override
    public List<CouponDTO> getAllCoupons(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return couponMapper.getAllCoupons(offset, pageSize); // 전체 쿠폰 목록 조회
    }

    @Override
    public CouponDTO getCouponById(Integer couponId) {
        return couponMapper.getCouponById(couponId); // 특정 쿠폰 조회
    }

    @Override
    @Transactional
    public void updateCoupon(CouponDTO couponDTO) {
        couponMapper.updateCoupon(couponDTO); // 쿠폰 수정
    }

    @Override
    @Transactional
    public void deleteCoupon(Integer couponId) {
        couponMapper.deleteCoupon(couponId); // 쿠폰 삭제
    }

    @Override
    public int countAllCoupons() {
        return couponMapper.countAllCoupons(); // 전체 쿠폰 개수 조회
    }

    @Override
    @Transactional
    public void issueCouponToUser(Integer couponId, Integer userId, Timestamp validUntil) {
        couponMapper.issueCouponToUser(couponId, userId, validUntil); // 특정 사용자에게 쿠폰 발급
    }

    @Override
    @Transactional
    public void issueCouponToAllUsers(Integer couponId, Timestamp validUntil) {
        List<Integer> userIds = couponMapper.getAllUserIds(); // 모든 사용자 ID 조회
        for (Integer userId : userIds) {
            couponMapper.issueCouponToUser(couponId, userId, validUntil); // 쿠폰 일괄 발급
        }
    }
}