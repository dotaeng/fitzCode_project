package kr.co.fitzcode.common.mapper;

import kr.co.fitzcode.common.dto.UserCouponDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserCouponMapper {
    void assignCouponToUser(UserCouponDTO userCoupon);
    int countUserCoupon(UserCouponDTO userCoupon);
}