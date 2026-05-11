package kr.co.fitzcode.order.service;

import kr.co.fitzcode.common.dto.CouponDTO;
import kr.co.fitzcode.order.mapper.CouponMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponServiceImple implements CouponService {

    private final CouponMapper couponMapper;

    @Override
    public List<CouponDTO> getUsersValidCoupon(int userId) {
        return couponMapper.getUsersValidCoupon(userId);
    }

    @Override
    public void markCouponAsUsed(int userId, Integer couponId, int orderId) {
        if(couponId != null) {
            couponMapper.markCouponAsUsed(userId, couponId, orderId);
            couponMapper.setIsUsedToTrue(userId, couponId);
        }
    }


}
