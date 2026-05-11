package kr.co.fitzcode.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserTier {
    BRONZE(1, "브론즈", 0),    // 0% 할인
    SILVER(2, "실버", 5),      // 5% 할인
    GOLD(3, "골드", 10),       // 10% 할인
    PLATINUM(4, "플래티넘", 15); // 15% 할인

    private final int code;         // 등급 코드 (1~4)
    private final String description; // 한글 등급명
    private final int discountRate; // 할인율 (%)

    // 등급 코드(1~4)로 UserTier ENUM 반환
    public static UserTier fromCode(int code) {
        for (UserTier tier : values()) {
            if (tier.getCode() == code) {
                return tier;
            }
        }
        throw new IllegalArgumentException("유효하지 않은 등급 코드: " + code);
    }

    // 등급 코드(1~4)로 등급명("브론즈", "실버" 등) 반환
    public static String getTierName(int code) {
        try {
            return fromCode(code).getDescription();
        } catch (IllegalArgumentException e) {
            return "알 수 없는 코드";
        }
    }

    // 등급 코드(1~4)로 할인율(%) 반환
    public static int getDiscountRate(int code) {
        try {
            return fromCode(code).getDiscountRate();
        } catch (IllegalArgumentException e) {
            return 0;
        }
    }
}