package kr.co.fitzcode.order.mapper;

import kr.co.fitzcode.common.dto.CouponDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CouponMapper {
    List<CouponDTO> getUsersValidCoupon(int userId);

    void markCouponAsUsed(int userId, Integer couponId, int orderId);

    void setIsUsedToTrue(int userId, Integer couponId);
}
