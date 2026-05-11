package kr.co.fitzcode.common.enums;

import lombok.Getter;

@Getter
public enum TransactionType {
    PURCHASE(1, "구매"),
    REFUND(2, "환불"),
    ORDER_CANCEL(3, "주문 취소");

    private final int code;
    private final String description;

    TransactionType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static TransactionType fromCode(int code) {
        for (TransactionType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("올바른 코드가 아님: " + code);
    }
}