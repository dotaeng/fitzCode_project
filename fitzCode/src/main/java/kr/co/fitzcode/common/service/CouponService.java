package kr.co.fitzcode.common.service;

import kr.co.fitzcode.common.dto.UserCouponDTO;
import kr.co.fitzcode.common.mapper.UserCouponMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final UserCouponMapper userCouponMapper;

    @Transactional
    public void issueWelcomeCoupon(int userId) {
        int welcomeCouponId = 7; // 쿠폰 ID 7번 고정

        // 중복 지급 방지: 이미 지급된 쿠폰인지 확인
        UserCouponDTO userCoupon = new UserCouponDTO();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(welcomeCouponId);

        int count = userCouponMapper.countUserCoupon(userCoupon);
        if (count > 0) {
            return;
        }

        // 쿠폰 지급
        userCouponMapper.assignCouponToUser(userCoupon);
    }
}