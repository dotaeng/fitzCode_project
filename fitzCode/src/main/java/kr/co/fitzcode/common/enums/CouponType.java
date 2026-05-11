
package kr.co.fitzcode.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CouponType {
    AMOUNT(1, "금액 할인"),
    PERCENTAGE(2, "퍼센트 할인"),
    FREESHIP(3, "무료 배송"),
    FIRSTORDER(4, "첫 주문 할인");

    private final int code;
    private final String description;

    public static CouponType fromCode(int code) {
        for (CouponType type : values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("유효하지 않은 쿠폰 타입 코드: " + code);
    }
}
