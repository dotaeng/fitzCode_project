package kr.co.fitzcode.order.service;

import kr.co.fitzcode.common.dto.CouponDTO;

import java.util.List;

public interface CouponService {
    List<CouponDTO> getUsersValidCoupon(int userId);

    void markCouponAsUsed(int userId, Integer couponId, int orderId);
}
