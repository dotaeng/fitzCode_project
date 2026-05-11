package kr.co.fitzcode.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCouponDTO {
    private int userCouponId;
    private int userId;
    private int couponId;
    private boolean isUsed;
    private LocalDateTime usedAt;
    private LocalDateTime validUntil;
}
