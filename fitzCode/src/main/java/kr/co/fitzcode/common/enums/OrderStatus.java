package kr.co.fitzcode.common.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PLACED(1, "주문 완료"),
    SHIPPED(2, "배송 중"),
    DELIVERED(3, "배송 완료"),
    CANCELLED(4, "주문 취소");

    private final int code;
    private final String description;

    OrderStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static OrderStatus fromCode(int code) {
        for (OrderStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid OrderStatus code: " + code);
    }
}